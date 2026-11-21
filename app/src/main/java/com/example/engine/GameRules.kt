package com.example.engine

// ============================================================================
// GAME RULES — pure presentation logic on top of the engine (stars, alarms,
// nurse call-outs, action feedback). Pure Kotlin so it is unit-tested.
// Never invents clinical facts: every string is built from case data.
// ============================================================================

enum class VitalAlarm(val label: String) {
    NO_PULSE("No pulse"),
    HR_LOW("Bradycardia"),
    HR_HIGH("Tachycardia"),
    BP_LOW("Hypotension"),
    SPO2_LOW("Low SpO₂"),
    RR_HIGH("Tachypnoea")
}

object GameRules {

    fun stars(scorePercent: Int): Int = when {
        scorePercent >= 90 -> 3
        scorePercent >= 70 -> 2
        scorePercent >= 40 -> 1
        else -> 0
    }

    fun alarms(v: SimVitals): Set<VitalAlarm> {
        if (v.hr == 0 && v.sbp == 0) return setOf(VitalAlarm.NO_PULSE)
        return buildSet {
            if (v.hr < 50) add(VitalAlarm.HR_LOW)
            if (v.hr > 120) add(VitalAlarm.HR_HIGH)
            if (v.sbp < 90) add(VitalAlarm.BP_LOW)
            if (v.spo2 < 90) add(VitalAlarm.SPO2_LOW)
            if (v.rr > 28) add(VitalAlarm.RR_HIGH)
        }
    }

    /** Log entries added between two sessions. */
    fun newEntries(prev: CaseSession, next: CaseSession): List<LogEntry> =
        if (next.log.size >= prev.log.size) next.log.drop(prev.log.size) else next.log

    /**
     * Nurse call-out when the patient changes state, or a result arrives.
     * Returns null when nothing worth calling out happened.
     */
    fun nurseCallout(def: CaseDefinition, prev: CaseSession, next: CaseSession): String? {
        if (next.stateId != prev.stateId) {
            val st = def.states.getValue(next.stateId)
            if (st.terminal) return null
            val v = st.vitals
            val a = alarms(v)
            return when {
                VitalAlarm.NO_PULSE in a -> "Doctor! No pulse — monitor shows ${v.rhythm}!"
                a.isNotEmpty() -> "Doctor, he's changed: HR ${v.hr}, BP ${v.bpDisplay}, SpO₂ ${v.spo2}%. ${v.rhythm}."
                else -> "Doctor, update: HR ${v.hr}, BP ${v.bpDisplay}, SpO₂ ${v.spo2}%. ${st.appearance}"
            }
        }
        val result = newEntries(prev, next).lastOrNull { it.kind == LogKind.RESULT } ?: return null
        val name = result.refId?.let { def.investigationsById[it]?.name } ?: "Result"
        return "Doctor, the $name is back."
    }

    /** The one line to show the player after their action (most important first). */
    fun feedbackFor(prev: CaseSession, next: CaseSession): LogEntry? {
        val fresh = newEntries(prev, next)
        val priority = listOf(LogKind.HARM, LogKind.REJECTED, LogKind.STATE_CHANGE, LogKind.NOTE, LogKind.INFO, LogKind.ORDERED, LogKind.ACTION)
        for (k in priority) fresh.lastOrNull { it.kind == k }?.let { return it }
        return null
    }

    /** Seconds until a pending investigation result is ready (null if not pending). */
    fun secondsUntilResult(s: CaseSession, investigationId: String): Int? =
        s.pending.firstOrNull { it.investigationId == investigationId }?.let { (it.readyAtSec - s.elapsedSec).coerceAtLeast(0) }

    /** Latest logged exam finding for a region (re-examining refreshes it). */
    fun lastExamFinding(s: CaseSession, region: String): String? =
        s.log.lastOrNull { it.kind == LogKind.EXAM && it.refId == region }?.text?.substringAfter(": ")

    /** Latest patient speech (opening line, state-change lines and history answers). */
    fun lastPatientLine(s: CaseSession): String? =
        s.log.lastOrNull { it.kind == LogKind.HISTORY }?.text
}
