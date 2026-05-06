package com.widgetg7.wear.tile

import android.app.Activity
import android.os.Bundle
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.Wearable
import com.widgetg7.wear.complication.ComplicationUpdateNotifier
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseKeys
import com.widgetg7.wear.sync.WatchSyncHealthMonitor

class GlucoseRefreshActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)

        val cache = GlucoseCache(this)
        val healthMonitor = WatchSyncHealthMonitor(this)
        cache.markRefreshPending()
        healthMonitor.updateAndReport()
        requestSurfaceUpdates()

        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                val node = nodes.firstOrNull()
                if (node == null) {
                    cache.markRefreshFailed("Téléphone indisponible")
                    healthMonitor.updateAndReport()
                    requestSurfaceUpdates()
                    finishNoAnim()
                    return@addOnSuccessListener
                }

                Wearable.getMessageClient(this)
                    .sendMessage(node.id, GlucoseKeys.PATH_REFRESH_REQUEST, ByteArray(0))
                    .addOnSuccessListener {
                        finishNoAnim()
                    }
                    .addOnFailureListener {
                        cache.markRefreshFailed("Echec de synchro")
                        healthMonitor.updateAndReport()
                        requestSurfaceUpdates()
                        finishNoAnim()
                    }
            }
            .addOnFailureListener {
                cache.markRefreshFailed("Téléphone indisponible")
                healthMonitor.updateAndReport()
                requestSurfaceUpdates()
                finishNoAnim()
            }
    }

    private fun finishNoAnim() {
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    private fun requestSurfaceUpdates() {
        TileService.getUpdater(this).requestUpdate(GlucoseSimpleTileService::class.java)
        ComplicationUpdateNotifier.requestUpdateAll(this)
    }
}
