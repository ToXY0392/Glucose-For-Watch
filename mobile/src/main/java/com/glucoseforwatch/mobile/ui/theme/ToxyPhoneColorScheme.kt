package com.glucoseforwatch.mobile.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Phone chrome palette "Menthe & Citron" (Material 3 light).
 * Aligned with [Theme.GlucoseForWatch.Phone] and the `gfw_*` XML colors.
 * Glucose values never use these colors: they go through GlucoseRangeResolver.
 */
internal val ToxyPhoneColorScheme =
    lightColorScheme(
        primary = GlassPalette.Accent,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE3F0E6),
        onPrimaryContainer = GlassPalette.Ink,
        secondary = Color(0xFF3F5B4A),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = GlassPalette.Lemon,
        onSecondaryContainer = GlassPalette.Ink,
        tertiary = Color(0xFF6E5E1C),
        onTertiary = Color(0xFFFFFFFF),
        background = Color(0xFFF1EFD9),
        onBackground = GlassPalette.Ink,
        surface = Color(0xFFFFFFFF),
        onSurface = GlassPalette.Ink,
        surfaceVariant = Color(0xFFEAF0E2),
        onSurfaceVariant = GlassPalette.InkSecondary,
        outline = Color(0xFFC6D0C2),
        outlineVariant = Color(0xFFDCE3D6),
        error = Color(0xFFBE504C),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9EBEA),
        onErrorContainer = Color(0xFFBE504C),
        surfaceContainer = Color(0xFFEFF3E8),
        surfaceContainerLow = Color(0xFFF6F7EF),
        surfaceContainerHigh = Color(0xFFE6EDE0),
    )
