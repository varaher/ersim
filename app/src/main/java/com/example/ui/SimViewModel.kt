package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.ResusBaySoundEngine
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

enum class SideDrawerSide { LEFT, RIGHT }

enum class SideDrawerTab(
    val label: String,
    val side: SideDrawerSide
) {
    // Left Edge Tabs (matching Screenshot 1 & 2)
    PATIENT("Patient", SideDrawerSide.LEFT),
    EXAM("Exam", SideDrawerSide.LEFT),
    STABILIZE("Stabilize", SideDrawerSide.LEFT),
    DIFFERENTIAL("Differential", SideDrawerSide.LEFT),
    // Right Edge Tabs (matching Screenshot 1)
    INVESTIGATIONS("Investigations", SideDrawerSide.RIGHT),
    INTERVENTIONS("Interventions", SideDrawerSide.RIGHT),
    COMMUNICATE("Communicate", SideDrawerSide.RIGHT),
    HANDOFF("Handoff", SideDrawerSide.RIGHT)
}

enum class TabletTab(val title: String, val requiredGate: QuestionGateType) {
    PATIENT_VITALS("Patient & Vitals", QuestionGateType.Q0_GATE),
    SURVEY_EXAM("Survey & Exam (Q1)", QuestionGateType.Q1_SURVEY),
    LABS_ABG("Labs & ABG (Q2)", QuestionGateType.Q2_LABS),
    ECG_IMAGING("ECG & Imaging (Q3)", QuestionGateType.Q3_IMAGING),
    TEAM_COMMS("Clues & Chat", QuestionGateType.Q0_GATE),
    ANALYTICS("Tasks & Analytics", QuestionGateType.Q0_GATE)
}

data class SimUiState(
    val rooms: List<RoomTeamSession> = emptyList(),
    val activeRoomId: String = "RESUS-BAY-1",
    // 3D Camera Pan/Tilt & Spatial Player Coordinates
    val playerX: Float = 0.50f,
    val playerY: Float = 0.80f,
    val cameraYawDeg: Float = 0f,
    val cameraPitchDeg: Float = 0f,
    // Minerva-style Side Drawer State
    val activeSideDrawer: SideDrawerTab? = null,
    val performedExamKeys: Set<String> = emptySet(),
    val showAttendingBubble: Boolean = true,
    val attendingBubbleText: String = "I'm here to help, tap my icon if you need assistance. Crack Q0 to unlock Patient Details, Initial Vitals & Tablet!",
    val showJoystickControl: Boolean = false,
    // Full-Screen Virtual Tablet & Modals
    val isTabletOpen: Boolean = false,
    val isTabletFullScreen: Boolean = false,
    val selectedTabletTab: TabletTab = TabletTab.PATIENT_VITALS,
    val isMapFullScreen: Boolean = false,
    val isCrashCartOpen: Boolean = false,
    val openCrashCartDrawerIndex: Int? = 1,
    val activeQuestionGateModal: QuestionGateType? = null,
    val questionAttemptFeedback: String? = null,
    val isInstructorControlOpen: Boolean = false,
    val isScenarioPickerOpen: Boolean = false,
    val isHamburgerMenuOpen: Boolean = false,
    // Live Equipment Overrides (driven by case preset + team adjustments)
    val currentVentilator: VentilatorSettings = VentilatorSettings(),
    val currentDefibrillator: DefibrillatorSettings = DefibrillatorSettings(),
    // Audio state
    val isAudioEnabled: Boolean = true,
    val isAlarmSilenced: Boolean = false,
    val toastBanner: String? = null
) {
    val activeRoom: RoomTeamSession
        get() = rooms.firstOrNull { it.roomId == activeRoomId } ?: rooms.first()

    val activeCase: OralBoardCase
        get() = OralBoardRepository.getCaseByNumber(activeRoom.assignedCaseNumber)

    val currentVitals: VitalSigns
        get() = if (activeRoom.isPatientStabilized) activeCase.stabilizedVitals else activeCase.initialVitals

    fun getQuestionsForActiveRoom(): List<UnlockQuestion> {
        return activeCase.defaultQuestions.map { defaultQ ->
            activeRoom.customQuestions[defaultQ.gateType] ?: defaultQ
        }
    }

    fun getNearestQuestionGate(): UnlockQuestion? {
        return getQuestionsForActiveRoom().minByOrNull { q ->
            hypot((q.mapX - playerX).toDouble(), (q.mapY - playerY).toDouble())
        }?.takeIf { q ->
            hypot((q.mapX - playerX).toDouble(), (q.mapY - playerY).toDouble()) < 0.16
        }
    }
}

