package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// ============================================================================
// ER SIM PALETTES (Dark = approved V2 spec, Light = contrast-checked ≥4.5:1)
// All ErSim* names below read the ACTIVE palette, so every existing screen
// switches theme with no other code changes. MyApplicationTheme sets it.
// ============================================================================

@Immutable
data class ErSimPalette(
    val isDark: Boolean,
    val bg: Color,
    val surface: Color,
    val raisedSurface: Color,
    val border: Color,
    val accent: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val red: Color,
    val amber: Color,
    val vitalWell: Color,
    val waveWell: Color,
    val outputWell: Color,
    val headerBg: Color
)

val ErSimDarkPalette = ErSimPalette(
    isDark = true,
    bg = Color(0xFF07111B),
    surface = Color(0xFF102234),
    raisedSurface = Color(0xFF172E43),
    border = Color(0xFF29445A),
    accent = Color(0xFF51E3B2),
    textPrimary = Color(0xFFE8F4FA),
    textMuted = Color(0xFF92ABBA),
    red = Color(0xFFFF687B),
    amber = Color(0xFFFFCB74),
    vitalWell = Color(0xFF061723),
    waveWell = Color(0xFF030D13),
    outputWell = Color(0xFF0B1D2B),
    headerBg = Color(0xFF0D1C2B)
)

val ErSimLightPalette = ErSimPalette(
    isDark = false,
    bg = Color(0xFFF3F6F9),
    surface = Color(0xFFFFFFFF),
    raisedSurface = Color(0xFFE9EFF4),
    border = Color(0xFFD2DCE5),
    accent = Color(0xFF087A5B),
    textPrimary = Color(0xFF0F1E2C),
    textMuted = Color(0xFF55687B),
    red = Color(0xFFC42A42),
    amber = Color(0xFF8A5400),
    // Monitor wells stay dark in both themes (real monitors are dark)
    vitalWell = Color(0xFF061723),
    waveWell = Color(0xFF030D13),
    outputWell = Color(0xFF0B1D2B),
    headerBg = Color(0xFFFFFFFF)
)

/** Snapshot state: reading a color in composition/draw subscribes to theme changes. */
object ErSimThemeState {
    var palette by mutableStateOf(ErSimDarkPalette)
}

// ER SIM Native Android UI V2 tokens (now theme-aware)
val ErSimBg: Color get() = ErSimThemeState.palette.bg
val ErSimSurface: Color get() = ErSimThemeState.palette.surface
val ErSimRaisedSurface: Color get() = ErSimThemeState.palette.raisedSurface
val ErSimBorder: Color get() = ErSimThemeState.palette.border
val ErSimMint: Color get() = ErSimThemeState.palette.accent
val ErSimTextPrimary: Color get() = ErSimThemeState.palette.textPrimary
val ErSimTextMuted: Color get() = ErSimThemeState.palette.textMuted
val ErSimRed: Color get() = ErSimThemeState.palette.red
val ErSimAmber: Color get() = ErSimThemeState.palette.amber
val ErSimVitalWell: Color get() = ErSimThemeState.palette.vitalWell
val ErSimWaveWell: Color get() = ErSimThemeState.palette.waveWell
val ErSimOutputWell: Color get() = ErSimThemeState.palette.outputWell
val ErSimHeaderBg: Color get() = ErSimThemeState.palette.headerBg

// Backward-compatible semantic aliases used by reusable drawers, modals & telemetry
val ResusNavyBg: Color get() = ErSimBg
val ResusSurfaceDark: Color get() = ErSimSurface
val ResusCardDark: Color get() = ErSimRaisedSurface
val ResusBorderBlue: Color get() = ErSimBorder

val TelemetryCyan = Color(0xFF00E5FF)
val EcgEmerald: Color get() = ErSimMint
val ArterialRed: Color get() = ErSimRed
val CapnoAmber: Color get() = ErSimAmber
val Spo2Sky = Color(0xFF38BDF8)
val DefibOrange = Color(0xFFFB923C)
val ClinicalViolet = Color(0xFFA855F7)

val TextPrimaryLight: Color get() = ErSimTextPrimary
val TextSecondaryMuted: Color get() = ErSimTextMuted

// Clinical Drawer Aliases
val ClinicalNavyHeader: Color get() = ErSimSurface
val ClinicalNavyDark: Color get() = ErSimHeaderBg
val ClinicalCoralAccent: Color get() = ErSimMint
val ClinicalActiveBlue: Color get() = ErSimMint
val ClinicalIconBlue: Color get() = ErSimMint

val ClinicalSheetWhite: Color get() = ErSimSurface
val ClinicalSurfaceLight: Color get() = ErSimRaisedSurface
val ClinicalDividerGray: Color get() = ErSimBorder
val ClinicalTextDark: Color get() = ErSimTextPrimary
val ClinicalTextMuted: Color get() = ErSimTextMuted
val ClinicalItalicLabel: Color get() = ErSimTextMuted
