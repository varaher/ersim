package com.example.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SimUiState
import com.example.ui.TabletTab
import com.example.ui.theme.*

@Composable
fun VirtualTabletOverlay(
    uiState: SimUiState,
    onSelectTab: (TabletTab) -> Unit,
    onToggleFullScreen: () -> Unit,
    onCloseTablet: () -> Unit,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onUpdateVentilator: (Int, Int, Int, Int, Boolean) -> Unit,
    onDeliverShock: (Int, Boolean) -> Unit,
    onToggleCriticalAction: (String) -> Unit,
    onUpdateDomainRating: (Int, Int) -> Unit,
    onSendChatMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase
    val q0Unlocked = room.unlockedGates.contains(QuestionGateType.Q0_GATE)
    val isFull = uiState.isTabletFullScreen

    val containerModifier = if (isFull) {
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(8.dp)
    } else {
        modifier
            .fillMaxWidth(0.95f)
            .fillMaxHeight(0.68f)
            .navigationBarsPadding()
            .padding(bottom = 12.dp)
    }

    Surface(
        color = Color(0xFF0A121E).copy(alpha = 0.97f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(2.5.dp, TelemetryCyan),
        shadowElevation = 16.dp,
        modifier = containerModifier.testTag("virtual_tablet_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Virtual Tablet Bezel Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF102035))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.TabletMac,
                        contentDescription = null,
                        tint = TelemetryCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "RESUS-PAD PRO • ${room.roomId} (${room.teamName})",
                            color = TextPrimaryLight,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Operator: ${room.activeMember} • Case #${activeCase.caseNumber}: ${activeCase.scenarioCategory} • Timer: ${room.formattedTimer}",
                            color = TelemetryCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Full-Screen Option Button for Virtual Tablet!
                    FilledTonalButton(
                        onClick = onToggleFullScreen,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("tablet_fullscreen_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isFull) Icons.Default.CloseFullscreen else Icons.Default.OpenInFull,
                            contentDescription = "Toggle Full Screen Tablet",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isFull) "Dock Tablet" else "Full Screen",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onCloseTablet,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_virtual_tablet_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Virtual Tablet", tint = TextPrimaryLight)
                    }
                }
            }

            // If Q0 (Entrance Gate Question) is NOT cracked yet, lock the entire Virtual Tablet!
            if (!q0Unlocked) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ResusSurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(2.dp, CapnoAmber),
                        modifier = Modifier.widthIn(max = 520.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = CapnoAmber,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "VIRTUAL TABLET, PATIENT DETAILS & INITIAL VITALS LOCKED",
                                color = CapnoAmber,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Upon entering the Resuscitation Bay, team members must first crack the Instructor's Initial Question (Q0) to unlock limited patient details, initial patient vitals, equipment parameters, and Virtual Tablet access.",
                                color = TextPrimaryLight,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "After unlocking Q0, team members individually collect and crack Q1 (Survey & Exam), Q2 (Lab & Blood Gas), and Q3 (ECG & Imaging) placed on the Resuscitation Bay map.",
                                color = TextSecondaryMuted,
                                fontSize = 12.sp
                            )
                            Button(
                                onClick = { onOpenQuestionGate(QuestionGateType.Q0_GATE) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CapnoAmber,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.testTag("tablet_unlock_q0_btn")
                            ) {
                                Icon(Icons.Default.Quiz, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Crack Initial Gate Question (Q0) Now", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            } else {
                // Tablet Navigation Tabs (with Lock Badges on Q1, Q2, Q3 if not cracked yet)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D1829))
                ) {
                    items(TabletTab.entries) { tab ->
                        val isTabUnlocked = room.unlockedGates.contains(tab.requiredGate)
                        val isSelected = uiState.selectedTabletTab == tab
                        Surface(
                            color = when {
                                isSelected -> TelemetryCyan
                                isTabUnlocked -> ResusCardDark
                                else -> Color(0xFF281E12)
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when {
                                    isSelected -> Color.White
                                    isTabUnlocked -> ResusBorderBlue
                                    else -> CapnoAmber
                                }
                            ),
                            modifier = Modifier
                                .testTag("tablet_tab_${tab.name.lowercase()}")
                                .clickable { onSelectTab(tab) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTabUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = when {
                                        isSelected -> Color.Black
                                        isTabUnlocked -> EcgEmerald
                                        else -> CapnoAmber
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = tab.title,
                                    color = if (isSelected) Color.Black else TextPrimaryLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Active Tab Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    when (uiState.selectedTabletTab) {
                        TabletTab.PATIENT_VITALS -> TabletPatientAndVitalsTab(
                            uiState = uiState,
                            onOpenQuestionGate = onOpenQuestionGate,
                            onUpdateVentilator = onUpdateVentilator,
                            onDeliverShock = onDeliverShock
                        )
                        TabletTab.SURVEY_EXAM -> TabletSurveyAndExamTab(activeCase = activeCase)
                        TabletTab.LABS_ABG -> TabletLabsAndAbgTab(activeCase = activeCase)
                        TabletTab.ECG_IMAGING -> TabletEcgAndImagingTab(activeCase = activeCase)
                        TabletTab.TEAM_COMMS -> TabletCluesAndChatTab(
                            uiState = uiState,
                            onSendChatMessage = onSendChatMessage
                        )
                        TabletTab.ANALYTICS -> TabletTasksAndAnalyticsTab(
                            uiState = uiState,
                            onToggleCriticalAction = onToggleCriticalAction,
                            onUpdateDomainRating = onUpdateDomainRating
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabletPatientAndVitalsTab(
    uiState: SimUiState,
    onOpenQuestionGate: (QuestionGateType) -> Unit,
    onUpdateVentilator: (Int, Int, Int, Int, Boolean) -> Unit,
    onDeliverShock: (Int, Boolean) -> Unit
) {
    val activeCase = uiState.activeCase
    val vitals = uiState.currentVitals
    val room = uiState.activeRoom
    val vent = uiState.currentVentilator
    val defib = uiState.currentDefibrillator

    var vtInput by remember(vent.tidalVolumeMlPerKg) { mutableStateOf(vent.tidalVolumeMlPerKg) }
    var rrInput by remember(vent.respRatePerMin) { mutableStateOf(vent.respRatePerMin) }
    var fio2Input by remember(vent.fio2Percent) { mutableStateOf(vent.fio2Percent) }
    var defibJoules by remember(defib.energyJoules) { mutableStateOf(defib.energyJoules) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 1. Patient Brief & Triage Handoff
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, TelemetryCyan)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "CASE #${activeCase.caseNumber}: ${activeCase.scenarioCategory.uppercase()} (PDF pp. ${activeCase.pdfPages})",
                            color = TelemetryCyan,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Surface(
                            color = if (room.isPatientStabilized) EcgEmerald else ArterialRed,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (room.isPatientStabilized) "STABILIZED" else "ACUTE RESUS",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Patient: ${activeCase.patientName} • ${activeCase.ageDesc} • Weight: ${activeCase.weightKg} kg",
                        color = TextPrimaryLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Chief Complaint: ${activeCase.chiefComplaint}",
                        color = CapnoAmber,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "EMS / Triage Note: ${activeCase.triageNote}",
                        color = TextSecondaryMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 2. Initial & Live Patient Vitals Grid
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1727)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcgEmerald)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "INITIAL & LIVE TELEMETRY VITALS (SYNCED WITH 3D MONITOR)",
                        color = EcgEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        VitalMetricBox("BP (mmHg)", vitals.bpDisplay, ArterialRed, Modifier.weight(1f))
                        VitalMetricBox("HR (bpm)", "${vitals.hr}", EcgEmerald, Modifier.weight(1f))
                        VitalMetricBox("RR (/min)", "${vitals.rr}", TelemetryCyan, Modifier.weight(1f))
                        VitalMetricBox("SpO₂ (%)", vitals.spo2?.let { "$it%" } ?: "None", Spo2Sky, Modifier.weight(1f))
                        VitalMetricBox("Temp (°C)", "${vitals.tempCelsius}°C", CapnoAmber, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Cardiac Rhythm: ${vitals.rhythmLabel} • EtCO₂: ${vitals.etco2} mmHg",
                        color = TextSecondaryMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 3. Remaining Map Questions Status Bar (Q1, Q2, Q3)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CapnoAmber)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "INDIVIDUAL TEAM QUESTION GATES IN RESUS BAY (TAP TO COLLECT & CRACK)",
                        color = CapnoAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            QuestionGateType.Q1_SURVEY,
                            QuestionGateType.Q2_LABS,
                            QuestionGateType.Q3_IMAGING
                        ).forEach { gate ->
                            val unlocked = room.unlockedGates.contains(gate)
                            OutlinedButton(
                                onClick = { onOpenQuestionGate(gate) },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.2.dp,
                                    if (unlocked) EcgEmerald else CapnoAmber
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (unlocked) "${gate.code} Cracked ✓" else "Crack ${gate.code}",
                                    color = if (unlocked) EcgEmerald else CapnoAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Live 3D Equipment Parameter Controls (ICU Mechanical Ventilator & Defibrillator)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Ventilator Controls
                Card(
                    colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TelemetryCyan),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "3D ICU VENTILATOR PARAMETERS",
                            color = TelemetryCyan,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = vent.sourceCitationNote,
                            color = CapnoAmber,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Vt: $vtInput mL/kg • Rate: $rrInput/min • FiO₂: $fio2Input%",
                            color = TextPrimaryLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    // Case 6 explicit preset button or toggle
                                    onUpdateVentilator(vtInput, rrInput, fio2Input, vent.peepCmH2O, !vent.isActive)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (vent.isActive) "Ventilator ON ✓" else "Engage Ventilator",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (activeCase.caseNumber == 6) {
                                Button(
                                    onClick = {
                                        vtInput = 10
                                        rrInput = 30
                                        fio2Input = 100
                                        onUpdateVentilator(10, 30, 100, 5, true)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EcgEmerald, contentColor = Color.Black),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Case 6 PDF (10mL/kg, 30, 100%)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Defibrillator Controls
                Card(
                    colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DefibOrange),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "3D DEFIBRILLATOR / MONITOR",
                            color = DefibOrange,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = defib.sourceCitationNote,
                            color = CapnoAmber,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Charge: $defibJoules J (${defib.energyPerKgLabel})",
                            color = TextPrimaryLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    val targetJ = if (activeCase.caseNumber == 17) (4 * activeCase.weightKg).toInt() else defibJoules
                                    defibJoules = targetJ
                                    onDeliverShock(targetJ, false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DefibOrange, contentColor = Color.Black),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (activeCase.caseNumber == 17) "Shock 4 J/kg (180 J)" else "Deliver Shock",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VitalMetricBox(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF102035),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.65f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(text = label, color = TextSecondaryMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Text(text = value, color = accent, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun TabletSurveyAndExamTab(activeCase: OralBoardCase) {
    val survey = activeCase.primarySurvey
    val exam = activeCase.physicalExam
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcgEmerald)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "PRIMARY SURVEY (ABCDE) — UNLOCKED BY Q1",
                        color = EcgEmerald,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    SurveyLine("A • Airway", survey.airway)
                    SurveyLine("B • Breathing", survey.breathing)
                    SurveyLine("C • Circulation", survey.circulation)
                    SurveyLine("D • Disability (Neuro / GCS / Pupils / POC Glucose)", survey.disability)
                    SurveyLine("E • Exposure & Environmental", survey.exposure)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, TelemetryCyan)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "SECONDARY PHYSICAL EXAMINATION CLUES (PDF pp. ${activeCase.pdfPages})",
                        color = TelemetryCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    SurveyLine("General Appearance", exam.generalAppearance)
                    SurveyLine("HEENT & Neck", exam.heentAndNeck)
                    SurveyLine("Chest & Lungs", exam.chestAndLungs)
                    SurveyLine("Cardiovascular", exam.cardiovascular)
                    SurveyLine("Abdomen, Pelvis & GU", exam.abdomenAndPelvis)
                    SurveyLine("Extremities & Skin", exam.extremitiesAndSkin)
                    SurveyLine("Neurologic", exam.neurologic)
                }
            }
        }
    }
}

@Composable
private fun SurveyLine(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, color = CapnoAmber, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text(text = value, color = TextPrimaryLight, fontSize = 12.sp)
    }
}

@Composable
private fun TabletLabsAndAbgTab(activeCase: OralBoardCase) {
    val allLabs = activeCase.abgVbgReports + activeCase.labReports
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text(
                text = "LABORATORY & ARTERIAL/VENOUS BLOOD GAS REPORT (UNLOCKED BY Q2 • Case #${activeCase.caseNumber})",
                color = TelemetryCyan,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
        items(allLabs) { lab ->
            val flagColor = when (lab.flag) {
                LabFlag.CRITICAL -> ArterialRed
                LabFlag.HIGH -> CapnoAmber
                LabFlag.LOW -> Spo2Sky
                LabFlag.NORMAL -> EcgEmerald
            }
            Surface(
                color = ResusCardDark,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, flagColor.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(text = lab.testName, color = TextPrimaryLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "${lab.category} • Ref: ${lab.referenceRange}", color = TextSecondaryMuted, fontSize = 11.sp)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${lab.value} ${lab.unit}".trim(),
                            color = flagColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Surface(
                            color = flagColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = lab.flag.name,
                                color = flagColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabletEcgAndImagingTab(activeCase: OralBoardCase) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(
                text = "12-LEAD ECG & DIAGNOSTIC IMAGING (CT / MRI / USG / X-RAY) — UNLOCKED BY Q3 (${activeCase.sourceFiguresCount} Source Figures • PDF pp. ${activeCase.pdfPages})",
                color = TelemetryCyan,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
        items(activeCase.imagingAndEcg) { study ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, TelemetryCyan)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${study.modality}: ${study.title}",
                            color = TextPrimaryLight,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = study.figureRef,
                            color = CapnoAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    ClinicalImagingFigureViewer(study = study)

                    Text(
                        text = "Radiology / Telemetry Findings: ${study.findings}",
                        color = TextPrimaryLight,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Clinical Impression: ${study.impression}",
                        color = EcgEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TabletCluesAndChatTab(
    uiState: SimUiState,
    onSendChatMessage: (String) -> Unit
) {
    val activeCase = uiState.activeCase
    val room = uiState.activeRoom
    var draft by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Instructor Clues Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F180D)),
            border = androidx.compose.foundation.BorderStroke(1.dp, CapnoAmber)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "INSTRUCTOR CLUES & ORAL BOARD PEARLS (Case #${activeCase.caseNumber})",
                    color = CapnoAmber,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
                activeCase.instructorClues.forEach { clue ->
                    Text(text = "• $clue", color = TextPrimaryLight, fontSize = 12.sp)
                }
            }
        }

        // Team Communication Log
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(room.chatMessages) { msg ->
                val badgeColor = when (msg.roleTag) {
                    "INSTRUCTOR" -> CapnoAmber
                    "CRASH_CART" -> ArterialRed
                    "TEAM" -> TelemetryCyan
                    else -> EcgEmerald
                }
                Surface(
                    color = ResusCardDark,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${msg.sender} [${msg.roleTag}]",
                                color = badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = msg.timestampLabel,
                                color = TextSecondaryMuted,
                                fontSize = 10.sp
                            )
                        }
                        Text(text = msg.text, color = TextPrimaryLight, fontSize = 12.sp)
                    }
                }
            }
        }

        // Send Team Comms Input
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Message team as ${room.activeMember}...", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (draft.isNotBlank()) {
                        onSendChatMessage(draft)
                        draft = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
private fun TabletTasksAndAnalyticsTab(
    uiState: SimUiState,
    onToggleCriticalAction: (String) -> Unit,
    onUpdateDomainRating: (Int, Int) -> Unit
) {
    val activeCase = uiState.activeCase
    val room = uiState.activeRoom

    // Sort all rooms for Tie-Breaker Leaderboard:
    // 1. Tasks Completed Count (DESC)
    // 2. Elapsed Time in Deciseconds (ASC — faster time wins a tie!)
    val rankedRooms = remember(uiState.rooms) {
        uiState.rooms.sortedWith(
            compareByDescending<RoomTeamSession> { it.tasksCompletedCount }
                .thenBy { it.elapsedDeciseconds }
        )
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 1. Multi-Room Tie-Breaker Timer Leaderboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1D2E)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CapnoAmber)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "MULTI-ROOM TIMER & TIE-BREAKER WINNER LEADERBOARD",
                        color = CapnoAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Tie-Breaker Rule: When 2 teams complete the same number of critical actions, the room with the faster completion time wins!",
                        color = TextSecondaryMuted,
                        fontSize = 11.sp
                    )
                    rankedRooms.forEachIndexed { idx, r ->
                        val rCase = OralBoardRepository.getCaseByNumber(r.assignedCaseNumber)
                        Surface(
                            color = if (r.roomId == room.roomId) Color(0xFF13334C) else ResusCardDark,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (idx == 0) CapnoAmber else ResusBorderBlue
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "#${idx + 1} • ${r.roomId} (${r.teamName})",
                                        color = if (idx == 0) CapnoAmber else TextPrimaryLight,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Case #${r.assignedCaseNumber}: ${rCase.scenarioCategory} (${rCase.mainDiagnosis})",
                                        color = TextSecondaryMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Tasks: ${r.tasksCompletedCount}/${rCase.criticalActions.size}",
                                        color = EcgEmerald,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Time: ${r.formattedTimer}",
                                        color = TelemetryCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Oral Board Critical Actions Checklist (Points from PDF)
        item {
            Text(
                text = "CASE #${activeCase.caseNumber} CRITICAL ACTIONS CHECKLIST (${room.tasksCompletedCount}/${activeCase.criticalActions.size} COMPLETED • PDF pp. ${activeCase.pdfPages})",
                color = EcgEmerald,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
        items(activeCase.criticalActions) { action ->
            val done = room.completedCriticalActionIds.contains(action.id)
            Surface(
                color = if (done) Color(0xFF063729) else ResusCardDark,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (done) EcgEmerald else ResusBorderBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleCriticalAction(action.id) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    Checkbox(
                        checked = done,
                        onCheckedChange = { onToggleCriticalAction(action.id) }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Action #${action.order}: ${action.description}",
                            color = TextPrimaryLight,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${action.domain.title} • +${action.points} pts",
                            color = TelemetryCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 3. Final Diagnosis & Disposition Pearl
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, TelemetryCyan)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "ORAL BOARD MAIN DIAGNOSIS & DISPOSITION",
                        color = TelemetryCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Main Diagnosis: ${activeCase.mainDiagnosis}",
                        color = EcgEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Disposition: ${activeCase.dispositionAndPearls}",
                        color = TextPrimaryLight,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 4. 1–8 Oral Board Performance Domains Rating
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, ResusBorderBlue)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "ABEM ORAL BOARD 1–8 PERFORMANCE DOMAIN RATINGS",
                        color = CapnoAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                    OralBoardDomain.entries.forEach { dom ->
                        val score = room.domainRatings[dom.number] ?: 6
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = dom.title,
                                color = TextPrimaryLight,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(2, 4, 6, 8).forEach { ratingVal ->
                                    Surface(
                                        color = if (score == ratingVal) EcgEmerald else Color(0xFF1E293B),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.clickable {
                                            onUpdateDomainRating(dom.number, ratingVal)
                                        }
                                    ) {
                                        Text(
                                            text = "$ratingVal/8",
                                            color = if (score == ratingVal) Color.Black else TextPrimaryLight,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
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
