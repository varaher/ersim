package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.ui.SimUiState
import com.example.ui.theme.*

private enum class AdminSection(val title: String) {
    SCENARIOS_20("1. Choose 20 Oral Board Scenarios & Assign Room"),
    QUESTION_ENGINE("2. Question-Making Engine & Map Placement (Q0–Q3)"),
    TIMER_LEADERBOARD("3. Multi-Room Timers, Tie-Breaker & Live Clues")
}

@Composable
fun InstructorControlRoomDialog(
    uiState: SimUiState,
    initialSectionIsScenarios: Boolean = false,
    onAssignCaseToRoom: (String, Int, String?, List<String>?) -> Unit,
    onCreateNewRoom: (String, String, String, Int) -> Unit,
    onSwitchRoom: (String) -> Unit,
    onSaveCustomQuestion: (QuestionGateType, String, Float, Float, String, List<String>, Int, String) -> Unit,
    onSendInstructorClue: (String) -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    var activeSection by remember(initialSectionIsScenarios) {
        mutableStateOf(
            if (initialSectionIsScenarios) AdminSection.SCENARIOS_20 else AdminSection.QUESTION_ENGINE
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ResusNavyBg,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, CapnoAmber),
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("instructor_control_room_dialog")
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
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CapnoAmber)
                            Text(
                                text = "ADMIN / INSTRUCTOR CONTROL ROOM & QUESTION-MAKING ENGINE",
                                color = CapnoAmber,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                        Text(
                            text = "Assign First 20 Oral Board Cases (2nd Ed. PDF) to Rooms & Members • Place Q0–Q3 on ER Map • Compare Room Timers for Tie-Breaking",
                            color = TextSecondaryMuted,
                            fontSize = 11.sp
                        )
                    }
                    FilledTonalIconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_instructor_control_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Admin Room")
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Section Switcher Tabs
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AdminSection.entries) { section ->
                        val selected = activeSection == section
                        Button(
                            onClick = { activeSection = section },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) CapnoAmber else ResusCardDark,
                                contentColor = if (selected) Color.Black else TextPrimaryLight
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(section.title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (activeSection) {
                        AdminSection.SCENARIOS_20 -> Scenarios20AssignmentPanel(
                            uiState = uiState,
                            onAssignCaseToRoom = onAssignCaseToRoom,
                            onCreateNewRoom = onCreateNewRoom,
                            onSwitchRoom = onSwitchRoom
                        )
                        AdminSection.QUESTION_ENGINE -> QuestionMakingEnginePanel(
                            uiState = uiState,
                            onSaveCustomQuestion = onSaveCustomQuestion
                        )
                        AdminSection.TIMER_LEADERBOARD -> MultiRoomTieBreakerPanel(
                            uiState = uiState,
                            onSwitchRoom = onSwitchRoom,
                            onSendInstructorClue = onSendInstructorClue,
                            onToggleTimer = onToggleTimer,
                            onResetTimer = onResetTimer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Scenarios20AssignmentPanel(
    uiState: SimUiState,
    onAssignCaseToRoom: (String, Int, String?, List<String>?) -> Unit,
    onCreateNewRoom: (String, String, String, Int) -> Unit,
    onSwitchRoom: (String) -> Unit
) {
    val allCases = OralBoardRepository.allCases
    val activeRoom = uiState.activeRoom

    var selectedRoomId by remember(activeRoom.roomId) { mutableStateOf(activeRoom.roomId) }
    var teamNameInput by remember(activeRoom.teamName) { mutableStateOf(activeRoom.teamName) }
    var membersInput by remember(activeRoom.members) {
        mutableStateOf(activeRoom.members.joinToString(", "))
    }
    var newRoomIdInput by remember { mutableStateOf("") }
    var searchFilter by remember { mutableStateOf("") }

    val filteredCases = remember(searchFilter) {
        if (searchFilter.isBlank()) allCases
        else allCases.filter {
            it.scenarioCategory.contains(searchFilter, ignoreCase = true) ||
                it.mainDiagnosis.contains(searchFilter, ignoreCase = true) ||
                it.caseNumber.toString() == searchFilter.trim()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Room ID & Team Members Configuration Card
        Card(
            colors = CardDefaults.cardColors(containerColor = ResusCardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, TelemetryCyan)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TARGET ROOM ID & TEAM ROSTER FOR SCENARIO ASSIGNMENT",
                        color = TelemetryCyan,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(uiState.rooms) { r ->
                            FilterChip(
                                selected = r.roomId == selectedRoomId,
                                onClick = {
                                    selectedRoomId = r.roomId
                                    teamNameInput = r.teamName
                                    membersInput = r.members.joinToString(", ")
                                    onSwitchRoom(r.roomId)
                                },
                                label = { Text("${r.roomId} (Case #${r.assignedCaseNumber})", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = teamNameInput,
                        onValueChange = { teamNameInput = it },
                        label = { Text("Team Name", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(0.9f)
                    )
                    OutlinedTextField(
                        value = membersInput,
                        onValueChange = { membersInput = it },
                        label = { Text("Team Members (comma-separated)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1.6f)
                    )
                    OutlinedTextField(
                        value = newRoomIdInput,
                        onValueChange = { newRoomIdInput = it },
                        label = { Text("Or New Room ID", fontSize = 11.sp) },
                        placeholder = { Text("RESUS-BAY-4", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }
            }
        }

        // Search across the 20 Oral Board Cases
        OutlinedTextField(
            value = searchFilter,
            onValueChange = { searchFilter = it },
            placeholder = { Text("Filter 20 Oral Board Cases by Case # (1–20), Scenario, or Diagnosis (e.g., Iron, Pyloric, Tension, Torsades, Torsion)...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // List of First 20 Oral Board Cases
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredCases) { c ->
                val isCurrentlyAssigned = uiState.activeRoom.assignedCaseNumber == c.caseNumber
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrentlyAssigned) Color(0xFF0A2E3D) else ResusSurfaceDark
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isCurrentlyAssigned) 2.dp else 1.dp,
                        color = if (isCurrentlyAssigned) TelemetryCyan else ResusBorderBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = TelemetryCyan,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "CASE #${c.caseNumber}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "${c.scenarioCategory} — ${c.mainDiagnosis}",
                                    color = TextPrimaryLight,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Patient: ${c.patientName} (${c.ageDesc}) • Initial Vitals: BP ${c.initialVitals.bpDisplay}, HR ${c.initialVitals.hr}, RR ${c.initialVitals.rr}, SpO₂ ${c.initialVitals.spo2 ?: "None"}, T ${c.initialVitals.tempCelsius}°C",
                                color = EcgEmerald,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Critical Actions: ${c.criticalActions.size} • Source Figures: ${c.sourceFiguresCount} • PDF Pages: ${c.pdfPages} • Device Presets: ${c.ventilatorPreset.mode} / ${c.defibrillatorPreset.energyPerKgLabel}",
                                color = CapnoAmber,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Button(
                            onClick = {
                                val parsedMembers = membersInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                if (newRoomIdInput.isNotBlank()) {
                                    onCreateNewRoom(newRoomIdInput, teamNameInput, membersInput, c.caseNumber)
                                    newRoomIdInput = ""
                                } else {
                                    onAssignCaseToRoom(
                                        selectedRoomId,
                                        c.caseNumber,
                                        teamNameInput.ifBlank { null },
                                        parsedMembers.ifEmpty { null }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrentlyAssigned) EcgEmerald else CapnoAmber,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("assign_case_${c.caseNumber}_btn")
                        ) {
                            Text(
                                text = if (isCurrentlyAssigned) "Assigned to $selectedRoomId ✓" else "Assign to $selectedRoomId",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionMakingEnginePanel(
    uiState: SimUiState,
    onSaveCustomQuestion: (QuestionGateType, String, Float, Float, String, List<String>, Int, String) -> Unit
) {
    val questions = uiState.getQuestionsForActiveRoom()
    var selectedGate by remember { mutableStateOf(QuestionGateType.Q0_GATE) }
    val currentQ = questions.first { it.gateType == selectedGate }

    var stationName by remember(currentQ) { mutableStateOf(currentQ.stationName) }
    var mapX by remember(currentQ) { mutableStateOf(currentQ.mapX) }
    var mapY by remember(currentQ) { mutableStateOf(currentQ.mapY) }
    var prompt by remember(currentQ) { mutableStateOf(currentQ.prompt) }
    var opt0 by remember(currentQ) { mutableStateOf(currentQ.options.getOrElse(0) { "" }) }
    var opt1 by remember(currentQ) { mutableStateOf(currentQ.options.getOrElse(1) { "" }) }
    var opt2 by remember(currentQ) { mutableStateOf(currentQ.options.getOrElse(2) { "" }) }
    var opt3 by remember(currentQ) { mutableStateOf(currentQ.options.getOrElse(3) { "" }) }
    var correctIdx by remember(currentQ) { mutableStateOf(currentQ.correctIndex) }
    var explanation by remember(currentQ) { mutableStateOf(currentQ.clinicalExplanation) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "SELECT QUESTION GATE TO CONFIGURE & PLACE ON RESUSCITATION BAY MAP (${uiState.activeRoom.roomId} • Case #${uiState.activeCase.caseNumber})",
                color = TelemetryCyan,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(QuestionGateType.entries) { gate ->
                    val selected = selectedGate == gate
                    Button(
                        onClick = { selectedGate = gate },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) TelemetryCyan else ResusCardDark,
                            contentColor = if (selected) Color.Black else TextPrimaryLight
                        )
                    ) {
                        Text("${gate.code}: ${gate.unlocksLabel.take(28)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CapnoAmber)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Editing ${selectedGate.code} — Unlocks: ${selectedGate.unlocksLabel}",
                        color = CapnoAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = stationName,
                            onValueChange = { stationName = it },
                            label = { Text("Map Station Name") },
                            singleLine = true,
                            modifier = Modifier.weight(1.2f)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Map X Coordinate (${"%.2f".format(mapX)})", color = TextSecondaryMuted, fontSize = 11.sp)
                            Slider(
                                value = mapX,
                                onValueChange = { mapX = it },
                                valueRange = 0.14f..0.86f
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Map Y Coordinate (${"%.2f".format(mapY)})", color = TextSecondaryMuted, fontSize = 11.sp)
                            Slider(
                                value = mapY,
                                onValueChange = { mapY = it },
                                valueRange = 0.18f..0.88f
                            )
                        }
                    }

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text("Instructor Question Prompt for Team Members") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Multiple-Choice Options (Select Radio Button for Correct Answer):",
                        color = TelemetryCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    listOf(
                        Triple(0, opt0) { v: String -> opt0 = v },
                        Triple(1, opt1) { v: String -> opt1 = v },
                        Triple(2, opt2) { v: String -> opt2 = v },
                        Triple(3, opt3) { v: String -> opt3 = v }
                    ).forEach { (idx, value, setter) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = correctIdx == idx,
                                onClick = { correctIdx = idx }
                            )
                            OutlinedTextField(
                                value = value,
                                onValueChange = setter,
                                label = { Text("Option ${'A' + idx} ${if (correctIdx == idx) "(Correct Answer)" else ""}") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = explanation,
                        onValueChange = { explanation = it },
                        label = { Text("Clinical Teaching Pearl / Explanation (shown on unlock)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onSaveCustomQuestion(
                                selectedGate,
                                stationName,
                                mapX,
                                mapY,
                                prompt,
                                listOf(opt0, opt1, opt2, opt3),
                                correctIdx,
                                explanation
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcgEmerald,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_custom_question_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Publish ${selectedGate.code} Question to Resus Bay Map", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiRoomTieBreakerPanel(
    uiState: SimUiState,
    onSwitchRoom: (String) -> Unit,
    onSendInstructorClue: (String) -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit
) {
    var clueText by remember { mutableStateOf("") }
    val room = uiState.activeRoom
    val rankedRooms = remember(uiState.rooms) {
        uiState.rooms.sortedWith(
            compareByDescending<RoomTeamSession> { it.tasksCompletedCount }
                .thenBy { it.elapsedDeciseconds }
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Active Room Timer Controls & Live Instructor Clue Transmitter
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ResusCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, TelemetryCyan)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE ROOM TIMER: ${room.roomId} (${room.teamName})",
                                color = TelemetryCyan,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Elapsed Time: ${room.formattedTimer} • Tasks Completed: ${room.tasksCompletedCount}/${uiState.activeCase.criticalActions.size}",
                                color = EcgEmerald,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onToggleTimer,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (room.isTimerRunning) CapnoAmber else EcgEmerald,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = if (room.isTimerRunning) "Pause Room Timer" else "Resume Room Timer",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            OutlinedButton(onClick = onResetTimer) {
                                Text("Reset Timer")
                            }
                        }
                    }

                    HorizontalDivider(color = ResusBorderBlue)

                    // Broadcast Live Instructor Clue to Virtual Tablet
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = clueText,
                            onValueChange = { clueText = it },
                            label = { Text("Broadcast Live Instructor Clue / Update to Team's Virtual Tablet") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (clueText.isNotBlank()) {
                                    onSendInstructorClue(clueText)
                                    clueText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CapnoAmber,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Send Clue", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Tie-Breaker Multi-Room Leaderboard
        item {
            Text(
                text = "ALL RESUSCITATION ROOMS — TIE-BREAKER RANKING (TASKS COMPLETED + FASTEST TIME WINS)",
                color = CapnoAmber,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
        items(rankedRooms.withIndex().toList()) { (idx, r) ->
            val rCase = OralBoardRepository.getCaseByNumber(r.assignedCaseNumber)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (idx == 0) Color(0xFF1E2912) else ResusCardDark
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (idx == 0) 2.dp else 1.dp,
                    color = if (idx == 0) CapnoAmber else ResusBorderBlue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSwitchRoom(r.roomId) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (idx == 0) "🏆 RANK #1 (LEADER) • ${r.roomId} — ${r.teamName}"
                            else "RANK #${idx + 1} • ${r.roomId} — ${r.teamName}",
                            color = if (idx == 0) CapnoAmber else TextPrimaryLight,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Assigned Case #${r.assignedCaseNumber}: ${rCase.scenarioCategory} (${rCase.mainDiagnosis}) • Members: ${r.members.joinToString(", ")}",
                            color = TextSecondaryMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Questions Cracked: ${r.unlockedGates.joinToString { it.code }.ifEmpty { "None" }}",
                            color = TelemetryCyan,
                            fontSize = 11.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${r.tasksCompletedCount} / ${rCase.criticalActions.size} Tasks",
                            color = EcgEmerald,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Time: ${r.formattedTimer}",
                            color = CapnoAmber,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
