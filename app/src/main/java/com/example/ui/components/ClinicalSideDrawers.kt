package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SideDrawerSide
import com.example.ui.SideDrawerTab
import com.example.ui.SimUiState
import com.example.ui.theme.*

/**
 * Rotates a Composable 90 degrees clockwise (top-to-bottom reading order)
 * while swapping measured width and height so vertical side tabs fit cleanly
 * exactly like the reference clinical simulator screenshots.
 */
fun Modifier.verticalRotate90(): Modifier = this.layout { measurable, constraints ->
    val placeable = measurable.measure(
        constraints.copy(
            minWidth = constraints.minHeight,
            maxWidth = constraints.maxHeight,
            minHeight = constraints.minWidth,
            maxHeight = constraints.maxWidth
        )
    )
    layout(placeable.height, placeable.width) {
        placeable.placeWithLayer(
            x = -(placeable.width - placeable.height) / 2,
            y = (placeable.width - placeable.height) / 2
        ) {
            rotationZ = 90f
        }
    }
}

@Composable
fun VerticalTabRail(
    side: SideDrawerSide,
    activeTab: SideDrawerTab?,
    onSelectTab: (SideDrawerTab) -> Unit,
    showCollapseButton: Boolean = false,
    onCollapse: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tabs = remember(side) { SideDrawerTab.entries.filter { it.side == side } }
    val shape = if (side == SideDrawerSide.LEFT) {
        RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
    } else {
        RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = if (side == SideDrawerSide.LEFT) Alignment.Start else Alignment.End,
        modifier = modifier
    ) {
        tabs.forEach { tab ->
            val isSelected = activeTab == tab
            val tabAccent = when (tab) {
                SideDrawerTab.PATIENT -> ErSimMint
                SideDrawerTab.EXAM -> ErSimViolet
                SideDrawerTab.STABILIZE -> ErSimSky
                SideDrawerTab.DIFFERENTIAL -> ErSimAmber
                SideDrawerTab.INVESTIGATIONS -> ErSimCyan
                SideDrawerTab.INTERVENTIONS -> ErSimRed
                SideDrawerTab.COMMUNICATE -> ErSimOrange
                SideDrawerTab.HANDOFF -> ErSimRose
            }
            Surface(
                color = if (isSelected) tabAccent else ClinicalSheetWhite,
                shape = shape,
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    if (isSelected) tabAccent else tabAccent.copy(alpha = 0.55f)
                ),
                shadowElevation = if (isSelected) 8.dp else 4.dp,
                modifier = Modifier
                    .width(40.dp)
                    .height(104.dp)
                    .testTag("edge_tab_${tab.name.lowercase()}")
                    .clickable { onSelectTab(tab) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) Color(0xFF06151C) else tabAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.verticalRotate90()
                    )
                }
            }
        }

        if (showCollapseButton) {
            Spacer(Modifier.height(6.dp))
            Surface(
                color = ClinicalSheetWhite,
                shape = shape,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .width(40.dp)
                    .height(52.dp)
                    .testTag("collapse_side_drawer_btn")
                    .clickable { onCollapse() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (side == SideDrawerSide.LEFT) Icons.Default.KeyboardDoubleArrowLeft else Icons.Default.KeyboardDoubleArrowRight,
                        contentDescription = "Collapse Panel",
                        tint = ClinicalTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClinicalSideDrawerSheet(
    uiState: SimUiState,
    activeTab: SideDrawerTab,
    onSelectTab: (SideDrawerTab) -> Unit,
    onCollapse: () -> Unit,
    onPerformExamStep: (String) -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onOpenFullScreenTablet: () -> Unit,
    onOpenCrashCartModal: () -> Unit,
    onSelectCrashCartDrawer: (Int?) -> Unit,
    onTakeCrashCartItem: (CrashCartItem) -> Unit,
    onUseCrashCartItem: (CrashCartItem) -> Unit,
    onUpdateVentilator: (Int, Int, Int, Int, Boolean) -> Unit,
    onDeliverShock: (Int, Boolean) -> Unit,
    onToggleCriticalAction: (String) -> Unit,
    onUpdateDomainRating: (Int, Int) -> Unit,
    onSelectTeamMember: (String) -> Unit,
    onSendChatMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLeft = activeTab.side == SideDrawerSide.LEFT

    Row(
        modifier = modifier
            .fillMaxSize()
            .testTag("clinical_side_drawer_${activeTab.name.lowercase()}")
    ) {
        if (!isLeft) {
            // Dimmed peek area on the left side + attached vertical tab rail
            Box(
                modifier = Modifier
                    .weight(0.14f)
                    .fillMaxHeight()
                    .background(Color.Black.copy(alpha = 0.22f))
                    .clickable { onCollapse() }
            )
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier.fillMaxHeight()
            ) {
                VerticalTabRail(
                    side = SideDrawerSide.RIGHT,
                    activeTab = activeTab,
                    onSelectTab = onSelectTab,
                    showCollapseButton = true,
                    onCollapse = onCollapse
                )
            }
        }

        // Crisp White Clinical Sheet (Exact Screenshot 2 visual feel!)
        Surface(
            color = ClinicalSheetWhite,
            shadowElevation = 12.dp,
            modifier = Modifier
                .weight(0.86f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (activeTab) {
                    SideDrawerTab.PATIENT -> PatientDrawerContent(
                        uiState = uiState,
                        onOpenQuestionGate = onOpenQuestionGate,
                        onOpenFullScreenTablet = onOpenFullScreenTablet,
                        onSelectTeamMember = onSelectTeamMember
                    )
                    SideDrawerTab.EXAM -> ExamDrawerContent(
                        uiState = uiState,
                        onPerformExamStep = onPerformExamStep,
                        onOpenQuestionGate = onOpenQuestionGate
                    )
                    SideDrawerTab.STABILIZE -> StabilizeDrawerContent(
                        uiState = uiState,
                        onUpdateVentilator = onUpdateVentilator,
                        onDeliverShock = onDeliverShock,
                        onOpenCrashCartModal = onOpenCrashCartModal
                    )
                    SideDrawerTab.DIFFERENTIAL -> DifferentialDrawerContent(
                        uiState = uiState,
                        onToggleCriticalAction = onToggleCriticalAction
                    )
                    SideDrawerTab.INVESTIGATIONS -> InvestigationsDrawerContent(
                        uiState = uiState,
                        onOpenQuestionGate = onOpenQuestionGate,
                        onOpenFullScreenTablet = onOpenFullScreenTablet
                    )
                    SideDrawerTab.INTERVENTIONS -> InterventionsCrashCartDrawerContent(
                        uiState = uiState,
                        onOpenCrashCartModal = onOpenCrashCartModal,
                        onSelectDrawer = onSelectCrashCartDrawer,
                        onTakeItem = onTakeCrashCartItem,
                        onUseItemOnPatient = onUseCrashCartItem
                    )
                    SideDrawerTab.COMMUNICATE -> CommunicateDrawerContent(
                        uiState = uiState,
                        onOpenQuestionGate = onOpenQuestionGate,
                        onSelectTeamMember = onSelectTeamMember,
                        onSendChatMessage = onSendChatMessage
                    )
                    SideDrawerTab.HANDOFF -> HandoffDrawerContent(
                        uiState = uiState,
                        onUpdateDomainRating = onUpdateDomainRating
                    )
                }
            }
        }

        if (isLeft) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxHeight()
            ) {
                VerticalTabRail(
                    side = SideDrawerSide.LEFT,
                    activeTab = activeTab,
                    onSelectTab = onSelectTab,
                    showCollapseButton = true,
                    onCollapse = onCollapse
                )
            }
            Box(
                modifier = Modifier
                    .weight(0.14f)
                    .fillMaxHeight()
                    .background(Color.Black.copy(alpha = 0.22f))
                    .clickable { onCollapse() }
            )
        }
    }
}

