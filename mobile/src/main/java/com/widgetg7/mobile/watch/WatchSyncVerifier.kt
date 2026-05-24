package com.widgetg7.mobile.watch

import android.content.Context
import com.widgetg7.mobile.data.PhoneGlucoseSourceFactory
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.sync.PhoneSyncStateStore
import com.widgetg7.mobile.sync.PhoneWearSyncService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

class WatchSyncVerifier(private val context: Context) {
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
            val reading =
                withTimeout(VERIFY_TIMEOUT_MS) {
                    PhoneGlucoseSourceFactory.create(context).latest()
                }
            val sendOk =
                withTimeout(VERIFY_TIMEOUT_MS) {
                    val stateStore = PhoneSyncStateStore(context)
                    val sequenceId = stateStore.nextSequenceId()
                    val ok = PhoneWearSyncService(context).pushLatest(reading, sequenceId)
                    if (ok) {
                        stateStore.recordPushSuccess(
                            timestampEpochMs = reading.timestampEpochMs,
                            sequenceId = sequenceId,
                            valueMgDl = reading.valueMgDl,
                            trend = reading.trend,
                            deltaMgDl = reading.deltaMgDl,
                            stale = reading.stale,
                        )
                    }
                    ok
                }
            if (sendOk) {
                Result.Sent(reading.valueMgDl, reading.trend)
            } else {
                Result.SendFailed
            }
        } catch (_: TimeoutCancellationException) {
            Result.Timeout
        } catch (error: Throwable) {
            Result.Error(error.message)
        }
    }

    companion object {
        private const val VERIFY_TIMEOUT_MS = 12_000L
    }
}
