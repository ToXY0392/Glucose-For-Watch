package com.glucoseforwatch.mobile.sync

import android.content.Context
import android.util.Log
import com.glucoseforwatch.core.model.GlucoseReading

/** Pushes a queued reading directly to the watch (Dexcom fetch not required). */
object PendingPushFlusher {
    private const val TAG = "WG7.PendingPushFlusher"

    suspend fun flush(context: Context): Boolean {
        val queue = PendingPushQueue(context)
        val reading = queue.loadReading() ?: return false
        return pushReading(context, reading, queue)
    }

    internal suspend fun pushReading(
        context: Context,
        reading: GlucoseReading,
        queue: PendingPushQueue = PendingPushQueue(context),
    ): Boolean {
        val syncStateStore = PhoneSyncStateStore(context)
        val sequenceId = syncStateStore.nextSequenceId()
        val pushed =
            runCatching {
                PhoneWearSyncService(context).pushLatest(reading, sequenceId)
            }.getOrElse { error ->
                syncStateStore.recordPushFailure(error.message.orEmpty())
                Log.w(TAG, "flush_push_failed error=${error.message}", error)
                false
            }
        if (pushed) {
            syncStateStore.recordPushSuccess(
                timestampEpochMs = reading.timestampEpochMs,
                sequenceId = sequenceId,
                valueMgDl = reading.valueMgDl,
                trend = reading.trend,
                deltaMgDl = reading.deltaMgDl,
                stale = reading.stale,
            )
            queue.clear()
            Log.i(TAG, "flush_success sequenceId=$sequenceId readingTs=${reading.timestampEpochMs}")
        } else {
            syncStateStore.recordWearPushUndelivered()
            Log.w(TAG, "flush_skipped watch_unavailable readingTs=${reading.timestampEpochMs}")
        }
        return pushed
    }
}
