package com.example.ui.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ============================================================================
// APP-LEVEL STATE: profile, settings, onboarding, case progress.
// Stored on-device only (SharedPreferences). No new dependencies.
// Clinical simulation state stays in SimViewModel.
// ============================================================================

enum class LearnerRole(val label: String) {
    MEDICAL_STUDENT("Medical student"),
    INTERN("Intern / House surgeon"),
    EM_RESIDENT("Emergency Medicine resident"),
    OTHER_RESIDENT("Resident (other specialty)"),
    EM_CONSULTANT("Emergency physician / Consultant"),
    NURSE("Nurse"),
    PARAMEDIC("Paramedic / EMT"),
    INSTRUCTOR("Instructor / Faculty"),
    OTHER("Other")
}

val SupportedLanguages = listOf(
    "English", "Malayalam", "Hindi", "Tamil", "Telugu", "Kannada", "Bengali", "Marathi"
)

data class UserProfile(
    val displayName: String = "",
    val role: LearnerRole = LearnerRole.MEDICAL_STUDENT,
    val institution: String = "",
    val preferredLanguage: String = "English"
) {
    val initials: String
        get() = displayName.trim().split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "ER" }
}

enum class ThemeMode(val label: String) { SYSTEM("System"), LIGHT("Light"), DARK("Dark") }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val soundEnabled: Boolean = true,
    val showJoystickByDefault: Boolean = false,
    val keepScreenOnInCase: Boolean = true
)

data class CaseProgress(
    val startedCaseNumbers: Set<Int> = emptySet(),
    val sessionsCount: Int = 0,
    val lastCaseNumber: Int? = null,
    val lastPlayedAtMillis: Long = 0L,
    val bestStars: Map<String, Int> = emptyMap()      // interactive caseId -> best stars (0-3)
)

data class AppPrefsState(
    val onboardingDone: Boolean = false,
    val disclaimerAccepted: Boolean = false,
    val seenGameTutorial: Boolean = false,
    val profile: UserProfile = UserProfile(),
    val settings: AppSettings = AppSettings(),
    val progress: CaseProgress = CaseProgress()
)

class AppPrefsRepository(context: Context) {
    private val sp = context.getSharedPreferences("ersim_app_prefs", Context.MODE_PRIVATE)

    fun load(): AppPrefsState = AppPrefsState(
        onboardingDone = sp.getBoolean(K_ONBOARD, false),
        disclaimerAccepted = sp.getBoolean(K_DISCLAIMER, false),
        seenGameTutorial = sp.getBoolean(K_TUTORIAL, false),
        profile = UserProfile(
            displayName = sp.getString(K_NAME, "") ?: "",
            role = runCatching { LearnerRole.valueOf(sp.getString(K_ROLE, null) ?: "") }
                .getOrDefault(LearnerRole.MEDICAL_STUDENT),
            institution = sp.getString(K_INSTITUTION, "") ?: "",
            preferredLanguage = sp.getString(K_LANGUAGE, "English") ?: "English"
        ),
        settings = AppSettings(
            themeMode = runCatching { ThemeMode.valueOf(sp.getString(K_THEME, null) ?: "") }
                .getOrDefault(ThemeMode.SYSTEM),
            soundEnabled = sp.getBoolean(K_SOUND, true),
            showJoystickByDefault = sp.getBoolean(K_JOYSTICK, false),
            keepScreenOnInCase = sp.getBoolean(K_SCREEN_ON, true)
        ),
        progress = CaseProgress(
            startedCaseNumbers = (sp.getStringSet(K_STARTED, emptySet()) ?: emptySet())
                .mapNotNull { it.toIntOrNull() }.toSet(),
            sessionsCount = sp.getInt(K_SESSIONS, 0),
            lastCaseNumber = sp.getInt(K_LAST_CASE, -1).takeIf { it > 0 },
            lastPlayedAtMillis = sp.getLong(K_LAST_AT, 0L),
            bestStars = (sp.getStringSet(K_STARS, emptySet()) ?: emptySet()).mapNotNull { e ->
                val id = e.substringBeforeLast("=", "")
                val n = e.substringAfterLast("=", "").toIntOrNull()
                if (id.isNotEmpty() && n != null) id to n else null
            }.toMap()
        )
    )

