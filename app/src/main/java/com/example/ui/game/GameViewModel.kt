package com.example.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.engine.CaseDefinition
import com.example.engine.CaseEngine
import com.example.engine.CaseLibrary
import com.example.engine.CaseSession
import com.example.engine.DebriefReport
import com.example.engine.GameRules
import com.example.engine.LogEntry
import com.example.engine.SimEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ============================================================================
// GAME VIEWMODEL — runs one engine-driven case in real time.
// 1 real second = 1 simulated second while playing. "Wait" jumps time.
// All clinical behaviour comes from CaseEngine + the case JSON.
// ============================================================================

data class GameUiState(
    val cases: List<CaseDefinition> = emptyList(),
    val loadError: String? = null,
    val def: CaseDefinition? = null,
    val session: CaseSession? = null,
    val paused: Boolean = false,
    val nurseMessage: String? = null,
    val feedback: LogEntry? = null,
    val debrief: DebriefReport? = null,
    val resultRecorded: Boolean = false
) {
    val isPlaying: Boolean get() = def != null && session != null
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var ticker: Job? = null
    private var backgrounded = false

    init {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { CaseLibrary.loadAll(getApplication()) }
            }
            _state.update {
                it.copy(
                    cases = result.getOrDefault(emptyList()),
                    loadError = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun startCase(caseId: String) {
        val def = _state.value.cases.firstOrNull { it.meta.caseId == caseId } ?: return
        _state.update {
            it.copy(def = def, session = CaseEngine.start(def), paused = false, nurseMessage = null, feedback = null, debrief = null, resultRecorded = false)
        }
        startTicker()
    }

    fun replay() {
        _state.value.def?.let { startCase(it.meta.caseId) }
    }

    fun exitGame() {
        ticker?.cancel()
        _state.update { it.copy(def = null, session = null, debrief = null, nurseMessage = null, feedback = null, paused = false) }
    }

    fun ask(historyId: String) = dispatch(SimEvent.Ask(historyId))
    fun examine(region: String) = dispatch(SimEvent.Examine(region))
    fun order(investigationId: String) = dispatch(SimEvent.Order(investigationId))
    fun perform(actionId: String) = dispatch(SimEvent.Perform(actionId))
    fun wait(seconds: Int) = dispatch(SimEvent.Advance(seconds))

    /** Debrief result saved to progress (guards against double-counting on recomposition). */
    fun markResultRecorded() = _state.update { it.copy(resultRecorded = true) }

    fun togglePause() = _state.update { it.copy(paused = !it.paused) }
    fun dismissNurse() = _state.update { it.copy(nurseMessage = null) }
    fun dismissFeedback() = _state.update { it.copy(feedback = null) }

    /** Called by the screen on app stop/start so the clock never runs unseen. */
    fun setBackgrounded(value: Boolean) {
        backgrounded = value
    }

    /** Player ends the case early: debrief on what was done so far. */
    fun endCase() {
        val st = _state.value
        val def = st.def ?: return
        val s = st.session ?: return
        ticker?.cancel()
        _state.update { it.copy(debrief = CaseEngine.debrief(def, s)) }
    }

    private fun dispatch(event: SimEvent) {
        val st = _state.value
        val def = st.def ?: return
        val prev = st.session ?: return
        if (st.debrief != null) return
        val next = CaseEngine.reduce(def, prev, event)
        val nurse = GameRules.nurseCallout(def, prev, next)
        val fb = if (event is SimEvent.Advance) null else GameRules.feedbackFor(prev, next)
        _state.update {
            it.copy(
                session = next,
                nurseMessage = nurse ?: it.nurseMessage,
                feedback = fb ?: it.feedback,
                debrief = if (next.ended) CaseEngine.debrief(def, next) else null
            )
        }
        if (next.ended) ticker?.cancel()
    }

    private fun startTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val st = _state.value
                if (!st.paused && !backgrounded && st.debrief == null) dispatch(SimEvent.Advance(1))
            }
        }
    }

    override fun onCleared() {
        ticker?.cancel()
        super.onCleared()
    }
}
