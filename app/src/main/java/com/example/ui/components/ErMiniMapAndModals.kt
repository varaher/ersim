package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.ui.SimUiState
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun ErTopRightMiniMap(
    uiState: SimUiState,
    onExpandFullScreen: () -> Unit,
    onSelectQuestionOnMap: (QuestionGateType) -> Unit,
    onOpenCrashCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xEE18293D),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.85f)),
        shadowElevation = 6.dp,
        modifier = modifier
            .width(106.dp)
            .height(104.dp)
            .testTag("er_top_right_minimap")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ClinicalNavyHeader)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "ER MAP",
                    color = Color.White,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(
                    onClick = onExpandFullScreen,
                    modifier = Modifier
                        .size(20.dp)
                        .testTag("expand_fullscreen_map_btn")
                ) {
                    Icon(
                        Icons.Default.OpenInFull,
                        contentDescription = "Full Screen Map",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Interactive Radar Map Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                ErBayBlueprintCanvas(
                    uiState = uiState,
                    isDetailedFullScreen = false,
                    onTapQuestion = onSelectQuestionOnMap,
                    onTapCrashCart = onOpenCrashCart
                )
            }

            // Legend Footer
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1B2E))
                    .padding(vertical = 3.dp)
            ) {
                Text("Tap Q0–Q3 to collect", color = CapnoAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FullScreenErMapDialog(
    uiState: SimUiState,
    onDismiss: () -> Unit,
    onSelectQuestionOnMap: (QuestionGateType) -> Unit,
    onOpenCrashCart: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ResusNavyBg,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, TelemetryCyan),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("fullscreen_er_map_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "EMERGENCY RESUSCITATION BAY — FULL-SCREEN TACTICAL MAP",
                            color = TelemetryCyan,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Room: ${uiState.activeRoom.roomId} • Active Member: ${uiState.activeRoom.activeMember} • Tap any Question Marker (Q0, Q1, Q2, Q3) or Crash Cart to navigate & interact",
                            color = TextSecondaryMuted,
                            fontSize = 12.sp
                        )
                    }
                    FilledTonalIconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_fullscreen_map_btn")
                    ) {
                        Icon(Icons.Default.CloseFullscreen, contentDescription = "Close Full Screen Map")
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Main Interactive Tactical Blueprint
                    Box(
                        modifier = Modifier
                            .weight(1.6f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0A1424))
                            .border(1.5.dp, ResusBorderBlue, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        ErBayBlueprintCanvas(
                            uiState = uiState,
                            isDetailedFullScreen = true,
                            onTapQuestion = onSelectQuestionOnMap,
                            onTapCrashCart = {
                                onDismiss()
                                onOpenCrashCart()
                            }
                        )
                    }

                    // Right Sidebar: Question Stations List & Status
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "INSTRUCTOR QUESTION STATIONS ON MAP",
                            color = CapnoAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(uiState.getQuestionsForActiveRoom()) { q ->
                                val isUnlocked = uiState.activeRoom.unlockedGates.contains(q.gateType)
                                val crackedBy = uiState.activeRoom.crackedByMember[q.gateType]
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUnlocked) Color(0xFF072E22) else Color(0xFF1E1B12)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isUnlocked) EcgEmerald else CapnoAmber
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectQuestionOnMap(q.gateType) }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${q.gateType.code} • ${q.stationName}",
                                                color = if (isUnlocked) EcgEmerald else CapnoAmber,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = if (isUnlocked) "CRACKED ✓" else "LOCKED",
                                                color = if (isUnlocked) EcgEmerald else CapnoAmber,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = "Unlocks: ${q.gateType.unlocksLabel}",
                                            color = TextPrimaryLight,
                                            fontSize = 11.sp
                                        )
                                        if (crackedBy != null) {
                                            Text(
                                                text = "Solved by: $crackedBy",
                                                color = EcgEmerald,
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
private fun ErBayBlueprintCanvas(
    uiState: SimUiState,
    isDetailedFullScreen: Boolean,
    onTapQuestion: (QuestionGateType) -> Unit,
    onTapCrashCart: () -> Unit
) {
    val questions = uiState.getQuestionsForActiveRoom()
    val room = uiState.activeRoom
    val labelPaint = remember {
        Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(questions) {
                detectTapGestures { tapOffset ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    // Check if tap hit any Question Beacon Q0..Q3
                    val hitQ = questions.minByOrNull { q ->
                        hypot(tapOffset.x - q.mapX * w, tapOffset.y - q.mapY * h)
                    }
                    if (hitQ != null && hypot(tapOffset.x - hitQ.mapX * w, tapOffset.y - hitQ.mapY * h) < 42.dp.toPx()) {
                        onTapQuestion(hitQ.gateType)
                        return@detectTapGestures
                    }
                    // Check if tap hit Crash Cart (Right of Patient adjacent to Ventilator: X=0.78, Y=0.42)
                    if (hypot(tapOffset.x - 0.78f * w, tapOffset.y - 0.42f * h) < 38.dp.toPx()) {
                        onTapCrashCart()
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height
        labelPaint.textSize = if (isDetailedFullScreen) 24f else 15f

        // Background Grid
        val stepX = w / 8f
        val stepY = h / 8f
        for (i in 1..7) {
            drawLine(Color(0xFF1E3A5F).copy(alpha = 0.45f), Offset(i * stepX, 0f), Offset(i * stepX, h), 1f)
            drawLine(Color(0xFF1E3A5F).copy(alpha = 0.45f), Offset(0f, i * stepY), Offset(w, i * stepY), 1f)
        }

        // 1. Center Patient Resuscitation Bed (X: 0.41..0.59, Y: 0.26..0.56)
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(w * 0.41f, h * 0.26f),
            size = Size(w * 0.18f, h * 0.30f),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = TelemetryCyan,
            topLeft = Offset(w * 0.41f, h * 0.26f),
            size = Size(w * 0.18f, h * 0.30f),
            cornerRadius = CornerRadius(8f, 8f),
            style = Stroke(2f)
        )

        // 2. Defibrillator / Monitor (Left of Patient: X: 0.22..0.37, Y: 0.25..0.36)
        drawRoundRect(
            color = Color(0xFF064E3B),
            topLeft = Offset(w * 0.22f, h * 0.25f),
            size = Size(w * 0.15f, h * 0.11f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // 3. ICU Mechanical Ventilator (Right of Patient Head: X: 0.63..0.76, Y: 0.25..0.35)
        drawRoundRect(
            color = Color(0xFF0C4A6E),
            topLeft = Offset(w * 0.63f, h * 0.25f),
            size = Size(w * 0.14f, h * 0.10f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // 4. CODE 100 6-Drawer Crash Cart (Right of Patient Adjacent to Ventilator: X: 0.72..0.88, Y: 0.38..0.52)
        drawRoundRect(
            color = Color(0xFF991B1B),
            topLeft = Offset(w * 0.72f, h * 0.38f),
            size = Size(w * 0.16f, h * 0.14f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = ArterialRed,
            topLeft = Offset(w * 0.72f, h * 0.38f),
            size = Size(w * 0.16f, h * 0.14f),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(2f)
        )

        if (isDetailedFullScreen) {
            drawContext.canvas.nativeCanvas.apply {
                labelPaint.color = android.graphics.Color.WHITE
                drawText("PATIENT BED", w * 0.43f, h * 0.42f, labelPaint)
                labelPaint.color = android.graphics.Color.parseColor("#6EE7B7")
                drawText("DEFIB/MON", w * 0.23f, h * 0.32f, labelPaint)
                labelPaint.color = android.graphics.Color.parseColor("#7DD3FC")
                drawText("VENT", w * 0.65f, h * 0.31f, labelPaint)
                labelPaint.color = android.graphics.Color.parseColor("#FCA5A5")
                drawText("CRASH CART", w * 0.73f, h * 0.46f, labelPaint)
            }
        }

        // 5. Question Markers (Q0 Gate, Q1 Survey, Q2 Labs/Gas, Q3 ECG/Imaging)
        questions.forEach { q ->
            val qx = q.mapX * w
            val qy = q.mapY * h
            val unlocked = room.unlockedGates.contains(q.gateType)
            val markerColor = if (unlocked) EcgEmerald else CapnoAmber
            val r = if (isDetailedFullScreen) 22f else 12f

            drawCircle(markerColor.copy(alpha = 0.28f), radius = r * 1.45f, center = Offset(qx, qy))
            drawCircle(Color(0xFF0A1424), radius = r, center = Offset(qx, qy))
            drawCircle(markerColor, radius = r, center = Offset(qx, qy), style = Stroke(2.5f))

            drawContext.canvas.nativeCanvas.apply {
                labelPaint.color = if (unlocked) {
                    android.graphics.Color.parseColor("#10B981")
                } else {
                    android.graphics.Color.parseColor("#FBBF24")
                }
                drawText(q.gateType.code, qx - r * 0.65f, qy + r * 0.35f, labelPaint)
            }
        }

        // 6. Active Team Member Position & Camera View Cone
        val px = uiState.playerX * w
        val py = uiState.playerY * h
        val yawRad = Math.toRadians(uiState.cameraYawDeg.toDouble()).toFloat()
        val coneLen = if (isDetailedFullScreen) 48f else 24f
        val leftAngle = yawRad - 0.45f
        val rightAngle = yawRad + 0.45f

        val conePath = Path().apply {
            moveTo(px, py)
            lineTo(px + sin(leftAngle) * coneLen, py - cos(leftAngle) * coneLen)
            lineTo(px + sin(rightAngle) * coneLen, py - cos(rightAngle) * coneLen)
            close()
        }
        drawPath(conePath, TelemetryCyan.copy(alpha = 0.32f))
        drawCircle(TelemetryCyan, radius = if (isDetailedFullScreen) 10f else 6f, center = Offset(px, py))
        drawCircle(Color.White, radius = if (isDetailedFullScreen) 4f else 2.5f, center = Offset(px, py))
    }
}

// ============================================================================
// QUESTION CRACKING MODAL (Q0 Gate, Q1 Survey, Q2 Labs/Gas, Q3 ECG/Imaging)
// ============================================================================
@Composable
fun QuestionCrackDialog(
    uiState: SimUiState,
    gateType: QuestionGateType,
    onSelectActiveMember: (String) -> Unit,
    onSubmitAnswer: (QuestionGateType, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val question = uiState.getQuestionsForActiveRoom().first { it.gateType == gateType }
    val room = uiState.activeRoom
    val isUnlocked = room.unlockedGates.contains(gateType)
    var selectedIdx by remember(gateType) { mutableStateOf(if (isUnlocked) question.correctIndex else -1) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ResusSurfaceDark,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, if (isUnlocked) EcgEmerald else CapnoAmber),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("question_crack_dialog")
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = if (isUnlocked) EcgEmerald else CapnoAmber,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = gateType.code,
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Column {
                            Text(
                                text = question.stationName,
                                color = TextPrimaryLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Unlocks: ${gateType.unlocksLabel}",
                                color = TelemetryCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Question")
                    }
                }

                // Team Member Selector (who is cracking this question individually)
                Text(
                    text = "Team Member Attempting ${gateType.code}:",
                    color = TextSecondaryMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(room.members) { member ->
                        val isSelectedMember = member == room.activeMember
                        FilterChip(
                            selected = isSelectedMember,
                            onClick = { onSelectActiveMember(member) },
                            label = { Text(member, fontSize = 11.sp) }
                        )
                    }
                }

                HorizontalDivider(color = ResusBorderBlue)

                // Question Prompt
                Text(
                    text = question.prompt,
                    color = TextPrimaryLight,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                // Multiple Choice Options
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    question.options.forEachIndexed { idx, optText ->
                        val isChosen = selectedIdx == idx
                        Surface(
                            color = if (isChosen) Color(0xFF0E3A53) else ResusCardDark,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isChosen) TelemetryCyan else ResusBorderBlue
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("question_option_$idx")
                                .clickable { selectedIdx = idx }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                RadioButton(
                                    selected = isChosen,
                                    onClick = { selectedIdx = idx }
                                )
                                Text(
                                    text = optText,
                                    color = TextPrimaryLight,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Feedback or Clinical Pearl
                if (uiState.questionAttemptFeedback != null) {
                    Surface(
                        color = Color(0xFF3F111B),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ArterialRed)
                    ) {
                        Text(
                            text = uiState.questionAttemptFeedback,
                            color = Color(0xFFFECDD3),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (isUnlocked) {
                    Surface(
                        color = Color(0xFF063729),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EcgEmerald)
                    ) {
                        Text(
                            text = "Clinical Pearl: ${question.clinicalExplanation}",
                            color = Color(0xFFA7F3D0),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedIdx >= 0) {
                                onSubmitAnswer(gateType, selectedIdx)
                            }
                        },
                        enabled = selectedIdx >= 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcgEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("submit_question_answer_btn")
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Crack ${gateType.code} & Unlock Tablet Access", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// CODE 100 6-DRAWER EMERGENCY CRASH CART INTERACTIVE INSPECTOR
// ============================================================================
@Composable
fun CrashCart6DrawerModal(
    uiState: SimUiState,
    onSelectDrawer: (Int?) -> Unit,
    onTakeItem: (CrashCartItem) -> Unit,
    onUseItemOnPatient: (CrashCartItem) -> Unit,
    onDismiss: () -> Unit
) {
    val drawers = OralBoardRepository.crashCartDrawers
    val openDrawerIdx = uiState.openCrashCartDrawerIndex
    var searchQuery by remember { mutableStateOf("") }
    val room = uiState.activeRoom

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ResusNavyBg,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, ArterialRed),
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .testTag("crash_cart_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = ArterialRed)
                            Text(
                                text = "CODE 100 EMERGENCY CRASH CART (RIGHT OF PATIENT • ADJACENT TO VENTILATOR)",
                                color = TextPrimaryLight,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                        Text(
                            text = "Sources: Govt. of India NHSRC 6-Drawer Resuscitation Trolley (Annexure VII) & Oman MOH Crash-Cart Guidelines (Annex One)",
                            color = TextSecondaryMuted,
                            fontSize = 11.sp
                        )
                    }
                    FilledTonalIconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_crash_cart_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Crash Cart")
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Active Team Member's Held Crash Cart Tray (Taken Items ready to apply on Patient)
                if (room.takenCartItems.isNotEmpty()) {
                    Surface(
                        color = Color(0xFF14273E),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TelemetryCyan),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "${room.activeMember}'s Prepared Bedside Tray (${room.takenCartItems.size} taken — tap 'Apply on Patient'):",
                                color = TelemetryCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(room.takenCartItems) { heldItem ->
                                    val alreadyApplied = room.appliedCartItemsOnPatient.contains(heldItem.name)
                                    Surface(
                                        color = if (alreadyApplied) Color(0xFF064E3B) else ResusCardDark,
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (alreadyApplied) EcgEmerald else CapnoAmber
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = heldItem.name,
                                                color = TextPrimaryLight,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Button(
                                                onClick = { onUseItemOnPatient(heldItem) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (alreadyApplied) EcgEmerald else CapnoAmber,
                                                    contentColor = Color.Black
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(26.dp)
                                            ) {
                                                Text(
                                                    text = if (alreadyApplied) "Applied ✓" else "Use on Patient",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // Search Filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search medications, airway tubes, chest drain, IV fluids, antidotes...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                // 6 Drawers + Top Shelf Accordion / Interactive Drawer Handles
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(drawers) { drawer ->
                        val isOpen = openDrawerIdx == drawer.drawerNumber || searchQuery.isNotBlank()
                        val filteredItems = if (searchQuery.isBlank()) {
                            drawer.items
                        } else {
                            drawer.items.filter {
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                    it.clinicalUseHint.contains(searchQuery, ignoreCase = true) ||
                                    it.specification.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        if (filteredItems.isNotEmpty() || searchQuery.isBlank()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ResusSurfaceDark),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isOpen) 2.dp else 1.dp,
                                    color = Color(drawer.colorHex)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    // Drawer Front Handle Row (Click to Pull Open / Push Closed!)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(drawer.colorHex).copy(alpha = 0.24f))
                                            .testTag("crash_cart_drawer_${drawer.drawerNumber}")
                                            .clickable { onSelectDrawer(drawer.drawerNumber) }
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                color = Color(drawer.colorHex),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = if (drawer.drawerNumber == 0) "TOP SHELF" else "DRAWER ${drawer.drawerNumber}",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = drawer.title,
                                                    color = TextPrimaryLight,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = drawer.subtitle,
                                                    color = TextSecondaryMuted,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Surface(
                                            color = if (isOpen) EcgEmerald.copy(alpha = 0.2f) else Color(0xFF1E293B),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isOpen) EcgEmerald else TextSecondaryMuted
                                            )
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            ) {
                                                Text(
                                                    text = if (isOpen) "DRAWER OPEN" else "PULL TO OPEN",
                                                    color = if (isOpen) EcgEmerald else TextPrimaryLight,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                    contentDescription = null,
                                                    tint = if (isOpen) EcgEmerald else TextPrimaryLight,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Inside of Drawer (Items Grid/List when Open)
                                    AnimatedVisibility(visible = isOpen) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            filteredItems.forEach { item ->
                                                val isTaken = room.takenCartItems.any { it.id == item.id }
                                                val isApplied = room.appliedCartItemsOnPatient.contains(item.name)

                                                Surface(
                                                    color = ResusCardDark,
                                                    shape = RoundedCornerShape(10.dp),
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        1.dp,
                                                        when {
                                                            isApplied -> EcgEmerald
                                                            isTaken -> TelemetryCyan
                                                            else -> ResusBorderBlue
                                                        }
                                                    ),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.padding(12.dp)
                                                    ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                            ) {
                                                                Text(
                                                                    text = item.name,
                                                                    color = TextPrimaryLight,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 13.sp
                                                                )
                                                                Surface(
                                                                    color = Color(0xFF1E3A5F),
                                                                    shape = RoundedCornerShape(4.dp)
                                                                ) {
                                                                    Text(
                                                                        text = "Stock: ${item.quantity}",
                                                                        color = TelemetryCyan,
                                                                        fontSize = 10.sp,
                                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                    )
                                                                }
                                                            }
                                                            Text(
                                                                text = "${item.specification} • [${item.sourceStandard}]",
                                                                color = CapnoAmber,
                                                                fontSize = 11.sp
                                                            )
                                                            Text(
                                                                text = "Clinical Indication: ${item.clinicalUseHint}",
                                                                color = TextSecondaryMuted,
                                                                fontSize = 11.sp
                                                            )
                                                        }

                                                        Spacer(Modifier.width(10.dp))

                                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                            OutlinedButton(
                                                                onClick = { onTakeItem(item) },
                                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                                modifier = Modifier.testTag("take_item_${item.id}")
                                                            ) {
                                                                Text(
                                                                    text = if (isTaken) "In Tray ✓" else "Take",
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                            Button(
                                                                onClick = { onUseItemOnPatient(item) },
                                                                colors = ButtonDefaults.buttonColors(
                                                                    containerColor = if (isApplied) EcgEmerald else ArterialRed,
                                                                    contentColor = if (isApplied) Color.Black else Color.White
                                                                ),
                                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                                modifier = Modifier.testTag("use_item_${item.id}")
                                                            ) {
                                                                Text(
                                                                    text = if (isApplied) "Used on Patient ✓" else "Use on Patient",
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold
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
                }
            }
        }
    }
}
