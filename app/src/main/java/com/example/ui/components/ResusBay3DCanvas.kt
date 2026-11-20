package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamRegion
import com.example.data.PatientCharacterRegistry
import com.example.data.QuestionGateType
import com.example.ui.SimUiState
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

@Composable
fun ResusBay3DViewport(
    uiState: SimUiState,
    onCameraDrag: (Float, Float) -> Unit,
    onJoystickMove: (Float, Float) -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onOpenCrashCart: () -> Unit,
    onOpenEquipmentStabilize: () -> Unit,
    modifier: Modifier = Modifier,
    onPerformExamStep: (String) -> Unit = {}
) {
    val activeCase = uiState.activeCase
    val vitals = uiState.currentVitals
    val room = uiState.activeRoom
    val q0Unlocked = room.unlockedGates.contains(QuestionGateType.Q0_GATE)
    val vent = uiState.currentVentilator
    val q1Unlocked = room.unlockedGates.contains(QuestionGateType.Q1_SURVEY)

    // Indian Patient Character System V1: typed character -> bay art + exam anchors
    val patientCharacter = remember(activeCase.caseNumber) { PatientCharacterRegistry.characterFor(activeCase) }
    val bayArt = remember(activeCase.caseNumber) {
        PatientBayArtRegistry.artFor(patientCharacter, usePediatricBay = activeCase.isPediatricOrInfant)
    }
    var focusedExamRegion by remember(activeCase.caseNumber) { mutableStateOf<ExamRegion?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "live_monitor_anim")
    val ecgPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ecg_scroll_phase"
    )
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_pulse"
    )

    // Smoothly animate camera pan/tilt from touch swipe, < > arrows, or joystick
    val animatedYaw by animateFloatAsState(
        targetValue = uiState.cameraYawDeg,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "yaw"
    )
    val animatedPitch by animateFloatAsState(
        targetValue = uiState.cameraPitchDeg,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "pitch"
    )
    val zoomScale = (1.16f + (0.85f - uiState.playerY) * 0.32f).coerceIn(1.12f, 1.48f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFD6DDE3))
            .testTag("resus_3d_viewport")
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onCameraDrag(-dragAmount.x * 0.16f, dragAmount.y * 0.12f)
                }
            }
    ) {
        val viewportW = maxWidth
        val viewportH = maxHeight

        // 1. PHOTOREALISTIC 3D RESUSCITATION BAY SCENE + LIVE IN-WORLD EQUIPMENT DISPLAYS
        // Entire layer pans/zooms together with 3D perspective depth!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                    translationX = (-animatedYaw * 3.8f) - ((uiState.playerX - 0.5f) * 140f)
                    translationY = (animatedPitch * 2.6f)
                    rotationY = (animatedYaw * 0.12f).coerceIn(-8f, 8f)
                    cameraDistance = 18f * density
                }
        ) {
            Image(
                painter = painterResource(id = bayArt.drawableRes),
                contentDescription = "Resuscitation bay with patient, monitor, ventilator and crash cart",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // BODY-REGION EXAM HOTSPOTS (follow pan/zoom; Q1-gated like the Exam tab)
            val cropScale = max(viewportW.value / bayArt.imageWidthPx, viewportH.value / bayArt.imageHeightPx)
            val drawnW = bayArt.imageWidthPx * cropScale
            val drawnH = bayArt.imageHeightPx * cropScale
            val cropOffX = (viewportW.value - drawnW) / 2f
            val cropOffY = (viewportH.value - drawnH) / 2f
            val pinSize = 30f
            bayArt.examAnchors.forEach { (region, anchor) ->
                val cx = cropOffX + anchor.x * drawnW
                val cy = cropOffY + anchor.y * drawnH
                if (cx < 0f || cy < 0f || cx > viewportW.value || cy > viewportH.value) return@forEach
                val examined = uiState.performedExamKeys.contains(region.key)
                Surface(
                    color = when {
                        !q1Unlocked -> ClinicalNavyHeader.copy(alpha = 0.78f)
                        examined -> Color(0xFF059669).copy(alpha = 0.90f)
                        else -> Color(0xFF0E7490).copy(alpha = 0.85f)
                    },
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.9f)),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (cx - pinSize / 2f).dp, y = (cy - pinSize / 2f).dp)
                        .size(pinSize.dp)
                        .clickable {
                            if (!q1Unlocked) {
                                onOpenQuestionGate(QuestionGateType.Q1_SURVEY)
                            } else {
                                onPerformExamStep(region.key)
                                focusedExamRegion = region
                            }
                        }
                        .testTag("bay_exam_hotspot_${region.key.lowercase()}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                !q1Unlocked -> Icons.Default.Lock
                                examined -> Icons.Default.CheckCircle
                                else -> Icons.Default.TouchApp
                            },
                            contentDescription = "Examine ${region.title}" + if (!q1Unlocked) " (locked, crack Q1)" else "",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // LIVE 3D IN-WORLD ICU MONITOR & DEFIBRILLATOR DISPLAY OVERLAY
            // Positioned directly over the wall-mounted ICU monitor in the bay!
            Surface(
                color = Color(0xFF060E17).copy(alpha = 0.94f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (q0Unlocked && (vitals.systolic < 90 || vitals.hr > 145 || vitals.hr < 60)) ArterialRed else TelemetryCyan
                ),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = viewportH * 0.19f, end = viewportW * 0.16f)
                    .width(138.dp)
                    .clickable { onOpenEquipmentStabilize() }
                    .testTag("inworld_live_monitor_screen")
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "LIVE MONITOR",
                            color = TelemetryCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (q0Unlocked) "HR ${vitals.hr}" else "LOCKED",
                            color = if (q0Unlocked) EcgEmerald else CapnoAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Live Scrolling ECG Waveform right on the in-world monitor!
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                    ) {
                        val w = size.width
                        val h = size.height
                        val path = Path()
                        val steps = 40
                        for (i in 0..steps) {
                            val frac = i.toFloat() / steps
                            val px = frac * w
                            val t = (frac * 2.5f + ecgPhase * 2.5f) % 1.0f
                            val yOff = when {
                                !q0Unlocked -> 0f
                                activeCase.caseNumber == 10 && !room.isPatientStabilized -> sin(t * 2f * PI.toFloat()) * (h * 0.4f)
                                activeCase.caseNumber == 17 && !room.isPatientStabilized -> sin(t * 5f * PI.toFloat()) * (h * 0.42f) * sin(frac * PI.toFloat() * 2f)
                                t in 0.28f..0.34f -> -(h * 0.42f)
                                t in 0.34f..0.40f -> (h * 0.22f)
                                t in 0.56f..0.68f -> -(h * 0.16f)
                                else -> 0f
                            }
                            val py = h * 0.55f + yOff
                            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                        }
                        drawPath(path, if (q0Unlocked) EcgEmerald else Color.Gray, style = Stroke(1.8f))
                    }

                    if (q0Unlocked) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("BP ${vitals.bpDisplay}", color = Color(0xFFFF6B81), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("SpO₂ ${vitals.spo2?.let { "$it%" } ?: "None"}", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("RR ${vitals.rr} • ${vitals.tempCelsius}°C", color = Color(0xFFFBBF24), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (activeCase.caseNumber == 17) "4 J/kg" else "${uiState.currentDefibrillator.energyJoules}J",
                                color = Color(0xFFFB923C),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    } else {
                        Text(
                            text = "Crack Q0 to unlock vitals",
                            color = Color(0xFFFBBF24),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // LIVE 3D IN-WORLD MECHANICAL VENTILATOR SCREEN OVERLAY
            // Positioned directly over the ICU ventilator below the monitor!
            Surface(
                color = Color(0xFF0A192F).copy(alpha = 0.92f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    if (vent.isActive) TelemetryCyan else Color(0xFF64748B)
                ),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(bottom = viewportH * 0.14f, end = viewportW * 0.21f)
                    .width(106.dp)
                    .clickable { onOpenEquipmentStabilize() }
                    .testTag("inworld_ventilator_screen")
            ) {
                Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                    Text(
                        text = if (vent.isActive) "VENTILATOR ON" else "VENT STANDBY",
                        color = if (vent.isActive) TelemetryCyan else TextSecondaryMuted,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Vt ${vent.tidalVolumeMlPerKg}mL/kg • RR ${vent.respRatePerMin}",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "FiO₂ ${vent.fio2Percent}% • PEEP ${vent.peepCmH2O}",
                        color = Color(0xFFFBBF24),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // INTERACTIVE HOTSPOT OVER RED 6-DRAWER CRASH CART (Right of Patient, Adjacent to Ventilator)
            Surface(
                color = ArterialRed.copy(alpha = 0.92f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(top = viewportH * 0.10f, end = viewportW * 0.04f)
                    .clickable { onOpenCrashCart() }
                    .testTag("inworld_crash_cart_hotspot")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = "Open Crash Cart",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "CRASH CART",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // IN-BAY FLOATING QUESTION PINS (Q0, Q1, Q2, Q3) SO PLAYERS CAN COLLECT THEM IN THE BAY
            val pinPositions = listOf(
                Triple(QuestionGateType.Q0_GATE, Alignment.BottomCenter, PaddingValues(bottom = viewportH * 0.16f)),
                Triple(QuestionGateType.Q1_SURVEY, Alignment.CenterStart, PaddingValues(start = viewportW * 0.16f, top = viewportH * 0.06f)),
                Triple(QuestionGateType.Q2_LABS, Alignment.BottomEnd, PaddingValues(end = viewportW * 0.18f, bottom = viewportH * 0.24f)),
                Triple(QuestionGateType.Q3_IMAGING, Alignment.TopStart, PaddingValues(start = viewportW * 0.16f, top = viewportH * 0.30f))
            )
            pinPositions.forEach { (gate, align, pad) ->
                val isUnlocked = room.unlockedGates.contains(gate)
                Surface(
                    color = if (isUnlocked) Color(0xFF059669).copy(alpha = 0.92f) else ClinicalNavyHeader.copy(alpha = 0.92f),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (isUnlocked) Color.White else ClinicalCoralAccent
                    ),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(align)
                        .padding(pad)
                        .graphicsLayer {
                            if (!isUnlocked) {
                                scaleX = beaconPulse
                                scaleY = beaconPulse
                            }
                        }
                        .clickable { onOpenQuestionGate(gate) }
                        .testTag("bay_beacon_${gate.code.lowercase()}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.Help,
                            contentDescription = gate.code,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = gate.code,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // EXAM FINDING CARD (fixed to viewport). Text comes only from activeCase.physicalExam.
        val shownRegion = focusedExamRegion
        AnimatedVisibility(
            visible = shownRegion != null && q1Unlocked,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 12.dp, end = 12.dp, bottom = 60.dp)
        ) {
            if (shownRegion != null) {
                Surface(
                    color = Color(0xFF102234).copy(alpha = 0.96f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF51E3B2)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .testTag("bay_exam_finding_card")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${shownRegion.key} — ${shownRegion.title}",
                                color = Color(0xFF51E3B2),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close finding",
                                tint = Color(0xFF92ABBA),
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { focusedExamRegion = null }
                                    .padding(5.dp)
                            )
                        }
                        Text(
                            text = shownRegion.findingFor(activeCase.physicalExam),
                            color = Color(0xFFE8F4FA),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Generic art that does not yet depict this patient
        if (bayArt.isPlaceholder) {
            Surface(
                color = Color(0xFF07111B).copy(alpha = 0.72f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 52.dp)
                    .testTag("bay_placeholder_art_chip")
            ) {
                Text(
                    text = "Placeholder patient art",
                    color = Color(0xFFFFCB74),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        // OPTIONAL FLOATING 3D VIRTUAL JOYSTICK (Bottom-Left when toggled on!)
        AnimatedVisibility(
            visible = uiState.showJoystickControl,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 52.dp, bottom = 78.dp)
        ) {
            VirtualJoystickControl(
                onMove = onJoystickMove,
                modifier = Modifier.testTag("virtual_joystick")
            )
        }
    }
}

@Composable
private fun VirtualJoystickControl(
    onMove: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadiusPx = 68f

    LaunchedEffect(knobOffset) {
        while (knobOffset != Offset.Zero) {
            onMove(
                (knobOffset.x / maxRadiusPx).coerceIn(-1f, 1f),
                (knobOffset.y / maxRadiusPx).coerceIn(-1f, 1f)
            )
            delay(32L)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(96.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xCC222B35))
            .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { knobOffset = Offset.Zero },
                    onDragCancel = { knobOffset = Offset.Zero }
                ) { change, dragAmount ->
                    change.consume()
                    val candidate = knobOffset + dragAmount
                    val dist = hypot(candidate.x, candidate.y)
                    knobOffset = if (dist <= maxRadiusPx) {
                        candidate
                    } else {
                        Offset(candidate.x / dist * maxRadiusPx, candidate.y / dist * maxRadiusPx)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = size.minDimension * 0.36f,
                center = c,
                style = Stroke(1.5f)
            )
            drawCircle(
                color = ClinicalActiveBlue,
                radius = 20.dp.toPx(),
                center = c + knobOffset
            )
            drawCircle(
                color = Color.White,
                radius = 7.dp.toPx(),
                center = c + knobOffset
            )
        }
        Text(
            text = "WALK 3D",
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp)
        )
    }
}
