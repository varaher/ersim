package com.example.ui.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.OralBoardCase
import com.example.data.OralBoardRepository
import com.example.data.PatientCharacterRegistry
import com.example.data.PatientSex
import com.example.ui.theme.*
import java.util.Calendar

// ============================================================================
// APP SHELL: Home • Cases • Profile • Settings (+ About, Onboarding)
// ============================================================================

enum class AppTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    CASES("Cases", Icons.AutoMirrored.Filled.MenuBook),
    PROFILE("Profile", Icons.Default.Person),
    SETTINGS("Settings", Icons.Default.Settings)
}

enum class CaseFilter(val label: String) { ALL("All"), ADULT("Adult"), PEDIATRIC("Paediatric"), NOT_STARTED("New to me") }

@Composable
fun ErSimAppShell(
    appState: AppPrefsState,
    lastCaseTitle: String?,
    onStartCase: (Int) -> Unit,
    onResumeLastCase: () -> Unit,
    onUpdateProfile: (UserProfile) -> Unit,
    onUpdateSettings: (AppSettings) -> Unit,
    onResetProgress: () -> Unit,
    onResetEverything: () -> Unit,
    onLogOut: () -> Unit
) {
    var tab by rememberSaveable { mutableStateOf(AppTab.HOME) }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    var caseFilter by rememberSaveable { mutableStateOf(CaseFilter.ALL) }

    if (showAbout) {
        androidx.activity.compose.BackHandler { showAbout = false }
        AboutScreen(onBack = { showAbout = false })
        return
    }
    if (tab != AppTab.HOME) {
        androidx.activity.compose.BackHandler { tab = AppTab.HOME }
    }

    Scaffold(
        containerColor = ErSimBg,
        bottomBar = {
            NavigationBar(containerColor = ErSimHeaderBg, tonalElevation = 0.dp) {
                AppTab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ErSimBg,
                            selectedTextColor = ErSimMint,
                            indicatorColor = ErSimMint,
                            unselectedIconColor = ErSimTextMuted,
                            unselectedTextColor = ErSimTextMuted
                        ),
                        modifier = Modifier.testTag("nav_${t.name.lowercase()}")
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (tab) {
                AppTab.HOME -> HomeScreen(
                    appState = appState,
                    lastCaseTitle = lastCaseTitle,
                    onResumeLastCase = onResumeLastCase,
                    onBrowseCases = { filter -> caseFilter = filter; tab = AppTab.CASES },
                    onOpenProfile = { tab = AppTab.PROFILE },
                    onOpenAbout = { showAbout = true }
                )
                AppTab.CASES -> CaseLibraryScreen(
                    progress = appState.progress,
                    filter = caseFilter,
                    onFilterChange = { caseFilter = it },
                    onStartCase = onStartCase
                )
                AppTab.PROFILE -> ProfileScreen(
                    appState = appState,
                    onSave = onUpdateProfile,
                    onLogOut = onLogOut
                )
                AppTab.SETTINGS -> SettingsScreen(
                    settings = appState.settings,
                    onUpdateSettings = onUpdateSettings,
                    onResetProgress = onResetProgress,
                    onResetEverything = onResetEverything,
                    onLogOut = onLogOut,
                    onOpenAbout = { showAbout = true }
                )
            }
        }
    }
}

// ============================================================================
// HOME
// ============================================================================

