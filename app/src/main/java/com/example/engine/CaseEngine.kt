package com.example.engine

// ============================================================================
// ER SIM CASE ENGINE V1 — deterministic reducer
//   session' = CaseEngine.reduce(case, session, event)
// No clocks, no randomness, no Android: same inputs -> same outputs, so every
// pathway is unit-testable and replayable (instructor review, voice, XR).
// ============================================================================

sealed interface SimEvent {
    /** Advance simulated time. UI ticks this from a timer; tests jump directly. */
    data class Advance(val seconds: Int) : SimEvent
    data class Perform(val actionId: String) : SimEvent
    data class Order(val investigationId: String) : SimEvent
    data class Ask(val historyId: String) : SimEvent
    data class Examine(val region: String) : SimEvent
}

enum class LogKind { INFO, ACTION, REJECTED, NOTE, HARM, ORDERED, RESULT, HISTORY, EXAM, STATE_CHANGE, END }

data class LogEntry(val atSec: Int, val kind: LogKind, val text: String, val refId: String? = null)

data class PendingResult(val investigationId: String, val orderedAtSec: Int, val readyAtSec: Int, val result: String)

data class CaseSession(
    val caseId: String,
    val caseVersion: Int,                                // a saved session only replays on the same case version
    val elapsedSec: Int = 0,
    val stateId: String,
    val stateEnteredAtSec: Int = 0,
    val visitedStates: List<String>,
    val performed: Map<String, Int> = emptyMap(),        // first time each action/order/ask happened ("ask:<id>" for history)
    val performedCount: Map<String, Int> = emptyMap(),
    val pending: List<PendingResult> = emptyList(),
    val results: Map<String, String> = emptyMap(),       // investigationId -> latest delivered result
    val examined: Set<String> = emptySet(),
    val harms: List<LogEntry> = emptyList(),
    val log: List<LogEntry> = emptyList(),
    val ended: Boolean = false,
    val outcome: String? = null
)

object CaseEngine {

    fun start(def: CaseDefinition): CaseSession {
        val s0 = def.states.getValue(def.initialStateId)
        val opening = listOfNotNull(
            LogEntry(0, LogKind.INFO, "${def.patient.name}, ${def.patient.ageYears} y. ${def.chiefComplaint}"),
            s0.patientSays?.let { LogEntry(0, LogKind.HISTORY, it) }
        )
        return CaseSession(
            caseId = def.meta.caseId,
            caseVersion = def.meta.version,
            stateId = s0.id,
            visitedStates = listOf(s0.id),
            log = opening
        )
    }

    fun currentState(def: CaseDefinition, s: CaseSession): PatientStateDef = def.states.getValue(s.stateId)

    fun reduce(def: CaseDefinition, s: CaseSession, e: SimEvent): CaseSession {
        require(s.caseId == def.meta.caseId && s.caseVersion == def.meta.version) {
            "Session ${s.caseId} v${s.caseVersion} does not belong to ${def.meta.caseId} v${def.meta.version}"
        }
        require(s.stateId in def.states) { "Unknown saved patient state ${s.stateId}" }
        if (s.ended) return s
        return when (e) {
            is SimEvent.Advance -> advance(def, s, e.seconds)
            is SimEvent.Perform -> perform(def, s, e.actionId)
            is SimEvent.Order -> order(def, s, e.investigationId)
            is SimEvent.Ask -> ask(def, s, e.historyId)
            is SimEvent.Examine -> examine(def, s, e.region)
        }
    }

    fun reduceAll(def: CaseDefinition, s: CaseSession, events: List<SimEvent>): CaseSession =
        events.fold(s) { acc, ev -> reduce(def, acc, ev) }

    // ---------------------------------------------------------------- time
    private fun advance(def: CaseDefinition, start: CaseSession, seconds: Int): CaseSession {
        var s = start
        repeat(seconds.coerceAtLeast(0)) {
            if (s.ended) return s
            s = s.copy(elapsedSec = s.elapsedSec + 1)
            s = deliverResults(def, s)
            s = applyTimeTransitions(def, s)
        }
        return s
    }

    private fun deliverResults(def: CaseDefinition, s: CaseSession): CaseSession {
        val (ready, waiting) = s.pending.partition { it.readyAtSec <= s.elapsedSec }
        if (ready.isEmpty()) return s
        var out = s.copy(pending = waiting)
        ready.forEach { r ->
            val name = def.investigationsById.getValue(r.investigationId).name
            val key = "result:${r.investigationId}"   // result available, not merely ordered
            out = out.copy(
                performed = if (key in out.performed) out.performed else out.performed + (key to s.elapsedSec),
                results = out.results + (r.investigationId to r.result),
                log = out.log + LogEntry(s.elapsedSec, LogKind.RESULT, "$name: ${r.result}", r.investigationId)
            )
        }
        return out
    }

