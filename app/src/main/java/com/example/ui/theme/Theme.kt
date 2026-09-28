package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FantasyDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = GoldContainer,
    onPrimaryContainer = GoldSecondary,
    secondary = ArcanePurple,
    onSecondary = Color.White,
    secondaryContainer = DungeonSurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = ManaBlue,
    onTertiary = Color.Black,
    tertiaryContainer = ManaBlueContainer,
    onTertiaryContainer = Color.White,
    background = DungeonDarkBg,
    onBackground = TextPrimary,
    surface = DungeonSurface,
    onSurface = TextPrimary,
    surfaceVariant = DungeonSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DungeonCardStroke
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FantasyDarkColorScheme,
        typography = Typography,
        content = content
    )
}
