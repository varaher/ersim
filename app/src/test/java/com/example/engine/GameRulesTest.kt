package com.example.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class GameRulesTest {

    private val def: CaseDefinition by lazy {
        val f = listOf("src/main/assets/cases/ersim_stemi_001.json", "app/src/main/assets/cases/ersim_stemi_001.json")
            .map { File(it) }.first { it.exists() }
        CaseParser.parse(f.readText())
    }

    @Test fun stars_thresholds() {
        assertEquals(3, GameRules.stars(100))
        assertEquals(3, GameRules.stars(90))
        assertEquals(2, GameRules.stars(70))
        assertEquals(1, GameRules.stars(40))
        assertEquals(0, GameRules.stars(39))
    }

    @Test fun alarms_fromVitals() {
        assertEquals(setOf(VitalAlarm.NO_PULSE), GameRules.alarms(SimVitals(0, 0, 0, 0, 0, 36.9, "VF")))
        val chb = GameRules.alarms(SimVitals(36, 82, 50, 22, 94, 36.9, "CHB"))
        assertTrue(VitalAlarm.HR_LOW in chb && VitalAlarm.BP_LOW in chb)
        assertTrue(GameRules.alarms(SimVitals(92, 108, 68, 20, 96, 36.9, "SR")).isEmpty())
    }

    @Test fun nurseCallsOut_stateChangeAndResults() {
        val s0 = CaseEngine.start(def)
        val s1 = CaseEngine.reduce(def, s0, SimEvent.Order("ecg_12lead"))
        val s2 = CaseEngine.reduce(def, s1, SimEvent.Advance(60))
        assertEquals("Doctor, the 12-lead ECG is back.", GameRules.nurseCallout(def, s1, s2))
        val s3 = CaseEngine.reduce(def, s2, SimEvent.Perform("gtn_sl"))
        val call = GameRules.nurseCallout(def, s2, s3)
        assertNotNull(call)
        assertTrue(call!!.contains("74/46"))
    }

    @Test fun feedback_prefersHarm() {
        val s0 = CaseEngine.reduceAll(def, CaseEngine.start(def), listOf(SimEvent.Order("ecg_12lead"), SimEvent.Advance(60)))
        val s1 = CaseEngine.reduce(def, s0, SimEvent.Perform("gtn_sl"))
        assertEquals(LogKind.HARM, GameRules.feedbackFor(s0, s1)!!.kind)
    }

    @Test fun pendingCountdown_andExamRefresh() {
        val s = CaseEngine.reduceAll(def, CaseEngine.start(def), listOf(SimEvent.Order("ecg_12lead"), SimEvent.Advance(20)))
        assertEquals(40, GameRules.secondsUntilResult(s, "ecg_12lead"))
        var e = CaseEngine.reduce(def, s, SimEvent.Examine("CARD"))
        assertTrue(GameRules.lastExamFinding(e, "CARD")!!.startsWith("Regular pulse"))
        e = CaseEngine.reduceAll(def, e, listOf(SimEvent.Advance(900), SimEvent.Examine("CARD")))
        assertTrue(GameRules.lastExamFinding(e, "CARD")!!.contains("Cannon waves"))
    }
}
