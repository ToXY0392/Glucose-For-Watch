package com.glucoseforwatch.mobile.watch

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.glucoseforwatch.mobile.sync.GlucoseKeys
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

/** Requests a fresh watch status snapshot via Data Layer. */
class WatchStatusVerifier(private val context: Context) {
    suspend fun requestStatus(targetNodeId: String): WatchSyncHealthStatus? {
        val requestedAt = System.currentTimeMillis()
        val request = PutDataMapRequest.create(GlucoseKeys.PATH_WATCH_STATUS_REQUEST).apply {
            dataMap.putString(GlucoseKeys.TARGET_NODE_ID, targetNodeId)
            dataMap.putLong(GlucoseKeys.SEQUENCE_ID, requestedAt)
        }.asPutDataRequest().setUrgent()

        Wearable.getDataClient(context).putDataItem(request).await()

        return withTimeoutOrNull<WatchSyncHealthStatus>(STATUS_TIMEOUT_MS) {
            val repository = WatchSyncHealthRepository(context)
            var freshStatus: WatchSyncHealthStatus? = null
            while (freshStatus == null) {
                val status = repository.load()
                if (status != null && status.updatedAtEpochMs >= requestedAt) {
                    freshStatus = status
                } else {
                    delay(POLL_INTERVAL_MS)
                }
            }
            freshStatus
        }
    }

    private companion object {
        private const val STATUS_TIMEOUT_MS = 4_000L
        private const val POLL_INTERVAL_MS = 250L
    }
}
