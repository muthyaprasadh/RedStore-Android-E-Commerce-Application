package com.redstore.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

val Red = Color(0xFFFF523B)
val Ink = Color(0xFF222222)
val Muted = Color(0xFF6B625E)
val Paper = Color(0xFFF7F4F2)
val CardWhite = Color(0xFFFFFFFF)
val Line = Color(0xFFE7E0DC)

private val scheme = lightColorScheme(
    primary = Red,
    onPrimary = Color.White,
    secondary = Ink,
    background = Paper,
    surface = CardWhite,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun RedStoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = scheme,
        content = content
    )
}

val DisplayFamily = FontFamily.SansSerif
