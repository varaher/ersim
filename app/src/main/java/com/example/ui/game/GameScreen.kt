package com.example.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CaseDefinition
import com.example.engine.CaseEngine
import com.example.engine.CaseParser
import com.example.engine.CaseSession
import com.example.engine.DebriefReport
import com.example.engine.GameRules
import com.example.engine.LogEntry
import com.example.engine.LogKind
import com.example.engine.VitalAlarm
import com.example.ui.app.GameTutorialDialog
import com.example.ui.app.HowToPlayScreen
import com.example.ui.theme.*

// ============================================================================
// INTERACTIVE CASE SCREEN — the playable game loop
// Monitor • nurse • patient • Talk / Examine / Tests / Treat / Log • Debrief
// ============================================================================

private enum class GameTab(val label: String, val icon: ImageVector) {
    TALK("Talk", Icons.Default.RecordVoiceOver),
    EXAMINE("Examine", Icons.Default.PanTool),
    TESTS("Tests", Icons.Default.Science),
    TREAT("Treat", Icons.Default.Medication),
    LOG("Log", Icons.Default.History)
}

private val REGION_LABELS = linkedMapOf(
    "GENERAL" to "General", "HEENT" to "HEENT & neck", "PULM" to "Chest & lungs",
    "CARD" to "Heart", "ABD" to "Abdomen", "SKIN" to "Skin & limbs", "NEURO" to "Neuro"
)

