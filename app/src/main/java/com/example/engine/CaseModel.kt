package com.example.engine

// ============================================================================
// ER SIM CASE ENGINE V1 — CASE FORMAT
// A case is authored data (assets/cases/*.json). Clinical truth lives here,
// never in UI or AI dialogue. Pure Kotlin: no Android imports.
// ============================================================================

data class CaseMeta(
    val caseId: String,
    val version: Int,
    val title: String,
    val category: String,
    val difficulty: String,
    val learnerLevel: String,
    val estimatedMinutes: Int,
    val reviewStatus: String,          // DRAFT | IN_REVIEW | APPROVED
    val authors: List<String>,
    val reviewers: List<String>,
    val guidelineBasis: String,
    val evidenceDate: String,
    val sourceNotes: String,           // internal provenance, never shown to learners
    val learningObjectives: List<String>
)

data class CasePatient(
    val name: String,
    val ageYears: Int,
    val sex: String,                   // MALE | FEMALE
    val weightKg: Double,
    val characterFamily: String,       // matches PatientCharacter.assetFamilyKey
    val language: String,
    val historian: String,
    val setting: String
)

data class SimVitals(
    val hr: Int,
    val sbp: Int,
    val dbp: Int,
    val rr: Int,
    val spo2: Int,
    val tempC: Double,
    val rhythm: String
) {
    val bpDisplay: String get() = "$sbp/$dbp"
}

data class PatientStateDef(
    val id: String,
    val label: String,
    val vitals: SimVitals,
    val appearance: String,
    val exam: Map<String, String>,     // region key (GENERAL, HEENT, PULM, CARD, ABD, SKIN, NEURO) -> finding
    val patientSays: String?,          // what the patient says/does on entering this state
    val terminal: Boolean,
    val outcome: String?
)

data class HistoryItem(
    val id: String,
    val topic: String,
    val samplePrompts: List<String>,   // for voice/intent matching later
    val answer: String,
    val answeredBy: String
)

data class InvestigationDef(
    val id: String,
    val name: String,
    val turnaroundSec: Int,
    val resultsByState: Map<String, String>   // stateId -> result, "*" = default
)

data class InterventionDef(
    val id: String,
    val name: String,
    val category: String,
    val dose: String?,
    val route: String?,
    val repeatable: Boolean,
    val requires: List<Condition>,            // all must hold, else REJECTED
    val requiresMessage: String?,
    val contraindicatedWhen: List<Condition>, // any holds -> performed, logged as HARM
    val contraindicationReason: String?,
    val notIndicatedWhen: List<Condition>,    // any holds -> performed, logged as NOTE
    val notIndicatedReason: String?,
    val feedbackByState: Map<String, String>  // stateId or "*" -> response text
)

sealed interface Trigger {
    data class OnAction(val actionId: String) : Trigger
    data class AfterCaseSeconds(val seconds: Int) : Trigger
    data class AfterSecondsInState(val seconds: Int) : Trigger
}

data class TransitionDef(
    val id: String,
    val from: List<String>,                   // state ids, or "*"
    val to: String,
    val trigger: Trigger,
    val whenAll: List<Condition>,
    val unlessAny: List<Condition>,
    val message: String
)

data class CriticalActionDef(
    val id: String,
    val label: String,
    val satisfiedByAny: List<String>,         // action ids, investigation ids or "ask:<historyId>"
    val avoid: Boolean,                       // true = success means NEVER doing it
    val deadlineSec: Int?,                    // from case start
    val deadlineAfterAction: String?,         // deadline counts from this action instead
    val appliesIfVisited: String?,            // only scored if this state occurred
    val points: Int,
    val teaching: String
)

data class DebriefDef(
    val diagnosis: String,
    val summary: String,
    val disposition: String,
    val teachingPoints: List<String>
)

data class CaseDefinition(
    val meta: CaseMeta,
    val patient: CasePatient,
    val chiefComplaint: String,
    val triageNote: String,
    val facts: Set<String>,                   // hidden case truths used by conditions
    val initialStateId: String,
    val states: Map<String, PatientStateDef>,
    val baselineExam: Map<String, String>,
    val history: List<HistoryItem>,
    val investigations: List<InvestigationDef>,
    val interventions: List<InterventionDef>,
    val transitions: List<TransitionDef>,
    val criticalActions: List<CriticalActionDef>,
    val debrief: DebriefDef
) {
    val investigationsById = investigations.associateBy { it.id }
    val interventionsById = interventions.associateBy { it.id }
    val historyById = history.associateBy { it.id }
}

