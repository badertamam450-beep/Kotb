package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class BookThemeMode(val title: String) {
    MIDNIGHT_GOLD("كحلي ملكي وذهب"),
    ROYAL_EMERALD("زمردي إسلامي"),
    WARM_PARCHMENT("ورق عتيق"),
    PURE_DARK("أسود فاخر")
}

private val MidnightGoldScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1E1700),
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = AmberAccent,
    onSecondary = Color.Black,
    background = MidnightNavyDark,
    onBackground = Color(0xFFF5F5F7),
    surface = MidnightNavyCard,
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = MidnightNavySurface,
    onSurfaceVariant = TextGoldGlow,
    outline = DividerGold
)

private val RoyalEmeraldScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = EmeraldSurface,
    onPrimaryContainer = GoldLight,
    secondary = EmeraldAccent,
    onSecondary = Color.Black,
    background = EmeraldDark,
    onBackground = Color(0xFFF0FDFC),
    surface = EmeraldSurface,
    onSurface = Color(0xFFE6FAF5),
    surfaceVariant = Color(0xFF1B4D3E),
    onSurfaceVariant = TextGoldGlow,
    outline = DividerGold
)

private val WarmParchmentScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = ParchmentBorder,
    onPrimaryContainer = ParchmentTextDark,
    secondary = Color(0xFF8D6E63),
    onSecondary = Color.White,
    background = ParchmentLight,
    onBackground = ParchmentTextDark,
    surface = ParchmentSurface,
    onSurface = ParchmentTextDark,
    surfaceVariant = ParchmentBorder,
    onSurfaceVariant = Color(0xFF5D4037),
    outline = Color(0xFFBCAAA4)
)

private val PureDarkScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF222222),
    onPrimaryContainer = GoldLight,
    secondary = AmberAccent,
    onSecondary = Color.Black,
    background = Color(0xFF0D0D0D),
    onBackground = Color(0xFFF0F0F0),
    surface = Color(0xFF181818),
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = Color(0xFF252525),
    onSurfaceVariant = TextGoldGlow,
    outline = Color(0xFF444444)
)

@Composable
fun AlMfazahTheme(
    themeMode: BookThemeMode = BookThemeMode.MIDNIGHT_GOLD,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        BookThemeMode.MIDNIGHT_GOLD -> MidnightGoldScheme
        BookThemeMode.ROYAL_EMERALD -> RoyalEmeraldScheme
        BookThemeMode.WARM_PARCHMENT -> WarmParchmentScheme
        BookThemeMode.PURE_DARK -> PureDarkScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BookTypography,
        content = content
    )
}
