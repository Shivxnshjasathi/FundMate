package com.zincstate.fundmate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

// Dark theme colors (tweaked to still feel like your brand)
private val DarkColorScheme = darkColorScheme(
    primary = GrowwGreen,
    onPrimary = BackgroundWhite,

    secondary = TextSecondary,
    onSecondary = BackgroundWhite,

    background = Color(0xFF0F1115),
    onBackground = BackgroundWhite,

    surface = Color(0xFF181B20),
    onSurface = BackgroundWhite
)

// Light theme colors (Groww-style)
private val LightColorScheme = lightColorScheme(
    primary = GrowwGreen,
    onPrimary = BackgroundWhite,

    secondary = TextSecondary,
    onSecondary = BackgroundWhite,

    background = SurfaceGrey,
    onBackground = TextPrimary,

    surface = BackgroundWhite,
    onSurface = TextPrimary,

    // Optional extras (can tweak later)
    // error = Color(0xFFB00020),
    // onError = BackgroundWhite,
)

@Composable
fun FundmateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
