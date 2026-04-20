package com.widgetg7.wear.tile

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.Wearable
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseKeys
import com.widgetg7.wear.sync.WatchSyncHealthMonitor

class GlucoseRefreshActivity : Activity() {
    private val logTag = "WidgetG7Wear"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cache = GlucoseCache(this)
        val healthMonitor = WatchSyncHealthMonitor(this)
        cache.markRefreshPending()
        healthMonitor.updateAndReport()
        TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)

        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                val node = nodes.firstOrNull()
                if (node == null) {
                    cache.markRefreshFailed("Telephone indisponible")
                    healthMonitor.updateAndReport()
                    TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)
                    finish()
                    return@addOnSuccessListener
                }

                Wearable.getMessageClient(this)
                    .sendMessage(node.id, GlucoseKeys.PATH_REFRESH_REQUEST, ByteArray(0))
                    .addOnSuccessListener {
                        Log.d(logTag, "Refresh request sent to node=${node.displayName}/${node.id}")
                        finish()
                    }
                    .addOnFailureListener { error ->
                        Log.e(logTag, "Refresh request failed", error)
                        cache.markRefreshFailed("Echec de synchro")
                        healthMonitor.updateAndReport()
                        TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)
                        finish()
                    }
            }
            .addOnFailureListener { error ->
                Log.e(logTag, "Unable to resolve phone node", error)
                cache.markRefreshFailed("Telephone indisponible")
                healthMonitor.updateAndReport()
                TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)
                finish()
            }
    }
}
