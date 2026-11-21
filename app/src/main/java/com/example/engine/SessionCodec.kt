package com.example.engine

/**
 * Save/resume for a case in progress.
 * The engine is deterministic, so a session is stored as the list of player events
 * and rebuilt by replaying them: no clinical state is ever serialised by hand.
 * Format: "A:60|P:aspirin|O:ecg_12lead|K:hx_onset|E:CARD" (consecutive waits merged).
 */
object SessionCodec {

    fun encode(events: List<SimEvent>): String =
        compact(events).joinToString("|") { e ->
            when (e) {
                is SimEvent.Advance -> "A:${e.seconds}"
                is SimEvent.Perform -> "P:${e.actionId}"
                is SimEvent.Order -> "O:${e.investigationId}"
                is SimEvent.Ask -> "K:${e.historyId}"
                is SimEvent.Examine -> "E:${e.region}"
            }
        }

    /** Returns null if the text is not a valid encoding (corrupt or from an older format). */
    fun decode(text: String): List<SimEvent>? {
        if (text.isBlank()) return emptyList()
        return text.split("|").map { token ->
            val type = token.substringBefore(":", "")
            val arg = token.substringAfter(":", "")
            if (arg.isEmpty()) return null
            when (type) {
                "A" -> SimEvent.Advance(arg.toIntOrNull()?.takeIf { it >= 0 } ?: return null)
                "P" -> SimEvent.Perform(arg)
                "O" -> SimEvent.Order(arg)
                "K" -> SimEvent.Ask(arg)
                "E" -> SimEvent.Examine(arg)
                else -> return null
            }
        }
    }

    /** Merges runs of Advance(1) from the clock into one Advance(n). Replay result is identical. */
    fun compact(events: List<SimEvent>): List<SimEvent> {
        val out = ArrayList<SimEvent>(events.size)
        for (e in events) {
            val last = out.lastOrNull()
            if (e is SimEvent.Advance && last is SimEvent.Advance) out[out.lastIndex] = SimEvent.Advance(last.seconds + e.seconds)
            else out.add(e)
        }
        return out
    }

    /** Rebuilds a session, or null if any event no longer fits this case version. */
    fun restore(def: CaseDefinition, events: List<SimEvent>): CaseSession? = runCatching {
        CaseEngine.reduceAll(def, CaseEngine.start(def), events)
    }.getOrNull()
}