// ============================================================================
// CONDITIONS — tiny, explicit, testable DSL
//   "always" | "fact:<id>" | "state:<id>" | "notState:<id>" |
//   "done:<actionId>" | "notDone:<actionId>" | "vitals.<hr|sbp|dbp|rr|spo2><op><n>"  (op: < <= > >=)
// ============================================================================

sealed interface Condition {
    object Always : Condition
    data class Fact(val id: String) : Condition
    data class InState(val id: String) : Condition
    data class NotInState(val id: String) : Condition
    data class Done(val actionId: String) : Condition
    data class NotDone(val actionId: String) : Condition
    data class VitalCompare(val field: String, val op: String, val value: Int) : Condition

    companion object {
        private val vitalRegex = Regex("""^vitals\.(hr|sbp|dbp|rr|spo2)(<=|>=|<|>)(-?\d+)$""")

        fun parse(raw: String): Condition {
            val s = raw.trim()
            return when {
                s == "always" -> Always
                s.startsWith("fact:") -> Fact(s.removePrefix("fact:"))
                s.startsWith("state:") -> InState(s.removePrefix("state:"))
                s.startsWith("notState:") -> NotInState(s.removePrefix("notState:"))
                s.startsWith("done:") -> Done(s.removePrefix("done:"))
                s.startsWith("notDone:") -> NotDone(s.removePrefix("notDone:"))
                else -> vitalRegex.matchEntire(s)?.let {
                    VitalCompare(it.groupValues[1], it.groupValues[2], it.groupValues[3].toInt())
                } ?: throw CaseFormatException("Unknown condition \"$raw\"")
            }
        }
    }
}

class CaseFormatException(message: String) : RuntimeException(message)

// ============================================================================
// PARSER + VALIDATOR
// ============================================================================

object CaseParser {

    val EXAM_REGIONS = setOf("GENERAL", "HEENT", "PULM", "CARD", "ABD", "SKIN", "NEURO")

    fun parse(json: String): CaseDefinition {
        @Suppress("UNCHECKED_CAST")
        val root = MiniJson.parse(json) as? Map<String, Any?> ?: throw CaseFormatException("Root must be an object")
        val def = build(root)
        validate(def)
        return def
    }

