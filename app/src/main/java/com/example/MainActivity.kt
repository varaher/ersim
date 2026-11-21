package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.OralBoardRepository
import com.example.ui.SideDrawerTab
import com.example.ui.SimViewModel
import com.example.ui.app.AppViewModel
import com.example.ui.app.ErSimAppShell
import com.example.ui.app.OnboardingScreen
import com.example.ui.app.ThemeMode
import com.example.ui.app.InteractiveCaseSummary
import com.example.ui.game.GameScreen
import com.example.ui.game.GameViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.parseColor("#0D1C2B")),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.parseColor("#07111B"))
        )
        setContent {
            ErSimRoot()
        }
    }
}

// =============================================================================
// ROOT: Onboarding -> App shell (Home / Cases / Profile / Settings) -> Simulation
// =============================================================================
@Composable
fun ErSimRoot(
    appViewModel: AppViewModel = viewModel(),
    simViewModel: SimViewModel = viewModel(),
    gameViewModel: GameViewModel = viewModel()
) {
    val appState by appViewModel.state.collectAsStateWithLifecycle()
    val simState by simViewModel.uiState.collectAsStateWithLifecycle()
    var inSimulation by rememberSaveable { mutableStateOf(false) }
    var inGame by rememberSaveable { mutableStateOf(false) }
    val gameState by gameViewModel.state.collectAsStateWithLifecycle()
    // After process death the game session is gone: fall back to Home
    LaunchedEffect(inGame, gameState.isPlaying, gameState.cases) {
        if (inGame && !gameState.isPlaying && (gameState.cases.isNotEmpty() || gameState.loadError != null)) inGame = false
    }

    // Theme: app screens follow the setting; the resus bay is always dark
    val systemDark = isSystemInDarkTheme()
    val appDark = when (appState.settings.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val useDark = inSimulation || inGame || appDark
    val activity = LocalContext.current as? ComponentActivity
    LaunchedEffect(useDark) {
        activity?.enableEdgeToEdge(
            statusBarStyle = if (useDark) SystemBarStyle.dark(android.graphics.Color.parseColor("#0D1C2B"))
            else SystemBarStyle.light(android.graphics.Color.WHITE, android.graphics.Color.parseColor("#0D1C2B")),
            navigationBarStyle = if (useDark) SystemBarStyle.dark(android.graphics.Color.parseColor("#07111B"))
            else SystemBarStyle.light(android.graphics.Color.WHITE, android.graphics.Color.parseColor("#07111B"))
        )
    }

    // Apply settings when a case is entered or a setting changes
    LaunchedEffect(inSimulation, appState.settings.soundEnabled, appState.settings.showJoystickByDefault) {
        if (inSimulation) {
            if (simViewModel.uiState.value.isAudioEnabled != appState.settings.soundEnabled) simViewModel.toggleAudio()
            if (simViewModel.uiState.value.showJoystickControl != appState.settings.showJoystickByDefault) simViewModel.toggleJoystickVisibility()
        }
    }
    // Bay audio plays only while a case is open AND the app is in the foreground
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, inSimulation) {
        fun sync() {
            val visible = lifecycleOwner.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)
            if (inSimulation && visible) simViewModel.resumeAudio() else simViewModel.pauseAudio()
        }
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_START || event == androidx.lifecycle.Lifecycle.Event.ON_STOP) sync()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        sync()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            simViewModel.pauseAudio()
        }
    }

    val view = LocalView.current
    DisposableEffect(inSimulation, inGame, appState.settings.keepScreenOnInCase) {
        view.keepScreenOn = (inSimulation || inGame) && appState.settings.keepScreenOnInCase
        onDispose { view.keepScreenOn = false }
    }

    MyApplicationTheme(darkTheme = useDark) {
    when {
        !appState.onboardingDone -> OnboardingScreen(onDone = appViewModel::completeOnboarding)

        inGame -> GameScreen(
            vm = gameViewModel,
            onExit = {
                gameViewModel.exitGame()
                inGame = false
            },
            onFinished = appViewModel::recordInteractiveResult,
            showTutorial = !appState.seenGameTutorial,
            onTutorialSeen = appViewModel::markTutorialSeen
        )

        inSimulation -> ErResusSimulationApp(
            simViewModel = simViewModel,
            onExitToHome = { inSimulation = false }
        )

        else -> ErSimAppShell(
            appState = appState,
            lastCaseTitle = appState.progress.lastCaseNumber?.let { n ->
                val c = OralBoardRepository.getCaseByNumber(n)
                "Case ${c.caseNumber} • ${c.scenarioCategory}"
            },
            onStartCase = { n ->
                simViewModel.assignCaseToRoom(simState.activeRoomId, n)
                appViewModel.recordCaseStarted(n)
                inSimulation = true
            },
            onResumeLastCase = {
                val n = appState.progress.lastCaseNumber
                if (n != null && simState.activeCase.caseNumber != n) {
                    simViewModel.assignCaseToRoom(simState.activeRoomId, n)
                }
                inSimulation = true
            },
            onUpdateProfile = appViewModel::updateProfile,
            onUpdateSettings = appViewModel::updateSettings,
            onResetProgress = appViewModel::resetProgress,
            onResetEverything = appViewModel::resetEverything,
            onLogOut = {
                inSimulation = false
                inGame = false
                appViewModel.logOut()
            },
            interactiveCases = gameState.cases.map {
                InteractiveCaseSummary(
                    it.meta.caseId,
                    it.meta.title,
                    it.meta.category,
                    it.meta.difficulty,
                    it.meta.estimatedMinutes,
                    it.meta.reviewStatus
                )
            },
            onPlayInteractive = { id ->
                gameViewModel.startCase(id)
                inGame = true
            }
        )
    }
    }
}

