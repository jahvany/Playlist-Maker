package com.practicum.playlistmaker.util.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Grey = Color(0xFFAEAFB4)
val LightGrey = Color(0xFFE6E8EB)
val DarkGrey = Color(0xFF1A1B22)
val Blue = Color(0xFF3772E7)

private val LightColorScheme = lightColorScheme(
    primary = DarkGrey,
    primaryContainer = Grey,
    onPrimary = Color.White,
    secondary = Blue,
    secondaryContainer = LightGrey,
    onSecondary = Grey
)

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    primaryContainer = Color.Black,
    onPrimary = DarkGrey,
    secondary = DarkGrey,
    secondaryContainer = Color.White,
    onSecondary = Color.White
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}