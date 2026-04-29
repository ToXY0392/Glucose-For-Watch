package com.widgetg7.mobile.sync

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
import com.widgetg7.mobile.watch.WatchSyncHealthStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PhoneWearRefreshRequestService : WearableListenerService() {
    private val logTag = "WidgetG7Phone"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path != GlucoseKeys.PATH_REFRESH_REQUEST) {
            super.onMessageReceived(messageEvent)
            return
        }

        Log.d(logTag, "Watch refresh requested from node=${messageEvent.sourceNodeId}")
        serviceScope.launch {
            PhoneWearRefreshStatusService(this@PhoneWearRefreshRequestService).pushInProgress()
            PhoneGlucoseSyncEngine(this@PhoneWearRefreshRequestService).run(
                triggeredFromWatch = true,
                forcePushCurrentReading = true,
            )
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type != DataEvent.TYPE_CHANGED) continue
            val item = event.dataItem

            if (item.uri.path == GlucoseKeys.PATH_WATCH_ACK) {
                val map = DataMapItem.fromDataItem(item).dataMap
                val readingTimestamp = map.getLong(GlucoseKeys.ACK_READING_TIMESTAMP_EPOCH_MS)
                val sequenceId = map.getLong(GlucoseKeys.ACK_SEQUENCE_ID)
                val nodeId = item.uri.host.orEmpty()
                PhoneSyncStateStore(this).recordWatchAck(
                    readingTimestampEpochMs = readingTimestamp,
                    sequenceId = sequenceId,
                    nodeId = nodeId,
                )
                Log.d(
                    logTag,
                    "Watch ack received node=$nodeId readingTimestamp=$readingTimestamp sequenceId=$sequenceId",
                )
                continue
            }

            if (item.uri.path != GlucoseKeys.PATH_WATCH_STATUS) continue

            val map = DataMapItem.fromDataItem(item).dataMap
            val status = WatchSyncHealthStatus(
                batteryLevel = map.getInt(GlucoseKeys.WATCH_BATTERY_LEVEL, -1),
                lowPowerMode = map.getBoolean(GlucoseKeys.WATCH_LOW_POWER),
                syncLimited = map.getBoolean(GlucoseKeys.WATCH_SYNC_LIMITED),
                message = map.getString(GlucoseKeys.WATCH_STATUS_MESSAGE).orEmpty(),
                updatedAtEpochMs = map.getLong(GlucoseKeys.WATCH_STATUS_UPDATED_AT),
                manufacturer = map.getString(GlucoseKeys.WATCH_MANUFACTURER).orEmpty(),
                model = map.getString(GlucoseKeys.WATCH_MODEL).orEmpty(),
                device = map.getString(GlucoseKeys.WATCH_DEVICE).orEmpty(),
            )
            WatchSyncHealthRepository(this).save(status)
            Log.d(
                logTag,
                "Watch health updated battery=${status.batteryLevel} lowPower=${status.lowPowerMode} syncLimited=${status.syncLimited} message=${status.message}",
            )
        }
        super.onDataChanged(dataEvents)
    }
}
