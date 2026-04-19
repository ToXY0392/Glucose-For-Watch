package com.widgetg7.mobile.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.widgetg7.mobile.data.PhoneGlucoseSourceFactory
import com.widgetg7.mobile.sync.PhoneWearSyncService

class PhoneGlucoseSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    private val logTag = "WidgetG7Phone"

    override suspend fun doWork(): Result {
        return try {
            Log.d(logTag, "Worker sync started")
            val source = PhoneGlucoseSourceFactory.create()
            val reading = source.latest()
            Log.d(
                logTag,
                "Worker fetched value=${reading.valueMgDl} trend=${reading.trend} delta=${reading.deltaMgDl} stale=${reading.stale}",
            )
            PhoneWearSyncService(applicationContext).pushLatest(reading)
            Log.d(logTag, "Worker sync push completed")
            Result.success()
        } catch (t: Throwable) {
            Log.e(logTag, "Worker sync failed", t)
            Result.retry()
        }
    }
}
