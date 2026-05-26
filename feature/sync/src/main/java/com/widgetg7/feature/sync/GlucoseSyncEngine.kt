package com.widgetg7.feature.sync

import com.widgetg7.core.model.GlucoseReading

/** Fetches the latest reading from a configured glucose source (e.g. Dexcom Share). */
interface GlucoseSourcePort {
    val sourceName: String
    suspend fun latest(): GlucoseReading
}

/** Tracks what was last pushed to the watch for deduplication and sequencing. */
data class SyncStateSnapshot(
    val lastPushedReadingTimestampEpochMs: Long,
)

/** Persists sync state: last push timestamp, sequence IDs, and fetch attempts. */
interface SyncStatePort {
    fun load(): SyncStateSnapshot
    fun recordFetchAttempt()
    fun recordFetchedReading(timestampEpochMs: Long)
    fun nextSequenceId(): Long
    fun recordPushSuccess(reading: GlucoseReading, sequenceId: Long)
}

/** Pushes a glucose reading to the watch via Wear Data Layer. */
interface WearSyncPort {
    suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean
}

/** Notifies the watch tile of refresh progress when sync is watch-initiated. */
interface RefreshStatusPort {
    suspend fun pushCompletedPhoneUpToDateWatchUnavailable()
    suspend fun pushCompletedNoNewReading()
}

/**
 * Orchestrates one sync pass: fetch from source, dedupe, push to watch, queue on failure.
 *
 * Watch push runs when there is a new reading, [forcePushCurrentReading], or a pending retry.
 */
class GlucoseSyncEngine(
    private val source: GlucoseSourcePort,
    private val syncState: SyncStatePort,
    private val wearSync: WearSyncPort,
    private val refreshStatus: RefreshStatusPort,
    private val pendingPush: PendingPushPort? = null,
) {
    /** Runs a full sync pass; returns whether the reading changed plus watch delivery outcome. */
    suspend fun run(
        triggeredFromWatch: Boolean,
        forcePushCurrentReading: Boolean = false,
    ): SyncExecutionResult {
        syncState.recordFetchAttempt()
        val previous = syncState.load()
        val reading = source.latest()
        syncState.recordFetchedReading(reading.timestampEpochMs)

        val hasNewReading = previous.lastPushedReadingTimestampEpochMs != reading.timestampEpochMs
        val hasPendingPush = pendingPush?.hasPending() == true
        val shouldPushToWatch = hasNewReading || forcePushCurrentReading || hasPendingPush

        var watchDelivery = WatchDeliveryStatus.NOT_APPLICABLE

        if (shouldPushToWatch) {
            val sequenceId = syncState.nextSequenceId()
            val pushed = wearSync.pushLatest(reading, sequenceId)
            if (pushed) {
                syncState.recordPushSuccess(reading, sequenceId)
                pendingPush?.clear()
                watchDelivery = WatchDeliveryStatus.DELIVERED
            } else {
                watchDelivery =
                    if (pendingPush != null) {
                        pendingPush.enqueue(reading)
                        WatchDeliveryStatus.QUEUED
                    } else {
                        WatchDeliveryStatus.WATCH_UNAVAILABLE
                    }
                if (triggeredFromWatch) {
                    refreshStatus.pushCompletedPhoneUpToDateWatchUnavailable()
                }
            }
        } else if (triggeredFromWatch) {
            refreshStatus.pushCompletedNoNewReading()
        }

        return if (hasNewReading) {
            SyncExecutionResult.SuccessNewReading(source.sourceName, watchDelivery)
        } else {
            SyncExecutionResult.SuccessNoNewReading(source.sourceName, watchDelivery)
        }
    }
}
