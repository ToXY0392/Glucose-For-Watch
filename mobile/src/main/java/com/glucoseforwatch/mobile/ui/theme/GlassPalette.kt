package com.glucoseforwatch.mobile.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * "Menthe & Citron" glass chrome for the phone home. Background, tiles and accents only:
 * glucose numbers and range marks take their color from GlucoseRangeResolver.
 */
internal object GlassPalette {
    val Ink = Color(0xFF1D2A22)
    val InkSecondary = Color(0xFF4A5A4F)
    val InkTertiary = Color(0xFF56645A)
    val Chevron = Color(0xFF6B786E)
    val Accent = Color(0xFF2E6A48)
    val AccentPressed = Color(0xFF22804A)
    val Lemon = Color(0xFFF2E6A2)
    val OnAccentSecondary = Color(0xFFE3EFE6)

    val BackgroundStart = Color(0xFFDCEADA)
    val BackgroundMiddle = Color(0xFFF3E8B0)
    val BackgroundEnd = Color(0xFFF6E39A)
    val HaloMint = Color(0xFF9FD0B0)
    val HaloLemon = Color(0xFFF2D35E)

    val GlassFill = Color(0x80FFFFFF)
    val GlassStroke = Color(0xE0FFFFFF)
    val PressOverlay = Color(0x38FFFFFF)
    val Divider = Color(0x141D2A22)
    val Track = Color(0x1F1D2A22)

    /*
     * Glucose card: near-opaque dark glass. The AGP green #008000 needs a very dark ground
     * to reach 3:1, and the AGP yellow and orange are unreadable on light glass.
     */
    val GlucoseCardFill = Color(0xF20E1210)
    val GlucoseCardStroke = Color(0x59FFFFFF)
    val OnDarkInk = Color(0xFFF1F4EF)
    val OnDarkLabel = Color(0xFFB9C3BB)
    val OnDarkUnit = Color(0xFFD3DAD4)
}
