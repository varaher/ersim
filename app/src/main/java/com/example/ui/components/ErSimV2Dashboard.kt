package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SideDrawerTab
import com.example.ui.SimUiState
import com.example.ui.TabletTab
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin

enum class ErSimWorkspaceTab(val title: String) {
    PATIENT_OVERVIEW("Patient Overview"),
    HISTORY("History"),
    XABCDE("XABCDE"),
    EXAMINATION("Examination"),
    INVESTIGATIONS("Investigations"),
    TREATMENT("Treatment")
}

@Composable
fun ErSimV2Screen(
    uiState: SimUiState,
    onToggleTimer: () -> Unit,
    onRestartCase: () -> Unit,
    onEndCase: () -> Unit,
    onOpenScenarioPicker: () -> Unit,
    onOpenAdminEngine: () -> Unit,
    onOpenFullScreenTablet: (TabletTab?) -> Unit,
    onOpenFullScreenMap: () -> Unit,
    onOpenCrashCart: (Int?) -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onWalkToQuestionOnMap: (QuestionGateType) -> Unit,
    onSelectSideDrawerTab: (SideDrawerTab?) -> Unit,
    onPerformExamStep: (String) -> Unit,
    onToggleCriticalAction: (String) -> Unit,
    onSelectActiveTeamMember: (String) -> Unit,
    onCameraDrag: (Float, Float) -> Unit,
    onStepCameraYaw: (Float) -> Unit,
    onJoystickMove: (Float, Float) -> Unit,
    onToggleAudio: () -> Unit,
    onToggleAlarmSilence: () -> Unit,
    onToggleJoystick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedWorkspaceTab by remember { mutableStateOf(ErSimWorkspaceTab.PATIENT_OVERVIEW) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ErSimBg)
    ) {
        // 1. TOP HEADER BAR (ER SIM Branding, Bay & Scenario Indicator, Timer, Pause/Restart/End Case)
        ErSimTopHeaderBar(
            uiState = uiState,
            onToggleTimer = onToggleTimer,
            onRestartCase = onRestartCase,
            onEndCase = onEndCase,
            onOpenScenarioPicker = onOpenScenarioPicker,
            onOpenAdminEngine = onOpenAdminEngine,
            onOpenFullScreenTablet = { onOpenFullScreenTablet(null) },
            onOpenFullScreenMap = onOpenFullScreenMap,
            onToggleAudio = onToggleAudio,
            onToggleAlarmSilence = onToggleAlarmSilence,
            onToggleJoystick = onToggleJoystick
        )

        // 2. ADAPTIVE PORTRAIT / LANDSCAPE BODY
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val isWideScreen = maxWidth >= 840.dp

            if (isWideScreen) {
                // Landscape / Tablet 3-Column Adaptive Layout
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(12.dp)
                ) {
                    // Left Column: Patient Overview, Case Selector & Progress
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .width(260.dp)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ErSimPatientOverviewSidebarCard(
                            uiState = uiState,
                            onOpenScenarioPicker = onOpenScenarioPicker,
                            onOpenQuestionGate = onOpenQuestionGate,
                            onSelectActiveTeamMember = onSelectActiveTeamMember
                        )
                    }

                    // Center Column: Vitals + Prominent ResusBay3DViewport + Workspace Tabs
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ContinuousPatientMonitoringCard(
                            uiState = uiState,
                            onOpenQuestionGate = onOpenQuestionGate
                        )
                        ProminentResusBay3DCard(
                            uiState = uiState,
                            bayHeightDp = 360,
                            onCameraDrag = onCameraDrag,
                            onStepCameraYaw = onStepCameraYaw,
                            onJoystickMove = onJoystickMove,
                            onOpenQuestionGate = onOpenQuestionGate,
                            onWalkToQuestionOnMap = onWalkToQuestionOnMap,
                            onOpenFullScreenMap = onOpenFullScreenMap,
                            onOpenCrashCart = { onOpenCrashCart(null) },
                            onOpenEquipmentStabilize = { onSelectSideDrawerTab(SideDrawerTab.STABILIZE) },
                            onOpenFullScreenTablet = { onOpenFullScreenTablet(null) },
                            onPerformExamStep = onPerformExamStep
                        )
                        ClinicalWorkspaceTabsCard(
                            uiState = uiState,
                            selectedTab = selectedWorkspaceTab,
                            onSelectTab = { selectedWorkspaceTab = it },
                            onOpenScenarioPicker = onOpenScenarioPicker,
                            onOpenQuestionGate = onOpenQuestionGate,
                            onPerformExamStep = onPerformExamStep,
                            onToggleCriticalAction = onToggleCriticalAction,
                            onSelectActiveTeamMember = onSelectActiveTeamMember,
                            onSelectSideDrawerTab = onSelectSideDrawerTab,
                            onOpenFullScreenTablet = onOpenFullScreenTablet
                        )
                    }

                    // Right Column: 2-Column Clinical Action Console & Real Clinical Timeline
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .width(300.dp)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ClinicalActionConsoleCard(
                            uiState = uiState,
                            onSelectWorkspaceTab = { selectedWorkspaceTab = it },
                            onOpenQuestionGate = onOpenQuestionGate,
                            onOpenCrashCart = onOpenCrashCart,
                            onSelectSideDrawerTab = onSelectSideDrawerTab,
                            onOpenFullScreenTablet = onOpenFullScreenTablet
                        )
                        ClinicalTimelineCard(uiState = uiState)
                    }
                }
            } else {
                // Portrait-First Native Android Vertical Scroll Layout
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    // 1. Above Scene: Native Vitals Cards + Live ECG Waveform (strictly Q0-gated)
                    ContinuousPatientMonitoringCard(
                        uiState = uiState,
                        onOpenQuestionGate = onOpenQuestionGate
                    )

                    // 2. Main: Prominently Displayed ResusBay3DViewport with All Hotspots & Mini-Map
                    ProminentResusBay3DCard(
                        uiState = uiState,
                        bayHeightDp = 340,
                        onCameraDrag = onCameraDrag,
                        onStepCameraYaw = onStepCameraYaw,
                        onJoystickMove = onJoystickMove,
                        onOpenQuestionGate = onOpenQuestionGate,
                        onWalkToQuestionOnMap = onWalkToQuestionOnMap,
                        onOpenFullScreenMap = onOpenFullScreenMap,
                        onOpenCrashCart = { onOpenCrashCart(null) },
                        onOpenEquipmentStabilize = { onSelectSideDrawerTab(SideDrawerTab.STABILIZE) },
                        onOpenFullScreenTablet = { onOpenFullScreenTablet(null) },
                        onPerformExamStep = onPerformExamStep
                    )

                    // 3. Below Scene: Patient Overview, History, XABCDE, Examination, Investigations & Treatment Tabs
                    ClinicalWorkspaceTabsCard(
                        uiState = uiState,
                        selectedTab = selectedWorkspaceTab,
                        onSelectTab = { selectedWorkspaceTab = it },
                        onOpenScenarioPicker = onOpenScenarioPicker,
                        onOpenQuestionGate = onOpenQuestionGate,
                        onPerformExamStep = onPerformExamStep,
                        onToggleCriticalAction = onToggleCriticalAction,
                        onSelectActiveTeamMember = onSelectActiveTeamMember,
                        onSelectSideDrawerTab = onSelectSideDrawerTab,
                        onOpenFullScreenTablet = onOpenFullScreenTablet
                    )

                    // 4. Bottom: 2-Column Clinical Action Console
                    ClinicalActionConsoleCard(
                        uiState = uiState,
                        onSelectWorkspaceTab = { selectedWorkspaceTab = it },
                        onOpenQuestionGate = onOpenQuestionGate,
                        onOpenCrashCart = onOpenCrashCart,
                        onSelectSideDrawerTab = onSelectSideDrawerTab,
                        onOpenFullScreenTablet = onOpenFullScreenTablet
                    )

                    // 5. Real Clinical Timeline (from room.chatMessages)
                    ClinicalTimelineCard(uiState = uiState)
                }
            }
        }
    }
}

