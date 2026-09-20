package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkNoorColorScheme = darkColorScheme(
  primary = EmeraldPrimary,
  onPrimary = EmeraldOnPrimary,
  primaryContainer = EmeraldPrimaryContainer,
  onPrimaryContainer = EmeraldOnPrimaryContainer,
  secondary = GoldSecondary,
  onSecondary = GoldOnSecondary,
  secondaryContainer = GoldSecondaryContainer,
  onSecondaryContainer = GoldOnSecondaryContainer,
  tertiary = MintTertiary,
  onTertiary = MintOnTertiary,
  tertiaryContainer = MintTertiaryContainer,
  onTertiaryContainer = MintOnTertiaryContainer,
  background = DarkSurface,
  onBackground = DarkOnSurface,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceContainerHighest,
  onSurfaceVariant = DarkOnSurfaceVariant,
  surfaceContainer = DarkSurfaceContainer,
  surfaceContainerLow = DarkSurfaceContainerLow,
  surfaceContainerHigh = DarkSurfaceContainerHigh,
  surfaceContainerHighest = DarkSurfaceContainerHighest,
  surfaceContainerLowest = DarkSurfaceContainerLowest,
  surfaceBright = DarkSurfaceBright,
  surfaceDim = DarkSurfaceDim,
  outline = DarkOutline,
  outlineVariant = DarkOutlineVariant
)

val LightNoorColorScheme = lightColorScheme(
  primary = LightPrimary,
  onPrimary = LightOnPrimary,
  primaryContainer = Color(0xFFCCFBF1),
  onPrimaryContainer = Color(0xFF0F766E),
  secondary = LightSecondary,
  onSecondary = LightOnSecondary,
  secondaryContainer = Color(0xFFFEF3C7),
  onSecondaryContainer = Color(0xFF92400E),
  background = LightSurface,
  onBackground = LightOnSurface,
  surface = LightSurface,
  onSurface = LightOnSurface,
  surfaceVariant = LightSurfaceContainerHigh,
  onSurfaceVariant = LightOnSurfaceVariant,
  surfaceContainer = LightSurfaceContainer,
  surfaceContainerHigh = LightSurfaceContainerHigh,
  outline = Color(0xFF94A3B8),
  outlineVariant = Color(0xFFCBD5E1)
)

@Composable
fun NoorAlFurqanTheme(
  darkTheme: Boolean = true, // Default to spiritual midnight nocturnal sanctuary theme
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkNoorColorScheme else LightNoorColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Backwards compatibility alias for default template
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  NoorAlFurqanTheme(darkTheme = darkTheme, content = content)
}
