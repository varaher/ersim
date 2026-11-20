package com.example

import com.example.data.OralBoardRepository
import com.example.data.QuestionGateType
import com.example.data.RoomTeamSession
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun verifyFirst20OralBoardCasesAndCriticalActionCounts() {
        val cases = OralBoardRepository.allCases
        assertEquals(20, cases.size)

        // Exact critical action counts from Emergency Medicine Oral Board Review Illustrated 2nd Ed. Index
        val expectedCriticalActions = mapOf(
            1 to 5, 2 to 4, 3 to 7, 4 to 6, 5 to 5,
            6 to 4, 7 to 5, 8 to 5, 9 to 5, 10 to 5,
            11 to 4, 12 to 6, 13 to 6, 14 to 5, 15 to 7,
            16 to 2, 17 to 4, 18 to 5, 19 to 4, 20 to 7
        )

        cases.forEach { c ->
            assertEquals(
                "Case #${c.caseNumber} critical action count mismatch",
                expectedCriticalActions[c.caseNumber],
                c.criticalActions.size
            )
            // Every case has 4 default map-placed unlock questions (Q0, Q1, Q2, Q3)
            assertEquals(4, c.defaultQuestions.size)
            assertTrue(c.defaultQuestions.map { it.gateType }.containsAll(QuestionGateType.entries))
        }

        // Verify Case 6 explicit ventilator preset (10 mL/kg, 30/min, FiO2 100%)
        val case6 = OralBoardRepository.getCaseByNumber(6)
        assertEquals(10, case6.ventilatorPreset.tidalVolumeMlPerKg)
        assertEquals(30, case6.ventilatorPreset.respRatePerMin)
        assertEquals(100, case6.ventilatorPreset.fio2Percent)

        // Verify Case 17 explicit defibrillator preset (4 J/kg)
        val case17 = OralBoardRepository.getCaseByNumber(17)
        assertTrue(case17.defibrillatorPreset.energyPerKgLabel.contains("4 J/kg"))
    }

    @Test
    fun verifyCode100SixDrawerCrashCartInventory() {
        val drawers = OralBoardRepository.crashCartDrawers
        // Top Shelf (0) + 6 Drawers (1..6) = 7 sections
        assertEquals(7, drawers.size)
        assertTrue(drawers.all { it.items.isNotEmpty() })
    }

    @Test
    fun verifyMultiRoomTieBreakerRanksFasterRoomFirst() {
        val teamSlow = RoomTeamSession(
            roomId = "RESUS-BAY-1",
            teamName = "Alpha",
            members = listOf("Dr. A"),
            activeMember = "Dr. A",
            assignedCaseNumber = 1,
            elapsedDeciseconds = 1500L, // 02:30.0
            completedCriticalActionIds = setOf("a1", "a2", "a3", "a4")
        )
        val teamFast = RoomTeamSession(
            roomId = "RESUS-BAY-2",
            teamName = "Bravo",
            members = listOf("Dr. B"),
            activeMember = "Dr. B",
            assignedCaseNumber = 1,
            elapsedDeciseconds = 1120L, // 01:52.0
            completedCriticalActionIds = setOf("a1", "a2", "a3", "a4")
        )
        val ranked = listOf(teamSlow, teamFast).sortedWith(
            compareByDescending<RoomTeamSession> { it.tasksCompletedCount }
                .thenBy { it.elapsedDeciseconds }
        )
        assertEquals("RESUS-BAY-2", ranked.first().roomId)
    }
}
