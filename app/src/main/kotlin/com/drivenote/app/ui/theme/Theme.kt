package com.drivenote.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorWhite = Color(0xFFFFFFFF)
private val ColorBlack = Color(0xFF111418)

private val LightScheme = lightColorScheme(
    primary = CalmMintContainer,
    onPrimary = NightBackground,
    primaryContainer = CalmMint,
    onPrimaryContainer = NightBackground,
    secondary = SlateBlue,
    onSecondary = NightBackground,
    tertiary = WarmSand,
    onTertiary = NightBackground,
    background = ColorWhite,
    onBackground = ColorBlack,
    surface = ColorWhite,
    onSurface = ColorBlack,
    surfaceVariant = Color(0xFFE9EEF5),
    onSurfaceVariant = Color(0xFF354252),
    outline = Color(0xFF68788A),
    error = Color(0xFFBA1A1A),
    onError = ColorWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val DarkScheme = darkColorScheme(
    primary = CalmMint,
    onPrimary = NightBackground,
    primaryContainer = CalmMintContainer,
    onPrimaryContainer = Color(0xFFC2F0E6),
    secondary = SlateBlue,
    onSecondary = NightBackground,
    tertiary = WarmSand,
    onTertiary = NightBackground,
    background = NightBackground,
    onBackground = Color(0xFFE4EBF5),
    surface = NightSurface,
    onSurface = Color(0xFFDEE5EE),
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = Color(0xFFC2CEDA),
    outline = NightOutline,
    error = ErrorRed,
    onError = Color(0xFF690005),
    errorContainer = ErrorContainerRed,
    onErrorContainer = ErrorRed
)

@Composable
fun DriveNoteTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
