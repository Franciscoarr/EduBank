package com.example.edubank.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GamifiedColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    secondary = SecondaryYellow,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    error = DangerRed
)

@Composable
fun EduBankTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GamifiedColorScheme,
        typography = AppTypography,
        content = content
    )
}