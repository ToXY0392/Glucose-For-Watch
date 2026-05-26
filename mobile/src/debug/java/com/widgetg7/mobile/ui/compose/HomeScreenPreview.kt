package com.widgetg7.mobile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.widgetg7.mobile.ui.theme.WidgetG7Theme

@Preview(name = "HomeScreen — connected", showBackground = true, heightDp = 900)
@ShowkaseComposable
@Composable
internal fun HomeScreenConnectedPreview() {
    val context = LocalContext.current
    WidgetG7Theme {
        HomeScreen(
            state = HomePreviewStates.connected(context),
            syncEnabled = true,
            onSyncClick = {},
            onDexcomClick = {},
            onWatchClick = {},
            onBatteryClick = {},
            onInstallClick = {},
            onNoticeClick = {},
            onPermissionsClick = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        )
    }
}

@Preview(name = "HomeScreen — sync error", showBackground = true, heightDp = 900)
@ShowkaseComposable
@Composable
internal fun HomeScreenSyncErrorPreview() {
    val context = LocalContext.current
    WidgetG7Theme {
        HomeScreen(
            state = HomePreviewStates.syncError(context),
            syncEnabled = true,
            onSyncClick = {},
            onDexcomClick = {},
            onWatchClick = {},
            onBatteryClick = {},
            onInstallClick = {},
            onNoticeClick = {},
            onPermissionsClick = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        )
    }
}