class SimViewModel : ViewModel() {

    val soundEngine = ResusBaySoundEngine()

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<SimUiState> = _uiState.asStateFlow()

    init {
        soundEngine.start(viewModelScope)
        syncAudioWithState(_uiState.value)

        // Room Timer Loop (100ms = 1 decisecond tick for tie-breaker accuracy)
        viewModelScope.launch {
            while (true) {
                delay(100L)
                _uiState.update { state ->
                    val updatedRooms = state.rooms.map { room ->
                        val caseObj = OralBoardRepository.getCaseByNumber(room.assignedCaseNumber)
                        val allDone = room.completedCriticalActionIds.size >= caseObj.criticalActions.size
                        if (room.isTimerRunning && !allDone) {
                            room.copy(elapsedDeciseconds = room.elapsedDeciseconds + 1)
                        } else {
                            room
                        }
                    }
                    state.copy(rooms = updatedRooms)
                }
            }
        }
    }

    private fun createInitialState(): SimUiState {
        val defaultCase1 = OralBoardRepository.getCaseByNumber(1)
        val initialRooms = listOf(
            RoomTeamSession(
                roomId = "RESUS-BAY-1",
                teamName = "Alpha Resus Squad",
                members = listOf(
                    "Dr. Maya (Team Lead)",
                    "Dr. Liam (Airway & Survey)",
                    "RN Sofia (Crash Cart & Meds)",
                    "Dr. Noah (Diagnostics & Procedures)"
                ),
                activeMember = "Dr. Maya (Team Lead)",
                assignedCaseNumber = 1,
                elapsedDeciseconds = 0L,
                isTimerRunning = true,
                chatMessages = listOf(
                    ChatMessage(
                        id = "m0",
                        sender = "Attending Physician",
                        roleTag = "INSTRUCTOR",
                        text = "Welcome to RESUS-BAY-1! Case #1 (${defaultCase1.scenarioCategory}) is active. Crack Gate Question Q0 to unlock Patient Details, Initial Vitals, and Virtual Tablet access!",
                        timestampLabel = "00:00.0"
                    )
                )
            ),
            RoomTeamSession(
                roomId = "RESUS-BAY-2",
                teamName = "Bravo Trauma Team",
                members = listOf("Dr. Ethan (Lead)", "Dr. Chloe (Airway)", "RN Lucas (Meds)", "Dr. Aria (Proc)"),
                activeMember = "Dr. Ethan (Lead)",
                assignedCaseNumber = 4,
                elapsedDeciseconds = 1420L, // 02:22.0 benchmark
                isTimerRunning = false,
                unlockedGates = setOf(QuestionGateType.Q0_GATE, QuestionGateType.Q1_SURVEY, QuestionGateType.Q2_LABS),
                completedCriticalActionIds = setOf("c4_a1", "c4_a2", "c4_a3", "c4_a4"),
                chatMessages = listOf(
                    ChatMessage("b1", "System", "SYSTEM", "Bravo Team completed 4 critical actions in 02:22.0.", "02:22")
                )
            ),
            RoomTeamSession(
                roomId = "TRAUMA-BAY-3",
                teamName = "Charlie Pediatric ICU",
                members = listOf("Dr. Priya (Lead)", "Dr. Sam (Airway)", "RN Elena (Meds)", "Dr. Kai (Proc)"),
                activeMember = "Dr. Priya (Lead)",
                assignedCaseNumber = 6,
                elapsedDeciseconds = 1185L, // 01:58.5 benchmark
                isTimerRunning = false,
                unlockedGates = QuestionGateType.entries.toSet(),
                completedCriticalActionIds = setOf("c6_a1", "c6_a2", "c6_a3", "c6_a4"),
                isPatientStabilized = true,
                chatMessages = listOf(
                    ChatMessage("c1", "System", "SYSTEM", "Charlie Team completed all 4 critical actions in 01:58.5!", "01:58")
                )
            )
        )

        return SimUiState(
            rooms = initialRooms,
            activeRoomId = "RESUS-BAY-1",
            currentVentilator = defaultCase1.ventilatorPreset,
            currentDefibrillator = defaultCase1.defibrillatorPreset
        )
    }