@Composable
fun GameScreen(
    vm: GameViewModel,
    onExit: () -> Unit,
    onFinished: (caseId: String, stars: Int) -> Unit,
    showTutorial: Boolean = false,
    onTutorialSeen: () -> Unit = {}
) {
    val state by vm.state.collectAsState()
    var showHelp by rememberSaveable { mutableStateOf(false) }
    val tutorialOpen = showTutorial && state.debrief == null
    // Clock waits while the guide or the tutorial is on screen
    LaunchedEffect(showHelp, tutorialOpen) { vm.setOverlayOpen(showHelp || tutorialOpen) }
    DisposableEffect(Unit) { onDispose { vm.setOverlayOpen(false) } }
    val def = state.def
    val session = state.session

    // Clock never runs while the app is not visible
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = androidx.lifecycle.LifecycleEventObserver { _, e ->
            if (e == androidx.lifecycle.Lifecycle.Event.ON_STOP) vm.setBackgrounded(true)
            if (e == androidx.lifecycle.Lifecycle.Event.ON_START) vm.setBackgrounded(false)
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    if (def == null || session == null) {
        Box(Modifier.fillMaxSize().background(ErSimBg), contentAlignment = Alignment.Center) {
            Text(state.loadError ?: "Loading case…", color = ErSimTextMuted)
        }
        return
    }

    val debrief = state.debrief
    LaunchedEffect(debrief, state.resultRecorded) {
        if (debrief != null && !state.resultRecorded) {
            vm.markResultRecorded()
            onFinished(def.meta.caseId, GameRules.stars(debrief.scorePercent))
        }
    }

    if (showHelp) {
        HowToPlayScreen(onBack = { showHelp = false }, startSectionId = "actions")
        return
    }

    var confirmExit by remember { mutableStateOf(false) }
    BackHandler { if (debrief != null) onExit() else confirmExit = true }

    if (debrief != null) {
        DebriefScreen(def, debrief, onReplay = vm::replay, onExit = onExit)
        return
    }

    var tab by rememberSaveable { mutableStateOf(GameTab.TALK) }
    val st = CaseEngine.currentState(def, session)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ErSimBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("game_screen")
    ) {
        // ---------------- top bar
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            IconButton(onClick = { confirmExit = true }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Leave case", tint = ErSimTextPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(def.meta.title, color = ErSimTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(def.patient.setting.substringBefore("."), color = ErSimCyan, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Surface(
                color = ErSimRaisedSurface,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, (if (state.paused) ErSimAmber else ErSimMint).copy(alpha = 0.55f))
            ) {
                Text(
                    CaseEngine.formatClock(session.elapsedSec),
                    color = if (state.paused) ErSimAmber else ErSimMint,
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp).testTag("game_clock")
                )
            }
            IconButton(onClick = { showHelp = true }, modifier = Modifier.testTag("game_help")) {
                Icon(Icons.Default.HelpOutline, contentDescription = "How to play", tint = ErSimTextPrimary)
            }
            IconButton(onClick = vm::togglePause) {
                Icon(
                    if (state.paused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (state.paused) "Resume" else "Pause",
                    tint = if (state.paused) ErSimAmber else ErSimTextPrimary
                )
            }
            TextButton(onClick = vm::endCase) { Text("End", color = ErSimRed, fontWeight = FontWeight.Bold) }
        }

        // ---------------- monitor
        MonitorPanel(st.vitals, st.vitals.rhythm, def.patient.ageYears < 18, def.patient.ageYears.toDouble())

        // ---------------- nurse call-out
        AnimatedVisibility(visible = state.nurseMessage != null) {
            state.nurseMessage?.let { msg ->
                Surface(
                    color = ErSimAmber.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, ErSimAmber),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).testTag("nurse_callout")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp)) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = ErSimAmber, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Nurse: $msg", color = ErSimTextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f).padding(vertical = 8.dp))
                        IconButton(onClick = vm::dismissNurse) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = ErSimTextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // ---------------- patient
        PatientCard(def, session, st.appearance)

        // ---------------- tabs
        TabRow(
            selectedTabIndex = tab.ordinal,
            containerColor = ErSimHeaderBg,
            contentColor = ErSimMint,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            GameTab.entries.forEach { t ->
                val tabColor = when (t) {
                    GameTab.TALK -> ErSimSky
                    GameTab.EXAMINE -> ErSimViolet
                    GameTab.TESTS -> ErSimCyan
                    GameTab.TREAT -> ErSimRose
                    GameTab.LOG -> ErSimAmber
                }
                Tab(
                    selected = tab == t,
                    onClick = { tab = t },
                    icon = { Icon(t.icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text(t.label, fontSize = 11.sp) },
                    selectedContentColor = tabColor,
                    unselectedContentColor = ErSimTextMuted,
                    modifier = Modifier.testTag("game_tab_${t.name.lowercase()}")
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (tab) {
                GameTab.TALK -> TalkTab(def, session, vm::ask)
                GameTab.EXAMINE -> ExamineTab(session, vm::examine)
                GameTab.TESTS -> TestsTab(def, session, vm::order, vm::wait)
                GameTab.TREAT -> TreatTab(def, session, vm::perform)
                GameTab.LOG -> LogTab(session)
            }
        }

        // ---------------- action feedback
        state.feedback?.let { fb -> FeedbackBar(fb, vm::dismissFeedback) }
    }

    if (tutorialOpen) {
        GameTutorialDialog(onDone = onTutorialSeen)
    }

    if (confirmExit) {
        AlertDialog(
            onDismissRequest = { confirmExit = false },
            containerColor = ErSimSurface,
            title = { Text("Leave this case?", color = ErSimTextPrimary) },
            text = { Text("Progress in this case will be lost.", color = ErSimTextMuted) },
            confirmButton = {
                TextButton(onClick = { confirmExit = false; vm.exitGame(); onExit() }) {
                    Text("Leave", color = ErSimRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { confirmExit = false }) { Text("Stay", color = ErSimMint) } }
        )
    }
}

// ============================================================================
// MONITOR (with critical severity color differentiation)
// ============================================================================

@Composable
private fun MonitorPanel(v: com.example.engine.SimVitals, rhythm: String, isPediatric: Boolean, ageYears: Double) {
    val alarms = GameRules.alarms(v)
    val noPulse = VitalAlarm.NO_PULSE in alarms
    val hrStyle = ClinicalSeverityEngine.assessHeartRate(v.hr, isPediatric, onDarkMonitorWell = true, ageYears = ageYears)
    val bpStyle = ClinicalSeverityEngine.assessBloodPressure(v.sbp, v.dbp, isPediatric, onDarkMonitorWell = true, ageYears = ageYears)
    val spo2Style = ClinicalSeverityEngine.assessSpo2(if (noPulse) null else v.spo2, onDarkMonitorWell = true)
    val rrStyle = ClinicalSeverityEngine.assessRespRate(v.rr, isPediatric, onDarkMonitorWell = true, ageYears = ageYears)
    val tempStyle = ClinicalSeverityEngine.assessTemp(v.tempC.toFloat(), onDarkMonitorWell = true)
    val hasCritical = alarms.isNotEmpty() || hrStyle.isCritical || bpStyle.isCritical || spo2Style.isCritical || rrStyle.isCritical

    Surface(
        color = ErSimWaveWell,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (hasCritical) 1.6.dp else 1.dp, if (hasCritical) MonitorRed else ErSimBorder),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).testTag("game_monitor")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                VitalCell("HR", if (noPulse) "---" else "${v.hr}", "bpm", hrStyle, VitalAlarm.HR_LOW in alarms || VitalAlarm.HR_HIGH in alarms || noPulse, Modifier.weight(1f))
                VitalCell("BP", if (noPulse) "---" else v.bpDisplay, "mmHg", bpStyle, VitalAlarm.BP_LOW in alarms || noPulse, Modifier.weight(1.25f))
                VitalCell("SpO₂", if (noPulse) "---" else "${v.spo2}", "%", spo2Style, VitalAlarm.SPO2_LOW in alarms || noPulse, Modifier.weight(1f))
                VitalCell("RR", if (noPulse) "---" else "${v.rr}", "/min", rrStyle, VitalAlarm.RR_HIGH in alarms, Modifier.weight(0.9f))
                VitalCell("T", "%.1f".format(v.tempC), "°C", tempStyle, tempStyle.isCritical, Modifier.weight(0.95f))
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MonitorHeart,
                    contentDescription = null,
                    tint = if (hasCritical) MonitorRed else MonitorMint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(rhythm, color = MonitorTextLight, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (alarms.isNotEmpty()) {
                    Surface(color = MonitorRed, shape = RoundedCornerShape(5.dp)) {
                        Text(
                            alarms.joinToString(" • ") { it.label }.uppercase(),
                            color = Color(0xFF1B0308),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VitalCell(
    label: String,
    value: String,
    unit: String,
    style: ClinicalColorStyle,
    alarm: Boolean,
    modifier: Modifier = Modifier
) {
    val isCrit = alarm || style.isCritical
    val accent = if (isCrit) MonitorRed else style.accent
    val cellBg = when {
        isCrit -> Color(0xFF280B16)
        style.severity == ClinicalSeverity.HIGH -> Color(0xFF231B09)
        style.severity == ClinicalSeverity.LOW -> Color(0xFF0A2034)
        else -> Color(0xFF081926)
    }
    Surface(
        color = cellBg,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(if (isCrit) 1.4.dp else 0.8.dp, accent.copy(alpha = if (isCrit) 0.95f else 0.35f)),
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(label, color = MonitorTextMuted, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                if (style.isAbnormal || alarm) {
                    Text(
                        text = if (isCrit) "!" else style.severity.shortBadge.take(1),
                        color = accent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Text(value, color = accent, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace, maxLines = 1)
            Text(unit, color = MonitorTextMuted, fontSize = 8.5.sp)
        }
    }
}

// ============================================================================
// PATIENT
// ============================================================================

@Composable
private fun PatientCard(def: CaseDefinition, s: CaseSession, appearance: String) {
    val line = GameRules.lastPatientLine(s)
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ErSimCyan.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(40.dp).background(ErSimCyan.copy(alpha = 0.16f), CircleShape)
            ) {
                Text(def.patient.name.take(1), color = ErSimCyan, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${def.patient.name}, ${def.patient.ageYears} y • ${def.patient.weightKg.toInt()} kg",
                    color = ErSimTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold
                )
                Text(appearance, color = ErSimTextMuted, fontSize = 12.sp, lineHeight = 16.sp)
                if (line != null) {
                    Spacer(Modifier.height(6.dp))
                    Surface(color = ErSimRaisedSurface, shape = RoundedCornerShape(topStart = 2.dp, topEnd = 10.dp, bottomEnd = 10.dp, bottomStart = 10.dp)) {
                        Text(line, color = ErSimTextPrimary, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(8.dp).testTag("patient_speech"))
                    }
                }
            }
        }
    }
}

// ============================================================================
// TABS
// ============================================================================

@Composable
private fun TalkTab(def: CaseDefinition, s: CaseSession, onAsk: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { TabHint("Ask the patient. Answers appear in the speech bubble.") }
        items(def.history, key = { it.id }) { h ->
            val asked = "ask:${h.id}" in s.performed
            ActionCard(
                title = h.samplePrompts.firstOrNull() ?: h.topic,
                subtitle = if (asked) "\"${h.answer}\"" else h.topic,
                done = asked,
                buttonLabel = if (asked) "Ask again" else "Ask",
                accentColor = ErSimSky,
                onClick = { onAsk(h.id) },
                tag = "ask_${h.id}"
            )
        }
    }
}

@Composable
private fun ExamineTab(s: CaseSession, onExamine: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { TabHint("Re-examine after treatment: findings change with the patient.") }
        items(REGION_LABELS.keys.filter { it in CaseParser.EXAM_REGIONS }.toList(), key = { it }) { region ->
            val finding = GameRules.lastExamFinding(s, region)
            ActionCard(
                title = REGION_LABELS.getValue(region),
                subtitle = finding ?: "Not examined yet",
                done = finding != null,
                buttonLabel = if (finding != null) "Re-examine" else "Examine",
                accentColor = ErSimViolet,
                onClick = { onExamine(region) },
                tag = "exam_${region.lowercase()}"
            )
        }
    }
}

@Composable
private fun TestsTab(def: CaseDefinition, s: CaseSession, onOrder: (String) -> Unit, onWait: (Int) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TabHint("Results take time. Waiting moves the clock — the patient keeps changing.", Modifier.weight(1f))
                OutlinedButton(
                    onClick = { onWait(60) },
                    border = BorderStroke(1.dp, ErSimAmber),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErSimAmber),
                    modifier = Modifier.testTag("wait_1min")
                ) { Text("Wait 1 min", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            }
        }
        items(def.investigations, key = { it.id }) { inv ->
            val wait = GameRules.secondsUntilResult(s, inv.id)
            val result = s.results[inv.id]
            val sub = when {
                wait != null -> "Pending… ready in ${CaseEngine.formatClock(wait)}"
                result != null -> result
                else -> "Turnaround ${CaseEngine.formatClock(inv.turnaroundSec)}"
            }
            ActionCard(
                title = inv.name,
                subtitle = sub,
                done = result != null,
                buttonLabel = if (result != null) "Repeat" else "Order",
                accentColor = ErSimCyan,
                enabled = wait == null,
                onClick = { onOrder(inv.id) },
                tag = "order_${inv.id}"
            )
        }
    }
}

@Composable
private fun TreatTab(def: CaseDefinition, s: CaseSession, onPerform: (String) -> Unit) {
    val groups = remember(def) { def.interventions.groupBy { it.category } }
    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { TabHint("Every order is checked against the patient. Think before you give.") }
        groups.forEach { (category, list) ->
            // Red and amber stay reserved for danger/warning, so categories use the other hues.
            // Keys match the "category" values used in case JSON.
            val catColor = when (category.uppercase()) {
                "RESUSCITATION", "PROCEDURE" -> ErSimOrange
                "FLUIDS", "FLUID", "AIRWAY", "OXYGEN", "SUPPORT" -> ErSimSky
                "DISPOSITION", "CONSULT", "COMMUNICATION" -> ErSimViolet
                "FIBRINOLYTIC", "ANTIPLATELET", "ANTICOAGULANT", "ANTI-ISCHAEMIC", "ANALGESIA",
                "ANTIARRHYTHMIC", "DRUG", "MEDICATION", "MEDS" -> ErSimRose
                else -> ErSimMint
            }
            item(key = "h_$category") {
                Text(category.replace('_', ' '), color = catColor, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, modifier = Modifier.padding(top = 6.dp))
            }
            items(list, key = { it.id }) { a ->
                val givenAt = s.performed[a.id]
                val count = s.performedCount[a.id] ?: 0
                val doseLine = listOfNotNull(a.dose, a.route).joinToString(" • ")
                ActionCard(
                    title = a.name,
                    subtitle = buildString {
                        if (doseLine.isNotEmpty()) append(doseLine)
                        if (givenAt != null) {
                            if (isNotEmpty()) append("\n")
                            append("Given at ${CaseEngine.formatClock(givenAt)}")
                            if (count > 1) append(" (×$count)")
                        }
                    },
                    done = givenAt != null,
                    buttonLabel = if (givenAt != null && a.repeatable) "Again" else "Give",
                    accentColor = catColor,
                    enabled = givenAt == null || a.repeatable,
                    onClick = { onPerform(a.id) },
                    tag = "give_${a.id}"
                )
            }
        }
    }
}

