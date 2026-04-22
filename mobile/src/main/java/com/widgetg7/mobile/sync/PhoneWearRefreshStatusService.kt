package com.widgetg7.mobile.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class PhoneWearRefreshStatusService(private val context: Context) {
    private val logTag = "WidgetG7Phone"

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
        val request = PutDataMapRequest.create(GlucoseKeys.PATH_REFRESH_STATUS).apply {
            dataMap.putString(GlucoseKeys.REFRESH_STATUS, status)
            dataMap.putString(GlucoseKeys.REFRESH_MESSAGE, message)
            dataMap.putLong(GlucoseKeys.REFRESH_UPDATED_AT, System.currentTimeMillis())
            dataMap.putLong(GlucoseKeys.PUSH_VERSION, System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()

        val item = Wearable.getDataClient(context).putDataItem(request).await()
        Log.d(logTag, "Put refresh status success uri=${item.uri} status=$status")
    }
}
