package com.widgetg7.wear.tile

import android.app.Activity
import android.content.ComponentName
import android.os.Bundle
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.google.android.gms.wearable.Wearable
import com.widgetg7.wear.complication.GlucoseComplicationService
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseKeys
import com.widgetg7.wear.sync.WatchSyncHealthMonitor

class GlucoseRefreshActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    finish()
                    return@addOnSuccessListener
                }

                Wearable.getMessageClient(this)
                    .sendMessage(node.id, GlucoseKeys.PATH_REFRESH_REQUEST, ByteArray(0))
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {
                        cache.markRefreshFailed("Echec de synchro")
                        healthMonitor.updateAndReport()
                        requestSurfaceUpdates()
                        finish()
                    }
            }
            .addOnFailureListener {
                cache.markRefreshFailed("Téléphone indisponible")
                healthMonitor.updateAndReport()
                requestSurfaceUpdates()
                finish()
            }
    }

    private fun requestSurfaceUpdates() {
        TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)
        ComplicationDataSourceUpdateRequester
            .create(this, ComponentName(this, GlucoseComplicationService::class.java))
            .requestUpdateAll()
    }
}
