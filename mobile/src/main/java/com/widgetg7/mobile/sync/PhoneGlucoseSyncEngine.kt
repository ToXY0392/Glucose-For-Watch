package com.widgetg7.mobile.sync

import android.content.Context
import android.util.Log
import com.widgetg7.mobile.data.GlucoseReading
import com.widgetg7.mobile.data.PhoneGlucoseSourceFactory
import com.widgetg7.mobile.notifications.NotificationHelper
import com.widgetg7.mobile.status.SyncErrorCategory
import com.widgetg7.mobile.status.SyncStatusRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.system.measureTimeMillis

class PhoneGlucoseSyncEngine(private val context: Context) {
    private val logTag = "WidgetG7Phone"

    suspend fun run(
        triggeredFromWatch: Boolean,
        forcePushCurrentReading: Boolean = false,
    ): SyncExecutionResult {
        return try {
            Log.d(
                logTag,
                "Sync engine started triggeredFromWatch=$triggeredFromWatch forcePushCurrentReading=$forcePushCurrentReading",
            )
            val source = PhoneGlucoseSourceFactory.create(context)
            val syncStatusRepository = SyncStatusRepository(context)
            val syncStateStore = PhoneSyncStateStore(context)
            val previousSyncState = syncStateStore.load()
            syncStateStore.recordFetchAttempt()
            var readingFetchMs = 0L
            val reading = withTimeout(FETCH_TIMEOUT_MS) {
                lateinit var latestReading: GlucoseReading
                readingFetchMs = measureTimeMillis {
                    latestReading = source.latest()
                }
                latestReading
            }
            Log.d(
                logTag,
                "Sync engine fetched value=${reading.valueMgDl} trend=${reading.trend} delta=${reading.deltaMgDl} stale=${reading.stale} fetchMs=$readingFetchMs",
            )
            syncStateStore.recordFetchedReading(reading.timestampEpochMs)

            val hasNewReading = previousSyncState.lastPushedReadingTimestampEpochMs != reading.timestampEpochMs
            val shouldPushToWatch = hasNewReading || forcePushCurrentReading
            if (shouldPushToWatch) {
                val sequenceId = syncStateStore.nextSequenceId()
                val wearPushMs = measureTimeMillis {
                    withTimeout(WEAR_PUSH_TIMEOUT_MS) {
                        PhoneWearSyncService(context).pushLatest(reading, sequenceId)
                    }
                }
                syncStateStore.recordPushSuccess(reading.timestampEpochMs, sequenceId)
                Log.d(
                    logTag,
                    "Sync engine push completed wearPushMs=$wearPushMs source=${source.sourceName} hasNewReading=$hasNewReading sequenceId=$sequenceId",
                )
            } else {
                Log.d(
                    logTag,
                    "Sync engine skipped wear push because reading timestamp is unchanged timestamp=${reading.timestampEpochMs}",
                )
                if (triggeredFromWatch) {
                    PhoneWearRefreshStatusService(context).pushCompleted("Aucune nouvelle mesure")
                }
            }

            syncStatusRepository.saveSuccess(source.sourceName, reading)
            NotificationHelper(context).cancelSyncAlerts()
            if (hasNewReading) {
                SyncExecutionResult.SuccessNewReading(source.sourceName)
            } else {
                SyncExecutionResult.SuccessNoNewReading(source.sourceName)
            }
        } catch (t: TimeoutCancellationException) {
            handleFailure(
                triggeredFromWatch = triggeredFromWatch,
                error = IllegalStateException("Delai depasse pendant la synchronisation.", t),
            )
        } catch (t: Throwable) {
            handleFailure(triggeredFromWatch, t)
        }
    }

    private suspend fun handleFailure(triggeredFromWatch: Boolean, error: Throwable): SyncExecutionResult.Failure {
        val message = SyncText.toUserMessage(error)
        val syncStatusRepository = SyncStatusRepository(context)
        PhoneSyncStateStore(context).recordPushFailure(message)
        syncStatusRepository.saveError(
            message = message,
            category = SyncText.toCategory(error),
        )
        notifyIfNeeded(syncStatusRepository.load())
        if (triggeredFromWatch) {
            PhoneWearRefreshStatusService(context).pushFailure(message)
        }
        Log.e(logTag, "Sync engine failed triggeredFromWatch=$triggeredFromWatch message=$message", error)
        return SyncExecutionResult.Failure(message)
    }

    private fun notifyIfNeeded(syncStatus: com.widgetg7.mobile.status.SyncStatusSnapshot) {
        val notificationHelper = NotificationHelper(context)
        when {
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                notificationHelper.notifyDexcomReconnectRequired()

            syncStatus.consecutiveFailureCount >= 3 ->
                notificationHelper.notifySyncInterrupted(syncStatus.lastError.ifBlank { "La synchronisation a besoin de votre attention." })
        }
    }

    companion object {
        private const val FETCH_TIMEOUT_MS = 12_000L
        private const val WEAR_PUSH_TIMEOUT_MS = 8_000L
    }
}

sealed interface SyncExecutionResult {
    data class SuccessNewReading(val sourceName: String) : SyncExecutionResult
    data class SuccessNoNewReading(val sourceName: String) : SyncExecutionResult
    data class Failure(val message: String) : SyncExecutionResult
}