@Composable
private fun HomeScreen(
    appState: AppPrefsState,
    lastCaseTitle: String?,
    onResumeLastCase: () -> Unit,
    onBrowseCases: (CaseFilter) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val profile = appState.profile
    val progress = appState.progress
    val totalCases = OralBoardRepository.allCases.size
    val pedsCount = OralBoardRepository.allCases.count { it.isPediatricOrInfant }
    val firstName = profile.displayName.trim().substringBefore(" ")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("home_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                BrandWordmark(size = 24)
                Text("Immersive emergency medicine training", color = ErSimTextMuted, fontSize = 12.sp)
            }
            AvatarCircle(initials = profile.initials, size = 42, modifier = Modifier.clickable { onOpenProfile() })
        }

        Column {
            Text(
                text = greeting() + if (firstName.isNotEmpty()) ", $firstName" else "",
                color = ErSimTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(text = "Ready for your next patient?", color = ErSimTextMuted, fontSize = 14.sp)
        }

        if (progress.lastCaseNumber != null && lastCaseTitle != null) {
            AppCard(borderColor = ErSimMint) {
                Text("CONTINUE", color = ErSimMint, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                Spacer(Modifier.height(4.dp))
                Text(lastCaseTitle, color = ErSimTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                PrimaryButton(text = "Resume case", icon = Icons.Default.PlayArrow, onClick = onResumeLastCase)
            }
        }

        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(Icons.Default.LocalHospital)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Start a new case", color = ErSimTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("$totalCases emergency scenarios • adult & paediatric", color = ErSimTextMuted, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SecondaryButton("All cases", Modifier.weight(1f)) { onBrowseCases(CaseFilter.ALL) }
                SecondaryButton("Adult", Modifier.weight(1f)) { onBrowseCases(CaseFilter.ADULT) }
                SecondaryButton("Paediatric", Modifier.weight(1f)) { onBrowseCases(CaseFilter.PEDIATRIC) }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile("${progress.startedCaseNumbers.size}/$totalCases", "Cases tried", Modifier.weight(1f))
            StatTile("${progress.sessionsCount}", "Sessions", Modifier.weight(1f))
            StatTile("$pedsCount", "Paediatric", Modifier.weight(1f))
        }

        AppCard {
            Text("How a case works", color = ErSimTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            HowStep(1, "Enter the bay", "Answer the entry question (Q0) to see the patient and monitor.")
            HowStep(2, "Assess and examine", "Work through XABCDE, tap the patient to examine, unlock labs and imaging.")
            HowStep(3, "Treat and stabilise", "Use the crash cart, ventilator and defibrillator. Complete critical actions.")
            HowStep(4, "Debrief", "End the case to review actions and hand off.")
        }

        Text("For supervised education only. Not for real patient care decisions.", color = ErSimTextMuted, fontSize = 11.sp)
        Text(
            text = "About ER SIM",
            color = ErSimMint,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onOpenAbout() }
                .padding(vertical = 6.dp)
        )
    }
}