@Composable
private fun ClinicalSectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = ClinicalItalicLabel,
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = ClinicalDividerGray, thickness = 1.dp)
    }
}

@Composable
private fun ClinicalGateLockedBox(
    gateType: QuestionGateType,
    description: String,
    onOpenQuestionGate: (QuestionGateType) -> Unit
) {
    Surface(
        color = ClinicalSurfaceLight,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ClinicalActiveBlue),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(18.dp)
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                tint = ClinicalNavyHeader,
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = "${gateType.code} Question Gate Required",
                color = ClinicalNavyHeader,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = description,
                color = ClinicalTextDark,
                fontSize = 13.sp
            )
            Button(
                onClick = { onOpenQuestionGate(gateType) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClinicalActiveBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("drawer_crack_${gateType.code.lowercase()}_btn")
            ) {
                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text("Crack ${gateType.code} to Unlock", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =========================================================================
// 1. EXAM DRAWER — EXACT MINERVA MEDICAL SCREENSHOT 2 LAYOUT
// =========================================================================
private data class ClinicalExamItem(
    val code: String,
    val label: String,
    val hasAudioIcon: Boolean,
    val finding: String
)

@Composable
private fun ExamDrawerContent(
    uiState: SimUiState,
    onPerformExamStep: (String) -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val q1Unlocked = room.unlockedGates.contains(QuestionGateType.Q1_SURVEY)

    if (!q1Unlocked) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ClinicalSectionHeader("Primary Survey & Physical Exam")
            ClinicalGateLockedBox(
                gateType = QuestionGateType.Q1_SURVEY,
                description = "Team members must locate and crack Question 1 (Q1) in the Resuscitation Bay (or on the top-right ER Map) to unlock the interactive Primary Survey & Physical Exam.",
                onOpenQuestionGate = onOpenQuestionGate
            )
        }
        return
    }

    val examItems = remember(activeCase) {
        val s = activeCase.primarySurvey
        val p = activeCase.physicalExam
        listOf(
            ClinicalExamItem("AIRWAY", "Perform Airway Exam", false, s.airway),
            ClinicalExamItem("BREATH", "Perform Breathing Exam", false, s.breathing),
            ClinicalExamItem("CIRC", "Perform Circulation Exam", false, s.circulation),
            ClinicalExamItem("GENERAL", "General Appearance", false, p.generalAppearance),
            ClinicalExamItem("HEENT", "Perform HEENT & Neck Exam", false, p.heentAndNeck),
            ClinicalExamItem("CARD", "Cardiovascular Exam", true, p.cardiovascular),
            ClinicalExamItem("PULM", "Pulmonary Exam", true, p.chestAndLungs),
            ClinicalExamItem("ABD", "Perform Abdomen, Pelvis & GU Exam", false, p.abdomenAndPelvis),
            ClinicalExamItem("BACK", "Perform Back/Flank Exam", false, s.exposure),
            ClinicalExamItem("SKIN", "Perform Skin & Extremities Exam", false, p.extremitiesAndSkin),
            ClinicalExamItem("NEURO", "Perform Neurological Exam", false, "${s.disability} ${p.neurologic}")
        )
    }

    val gcsText = remember(activeCase) {
        Regex("GCS \\d+").find(activeCase.primarySurvey.disability)?.value ?: "GCS 15"
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ClinicalSectionHeader("General Status")
            Spacer(Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    append("${activeCase.physicalExam.generalAppearance.substringBefore(".")}, ")
                    withStyle(
                        SpanStyle(
                            color = ClinicalNavyHeader,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(gcsText)
                    }
                    append(".")
                },
                color = ClinicalTextDark,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(16.dp))
            ClinicalSectionHeader("Physical Exam")
            Spacer(Modifier.height(6.dp))
        }

        items(examItems) { item ->
            val performed = uiState.performedExamKeys.contains(item.code)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exam_row_${item.code.lowercase()}")
                    .clickable { onPerformExamStep(item.code) }
                    .padding(vertical = 11.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.code,
                        color = ClinicalItalicLabel,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.width(74.dp)
                    )
                    Text(
                        text = item.label,
                        color = if (performed) ClinicalActiveBlue else ClinicalTextDark,
                        fontSize = 15.sp,
                        fontWeight = if (performed) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    if (item.hasAudioIcon) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Auscultation Audio",
                            tint = ClinicalNavyHeader,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    if (performed) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Exam Performed",
                            tint = EcgEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = performed) {
                    Surface(
                        color = ClinicalSurfaceLight,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalDividerGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 74.dp, top = 6.dp)
                    ) {
                        Text(
                            text = item.finding,
                            color = ClinicalTextDark,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// 2. PATIENT DRAWER (Gated by Q0)
// =========================================================================
@Composable
private fun PatientDrawerContent(
    uiState: SimUiState,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onOpenFullScreenTablet: () -> Unit,
    onSelectTeamMember: (String) -> Unit
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val vitals = uiState.currentVitals
    val q0Unlocked = room.unlockedGates.contains(QuestionGateType.Q0_GATE)

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ClinicalSectionHeader("Patient Details & Initial Vitals")
        }

        if (!q0Unlocked) {
            item {
                ClinicalGateLockedBox(
                    gateType = QuestionGateType.Q0_GATE,
                    description = "On entering the resuscitation room, your team must crack the Instructor's Initial Question (Q0) to unlock Patient Details, Initial Vitals, Equipment Parameters, and Virtual Tablet access.",
                    onOpenQuestionGate = onOpenQuestionGate
                )
            }
            return@LazyColumn
        }

        item {
            Text(
                text = "${activeCase.patientName} • ${activeCase.ageDesc} (${activeCase.weightKg} kg)",
                color = ClinicalNavyHeader,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Chief Complaint: ${activeCase.chiefComplaint}",
                color = ClinicalTextDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = activeCase.triageNote,
                color = ClinicalTextMuted,
                fontSize = 13.sp
            )
        }

        item {
            val isPeds = activeCase.isPediatricOrInfant
            val bpStyle = ClinicalSeverityEngine.assessBloodPressure(vitals.systolic, vitals.diastolic, isPeds, onDarkMonitorWell = false)
            val hrStyle = ClinicalSeverityEngine.assessHeartRate(vitals.hr, isPeds, onDarkMonitorWell = false)
            val rrStyle = ClinicalSeverityEngine.assessRespRate(vitals.rr, isPeds, onDarkMonitorWell = false)
            val spo2Style = ClinicalSeverityEngine.assessSpo2(vitals.spo2, onDarkMonitorWell = false)
            val tempStyle = ClinicalSeverityEngine.assessTemp(vitals.tempCelsius, onDarkMonitorWell = false)
            val etco2Style = ClinicalSeverityEngine.assessEtco2(vitals.etco2, onDarkMonitorWell = false)

            ClinicalSectionHeader("Live Vital Signs (${vitals.rhythmLabel})")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                LightVitalChip("BP", vitals.bpDisplay, bpStyle, Modifier.weight(1f))
                LightVitalChip("HR", "${vitals.hr}", hrStyle, Modifier.weight(1f))
                LightVitalChip("RR", "${vitals.rr}", rrStyle, Modifier.weight(1f))
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                LightVitalChip("SpO₂", vitals.spo2?.let { "$it%" } ?: "None", spo2Style, Modifier.weight(1f))
                LightVitalChip("Temp", "${vitals.tempCelsius}°C", tempStyle, Modifier.weight(1f))
                LightVitalChip("EtCO₂", "${vitals.etco2}", etco2Style, Modifier.weight(1f))
            }
        }

        item {
            ClinicalSectionHeader("Active Team Member Role")
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(room.members) { member ->
                    FilterChip(
                        selected = member == room.activeMember,
                        onClick = { onSelectTeamMember(member) },
                        label = { Text(member, fontSize = 11.sp) }
                    )
                }
            }
        }

        item {
            Button(
                onClick = onOpenFullScreenTablet,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErSimCyan,
                    contentColor = Color(0xFF06151C)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drawer_open_fullscreen_tablet_btn")
            ) {
                Icon(Icons.Default.TabletMac, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Open Full-Screen Virtual Tablet", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LightVitalChip(
    label: String,
    value: String,
    style: ClinicalColorStyle,
    modifier: Modifier = Modifier
) {
    Surface(
        color = style.containerTint,
        shape = RoundedCornerShape(9.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (style.isCritical) 1.6.dp else 1.dp,
            style.borderColor
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(label, color = ClinicalTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = style.accent, fontSize = 14.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(2.dp))
            Surface(
                color = if (style.isCritical) style.accent else style.accent.copy(alpha = 0.20f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = style.badgeText,
                    color = if (style.isCritical) Color.White else style.accent,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

// =========================================================================
// 3. STABILIZE DRAWER (Live 3D Ventilator & Defibrillator Parameters)
// =========================================================================
@Composable
private fun StabilizeDrawerContent(
    uiState: SimUiState,
    onUpdateVentilator: (Int, Int, Int, Int, Boolean) -> Unit,
    onDeliverShock: (Int, Boolean) -> Unit,
    onOpenCrashCartModal: () -> Unit
) {
    val activeCase = uiState.activeCase
    val vent = uiState.currentVentilator
    val defib = uiState.currentDefibrillator

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ClinicalSectionHeader("Mechanical Ventilator (Synced with 3D Bay)")
            Spacer(Modifier.height(6.dp))
            Surface(
                color = ClinicalSurfaceLight,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalDividerGray)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Mode: ${vent.mode}",
                        color = ClinicalNavyHeader,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Vt: ${vent.tidalVolumeMlPerKg} mL/kg • Rate: ${vent.respRatePerMin}/min • FiO₂: ${vent.fio2Percent}% • PEEP: ${vent.peepCmH2O} cmH₂O",
                        color = ClinicalTextDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(text = vent.sourceCitationNote, color = ClinicalTextMuted, fontSize = 11.sp)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onUpdateVentilator(
                                    vent.tidalVolumeMlPerKg,
                                    vent.respRatePerMin,
                                    vent.fio2Percent,
                                    vent.peepCmH2O,
                                    !vent.isActive
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (vent.isActive) EcgEmerald else ClinicalActiveBlue
                            )
                        ) {
                            Text(if (vent.isActive) "Ventilator Active ✓" else "Activate Ventilator", fontSize = 12.sp)
                        }
                        if (activeCase.caseNumber == 6) {
                            OutlinedButton(
                                onClick = { onUpdateVentilator(10, 30, 100, 5, true) }
                            ) {
                                Text("Case 6 Preset (10/30/100%)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            ClinicalSectionHeader("Defibrillator & Cardioversion Monitor")
            Spacer(Modifier.height(6.dp))
            Surface(
                color = ClinicalSurfaceLight,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalDividerGray)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Energy Setting: ${defib.energyJoules} J (${defib.energyPerKgLabel})",
                        color = ClinicalNavyHeader,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(text = defib.sourceCitationNote, color = ClinicalTextMuted, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val joules = if (activeCase.caseNumber == 17) (4 * activeCase.weightKg).toInt() else defib.energyJoules
                                onDeliverShock(joules, false)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DefibOrange)
                        ) {
                            Text(
                                text = if (activeCase.caseNumber == 17) "Deliver 4 J/kg Shock (180 J)" else "Deliver Defib Shock",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            ClinicalSectionHeader("Airway, Fluids & Crash Cart Access")
            Spacer(Modifier.height(6.dp))
            Button(
                onClick = onOpenCrashCartModal,
                colors = ButtonDefaults.buttonColors(containerColor = ArterialRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Open CODE 100 6-Drawer Crash Cart", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =========================================================================
// 4. DIFFERENTIAL & CRITICAL ACTIONS CHECKLIST
// =========================================================================
@Composable
private fun DifferentialDrawerContent(
    uiState: SimUiState,
    onToggleCriticalAction: (String) -> Unit
) {
    val activeCase = uiState.activeCase
    val room = uiState.activeRoom

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            ClinicalSectionHeader("Oral Board Critical Actions (${room.tasksCompletedCount}/${activeCase.criticalActions.size})")
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Based on Emergency Medicine Oral Board Review Illustrated (2nd Ed., pp. ${activeCase.pdfPages})",
                color = ClinicalTextMuted,
                fontSize = 12.sp
            )
        }

        items(activeCase.criticalActions) { action ->
            val done = room.completedCriticalActionIds.contains(action.id)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (done) EcgEmerald.copy(alpha = 0.15f) else ClinicalSurfaceLight)
                    .clickable { onToggleCriticalAction(action.id) }
                    .padding(10.dp)
            ) {
                Checkbox(checked = done, onCheckedChange = { onToggleCriticalAction(action.id) })
                Spacer(Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${action.order}. ${action.description}",
                        color = ClinicalTextDark,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${action.domain.title} • +${action.points} pts",
                        color = ClinicalActiveBlue,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(6.dp))
            ClinicalSectionHeader("Target Diagnosis & Disposition")
            Spacer(Modifier.height(6.dp))
            Text(
                text = activeCase.mainDiagnosis,
                color = ClinicalNavyHeader,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = activeCase.dispositionAndPearls,
                color = ClinicalTextDark,
                fontSize = 13.sp
            )
        }
    }
}

// =========================================================================
// 5. INVESTIGATIONS DRAWER (Labs/ABG gated by Q2 + ECG/Imaging gated by Q3)
// =========================================================================
@Composable
private fun InvestigationsDrawerContent(
    uiState: SimUiState,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onOpenFullScreenTablet: () -> Unit
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val q2Unlocked = room.unlockedGates.contains(QuestionGateType.Q2_LABS)
    val q3Unlocked = room.unlockedGates.contains(QuestionGateType.Q3_IMAGING)

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ClinicalSectionHeader("Laboratory & ABG / VBG Blood Gas Report (Q2)")
        }

        if (!q2Unlocked) {
            item {
                ClinicalGateLockedBox(
                    gateType = QuestionGateType.Q2_LABS,
                    description = "Crack Question 2 (Q2) placed in the Resuscitation Bay to unlock CBC, Chemistry, Toxicology, and ABG/VBG Blood Gas results.",
                    onOpenQuestionGate = onOpenQuestionGate
                )
            }
        } else {
            val allLabs = activeCase.abgVbgReports + activeCase.labReports
            val critCount = allLabs.count { it.flag == LabFlag.CRITICAL }
            val highCount = allLabs.count { it.flag == LabFlag.HIGH }
            val lowCount = allLabs.count { it.flag == LabFlag.LOW }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (critCount > 0) {
                        Surface(color = ErSimRed, shape = RoundedCornerShape(6.dp)) {
                            Text("$critCount CRIT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    if (highCount > 0) {
                        Surface(color = ErSimAmber.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                            Text("$highCount HIGH", color = ErSimAmber, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    if (lowCount > 0) {
                        Surface(color = ErSimSky.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                            Text("$lowCount LOW", color = ErSimSky, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
            items(allLabs) { lab ->
                val style = ClinicalSeverityEngine.labStyle(lab.flag, onDarkMonitorWell = false)
                Surface(
                    color = style.containerTint,
                    shape = RoundedCornerShape(9.dp),
                    border = androidx.compose.foundation.BorderStroke(
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    lab.testName,
                                    color = ClinicalTextDark,
                                    fontWeight = if (style.isCritical) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text("${lab.category} • Ref: ${lab.referenceRange}", color = ClinicalTextMuted, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${lab.value} ${lab.unit}".trim(),
                                    color = style.accent,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.5.sp
                                )
                                Surface(
                                    color = if (style.isCritical) style.accent else style.accent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = style.badgeText,
                                        color = if (style.isCritical) Color.White else style.accent,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            ClinicalSectionHeader("12-Lead ECG & Diagnostic Imaging (CT/MRI/USG/X-Ray) (Q3)")
        }

        if (!q3Unlocked) {
            item {
                ClinicalGateLockedBox(
                    gateType = QuestionGateType.Q3_IMAGING,
                    description = "Crack Question 3 (Q3) placed in the Resuscitation Bay to view 12-Lead ECG strips and CT / MRI / Ultrasound / X-Ray figures from the PDF.",
                    onOpenQuestionGate = onOpenQuestionGate
                )
            }
        } else {
            items(activeCase.imagingAndEcg) { study ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${study.modality}: ${study.title}",
                        color = ClinicalNavyHeader,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    ClinicalImagingFigureViewer(study = study)
                    Text(study.findings, color = ClinicalTextDark, fontSize = 12.sp)
                    Text("Impression: ${study.impression}", color = EcgEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            item {
                OutlinedButton(
                    onClick = onOpenFullScreenTablet,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Inspect Full-Screen on Virtual Tablet")
                }
            }
        }
    }
}

// =========================================================================
// 6. INTERVENTIONS DRAWER — INLINE CODE 100 6-DRAWER CRASH CART
// =========================================================================
@Composable
private fun InterventionsCrashCartDrawerContent(
    uiState: SimUiState,
    onOpenCrashCartModal: () -> Unit,
    onSelectDrawer: (Int?) -> Unit,
    onTakeItem: (CrashCartItem) -> Unit,
    onUseItemOnPatient: (CrashCartItem) -> Unit
) {
    val drawers = OralBoardRepository.crashCartDrawers
    val openIdx = uiState.openCrashCartDrawerIndex
    val room = uiState.activeRoom

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            ClinicalSectionHeader("CODE 100 6-Drawer Emergency Crash Cart")
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pull drawers open to Take & Use items on patient",
                    color = ClinicalTextMuted,
                    fontSize = 12.sp
                )
                TextButton(onClick = onOpenCrashCartModal) {
                    Text("Full View", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(drawers) { drawer ->
            val isOpen = openIdx == drawer.drawerNumber
            Surface(
                color = ClinicalSurfaceLight,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(drawer.colorHex)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(drawer.colorHex).copy(alpha = 0.14f))
                            .clickable { onSelectDrawer(drawer.drawerNumber) }
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = drawer.title,
                                color = ClinicalTextDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(text = drawer.subtitle, color = ClinicalTextMuted, fontSize = 10.sp)
                        }
                        Icon(
                            imageVector = if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Drawer",
                            tint = Color(drawer.colorHex)
                        )
                    }

                    AnimatedVisibility(visible = isOpen) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(10.dp)
                        ) {
                            drawer.items.forEach { item ->
                                val isApplied = room.appliedCartItemsOnPatient.contains(item.name)
                                Surface(
                                    color = ClinicalSheetWhite,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isApplied) EcgEmerald else ClinicalDividerGray
                                    )
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(10.dp)
                                    ) {
                                        Text(item.name, color = ClinicalTextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${item.specification} • ${item.clinicalUseHint}", color = ClinicalTextMuted, fontSize = 11.sp)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(
                                                onClick = { onTakeItem(item) },
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text("Take", fontSize = 11.sp)
                                            }
                                            Button(
                                                onClick = { onUseItemOnPatient(item) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isApplied) EcgEmerald else ArterialRed
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Text(if (isApplied) "Applied ✓" else "Use on Patient", fontSize = 11.sp)
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

// =========================================================================
// 7. COMMUNICATE DRAWER (Team Chat, Clues & Q0-Q3 Gate Tracker)
// =========================================================================
@Composable
private fun CommunicateDrawerContent(
    uiState: SimUiState,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onSelectTeamMember: (String) -> Unit,
    onSendChatMessage: (String) -> Unit
) {
    val room = uiState.activeRoom
    var draft by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
        ClinicalSectionHeader("Map Question Gates (Q0, Q1, Q2, Q3)")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            QuestionGateType.entries.forEach { gate ->
                val done = room.unlockedGates.contains(gate)
                OutlinedButton(
                    onClick = { onOpenQuestionGate(gate) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (done) "${gate.code} ✓" else gate.code,
                        color = if (done) EcgEmerald else ClinicalNavyHeader,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        ClinicalSectionHeader("Team Role & Communication Log")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(room.members) { member ->
                FilterChip(
                    selected = member == room.activeMember,
                    onClick = { onSelectTeamMember(member) },
                    label = { Text(member, fontSize = 10.sp) }
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
            items(room.chatMessages) { msg ->
                Surface(
                    color = ClinicalSurfaceLight,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalDividerGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "${msg.sender} • ${msg.timestampLabel}",
                            color = ClinicalActiveBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(msg.text, color = ClinicalTextDark, fontSize = 12.sp)
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Speak to team / nurse...", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    if (draft.isNotBlank()) {
                        onSendChatMessage(draft)
                        draft = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = ClinicalActiveBlue)
            }
        }
    }
}

// =========================================================================
// 8. HANDOFF DRAWER (Disposition, 1–8 Ratings & Multi-Room Tie-Breaker)
// =========================================================================
@Composable
private fun HandoffDrawerContent(
    uiState: SimUiState,
    onUpdateDomainRating: (Int, Int) -> Unit
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val rankedRooms = remember(uiState.rooms) {
        uiState.rooms.sortedWith(
            compareByDescending<RoomTeamSession> { it.tasksCompletedCount }
                .thenBy { it.elapsedDeciseconds }
        )
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            ClinicalSectionHeader("Multi-Room Timer & Tie-Breaker Standings")
            Spacer(Modifier.height(6.dp))
            rankedRooms.forEachIndexed { idx, r ->
                val rc = OralBoardRepository.getCaseByNumber(r.assignedCaseNumber)
                Surface(
                    color = if (idx == 0) CapnoAmber.copy(alpha = 0.15f) else ClinicalSurfaceLight,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (idx == 0) CapnoAmber else ClinicalDividerGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "#${idx + 1} ${r.roomId} (${r.teamName})",
                                color = ClinicalNavyHeader,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text("Case #${r.assignedCaseNumber}: ${rc.scenarioCategory}", color = ClinicalTextMuted, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${r.tasksCompletedCount}/${rc.criticalActions.size} Tasks", color = EcgEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(r.formattedTimer, color = ClinicalActiveBlue, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            ClinicalSectionHeader("Disposition & Specialist Handoff")
            Spacer(Modifier.height(4.dp))
            Text(activeCase.dispositionAndPearls, color = ClinicalTextDark, fontSize = 13.sp)
        }

        item {
            ClinicalSectionHeader("Oral Board 1–8 Performance Domain Ratings")
            Spacer(Modifier.height(4.dp))
            OralBoardDomain.entries.forEach { dom ->
                val score = room.domainRatings[dom.number] ?: 6
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(dom.title, color = ClinicalTextDark, fontSize = 11.sp, modifier = Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(2, 4, 6, 8).forEach { v ->
                            Surface(
                                color = if (score == v) ClinicalActiveBlue else ClinicalSurfaceLight,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { onUpdateDomainRating(dom.number, v) }
                            ) {
                                Text(
                                    text = "$v",
                                    color = if (score == v) Color.White else ClinicalTextDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
