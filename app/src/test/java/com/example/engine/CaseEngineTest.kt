package com.example.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

class CaseEngineTest {

    private val def: CaseDefinition by lazy {
        // Gradle runs unit tests with the module (app/) as working directory
        val f = listOf("src/main/assets/cases/ersim_stemi_001.json", "app/src/main/assets/cases/ersim_stemi_001.json")
            .map { File(it) }.firstOrNull { it.exists() } ?: error("Case file not found from ${File(".").absolutePath}")
        CaseParser.parse(f.readText())
    }

    private fun run(vararg events: SimEvent): CaseSession =
        CaseEngine.reduceAll(def, CaseEngine.start(def), events.toList())

    private fun CaseSession.kinds(kind: LogKind) = log.filter { it.kind == kind }

    // ------------------------------------------------------------ format
    @Test fun miniJson_parsesNestedValues() {
        val v = MiniJson.parse("""{"a":[1,2.5,-3e1],"b":{"c":"x\"y\u00e9"},"d":true,"e":null}""") as Map<*, *>
        assertEquals(listOf(1.0, 2.5, -30.0), v["a"])
        assertEquals("x\"yé", (v["b"] as Map<*, *>)["c"])
        assertEquals(true, v["d"])
        assertTrue(v.containsKey("e"))
    }

    @Test fun miniJson_rejectsTrailingCommaAndDuplicates() {
        expectFailure { MiniJson.parse("""{"a":1,}""") }
        expectFailure { MiniJson.parse("""{"a":1,"a":2}""") }
    }

    @Test fun stemiCase_loadsAndValidates() {
        assertEquals("ERSIM-STEMI-001", def.meta.caseId)
        assertEquals("DRAFT", def.meta.reviewStatus)
        assertEquals("presenting", def.initialStateId)
        assertTrue(def.states.values.any { it.terminal })
        assertEquals(CaseParser.EXAM_REGIONS, def.baselineExam.keys)
    }

    @Test fun validator_catchesBrokenReferences() {
        val broken = File(caseFile()).readText().replace("\"to\": \"post_lysis\"", "\"to\": \"nowhere\"")
        expectFailure { CaseParser.parse(broken) }
    }

    @Test fun approvedCase_withoutReviewer_isRejected() {
        val approved = File(caseFile()).readText().replace("\"reviewStatus\": \"DRAFT\"", "\"reviewStatus\": \"APPROVED\"")
        expectFailure { CaseParser.parse(approved) }
    }

    @Test fun sessionFromAnotherCaseVersion_isRejected() {
        val old = CaseEngine.start(def).copy(caseVersion = 999)
        expectFailure { CaseEngine.reduce(def, old, SimEvent.Advance(1)) }
    }

    @Test fun condition_parserRejectsUnknownSyntax() {
        expectFailure { Condition.parse("vitals.map<65") }
        assertEquals(Condition.VitalCompare("sbp", "<", 90), Condition.parse("vitals.sbp<90"))
    }

    // ------------------------------------------------------------ ideal pathway
    @Test fun idealPathway_reperfusesAndTransfers_fullScore() {
        val s = run(
            SimEvent.Perform("monitor_iv"),
            SimEvent.Order("ecg_12lead"),
            SimEvent.Ask("hx_onset"),
            SimEvent.Ask("hx_lysis_ci"),
            SimEvent.Advance(60),                       // ECG result
            SimEvent.Order("ecg_right_sided"),
            SimEvent.Perform("aspirin"),
            SimEvent.Perform("clopidogrel"),
            SimEvent.Perform("enoxaparin"),
            SimEvent.Perform("arrange_transfer_pci"),
            SimEvent.Perform("explain_consent"),
            SimEvent.Advance(60),
            SimEvent.Perform("tenecteplase"),
            SimEvent.Advance(600),                      // reperfusion
            SimEvent.Advance(60)                        // auto handover (transfer already arranged)
        )
        assertTrue(s.ended)
        assertEquals("handover", s.stateId)
        assertTrue(s.harms.isEmpty())
        assertTrue("reperfused" in s.visitedStates)
        assertTrue(s.results["ecg_12lead"]!!.contains("inferior STEMI"))
        val d = CaseEngine.debrief(def, s)
        assertEquals(100, d.scorePercent)
        assertTrue(d.criticalActions.all { it.met })
        // conditional actions not scored because those complications never happened
        assertFalse(d.criticalActions.any { it.def.id == "ca_pacing" || it.def.id == "ca_defib" })
    }