    private fun syncAudioWithState(state: SimUiState) {
        val vitals = state.currentVitals
        val isCritical = vitals.systolic < 90 || vitals.hr > 145 || vitals.hr < 60 || (vitals.spo2 != null && vitals.spo2 < 93)
        soundEngine.isAudioEnabled = state.isAudioEnabled
        soundEngine.isAlarmSilenced = state.isAlarmSilenced
        soundEngine.updateTelemetry(
            hr = vitals.hr,
            rr = vitals.rr,
            spo2 = vitals.spo2,
            ventilatorOn = state.currentVentilator.isActive,
            criticalAlarm = isCritical
        )
    }

    fun selectSideDrawerTab(tab: SideDrawerTab?) {
        _uiState.update { state ->
            val nextTab = if (state.activeSideDrawer == tab) null else tab
            state.copy(
                activeSideDrawer = nextTab,
                showAttendingBubble = if (nextTab != null) false else state.showAttendingBubble
            )
        }
    }

    fun performExamStep(examKey: String) {
        _uiState.update { state ->
            state.copy(performedExamKeys = state.performedExamKeys + examKey)
        }
    }

    fun toggleAttendingBubble() {
        _uiState.update { state ->
            val nextVisible = !state.showAttendingBubble
            val hint = state.activeCase.instructorClues.firstOrNull()
                ?: "I'm here to help, tap my icon if you need assistance."
            val message = if (!state.activeRoom.unlockedGates.contains(QuestionGateType.Q0_GATE)) {
                "I'm here to help! Crack initial Gate Question Q0 first to unlock Patient Details, Initial Vitals & Virtual Tablet."
            } else {
                "Attending Guidance: $hint"
            }
            state.copy(showAttendingBubble = nextVisible, attendingBubbleText = message)
        }
    }

    fun toggleJoystickVisibility() {
        _uiState.update { it.copy(showJoystickControl = !it.showJoystickControl) }
    }

    fun setHamburgerMenuOpen(open: Boolean) {
        _uiState.update { it.copy(isHamburgerMenuOpen = open) }
    }

    fun toggleAudio() {
        _uiState.update { state ->
            val next = state.copy(isAudioEnabled = !state.isAudioEnabled)
            syncAudioWithState(next)
            next
        }
    }

    fun toggleAlarmSilence() {
        _uiState.update { state ->
            val next = state.copy(isAlarmSilenced = !state.isAlarmSilenced)
            syncAudioWithState(next)
            next
        }
    }

    fun dismissToast() {
        _uiState.update { it.copy(toastBanner = null) }
    }

    // 3D Camera Swipe Look-Around & Quick Step Turn
    fun onCameraDrag(deltaYawDeg: Float, deltaPitchDeg: Float) {
        _uiState.update { state ->
            val newYaw = (state.cameraYawDeg + deltaYawDeg).coerceIn(-65f, 65f)
            val newPitch = (state.cameraPitchDeg + deltaPitchDeg).coerceIn(-28f, 28f)
            state.copy(cameraYawDeg = newYaw, cameraPitchDeg = newPitch)
        }
    }

    fun stepCameraYaw(deltaDeg: Float) {
        _uiState.update { state ->
            state.copy(cameraYawDeg = (state.cameraYawDeg + deltaDeg).coerceIn(-65f, 65f))
        }
    }

