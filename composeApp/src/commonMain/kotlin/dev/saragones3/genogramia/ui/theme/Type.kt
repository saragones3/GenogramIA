package dev.saragones3.genogramia.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// In the future, these should be replaced with actual Font instances for Manrope and Public Sans
val ManropeFontFamily = FontFamily.SansSerif 
val PublicSansFontFamily = FontFamily.SansSerif

val GenogramiaTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PublicSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.05.em // Archival feel
    ),
    labelMedium = TextStyle(
        fontFamily = PublicSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PublicSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp, // 0.6875rem
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
