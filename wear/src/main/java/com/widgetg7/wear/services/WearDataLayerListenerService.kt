package com.widgetg7.wear.services

import android.content.ComponentName
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.widgetg7.wear.complication.GlucoseComplicationService
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseKeys
import com.widgetg7.wear.data.GlucoseSnapshot
import com.widgetg7.wear.sync.WatchSyncHealthMonitor
import com.widgetg7.wear.tile.GlucoseTileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WearDataLayerListenerService : WearableListenerService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        val cache = GlucoseCache(this)
        val healthMonitor = WatchSyncHealthMonitor(this)

        for (event in dataEvents) {
            if (event.type != DataEvent.TYPE_CHANGED) continue
            val item = event.dataItem

            if (item.uri.path == GlucoseKeys.PATH_LATEST) {
                val map = DataMapItem.fromDataItem(item).dataMap
                if (!isForThisWatch(map.getString(GlucoseKeys.TARGET_NODE_ID).orEmpty())) continue
                val snapshot = GlucoseSnapshot(
                    valueMgDl = map.getInt(GlucoseKeys.VALUE_MG_DL),
                    trend = map.getString(GlucoseKeys.TREND).orEmpty(),
                    deltaMgDl = map.getInt(GlucoseKeys.DELTA_MG_DL),
                    timestampEpochMs = map.getLong(GlucoseKeys.TIMESTAMP_EPOCH_MS),
                    stale = map.getBoolean(GlucoseKeys.STALE),
                )
                val sequenceId = map.getLong(GlucoseKeys.SEQUENCE_ID)
                cache.save(snapshot)
                cache.clearRefreshStatus()
                sendAck(snapshot.timestampEpochMs, sequenceId)
                requestSurfaceUpdates()
                healthMonitor.updateAndReport()
                continue
            }

            if (item.uri.path == GlucoseKeys.PATH_WATCH_STATUS_REQUEST) {
                val map = DataMapItem.fromDataItem(item).dataMap
                if (!isForThisWatch(map.getString(GlucoseKeys.TARGET_NODE_ID).orEmpty())) continue
                healthMonitor.updateAndReport()
                continue
            }

            if (item.uri.path == GlucoseKeys.PATH_REFRESH_STATUS) {
                val map = DataMapItem.fromDataItem(item).dataMap
                if (!isForThisWatch(map.getString(GlucoseKeys.TARGET_NODE_ID).orEmpty())) continue
                val status = map.getString(GlucoseKeys.REFRESH_STATUS).orEmpty()
                val message = map.getString(GlucoseKeys.REFRESH_MESSAGE).orEmpty()
                when (status) {
                    GlucoseKeys.REFRESH_IN_PROGRESS -> cache.markRefreshPending(message)
                    GlucoseKeys.REFRESH_COMPLETED ->
                        if (message.isBlank()) {
                            cache.clearRefreshStatus()
                        } else {
                            cache.markRefreshCompleted(message)
                        }
                    GlucoseKeys.REFRESH_FAILED -> cache.markRefreshFailed(message)
                }
                requestSurfaceUpdates()
                healthMonitor.updateAndReport()
            }
        }

        super.onDataChanged(dataEvents)
    }

    private fun requestSurfaceUpdates() {
        TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)

        try {
            ComplicationDataSourceUpdateRequester
                .create(this, ComponentName(this, GlucoseComplicationService::class.java))
                .requestUpdateAll()
        } catch (_: Throwable) {
            // Some watch faces reject complication update requests.
        }
    }

    private fun sendAck(readingTimestampEpochMs: Long, sequenceId: Long) {
        serviceScope.launch {
            runCatching {
                val now = System.currentTimeMillis()
                val request = PutDataMapRequest.create(GlucoseKeys.PATH_WATCH_ACK).apply {
                    dataMap.putLong(GlucoseKeys.ACK_READING_TIMESTAMP_EPOCH_MS, readingTimestampEpochMs)
                    dataMap.putLong(GlucoseKeys.ACK_SEQUENCE_ID, sequenceId)
                    dataMap.putLong(GlucoseKeys.ACK_RECEIVED_AT, now)
                    dataMap.putLong(GlucoseKeys.SEQUENCE_ID, now)
                }.asPutDataRequest().setUrgent()
                Wearable.getDataClient(this@WearDataLayerListenerService).putDataItem(request)
            }
        }
    }

    private fun isForThisWatch(targetNodeId: String): Boolean {
        if (targetNodeId.isBlank()) return true
        val localNodeId = runCatching {
            Tasks.await(Wearable.getNodeClient(this).localNode).id
        }.getOrDefault("")
        return localNodeId.isBlank() || localNodeId == targetNodeId
    }
}