    // 3D Virtual Joystick Movement
    fun onJoystickMove(joyX: Float, joyY: Float) {
        _uiState.update { state ->
            val speed = 0.015f
            val yawRad = Math.toRadians(state.cameraYawDeg.toDouble())
            val forward = -joyY * speed
            val strafe = joyX * speed

            val dx = (forward * sin(yawRad) + strafe * cos(yawRad)).toFloat()
            val dy = (-forward * cos(yawRad) + strafe * sin(yawRad)).toFloat()

            val nextX = (state.playerX + dx).coerceIn(0.10f, 0.90f)
            val nextY = (state.playerY + dy).coerceIn(0.16f, 0.92f)
            state.copy(playerX = nextX, playerY = nextY)
        }
    }

    fun walkToQuestionStation(gateType: QuestionGateType) {
        _uiState.update { state ->
            val q = state.getQuestionsForActiveRoom().first { it.gateType == gateType }
            state.copy(
                playerX = q.mapX,
                playerY = (q.mapY + 0.05f).coerceIn(0.16f, 0.90f),
                activeQuestionGateModal = gateType,
                questionAttemptFeedback = null,
                isMapFullScreen = false
            )
        }
    }

    fun openQuestionModal(gateType: QuestionGateType) {
        _uiState.update {
            it.copy(
                activeQuestionGateModal = gateType,
                questionAttemptFeedback = null
            )
        }
    }

    fun closeQuestionModal() {
        _uiState.update {
            it.copy(
                activeQuestionGateModal = null,
                questionAttemptFeedback = null
            )
        }
    }