// ============================================================================
// 1. TOP HEADER BAR
// ============================================================================
@Composable
private fun ErSimTopHeaderBar(
    uiState: SimUiState,
    onToggleTimer: () -> Unit,
    onRestartCase: () -> Unit,
    onEndCase: () -> Unit,
    onOpenScenarioPicker: () -> Unit,
    onOpenAdminEngine: () -> Unit,
    onOpenFullScreenTablet: () -> Unit,
    onOpenFullScreenMap: () -> Unit,
    onToggleAudio: () -> Unit,
    onToggleAlarmSilence: () -> Unit,
    onToggleJoystick: () -> Unit
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase

    Surface(
        color = ErSimHeaderBg,
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Top Row: ER SIM Branding + Bay/Scenario Indicator + Utility Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = ErSimTextPrimary, fontWeight = FontWeight.Black)) {
                                append("ER ")
                            }
                            withStyle(SpanStyle(color = ErSimMint, fontWeight = FontWeight.Black)) {
                                append("SIM")
                            }
                        },
                        fontSize = 22.sp,
                        letterSpacing = 1.8.sp
                    )
                    Text(
                        text = "IMMERSIVE EMERGENCY MEDICINE",
                        color = ErSimTextMuted,
                        fontSize = 10.sp,
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Bay & Scenario Indicator Chip (Tappable to open 20 Oral Board Scenarios)
                Surface(
                    color = ErSimRaisedSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ErSimBorder),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(horizontal = 8.dp)
                        .testTag("open_20_scenarios_btn")
                        .clickable { onOpenScenarioPicker() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = "Select Scenario",
                            tint = ErSimMint,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "${room.roomId} • Case #${activeCase.caseNumber}",
                                color = ErSimMint,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = activeCase.scenarioCategory,
                                color = ErSimTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = ErSimTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Quick Utility Icons (Admin Engine, Tablet, Audio)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onOpenAdminEngine,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("open_instructor_admin_btn")
                    ) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Instructor Question Engine & Admin Room",
                            tint = ErSimAmber
                        )
                    }
                    IconButton(
                        onClick = onOpenFullScreenTablet,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("hud_fullscreen_tablet_btn")
                    ) {
                        Icon(
                            Icons.Default.TabletMac,
                            contentDescription = "Full-Screen Virtual Tablet",
                            tint = ErSimMint
                        )
                    }
                    IconButton(
                        onClick = onToggleAudio,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("hud_audio_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (uiState.isAudioEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Resus Bay Audio",
                            tint = if (uiState.isAudioEnabled) ErSimMint else ErSimTextMuted
                        )
                    }
                }
            }

            // Second Row: Clinical Time + Pause/Resume + Restart + End Case + Map/Joystick/Alarm Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Live Room Timer (Single Source of Truth from RoomTeamSession)
                    Surface(
                        color = ErSimVitalWell,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ErSimBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Clinical Timer",
                                tint = ErSimMint,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = room.formattedTimer,
                                color = ErSimTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                modifier = Modifier.testTag("header_clinical_timer")
                            )
                        }
                    }

                    // Pause / Resume Button
                    ErSimHeaderActionButton(
                        label = if (room.isTimerRunning) "Pause" else "Resume",
                        isPrimary = !room.isTimerRunning,
                        onClick = onToggleTimer,
                        testTag = "header_pause_resume_btn"
                    )

                    // Restart Button
                    ErSimHeaderActionButton(
                        label = "Restart",
                        onClick = onRestartCase,
                        testTag = "header_restart_btn"
                    )

                    // End Case Button
                    ErSimHeaderActionButton(
                        label = "End Case",
                        textColor = ErSimRed,
                        onClick = onEndCase,
                        testTag = "header_end_case_btn"
                    )
                }

                Spacer(Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ErSimHeaderActionButton(
                        label = if (uiState.showJoystickControl) "Joystick ON" else "3D Joystick",
                        textColor = if (uiState.showJoystickControl) ErSimMint else ErSimTextPrimary,
                        onClick = onToggleJoystick,
                        testTag = "header_joystick_btn"
                    )
                    if (uiState.isAudioEnabled) {
                        ErSimHeaderActionButton(
                            label = if (uiState.isAlarmSilenced) "Alarm Muted" else "Mute Alarm",
                            textColor = if (uiState.isAlarmSilenced) ErSimTextMuted else ErSimAmber,
                            onClick = onToggleAlarmSilence,
                            testTag = "hud_alarm_silence_btn"
                        )
                    }
                    ErSimHeaderActionButton(
                        label = "ER Map",
                        textColor = ErSimMint,
                        onClick = onOpenFullScreenMap,
                        testTag = "header_er_map_btn"
                    )
                }
            }
        }
    }
}

