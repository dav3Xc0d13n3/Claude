package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class AppThemeMode {
    DARK,
    LIGHT,
    AMOLED
}

private val AmoledColorScheme = darkColorScheme(
    primary = ClaudeTerracotta,
    onPrimary = AmoledBackground,
    primaryContainer = ClaudeWarmOrange,
    onPrimaryContainer = AmoledOnBackground,
    secondary = ClaudeAccentGold,
    onSecondary = AmoledBackground,
    background = AmoledBackground,
    onBackground = AmoledOnBackground,
    surface = AmoledSurface,
    onSurface = AmoledOnSurface,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = AmoledOnSurfaceMuted,
    outline = AmoledBorder,
    surfaceContainer = AmoledSurfaceContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = ClaudeTerracotta,
    onPrimary = DarkBackground,
    primaryContainer = ClaudeWarmOrange,
    onPrimaryContainer = DarkOnBackground,
    secondary = ClaudeAccentGold,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = DarkBorder,
    surfaceContainer = DarkSurfaceContainer
)

private val LightColorScheme = lightColorScheme(
    primary = ClaudeTerracotta,
    onPrimary = LightSurface,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightOnBackground,
    secondary = ClaudeWarmOrange,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceMuted,
    outline = LightBorder,
    surfaceContainer = LightSurfaceContainer
)

@Composable
fun ClaudeAiWorkspaceTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.AMOLED -> AmoledColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.DARK -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
