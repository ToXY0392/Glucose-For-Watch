package com.widgetg7.feature.sync

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.widgetg7.core.datalayer.GlucoseDataLayerContract
import kotlinx.coroutines.tasks.await

class RefreshStatusPublisher(
    private val context: Context,
    private val resolveTargetNodeId: suspend () -> String?,
) {
    suspend fun pushInProgress() {
        pushStatus(
            status = GlucoseDataLayerContract.REFRESH_IN_PROGRESS,
            message = SyncMessageCatalog.REFRESH_IN_PROGRESS,
        )
    }

    suspend fun pushFailure(message: String) {
        pushStatus(
            status = GlucoseDataLayerContract.REFRESH_FAILED,
            message = message,
        )
    }

    suspend fun pushCompleted(message: String = "") {
        pushStatus(
            status = GlucoseDataLayerContract.REFRESH_COMPLETED,
            message = message,
        )
    }

    private suspend fun pushStatus(status: String, message: String) {
        val targetNodeId = resolveTargetNodeId().orEmpty()
        val now = System.currentTimeMillis()
        val request = PutDataMapRequest.create(GlucoseDataLayerContract.PATH_REFRESH_STATUS).apply {
            dataMap.putString(GlucoseDataLayerContract.REFRESH_STATUS, status)
            dataMap.putString(GlucoseDataLayerContract.REFRESH_MESSAGE, message)
            dataMap.putLong(GlucoseDataLayerContract.REFRESH_UPDATED_AT, now)
            dataMap.putString(GlucoseDataLayerContract.TARGET_NODE_ID, targetNodeId)
            dataMap.putLong(GlucoseDataLayerContract.PUSH_VERSION, now)
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(context).putDataItem(request).await()
    }
}
