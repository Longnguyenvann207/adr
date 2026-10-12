package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = CalcOpKeyDark,
    onPrimary = CalcOpKeyTextDark,
    primaryContainer = CalcFunctionKeyDark,
    onPrimaryContainer = CalcFunctionKeyTextDark,
    secondary = CalcFunctionKeyTextDark,
    onSecondary = Color.White,
    background = CalcBgDark,
    onBackground = CalcTextPrimaryDark,
    surface = CalcSurfaceDark,
    onSurface = CalcTextPrimaryDark,
    surfaceVariant = CalcSurfaceVariantDark,
    onSurfaceVariant = CalcTextSecondaryDark,
    error = CalcActionKeyTextDark,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CalcOpKeyLight,
    onPrimary = CalcOpKeyTextLight,
    primaryContainer = CalcFunctionKeyLight,
    onPrimaryContainer = CalcFunctionKeyTextLight,
    secondary = CalcFunctionKeyTextLight,
    onSecondary = Color.White,
    background = CalcBgLight,
    onBackground = CalcTextPrimaryLight,
    surface = CalcSurfaceLight,
    onSurface = CalcTextPrimaryLight,
    surfaceVariant = CalcSurfaceVariantLight,
    onSurfaceVariant = CalcTextSecondaryLight,
    error = CalcActionKeyTextLight,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our sleek custom calculator theme
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