    fun submitQuestionAnswer(gateType: QuestionGateType, selectedOptionIndex: Int) {
        _uiState.update { state ->
            val q = state.getQuestionsForActiveRoom().first { it.gateType == gateType }
            if (selectedOptionIndex == q.correctIndex) {
                val room = state.activeRoom
                val newUnlocked = room.unlockedGates + gateType
                val newCrackedBy = room.crackedByMember + (gateType to room.activeMember)
                val unlockMsg = ChatMessage(
                    id = "q_${System.currentTimeMillis()}",
                    sender = room.activeMember,
                    roleTag = "TEAM",
                    text = "Cracked ${gateType.code} (${q.stationName})! Unlocked: ${gateType.unlocksLabel}.",
                    timestampLabel = room.formattedTimer
                )
                val targetSideTab = when (gateType) {
                    QuestionGateType.Q0_GATE -> SideDrawerTab.PATIENT
                    QuestionGateType.Q1_SURVEY -> SideDrawerTab.EXAM
                    QuestionGateType.Q2_LABS -> SideDrawerTab.INVESTIGATIONS
                    QuestionGateType.Q3_IMAGING -> SideDrawerTab.INVESTIGATIONS
                }
                val targetTabletTab = when (gateType) {
                    QuestionGateType.Q0_GATE -> TabletTab.PATIENT_VITALS
                    QuestionGateType.Q1_SURVEY -> TabletTab.SURVEY_EXAM
                    QuestionGateType.Q2_LABS -> TabletTab.LABS_ABG
                    QuestionGateType.Q3_IMAGING -> TabletTab.ECG_IMAGING
                }
                val updatedRoom = room.copy(
                    unlockedGates = newUnlocked,
                    crackedByMember = newCrackedBy,
                    chatMessages = room.chatMessages + unlockMsg
                )
                state.copy(
                    rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it },
                    activeQuestionGateModal = null,
                    questionAttemptFeedback = null,
                    activeSideDrawer = if (state.isTabletOpen) state.activeSideDrawer else targetSideTab,
                    selectedTabletTab = targetTabletTab,
                    showAttendingBubble = false,
                    attendingBubbleText = "Great job! ${gateType.code} unlocked ${gateType.unlocksLabel}.",
                    toastBanner = "${gateType.code} Cracked by ${room.activeMember}! Unlocked ${gateType.unlocksLabel}"
                )
            } else {
                state.copy(
                    questionAttemptFeedback = "Incorrect choice. Review the clinical reasoning and try again to unlock ${gateType.unlocksLabel}!"
                )
            }
        }
    }

    fun selectActiveTeamMember(memberName: String) {
        _uiState.update { state ->
            val updatedRoom = state.activeRoom.copy(activeMember = memberName)
            state.copy(
                rooms = state.rooms.map { if (it.roomId == updatedRoom.roomId) updatedRoom else it },
                toastBanner = "Active Resus Role switched to $memberName"
            )
        }
    }

    fun assignCaseToRoom(
        roomId: String,
        caseNumber: Int,
        teamName: String? = null,
        members: List<String>? = null
    ) {
        _uiState.update { state ->
            val targetCase = OralBoardRepository.getCaseByNumber(caseNumber)
            val updatedRooms = state.rooms.map { room ->
                if (room.roomId == roomId) {
                    val newTeamName = teamName ?: room.teamName
                    val newMembers = members ?: room.members
                    room.copy(
                        teamName = newTeamName,
                        members = newMembers,
                        activeMember = newMembers.firstOrNull() ?: room.activeMember,
                        assignedCaseNumber = caseNumber,
                        elapsedDeciseconds = 0L,
                        isTimerRunning = true,
                        unlockedGates = emptySet(),
                        crackedByMember = emptyMap(),
                        completedCriticalActionIds = emptySet(),
                        takenCartItems = emptyList(),
                        appliedCartItemsOnPatient = emptyList(),
                        customQuestions = emptyMap(),
                        isPatientStabilized = false,
                        chatMessages = listOf(
                            ChatMessage(
                                id = "assign_${System.currentTimeMillis()}",
                                sender = "Attending Physician",
                                roleTag = "INSTRUCTOR",
                                text = "Assigned Oral Board Case #$caseNumber (${targetCase.scenarioCategory}, PDF pp. ${targetCase.pdfPages}) to $roomId ($newTeamName). Crack Q0 to unlock patient details, vitals & tablet!",
                                timestampLabel = "00:00.0"
                            )
                        )
                    )
                } else room
            }
            val nextState = state.copy(
                rooms = updatedRooms,
                activeRoomId = roomId,
                playerX = 0.50f,
                playerY = 0.80f,
                cameraYawDeg = 0f,
                cameraPitchDeg = 0f,
                activeSideDrawer = null,
                performedExamKeys = emptySet(),
                showAttendingBubble = true,
                attendingBubbleText = "I'm here to help, tap my icon if you need assistance. Case #$caseNumber (${targetCase.scenarioCategory}) loaded — crack Q0 to unlock vitals & tablet!",
                currentVentilator = targetCase.ventilatorPreset,
                currentDefibrillator = targetCase.defibrillatorPreset,
                isScenarioPickerOpen = false,
                isInstructorControlOpen = false,
                toastBanner = "Room $roomId preset with Case #$caseNumber: ${targetCase.scenarioCategory}"
            )
            syncAudioWithState(nextState)
            nextState
        }
    }

    fun switchActiveRoom(roomId: String) {
        _uiState.update { state ->
            val room = state.rooms.firstOrNull { it.roomId == roomId } ?: return@update state
            val c = OralBoardRepository.getCaseByNumber(room.assignedCaseNumber)
            val next = state.copy(
                activeRoomId = roomId,
                performedExamKeys = emptySet(),
                currentVentilator = c.ventilatorPreset,
                currentDefibrillator = c.defibrillatorPreset,
                toastBanner = "Switched to $roomId (${room.teamName})"
            )
            syncAudioWithState(next)
            next
        }
    }

    fun createNewRoomSession(roomId: String, teamName: String, membersText: String, caseNumber: Int) {
        val cleanId = roomId.trim().uppercase().ifEmpty { "RESUS-BAY-${_uiState.value.rooms.size + 1}" }
        val membersList = membersText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            .ifEmpty { listOf("Dr. Lead", "Dr. Airway", "RN Crash Cart", "Dr. Procedures") }
        val c = OralBoardRepository.getCaseByNumber(caseNumber)

        _uiState.update { state ->
            if (state.rooms.any { it.roomId == cleanId }) {
                return@update state.copy(toastBanner = "Room $cleanId already exists.")
            }
            val newRoom = RoomTeamSession(
                roomId = cleanId,
                teamName = teamName.ifBlank { "Team $cleanId" },
                members = membersList,
                activeMember = membersList.first(),
                assignedCaseNumber = caseNumber,
                chatMessages = listOf(
                    ChatMessage(
                        id = "init_$cleanId",
                        sender = "Attending Physician",
                        roleTag = "INSTRUCTOR",
                        text = "Room $cleanId initialized with Case #$caseNumber (${c.scenarioCategory}). Timer started!",
                        timestampLabel = "00:00.0"
                    )
                )
            )
            val next = state.copy(
                rooms = state.rooms + newRoom,
                activeRoomId = cleanId,
                performedExamKeys = emptySet(),
                currentVentilator = c.ventilatorPreset,
                currentDefibrillator = c.defibrillatorPreset,
                isScenarioPickerOpen = false,
                toastBanner = "Created Room $cleanId for ${newRoom.teamName} (Case #$caseNumber)"
            )
            syncAudioWithState(next)
            next
        }
    }

    fun saveCustomInstructorQuestion(
        gateType: QuestionGateType,
        stationName: String,
        mapX: Float,
        mapY: Float,
        prompt: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String
    ) {
        _uiState.update { state ->
            val room = state.activeRoom
            val customQ = UnlockQuestion(
                gateType = gateType,
                stationName = stationName.ifBlank { gateType.title },
                mapX = mapX.coerceIn(0.12f, 0.88f),
                mapY = mapY.coerceIn(0.16f, 0.88f),
                prompt = prompt,
                options = options,
                correctIndex = correctIndex.coerceIn(0, options.lastIndex),
                clinicalExplanation = explanation,
                assignedByInstructor = "Admin Control Room Engine"
            )
            val updatedCustomMap = room.customQuestions + (gateType to customQ)
            val msg = ChatMessage(
                id = "customq_${System.currentTimeMillis()}",
                sender = "Instructor Control Room",
                roleTag = "INSTRUCTOR",
                text = "Updated ${gateType.code} question marker at Map (${"%.2f".format(mapX)}, ${"%.2f".format(mapY)}): \"$prompt\"",
                timestampLabel = room.formattedTimer
            )
            val updatedRoom = room.copy(
                customQuestions = updatedCustomMap,
                chatMessages = room.chatMessages + msg
            )
            state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it },
                toastBanner = "Saved custom ${gateType.code} question on Resus Bay Map!"
            )
        }
    }

    fun toggleCrashCartModal(open: Boolean) {
        _uiState.update { it.copy(isCrashCartOpen = open) }
    }

    fun selectCrashCartDrawer(drawerNumber: Int?) {
        _uiState.update { state ->
            val nextDrawer = if (state.openCrashCartDrawerIndex == drawerNumber) null else drawerNumber
            state.copy(openCrashCartDrawerIndex = nextDrawer)
        }
    }

    fun takeItemFromCrashCart(item: CrashCartItem) {
        _uiState.update { state ->
            val room = state.activeRoom
            if (room.takenCartItems.any { it.id == item.id }) {
                return@update state.copy(toastBanner = "${item.name} is already in ${room.activeMember}'s hands!")
            }
            val msg = ChatMessage(
                id = "take_${System.currentTimeMillis()}",
                sender = room.activeMember,
                roleTag = "CRASH_CART",
                text = "Took [${item.name}] (${item.specification}) from CODE 100 Crash Cart.",
                timestampLabel = room.formattedTimer
            )
            val updatedRoom = room.copy(
                takenCartItems = room.takenCartItems + item,
                chatMessages = room.chatMessages + msg
            )
            state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it },
                toastBanner = "${room.activeMember} took ${item.name}"
            )
        }
    }

    fun useCrashCartItemOnPatient(item: CrashCartItem) {
        _uiState.update { state ->
            val room = state.activeRoom
            val activeCase = state.activeCase

            val newlyMatchedActionIds = activeCase.criticalActions.filter { action ->
                val actionText = action.description.lowercase()
                item.matchingActionKeywords.any { kw -> actionText.contains(kw.lowercase()) } ||
                    action.triggerKeywords.any { kw ->
                        item.name.lowercase().contains(kw.lowercase()) ||
                            item.clinicalUseHint.lowercase().contains(kw.lowercase())
                    }
            }.map { it.id }.toSet()

            val updatedActions = room.completedCriticalActionIds + newlyMatchedActionIds
            val stabilized = updatedActions.size >= (activeCase.criticalActions.size + 1) / 2

            val actionFeedback = if (newlyMatchedActionIds.isNotEmpty()) {
                " ✓ Completed ${newlyMatchedActionIds.size} Critical Action(s)!"
            } else ""

            val msg = ChatMessage(
                id = "use_${System.currentTimeMillis()}",
                sender = room.activeMember,
                roleTag = "CRASH_CART",
                text = "Applied [${item.name}] on patient ${activeCase.patientName}.$actionFeedback",
                timestampLabel = room.formattedTimer
            )

            val updatedRoom = room.copy(
                takenCartItems = if (room.takenCartItems.any { it.id == item.id }) room.takenCartItems else room.takenCartItems + item,
                appliedCartItemsOnPatient = (room.appliedCartItemsOnPatient + item.name).distinct(),
                completedCriticalActionIds = updatedActions,
                isPatientStabilized = stabilized,
                chatMessages = room.chatMessages + msg
            )

            val nextVent = if (item.id.contains("ett") || item.id.contains("bvm") || item.id.contains("laryngoscope")) {
                state.currentVentilator.copy(isActive = true)
            } else state.currentVentilator

            val nextState = state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it },
                currentVentilator = nextVent,
                toastBanner = "Used ${item.name} on ${activeCase.patientName}!$actionFeedback"
            )
            syncAudioWithState(nextState)
            nextState
        }
    }

    fun toggleCriticalAction(actionId: String) {
        _uiState.update { state ->
            val room = state.activeRoom
            val activeCase = state.activeCase
            val newSet = if (room.completedCriticalActionIds.contains(actionId)) {
                room.completedCriticalActionIds - actionId
            } else {
                room.completedCriticalActionIds + actionId
            }
            val stabilized = newSet.size >= (activeCase.criticalActions.size + 1) / 2
            val updatedRoom = room.copy(
                completedCriticalActionIds = newSet,
                isPatientStabilized = stabilized
            )
            val nextState = state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it }
            )
            syncAudioWithState(nextState)
            nextState
        }
    }

    fun updateDomainRating(domainNumber: Int, score1To8: Int) {
        _uiState.update { state ->
            val room = state.activeRoom
            val updatedRoom = room.copy(
                domainRatings = room.domainRatings + (domainNumber to score1To8.coerceIn(1, 8))
            )
            state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it }
            )
        }
    }

    fun updateVentilatorSettings(
        tidalVolumeMlPerKg: Int,
        respRate: Int,
        fio2: Int,
        peep: Int,
        isActive: Boolean
    ) {
        _uiState.update { state ->
            val nextVent = state.currentVentilator.copy(
                tidalVolumeMlPerKg = tidalVolumeMlPerKg.coerceIn(4, 15),
                respRatePerMin = respRate.coerceIn(8, 60),
                fio2Percent = fio2.coerceIn(21, 100),
                peepCmH2O = peep.coerceIn(0, 20),
                isActive = isActive
            )
            val nextState = state.copy(
                currentVentilator = nextVent,
                toastBanner = "ICU Ventilator updated: ${nextVent.tidalVolumeMlPerKg} mL/kg, ${nextVent.respRatePerMin}/min, FiO₂ ${nextVent.fio2Percent}%"
            )
            syncAudioWithState(nextState)
            nextState
        }
    }

    fun deliverDefibrillatorShock(energyJoules: Int, syncMode: Boolean) {
        _uiState.update { state ->
            val room = state.activeRoom
            val activeCase = state.activeCase
            val perKg = "%.1f J/kg".format(energyJoules / activeCase.weightKg.coerceAtLeast(1f))
            val nextDefib = state.currentDefibrillator.copy(
                energyJoules = energyJoules,
                energyPerKgLabel = "$perKg (${energyJoules} J delivered)",
                syncEnabled = syncMode,
                shocksDelivered = state.currentDefibrillator.shocksDelivered + 1
            )
            val matchedShockActions = activeCase.criticalActions.filter {
                it.description.lowercase().contains("defibrillat") || it.description.lowercase().contains("4 j/kg")
            }.map { it.id }.toSet()

            val updatedActions = room.completedCriticalActionIds + matchedShockActions
            val stabilized = updatedActions.size >= (activeCase.criticalActions.size + 1) / 2
            val msg = ChatMessage(
                id = "shock_${System.currentTimeMillis()}",
                sender = room.activeMember,
                roleTag = "TEAM",
                text = "Delivered ${if (syncMode) "Synchronized Cardioversion" else "Unsynchronized Defibrillation"} Shock at $energyJoules J ($perKg)!",
                timestampLabel = room.formattedTimer
            )
            val updatedRoom = room.copy(
                completedCriticalActionIds = updatedActions,
                isPatientStabilized = stabilized,
                chatMessages = room.chatMessages + msg
            )
            val nextState = state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it },
                currentDefibrillator = nextDefib,
                toastBanner = "Shock Delivered ($energyJoules J / $perKg) on Patient Monitor/Defibrillator!"
            )
            syncAudioWithState(nextState)
            nextState
        }
    }

    fun sendChatMessage(sender: String, roleTag: String, text: String) {
        if (text.isBlank()) return
        _uiState.update { state ->
            val room = state.activeRoom
            val msg = ChatMessage(
                id = "chat_${System.currentTimeMillis()}",
                sender = sender,
                roleTag = roleTag,
                text = text.trim(),
                timestampLabel = room.formattedTimer
            )
            val updatedRoom = room.copy(chatMessages = room.chatMessages + msg)
            state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it },
                attendingBubbleText = if (roleTag == "INSTRUCTOR") text.trim() else state.attendingBubbleText,
                showAttendingBubble = if (roleTag == "INSTRUCTOR") true else state.showAttendingBubble
            )
        }
    }

    fun toggleRoomTimer() {
        _uiState.update { state ->
            val room = state.activeRoom
            val updatedRoom = room.copy(isTimerRunning = !room.isTimerRunning)
            state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it }
            )
        }
    }

    fun resetRoomTimer() {
        _uiState.update { state ->
            val room = state.activeRoom
            val updatedRoom = room.copy(elapsedDeciseconds = 0L, isTimerRunning = true)
            state.copy(
                rooms = state.rooms.map { if (it.roomId == room.roomId) updatedRoom else it }
            )
        }
    }

    fun setTabletOpen(open: Boolean, fullScreen: Boolean = _uiState.value.isTabletFullScreen) {
        _uiState.update { it.copy(isTabletOpen = open, isTabletFullScreen = fullScreen) }
    }

    fun toggleTabletFullScreen() {
        _uiState.update { it.copy(isTabletOpen = true, isTabletFullScreen = !it.isTabletFullScreen) }
    }

    fun selectTabletTab(tab: TabletTab) {
        _uiState.update { state ->
            val isUnlocked = state.activeRoom.unlockedGates.contains(tab.requiredGate)
            if (!isUnlocked) {
                state.copy(
                    activeQuestionGateModal = tab.requiredGate,
                    questionAttemptFeedback = null,
                    toastBanner = "Crack ${tab.requiredGate.code} (${tab.requiredGate.title}) to unlock ${tab.title}!"
                )
            } else {
                state.copy(selectedTabletTab = tab, isTabletOpen = true)
            }
        }
    }

    fun setMapFullScreen(fullScreen: Boolean) {
        _uiState.update { it.copy(isMapFullScreen = fullScreen) }
    }

    fun setInstructorControlOpen(open: Boolean) {
        _uiState.update { it.copy(isInstructorControlOpen = open, isHamburgerMenuOpen = false) }
    }

    fun setScenarioPickerOpen(open: Boolean) {
        _uiState.update { it.copy(isScenarioPickerOpen = open, isHamburgerMenuOpen = false) }
    }

    override fun onCleared() {
        super.onCleared()
        soundEngine.stop()
    }
}
