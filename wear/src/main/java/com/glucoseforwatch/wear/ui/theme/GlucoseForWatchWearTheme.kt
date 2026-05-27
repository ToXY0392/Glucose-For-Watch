package com.glucoseforwatch.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme

/** ToXY Wear shell — chrome from kit tokens; AGP colors are set per glucose value. */
@Composable
fun GlucoseForWatchWearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ToxyWearColorScheme,
        content = content,
    )
}