    private fun applyTimeTransitions(def: CaseDefinition, s: CaseSession): CaseSession {
        val t = def.transitions.firstOrNull { t ->
            appliesFrom(t, s) && when (val trig = t.trigger) {
                is Trigger.AfterCaseSeconds -> s.elapsedSec >= trig.seconds
                is Trigger.AfterSecondsInState -> s.elapsedSec - s.stateEnteredAtSec >= trig.seconds
                is Trigger.OnAction -> false
            } && guardsPass(def, s, t)
        } ?: return s
        return enterState(def, s, t)
    }

    // ---------------------------------------------------------------- actions
    private fun perform(def: CaseDefinition, s: CaseSession, actionId: String): CaseSession {
        val a = def.interventionsById[actionId]
            ?: return s.withLog(LogKind.REJECTED, "Unknown action '$actionId'", actionId)
        if (!a.repeatable && actionId in s.performed) {
            return s.withLog(LogKind.REJECTED, "${a.name} already done.", actionId)
        }
        if (!a.requires.all { holds(def, s, it) }) {
            return s.withLog(LogKind.REJECTED, a.requiresMessage ?: "${a.name}: prerequisites not met.", actionId)
        }

        val doseText = listOfNotNull(a.dose, a.route).joinToString(" ")
        var out = s.markDone(actionId).withLog(LogKind.ACTION, a.name + if (doseText.isNotEmpty()) " — $doseText" else "", actionId)

        // Safety checks use the state BEFORE the action takes effect.
        // Process harm: a required safety check was skipped (e.g. lysis without contraindication screening).
        if (a.unsafeUnless.isNotEmpty() && !a.unsafeUnless.all { holds(def, s, it) }) {
            val harm = LogEntry(s.elapsedSec, LogKind.HARM, a.unsafeReason ?: "${a.name} given without the required safety checks.", actionId)
            out = out.copy(harms = out.harms + harm, log = out.log + harm)
        }
        // Patient harm: the action itself is contraindicated now.
        if (a.contraindicatedWhen.any { holds(def, s, it) }) {
            val harm = LogEntry(s.elapsedSec, LogKind.HARM, a.contraindicationReason ?: "${a.name} was contraindicated.", actionId)
            out = out.copy(harms = out.harms + harm, log = out.log + harm)
        } else if (a.notIndicatedWhen.any { holds(def, s, it) }) {
            out = out.withLog(LogKind.NOTE, a.notIndicatedReason ?: "${a.name} not indicated.", actionId)
        }

        (a.feedbackByState[s.stateId] ?: a.feedbackByState["*"])?.let {
            out = out.withLog(LogKind.INFO, it, actionId)
        }

        val t = def.transitions.firstOrNull { t ->
            appliesFrom(t, s) && (t.trigger as? Trigger.OnAction)?.actionId == actionId && guardsPass(def, out, t)
        }
        return if (t != null) enterState(def, out, t) else out
    }

    private fun order(def: CaseDefinition, s: CaseSession, id: String): CaseSession {
        val inv = def.investigationsById[id]
            ?: return s.withLog(LogKind.REJECTED, "Unknown investigation '$id'", id)
        if (s.pending.any { it.investigationId == id }) {
            return s.withLog(LogKind.REJECTED, "${inv.name} already pending.", id)
        }
        // Result reflects the patient at the moment the test is done
        val result = inv.resultsByState[s.stateId] ?: inv.resultsByState.getValue("*")
        var out = s.markDone(id).withLog(LogKind.ORDERED, "${inv.name} ordered", id)
        out = out.copy(pending = out.pending + PendingResult(id, s.elapsedSec, s.elapsedSec + inv.turnaroundSec, result))
        out = if (inv.turnaroundSec == 0) deliverResults(def, out) else out
        val t = def.transitions.firstOrNull { t ->
            appliesFrom(t, s) && (t.trigger as? Trigger.OnAction)?.actionId == id && guardsPass(def, out, t)
        }
        return if (t != null) enterState(def, out, t) else out
    }

    private fun ask(def: CaseDefinition, s: CaseSession, id: String): CaseSession {
        val h = def.historyById[id] ?: return s.withLog(LogKind.REJECTED, "Unknown question '$id'", id)
        val who = if (h.answeredBy == "patient") def.patient.name.substringBefore(" ") else h.answeredBy
        return s.markDone("ask:$id").withLog(LogKind.HISTORY, "${h.topic} — $who: \"${h.answer}\"", id)
    }

    private fun examine(def: CaseDefinition, s: CaseSession, region: String): CaseSession {
        if (region !in CaseParser.EXAM_REGIONS) return s.withLog(LogKind.REJECTED, "Unknown exam region '$region'", region)
        val finding = currentState(def, s).exam[region] ?: def.baselineExam.getValue(region)
        return s.copy(examined = s.examined + region).withLog(LogKind.EXAM, "$region: $finding", region)
    }

    // ---------------------------------------------------------------- transitions
    private fun appliesFrom(t: TransitionDef, s: CaseSession) = "*" in t.from || s.stateId in t.from

    private fun guardsPass(def: CaseDefinition, s: CaseSession, t: TransitionDef) =
        t.whenAll.all { holds(def, s, it) } && t.unlessAny.none { holds(def, s, it) }

