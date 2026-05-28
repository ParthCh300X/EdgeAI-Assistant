package parth.appdev.edgeaiassistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary          = Primary,
    background       = BackgroundDark,
    surface          = SurfaceDark,
    onPrimary        = OnPrimary,
    onBackground     = OnBackgroundDark,
    onSurface        = OnSurfaceDark,
    surfaceVariant   = SurfaceDark,
    onSurfaceVariant = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary          = PrimaryLight,
    background       = BackgroundLight,
    surface          = SurfaceLight,
    onPrimary        = OnPrimary,
    onBackground     = OnBackgroundLight,
    onSurface        = OnSurfaceLight,
    surfaceVariant   = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceLight
)

@Composable
fun EdgeAITheme(
    darkTheme : Boolean = isSystemInDarkTheme(),
    content   : @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography(),
        content     = content
    )
}