package com.glucoseforwatch.mobile.sync

import android.content.Context
import android.util.Log
import com.glucoseforwatch.core.model.GlucoseReading
import com.glucoseforwatch.feature.sync.GlucoseSourcePort
import com.glucoseforwatch.feature.sync.GlucoseSyncEngine
import com.glucoseforwatch.feature.sync.RefreshStatusPort
import com.glucoseforwatch.feature.sync.SyncMessageCatalog
import com.glucoseforwatch.feature.sync.SyncNotificationAction
import com.glucoseforwatch.feature.sync.SyncExecutionResult
import com.glucoseforwatch.feature.sync.SyncStatePort
import com.glucoseforwatch.feature.sync.SyncStateSnapshot
import com.glucoseforwatch.feature.sync.SyncStatusRepository
import com.glucoseforwatch.feature.sync.WearSyncPort
import com.glucoseforwatch.mobile.data.PhoneGlucoseSourceFactory
import com.glucoseforwatch.mobile.notifications.NotificationHelper
import com.glucoseforwatch.mobile.watch.WatchSyncHealthRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

/** Phone-side glucose sync: Dexcom Share fetch, watch push, and push-failure tracking. */
class PhoneGlucoseSyncEngine(private val context: Context) {
    suspend fun run(
        triggeredFromWatch: Boolean,
        forcePushCurrentReading: Boolean = false,
    ): SyncExecutionResult {
        return try {
            val source = PhoneGlucoseSourceFactory.create(context)
            val syncStatusRepository = SyncStatusRepository(context)
            val syncStateStore = PhoneSyncStateStore(context)
            val pendingPushQueue = PendingPushQueue(context)
            var latestReading: GlucoseReading? = null
            val result = GlucoseSyncEngine(
                source = object : GlucoseSourcePort {
                    override val sourceName: String = source.sourceName
                    override suspend fun latest(): GlucoseReading = withTimeout(FETCH_TIMEOUT_MS) {
                        source.latest().also { latestReading = it }
                    }
                },
                syncState = object : SyncStatePort {
                    override fun load(): SyncStateSnapshot =
                        SyncStateSnapshot(
                            lastPushedReadingTimestampEpochMs = syncStateStore.load().lastPushedReadingTimestampEpochMs,
                        )

                    override fun recordFetchAttempt() = syncStateStore.recordFetchAttempt()

                    override fun recordFetchedReading(timestampEpochMs: Long) =
                        syncStateStore.recordFetchedReading(timestampEpochMs)

                    override fun nextSequenceId(): Long = syncStateStore.nextSequenceId()

                    override fun recordPushSuccess(reading: GlucoseReading, sequenceId: Long) {
                        syncStateStore.recordPushSuccess(
                            timestampEpochMs = reading.timestampEpochMs,
                            sequenceId = sequenceId,
                            valueMgDl = reading.valueMgDl,
                            trend = reading.trend,
                            deltaMgDl = reading.deltaMgDl,
                            stale = reading.stale,
                        )
                    }
                },
                wearSync = object : WearSyncPort {
                    override suspend fun pushLatest(reading: GlucoseReading, sequenceId: Long): Boolean {
                        val pushed =
                            withTimeout(WEAR_PUSH_TIMEOUT_MS) {
                                PhoneWearSyncService(context).pushLatest(reading, sequenceId)
                            }
                        if (pushed) {
                            syncStateStore.recordWearPushDelivered()
                        } else {
                            syncStateStore.recordWearPushUndelivered()
                        }
                        return pushed
                    }
                },
                refreshStatus = object : RefreshStatusPort {
                    override suspend fun pushCompletedPhoneUpToDateWatchUnavailable() {
                        PhoneWearRefreshStatusService(context)
                            .pushCompleted(degradedWatchMessage(SyncMessageCatalog.REFRESH_PHONE_UP_TO_DATE_WATCH_UNAVAILABLE))
                    }

                    override suspend fun pushCompletedNoNewReading() {
                        PhoneWearRefreshStatusService(context).pushCompleted(degradedWatchMessage(SyncMessageCatalog.REFRESH_NO_NEW_READING))
                    }
                },
                pendingPush = pendingPushQueue,
            ).run(
                triggeredFromWatch = triggeredFromWatch,
                forcePushCurrentReading = forcePushCurrentReading,
            )

            Log.i(TAG, "sync_result fromWatch=$triggeredFromWatch forcePush=$forcePushCurrentReading result=${result::class.simpleName}")
            latestReading?.let { reading ->
                PhoneSyncStatusPersister.persist(syncStatusRepository, result, reading)
            }
            NotificationHelper(context).cancelSyncAlerts()
            result
        } catch (t: TimeoutCancellationException) {
            handleFailure(
                triggeredFromWatch = triggeredFromWatch,
                error = IllegalStateException(SyncMessageCatalog.SYNC_TIMEOUT, t),
            )
        } catch (t: Throwable) {
            handleFailure(triggeredFromWatch, t)
        }
    }

    private suspend fun handleFailure(triggeredFromWatch: Boolean, error: Throwable): SyncExecutionResult.Failure {
        val syncStatusRepository = SyncStatusRepository(context)
        val outcome = PhoneSyncFailureHandler.evaluate(error, syncStatusRepository.load())
        Log.e(TAG, "sync_failure triggeredFromWatch=$triggeredFromWatch message=${outcome.message}", error)
        PhoneSyncStateStore(context).recordPushFailure(outcome.message)
        syncStatusRepository.saveError(
            message = outcome.message,
            category = outcome.category,
        )
        notifyIfNeeded(outcome.notificationAction, syncStatusRepository.load().lastError)
        if (triggeredFromWatch) {
            PhoneWearRefreshStatusService(context).pushFailure(outcome.message)
        }
        return SyncExecutionResult.Failure(outcome.message)
    }

    private fun notifyIfNeeded(notificationAction: SyncNotificationAction?, lastError: String) {
        val notificationHelper = NotificationHelper(context)
        when (notificationAction) {
            SyncNotificationAction.DEXCOM_RECONNECT_REQUIRED ->
                notificationHelper.notifyDexcomReconnectRequired()

            SyncNotificationAction.SYNC_INTERRUPTED ->
                notificationHelper.notifySyncInterrupted(
                    lastError.ifBlank { SyncMessageCatalog.SYNC_NEEDS_ATTENTION },
                )

            null -> Unit
        }
    }

    private fun degradedWatchMessage(base: String): String {
        val health = WatchSyncHealthRepository(context).load()
        return WatchBatteryPolicy.withDegradedSuffix(base, health)
    }

    companion object {
        private const val TAG = "WG7.PhoneSyncEngine"
        private const val FETCH_TIMEOUT_MS = 12_000L
        private const val WEAR_PUSH_TIMEOUT_MS = 8_000L
    }
}
