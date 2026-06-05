package com.drivenote.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorWhite = Color(0xFFFFFFFF)
private val ColorInk   = Color(0xFF080D12)

private val LightScheme = lightColorScheme(
    primary             = CalmMintContainer,
    onPrimary           = ColorWhite,
    primaryContainer    = CalmMint,
    onPrimaryContainer  = ColorInk,
    secondary           = SlateBlue,
    onSecondary         = ColorInk,
    tertiary            = WarmSand,
    onTertiary          = ColorInk,
    background          = Color(0xFFF2F6FA),
    onBackground        = Color(0xFF18222E),
    surface             = ColorWhite,
    onSurface           = Color(0xFF18222E),
    surfaceVariant      = Color(0xFFE2EAF4),
    onSurfaceVariant    = Color(0xFF3A4E60),
    outline             = Color(0xFF627888),
    error               = Color(0xFFBA1A1A),
    onError             = ColorWhite,
    errorContainer      = Color(0xFFFFDAD6),
    onErrorContainer    = Color(0xFF410002)
)

private val DarkScheme = darkColorScheme(
    primary                  = CalmMint,
    onPrimary                = NightBackground,
    primaryContainer         = CalmMintContainer,
    onPrimaryContainer       = Color(0xFFC2F0E6),
    secondary                = SlateBlue,
    onSecondary              = NightBackground,
    secondaryContainer       = Color(0xFF1C2E42),
    onSecondaryContainer     = Color(0xFFCCDEF0),
    tertiary                 = WarmSand,
    onTertiary               = NightBackground,
    tertiaryContainer        = Color(0xFF2A2010),
    onTertiaryContainer      = Color(0xFFEDD9B8),
    background               = NightBackground,
    onBackground             = Color(0xFFD8E6F2),
    surface                  = NightSurface,
    onSurface                = Color(0xFFD4E2EE),
    surfaceVariant           = NightSurfaceVariant,
    onSurfaceVariant         = Color(0xFFA0B4C8),
    surfaceContainerLowest   = NightBackground,
    surfaceContainerLow      = NightSurfaceLow,
    surfaceContainer         = NightSurfaceMid,
    surfaceContainerHigh     = NightSurfaceHigh,
    surfaceContainerHighest  = NightSurfaceTop,
    outline                  = NightOutline,
    outlineVariant           = NightOutlineVar,
    error                    = ErrorRed,
    onError                  = Color(0xFF690005),
    errorContainer           = ErrorContainerRed,
    onErrorContainer         = ErrorRed
)

@Composable
fun DriveNoteTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography  = AppTypography,
        shapes      = AppShapes,
        content     = content
    )
}
