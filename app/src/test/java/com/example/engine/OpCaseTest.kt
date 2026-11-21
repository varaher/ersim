package com.example.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Pathway tests for ERSIM-OP-001 (organophosphate poisoning). */
class OpCaseTest {

    private val def: CaseDefinition by lazy {
        val f = listOf("src/main/assets/cases/ersim_op_001.json", "app/src/main/assets/cases/ersim_op_001.json")
            .map { File(it) }.first { it.exists() }
        CaseParser.parse(f.readText())
    }

    private fun run(vararg e: SimEvent) = CaseEngine.reduceAll(def, CaseEngine.start(def), e.toList())
    private fun ca(s: CaseSession, id: String) = CaseEngine.debrief(def, s).criticalActions.first { it.def.id == id }

    @Test fun loadsAndValidates() {
        assertEquals("ERSIM-OP-001", def.meta.caseId)
        assertEquals("cholinergic_crisis", def.initialStateId)
        assertEquals(48, CaseEngine.currentState(def, CaseEngine.start(def)).vitals.hr)
    }

    @Test fun idealPathway_atropiniseInfuseAdmit_fullScore() {
        val s = run(
            SimEvent.Perform("iv_access"),
            SimEvent.Perform("oxygen"),
            SimEvent.Perform("atropine_bolus"),          // partial
            SimEvent.Ask("hx_poison"),
            SimEvent.Perform("decontaminate"),
            SimEvent.Perform("ns_bolus"),
            SimEvent.Advance(300),                        // reassess at 5 min
            SimEvent.Perform("atropine_bolus"),          // doubled -> atropinised
            SimEvent.Perform("atropine_infusion"),       // maintained
            SimEvent.Perform("medicolegal"),
            SimEvent.Perform("psych_plan"),
            SimEvent.Perform("arrange_icu")              // handover
        )
        assertTrue(s.ended)
        assertEquals("handover", s.stateId)
        assertTrue(s.harms.isEmpty())
        val d = CaseEngine.debrief(def, s)
        assertEquals(100, d.scorePercent)
        assertFalse(d.criticalActions.any { it.def.id == "ca_airway" || it.def.id == "ca_hold" })
    }

    @Test fun singleBolusNotEscalated_progressesToRespiratoryFailure_thenDeath() {
        var s = run(SimEvent.Perform("atropine_bolus"), SimEvent.Advance(599))
        assertEquals("partial_atropinisation", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Advance(1))
        assertEquals("resp_failure", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Advance(300))
        assertEquals("death", s.stateId)
        assertTrue(s.ended)
    }

    @Test fun noAtropine_respiratoryFailureAt10min_rocuroniumIntubationScores() {
        var s = run(SimEvent.Advance(600))
        assertEquals("resp_failure", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Perform("rsi_rocuronium"))
        assertEquals("ventilated_crisis", s.stateId)
        s = CaseEngine.reduceAll(def, s, listOf(
            SimEvent.Perform("atropine_bolus"),
            SimEvent.Perform("arrange_icu"),
            SimEvent.Perform("atropine_infusion")
        ))
        assertEquals("icu_handover", s.stateId)
        assertTrue(ca(s, "ca_airway").met)
        assertTrue(ca(s, "ca_atropine").late)
    }

    @Test fun succinylcholine_isHarm() {
        val s = run(SimEvent.Advance(600), SimEvent.Perform("rsi_succinylcholine"))
        assertEquals("ventilated_crisis", s.stateId)
        assertEquals(1, s.harms.size)
        assertFalse(ca(s, "ca_no_sux").met)
    }

    @Test fun extraBolusWhenAtropinised_causesToxicity_holdingInfusionResolves() {
        var s = run(SimEvent.Perform("atropine_bolus"), SimEvent.Perform("atropine_bolus"))
        assertEquals("atropinised", s.stateId)
        s = CaseEngine.reduce(def, s, SimEvent.Perform("atropine_bolus"))
        assertEquals("atropine_toxicity", s.stateId)
        assertEquals(1, s.harms.size)
        assertEquals(144, CaseEngine.currentState(def, s).vitals.hr)
        s = CaseEngine.reduce(def, s, SimEvent.Perform("hold_atropine_infusion"))
        assertEquals("maintained", s.stateId)
        assertTrue(ca(s, "ca_hold").met)
    }

    @Test fun atropinisedWithoutInfusion_recurs_rebolusRecovers() {
        var s = run(SimEvent.Perform("atropine_bolus"), SimEvent.Perform("atropine_bolus"), SimEvent.Advance(600))
        assertEquals("recurrent_crisis", s.stateId)
        assertTrue(CaseEngine.currentState(def, s).vitals.hr < 60)
        s = CaseEngine.reduce(def, s, SimEvent.Perform("atropine_bolus"))
        assertEquals("atropinised", s.stateId)
        assertTrue(s.harms.isEmpty())
    }

    @Test fun infusionBeforeAtropinisation_isBlockedThenNoted() {
        var s = run(SimEvent.Perform("atropine_infusion"))
        assertFalse("atropine_infusion" in s.performed)          // requires a bolus first
        s = CaseEngine.reduceAll(def, s, listOf(SimEvent.Perform("atropine_bolus"), SimEvent.Perform("atropine_infusion")))
        assertEquals("partial_atropinisation", s.stateId)       // infusion alone does not atropinise
        assertTrue(s.log.any { it.kind == LogKind.NOTE && it.refId == "atropine_infusion" })
    }

    @Test fun gastricLavage_isHarm_charcoalIsNote() {
        val s = run(SimEvent.Perform("gastric_lavage"), SimEvent.Perform("activated_charcoal"))
        assertEquals(1, s.harms.size)
        assertTrue(s.log.any { it.kind == LogKind.NOTE && it.refId == "activated_charcoal" })
    }

    @Test fun examFindings_changeWithAtropinisation() {
        var s = run(SimEvent.Examine("PULM"))
        assertTrue(GameRules.lastExamFinding(s, "PULM")!!.contains("crackles"))
        s = CaseEngine.reduceAll(def, s, listOf(SimEvent.Perform("atropine_bolus"), SimEvent.Perform("atropine_bolus"), SimEvent.Examine("PULM")))
        assertTrue(GameRules.lastExamFinding(s, "PULM")!!.startsWith("Chest clear"))
    }

    @Test fun container_identifiesPoison_after5min() {
        val s = run(SimEvent.Order("check_container"), SimEvent.Perform("atropine_bolus"), SimEvent.Advance(300))
        assertTrue(s.results.getValue("check_container").contains("quinalphos"))
    }
}
