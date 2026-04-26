package dev.saragones3.genogramia.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    tertiary = Tertiary,
    surface = Surface,
    onSurface = OnSurface,
    // Add these if supported by current material3 version, otherwise they are available as direct variables
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerHighest = SurfaceContainerHighest
)

@Composable
fun GenogramiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For now we only implement light theme as per design system
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GenogramiaTypography,
        shapes = GenogramiaShapes,
        content = content
    )
}
