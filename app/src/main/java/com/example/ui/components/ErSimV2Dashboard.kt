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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
    onNavigateHome: (() -> Unit)? = null,
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
            onToggleJoystick = onToggleJoystick,
            onNavigateHome = onNavigateHome
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
    onToggleJoystick: () -> Unit,
    onNavigateHome: (() -> Unit)? = null
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val caseAccent = ClinicalSeverityEngine.categoryAccent(activeCase.scenarioCategory, activeCase.isPediatricOrInfant)

    Surface(
        color = ErSimHeaderBg,
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Vibrant clinical spectrum accent strip along the top edge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(ErSimMint, ErSimCyan, ErSimViolet, ErSimRose, ErSimAmber)
                        )
                    )
            )
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onNavigateHome != null) {
                            IconButton(
                                onClick = onNavigateHome,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("sim_home_button")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back to Home",
                                    tint = ErSimTextPrimary
                                )
                            }
                        }
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
                                color = ErSimCyan,
                                fontSize = 10.sp,
                                letterSpacing = 1.1.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bay & Scenario Indicator Chip (Category-color accented)
                    Surface(
                        color = caseAccent.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, caseAccent.copy(alpha = 0.55f)),
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
                                tint = caseAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text = "${room.roomId} • Case #${activeCase.caseNumber}",
                                    color = caseAccent,
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
                                tint = caseAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Quick Utility Icons (Color-coded tinted circles)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HeaderIconPill(
                            icon = Icons.Default.AdminPanelSettings,
                            contentDescription = "Instructor Question Engine & Admin Room",
                            accent = ErSimAmber,
                            onClick = onOpenAdminEngine,
                            testTag = "open_instructor_admin_btn"
                        )
                        HeaderIconPill(
                            icon = Icons.Default.TabletMac,
                            contentDescription = "Full-Screen Virtual Tablet",
                            accent = ErSimCyan,
                            onClick = onOpenFullScreenTablet,
                            testTag = "hud_fullscreen_tablet_btn"
                        )
                        HeaderIconPill(
                            icon = if (uiState.isAudioEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Resus Bay Audio",
                            accent = if (uiState.isAudioEnabled) ErSimMint else ErSimTextMuted,
                            onClick = onToggleAudio,
                            testTag = "hud_audio_toggle_btn"
                        )
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
                            border = BorderStroke(1.2.dp, MonitorCyan.copy(alpha = 0.65f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = "Clinical Timer",
                                    tint = MonitorCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = room.formattedTimer,
                                    color = MonitorTextLight,
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
                            accentColor = ErSimAmber,
                            onClick = onToggleTimer,
                            testTag = "header_pause_resume_btn"
                        )

                        // Restart Button
                        ErSimHeaderActionButton(
                            label = "Restart",
                            accentColor = ErSimSky,
                            onClick = onRestartCase,
                            testTag = "header_restart_btn"
                        )

                        // End Case Button
                        ErSimHeaderActionButton(
                            label = "End Case",
                            accentColor = ErSimRed,
                            tintedFill = true,
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
                            accentColor = if (uiState.showJoystickControl) ErSimViolet else ErSimTextMuted,
                            tintedFill = uiState.showJoystickControl,
                            onClick = onToggleJoystick,
                            testTag = "header_joystick_btn"
                        )
                        if (uiState.isAudioEnabled) {
                            ErSimHeaderActionButton(
                                label = if (uiState.isAlarmSilenced) "Alarm Muted" else "Mute Alarm",
                                accentColor = if (uiState.isAlarmSilenced) ErSimTextMuted else ErSimAmber,
                                tintedFill = !uiState.isAlarmSilenced,
                                onClick = onToggleAlarmSilence,
                                testTag = "hud_alarm_silence_btn"
                            )
                        }
                        ErSimHeaderActionButton(
                            label = "ER Map",
                            accentColor = ErSimCyan,
                            tintedFill = true,
                            onClick = onOpenFullScreenMap,
                            testTag = "header_er_map_btn"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderIconPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    accent: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = accent.copy(alpha = 0.14f),
        shape = CircleShape,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.5f)),
        modifier = Modifier
            .size(38.dp)
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = accent,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
private fun ErSimHeaderActionButton(
    label: String,
    isPrimary: Boolean = false,
    accentColor: Color = ErSimTextPrimary,
    tintedFill: Boolean = false,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = when {
            isPrimary -> ErSimMint
            tintedFill -> accentColor.copy(alpha = 0.15f)
            else -> ErSimRaisedSurface
        },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            1.dp,
            when {
                isPrimary -> ErSimMint
                tintedFill -> accentColor.copy(alpha = 0.65f)
                else -> ErSimBorder
            }
        ),
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
                color = if (isPrimary) Color(0xFF06211B) else accentColor,
                fontWeight = if (isPrimary || tintedFill) FontWeight.ExtraBold else FontWeight.SemiBold,
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
    val isPeds = activeCase.isPediatricOrInfant
    val q0Unlocked = room.unlockedGates.contains(QuestionGateType.Q0_GATE)

    // Live clinical severity assessment for every parameter
    val hrStyle = ClinicalSeverityEngine.assessHeartRate(vitals.hr, isPeds, onDarkMonitorWell = true)
    val bpStyle = ClinicalSeverityEngine.assessBloodPressure(vitals.systolic, vitals.diastolic, isPeds, onDarkMonitorWell = true)
    val spo2Style = ClinicalSeverityEngine.assessSpo2(vitals.spo2, onDarkMonitorWell = true)
    val rrStyle = ClinicalSeverityEngine.assessRespRate(vitals.rr, isPeds, onDarkMonitorWell = true)
    val tempStyle = ClinicalSeverityEngine.assessTemp(vitals.tempCelsius, onDarkMonitorWell = true)
    val etco2Style = ClinicalSeverityEngine.assessEtco2(vitals.etco2, onDarkMonitorWell = true)
    val rhythmSeverity = ClinicalSeverityEngine.assessRhythm(vitals.rhythmLabel)
    val criticalTags = if (q0Unlocked) ClinicalSeverityEngine.criticalVitalTags(vitals, isPeds) else emptyList()

    // Subtle critical pulse animation for life-threatening vitals
    val pulseTransition = rememberInfiniteTransition(label = "critical_vital_pulse")
    val criticalPulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "critical_pulse_alpha"
    )

    ErSimPanelContainer(
        title = "CONTINUOUS PATIENT MONITORING",
        accentColor = when {
            !q0Unlocked -> ErSimAmber
            criticalTags.isNotEmpty() -> ErSimRed
            else -> ErSimMint
        },
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Temperature mini severity pill
                    Surface(
                        color = tempStyle.containerTint,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, tempStyle.borderColor)
                    ) {
                        Text(
                            text = "T ${vitals.tempCelsius}°C",
                            color = tempStyle.accent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    // EtCO2 mini severity pill
                    Surface(
                        color = etco2Style.containerTint,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, etco2Style.borderColor)
                    ) {
                        Text(
                            text = "EtCO₂ ${vitals.etco2}",
                            color = etco2Style.accent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        // Critical Values Alert Strip when any vital is in the critical zone
        if (q0Unlocked && criticalTags.isNotEmpty()) {
            Surface(
                color = MonitorRed.copy(alpha = 0.16f * criticalPulseAlpha),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.3.dp, MonitorRed.copy(alpha = criticalPulseAlpha)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 9.dp)
                    .testTag("critical_vitals_alert_banner")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(MonitorRed.copy(alpha = criticalPulseAlpha))
                    )
                    Text(
                        text = "CRITICAL VALUES (${criticalTags.size}): ${criticalTags.joinToString(" • ")}",
                        color = MonitorRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

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
                    ErSimVitalTile("HEART RATE", hrText, "bpm", q0Unlocked, hrStyle, criticalPulseAlpha, Modifier.weight(1f))
                    ErSimVitalTile("BLOOD PRESSURE", bpText, "mmHg", q0Unlocked, bpStyle, criticalPulseAlpha, Modifier.weight(1f))
                    ErSimVitalTile("OXYGEN SAT", spo2Text, "%", q0Unlocked, spo2Style, criticalPulseAlpha, Modifier.weight(1f))
                    ErSimVitalTile("RESP RATE", rrText, "/min", q0Unlocked, rrStyle, criticalPulseAlpha, Modifier.weight(1f))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ErSimVitalTile("HEART RATE", hrText, "bpm", q0Unlocked, hrStyle, criticalPulseAlpha, Modifier.weight(1f))
                        ErSimVitalTile("BLOOD PRESSURE", bpText, "mmHg", q0Unlocked, bpStyle, criticalPulseAlpha, Modifier.weight(1f))
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ErSimVitalTile("OXYGEN SAT", spo2Text, "%", q0Unlocked, spo2Style, criticalPulseAlpha, Modifier.weight(1f))
                        ErSimVitalTile("RESP RATE", rrText, "/min", q0Unlocked, rrStyle, criticalPulseAlpha, Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Live Scrolling Monitor ECG Waveform Strip (color-coded by rhythm & stability severity)
        val ecgSeverity = when {
            !q0Unlocked -> ClinicalSeverity.NORMAL
            (activeCase.caseNumber == 10 || activeCase.caseNumber == 17) && !room.isPatientStabilized -> ClinicalSeverity.CRITICAL
            rhythmSeverity == ClinicalSeverity.CRITICAL || hrStyle.isCritical -> ClinicalSeverity.CRITICAL
            rhythmSeverity == ClinicalSeverity.HIGH || hrStyle.severity == ClinicalSeverity.HIGH -> ClinicalSeverity.HIGH
            rhythmSeverity == ClinicalSeverity.LOW || hrStyle.severity == ClinicalSeverity.LOW -> ClinicalSeverity.LOW
            else -> ClinicalSeverity.NORMAL
        }
        ReusableMonitorEcgWaveStrip(
            q0Unlocked = q0Unlocked,
            caseNumber = activeCase.caseNumber,
            isStabilized = room.isPatientStabilized,
            ecgSeverity = ecgSeverity,
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
    q0Unlocked: Boolean,
    style: ClinicalColorStyle,
    pulseAlpha: Float,
    modifier: Modifier = Modifier
) {
    val activeAccent = if (q0Unlocked) style.accent else MonitorAmber
    val borderWidth = if (q0Unlocked && style.isCritical) 1.8.dp else 1.dp
    val borderColor = when {
        !q0Unlocked -> ErSimBorder.copy(alpha = 0.65f)
        style.isCritical -> style.accent.copy(alpha = pulseAlpha)
        else -> style.borderColor
    }
    val surfaceBg = when {
        !q0Unlocked -> ErSimVitalWell
        style.isCritical -> Color(0xFF1F0A14)
        style.severity == ClinicalSeverity.HIGH -> Color(0xFF1C1609)
        style.severity == ClinicalSeverity.LOW -> Color(0xFF081B2C)
        else -> ErSimVitalWell
    }

    Surface(
        color = surfaceBg,
        shape = RoundedCornerShape(11.dp),
        border = BorderStroke(borderWidth, borderColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label,
                    color = MonitorTextMuted,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (q0Unlocked) {
                    Surface(
                        color = if (style.isCritical) style.accent else style.accent.copy(alpha = 0.20f),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = style.badgeText,
                            color = if (style.isCritical) Color(0xFF1B0308) else style.accent,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.4.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = value,
                color = activeAccent,
                fontSize = if (value == "LOCKED") 16.sp else 25.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(vertical = 3.dp)
            )
            Text(
                text = unit,
                color = MonitorTextMuted,
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
    ecgSeverity: ClinicalSeverity = ClinicalSeverity.NORMAL,
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
    val traceColor = when {
        !q0Unlocked -> MonitorTextMuted
        ecgSeverity == ClinicalSeverity.CRITICAL -> MonitorRed
        ecgSeverity == ClinicalSeverity.HIGH -> MonitorAmber
        ecgSeverity == ClinicalSeverity.LOW -> MonitorSky
        else -> MonitorMint
    }

    Surface(
        color = ErSimWaveWell,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.2.dp, if (q0Unlocked) traceColor.copy(alpha = 0.55f) else ErSimBorder.copy(alpha = 0.5f)),
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
                // Subtle clinical oscilloscope grid
                val gridColor = traceColor.copy(alpha = 0.11f)
                for (gx in 1..9) {
                    val x = w * (gx / 10f)
                    drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                }
                for (gy in 1..3) {
                    val y = h * (gy / 4f)
                    drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                }

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
                // Soft glow under waveform + crisp core trace
                drawPath(
                    path = path,
                    color = traceColor.copy(alpha = 0.28f),
                    style = Stroke(width = 6.5f)
                )
                drawPath(
                    path = path,
                    color = traceColor,
                    style = Stroke(width = 2.6f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = rhythmLabel,
                    color = if (q0Unlocked) traceColor else MonitorAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (q0Unlocked && ecgSeverity != ClinicalSeverity.NORMAL) {
                    Surface(
                        color = traceColor.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = ecgSeverity.shortBadge,
                            color = traceColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }
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

    val criticalLabCount = remember(activeCase) {
        (activeCase.abgVbgReports + activeCase.labReports).count { it.flag == LabFlag.CRITICAL }
    }

    ErSimPanelContainer(
        title = "CLINICAL CHART & DIAGNOSTIC WORKSPACE",
        accentColor = ErSimCyan
    ) {
        // Horizontal Scrollable Tab Pills (Color-coded by clinical workspace domain)
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
                val tabAccent = when (tab) {
                    ErSimWorkspaceTab.PATIENT_OVERVIEW -> ErSimMint
                    ErSimWorkspaceTab.HISTORY -> ErSimSky
                    ErSimWorkspaceTab.XABCDE -> ErSimAmber
                    ErSimWorkspaceTab.EXAMINATION -> ErSimViolet
                    ErSimWorkspaceTab.INVESTIGATIONS -> ErSimCyan
                    ErSimWorkspaceTab.TREATMENT -> ErSimRose
                }
                val showCritLabPill = tab == ErSimWorkspaceTab.INVESTIGATIONS && q2Unlocked && criticalLabCount > 0

                Surface(
                    color = if (isSelected) tabAccent else tabAccent.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        when {
                            isSelected -> tabAccent
                            showCritLabPill -> ErSimRed
                            isGateUnlocked -> tabAccent.copy(alpha = 0.45f)
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
                                tint = if (isSelected) Color(0xFF06151C) else ErSimAmber,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = tab.title,
                            color = if (isSelected) Color(0xFF06151C) else ErSimTextPrimary,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        if (showCritLabPill) {
                            Surface(
                                color = ErSimRed,
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Text(
                                    text = "$criticalLabCount!",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
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
                            ErSimKeyValueLine("A", "Airway", s.airway, MonitorSky)
                            ErSimKeyValueLine("B", "Breathing", s.breathing, MonitorCyan)
                            ErSimKeyValueLine("C", "Circulation", s.circulation, MonitorRed)
                            ErSimKeyValueLine("D", "Disability", s.disability, MonitorViolet)
                            ErSimKeyValueLine("E", "Exposure", s.exposure, MonitorAmber)
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
                            val critCount = allLabs.count { it.flag == LabFlag.CRITICAL }
                            val highCount = allLabs.count { it.flag == LabFlag.HIGH }
                            val lowCount = allLabs.count { it.flag == LabFlag.LOW }
                            val normCount = allLabs.count { it.flag == LabFlag.NORMAL }

                            // Color-coded severity summary strip
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                if (critCount > 0) LabSeveritySummaryChip("$critCount CRITICAL", MonitorRed, filled = true)
                                if (highCount > 0) LabSeveritySummaryChip("$highCount HIGH", MonitorAmber)
                                if (lowCount > 0) LabSeveritySummaryChip("$lowCount LOW", MonitorSky)
                                LabSeveritySummaryChip("$normCount NORMAL", MonitorMint)
                            }

                            allLabs.forEach { lab ->
                                val style = ClinicalSeverityEngine.labStyle(lab.flag, onDarkMonitorWell = true)
                                Surface(
                                    color = style.containerTint,
                                    shape = RoundedCornerShape(9.dp),
                                    border = BorderStroke(
                                        if (style.isCritical) 1.5.dp else 1.dp,
                                        style.borderColor
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .fillMaxHeight()
                                                .background(style.accent)
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = lab.testName,
                                                    color = MonitorTextLight,
                                                    fontWeight = if (style.isCritical) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                    fontSize = 12.sp
                                                )
                                                Text(
                                                    text = "${lab.category} • Ref: ${lab.referenceRange}",
                                                    color = MonitorTextMuted,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "${lab.value} ${lab.unit}".trim(),
                                                    color = style.accent,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.5.sp
                                                )
                                                Spacer(Modifier.height(2.dp))
                                                Surface(
                                                    color = if (style.isCritical) style.accent else style.accent.copy(alpha = 0.22f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = style.badgeText,
                                                        color = if (style.isCritical) Color(0xFF1B0308) else style.accent,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 8.5.sp,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = ErSimBorder)
                        Spacer(Modifier.height(4.dp))

                        // Part B: 12-Lead ECG & Diagnostic Imaging CT/MRI/USG/X-Ray (Gated strictly by Q3_IMAGING)
                        Text(
                            text = "2. 12-LEAD ECG & DIAGNOSTIC IMAGING (CT / MRI / USG / X-RAY) (Q3)",
                            color = MonitorCyan,
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
                                val modColor = when (study.modality.uppercase()) {
                                    "ECG" -> MonitorRed
                                    "CT", "MRI" -> MonitorViolet
                                    "USG" -> MonitorCyan
                                    else -> MonitorSky
                                }
                                Surface(
                                    color = ErSimSurface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, modColor.copy(alpha = 0.55f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                color = modColor.copy(alpha = 0.20f),
                                                shape = RoundedCornerShape(5.dp)
                                            ) {
                                                Text(
                                                    text = study.modality,
                                                    color = modColor,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = "${study.title} (${study.figureRef})",
                                                color = ErSimTextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                        ClinicalImagingFigureViewer(study = study)
                                        Text("Findings: ${study.findings}", color = ErSimTextPrimary, fontSize = 12.sp)
                                        Text("Impression: ${study.impression}", color = MonitorMint, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                            OutlinedButton(
                                onClick = { onOpenFullScreenTablet(TabletTab.ECG_IMAGING) },
                                border = BorderStroke(1.dp, MonitorCyan),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open Full-Screen Virtual Tablet Imaging Viewer", color = MonitorCyan, fontWeight = FontWeight.Bold)
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
private fun LabSeveritySummaryChip(
    label: String,
    accent: Color,
    filled: Boolean = false
) {
    Surface(
        color = if (filled) accent else accent.copy(alpha = 0.16f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.7f))
    ) {
        Text(
            text = label,
            color = if (filled) Color(0xFF1B0308) else accent,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun ErSimKeyValueLine(
    letter: String,
    label: String,
    value: String,
    accent: Color
) {
    Surface(
        color = accent.copy(alpha = 0.09f),
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.38f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .background(accent.copy(alpha = 0.22f), CircleShape)
            ) {
                Text(
                    text = letter,
                    color = accent,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label.uppercase(), color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                Text(text = value, color = MonitorTextLight, fontSize = 12.5.sp, lineHeight = 18.sp)
            }
        }
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

    ErSimPanelContainer(
        title = "CLINICAL ACTION CONSOLE",
        accentColor = ErSimRose
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Row 1: XABCDE | Oxygen
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ErSimConsoleActionButton(
                    title = "🩺 XABCDE",
                    subtitle = if (q1Unlocked) "Primary Survey & Exam" else "Crack Q1 to Unlock",
                    accentColor = ErSimMint,
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
                    accentColor = ErSimSky,
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
                    accentColor = ErSimCyan,
                    onClick = {
                        onOpenCrashCart(2)
                    },
                    testTag = "console_action_iv",
                    modifier = Modifier.weight(1f)
                )
                ErSimConsoleActionButton(
                    title = "🧪 Blood Tests",
                    subtitle = if (q2Unlocked) "Labs & ABG/VBG Report" else "Crack Q2 to Unlock",
                    accentColor = ErSimViolet,
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
                    accentColor = ErSimRose,
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
                    accentColor = ErSimAmber,
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
                    accentColor = ErSimRed,
                    onClick = {
                        onOpenCrashCart(null)
                    },
                    testTag = "console_action_cart",
                    modifier = Modifier.weight(1f)
                )
                ErSimConsoleActionButton(
                    title = "📞 Call Team",
                    subtitle = "Team Comms & Attending",
                    accentColor = ErSimOrange,
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
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = accentColor.copy(alpha = 0.11f),
        shape = RoundedCornerShape(11.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.50f)),
        modifier = modifier
            .heightIn(min = 58.dp)
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp)
            ) {
                Text(
                    text = title,
                    color = ErSimTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    color = accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
        title = "CLINICAL TIMELINE (${events.size} RECORDED ROOM EVENTS)",
        accentColor = ErSimViolet
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
                        "TEAM" -> ErSimCyan
                        else -> ErSimMint
                    }
                    Surface(
                        color = tagColor.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(9.dp),
                        border = BorderStroke(1.dp, tagColor.copy(alpha = 0.30f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .fillMaxHeight()
                                    .background(tagColor)
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp, vertical = 7.dp)
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
                                    Surface(
                                        color = tagColor.copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = msg.roleTag,
                                            color = tagColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    text = msg.text,
                                    color = ErSimTextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// REUSABLE ER SIM PANEL WRAPPER (#102234 Surface, Accent Dot Header, 15.dp Radius)
// ============================================================================
@Composable
fun ErSimPanelContainer(
    title: String,
    modifier: Modifier = Modifier,
    accentColor: Color = ErSimMint,
    trailingHeader: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.38f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = title,
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.3.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                trailingHeader?.invoke()
            }
            Spacer(Modifier.height(11.dp))
            content()
        }
    }
}
