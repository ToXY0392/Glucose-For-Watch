package com.widgetg7.mobile.sync

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.widgetg7.mobile.data.GlucoseReading
import com.widgetg7.mobile.watch.WatchConnectionRepository
import kotlinx.coroutines.tasks.await

class PhoneWearSyncService(private val context: Context) {
    /**
     * Envoie la donnée vers la montre sélectionnée via Data Layer.
     * @return false si aucune montre Wear n’est joignable (pas d’exception : la sync téléphone peut continuer).
     */
    suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean {
        val dataClient = Wearable.getDataClient(context)
        val watchStatus = WatchConnectionRepository(context).loadStatus()
        if (!watchStatus.connected || watchStatus.nodeId.isBlank()) {
            return false
        }

        val request = PutDataMapRequest.create(GlucoseKeys.PATH_LATEST).apply {
            dataMap.putInt(GlucoseKeys.VALUE_MG_DL, reading.valueMgDl)
            dataMap.putString(GlucoseKeys.TREND, reading.trend)
            dataMap.putInt(GlucoseKeys.DELTA_MG_DL, reading.deltaMgDl)
            dataMap.putLong(GlucoseKeys.TIMESTAMP_EPOCH_MS, reading.timestampEpochMs)
            dataMap.putBoolean(GlucoseKeys.STALE, reading.stale)
            dataMap.putLong(GlucoseKeys.SEQUENCE_ID, sequenceId)
            dataMap.putString(GlucoseKeys.TARGET_NODE_ID, watchStatus.nodeId)
            dataMap.putLong(GlucoseKeys.PUSH_VERSION, sequenceId)
        }.asPutDataRequest().setUrgent()

        dataClient.putDataItem(request).await()
        return true
    }
}