    // ------------------------------------------------------------ safety
    @Test fun nitrateInRvInfarct_causesHypotensionAndHarm_fluidsRescue() {
        var s = run(SimEvent.Order("ecg_12lead"), SimEvent.Advance(60), SimEvent.Perform("gtn_sl"))
        assertEquals("nitrate_hypotension", s.stateId)
        assertEquals(1, s.harms.size)
        assertEquals(74, CaseEngine.currentState(def, s).vitals.sbp)
        s = CaseEngine.reduce(def, s, SimEvent.Perform("ns_bolus"))
        assertEquals("presenting", s.stateId)
        val d = CaseEngine.debrief(def, s)
        assertFalse(d.criticalActions.first { it.def.id == "ca_no_nitrate" }.met)
    }

    @Test fun lysisAfterEcgOrderedButBeforeResult_isRejected() {
        val s = run(SimEvent.Order("ecg_12lead"), SimEvent.Advance(30), SimEvent.Perform("tenecteplase"))
        assertFalse("tenecteplase" in s.performed)
        assertEquals("presenting", s.stateId)
        assertEquals(1, s.kinds(LogKind.REJECTED).size)
        // once the result is back it is allowed
        val after = CaseEngine.reduceAll(def, s, listOf(SimEvent.Advance(30), SimEvent.Perform("tenecteplase")))
        assertEquals("post_lysis", after.stateId)
    }

    @Test fun lysisWithoutContraindicationScreening_isProcessHarm_andCostsPoints() {
        val s = run(SimEvent.Order("ecg_12lead"), SimEvent.Advance(60), SimEvent.Perform("tenecteplase"))
        assertEquals("post_lysis", s.stateId)
        assertEquals(1, s.harms.size)
        val d = CaseEngine.debrief(def, s)
        assertEquals(CaseEngine.HARM_PENALTY, d.harmPenalty)
        assertEquals(d.actionsPercent - CaseEngine.HARM_PENALTY, d.scorePercent)
    }

    @Test fun death_capsScoreBelowOneStar() {
        // Everything ticked except treatment of VF: patient dies
        val s = run(
            SimEvent.Order("ecg_12lead"), SimEvent.Ask("hx_lysis_ci"), SimEvent.Order("ecg_right_sided"),
            SimEvent.Perform("aspirin"), SimEvent.Perform("clopidogrel"), SimEvent.Perform("enoxaparin"),
            SimEvent.Perform("arrange_transfer_pci"), SimEvent.Advance(1500), SimEvent.Advance(240)
        )
        assertEquals("death", s.stateId)
        val d = CaseEngine.debrief(def, s)
        assertTrue(d.badOutcome)
        assertTrue(d.scorePercent <= CaseEngine.BAD_OUTCOME_CAP)
    }

    @Test fun shockWithoutCpr_refibrillatesAtRhythmCheck() {
        var s = run(SimEvent.Advance(1500), SimEvent.Perform("defibrillate"))
        assertEquals("post_shock_cpr", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Advance(120))
        assertEquals("vf_arrest", s.stateId)
    }

    @Test fun lysisWithoutEcg_isRejected() {
        val s = run(SimEvent.Perform("tenecteplase"))
        assertEquals("presenting", s.stateId)
        assertFalse("tenecteplase" in s.performed)
        assertEquals(1, s.kinds(LogKind.REJECTED).size)
    }

    @Test fun oxygenAtNormalSats_isNoteNotHarm() {
        val s = run(SimEvent.Perform("oxygen"))
        assertTrue(s.harms.isEmpty())
        assertEquals(1, s.kinds(LogKind.NOTE).size)
    }

    @Test fun shockingPatientWithPulse_isHarm() {
        val s = run(SimEvent.Perform("defibrillate"))
        assertEquals(1, s.harms.size)
        assertEquals("presenting", s.stateId)
    }

    @Test fun nonRepeatableAction_secondAttemptRejected() {
        val s = run(SimEvent.Perform("aspirin"), SimEvent.Perform("aspirin"))
        assertEquals(1, s.performedCount["aspirin"])
        assertEquals(1, s.kinds(LogKind.REJECTED).size)
    }

    // ------------------------------------------------------------ timed deterioration
    @Test fun noReperfusion_heartBlockAt15min_pacingRescues() {
        var s = run(SimEvent.Advance(899))
        assertEquals("presenting", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Advance(1))
        assertEquals("complete_heart_block", s.stateId)
        assertEquals(36, CaseEngine.currentState(def, s).vitals.hr)

        s = CaseEngine.reduce(def, s, SimEvent.Perform("atropine"))
        assertEquals("complete_heart_block", s.stateId)          // atropine fails in infranodal block
        s = CaseEngine.reduce(def, s, SimEvent.Perform("transcutaneous_pacing"))
        assertEquals("paced", s.stateId)
        val d = CaseEngine.debrief(def, s)
        assertTrue(d.criticalActions.first { it.def.id == "ca_pacing" }.met)
    }