    private fun build(r: Map<String, Any?>): CaseDefinition {
        val m = r.obj("meta")
        val p = r.obj("patient")
        val pres = r.obj("presentation")
        val d = r.obj("debrief")
        return CaseDefinition(
            meta = CaseMeta(
                caseId = m.str("caseId"), version = m.int("version"), title = m.str("title"),
                category = m.str("category"), difficulty = m.str("difficulty"),
                learnerLevel = m.str("learnerLevel"), estimatedMinutes = m.int("estimatedMinutes"),
                reviewStatus = m.str("reviewStatus"), authors = m.strList("authors"),
                reviewers = m.strList("reviewers"), guidelineBasis = m.str("guidelineBasis"),
                evidenceDate = m.str("evidenceDate"), sourceNotes = m.str("sourceNotes"),
                learningObjectives = m.strList("learningObjectives")
            ),
            patient = CasePatient(
                name = p.str("name"), ageYears = p.int("ageYears"), sex = p.str("sex"),
                weightKg = p.dbl("weightKg"), characterFamily = p.str("characterFamily"),
                language = p.str("language"), historian = p.str("historian"), setting = p.str("setting")
            ),
            chiefComplaint = pres.str("chiefComplaint"),
            triageNote = pres.str("triageNote"),
            facts = r.strList("facts").toSet(),
            initialStateId = r.str("initialState"),
            states = r.objList("states").map { s ->
                val v = s.obj("vitals")
                PatientStateDef(
                    id = s.str("id"), label = s.str("label"),
                    vitals = SimVitals(v.int("hr"), v.int("sbp"), v.int("dbp"), v.int("rr"), v.int("spo2"), v.dbl("tempC"), v.str("rhythm")),
                    appearance = s.str("appearance"),
                    exam = s.strMapOpt("exam"),
                    patientSays = s.strOpt("patientSays"),
                    terminal = s.boolOpt("terminal") ?: false,
                    outcome = s.strOpt("outcome")
                )
            }.associateBy { it.id },
            baselineExam = r.strMapOpt("baselineExam"),
            history = r.objList("history").map { h ->
                HistoryItem(h.str("id"), h.str("topic"), h.strList("samplePrompts"), h.str("answer"), h.strOpt("answeredBy") ?: "patient")
            },
            investigations = r.objList("investigations").map { iv ->
                InvestigationDef(iv.str("id"), iv.str("name"), iv.int("turnaroundSec"), iv.strMapOpt("resultsByState"))
            },
            interventions = r.objList("interventions").map { a ->
                InterventionDef(
                    id = a.str("id"), name = a.str("name"), category = a.str("category"),
                    dose = a.strOpt("dose"), route = a.strOpt("route"),
                    repeatable = a.boolOpt("repeatable") ?: false,
                    requires = a.condList("requires"), requiresMessage = a.strOpt("requiresMessage"),
                    contraindicatedWhen = a.condList("contraindicatedWhen"),
                    contraindicationReason = a.strOpt("contraindicationReason"),
                    notIndicatedWhen = a.condList("notIndicatedWhen"),
                    notIndicatedReason = a.strOpt("notIndicatedReason"),
                    feedbackByState = a.strMapOpt("feedbackByState")
                )
            },
            transitions = r.objList("transitions").map { t ->
                val trig = t.obj("trigger")
                val trigger = when {
                    trig.containsKey("onAction") -> Trigger.OnAction(trig.str("onAction"))
                    trig.containsKey("afterCaseSeconds") -> Trigger.AfterCaseSeconds(trig.int("afterCaseSeconds"))
                    trig.containsKey("afterSecondsInState") -> Trigger.AfterSecondsInState(trig.int("afterSecondsInState"))
                    else -> throw CaseFormatException("Transition ${t.str("id")}: unknown trigger")
                }
                TransitionDef(t.str("id"), t.strList("from"), t.str("to"), trigger, t.condList("when"), t.condList("unless"), t.strOpt("message") ?: "")
            },
            criticalActions = r.objList("criticalActions").map { c ->
                CriticalActionDef(
                    id = c.str("id"), label = c.str("label"), satisfiedByAny = c.strList("satisfiedBy"),
                    avoid = c.boolOpt("avoid") ?: false, deadlineSec = c.intOpt("deadlineSec"),
                    deadlineAfterAction = c.strOpt("deadlineAfterAction"),
                    appliesIfVisited = c.strOpt("appliesIfVisited"),
                    points = c.int("points"), teaching = c.str("teaching")
                )
            },
            debrief = DebriefDef(d.str("diagnosis"), d.str("summary"), d.str("disposition"), d.strList("teachingPoints"))
        )
    }

