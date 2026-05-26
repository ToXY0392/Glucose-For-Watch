package com.widgetg7.mobile.ui.compose

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberRawText(@RawRes rawResId: Int): String {
    val context = LocalContext.current
    return remember(rawResId) {
        context.resources
            .openRawResource(rawResId)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
    }
}
