package com.widgetg7.mobile.watch

import android.content.Context
import com.widgetg7.feature.sync.SyncExecutionResult
import com.widgetg7.feature.sync.WatchDeliveryStatus
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.sync.PhoneGlucoseSyncEngine
import com.widgetg7.mobile.sync.PhoneSyncStateSnapshot
import com.widgetg7.mobile.sync.PhoneSyncStateStore
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

/** End-to-end test: sync via [PhoneGlucoseSyncEngine], then wait for watch ack or timeout. */
class WatchSyncVerifier(private val context: Context) {
    /** Outcome of the watch sync connectivity test. */
    sealed interface Result {
        data object NoWatch : Result

        data object NoDexcom : Result

        data object Timeout : Result

        data class Sent(val valueMgDl: Int, val trend: String) : Result

        data object SendFailed : Result

        data class Error(val message: String?) : Result
    }

    suspend fun runTest(): Result {
        val status = WatchConnectionRepository(context).loadStatus()
        if (!status.connected) {
            return Result.NoWatch
        }

        if (!AppSettingsStore(context).loadDexcomSettings().isConfigured()) {
            return Result.NoDexcom
        }

        return try {
            withTimeout(VERIFY_TIMEOUT_MS) {
                when (val syncResult = PhoneGlucoseSyncEngine(context).run(
                    triggeredFromWatch = false,
                    forcePushCurrentReading = true,
                )) {
                    is SyncExecutionResult.Failure ->
                        Result.Error(syncResult.message)

                    is SyncExecutionResult.SuccessNewReading,
                    is SyncExecutionResult.SuccessNoNewReading,
                    ->
                        mapDeliveredSync(syncResult.watchDelivery)
                }
            }
        } catch (_: TimeoutCancellationException) {
            Result.Timeout
        } catch (error: Throwable) {
            Result.Error(error.message)
        }
    }

    private suspend fun mapDeliveredSync(watchDelivery: WatchDeliveryStatus): Result {
        if (watchDelivery != WatchDeliveryStatus.DELIVERED) {
            return Result.SendFailed
        }

        val stateStore = PhoneSyncStateStore(context)
        val pushSequenceId = stateStore.load().lastPushSequenceId
        if (pushSequenceId <= 0L) {
            return Result.SendFailed
        }

        if (!waitForWatchAck(stateStore::load, pushSequenceId)) {
            return Result.Timeout
        }

        val pushed = stateStore.load()
        val valueMgDl = pushed.lastPushedValueMgDl ?: return Result.SendFailed
        return Result.Sent(valueMgDl, pushed.lastPushedTrend)
    }

    companion object {
        private const val VERIFY_TIMEOUT_MS = 20_000L
        internal const val ACK_WAIT_MS = 8_000L
        internal const val POLL_INTERVAL_MS = 250L

        internal suspend fun waitForWatchAck(
            loadState: () -> PhoneSyncStateSnapshot,
            pushSequenceId: Long,
            timeoutMs: Long = ACK_WAIT_MS,
            pollIntervalMs: Long = POLL_INTERVAL_MS,
        ): Boolean =
            withTimeoutOrNull(timeoutMs) {
                while (true) {
                    val state = loadState()
                    if (state.lastAckSequenceId == pushSequenceId && pushSequenceId > 0L) {
                        return@withTimeoutOrNull true
                    }
                    delay(pollIntervalMs)
                }
                @Suppress("UNREACHABLE_CODE")
                false
            } == true
    }
}
