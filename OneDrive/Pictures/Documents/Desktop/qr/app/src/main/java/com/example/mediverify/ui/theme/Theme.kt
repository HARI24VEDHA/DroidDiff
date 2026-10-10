package com.example.mediverify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    secondary = MintLight,
    tertiary = MintSoft,
    background = Slate50,
    surface = White,
    onPrimary = White,
    onSecondary = TitleText,
    onBackground = TitleText,
    onSurface = TitleText,
)

private val DarkColors = darkColorScheme(
    primary = TealPrimary,
    secondary = MintLight,
    tertiary = MintSoft,
    background = TitleText,
    surface = TealDark,
    onPrimary = White,
    onSecondary = TitleText,
    onBackground = White,
    onSurface = White,
)

@Composable
fun MediVerifyTheme(content: @Composable () -> Unit) {
    val colors = LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content,
    )
}