@Composable
private fun LogTab(s: CaseSession) {
    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(s.log.asReversed()) { e -> LogRow(e) }
    }
}

@Composable
private fun LogRow(e: LogEntry) {
    val (color, label) = logStyle(e.kind)
    Row(verticalAlignment = Alignment.Top) {
        Text(CaseEngine.formatClock(e.atSec), color = ErSimTextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(44.dp))
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(62.dp))
        Text(e.text, color = ErSimTextPrimary, fontSize = 12.sp, lineHeight = 16.sp, modifier = Modifier.weight(1f))
    }
}

private fun logStyle(k: LogKind): Pair<Color, String> = when (k) {
    LogKind.HARM -> ErSimRed to "HARM"
    LogKind.REJECTED -> ErSimAmber to "BLOCKED"
    LogKind.NOTE -> ErSimAmber to "NOTE"
    LogKind.STATE_CHANGE -> ErSimRed to "CHANGE"
    LogKind.RESULT -> ErSimCyan to "RESULT"
    LogKind.ORDERED -> ErSimSky to "ORDER"
    LogKind.ACTION -> ErSimMint to "GIVEN"
    LogKind.HISTORY -> ErSimTextPrimary to "PATIENT"
    LogKind.EXAM -> ErSimViolet to "EXAM"
    LogKind.END -> ErSimMint to "END"
    LogKind.INFO -> ErSimTextMuted to "INFO"
}

