package com.vasant.pillpal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vasant.pillpal.R

val rubikFamily = FontFamily(
    Font(R.font.rubikregular, FontWeight.Normal),
    Font(R.font.rubikbold, FontWeight.Bold),
    Font(R.font.rubiksemibold, FontWeight.SemiBold),
    Font(R.font.rubikblack, FontWeight.Black)
)
val jetbrainFamily = rubikFamily

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        fontFamily = jetbrainFamily
    ),
    titleLarge = TextStyle(
        fontFamily = jetbrainFamily,
        fontWeight = FontWeight.W800,
        fontSize = 96.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
