package com.widgetg7.mobile.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.widgetg7.mobile.data.GlucoseReading
import kotlinx.coroutines.tasks.await

class PhoneWearSyncService(private val context: Context) {
    private val logTag = "WidgetG7Phone"

    suspend fun pushLatest(reading: GlucoseReading) {
        val dataClient = Wearable.getDataClient(context)
        val nodeClient = Wearable.getNodeClient(context)
        val connectedNodes = nodeClient.connectedNodes.await()
        Log.d(logTag, "Connected wear nodes=${connectedNodes.map { it.displayName + "/" + it.id }}")
        if (connectedNodes.isEmpty()) {
            throw IllegalStateException("Aucune montre connectee via Wear OS.")
        }

        val request = PutDataMapRequest.create(GlucoseKeys.PATH_LATEST).apply {
            dataMap.putInt(GlucoseKeys.VALUE_MG_DL, reading.valueMgDl)
            dataMap.putString(GlucoseKeys.TREND, reading.trend)
            dataMap.putInt(GlucoseKeys.DELTA_MG_DL, reading.deltaMgDl)
            dataMap.putLong(GlucoseKeys.TIMESTAMP_EPOCH_MS, reading.timestampEpochMs)
            dataMap.putBoolean(GlucoseKeys.STALE, reading.stale)
            dataMap.putLong(GlucoseKeys.PUSH_VERSION, System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        val item = dataClient.putDataItem(request).await()
        Log.d(logTag, "PutDataItem success uri=${item.uri}")
    }
}