@Composable
private fun HowStep(n: Int, title: String, body: String) {
    Row(modifier = Modifier.padding(vertical = 5.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .background(ErSimRaisedSurface, CircleShape)
                .border(1.dp, ErSimMint, CircleShape)
        ) {
            Text("$n", color = ErSimMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = ErSimTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(body, color = ErSimTextMuted, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

private fun greeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    else -> "Good evening"
}

// ============================================================================
// CASE LIBRARY (no diagnoses shown: no spoilers)
// ============================================================================

@Composable
private fun CaseLibraryScreen(
    progress: CaseProgress,
    filter: CaseFilter,
    onFilterChange: (CaseFilter) -> Unit,
    onStartCase: (Int) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val cases = remember(filter, query, progress.startedCaseNumbers) {
        OralBoardRepository.allCases.filter { c ->
            val f = when (filter) {
                CaseFilter.ALL -> true
                CaseFilter.ADULT -> !c.isPediatricOrInfant
                CaseFilter.PEDIATRIC -> c.isPediatricOrInfant
                CaseFilter.NOT_STARTED -> c.caseNumber !in progress.startedCaseNumbers
            }
            f && (query.isBlank() || c.scenarioCategory.contains(query.trim(), ignoreCase = true) ||
                c.caseNumber.toString() == query.trim())
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("case_library_screen")) {
        Spacer(Modifier.height(12.dp))
        ScreenTitle("Case library", "Pick a patient. Diagnosis is revealed only in the debrief.")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = { Text("Search by complaint or case number", color = ErSimTextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ErSimTextMuted) },
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            CaseFilter.entries.forEach { f ->
                FilterChip(
                    selected = filter == f,
                    onClick = { onFilterChange(f) },
                    label = { Text(f.label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = ErSimSurface,
                        labelColor = ErSimTextMuted,
                        selectedContainerColor = ErSimMint,
                        selectedLabelColor = ErSimBg
                    )
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        if (cases.isEmpty()) {
            Text("No cases match.", color = ErSimTextMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 24.dp))
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(cases, key = { it.caseNumber }) { c ->
                CaseRow(c, started = c.caseNumber in progress.startedCaseNumbers, onClick = { onStartCase(c.caseNumber) })
            }
        }
    }
}

@Composable
private fun CaseRow(case: OralBoardCase, started: Boolean, onClick: () -> Unit) {
    val character = remember(case.caseNumber) { PatientCharacterRegistry.characterFor(case) }
    val sexLabel = if (character.sex == PatientSex.FEMALE) "Female" else "Male"
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("case_row_${case.caseNumber}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(14.dp)) {
            IconBadge(if (case.isPediatricOrInfant) Icons.Default.ChildCare else Icons.Default.Person)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("CASE ${case.caseNumber}", color = ErSimMint, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Text(case.scenarioCategory, color = ErSimTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${character.ageBand.label} • $sexLabel", color = ErSimTextMuted, fontSize = 12.sp)
            }
            if (started) {
                Text("TRIED", color = ErSimAmber, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(end = 8.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Start case", tint = ErSimTextMuted)
        }
    }
}

// ============================================================================
// PROFILE
// ============================================================================

@Composable
private fun ProfileScreen(appState: AppPrefsState, onSave: (UserProfile) -> Unit, onLogOut: () -> Unit) {
    var confirmLogOut by remember { mutableStateOf(false) }
    var name by rememberSaveable(appState.profile) { mutableStateOf(appState.profile.displayName) }
    var institution by rememberSaveable(appState.profile) { mutableStateOf(appState.profile.institution) }
    var role by remember(appState.profile) { mutableStateOf(appState.profile.role) }
    var language by remember(appState.profile) { mutableStateOf(appState.profile.preferredLanguage) }
    var showRolePicker by remember { mutableStateOf(false) }
    var showLangPicker by remember { mutableStateOf(false) }
    var savedNote by remember { mutableStateOf(false) }
    val draft = UserProfile(name.trim(), role, institution.trim(), language)
    val dirty = draft != appState.profile

    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle(initials = draft.initials, size = 64)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(draft.displayName.ifBlank { "Your name" }, color = ErSimTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(role.label, color = ErSimMint, fontSize = 13.sp)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatTile("${appState.progress.startedCaseNumbers.size}", "Cases tried", Modifier.weight(1f))
            StatTile("${appState.progress.sessionsCount}", "Sessions", Modifier.weight(1f))
        }

        SectionLabel("ABOUT YOU")
        OutlinedTextField(
            value = name, onValueChange = { name = it; savedNote = false },
            label = { Text("Full name") }, singleLine = true,
            colors = appTextFieldColors(), shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        PickerField("Role", role.label) { showRolePicker = true }
        OutlinedTextField(
            value = institution, onValueChange = { institution = it; savedNote = false },
            label = { Text("Hospital / college (optional)") }, singleLine = true,
            colors = appTextFieldColors(), shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        PickerField("Preferred language", language) { showLangPicker = true }
        Text("Language is saved now and will be used when voice patients arrive.", color = ErSimTextMuted, fontSize = 11.sp)

        PrimaryButton(
            text = if (savedNote && !dirty) "Saved" else "Save profile",
            icon = Icons.Default.Check,
            enabled = dirty,
            onClick = { onSave(draft); savedNote = true }
        )
        Text("Your profile is stored only on this device.", color = ErSimTextMuted, fontSize = 11.sp)

        ActionRow(Icons.AutoMirrored.Filled.Logout, "Log out", "Sign out of ER SIM on this device", danger = true) {
            confirmLogOut = true
        }
    }

    if (confirmLogOut) {
        LogOutDialog(onConfirm = { confirmLogOut = false; onLogOut() }, onDismiss = { confirmLogOut = false })
    }

    if (showRolePicker) {
        OptionPickerDialog(
            title = "Your role",
            options = LearnerRole.entries.map { it.label },
            selected = role.label,
            onSelect = { label -> role = LearnerRole.entries.first { it.label == label }; savedNote = false },
            onDismiss = { showRolePicker = false }
        )
    }
    if (showLangPicker) {
        OptionPickerDialog(
            title = "Preferred language",
            options = SupportedLanguages,
            selected = language,
            onSelect = { language = it; savedNote = false },
            onDismiss = { showLangPicker = false }
        )
    }
}

// ============================================================================
// SETTINGS
// ============================================================================

@Composable
private fun SettingsScreen(
    settings: AppSettings,
    onUpdateSettings: (AppSettings) -> Unit,
    onResetProgress: () -> Unit,
    onResetEverything: () -> Unit,
    onLogOut: () -> Unit,
    onOpenAbout: () -> Unit
) {
    var confirmLogOut by remember { mutableStateOf(false) }
    var confirm by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        ScreenTitle("Settings", null)

        SectionLabel("APPEARANCE")
        ThemeModeSelector(
            selected = settings.themeMode,
            onSelect = { onUpdateSettings(settings.copy(themeMode = it)) }
        )
        Text(
            "The resuscitation bay always uses dark mode, like a real monitor.",
            color = ErSimTextMuted, fontSize = 11.sp
        )

        SectionLabel("SIMULATION")
        ToggleRow(
            Icons.AutoMirrored.Filled.VolumeUp, "Sound & alarms", "Monitor beeps and alarm sounds in the bay",
            settings.soundEnabled
        ) { onUpdateSettings(settings.copy(soundEnabled = it)) }
        ToggleRow(
            Icons.Default.Gamepad, "Show movement joystick", "On-screen joystick for moving around the bay",
            settings.showJoystickByDefault
        ) { onUpdateSettings(settings.copy(showJoystickByDefault = it)) }
        ToggleRow(
            Icons.Default.LightMode, "Keep screen on", "Prevent the screen from sleeping during a case",
            settings.keepScreenOnInCase
        ) { onUpdateSettings(settings.copy(keepScreenOnInCase = it)) }

        SectionLabel("DATA")
        ActionRow(Icons.Default.Refresh, "Reset case progress", "Clears cases tried and session count") { confirm = "progress" }
        ActionRow(Icons.Default.DeleteForever, "Reset app", "Clears profile, settings and progress", danger = true) { confirm = "all" }

        SectionLabel("INFORMATION")
        ActionRow(Icons.Default.Info, "About ER SIM", "Version, purpose and disclaimer") { onOpenAbout() }

        SectionLabel("ACCOUNT")
        ActionRow(Icons.AutoMirrored.Filled.Logout, "Log out", "Sign out of ER SIM on this device", danger = true) {
            confirmLogOut = true
        }

        Spacer(Modifier.height(8.dp))
        Text("Version ${BuildConfig.VERSION_NAME}", color = ErSimTextMuted, fontSize = 11.sp)
    }

    if (confirmLogOut) {
        LogOutDialog(onConfirm = { confirmLogOut = false; onLogOut() }, onDismiss = { confirmLogOut = false })
    }

    confirm?.let { which ->
        AlertDialog(
            onDismissRequest = { confirm = null },
            containerColor = ErSimSurface,
            title = { Text(if (which == "all") "Reset app?" else "Reset progress?", color = ErSimTextPrimary) },
            text = {
                Text(
                    if (which == "all") "Profile, settings and progress will be deleted from this device."
                    else "Cases tried and session count will be cleared.",
                    color = ErSimTextMuted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (which == "all") onResetEverything() else onResetProgress()
                    confirm = null
                }) { Text("Reset", color = ErSimRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirm = null }) { Text("Cancel", color = ErSimTextMuted) }
            }
        )
    }
}

@Composable
private fun LogOutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ErSimSurface,
        title = { Text("Log out?", color = ErSimTextPrimary) },
        text = {
            Text(
                "Your profile and case progress on this device will be cleared. Display settings are kept.",
                color = ErSimTextMuted
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, modifier = Modifier.testTag("logout_confirm")) {
                Text("Log out", color = ErSimRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = ErSimTextMuted) }
        }
    )
}

// ============================================================================
// ABOUT
// ============================================================================

@Composable
private fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ErSimBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("about_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ErSimTextPrimary)
            }
            Text("About", color = ErSimTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }

        AppCard {
            BrandWordmark(size = 28)
            Text("Immersive emergency medicine simulation", color = ErSimTextMuted, fontSize = 13.sp)
            Spacer(Modifier.height(6.dp))
            Text("Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})", color = ErSimTextMuted, fontSize = 12.sp)
        }

        AboutBlock(
            "What it is",
            "ER SIM puts you in a resuscitation bay with a virtual patient. Assess, examine, investigate, treat and reassess, then review your actions in the debrief."
        )
        AboutBlock(
            "Built for Indian emergency care",
            "Indian patients, settings and languages. Voice-interactive patients in English and Malayalam are planned."
        )
        AboutBlock(
            "Educational use only",
            "ER SIM is a supervised training tool. It is not a medical device and must not be used for real patient diagnosis or treatment. Drug doses and protocols should be checked against current local guidelines."
        )
        AboutBlock("Privacy", "Your profile, settings and progress are stored only on this device. Nothing is uploaded.")
        AboutBlock("Developed by", "Varah Health and Technology Private Limited, Thrissur, Kerala.")
    }
}

@Composable
private fun AboutBlock(title: String, body: String) {
    AppCard {
        Text(title, color = ErSimMint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(body, color = ErSimTextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
    }
}

// ============================================================================
// ONBOARDING (first launch)
// ============================================================================

@Composable
fun OnboardingScreen(onDone: (UserProfile) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var role by remember { mutableStateOf(LearnerRole.MEDICAL_STUDENT) }
    var accepted by rememberSaveable { mutableStateOf(false) }
    var showRolePicker by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(ErSimBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .testTag("onboarding_screen")
    ) {
        Spacer(Modifier.height(16.dp))
        IconBadge(Icons.Default.MonitorHeart, size = 56)
        BrandWordmark(size = 34)
        Text(
            "Step into the resus bay. Talk to the patient, examine, decide, act and reassess.",
            color = ErSimTextMuted, fontSize = 15.sp, lineHeight = 21.sp
        )

        SectionLabel("ABOUT YOU")
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Your name") }, singleLine = true,
            colors = appTextFieldColors(), shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        PickerField("Role", role.label) { showRolePicker = true }

        Surface(
            color = ErSimSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, if (accepted) ErSimMint else ErSimBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { accepted = !accepted }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                Checkbox(
                    checked = accepted,
                    onCheckedChange = { accepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = ErSimMint, checkmarkColor = ErSimBg, uncheckedColor = ErSimTextMuted)
                )
                Text(
                    "I understand ER SIM is for education only and not for real patient care decisions.",
                    color = ErSimTextPrimary, fontSize = 13.sp, lineHeight = 18.sp
                )
            }
        }

        PrimaryButton(
            text = "Get started",
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            enabled = accepted,
            onClick = { onDone(UserProfile(displayName = name.trim(), role = role)) }
        )
        Text("You can change these later in Profile.", color = ErSimTextMuted, fontSize = 11.sp)
    }

    if (showRolePicker) {
        OptionPickerDialog(
            title = "Your role",
            options = LearnerRole.entries.map { it.label },
            selected = role.label,
            onSelect = { label -> role = LearnerRole.entries.first { it.label == label } },
            onDismiss = { showRolePicker = false }
        )
    }
}

// ============================================================================
// SHARED COMPONENTS
// ============================================================================

@Composable
private fun BrandWordmark(size: Int) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = ErSimTextPrimary, fontWeight = FontWeight.Black)) { append("ER ") }
            withStyle(SpanStyle(color = ErSimMint, fontWeight = FontWeight.Black)) { append("SIM") }
        },
        fontSize = size.sp,
        letterSpacing = 1.6.sp
    )
}

@Composable
private fun AvatarCircle(initials: String, size: Int, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .background(ErSimRaisedSurface, CircleShape)
            .border(1.5.dp, ErSimMint, CircleShape)
    ) {
        Text(initials, color = ErSimMint, fontSize = (size * 0.36f).sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun IconBadge(icon: ImageVector, size: Int = 42) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .background(ErSimRaisedSurface, RoundedCornerShape(12.dp))
    ) {
        Icon(icon, contentDescription = null, tint = ErSimMint, modifier = Modifier.size((size * 0.5f).dp))
    }
}

@Composable
private fun AppCard(borderColor: Color = ErSimBorder, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp)) {
            Text(value, color = ErSimTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = ErSimTextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ScreenTitle(title: String, subtitle: String?) {
    Column {
        Text(title, color = ErSimTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        if (subtitle != null) Text(subtitle, color = ErSimTextMuted, fontSize = 13.sp)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text, color = ErSimTextMuted, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.2.sp, modifier = Modifier.padding(top = 6.dp)
    )
}

@Composable
private fun PrimaryButton(text: String, icon: ImageVector, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ErSimMint,
            contentColor = ErSimBg,
            disabledContainerColor = ErSimRaisedSurface,
            disabledContentColor = ErSimTextMuted
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun SecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErSimTextPrimary),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp),
        modifier = modifier
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun ToggleRow(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChange(!checked) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Icon(icon, contentDescription = null, tint = ErSimMint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = ErSimTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = ErSimTextMuted, fontSize = 12.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ErSimBg,
                    checkedTrackColor = ErSimMint,
                    uncheckedThumbColor = ErSimTextMuted,
                    uncheckedTrackColor = ErSimRaisedSurface
                )
            )
        }
    }
}