    @Test fun noReperfusion_vfAt25min_deathWithoutShock() {
        var s = run(SimEvent.Advance(1500))
        assertEquals("vf_arrest", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Advance(240))
        assertTrue(s.ended)
        assertEquals("death", s.stateId)
        // ended sessions ignore further events
        assertEquals(s, CaseEngine.reduce(def, s, SimEvent.Perform("defibrillate")))
    }

    @Test fun vf_shockGivesRosc_thenLysisStillPossible() {
        var s = run(SimEvent.Order("ecg_12lead"), SimEvent.Advance(1500))
        assertEquals("vf_arrest", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Perform("tenecteplase"))   // blocked during arrest
        assertEquals("vf_arrest", s.stateId)
        s = CaseEngine.reduceAll(def, s, listOf(SimEvent.Perform("cpr"), SimEvent.Perform("defibrillate")))
        assertEquals("post_shock_cpr", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Advance(119))
        assertEquals("post_shock_cpr", s.stateId)                  // no instant ROSC
        s = CaseEngine.reduce(def, s, SimEvent.Advance(1))
        assertEquals("rosc", s.stateId)
        assertTrue(s.harms.isEmpty())
        s = CaseEngine.reduce(def, s, SimEvent.Perform("tenecteplase"))
        assertEquals("post_lysis", s.stateId)
    }

    @Test fun lysisBefore15min_preventsHeartBlock() {
        val s = run(SimEvent.Order("ecg_12lead"), SimEvent.Advance(60), SimEvent.Perform("tenecteplase"), SimEvent.Advance(900))
        assertFalse("complete_heart_block" in s.visitedStates)
        assertEquals("reperfused", s.stateId)
    }

    // ------------------------------------------------------------ investigations
    @Test fun result_reflectsStateWhenOrdered_andArrivesAfterTurnaround() {
        var s = run(SimEvent.Order("ecg_12lead"))
        assertTrue(s.results.isEmpty())
        s = CaseEngine.reduce(def, s, SimEvent.Advance(59))
        assertTrue(s.results.isEmpty())
        s = CaseEngine.reduce(def, s, SimEvent.Advance(1))
        assertTrue(s.results.getValue("ecg_12lead").startsWith("Sinus rhythm 92"))
    }

    @Test fun troponin_takesAnHour() {
        // Lyse first: an untreated patient dies before an hour has passed
        val s = run(
            SimEvent.Order("ecg_12lead"), SimEvent.Advance(60), SimEvent.Perform("tenecteplase"),
            SimEvent.Order("troponin"), SimEvent.Advance(3599)
        )
        assertFalse("troponin" in s.results)
        assertNotNull(CaseEngine.reduce(def, s, SimEvent.Advance(1)).results["troponin"])
    }

    // ------------------------------------------------------------ scoring
    @Test fun lateLysis_isMarkedLate() {
        val s = run(SimEvent.Order("ecg_12lead"), SimEvent.Advance(700), SimEvent.Perform("tenecteplase"))
        val ca = CaseEngine.debrief(def, s).criticalActions.first { it.def.id == "ca_lysis" }
        assertTrue(ca.late)
        assertFalse(ca.met)
    }

    @Test fun exam_usesStateOverrideThenBaseline() {
        var s = run(SimEvent.Examine("HEENT"))
        assertTrue(s.log.last().text.contains("JVP raised"))
        s = CaseEngine.reduceAll(def, s, listOf(SimEvent.Advance(900), SimEvent.Examine("CARD")))
        assertTrue(s.log.last().text.contains("Cannon waves"))
    }

    @Test fun engine_isDeterministic() {
        val events = listOf(SimEvent.Order("ecg_12lead"), SimEvent.Advance(120), SimEvent.Perform("gtn_sl"), SimEvent.Advance(1000))
        val a = CaseEngine.reduceAll(def, CaseEngine.start(def), events)
        val b = CaseEngine.reduceAll(def, CaseEngine.start(def), events)
        assertEquals(a, b)
    }

    // ------------------------------------------------------------ helpers
    private fun caseFile() = listOf("src/main/assets/cases/ersim_stemi_001.json", "app/src/main/assets/cases/ersim_stemi_001.json")
        .first { File(it).exists() }

    private fun expectFailure(block: () -> Unit) {
        try { block() } catch (e: RuntimeException) { return }
        fail("Expected an exception")
    }
}
