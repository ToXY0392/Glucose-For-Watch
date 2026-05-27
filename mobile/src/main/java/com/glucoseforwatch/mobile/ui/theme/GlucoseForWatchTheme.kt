package com.glucoseforwatch.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** ToXY phone shell — chrome from kit tokens; AGP colors are set per glucose value. */
@Composable
fun GlucoseForWatchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ToxyPhoneColorScheme,
        content = content,
    )
}
