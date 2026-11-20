package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ErSimV2DarkColorScheme = darkColorScheme(
    primary = ErSimMint,
    onPrimary = Color(0xFF06211B),
    primaryContainer = ErSimRaisedSurface,
    onPrimaryContainer = ErSimTextPrimary,
    secondary = ErSimMint,
    onSecondary = Color(0xFF06211B),
    secondaryContainer = ErSimRaisedSurface,
    onSecondaryContainer = ErSimTextPrimary,
    tertiary = ErSimAmber,
    onTertiary = Color(0xFF231800),
    background = ErSimBg,
    onBackground = ErSimTextPrimary,
    surface = ErSimSurface,
    onSurface = ErSimTextPrimary,
    surfaceVariant = ErSimRaisedSurface,
    onSurfaceVariant = ErSimTextMuted,
    error = ErSimRed,
    onError = Color(0xFF2B050B),
    outline = ErSimBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ErSimV2DarkColorScheme,
        typography = Typography,
        content = content
    )
}
