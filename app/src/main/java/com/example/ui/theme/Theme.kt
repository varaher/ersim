package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private fun ErSimPalette.toColorScheme(): ColorScheme =
    if (isDark) darkColorScheme(
        primary = accent, onPrimary = Color(0xFF06211B),
        primaryContainer = raisedSurface, onPrimaryContainer = textPrimary,
        secondary = accent, onSecondary = Color(0xFF06211B),
        secondaryContainer = raisedSurface, onSecondaryContainer = textPrimary,
        tertiary = amber, onTertiary = Color(0xFF231800),
        background = bg, onBackground = textPrimary,
        surface = surface, onSurface = textPrimary,
        surfaceVariant = raisedSurface, onSurfaceVariant = textMuted,
        error = red, onError = Color(0xFF2B050B),
        outline = border
    ) else lightColorScheme(
        primary = accent, onPrimary = Color.White,
        primaryContainer = raisedSurface, onPrimaryContainer = textPrimary,
        secondary = accent, onSecondary = Color.White,
        secondaryContainer = raisedSurface, onSecondaryContainer = textPrimary,
        tertiary = amber, onTertiary = Color.White,
        background = bg, onBackground = textPrimary,
        surface = surface, onSurface = textPrimary,
        surfaceVariant = raisedSurface, onSurfaceVariant = textMuted,
        error = red, onError = Color.White,
        outline = border
    )

/**
 * [darkTheme] picks the palette. The ErSim* color names read ErSimThemeState,
 * so the palette is set here before any child reads it.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val palette = if (darkTheme) ErSimDarkPalette else ErSimLightPalette
    if (ErSimThemeState.palette != palette) {
        ErSimThemeState.palette = palette
    }
    MaterialTheme(
        colorScheme = palette.toColorScheme(),
        typography = Typography,
        content = content
    )
}
