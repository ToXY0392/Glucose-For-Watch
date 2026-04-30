package com.widgetg7.mobile.sync

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.widgetg7.mobile.data.GlucoseReading
import kotlinx.coroutines.tasks.await

class PhoneWearSyncService(private val context: Context) {
    suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long) {
        val dataClient = Wearable.getDataClient(context)
        val nodeClient = Wearable.getNodeClient(context)
        val connectedNodes = nodeClient.connectedNodes.await()
        if (connectedNodes.isEmpty()) {
            throw IllegalStateException("Aucune montre connectee via Wear OS.")
        }

        val request = PutDataMapRequest.create(GlucoseKeys.PATH_LATEST).apply {
            dataMap.putInt(GlucoseKeys.VALUE_MG_DL, reading.valueMgDl)
            dataMap.putString(GlucoseKeys.TREND, reading.trend)
            dataMap.putInt(GlucoseKeys.DELTA_MG_DL, reading.deltaMgDl)
            dataMap.putLong(GlucoseKeys.TIMESTAMP_EPOCH_MS, reading.timestampEpochMs)
            dataMap.putBoolean(GlucoseKeys.STALE, reading.stale)
            dataMap.putLong(GlucoseKeys.SEQUENCE_ID, sequenceId)
            dataMap.putLong(GlucoseKeys.PUSH_VERSION, sequenceId)
        }.asPutDataRequest().setUrgent()

        dataClient.putDataItem(request).await()
    }
}
