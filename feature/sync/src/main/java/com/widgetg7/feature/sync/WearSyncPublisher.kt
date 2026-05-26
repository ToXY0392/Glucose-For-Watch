package com.widgetg7.feature.sync

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.widgetg7.core.datalayer.GlucoseDataLayerContract
import com.widgetg7.core.model.GlucoseReading
import kotlinx.coroutines.tasks.await

/** Pushes the latest glucose reading to the watch via an urgent Data Layer item. */
class WearSyncPublisher(
    private val context: Context,
    private val resolveTargetNodeId: suspend () -> String?,
    private val resolveSourceNodeId: suspend () -> String? = { null },
) {
    /** Returns false when no target watch node is connected. */
    suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean {
        val nodeId = resolveTargetNodeId().orEmpty()
        if (nodeId.isBlank()) {
            return false
        }

        val request = PutDataMapRequest.create(GlucoseDataLayerContract.PATH_LATEST).apply {
            dataMap.putInt(GlucoseDataLayerContract.VALUE_MG_DL, reading.valueMgDl)
            dataMap.putString(GlucoseDataLayerContract.TREND, reading.trend)
            dataMap.putInt(GlucoseDataLayerContract.DELTA_MG_DL, reading.deltaMgDl)
            dataMap.putLong(GlucoseDataLayerContract.TIMESTAMP_EPOCH_MS, reading.timestampEpochMs)
            dataMap.putBoolean(GlucoseDataLayerContract.STALE, reading.stale)
            dataMap.putLong(GlucoseDataLayerContract.SEQUENCE_ID, sequenceId)
            dataMap.putString(GlucoseDataLayerContract.TARGET_NODE_ID, nodeId)
            dataMap.putLong(GlucoseDataLayerContract.PUSH_VERSION, sequenceId)
            resolveSourceNodeId()?.takeIf { it.isNotBlank() }?.let { sourceNodeId ->
                dataMap.putString(GlucoseDataLayerContract.SOURCE_PHONE_NODE_ID, sourceNodeId)
            }
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(context).putDataItem(request).await()
        return true
    }
}