@Composable
private fun ErSimHeaderActionButton(
    label: String,
    isPrimary: Boolean = false,
    textColor: Color = ErSimTextPrimary,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = if (isPrimary) ErSimMint else ErSimRaisedSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (isPrimary) ErSimMint else ErSimBorder),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
        ) {
            Text(
                text = label,
                color = if (isPrimary) Color(0xFF06211B) else textColor,
                fontWeight = if (isPrimary) FontWeight.ExtraBold else FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
}

// ============================================================================
// 2. CONTINUOUS PATIENT MONITORING (NATIVE VITALS + REUSED ECG WAVEFORM)
// ============================================================================
@Composable
fun ContinuousPatientMonitoringCard(
    uiState: SimUiState,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    modifier: Modifier = Modifier
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val vitals = uiState.currentVitals
    val q0Unlocked = room.unlockedGates.contains(QuestionGateType.Q0_GATE)

    ErSimPanelContainer(
        title = "CONTINUOUS PATIENT MONITORING",
        trailingHeader = {
            if (!q0Unlocked) {
                Surface(
                    color = ErSimAmber.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ErSimAmber),
                    modifier = Modifier.clickable { onOpenQuestionGate(QuestionGateType.Q0_GATE) }
                ) {
                    Text(
                        text = "LOCKED • TAP TO CRACK Q0",
                        color = ErSimAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Text(
                    text = "${vitals.rhythmLabel} • T ${vitals.tempCelsius}°C",
                    color = ErSimMint,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        modifier = modifier
    ) {
        // 4 Vitals Grid (2x2 on Portrait, 4-across on wide)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val isFourCol = maxWidth >= 520.dp
            val hrText = if (q0Unlocked) "${vitals.hr}" else "LOCKED"
            val bpText = if (q0Unlocked) vitals.bpDisplay else "LOCKED"
            val spo2Text = if (q0Unlocked) (vitals.spo2?.let { "$it" } ?: "None") else "LOCKED"
            val rrText = if (q0Unlocked) "${vitals.rr}" else "LOCKED"

            if (isFourCol) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ErSimVitalTile("HEART RATE", hrText, "bpm", ErSimMint, Modifier.weight(1f))
                    ErSimVitalTile("BLOOD PRESSURE", bpText, "mmHg", ErSimRed, Modifier.weight(1f))
                    ErSimVitalTile("OXYGEN SATURATION", spo2Text, "%", ErSimAmber, Modifier.weight(1f))
                    ErSimVitalTile("RESPIRATORY RATE", rrText, "/min", ErSimTextPrimary, Modifier.weight(1f))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ErSimVitalTile("HEART RATE", hrText, "bpm", ErSimMint, Modifier.weight(1f))
                        ErSimVitalTile("BLOOD PRESSURE", bpText, "mmHg", ErSimRed, Modifier.weight(1f))
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ErSimVitalTile("OXYGEN SATURATION", spo2Text, "%", ErSimAmber, Modifier.weight(1f))
                        ErSimVitalTile("RESPIRATORY RATE", rrText, "/min", ErSimTextPrimary, Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Live Scrolling Monitor ECG Waveform Strip
        ReusableMonitorEcgWaveStrip(
            q0Unlocked = q0Unlocked,
            caseNumber = activeCase.caseNumber,
            isStabilized = room.isPatientStabilized,
            rhythmLabel = if (q0Unlocked) "LIVE TELEMETRY ECG — ${vitals.rhythmLabel.uppercase()}"
            else "TELEMETRY LOCKED — CRACK INITIAL QUESTION (Q0) TO UNLOCK VITALS & TABLET"
        )
    }
}

@Composable
private fun ErSimVitalTile(
    label: String,
    value: String,
    unit: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ErSimVitalWell,
        shape = RoundedCornerShape(11.dp),
        border = BorderStroke(1.dp, ErSimBorder.copy(alpha = 0.65f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = label,
                color = ErSimTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = if (value == "LOCKED") 17.sp else 26.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(vertical = 3.dp)
            )
            Text(
                text = unit,
                color = ErSimTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ReusableMonitorEcgWaveStrip(
    q0Unlocked: Boolean,
    caseNumber: Int,
    isStabilized: Boolean,
    rhythmLabel: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ersim_ecg_wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ersim_ecg_phase"
    )

    Surface(
        color = ErSimWaveWell,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ErSimBorder.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                val w = size.width
                val h = size.height
                val path = Path()
                val steps = 110
                for (i in 0..steps) {
                    val frac = i.toFloat() / steps
                    val px = frac * w
                    val t = (frac * 3.5f + phase * 3.5f) % 1.0f
                    val yOff = when {
                        !q0Unlocked -> 0f
                        caseNumber == 10 && !isStabilized -> sin(t * 2f * PI.toFloat()) * (h * 0.38f)
                        caseNumber == 17 && !isStabilized -> sin(t * 5f * PI.toFloat()) * (h * 0.40f) * sin(frac * PI.toFloat() * 2f)
                        t in 0.12f..0.20f -> -sin((t - 0.12f) / 0.08f * PI.toFloat()) * (h * 0.10f)
                        t in 0.28f..0.31f -> (h * 0.12f)
                        t in 0.31f..0.36f -> -(h * 0.42f)
                        t in 0.36f..0.41f -> (h * 0.22f)
                        t in 0.55f..0.70f -> -sin((t - 0.55f) / 0.15f * PI.toFloat()) * (h * 0.16f)
                        else -> 0f
                    }
                    val py = h * 0.58f + yOff
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                drawPath(
                    path = path,
                    color = if (q0Unlocked) ErSimMint else ErSimTextMuted,
                    style = Stroke(width = 2.6f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = rhythmLabel,
                color = if (q0Unlocked) ErSimMint else ErSimAmber,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}

// ============================================================================
// 3. PROMINENT RESUS BAY 3D VIEWPORT CARD (PRESERVING ALL HOTSPOTS & CONTROLS)
// ============================================================================
@Composable
private fun ProminentResusBay3DCard(
    uiState: SimUiState,
    bayHeightDp: Int,
    onCameraDrag: (Float, Float) -> Unit,
    onStepCameraYaw: (Float) -> Unit,
    onJoystickMove: (Float, Float) -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onWalkToQuestionOnMap: (QuestionGateType) -> Unit,
    onOpenFullScreenMap: () -> Unit,
    onOpenCrashCart: () -> Unit,
    onOpenEquipmentStabilize: () -> Unit,
    onOpenFullScreenTablet: () -> Unit,
    onPerformExamStep: (String) -> Unit = {}
) {
    val room = uiState.activeRoom
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bayHeightDp.dp)
                .clip(RoundedCornerShape(15.dp))
        ) {
            // Existing ResusBay3DViewport with real patient artwork, equipment hotspots & Q0-Q3 beacons
            ResusBay3DViewport(
                uiState = uiState,
                onCameraDrag = onCameraDrag,
                onJoystickMove = onJoystickMove,
                onOpenQuestionGate = onOpenQuestionGate,
                onOpenCrashCart = onOpenCrashCart,
                onOpenEquipmentStabilize = onOpenEquipmentStabilize,
                modifier = Modifier.fillMaxSize(),
                onPerformExamStep = onPerformExamStep
            )

            // Top-Left Bay Badge + Quick Question Gate Pills (Q0, Q1, Q2, Q3)
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Surface(
                    color = ErSimHeaderBg.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ErSimBorder)
                ) {
                    Text(
                        text = "${room.roomId} • SWIPE 3D OR TAP BEACONS",
                        color = ErSimMint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    QuestionGateType.entries.forEach { gate ->
                        val unlocked = room.unlockedGates.contains(gate)
                        Surface(
                            color = if (unlocked) ErSimMint else ErSimRaisedSurface.copy(alpha = 0.94f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (unlocked) ErSimMint else ErSimAmber),
                            modifier = Modifier
                                .testTag("quick_gate_${gate.code.lowercase()}")
                                .clickable { onOpenQuestionGate(gate) }
                        ) {
                            Text(
                                text = if (unlocked) "${gate.code} ✓" else "${gate.code} ?",
                                color = if (unlocked) Color(0xFF06211B) else ErSimAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Top-Right Compact ER Mini-Map (with Full-Screen Expand Button)
            ErTopRightMiniMap(
                uiState = uiState,
                onExpandFullScreen = onOpenFullScreenMap,
                onSelectQuestionOnMap = onWalkToQuestionOnMap,
                onOpenCrashCart = onOpenCrashCart,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )

            // Bottom Overlay Bar: Camera Pan < > + Hint + Virtual Tablet Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xAA07111B))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = ErSimRaisedSurface,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, ErSimBorder),
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("dock_pan_left_btn")
                            .clickable { onStepCameraYaw(-18f) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Pan Left", tint = ErSimMint)
                        }
                    }
                    Surface(
                        color = ErSimRaisedSurface,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, ErSimBorder),
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("dock_pan_right_btn")
                            .clickable { onStepCameraYaw(18f) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Pan Right", tint = ErSimMint)
                        }
                    }
                }

                Text(
                    text = "Tap Monitor, Ventilator, Crash Cart or Q0–Q3",
                    color = ErSimTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )

                Surface(
                    color = ErSimMint,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .testTag("open_virtual_tablet_btn")
                        .clickable { onOpenFullScreenTablet() }
                ) {
                    Text(
                        text = "Virtual Tablet",
                        color = Color(0xFF06211B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 4. LEFT SIDEBAR / PATIENT OVERVIEW CARD (For Wide Layouts & Patient Overview Tab)
// ============================================================================
@Composable
private fun ErSimPatientOverviewSidebarCard(
    uiState: SimUiState,
    onOpenScenarioPicker: () -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onSelectActiveTeamMember: (String) -> Unit
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val q0Unlocked = room.unlockedGates.contains(QuestionGateType.Q0_GATE)

    ErSimPanelContainer(title = "PATIENT OVERVIEW") {
        if (!q0Unlocked) {
            ErSimGateLockedNotice(
                gateType = QuestionGateType.Q0_GATE,
                message = "Crack the Instructor's Initial Question (Q0) at the Resuscitation Bay entrance to unlock Patient Details, Initial Vitals, and Virtual Tablet access.",
                onCrackClick = { onOpenQuestionGate(QuestionGateType.Q0_GATE) }
            )
        } else {
            Text(
                text = activeCase.patientName,
                color = ErSimTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "${activeCase.ageDesc} • ${activeCase.weightKg} kg",
                color = ErSimMint,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            val patientProfile = PatientCharacterRegistry.profileFor(activeCase)
            Text(
                text = "${patientProfile.regionState} • ${patientProfile.primaryLanguage} • History from: ${patientProfile.historian.label}",
                color = ErSimTextMuted,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = activeCase.chiefComplaint,
                color = ErSimTextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = ErSimBorder)
        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Triage", color = ErSimTextMuted, fontSize = 12.sp)
            Text(
                text = if (room.isPatientStabilized) "STABILIZED" else "RED",
                color = if (room.isPatientStabilized) ErSimMint else ErSimRed,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Location", color = ErSimTextMuted, fontSize = 12.sp)
            Text(room.roomId, color = ErSimTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Progress", color = ErSimTextMuted, fontSize = 12.sp)
            Text(
                text = "${room.tasksCompletedCount} / ${activeCase.criticalActions.size} Actions",
                color = ErSimMint,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(10.dp))
        Text("Active Team Role:", color = ErSimTextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            room.members.forEach { member ->
                val selected = member == room.activeMember
                Surface(
                    color = if (selected) ErSimMint else ErSimRaisedSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (selected) ErSimMint else ErSimBorder),
                    modifier = Modifier.clickable { onSelectActiveTeamMember(member) }
                ) {
                    Text(
                        text = member,
                        color = if (selected) Color(0xFF06211B) else ErSimTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Surface(
            color = ErSimRaisedSurface,
            shape = RoundedCornerShape(9.dp),
            border = BorderStroke(1.dp, ErSimBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenScenarioPicker() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Case #${activeCase.caseNumber} — ${activeCase.scenarioCategory}",
                    color = ErSimTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.UnfoldMore, contentDescription = null, tint = ErSimMint, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ============================================================================
// 5. BELOW SCENE: CLINICAL WORKSPACE TABS (Patient Overview, History, XABCDE, Examination, Investigations, Treatment)
// ============================================================================
@Composable
private fun ClinicalWorkspaceTabsCard(
    uiState: SimUiState,
    selectedTab: ErSimWorkspaceTab,
    onSelectTab: (ErSimWorkspaceTab) -> Unit,
    onOpenScenarioPicker: () -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onPerformExamStep: (String) -> Unit,
    onToggleCriticalAction: (String) -> Unit,
    onSelectActiveTeamMember: (String) -> Unit,
    onSelectSideDrawerTab: (SideDrawerTab?) -> Unit,
    onOpenFullScreenTablet: (TabletTab?) -> Unit
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val q0Unlocked = room.unlockedGates.contains(QuestionGateType.Q0_GATE)
    val q1Unlocked = room.unlockedGates.contains(QuestionGateType.Q1_SURVEY)
    val q2Unlocked = room.unlockedGates.contains(QuestionGateType.Q2_LABS)
    val q3Unlocked = room.unlockedGates.contains(QuestionGateType.Q3_IMAGING)

    ErSimPanelContainer(title = "CLINICAL CHART & DIAGNOSTIC WORKSPACE") {
        // Horizontal Scrollable Tab Pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            ErSimWorkspaceTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val isGateUnlocked = when (tab) {
                    ErSimWorkspaceTab.PATIENT_OVERVIEW,
                    ErSimWorkspaceTab.HISTORY,
                    ErSimWorkspaceTab.TREATMENT -> q0Unlocked
                    ErSimWorkspaceTab.XABCDE,
                    ErSimWorkspaceTab.EXAMINATION -> q1Unlocked
                    ErSimWorkspaceTab.INVESTIGATIONS -> q2Unlocked || q3Unlocked
                }

                Surface(
                    color = if (isSelected) ErSimMint else ErSimRaisedSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        when {
                            isSelected -> ErSimMint
                            isGateUnlocked -> ErSimBorder
                            else -> ErSimAmber.copy(alpha = 0.7f)
                        }
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("workspace_tab_${tab.name.lowercase()}")
                        .clickable { onSelectTab(tab) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        if (!isGateUnlocked) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = if (isSelected) Color(0xFF06211B) else ErSimAmber,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = tab.title,
                            color = if (isSelected) Color(0xFF06211B) else ErSimTextPrimary,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Output Well (#0B1D2B)
        Surface(
            color = ErSimOutputWell,
            shape = RoundedCornerShape(11.dp),
            border = BorderStroke(1.dp, ErSimBorder.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(14.dp)
            ) {
                when (selectedTab) {
                    ErSimWorkspaceTab.PATIENT_OVERVIEW -> {
                        if (!q0Unlocked) {
                            ErSimGateLockedNotice(
                                gateType = QuestionGateType.Q0_GATE,
                                message = "Patient details and initial vitals are locked. Crack the Instructor's Initial Gate Question (Q0) at the resuscitation bay entrance to unlock.",
                                onCrackClick = { onOpenQuestionGate(QuestionGateType.Q0_GATE) }
                            )
                        } else {
                            Text(
                                text = "${activeCase.patientName} (${activeCase.ageDesc} • ${activeCase.weightKg} kg)",
                                color = ErSimMint,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            val overviewProfile = PatientCharacterRegistry.profileFor(activeCase)
                            Text(
                                text = "${overviewProfile.regionState} • ${overviewProfile.primaryLanguage} • History from: ${overviewProfile.historian.label}",
                                color = ErSimTextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Chief Complaint: ${activeCase.chiefComplaint}",
                                color = ErSimTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Location: ${room.roomId} (${room.teamName}) • Triage: ${if (room.isPatientStabilized) "STABILIZED" else "RED"} • Actions Completed: ${room.tasksCompletedCount} / ${activeCase.criticalActions.size}",
                                color = ErSimTextMuted,
                                fontSize = 12.sp
                            )
                        }

                        // Always allow switching Active Team Member & Opening 20 Scenarios
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            room.members.forEach { member ->
                                val selected = member == room.activeMember
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSelectActiveTeamMember(member) },
                                    label = { Text(member, fontSize = 11.sp) }
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = onOpenScenarioPicker,
                            border = BorderStroke(1.dp, ErSimMint),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = ErSimMint, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Simulation Library: Case #${activeCase.caseNumber} — ${activeCase.scenarioCategory} (20 Oral Board Cases)",
                                color = ErSimMint,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    ErSimWorkspaceTab.HISTORY -> {
                        if (!q0Unlocked) {
                            ErSimGateLockedNotice(
                                gateType = QuestionGateType.Q0_GATE,
                                message = "Crack Initial Question Q0 to unlock EMS triage handoff, patient history, and instructor clues.",
                                onCrackClick = { onOpenQuestionGate(QuestionGateType.Q0_GATE) }
                            )
                        } else {
                            Text("Chief Complaint & History", color = ErSimMint, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            Text(activeCase.chiefComplaint, color = ErSimTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(activeCase.triageNote, color = ErSimTextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
                            HorizontalDivider(color = ErSimBorder)
                            Text("Attending / Instructor Clinical Clues", color = ErSimAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            activeCase.instructorClues.forEach { clue ->
                                Text("• $clue", color = ErSimTextPrimary, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }

                    ErSimWorkspaceTab.XABCDE -> {
                        if (!q1Unlocked) {
                            ErSimGateLockedNotice(
                                gateType = QuestionGateType.Q1_SURVEY,
                                message = "Primary Survey (XABCDE) is locked. Navigate to Question 1 (Q1) in the Resuscitation Bay (or tap Q1 on the map) and crack it to reveal XABCDE findings.",
                                onCrackClick = { onOpenQuestionGate(QuestionGateType.Q1_SURVEY) }
                            )
                        } else {
                            val s = activeCase.primarySurvey
                            ErSimKeyValueLine("Airway", s.airway)
                            ErSimKeyValueLine("Breathing", s.breathing)
                            ErSimKeyValueLine("Circulation", s.circulation)
                            ErSimKeyValueLine("Disability", s.disability)
                            ErSimKeyValueLine("Exposure", s.exposure)
                        }
                    }

                    ErSimWorkspaceTab.EXAMINATION -> {
                        if (!q1Unlocked) {
                            ErSimGateLockedNotice(
                                gateType = QuestionGateType.Q1_SURVEY,
                                message = "Physical Examination is locked behind Question 1 (Q1). Crack Q1 to perform system-by-system bedside examination.",
                                onCrackClick = { onOpenQuestionGate(QuestionGateType.Q1_SURVEY) }
                            )
                        } else {
                            val p = activeCase.physicalExam
                            val examSystems = listOf(
                                Triple("GENERAL", "General Appearance", p.generalAppearance),
                                Triple("HEENT", "HEENT & Neck", p.heentAndNeck),
                                Triple("PULM", "Chest & Lungs", p.chestAndLungs),
                                Triple("CARD", "Cardiovascular", p.cardiovascular),
                                Triple("ABD", "Abdomen, Pelvis & GU", p.abdomenAndPelvis),
                                Triple("SKIN", "Extremities & Skin", p.extremitiesAndSkin),
                                Triple("NEURO", "Neurologic", p.neurologic)
                            )
                            Text(
                                text = "Tap a body system below (or open the full Exam Drawer) to examine and log findings:",
                                color = ErSimTextMuted,
                                fontSize = 12.sp
                            )
                            examSystems.forEach { (key, title, finding) ->
                                val inspected = uiState.performedExamKeys.contains(key)
                                Surface(
                                    color = if (inspected) ErSimSurface else ErSimRaisedSurface,
                                    shape = RoundedCornerShape(9.dp),
                                    border = BorderStroke(1.dp, if (inspected) ErSimMint else ErSimBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("exam_system_${key.lowercase()}")
                                        .clickable { onPerformExamStep(key) }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "$key — $title",
                                                color = if (inspected) ErSimMint else ErSimTextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = if (inspected) "Examined ✓" else "Tap to Examine",
                                                color = if (inspected) ErSimMint else ErSimAmber,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        AnimatedVisibility(visible = inspected) {
                                            Text(
                                                text = finding,
                                                color = ErSimTextPrimary,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp,
                                                modifier = Modifier.padding(top = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            OutlinedButton(
                                onClick = { onSelectSideDrawerTab(SideDrawerTab.EXAM) },
                                border = BorderStroke(1.dp, ErSimMint),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open Full 14-Point Clinical Exam Sheet", color = ErSimMint, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    ErSimWorkspaceTab.INVESTIGATIONS -> {
                        // Part A: Laboratory & ABG/VBG Blood Gas Reports (Gated strictly by Q2_LABS)
                        Text(
                            text = "1. LABORATORY & ABG / VBG BLOOD GAS REPORTS (Q2)",
                            color = ErSimMint,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                        if (!q2Unlocked) {
                            ErSimGateLockedNotice(
                                gateType = QuestionGateType.Q2_LABS,
                                message = "Laboratory & Blood Gas values are locked. Crack Question 2 (Q2) in the Resuscitation Bay to unlock CBC, Chemistry, Coags, and ABG/VBG results.",
                                onCrackClick = { onOpenQuestionGate(QuestionGateType.Q2_LABS) }
                            )
                        } else {
                            val allLabs = activeCase.abgVbgReports + activeCase.labReports
                            allLabs.forEach { lab ->
                                val flagColor = when (lab.flag) {
                                    LabFlag.CRITICAL -> ErSimRed
                                    LabFlag.HIGH -> ErSimAmber
                                    LabFlag.LOW -> Spo2Sky
                                    LabFlag.NORMAL -> ErSimMint
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ErSimSurface, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 7.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(lab.testName, color = ErSimTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                        Text("${lab.category} (Ref: ${lab.referenceRange})", color = ErSimTextMuted, fontSize = 10.sp)
                                    }
                                    Text(
                                        text = "${lab.value} ${lab.unit}".trim(),
                                        color = flagColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = ErSimBorder)
                        Spacer(Modifier.height(4.dp))

                        // Part B: 12-Lead ECG & Diagnostic Imaging CT/MRI/USG/X-Ray (Gated strictly by Q3_IMAGING)
                        Text(
                            text = "2. 12-LEAD ECG & DIAGNOSTIC IMAGING (CT / MRI / USG / X-RAY) (Q3)",
                            color = ErSimMint,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                        if (!q3Unlocked) {
                            ErSimGateLockedNotice(
                                gateType = QuestionGateType.Q3_IMAGING,
                                message = "12-Lead ECG & Imaging reports are locked. Crack Question 3 (Q3) in the Resuscitation Bay to unlock source figures and findings.",
                                onCrackClick = { onOpenQuestionGate(QuestionGateType.Q3_IMAGING) }
                            )
                        } else {
                            activeCase.imagingAndEcg.forEach { study ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ErSimSurface, RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "${study.modality}: ${study.title} (${study.figureRef})",
                                        color = ErSimAmber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    ClinicalImagingFigureViewer(study = study)
                                    Text("Findings: ${study.findings}", color = ErSimTextPrimary, fontSize = 12.sp)
                                    Text("Impression: ${study.impression}", color = ErSimMint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            OutlinedButton(
                                onClick = { onOpenFullScreenTablet(TabletTab.ECG_IMAGING) },
                                border = BorderStroke(1.dp, ErSimMint),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open Full-Screen Virtual Tablet Imaging Viewer", color = ErSimMint, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    ErSimWorkspaceTab.TREATMENT -> {
                        if (!q0Unlocked) {
                            ErSimGateLockedNotice(
                                gateType = QuestionGateType.Q0_GATE,
                                message = "Crack Initial Question Q0 first to access patient treatment & resuscitation equipment controls.",
                                onCrackClick = { onOpenQuestionGate(QuestionGateType.Q0_GATE) }
                            )
                        } else {
                            val vent = uiState.currentVentilator
                            val defib = uiState.currentDefibrillator
                            Text(
                                text = "Live Equipment Parameters",
                                color = ErSimMint,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "• Mechanical Ventilator: ${if (vent.isActive) "ACTIVE" else "Standby"} — Vt ${vent.tidalVolumeMlPerKg} mL/kg, Rate ${vent.respRatePerMin}/min, FiO₂ ${vent.fio2Percent}%, PEEP ${vent.peepCmH2O} cmH₂O\n• Defibrillator: ${defib.energyJoules} J (${defib.energyPerKgLabel}) • Shocks Delivered: ${defib.shocksDelivered}",
                                color = ErSimTextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            if (room.appliedCartItemsOnPatient.isNotEmpty()) {
                                Text(
                                    text = "Applied Crash Cart Interventions (${room.appliedCartItemsOnPatient.size}):",
                                    color = ErSimAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                room.appliedCartItemsOnPatient.forEach { itemName ->
                                    Text("✓ $itemName", color = ErSimMint, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                Text(
                                    text = "No Crash Cart medications or devices applied yet. Use the Clinical Action Console below to open Medications, Oxygen, IV Access, or the 6-Drawer Crash Cart.",
                                    color = ErSimTextMuted,
                                    fontSize = 12.sp
                                )
                            }

                            HorizontalDivider(color = ErSimBorder)

                            Text(
                                text = "Oral Board Critical Actions Checklist (${room.tasksCompletedCount}/${activeCase.criticalActions.size} • PDF pp. ${activeCase.pdfPages})",
                                color = ErSimMint,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            activeCase.criticalActions.forEach { action ->
                                val done = room.completedCriticalActionIds.contains(action.id)
                                Surface(
                                    color = if (done) ErSimSurface else ErSimRaisedSurface,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (done) ErSimMint else ErSimBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleCriticalAction(action.id) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Checkbox(
                                            checked = done,
                                            onCheckedChange = { onToggleCriticalAction(action.id) }
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${action.order}. ${action.description}",
                                                color = ErSimTextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${action.domain.title} (+${action.points} pts)",
                                                color = ErSimMint,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErSimKeyValueLine(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label.uppercase(), color = ErSimAmber, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
        Text(text = value, color = ErSimTextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
    }
}

@Composable
private fun ErSimGateLockedNotice(
    gateType: QuestionGateType,
    message: String,
    onCrackClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(ErSimSurface, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = ErSimAmber, modifier = Modifier.size(18.dp))
            Text(
                text = "${gateType.code} Authorization Required — ${gateType.title}",
                color = ErSimAmber,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
        Text(text = message, color = ErSimTextPrimary, fontSize = 12.sp, lineHeight = 18.sp)
        Button(
            onClick = onCrackClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = ErSimMint,
                contentColor = Color(0xFF06211B)
            ),
            shape = RoundedCornerShape(9.dp),
            modifier = Modifier.testTag("unlock_notice_${gateType.code.lowercase()}_btn")
        ) {
            Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Crack ${gateType.code} Question Now", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
        }
    }
}

// ============================================================================
// 6. BOTTOM 2-COLUMN CLINICAL ACTION CONSOLE (STRICTLY WIRED TO VERIFIED VIEWMODEL OPS)
// ============================================================================
@Composable
private fun ClinicalActionConsoleCard(
    uiState: SimUiState,
    onSelectWorkspaceTab: (ErSimWorkspaceTab) -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onOpenCrashCart: (Int?) -> Unit,
    onSelectSideDrawerTab: (SideDrawerTab?) -> Unit,
    onOpenFullScreenTablet: (TabletTab?) -> Unit
) {
    val room = uiState.activeRoom
    val q1Unlocked = room.unlockedGates.contains(QuestionGateType.Q1_SURVEY)
    val q2Unlocked = room.unlockedGates.contains(QuestionGateType.Q2_LABS)
    val q3Unlocked = room.unlockedGates.contains(QuestionGateType.Q3_IMAGING)

    ErSimPanelContainer(title = "CLINICAL ACTION CONSOLE") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Row 1: XABCDE | Oxygen
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ErSimConsoleActionButton(
                    title = "🩺 XABCDE",
                    subtitle = if (q1Unlocked) "Primary Survey & Exam" else "Crack Q1 to Unlock",
                    onClick = {
                        if (!q1Unlocked) {
                            onOpenQuestionGate(QuestionGateType.Q1_SURVEY)
                        } else {
                            onSelectWorkspaceTab(ErSimWorkspaceTab.XABCDE)
                            onSelectSideDrawerTab(SideDrawerTab.EXAM)
                        }
                    },
                    testTag = "console_action_xabcde",
                    modifier = Modifier.weight(1f)
                )
                ErSimConsoleActionButton(
                    title = "🫁 Oxygen",
                    subtitle = "Ventilator & O₂ (Drawer 4)",
                    onClick = {
                        onSelectSideDrawerTab(SideDrawerTab.STABILIZE)
                    },
                    testTag = "console_action_oxygen",
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2: IV Access | Blood Tests
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ErSimConsoleActionButton(
                    title = "💉 IV Access",
                    subtitle = "Cannulas & IO (Drawer 2)",
                    onClick = {
                        onOpenCrashCart(2)
                    },
                    testTag = "console_action_iv",
                    modifier = Modifier.weight(1f)
                )
                ErSimConsoleActionButton(
                    title = "🧪 Blood Tests",
                    subtitle = if (q2Unlocked) "Labs & ABG/VBG Report" else "Crack Q2 to Unlock",
                    onClick = {
                        if (!q2Unlocked) {
                            onOpenQuestionGate(QuestionGateType.Q2_LABS)
                        } else {
                            onSelectWorkspaceTab(ErSimWorkspaceTab.INVESTIGATIONS)
                            onOpenFullScreenTablet(TabletTab.LABS_ABG)
                        }
                    },
                    testTag = "console_action_labs",
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3: ECG | Medications
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ErSimConsoleActionButton(
                    title = "❤️ ECG",
                    subtitle = if (q3Unlocked) "12-Lead ECG & Imaging" else "Crack Q3 to Unlock",
                    onClick = {
                        if (!q3Unlocked) {
                            onOpenQuestionGate(QuestionGateType.Q3_IMAGING)
                        } else {
                            onSelectWorkspaceTab(ErSimWorkspaceTab.INVESTIGATIONS)
                            onOpenFullScreenTablet(TabletTab.ECG_IMAGING)
                        }
                    },
                    testTag = "console_action_ecg",
                    modifier = Modifier.weight(1f)
                )
                ErSimConsoleActionButton(
                    title = "💊 Medications",
                    subtitle = "Emergency Drugs (Drawer 1)",
                    onClick = {
                        onOpenCrashCart(1)
                    },
                    testTag = "console_action_meds",
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 4: Crash Cart | Call Team
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ErSimConsoleActionButton(
                    title = "🧰 Crash Cart",
                    subtitle = "CODE 100 (6 Drawers)",
                    onClick = {
                        onOpenCrashCart(null)
                    },
                    testTag = "console_action_cart",
                    modifier = Modifier.weight(1f)
                )
                ErSimConsoleActionButton(
                    title = "📞 Call Team",
                    subtitle = "Team Comms & Attending",
                    onClick = {
                        onSelectSideDrawerTab(SideDrawerTab.COMMUNICATE)
                    },
                    testTag = "console_action_help",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ErSimConsoleActionButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ErSimRaisedSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = modifier
            .heightIn(min = 56.dp)
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
        ) {
            Text(
                text = title,
                color = ErSimTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = subtitle,
                color = ErSimTextMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// 7. REAL CLINICAL TIMELINE (SOURCED FROM room.chatMessages — NO FAKE EVENTS)
// ============================================================================
@Composable
private fun ClinicalTimelineCard(
    uiState: SimUiState
) {
    val room = uiState.activeRoom
    val events = remember(room.chatMessages) { room.chatMessages.asReversed() }

    ErSimPanelContainer(
        title = "CLINICAL TIMELINE (${events.size} RECORDED ROOM EVENTS)"
    ) {
        if (events.isEmpty()) {
            Text(
                text = "No room activities recorded yet.",
                color = ErSimTextMuted,
                fontSize = 12.sp
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                events.take(15).forEach { msg ->
                    val tagColor = when (msg.roleTag) {
                        "INSTRUCTOR" -> ErSimAmber
                        "CRASH_CART" -> ErSimRed
                        "TEAM" -> ErSimMint
                        else -> ErSimTextMuted
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${msg.timestampLabel} • ${msg.sender}",
                                color = tagColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = msg.roleTag,
                                color = ErSimTextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = msg.text,
                            color = ErSimTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        HorizontalDivider(color = ErSimBorder.copy(alpha = 0.65f))
                    }
                }
            }
        }
    }
}

// ============================================================================
// REUSABLE ER SIM PANEL WRAPPER (#102234 Surface, #29445A Border, 15.dp Radius)
// ============================================================================
@Composable
fun ErSimPanelContainer(
    title: String,
    modifier: Modifier = Modifier,
    trailingHeader: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = ErSimTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
                trailingHeader?.invoke()
            }
            Spacer(Modifier.height(11.dp))
            content()
        }
    }
}
