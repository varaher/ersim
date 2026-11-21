package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// ============================================================================
// ER SIM PALETTES (Dark = vibrant clinical V2, Light = contrast-checked ≥4.5:1)
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
    val sky: Color,
    val cyan: Color,
    val violet: Color,
    val rose: Color,
    val orange: Color,
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
    accent = Color(0xFF34F5B2),      // Vibrant clinical emerald-mint
    textPrimary = Color(0xFFEAF6FC),
    textMuted = Color(0xFF93B0C2),
    red = Color(0xFFFF4D6D),         // Vivid critical crimson
    amber = Color(0xFFFFC857),       // Bright warning amber-gold
    sky = Color(0xFF38BDF8),         // Crisp oxygen / low-value sky blue
    cyan = Color(0xFF22D3EE),        // Electric telemetry cyan
    violet = Color(0xFFB388FF),      // Diagnostic / lab / neuro violet
    rose = Color(0xFFFB7185),        // Cardiac / paediatric rose
    orange = Color(0xFFFF9248),      // Defib / high-alert orange
    vitalWell = Color(0xFF061623),
    waveWell = Color(0xFF030D14),
    outputWell = Color(0xFF0A1B2A),
    headerBg = Color(0xFF0C1C2C)
)

val ErSimLightPalette = ErSimPalette(
    isDark = false,
    bg = Color(0xFFF1F6FA),
    surface = Color(0xFFFFFFFF),
    raisedSurface = Color(0xFFE6EFF6),
    border = Color(0xFFCBD9E6),
    accent = Color(0xFF047857),      // Deep emerald: >=4.5:1 on white AND on raised surface
    textPrimary = Color(0xFF0E1F2F),
    textMuted = Color(0xFF4F667A),
    red = Color(0xFFD11F3E),         // High-contrast clinical crimson
    amber = Color(0xFFA04A06),       // Deep warm amber (>=4.5:1 on raised surface)
    sky = Color(0xFF01699F),         // High-contrast clinical blue (>=4.5:1 on raised surface)
    cyan = Color(0xFF00727C),        // Deep telemetry teal-cyan (>=4.5:1 on raised surface)
    violet = Color(0xFF6D28D9),      // Deep clinical indigo-violet
    rose = Color(0xFFBE185D),        // Crisp rose
    orange = Color(0xFFC2410C),      // Deep alert orange
    // Monitor wells stay dark in both themes (real bedside monitors are dark)
    vitalWell = Color(0xFF061623),
    waveWell = Color(0xFF030D14),
    outputWell = Color(0xFF0A1B2A),
    headerBg = Color(0xFFFFFFFF)
)

/** Snapshot state: reading a color in composition/draw subscribes to theme changes. */
object ErSimThemeState {
    var palette by mutableStateOf(ErSimDarkPalette)
}

// ER SIM Native Android UI V2 tokens (theme-aware)
val ErSimBg: Color get() = ErSimThemeState.palette.bg
val ErSimSurface: Color get() = ErSimThemeState.palette.surface
val ErSimRaisedSurface: Color get() = ErSimThemeState.palette.raisedSurface
val ErSimBorder: Color get() = ErSimThemeState.palette.border
val ErSimMint: Color get() = ErSimThemeState.palette.accent
val ErSimTextPrimary: Color get() = ErSimThemeState.palette.textPrimary
val ErSimTextMuted: Color get() = ErSimThemeState.palette.textMuted
val ErSimRed: Color get() = ErSimThemeState.palette.red
val ErSimAmber: Color get() = ErSimThemeState.palette.amber
val ErSimSky: Color get() = ErSimThemeState.palette.sky
val ErSimCyan: Color get() = ErSimThemeState.palette.cyan
val ErSimViolet: Color get() = ErSimThemeState.palette.violet
val ErSimRose: Color get() = ErSimThemeState.palette.rose
val ErSimOrange: Color get() = ErSimThemeState.palette.orange
val ErSimVitalWell: Color get() = ErSimThemeState.palette.vitalWell
val ErSimWaveWell: Color get() = ErSimThemeState.palette.waveWell
val ErSimOutputWell: Color get() = ErSimThemeState.palette.outputWell
val ErSimHeaderBg: Color get() = ErSimThemeState.palette.headerBg

/** Text/icon colour on a solid accent fill (buttons, selected chips): dark ink on bright dark-theme accents, white on deep light-theme accents. */
val ErSimOnAccent: Color get() = if (ErSimThemeState.palette.isDark) Color(0xFF06151C) else Color.White

// Monitor-specific bright tokens (always rendered inside dark monitor wells)
val MonitorMint = Color(0xFF34F5B2)
val MonitorRed = Color(0xFFFF4D6D)
val MonitorAmber = Color(0xFFFFC857)
val MonitorSky = Color(0xFF38BDF8)
val MonitorCyan = Color(0xFF22D3EE)
val MonitorViolet = Color(0xFFB388FF)
val MonitorOrange = Color(0xFFFF9248)
val MonitorTextLight = Color(0xFFEAF6FC)
val MonitorTextMuted = Color(0xFF93B0C2)

// Backward-compatible semantic aliases used by reusable drawers, modals & telemetry
val ResusNavyBg: Color get() = ErSimBg
val ResusSurfaceDark: Color get() = ErSimSurface
val ResusCardDark: Color get() = ErSimRaisedSurface
val ResusBorderBlue: Color get() = ErSimBorder

val TelemetryCyan: Color get() = ErSimCyan
val EcgEmerald: Color get() = ErSimMint
val ArterialRed: Color get() = ErSimRed
val CapnoAmber: Color get() = ErSimAmber
val Spo2Sky: Color get() = ErSimSky
val DefibOrange: Color get() = ErSimOrange
val ClinicalViolet: Color get() = ErSimViolet

val TextPrimaryLight: Color get() = ErSimTextPrimary
val TextSecondaryMuted: Color get() = ErSimTextMuted

// Clinical Drawer Aliases (high-contrast header text on ClinicalSheetWhite)
val ClinicalNavyHeader: Color get() = ErSimCyan
val ClinicalNavyDark: Color get() = ErSimHeaderBg
val ClinicalCoralAccent: Color get() = ErSimRose
val ClinicalActiveBlue: Color get() = ErSimSky
val ClinicalIconBlue: Color get() = ErSimMint

val ClinicalSheetWhite: Color get() = ErSimSurface
val ClinicalSurfaceLight: Color get() = ErSimRaisedSurface
val ClinicalDividerGray: Color get() = ErSimBorder
val ClinicalTextDark: Color get() = ErSimTextPrimary
val ClinicalTextMuted: Color get() = ErSimTextMuted
val ClinicalItalicLabel: Color get() = ErSimTextMuted
