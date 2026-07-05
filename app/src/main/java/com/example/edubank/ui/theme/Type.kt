package com.example.edubank.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.edubank.R

val Fredoka = FontFamily(
    Font(R.font.fredoka_regular)
)

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Fredoka,
        fontSize = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Fredoka,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Fredoka,
        fontSize = 16.sp
    )
)