@Composable
private fun ThemeModeSelector(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(6.dp)) {
            ThemeMode.entries.forEach { mode ->
                val isOn = mode == selected
                val icon = when (mode) {
                    ThemeMode.SYSTEM -> Icons.Default.PhoneAndroid
                    ThemeMode.LIGHT -> Icons.Default.LightMode
                    ThemeMode.DARK -> Icons.Default.DarkMode
                }
                Surface(
                    color = if (isOn) ErSimMint else Color.Transparent,
                    contentColor = if (isOn) ErSimBg else ErSimTextPrimary,
                    shape = RoundedCornerShape(9.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable { onSelect(mode) }
                        .testTag("theme_${mode.name.lowercase()}")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(mode.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionRow(icon: ImageVector, title: String, subtitle: String, danger: Boolean = false, onClick: () -> Unit) {
    Surface(
        color = ErSimSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
            Icon(icon, contentDescription = null, tint = if (danger) ErSimRed else ErSimMint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = if (danger) ErSimRed else ErSimTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = ErSimTextMuted, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ErSimTextMuted)
        }
    }
}

@Composable
private fun PickerField(label: String, value: String, onClick: () -> Unit) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ErSimBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = ErSimTextMuted, fontSize = 11.sp)
                Text(value, color = ErSimTextPrimary, fontSize = 15.sp)
            }
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose $label", tint = ErSimTextMuted)
        }
    }
}

@Composable
private fun OptionPickerDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ErSimSurface,
        title = { Text(title, color = ErSimTextPrimary) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option); onDismiss() }
                            .padding(vertical = 10.dp)
                    ) {
                        RadioButton(
                            selected = option == selected,
                            onClick = { onSelect(option); onDismiss() },
                            colors = RadioButtonDefaults.colors(selectedColor = ErSimMint, unselectedColor = ErSimTextMuted)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(option, color = ErSimTextPrimary, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = ErSimMint) }
        }
    )
}

@Composable
private fun appTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ErSimTextPrimary,
    unfocusedTextColor = ErSimTextPrimary,
    focusedBorderColor = ErSimMint,
    unfocusedBorderColor = ErSimBorder,
    cursorColor = ErSimMint,
    focusedLabelColor = ErSimMint,
    unfocusedLabelColor = ErSimTextMuted,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent
)
