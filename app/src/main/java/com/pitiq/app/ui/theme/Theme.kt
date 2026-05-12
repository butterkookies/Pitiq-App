package com.pitiq.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PitiqColorScheme = darkColorScheme(
    primary = Color(0xFFE8C97E),
    onPrimary = Color(0xFF1A1208),
    secondary = Color(0xFFD4A853),
    background = Color(0xFF0D0D0D),
    surface = Color(0xFF1C1C1C),
    onBackground = Color(0xFFF5F0E8),
    onSurface = Color(0xFFF5F0E8),
)

@Composable
fun PitiqTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PitiqColorScheme,
        content = content,
    )
}
