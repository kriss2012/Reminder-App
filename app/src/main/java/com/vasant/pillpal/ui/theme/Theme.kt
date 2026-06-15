package com.vasant.pillpal.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Whisker Peach Theme (Default)
private val WhiskerColorScheme = lightColorScheme(
    primary = SecondaryContainerColor,
    secondary = SecondaryContainerColor,
    background = BackgroundColor,
    surface = SurfaceWhite,
    onBackground = fontColor,
    onSurface = fontColor,
)

// Minty Fresh Theme
private val MintyColorScheme = lightColorScheme(
    primary = Color(0xFF4DB6AC),
    secondary = Color(0xFF80CBC4),
    background = Color(0xFFF1F8F6),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1D3C37),
    onSurface = Color(0xFF1D3C37),
)

// Lavender Fields Theme
private val LavenderColorScheme = lightColorScheme(
    primary = Color(0xFF9E8BF2),
    secondary = Color(0xFFC3B8F9),
    background = Color(0xFFF9F7FF),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF2E2452),
    onSurface = Color(0xFF2E2452),
)

// Charcoal Dark Theme
private val CharcoalColorScheme = darkColorScheme(
    primary = Color(0xFFF28B6A),
    secondary = Color(0xFFF28B6A),
    background = Color(0xFF181512),
    surface = Color(0xFF23201D),
    onBackground = Color(0xFFF7F5F2),
    onSurface = Color(0xFFF7F5F2),
)

@Composable
fun PillPalTheme(
    themeName: String = "whisker",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeName.lowercase()) {
        "minty" -> MintyColorScheme
        "lavender" -> LavenderColorScheme
        "dark" -> CharcoalColorScheme
        else -> WhiskerColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}