package com.glucoseforwatch.mobile.sync

import android.content.Context
import com.glucoseforwatch.core.model.GlucoseReading
import com.glucoseforwatch.feature.sync.PendingPushPort

/**
 * Phone-side queue holding the latest reading that failed to reach the watch.
 * Survives process death so WorkManager / reconnect can flush without a new Dexcom fetch.
 */
class PendingPushQueue(context: Context) : PendingPushPort {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun hasPending(): Boolean = prefs.contains(KEY_TIMESTAMP)

    override fun enqueue(reading: GlucoseReading) {
        prefs.edit()
            .putInt(KEY_VALUE, reading.valueMgDl)
            .putString(KEY_TREND, reading.trend)
            .putInt(KEY_DELTA, reading.deltaMgDl)
            .putLong(KEY_TIMESTAMP, reading.timestampEpochMs)
            .putBoolean(KEY_STALE, reading.stale)
            .putLong(KEY_ENQUEUED_AT, System.currentTimeMillis())
            .apply()
    }

    override fun clear() {
        prefs.edit()
            .remove(KEY_VALUE)
            .remove(KEY_TREND)
            .remove(KEY_DELTA)
            .remove(KEY_TIMESTAMP)
            .remove(KEY_STALE)
            .remove(KEY_ENQUEUED_AT)
            .apply()
    }

    fun loadReading(): GlucoseReading? {
        if (!hasPending()) return null
        val timestamp = prefs.getLong(KEY_TIMESTAMP, 0L)
        if (timestamp <= 0L) return null
        return GlucoseReading(
            valueMgDl = prefs.getInt(KEY_VALUE, 0),
            trend = prefs.getString(KEY_TREND, "").orEmpty(),
            deltaMgDl = prefs.getInt(KEY_DELTA, 0),
            timestampEpochMs = timestamp,
            stale = prefs.getBoolean(KEY_STALE, true),
        )
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_pending_push_queue"
        private const val KEY_VALUE = "value_mg_dl"
        private const val KEY_TREND = "trend"
        private const val KEY_DELTA = "delta_mg_dl"
        private const val KEY_TIMESTAMP = "timestamp_epoch_ms"
        private const val KEY_STALE = "stale"
        private const val KEY_ENQUEUED_AT = "enqueued_at_epoch_ms"
    }
}
