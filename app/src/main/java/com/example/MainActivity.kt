package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SideDrawerSide
import com.example.ui.SideDrawerTab
import com.example.ui.SimViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.parseColor("#2B4D6F"))
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ClinicalNavyHeader)
    ) {
        // =========================================================================
        // 1. TOP CLINICAL SLATE-NAVY HEADER BAR + CORAL ACCENT LINE (MATCHING SCREENSHOTS)
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ClinicalNavyHeader)
                .statusBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 14.dp)
            ) {
                // Left: "Case #X" stacked cleanly (Tap to open 20 Oral Board Scenarios)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { simViewModel.setScenarioPickerOpen(true) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .testTag("open_20_scenarios_btn")
                ) {
                    Text(
                        text = "Case",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "#${activeCase.caseNumber}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Center: Case Title + Live Room ID & Tie-Breaker Timer Subtitle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .clickable { simViewModel.setScenarioPickerOpen(true) }
                ) {
                    Text(
                        text = "${activeCase.scenarioCategory} • ${activeCase.chiefComplaint.take(36)}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${room.roomId}  •  ${room.formattedTimer}  •  ${room.tasksCompletedCount}/${activeCase.criticalActions.size} Tasks",
                            color = Color(0xFFB3E5FC),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Right: Hamburger Menu (Opens 20 Scenarios, Admin Question Engine, Full-Screen Tablet, Map, Timers)
                Box {
                    IconButton(
                        onClick = { simViewModel.setHamburgerMenuOpen(true) },
                        modifier = Modifier.testTag("header_hamburger_menu_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Simulation Menu",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = uiState.isHamburgerMenuOpen,
                        onDismissRequest = { simViewModel.setHamburgerMenuOpen(false) },
                        containerColor = ClinicalSheetWhite
                    ) {
                        DropdownMenuItem(
                            text = { Text("Choose 20 Oral Board Scenarios", color = ClinicalTextDark, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null, tint = ClinicalNavyHeader) },
                            onClick = { simViewModel.setScenarioPickerOpen(true) }
                        )
                        DropdownMenuItem(
                            text = { Text("Admin / Instructor Question Engine", color = ClinicalTextDark, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CapnoAmber) },
                            onClick = { simViewModel.setInstructorControlOpen(true) }
                        )
                        DropdownMenuItem(
                            text = { Text("Full-Screen Virtual Tablet", color = ClinicalTextDark, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Default.TabletMac, contentDescription = null, tint = ClinicalActiveBlue) },
                            onClick = {
                                simViewModel.setHamburgerMenuOpen(false)
                                simViewModel.setTabletOpen(true, fullScreen = true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Full-Screen ER Tactical Map", color = ClinicalTextDark, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = ClinicalNavyHeader) },
                            onClick = {
                                simViewModel.setHamburgerMenuOpen(false)
                                simViewModel.setMapFullScreen(true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("CODE 100 6-Drawer Crash Cart", color = ClinicalTextDark, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null, tint = ArterialRed) },
                            onClick = {
                                simViewModel.setHamburgerMenuOpen(false)
                                simViewModel.toggleCrashCartModal(true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (uiState.showJoystickControl) "Hide 3D Walking Joystick" else "Show 3D Walking Joystick", color = ClinicalTextDark) },
                            leadingIcon = { Icon(Icons.Default.Gamepad, contentDescription = null, tint = ClinicalNavyHeader) },
                            onClick = {
                                simViewModel.setHamburgerMenuOpen(false)
                                simViewModel.toggleJoystickVisibility()
                            }
                        )
                    }
                }
            }
            // Signature Coral/Peach Accent Line right below the Top Header (from Screenshot 1 & 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(ClinicalCoralAccent)
            )
        }

        // =========================================================================
        // 2. MAIN CLINICAL SIMULATION STAGE
        // =========================================================================
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // A. Realistic 3D-Panning ER Resuscitation Bay with Live In-World Monitor, Ventilator & Crash Cart
            ResusBay3DViewport(
                uiState = uiState,
                onCameraDrag = simViewModel::onCameraDrag,
                onJoystickMove = simViewModel::onJoystickMove,
                onOpenQuestionGate = simViewModel::openQuestionModal,
                onOpenCrashCart = { simViewModel.toggleCrashCartModal(true) },
                onOpenEquipmentStabilize = { simViewModel.selectSideDrawerTab(SideDrawerTab.STABILIZE) },
                modifier = Modifier.fillMaxSize()
            )

            // B. TOP-LEFT ATTENDING PHYSICIAN AVATAR + SPEECH BUBBLE (MATCHING SCREENSHOT 1)
            if (uiState.activeSideDrawer == null) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 24.dp, top = 12.dp)
                ) {
                    ClinicalCharacterAvatar(
                        drawableRes = R.drawable.img_avatar_attending_1795157816643,
                        roleLabel = "Attending\nPhysician",
                        ringColor = ClinicalNavyHeader,
                        onClick = { simViewModel.toggleAttendingBubble() },
                        modifier = Modifier.testTag("avatar_attending_physician")
                    )

                    // White Speech Bubble with Upward Pointer Triangle
                    AnimatedVisibility(
                        visible = uiState.showAttendingBubble,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .widthIn(max = 290.dp)
                                .clickable { simViewModel.toggleAttendingBubble() }
                        ) {
                            // Upward speech triangle
                            Canvas(
                                modifier = Modifier
                                    .padding(start = 26.dp)
                                    .size(width = 16.dp, height = 9.dp)
                            ) {
                                val p = Path().apply {
                                    moveTo(size.width / 2f, 0f)
                                    lineTo(size.width, size.height)
                                    lineTo(0f, size.height)
                                    close()
                                }
                                drawPath(p, Color.White)
                            }
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = 6.dp
                            ) {
                                Text(
                                    text = uiState.attendingBubbleText,
                                    color = ClinicalTextDark,
                                    fontSize = 14.sp,
                                    lineHeight = 19.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // C. TOP-RIGHT NURSE AVATAR + COMPACT ER MINI-MAP (WITH FULL-SCREEN OPTION)
            if (uiState.activeSideDrawer == null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 14.dp, top = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClinicalCharacterAvatar(
                            drawableRes = R.drawable.img_avatar_nurse_1795157816826,
                            roleLabel = "Nurse",
                            ringColor = ClinicalNavyHeader,
                            onClick = { simViewModel.selectSideDrawerTab(SideDrawerTab.COMMUNICATE) },
                            modifier = Modifier.testTag("avatar_nurse")
                        )

                        // Top-Right Corner ER Map (with Full-Screen Window Expand Option!)
                        ErTopRightMiniMap(
                            uiState = uiState,
                            onExpandFullScreen = { simViewModel.setMapFullScreen(true) },
                            onSelectQuestionOnMap = simViewModel::walkToQuestionStation,
                            onOpenCrashCart = { simViewModel.toggleCrashCartModal(true) }
                        )
                    }
                }
            }

            // D. SIGNATURE LEFT & RIGHT VERTICAL EDGE TABS (EXACT SCREENSHOT 1 FEEL)
            if (uiState.activeSideDrawer == null) {
                VerticalTabRail(
                    side = SideDrawerSide.LEFT,
                    activeTab = null,
                    onSelectTab = simViewModel::selectSideDrawerTab,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(top = 36.dp)
                )

                VerticalTabRail(
                    side = SideDrawerSide.RIGHT,
                    activeTab = null,
                    onSelectTab = simViewModel::selectSideDrawerTab,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = 36.dp)
                )
            }

            // E. BOTTOM FLOATING DARK-GLASS PILL CONTROL DOCK (MATCHING SCREENSHOT 1)
            if (uiState.activeSideDrawer == null) {
                Surface(
                    color = Color(0x99384048),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 14.dp)
                        .testTag("bottom_floating_dock")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        // Virtual Tablet / Clinical Chart Button
                        DockCircleButton(
                            icon = Icons.Default.Description,
                            contentDescription = "Open Full-Screen Virtual Tablet",
                            sizeDp = 38,
                            onClick = { simViewModel.setTabletOpen(true, fullScreen = true) },
                            testTag = "dock_tablet_btn"
                        )
                        // Turn Camera Left <
                        DockCircleButton(
                            icon = Icons.Default.ChevronLeft,
                            contentDescription = "Pan Camera Left",
                            sizeDp = 46,
                            onClick = { simViewModel.stepCameraYaw(-18f) },
                            testTag = "dock_pan_left_btn"
                        )
                        // Turn Camera Right >
                        DockCircleButton(
                            icon = Icons.Default.ChevronRight,
                            contentDescription = "Pan Camera Right",
                            sizeDp = 46,
                            onClick = { simViewModel.stepCameraYaw(18f) },
                            testTag = "dock_pan_right_btn"
                        )
                        // Live Resus Bay Audio Toggle
                        DockCircleButton(
                            icon = if (uiState.isAudioEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Resus Bay Audio",
                            sizeDp = 38,
                            iconTint = if (uiState.isAudioEnabled) ClinicalIconBlue else ClinicalTextMuted,
                            onClick = simViewModel::toggleAudio,
                            testTag = "dock_audio_btn"
                        )
                        // Toggle 3D Virtual Walking Joystick
                        DockCircleButton(
                            icon = Icons.Default.OpenWith,
                            contentDescription = "Toggle 3D Virtual Joystick",
                            sizeDp = 38,
                            iconTint = if (uiState.showJoystickControl) EcgEmerald else ClinicalIconBlue,
                            onClick = simViewModel::toggleJoystickVisibility,
                            testTag = "dock_joystick_toggle_btn"
                        )
                    }
                }
            }

            // F. SLIDING CRISP WHITE CLINICAL SIDE DRAWER (MATCHING SCREENSHOT 2!)
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

            // G. COMPACT CLINICAL TOAST FEEDBACK PILL
            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.toastBanner != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 80.dp)
            ) {
                uiState.toastBanner?.let { bannerText ->
                    Surface(
                        color = ClinicalNavyDark.copy(alpha = 0.94f),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .clickable { simViewModel.dismissToast() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcgEmerald, modifier = Modifier.size(16.dp))
                            Text(
                                text = bannerText,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // H. FULL-SCREEN / DOCKED VIRTUAL TABLET OVERLAY
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

@Composable
private fun ClinicalCharacterAvatar(
    drawableRes: Int,
    roleLabel: String,
    ringColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .shadow(5.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.5.dp, ringColor, CircleShape)
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = roleLabel,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(3.dp))
        Surface(
            color = Color.White.copy(alpha = 0.94f),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 3.dp
        ) {
            Text(
                text = roleLabel,
                color = ClinicalTextDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun DockCircleButton(
    icon: ImageVector,
    contentDescription: String,
    sizeDp: Int,
    iconTint: Color = ClinicalIconBlue,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = Color.White,
        shape = CircleShape,
        shadowElevation = 4.dp,
        modifier = Modifier
            .size(sizeDp.dp)
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size((sizeDp * 0.56f).dp)
            )
        }
    }
}
