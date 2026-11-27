package com.glucoseforwatch.mobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.glucoseforwatch.mobile.R

private fun jakarta(weight: FontWeight) =
    Font(
        resId = R.font.plus_jakarta_sans,
        weight = weight,
        variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
    )

/** Plus Jakarta Sans (variable font, SIL OFL 1.1 — docs/legal/fonts). */
internal val PlusJakartaSans =
    FontFamily(
        jakarta(FontWeight.Normal),
        jakarta(FontWeight.Medium),
        jakarta(FontWeight.SemiBold),
        jakarta(FontWeight.Bold),
        jakarta(FontWeight.ExtraBold),
    )

private val base = Typography()

/** Material 3 type scale on Plus Jakarta Sans. */
internal val GfwTypography =
    Typography(
        displayLarge = base.displayLarge.copy(fontFamily = PlusJakartaSans),
        displayMedium = base.displayMedium.copy(fontFamily = PlusJakartaSans),
        displaySmall = base.displaySmall.copy(fontFamily = PlusJakartaSans),
        headlineLarge = base.headlineLarge.copy(fontFamily = PlusJakartaSans),
        headlineMedium = base.headlineMedium.copy(fontFamily = PlusJakartaSans),
        headlineSmall = base.headlineSmall.copy(fontFamily = PlusJakartaSans),
        titleLarge = base.titleLarge.copy(fontFamily = PlusJakartaSans),
        titleMedium = base.titleMedium.copy(fontFamily = PlusJakartaSans),
        titleSmall = base.titleSmall.copy(fontFamily = PlusJakartaSans),
        bodyLarge = base.bodyLarge.copy(fontFamily = PlusJakartaSans),
        bodyMedium = base.bodyMedium.copy(fontFamily = PlusJakartaSans),
        bodySmall = base.bodySmall.copy(fontFamily = PlusJakartaSans),
        labelLarge = base.labelLarge.copy(fontFamily = PlusJakartaSans),
        labelMedium = base.labelMedium.copy(fontFamily = PlusJakartaSans),
        labelSmall = base.labelSmall.copy(fontFamily = PlusJakartaSans),
    )
