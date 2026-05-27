package com.glucoseforwatch.mobile.sync

import android.content.Context
import android.util.Log
import com.glucoseforwatch.mobile.watch.WatchConnectionRepository

/**
 * Detects watch reconnect by comparing connection state between sync passes.
 */
internal class WatchReconnectDetector(
    private val isWatchConnected: suspend () -> Boolean,
) {
    constructor(context: Context) : this({
        WatchConnectionRepository(context.applicationContext).loadStatus().connected
    })

    private var watchWasConnected: Boolean? = null

    suspend fun onBeforeSyncPass(onReconnected: suspend () -> Unit) {
        val connected = isWatchConnected()
        val reconnected = watchWasConnected == false && connected
        watchWasConnected = connected
        if (reconnected) {
            Log.i(TAG, "watch_reconnected")
            onReconnected()
        }
    }

    private companion object {
        private const val TAG = "WG7.WatchReconnect"
    }
}
