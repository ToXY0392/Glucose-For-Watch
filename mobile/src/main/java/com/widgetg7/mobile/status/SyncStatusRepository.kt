package com.widgetg7.mobile.status

import android.content.Context
import com.widgetg7.mobile.data.GlucoseReading

data class SyncStatusSnapshot(
    val lastValueMgDl: Int?,
    val lastTrend: String,
    val lastSourceName: String,
    val lastSyncEpochMs: Long,
    val lastError: String,
) {
    fun hasSuccessfulSync(): Boolean = lastValueMgDl != null && lastSyncEpochMs > 0L
}

class SyncStatusRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): SyncStatusSnapshot {
        val lastValue = if (prefs.contains(KEY_LAST_VALUE)) prefs.getInt(KEY_LAST_VALUE, 0) else null
        return SyncStatusSnapshot(
            lastValueMgDl = lastValue,
            lastTrend = prefs.getString(KEY_LAST_TREND, "").orEmpty(),
            lastSourceName = prefs.getString(KEY_LAST_SOURCE, "").orEmpty(),
            lastSyncEpochMs = prefs.getLong(KEY_LAST_SYNC_AT, 0L),
            lastError = prefs.getString(KEY_LAST_ERROR, "").orEmpty(),
        )
    }

    fun saveSuccess(sourceName: String, reading: GlucoseReading) {
        prefs.edit()
            .putInt(KEY_LAST_VALUE, reading.valueMgDl)
            .putString(KEY_LAST_TREND, reading.trend)
            .putString(KEY_LAST_SOURCE, sourceName)
            .putLong(KEY_LAST_SYNC_AT, System.currentTimeMillis())
            .putString(KEY_LAST_ERROR, "")
            .apply()
    }

    fun saveError(message: String) {
        prefs.edit()
            .putLong(KEY_LAST_SYNC_AT, System.currentTimeMillis())
            .putString(KEY_LAST_ERROR, message)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_sync_status"
        private const val KEY_LAST_VALUE = "last_value"
        private const val KEY_LAST_TREND = "last_trend"
        private const val KEY_LAST_SOURCE = "last_source"
        private const val KEY_LAST_SYNC_AT = "last_sync_at"
        private const val KEY_LAST_ERROR = "last_error"
    }
}