@Composable
fun ErResusSimulationApp(
    simViewModel: SimViewModel = viewModel(),
    onExitToHome: () -> Unit = {}
) {
    var showHelp by rememberSaveable { mutableStateOf(false) }
    if (showHelp) {
        com.example.ui.app.HowToPlayScreen(onBack = { showHelp = false }, startSectionId = "classic")
        return
    }
    val uiState by simViewModel.uiState.collectAsStateWithLifecycle()
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase

    // Back closes the top overlay first; with nothing open it returns to Home
    BackHandler(enabled = true) {
        when {
            uiState.activeQuestionGateModal != null -> simViewModel.closeQuestionModal()
            uiState.isMapFullScreen -> simViewModel.setMapFullScreen(false)
            uiState.isCrashCartOpen -> simViewModel.toggleCrashCartModal(false)
            uiState.isInstructorControlOpen -> simViewModel.setInstructorControlOpen(false)
            uiState.isScenarioPickerOpen -> simViewModel.setScenarioPickerOpen(false)
            uiState.isTabletFullScreen -> simViewModel.toggleTabletFullScreen()
            uiState.isTabletOpen -> simViewModel.setTabletOpen(false)
            uiState.activeSideDrawer != null -> simViewModel.selectSideDrawerTab(null)
            else -> onExitToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ErSimBg)
            .testTag("er_sim_v2_root")
    ) {
        // =========================================================================
        // 1. ER SIM V2 NATIVE DARK SIMULATION SCREEN
        //    (Top Header + Native Vitals + Prominent ResusBay3DViewport +
        //     6 Clinical Workspace Tabs + 2-Column Action Console + Real Timeline)
        // =========================================================================
        ErSimV2Screen(
            uiState = uiState,
            onToggleTimer = simViewModel::toggleRoomTimer,
            onRestartCase = {
                simViewModel.assignCaseToRoom(room.roomId, activeCase.caseNumber)
            },
            onEndCase = {
                if (room.isTimerRunning) {
                    simViewModel.toggleRoomTimer()
                }
                simViewModel.selectSideDrawerTab(SideDrawerTab.HANDOFF)
            },
            onOpenScenarioPicker = { simViewModel.setScenarioPickerOpen(true) },
            onOpenAdminEngine = { simViewModel.setInstructorControlOpen(true) },
            onOpenFullScreenTablet = { tab ->
                if (tab != null) {
                    simViewModel.selectTabletTab(tab)
                } else {
                    simViewModel.setTabletOpen(true, fullScreen = true)
                }
            },
            onOpenFullScreenMap = { simViewModel.setMapFullScreen(true) },
            onOpenCrashCart = { drawerIdx ->
                if (drawerIdx != null) {
                    simViewModel.selectCrashCartDrawer(drawerIdx)
                }
                simViewModel.toggleCrashCartModal(true)
            },
            onOpenQuestionGate = simViewModel::openQuestionModal,
            onWalkToQuestionOnMap = simViewModel::walkToQuestionStation,
            onSelectSideDrawerTab = simViewModel::selectSideDrawerTab,
            onPerformExamStep = simViewModel::performExamStep,
            onToggleCriticalAction = simViewModel::toggleCriticalAction,
            onSelectActiveTeamMember = simViewModel::selectActiveTeamMember,
            onCameraDrag = simViewModel::onCameraDrag,
            onStepCameraYaw = simViewModel::stepCameraYaw,
            onJoystickMove = simViewModel::onJoystickMove,
            onToggleAudio = simViewModel::toggleAudio,
            onToggleAlarmSilence = simViewModel::toggleAlarmSilence,
            onToggleJoystick = simViewModel::toggleJoystickVisibility,
            onNavigateHome = onExitToHome,
            onOpenHelp = { showHelp = true },
            modifier = Modifier.fillMaxSize()
        )

        // =========================================================================
        // 2. SLIDING CLINICAL SIDE DRAWER OVERLAY (PATIENT, EXAM, STABILIZE, ETC.)
        // =========================================================================
        uiState.activeSideDrawer?.let { activeTab ->
            ClinicalSideDrawerSheet(
                uiState = uiState,
                activeTab = activeTab,
                onSelectTab = simViewModel::selectSideDrawerTab,
                onCollapse = { simViewModel.selectSideDrawerTab(null) },
                onPerformExamStep = simViewModel::performExamStep,
                onOpenQuestionGate = simViewModel::openQuestionModal,
                onOpenFullScreenTablet = {
                    simViewModel.selectSideDrawerTab(null)
                    simViewModel.setTabletOpen(true, fullScreen = true)
                },
                onOpenCrashCartModal = { simViewModel.toggleCrashCartModal(true) },
                onSelectCrashCartDrawer = simViewModel::selectCrashCartDrawer,
                onTakeCrashCartItem = simViewModel::takeItemFromCrashCart,
                onUseCrashCartItem = simViewModel::useCrashCartItemOnPatient,
                onUpdateVentilator = simViewModel::updateVentilatorSettings,
                onDeliverShock = simViewModel::deliverDefibrillatorShock,
                onToggleCriticalAction = simViewModel::toggleCriticalAction,
                onUpdateDomainRating = simViewModel::updateDomainRating,
                onSelectTeamMember = simViewModel::selectActiveTeamMember,
                onSendChatMessage = { text ->
                    simViewModel.sendChatMessage(room.activeMember, "TEAM", text)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // =========================================================================
        // 3. FULL-SCREEN / DOCKED VIRTUAL TABLET OVERLAY
        // =========================================================================
        if (uiState.isTabletOpen) {
            VirtualTabletOverlay(
                uiState = uiState,
                onSelectTab = simViewModel::selectTabletTab,
                onToggleFullScreen = simViewModel::toggleTabletFullScreen,
                onCloseTablet = { simViewModel.setTabletOpen(false) },
                onOpenQuestionGate = simViewModel::openQuestionModal,
                onUpdateVentilator = simViewModel::updateVentilatorSettings,
                onDeliverShock = simViewModel::deliverDefibrillatorShock,
                onToggleCriticalAction = simViewModel::toggleCriticalAction,
                onUpdateDomainRating = simViewModel::updateDomainRating,
                onSendChatMessage = { text ->
                    simViewModel.sendChatMessage(room.activeMember, "TEAM", text)
                },
                modifier = Modifier.align(if (uiState.isTabletFullScreen) Alignment.Center else Alignment.BottomCenter)
            )
        }

        // =========================================================================
        // 4. COMPACT CLINICAL TOAST FEEDBACK PILL
        // =========================================================================
        androidx.compose.animation.AnimatedVisibility(
            visible = uiState.toastBanner != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            uiState.toastBanner?.let { bannerText ->
                Surface(
                    color = ErSimSurface.copy(alpha = 0.97f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ErSimMint),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clickable { simViewModel.dismissToast() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ErSimMint,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = bannerText,
                            color = ErSimTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // =========================================================================
    // FULL-SCREEN MODALS (ER MAP, 6-DRAWER CRASH CART, QUESTIONS Q0-Q3, ADMIN ENGINE)
    // =========================================================================
    if (uiState.isMapFullScreen) {
        FullScreenErMapDialog(
            uiState = uiState,
            onDismiss = { simViewModel.setMapFullScreen(false) },
            onSelectQuestionOnMap = simViewModel::walkToQuestionStation,
            onOpenCrashCart = { simViewModel.toggleCrashCartModal(true) }
        )
    }

    if (uiState.isCrashCartOpen) {
        CrashCart6DrawerModal(
            uiState = uiState,
            onSelectDrawer = simViewModel::selectCrashCartDrawer,
            onTakeItem = simViewModel::takeItemFromCrashCart,
            onUseItemOnPatient = simViewModel::useCrashCartItemOnPatient,
            onDismiss = { simViewModel.toggleCrashCartModal(false) }
        )
    }

    uiState.activeQuestionGateModal?.let { gateType ->
        QuestionCrackDialog(
            uiState = uiState,
            gateType = gateType,
            onSelectActiveMember = simViewModel::selectActiveTeamMember,
            onSubmitAnswer = simViewModel::submitQuestionAnswer,
            onDismiss = simViewModel::closeQuestionModal
        )
    }

    if (uiState.isInstructorControlOpen || uiState.isScenarioPickerOpen) {
        InstructorControlRoomDialog(
            uiState = uiState,
            initialSectionIsScenarios = uiState.isScenarioPickerOpen,
            onAssignCaseToRoom = simViewModel::assignCaseToRoom,
            onCreateNewRoom = simViewModel::createNewRoomSession,
            onSwitchRoom = simViewModel::switchActiveRoom,
            onSaveCustomQuestion = simViewModel::saveCustomInstructorQuestion,
            onSendInstructorClue = { clue ->
                simViewModel.sendChatMessage("Attending Physician", "INSTRUCTOR", clue)
            },
            onToggleTimer = simViewModel::toggleRoomTimer,
            onResetTimer = simViewModel::resetRoomTimer,
            onDismiss = {
                simViewModel.setInstructorControlOpen(false)
                simViewModel.setScenarioPickerOpen(false)
            }
        )
    }
}
