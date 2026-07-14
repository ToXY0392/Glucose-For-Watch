package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.glucoseforwatch.mobile.ui.theme.GlucoseForWatchTheme

@Preview(name = "HomeScreen — connected", showBackground = true, heightDp = 900)
@ShowkaseComposable
@Composable
internal fun HomeScreenConnectedPreview() {
    val context = LocalContext.current
    GlucoseForWatchTheme {
        HomeScreen(
            state = HomePreviewStates.connected(context),
            syncEnabled = true,
            onSyncClick = {},
            onDexcomClick = {},
            onWatchClick = {},
            onUnitClick = {},
            onBatteryClick = {},
            onPermissionsClick = {},
            onAboutClick = {},
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
    GlucoseForWatchTheme {
        HomeScreen(
            state = HomePreviewStates.syncError(context),
            syncEnabled = true,
            onSyncClick = {},
            onDexcomClick = {},
            onWatchClick = {},
            onUnitClick = {},
            onBatteryClick = {},
            onPermissionsClick = {},
            onAboutClick = {},
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        )
    }
}
