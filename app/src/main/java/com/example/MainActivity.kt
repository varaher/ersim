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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SideDrawerTab
import com.example.ui.SimViewModel
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
            MyApplicationTheme {
                ErResusSimulationApp()
            }
        }
    }
}

@Composable
fun ErResusSimulationApp(
    simViewModel: SimViewModel = viewModel()
) {
    val uiState by simViewModel.uiState.collectAsStateWithLifecycle()
    val room = uiState.activeRoom
    val activeCase = uiState.activeCase

    BackHandler(
        enabled = uiState.activeSideDrawer != null ||
            uiState.isTabletFullScreen ||
            uiState.isTabletOpen ||
            uiState.isMapFullScreen ||
            uiState.isCrashCartOpen ||
            uiState.isInstructorControlOpen ||
            uiState.isScenarioPickerOpen ||
            uiState.activeQuestionGateModal != null
    ) {
        when {
            uiState.activeQuestionGateModal != null -> simViewModel.closeQuestionModal()
            uiState.isMapFullScreen -> simViewModel.setMapFullScreen(false)
            uiState.isCrashCartOpen -> simViewModel.toggleCrashCartModal(false)
            uiState.isInstructorControlOpen -> simViewModel.setInstructorControlOpen(false)
            uiState.isScenarioPickerOpen -> simViewModel.setScenarioPickerOpen(false)
            uiState.isTabletFullScreen -> simViewModel.toggleTabletFullScreen()
            uiState.isTabletOpen -> simViewModel.setTabletOpen(false)
            uiState.activeSideDrawer != null -> simViewModel.selectSideDrawerTab(null)
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