    fun save(s: AppPrefsState) {
        sp.edit()
            .putBoolean(K_ONBOARD, s.onboardingDone)
            .putBoolean(K_DISCLAIMER, s.disclaimerAccepted)
            .putBoolean(K_TUTORIAL, s.seenGameTutorial)
            .putString(K_NAME, s.profile.displayName)
            .putString(K_ROLE, s.profile.role.name)
            .putString(K_INSTITUTION, s.profile.institution)
            .putString(K_LANGUAGE, s.profile.preferredLanguage)
            .putString(K_THEME, s.settings.themeMode.name)
            .putBoolean(K_SOUND, s.settings.soundEnabled)
            .putBoolean(K_JOYSTICK, s.settings.showJoystickByDefault)
            .putBoolean(K_SCREEN_ON, s.settings.keepScreenOnInCase)
            .putStringSet(K_STARTED, s.progress.startedCaseNumbers.map { it.toString() }.toSet())
            .putInt(K_SESSIONS, s.progress.sessionsCount)
            .putInt(K_LAST_CASE, s.progress.lastCaseNumber ?: -1)
            .putLong(K_LAST_AT, s.progress.lastPlayedAtMillis)
            .putStringSet(K_STARS, s.progress.bestStars.map { (id, n) -> "$id=$n" }.toSet())
            .apply()
    }

    fun clear() {
        sp.edit().clear().apply()
    }

    private companion object {
        const val K_ONBOARD = "onboarding_done"
        const val K_DISCLAIMER = "disclaimer_accepted"
        const val K_TUTORIAL = "seen_game_tutorial"
        const val K_NAME = "profile_name"
        const val K_ROLE = "profile_role"
        const val K_INSTITUTION = "profile_institution"
        const val K_LANGUAGE = "profile_language"
        const val K_THEME = "settings_theme"
        const val K_SOUND = "settings_sound"
        const val K_JOYSTICK = "settings_joystick"
        const val K_SCREEN_ON = "settings_screen_on"
        const val K_STARTED = "progress_started_cases"
        const val K_SESSIONS = "progress_sessions"
        const val K_LAST_CASE = "progress_last_case"
        const val K_LAST_AT = "progress_last_at"
        const val K_STARS = "progress_best_stars"
    }
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = AppPrefsRepository(application)
    private val _state = MutableStateFlow(repo.load())
    val state: StateFlow<AppPrefsState> = _state.asStateFlow()

    private fun mutate(transform: (AppPrefsState) -> AppPrefsState) {
        _state.update(transform)
        repo.save(_state.value)
    }

    fun completeOnboarding(profile: UserProfile) = mutate {
        it.copy(onboardingDone = true, disclaimerAccepted = true, profile = profile)
    }

    fun markTutorialSeen() = mutate { it.copy(seenGameTutorial = true) }

    fun updateProfile(profile: UserProfile) = mutate { it.copy(profile = profile) }

    fun updateSettings(settings: AppSettings) = mutate { it.copy(settings = settings) }

    fun recordCaseStarted(caseNumber: Int) = mutate {
        it.copy(
            progress = it.progress.copy(
                startedCaseNumbers = it.progress.startedCaseNumbers + caseNumber,
                sessionsCount = it.progress.sessionsCount + 1,
                lastCaseNumber = caseNumber,
                lastPlayedAtMillis = System.currentTimeMillis()
            )
        )
    }

    fun resetProgress() = mutate { it.copy(progress = CaseProgress()) }

    /** Interactive case finished: keep the best star rating and count the session. */
    fun recordInteractiveResult(caseId: String, stars: Int) = mutate {
        val best = maxOf(stars, it.progress.bestStars[caseId] ?: 0)
        it.copy(
            progress = it.progress.copy(
                bestStars = it.progress.bestStars + (caseId to best),
                sessionsCount = it.progress.sessionsCount + 1,
                lastPlayedAtMillis = System.currentTimeMillis()
            )
        )
    }

    /** Log out: clears this device's profile and progress, keeps display settings, returns to Welcome. */
    fun logOut() = mutate { AppPrefsState(settings = it.settings) }

    fun resetEverything() {
        repo.clear()
        _state.value = AppPrefsState()
    }
}
