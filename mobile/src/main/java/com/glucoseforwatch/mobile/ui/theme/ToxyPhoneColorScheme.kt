package com.glucoseforwatch.mobile.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Phone chrome palette — vert/blanc ToXY (Material 3 light, contraste élevé).
 * Aligné sur [Theme.GlucoseForWatch.Phone] et `wg7_*` / `gfw_*` XML.
 * Les couleurs AGP glycémie restent sur hero/value via [HomeStateMapper] uniquement.
 */
internal val ToxyPhoneColorScheme =
    lightColorScheme(
        primary = Color(0xFF0B6640),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFEAF2EC),
        onPrimaryContainer = Color(0xFF084D31),
        secondary = Color(0xFF084D31),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFE2EFE5),
        onSecondaryContainer = Color(0xFF1A2E24),
        tertiary = Color(0xFF4A8F6A),
        onTertiary = Color(0xFFFFFFFF),
        background = Color(0xFFF7F9F7),
        onBackground = Color(0xFF1A2E24),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1A2E24),
        surfaceVariant = Color(0xFFEAF2EC),
        onSurfaceVariant = Color(0xFF3D5348),
        outline = Color(0xFFC5D4CA),
        outlineVariant = Color(0xFFD8E5DC),
        error = Color(0xFFBE504C),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFF9EBEA),
        onErrorContainer = Color(0xFFBE504C),
        surfaceContainer = Color(0xFFEAF2EC),
        surfaceContainerLow = Color(0xFFF7F9F7),
        surfaceContainerHigh = Color(0xFFE2EFE5),
    )
