package com.widgetg7.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme

/** ToXY Wear shell — uses Wear Material 3 defaults; AGP colors are set per glucose value. */
@Composable
fun WidgetG7WearTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
