package com.example.iotbazar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 🌞 Light Theme Colors with Gradient Feel
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00796B), // Deep Teal
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA7FFEB), // Soft Cyan
    onPrimaryContainer = Color(0xFF00332C),

    secondary = Color(0xFF00ACC1), // Vibrant Cyan
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBF2), // Light Cyan
    onSecondaryContainer = Color(0xFF002F36),

    background = Color(0xFFE3F2FD), // Pastel Blue
    onBackground = Color(0xFF102027),

    surface = Color.White,
    onSurface = Color(0xFF102027),
)

// 🌙 Dark Theme Colors with a Futuristic Glow
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF), // Neon Cyan
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D40), // Dark Emerald
    onPrimaryContainer = Color(0xFFA7FFEB),

    secondary = Color(0xFF26C6DA), // Cool Blue
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003840),
    onSecondaryContainer = Color(0xFFB2EBF2),

    background = Color(0xFF121212), // Deep Charcoal
    onBackground = Color(0xFFE0F2F1),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0F2F1),
)

// 🎨 **Apply Custom Theme with Gradient Support**
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(), // Default M3 Typography
        content = content
    )
}
