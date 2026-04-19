package com.widgetg7.mobile.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.widgetg7.mobile.data.PhoneGlucoseSourceFactory
import com.widgetg7.mobile.dexcom.DexcomShareErrorKind
import com.widgetg7.mobile.dexcom.DexcomShareException
import com.widgetg7.mobile.status.SyncErrorCategory
import com.widgetg7.mobile.status.SyncStatusRepository
import com.widgetg7.mobile.sync.PhoneWearSyncService

class PhoneGlucoseSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    private val logTag = "WidgetG7Phone"

    override suspend fun doWork(): Result {
        return try {
            Log.d(logTag, "Worker sync started")
            val source = PhoneGlucoseSourceFactory.create(applicationContext)
            val reading = source.latest()
            Log.d(
                logTag,
                "Worker fetched value=${reading.valueMgDl} trend=${reading.trend} delta=${reading.deltaMgDl} stale=${reading.stale}",
            )
            PhoneWearSyncService(applicationContext).pushLatest(reading)
            SyncStatusRepository(applicationContext).saveSuccess(source.sourceName, reading)
            Log.d(logTag, "Worker sync push completed")
            Result.success()
        } catch (t: Throwable) {
            SyncStatusRepository(applicationContext).saveError(
                message = toUserMessage(t),
                category = toCategory(t),
            )
            Log.e(logTag, "Worker sync failed", t)
            Result.retry()
        }
    }

    private fun toCategory(t: Throwable): SyncErrorCategory {
        if (t is DexcomShareException) {
            return when (t.kind) {
                DexcomShareErrorKind.AUTH -> SyncErrorCategory.AUTH
                DexcomShareErrorKind.NETWORK -> SyncErrorCategory.NETWORK
                DexcomShareErrorKind.NO_DATA -> SyncErrorCategory.OTHER
                DexcomShareErrorKind.UNKNOWN -> SyncErrorCategory.OTHER
            }
        }
        return SyncErrorCategory.OTHER
    }

    private fun toUserMessage(t: Throwable): String {
        return when {
            t is DexcomShareException -> t.message
            else -> t.message ?: "Erreur inconnue"
        }
    }
}
