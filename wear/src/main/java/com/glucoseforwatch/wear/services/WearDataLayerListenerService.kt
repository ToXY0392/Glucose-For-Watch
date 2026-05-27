package com.glucoseforwatch.wear.services

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.glucoseforwatch.wear.complication.ComplicationUpdateNotifier
import com.glucoseforwatch.wear.data.GlucoseCache
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.wear.data.GlucoseKeys
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import com.glucoseforwatch.wear.sync.WatchSyncHealthMonitor
import com.glucoseforwatch.wear.sync.WearAckSender
import com.glucoseforwatch.wear.tile.GlucoseSyncCoordinator
import com.glucoseforwatch.wear.tile.GlucoseTileUpdateRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Receives glucose and refresh events from the phone via the Wearable Data Layer.
 *
 * Persists readings, sends ACKs, updates tile/complication, and reports watch health.
 */
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
                    displayUnit = GlucoseDisplayUnit.fromStorage(map.getString(GlucoseKeys.DISPLAY_UNIT)),
                )
                val sequenceId = map.getLong(GlucoseKeys.SEQUENCE_ID)
                val sourcePhoneNodeId = map.getString(GlucoseKeys.SOURCE_PHONE_NODE_ID).orEmpty()
                    .ifBlank { item.uri.host.orEmpty() }
                if (sourcePhoneNodeId.isNotBlank()) {
                    cache.recordLastPhoneNodeId(sourcePhoneNodeId)
                }
                cache.save(snapshot)
                cache.clearRefreshStatus()
                GlucoseSyncCoordinator.endSync()
                sendAck(cache, healthMonitor, snapshot.timestampEpochMs, sequenceId)
                requestTileUpdateImmediate()
                notifyComplicationReading(snapshot, sequenceId)
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
                    GlucoseKeys.REFRESH_IN_PROGRESS -> {
                        cache.markRefreshPending(message)
                        healthMonitor.updateAndReport()
                    }
                    GlucoseKeys.REFRESH_COMPLETED ->
                        if (message.isBlank()) {
                            cache.clearRefreshStatus()
                        } else {
                            cache.markRefreshCompleted(message)
                        }
                    GlucoseKeys.REFRESH_FAILED -> cache.markRefreshFailed(message)
                }
                if (status != GlucoseKeys.REFRESH_IN_PROGRESS) {
                    GlucoseSyncCoordinator.endSync()
                    requestTileUpdateImmediate()
                    healthMonitor.updateAndReport()
                }
            }
        }

        super.onDataChanged(dataEvents)
    }

    private fun requestTileUpdate() {
        GlucoseTileUpdateRequester.requestUpdate(this)
    }

    private fun requestTileUpdateImmediate() {
        GlucoseTileUpdateRequester.requestUpdateImmediate(this)
    }

    private fun notifyComplicationReading(snapshot: GlucoseSnapshot, sequenceId: Long) {
        ComplicationUpdateNotifier.notifyReadingChanged(
            context = this,
            sequenceId = sequenceId,
            readingTimestampEpochMs = snapshot.timestampEpochMs,
        )
    }

    private fun sendAck(
        cache: GlucoseCache,
        healthMonitor: WatchSyncHealthMonitor,
        readingTimestampEpochMs: Long,
        sequenceId: Long,
    ) {
        serviceScope.launch {
            val sender = WearAckSender()
            val sent = sender.send {
                val now = System.currentTimeMillis()
                val request = PutDataMapRequest.create(GlucoseKeys.PATH_WATCH_ACK).apply {
                    dataMap.putLong(GlucoseKeys.ACK_READING_TIMESTAMP_EPOCH_MS, readingTimestampEpochMs)
                    dataMap.putLong(GlucoseKeys.ACK_SEQUENCE_ID, sequenceId)
                    dataMap.putLong(GlucoseKeys.ACK_RECEIVED_AT, now)
                    dataMap.putLong(GlucoseKeys.SEQUENCE_ID, now)
                }.asPutDataRequest().setUrgent()
                Wearable.getDataClient(this@WearDataLayerListenerService).putDataItem(request).await()
            }
            if (sent) {
                return@launch
            }

            Log.w(TAG, "ack_failed sequenceId=$sequenceId attempts=${WearAckSender.MAX_ACK_ATTEMPTS}")
            cache.recordAckFailed(sequenceId)
            healthMonitor.recordAckFailure()
        }
    }

    private fun isForThisWatch(targetNodeId: String): Boolean {
        if (targetNodeId.isBlank()) return true
        val localNodeId = runCatching {
            Tasks.await(Wearable.getNodeClient(this).localNode).id
        }.getOrDefault("")
        return localNodeId.isBlank() || localNodeId == targetNodeId
    }

    companion object {
        private const val TAG = "WG7.WearDataLayer"
    }
}
