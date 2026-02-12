package com.vishalpvijayan.theslate.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Old book inspired colors


private val DarkColorScheme = darkColorScheme(
    primary = AntiqueGold,
    secondary = SoftInk,
    tertiary = MutedGold,
    background = DarkParchment,
    surface = DarkPaper,
    onPrimary = DeepSepia,
    onBackground = SoftInk,
    onSurface = SoftInk
)

private val LightColorScheme = lightColorScheme(
    primary = MutedGold,
    secondary = InkBrown,
    tertiary = AntiqueGold,
    background = Parchment,
    surface = AgedPaper,
    onPrimary = Color.White,
    onBackground = InkBrown,
    onSurface = InkBrown
)

@Composable
fun TheSlateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to preserve book aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
