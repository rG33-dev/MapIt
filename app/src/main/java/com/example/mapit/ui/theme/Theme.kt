package com.example.mapit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBDBDBD), // AccentGrey
    secondary = Color(0xFF2C2C2C), // MediumGrey
    tertiary = Color(0xFF1E1E1E), // LightGrey
    background = Color(0xFF000000), // Black
    surface = Color(0xFF121212), // DarkGrey
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MapItTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
