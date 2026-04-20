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

    suspend fun run(triggeredFromWatch: Boolean): SyncExecutionResult {
        return try {
            Log.d(logTag, "Sync engine started triggeredFromWatch=$triggeredFromWatch")
            val source = PhoneGlucoseSourceFactory.create(context)
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
            val wearPushMs = measureTimeMillis {
                withTimeout(WEAR_PUSH_TIMEOUT_MS) {
                    PhoneWearSyncService(context).pushLatest(reading)
                }
            }
            SyncStatusRepository(context).saveSuccess(source.sourceName, reading)
            NotificationHelper(context).cancelSyncAlerts()
            Log.d(logTag, "Sync engine push completed wearPushMs=$wearPushMs source=${source.sourceName}")
            SyncExecutionResult.Success(source.sourceName)
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
    data class Success(val sourceName: String) : SyncExecutionResult
    data class Failure(val message: String) : SyncExecutionResult
}
