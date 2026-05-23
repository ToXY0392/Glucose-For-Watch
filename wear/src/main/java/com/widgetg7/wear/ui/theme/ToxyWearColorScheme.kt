package com.widgetg7.wear.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme

/**
 * ToXY chrome colors for Wear Compose M3.
 *
 * Source of truth: [toxy-ux-kit/tokens/toxy.color.json]. Glucose value colors stay AGP-only.
 */
internal val ToxyWearColorScheme =
    ColorScheme(
        primary = Color(0xFF34D399),
        onPrimary = Color(0xFF0F172A),
        primaryContainer = Color(0xFF163D34),
        onPrimaryContainer = Color(0xFFF8FAFC),
        tertiary = Color(0xFFFB923C),
        onTertiary = Color(0xFF0F172A),
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFF8FAFC),
        surfaceContainerLow = Color(0xFF111827),
        surfaceContainer = Color(0xFF111827),
        surfaceContainerHigh = Color(0xFF1E293B),
        onSurface = Color(0xFFF8FAFC),
        onSurfaceVariant = Color(0xFF94A3B8),
        outline = Color(0xFF334155),
        outlineVariant = Color(0xFF334155),
        error = Color(0xFFF87171),
        onError = Color(0xFFF8FAFC),
        errorContainer = Color(0xFF1E293B),
        onErrorContainer = Color(0xFFF87171),
    )
