package com.widgetg7.mobile.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Phone chrome palette aligned with [Theme.WidgetG7.Phone] XML tokens.
 * AGP glucose colors stay on hero/value surfaces only (F3+).
 */
internal val ToxyPhoneColorScheme =
    lightColorScheme(
        primary = Color(0xFF6F6963),
        onPrimary = Color(0xFF403C38),
        primaryContainer = Color(0xFFF5F2ED),
        onPrimaryContainer = Color(0xFF403C38),
        secondary = Color(0xFF504C48),
        onSecondary = Color(0xFF403C38),
        secondaryContainer = Color(0xFFF0EDE8),
        onSecondaryContainer = Color(0xFF403C38),
        tertiary = Color(0xFF7A9184),
        onTertiary = Color(0xFF403C38),
        background = Color(0xFFF1EEE9),
        onBackground = Color(0xFF403C38),
        surface = Color(0xFFFDFCFA),
        onSurface = Color(0xFF403C38),
        surfaceVariant = Color(0xFFF5F2ED),
        onSurfaceVariant = Color(0xFF6F6963),
        outline = Color(0xFFE5DFD7),
        outlineVariant = Color(0xFFDED8D0),
        error = Color(0xFFBE504C),
        onError = Color(0xFFFDFCFA),
        errorContainer = Color(0xFFF9EBEA),
        onErrorContainer = Color(0xFFBE504C),
        surfaceContainer = Color(0xFFFDFCFA),
        surfaceContainerLow = Color(0xFFF1EEE9),
        surfaceContainerHigh = Color(0xFFF5F2ED),
    )
