package com.glucoseforwatch.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/** Phone shell: Menthe & Citron chrome, Plus Jakarta Sans; AGP colors are set per glucose value. */
@Composable
fun GlucoseForWatchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ToxyPhoneColorScheme,
        typography = GfwTypography,
        content = content,
    )
}
