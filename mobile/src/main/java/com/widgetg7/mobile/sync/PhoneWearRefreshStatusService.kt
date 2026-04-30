package com.widgetg7.mobile.sync

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.widgetg7.mobile.watch.WatchConnectionRepository
import kotlinx.coroutines.tasks.await

class PhoneWearRefreshStatusService(private val context: Context) {
    suspend fun pushInProgress() {
        pushStatus(
            status = GlucoseKeys.REFRESH_IN_PROGRESS,
            message = "Actualisation...",
        )
    }

    suspend fun pushFailure(message: String) {
        pushStatus(
            status = GlucoseKeys.REFRESH_FAILED,
            message = message,
        )
    }

    suspend fun pushCompleted(message: String = "") {
        pushStatus(
            status = GlucoseKeys.REFRESH_COMPLETED,
            message = message,
        )
    }

    private suspend fun pushStatus(status: String, message: String) {
        val targetNodeId = WatchConnectionRepository(context).loadStatus()
            .takeIf { it.connected }
            ?.nodeId
            .orEmpty()
        val request = PutDataMapRequest.create(GlucoseKeys.PATH_REFRESH_STATUS).apply {
            dataMap.putString(GlucoseKeys.REFRESH_STATUS, status)
            dataMap.putString(GlucoseKeys.REFRESH_MESSAGE, message)
            dataMap.putLong(GlucoseKeys.REFRESH_UPDATED_AT, System.currentTimeMillis())
            dataMap.putString(GlucoseKeys.TARGET_NODE_ID, targetNodeId)
            dataMap.putLong(GlucoseKeys.PUSH_VERSION, System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(context).putDataItem(request).await()
    }
}
