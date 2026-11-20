package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClinicalLightColorScheme = lightColorScheme(
    primary = ClinicalActiveBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDFF3FD),
    onPrimaryContainer = ClinicalNavyDark,
    secondary = ClinicalNavyHeader,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6EEF5),
    onSecondaryContainer = ClinicalNavyDark,
    tertiary = ClinicalCoralAccent,
    onTertiary = Color.White,
    background = ClinicalSurfaceLight,
    onBackground = ClinicalTextDark,
    surface = ClinicalSheetWhite,
    onSurface = ClinicalTextDark,
    surfaceVariant = ClinicalSurfaceLight,
    onSurfaceVariant = ClinicalTextMuted,
    error = ArterialRed,
    onError = Color.White,
    outline = ClinicalDividerGray
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClinicalLightColorScheme,
        typography = Typography,
        content = content
    )
}