// ============================================================================
// SHARED PIECES
// ============================================================================

@Composable
private fun TabHint(text: String, modifier: Modifier = Modifier) {
    Text(text, color = ErSimTextMuted, fontSize = 11.sp, modifier = modifier.padding(bottom = 2.dp))
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    done: Boolean,
    buttonLabel: String,
    onClick: () -> Unit,
    tag: String,
    accentColor: Color = ErSimMint,
    enabled: Boolean = true
) {
    Surface(
        color = if (done) accentColor.copy(alpha = 0.08f) else ErSimSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (done) accentColor.copy(alpha = 0.6f) else ErSimBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = ErSimTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, color = if (done) ErSimTextPrimary else ErSimTextMuted, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor, contentColor = Color(0xFF06151C),
                    disabledContainerColor = ErSimRaisedSurface, disabledContentColor = ErSimTextMuted
                ),
                modifier = Modifier.heightIn(min = 44.dp).testTag(tag)
            ) { Text(buttonLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun FeedbackBar(e: LogEntry, onDismiss: () -> Unit) {
    val (color, label) = logStyle(e.kind)
    Surface(
        color = if (e.kind == LogKind.HARM) Color(0xFF2B0A14) else ErSimHeaderBg,
        border = BorderStroke(1.5.dp, color),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        modifier = Modifier.fillMaxWidth().testTag("game_feedback")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 12.dp)) {
            Surface(color = color.copy(alpha = 0.20f), shape = RoundedCornerShape(5.dp)) {
                Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(e.text, color = ErSimTextPrimary, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f).padding(vertical = 8.dp))
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = ErSimTextMuted, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ============================================================================
// DEBRIEF
// ============================================================================

@Composable
private fun DebriefScreen(def: CaseDefinition, d: DebriefReport, onReplay: () -> Unit, onExit: () -> Unit) {
    val stars = GameRules.stars(d.scorePercent)
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(ErSimBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("debrief_screen")
    ) {
        Text("DEBRIEF", color = ErSimCyan, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp)
        Text(def.meta.title, color = ErSimTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

        Surface(color = ErSimSurface, shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, if (stars >= 2) ErSimMint else ErSimAmber), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    repeat(3) { i ->
                        Icon(
                            if (i < stars) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (i < stars) ErSimAmber else ErSimTextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Text("${d.scorePercent}%", color = if (d.scorePercent >= 70) ErSimMint else if (d.scorePercent >= 40) ErSimAmber else ErSimRed, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.semanticsLabel("$stars of 3 stars, score ${d.scorePercent} percent"))
                Text("Time ${CaseEngine.formatClock(d.elapsedSec)}", color = ErSimTextMuted, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Text(d.outcome, color = ErSimTextPrimary, fontSize = 14.sp, lineHeight = 19.sp)
            }
        }

        DebriefSection("Diagnosis", ErSimCyan) {
            Text(d.diagnosis, color = ErSimTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(d.summary, color = ErSimTextMuted, fontSize = 13.sp, lineHeight = 18.sp)
        }

        DebriefSection("Critical actions", ErSimMint) {
            d.criticalActions.forEach { r ->
                val (icon, tint, status) = when {
                    r.met -> Triple(Icons.Default.CheckCircle, ErSimMint, r.doneAtSec?.let { "Done at ${CaseEngine.formatClock(it)}" } ?: "Avoided")
                    r.late -> Triple(Icons.Default.Schedule, ErSimAmber, "Late — at ${CaseEngine.formatClock(r.doneAtSec ?: 0)}")
                    else -> Triple(Icons.Default.Cancel, ErSimRed, if (r.def.avoid) "Done — should have been avoided" else "Missed")
                }
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(r.def.label, color = ErSimTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(status, color = tint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        if (!r.met) Text(r.def.teaching, color = ErSimTextMuted, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                    Text("${r.def.points}", color = ErSimTextMuted, fontSize = 11.sp)
                }
            }
        }

        if (d.harms.isNotEmpty()) {
            DebriefSection("Harm events", ErSimRed) {
                d.harms.forEach { h ->
                    Text("${CaseEngine.formatClock(h.atSec)}  ${h.text}", color = ErSimRed, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }

        DebriefSection("Disposition", ErSimViolet) {
            Text(d.disposition, color = ErSimTextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
        }

        DebriefSection("Teaching points", ErSimAmber) {
            d.teachingPoints.forEach { t ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("•", color = ErSimAmber, fontSize = 13.sp, modifier = Modifier.width(14.dp))
                    Text(t, color = ErSimTextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }

        Text(
            "Guideline basis: ${def.meta.guidelineBasis}. Review status: ${def.meta.reviewStatus}.",
            color = ErSimTextMuted, fontSize = 11.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onExit,
                border = BorderStroke(1.dp, ErSimBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErSimTextPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(50.dp).testTag("debrief_home")
            ) { Text("Home", fontWeight = FontWeight.Bold) }
            Button(
                onClick = onReplay,
                colors = ButtonDefaults.buttonColors(containerColor = ErSimMint, contentColor = ErSimBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(50.dp).testTag("debrief_replay")
            ) {
                Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Replay", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DebriefSection(title: String, accent: Color = ErSimMint, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = ErSimSurface, shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, accent.copy(alpha = 0.40f)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title.uppercase(), color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}

private fun Modifier.semanticsLabel(label: String): Modifier =
    this.then(Modifier.semantics(mergeDescendants = true) { contentDescription = label })
