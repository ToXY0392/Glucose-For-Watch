package com.widgetg7.wear.tile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.widgetg7.wear.data.GlucoseCache

/** Zero-UI trampoline: acquire sync lock, mark pending, dispatch request, exit immediately. */
class GlucoseRefreshActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)

        if (!GlucoseSyncCoordinator.tryBeginSync()) {
            Log.d(TAG, "sync_tap_ignored locked")
            finishNoAnim()
            return
        }

        val cache = GlucoseCache(this)
        cache.markRefreshPending()
        GlucoseTileUpdateRequester.requestUpdateImmediate(this)
        GlucoseSyncRequestExecutor.dispatch(this)
        finishNoAnim()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        finishNoAnim()
    }

    private fun finishNoAnim() {
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    companion object {
        private const val TAG = "WG7.WearDataLayer"
    }
}