    private fun enterState(def: CaseDefinition, s: CaseSession, t: TransitionDef): CaseSession {
        val next = def.states.getValue(t.to)
        var out = s.copy(
            stateId = next.id,
            stateEnteredAtSec = s.elapsedSec,
            visitedStates = s.visitedStates + next.id
        )
        out = out.withLog(LogKind.STATE_CHANGE, t.message.ifBlank { next.label }, t.id)
        next.patientSays?.let { out = out.withLog(LogKind.HISTORY, it) }
        if (next.terminal) {
            out = out.copy(ended = true, outcome = next.outcome ?: next.label)
                .withLog(LogKind.END, "Case ended: ${next.outcome ?: next.label}")
        }
        return out
    }

    // ---------------------------------------------------------------- conditions
    fun holds(def: CaseDefinition, s: CaseSession, c: Condition): Boolean {
        val v = def.states.getValue(s.stateId).vitals
        return when (c) {
            Condition.Always -> true
            is Condition.Fact -> c.id in def.facts
            is Condition.InState -> s.stateId == c.id
            is Condition.NotInState -> s.stateId != c.id
            is Condition.Done -> c.actionId in s.performed
            is Condition.NotDone -> c.actionId !in s.performed
            is Condition.ResultReady -> "result:${c.investigationId}" in s.performed
            is Condition.Asked -> "ask:${c.historyId}" in s.performed
            is Condition.VitalCompare -> {
                val x = when (c.field) { "hr" -> v.hr; "sbp" -> v.sbp; "dbp" -> v.dbp; "rr" -> v.rr; else -> v.spo2 }
                when (c.op) { "<" -> x < c.value; "<=" -> x <= c.value; ">" -> x > c.value; else -> x >= c.value }
            }
        }
    }

    private fun CaseSession.markDone(id: String) = copy(
        performed = if (id in performed) performed else performed + (id to elapsedSec),
        performedCount = performedCount + (id to ((performedCount[id] ?: 0) + 1))
    )

    private fun CaseSession.withLog(kind: LogKind, text: String, ref: String? = null) =
        copy(log = log + LogEntry(elapsedSec, kind, text, ref))

    // ---------------------------------------------------------------- debrief
    fun debrief(def: CaseDefinition, s: CaseSession): DebriefReport {
        val items = def.criticalActions
            .filter { it.appliesIfVisited == null || it.appliesIfVisited in s.visitedStates }
            .map { ca ->
                val doneAt = ca.satisfiedByAny.mapNotNull { s.performed[it] }.minOrNull()
                if (ca.avoid) {
                    CriticalActionResult(ca, met = doneAt == null, doneAtSec = doneAt, late = false)
                } else {
                    val anchor = ca.deadlineAfterAction?.let { s.performed[it] } ?: 0
                    val deadline = ca.deadlineSec?.let { anchor + it }
                    val late = doneAt != null && deadline != null && doneAt > deadline
                    CriticalActionResult(ca, met = doneAt != null && !late, doneAtSec = doneAt, late = late)
                }
            }
        val possible = items.sumOf { it.def.points }
        val earned = items.filter { it.met }.sumOf { it.def.points }
        val raw = if (possible == 0) 0 else (earned * 100) / possible
        // Harm stays visible in the score: each harm event costs HARM_PENALTY points.
        val afterHarm = (raw - s.harms.size * HARM_PENALTY).coerceAtLeast(0)
        // A bad outcome (e.g. death) caps the score below one star, however many boxes were ticked.
        val badOutcome = s.ended && def.states[s.stateId]?.badOutcome == true
        val final = if (badOutcome) afterHarm.coerceAtMost(BAD_OUTCOME_CAP) else afterHarm
        return DebriefReport(
            caseId = def.meta.caseId,
            outcome = s.outcome ?: "Case in progress at ${formatClock(s.elapsedSec)}",
            diagnosis = def.debrief.diagnosis,
            summary = def.debrief.summary,
            disposition = def.debrief.disposition,
            criticalActions = items,
            harms = s.harms,
            scorePercent = final,
            teachingPoints = def.debrief.teachingPoints,
            elapsedSec = s.elapsedSec,
            actionsPercent = raw,
            harmPenalty = raw - afterHarm,
            badOutcome = badOutcome
        )
    }

    const val HARM_PENALTY = 10
    const val BAD_OUTCOME_CAP = 39

    fun formatClock(sec: Int) = "%02d:%02d".format(sec / 60, sec % 60)
}

data class CriticalActionResult(val def: CriticalActionDef, val met: Boolean, val doneAtSec: Int?, val late: Boolean)

data class DebriefReport(
    val caseId: String,
    val outcome: String,
    val diagnosis: String,
    val summary: String,
    val disposition: String,
    val criticalActions: List<CriticalActionResult>,
    val harms: List<LogEntry>,
    val scorePercent: Int,
    val teachingPoints: List<String>,
    val elapsedSec: Int,
    val actionsPercent: Int = scorePercent,   // critical actions only, before penalties
    val harmPenalty: Int = 0,                 // points removed for harm events
    val badOutcome: Boolean = false           // score capped because the patient died / bad outcome
)