    /** Referential integrity: every id a case points to must exist. Fails loudly at load time. */
    fun validate(def: CaseDefinition) {
        val errors = mutableListOf<String>()
        val stateIds = def.states.keys
        val actionIds = def.interventions.map { it.id } + def.investigations.map { it.id }
        val historyIds = def.history.map { it.id }.toSet()
        val dupes = (actionIds + historyIds).groupBy { it }.filter { it.value.size > 1 }.keys
        if (dupes.isNotEmpty()) errors += "Duplicate ids: $dupes"
        if (def.initialStateId !in stateIds) errors += "initialState '${def.initialStateId}' missing"
        if (def.meta.version < 1) errors += "meta.version must be 1 or more"
        if (def.meta.reviewStatus !in setOf("DRAFT", "IN_REVIEW", "APPROVED")) errors += "meta.reviewStatus must be DRAFT, IN_REVIEW or APPROVED"
        if (def.meta.reviewStatus == "APPROVED" && def.meta.reviewers.none { it.isNotBlank() }) errors += "APPROVED case needs at least one named reviewer"

        fun checkCond(where: String, c: Condition) {
            when (c) {
                is Condition.InState -> if (c.id !in stateIds) errors += "$where: unknown state ${c.id}"
                is Condition.NotInState -> if (c.id !in stateIds) errors += "$where: unknown state ${c.id}"
                is Condition.Done -> if (c.actionId !in actionIds) errors += "$where: unknown action ${c.actionId}"
                is Condition.NotDone -> if (c.actionId !in actionIds) errors += "$where: unknown action ${c.actionId}"
                is Condition.Fact -> Unit // facts may be absent on purpose (e.g. a contraindication this patient lacks)
                else -> Unit
            }
        }
        def.states.values.forEach { s ->
            s.exam.keys.filter { it !in EXAM_REGIONS }.forEach { errors += "State ${s.id}: bad exam region $it" }
        }
        def.baselineExam.keys.filter { it !in EXAM_REGIONS }.forEach { errors += "baselineExam: bad region $it" }
        EXAM_REGIONS.filter { it !in def.baselineExam }.forEach { errors += "baselineExam missing $it" }
        def.investigations.forEach { iv ->
            if (iv.resultsByState.isEmpty()) errors += "Investigation ${iv.id}: no results"
            iv.resultsByState.keys.filter { it != "*" && it !in stateIds }.forEach { errors += "Investigation ${iv.id}: unknown state $it" }
        }
        def.interventions.forEach { a ->
            (a.requires + a.contraindicatedWhen + a.notIndicatedWhen).forEach { checkCond("Intervention ${a.id}", it) }
            a.feedbackByState.keys.filter { it != "*" && it !in stateIds }.forEach { errors += "Intervention ${a.id}: unknown state $it" }
        }
        def.transitions.forEach { t ->
            if (t.to !in stateIds) errors += "Transition ${t.id}: unknown target ${t.to}"
            t.from.filter { it != "*" && it !in stateIds }.forEach { errors += "Transition ${t.id}: unknown source $it" }
            val trig = t.trigger
            if (trig is Trigger.OnAction && trig.actionId !in actionIds) errors += "Transition ${t.id}: unknown action ${trig.actionId}"
            (t.whenAll + t.unlessAny).forEach { checkCond("Transition ${t.id}", it) }
        }
        def.criticalActions.forEach { c ->
            c.satisfiedByAny.forEach { ref ->
                val ok = if (ref.startsWith("ask:")) ref.removePrefix("ask:") in historyIds else ref in actionIds
                if (!ok) errors += "Critical action ${c.id}: unknown reference $ref"
            }
            c.deadlineAfterAction?.let { if (it !in actionIds) errors += "Critical action ${c.id}: unknown deadlineAfterAction $it" }
            c.appliesIfVisited?.let { if (it !in stateIds) errors += "Critical action ${c.id}: unknown state $it" }
        }
        if (def.states.values.none { it.terminal }) errors += "No terminal state"
        if (errors.isNotEmpty()) throw CaseFormatException("Case ${def.meta.caseId} invalid:\n- " + errors.joinToString("\n- "))
    }

    // ---- typed accessors ----
    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.obj(k: String) = this[k] as? Map<String, Any?> ?: throw CaseFormatException("Missing object '$k'")
    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.objList(k: String) = (this[k] as? List<Any?> ?: emptyList()).map {
        it as? Map<String, Any?> ?: throw CaseFormatException("'$k' must contain objects")
    }
    private fun Map<String, Any?>.str(k: String) = this[k] as? String ?: throw CaseFormatException("Missing string '$k'")
    private fun Map<String, Any?>.strOpt(k: String) = this[k] as? String
    private fun Map<String, Any?>.dbl(k: String) = (this[k] as? Double) ?: throw CaseFormatException("Missing number '$k'")
    private fun Map<String, Any?>.int(k: String) = dbl(k).toInt()
    private fun Map<String, Any?>.intOpt(k: String) = (this[k] as? Double)?.toInt()
    private fun Map<String, Any?>.boolOpt(k: String) = this[k] as? Boolean
    private fun Map<String, Any?>.strList(k: String) = (this[k] as? List<*> ?: emptyList<Any?>()).map {
        it as? String ?: throw CaseFormatException("'$k' must contain strings")
    }
    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.strMapOpt(k: String): Map<String, String> =
        (this[k] as? Map<String, Any?> ?: emptyMap()).mapValues { (key, v) ->
            v as? String ?: throw CaseFormatException("'$k.$key' must be a string")
        }
    private fun Map<String, Any?>.condList(k: String) = strList(k).map { Condition.parse(it) }
}
