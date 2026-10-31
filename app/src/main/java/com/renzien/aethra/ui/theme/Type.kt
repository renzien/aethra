package com.renzien.aethra.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.renzien.aethra.R

val Quicksand = FontFamily(
    Font(
        resId = R.font.quicksand_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.quicksand_medium,
        weight = FontWeight.Medium
    ),
    Font(
        resId = R.font.quicksand_bold,
        weight = FontWeight.Bold
    )
)

private val defaultTypography = Typography()

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(
        fontFamily = Quicksand
    ),
    displayMedium = defaultTypography.displayMedium.copy(
        fontFamily = Quicksand
    ),
    displaySmall = defaultTypography.displaySmall.copy(
        fontFamily = Quicksand
    ),
    headlineLarge = defaultTypography.headlineLarge.copy(
        fontFamily = Quicksand
    ),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = Quicksand
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = Quicksand
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = Quicksand
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = Quicksand
    ),
    titleSmall = defaultTypography.titleSmall.copy(
        fontFamily = Quicksand
    ),
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = Quicksand
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = Quicksand
    ),
    bodySmall = defaultTypography.bodySmall.copy(
        fontFamily = Quicksand
    ),
    labelLarge = defaultTypography.labelLarge.copy(
        fontFamily = Quicksand
    ),
    labelMedium = defaultTypography.labelMedium.copy(
        fontFamily = Quicksand
    ),
    labelSmall = defaultTypography.labelSmall.copy(
        fontFamily = Quicksand
    )
)