package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ER SIM Native Android UI V2 Palette (Exact Specification)
val ErSimBg = Color(0xFF07111B)
val ErSimSurface = Color(0xFF102234)
val ErSimRaisedSurface = Color(0xFF172E43)
val ErSimBorder = Color(0xFF29445A)
val ErSimMint = Color(0xFF51E3B2)
val ErSimTextPrimary = Color(0xFFE8F4FA)
val ErSimTextMuted = Color(0xFF92ABBA)
val ErSimRed = Color(0xFFFF687B)
val ErSimAmber = Color(0xFFFFCB74)
val ErSimVitalWell = Color(0xFF061723)
val ErSimWaveWell = Color(0xFF030D13)
val ErSimOutputWell = Color(0xFF0B1D2B)
val ErSimHeaderBg = Color(0xFF0D1C2B)

// Backward-compatible semantic aliases used by reusable drawers, modals & telemetry
val ResusNavyBg = ErSimBg
val ResusSurfaceDark = ErSimSurface
val ResusCardDark = ErSimRaisedSurface
val ResusBorderBlue = ErSimBorder

val TelemetryCyan = Color(0xFF00E5FF)
val EcgEmerald = ErSimMint
val ArterialRed = ErSimRed
val CapnoAmber = ErSimAmber
val Spo2Sky = Color(0xFF38BDF8)
val DefibOrange = Color(0xFFFB923C)
val ClinicalViolet = Color(0xFFA855F7)

val TextPrimaryLight = ErSimTextPrimary
val TextSecondaryMuted = ErSimTextMuted

// Clinical Drawer Aliases (mapped to V2 dark ER SIM theme for cohesive contrast)
val ClinicalNavyHeader = ErSimSurface
val ClinicalNavyDark = ErSimHeaderBg
val ClinicalCoralAccent = ErSimMint
val ClinicalActiveBlue = ErSimMint
val ClinicalIconBlue = ErSimMint

val ClinicalSheetWhite = ErSimSurface
val ClinicalSurfaceLight = ErSimRaisedSurface
val ClinicalDividerGray = ErSimBorder
val ClinicalTextDark = ErSimTextPrimary
val ClinicalTextMuted = ErSimTextMuted
val ClinicalItalicLabel = ErSimTextMuted
