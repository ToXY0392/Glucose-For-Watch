package com.glucoseforwatch.wear

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Keep
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import android.util.Log
import com.glucoseforwatch.wear.complication.ComplicationInstanceRegistry
import com.glucoseforwatch.wear.tile.GlucoseRefreshActivity
import com.glucoseforwatch.wear.ui.WearStatusScreen
import com.glucoseforwatch.wear.ui.theme.GlucoseForWatchWearTheme

/** Launcher: glucose status (Compose M3) aligned with tile + complication. */
@Keep
class WearMainActivity : ComponentActivity() {
    private var refreshKey by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlucoseForWatchWearTheme {
                WearStatusScreen(
                    refreshKey = refreshKey,
                    onSyncClick = { startActivity(Intent(this, GlucoseRefreshActivity::class.java)) },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshKey++
        val instanceIds = ComplicationInstanceRegistry.activeInstanceIds(this)
        Log.i(
            TAG,
            "complication_linked=${instanceIds.isNotEmpty()} instances=${instanceIds.toList()}",
        )
    }

    private companion object {
        private const val TAG = "WG7.Complication"
    }
}
