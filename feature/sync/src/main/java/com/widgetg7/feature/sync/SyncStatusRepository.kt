package com.widgetg7.feature.sync

import android.content.Context
import com.widgetg7.core.model.GlucoseReading
import com.widgetg7.core.model.SyncErrorCategory
import com.widgetg7.core.model.SyncStatusSnapshot

class SyncStatusRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): SyncStatusSnapshot {
        val lastValue = if (prefs.contains(KEY_LAST_VALUE)) prefs.getInt(KEY_LAST_VALUE, 0) else null
        return SyncStatusSnapshot(
            lastValueMgDl = lastValue,
            lastTrend = prefs.getString(KEY_LAST_TREND, "").orEmpty(),
            lastSourceName = prefs.getString(KEY_LAST_SOURCE, "").orEmpty(),
            lastSyncEpochMs = prefs.getLong(KEY_LAST_SYNC_AT, 0L),
            lastReadingTimestampEpochMs = prefs.getLong(KEY_LAST_READING_TIMESTAMP, 0L),
            lastError = prefs.getString(KEY_LAST_ERROR, "").orEmpty(),
            lastErrorCategory = prefs.getString(KEY_LAST_ERROR_CATEGORY, SyncErrorCategory.NONE.name)
                ?.let { runCatching { SyncErrorCategory.valueOf(it) }.getOrDefault(SyncErrorCategory.NONE) }
                ?: SyncErrorCategory.NONE,
            authFailureCount = prefs.getInt(KEY_AUTH_FAILURE_COUNT, 0),
            consecutiveFailureCount = prefs.getInt(KEY_CONSECUTIVE_FAILURE_COUNT, 0),
        )
    }

    fun saveSuccess(sourceName: String, reading: GlucoseReading) {
        prefs.edit()
            .putInt(KEY_LAST_VALUE, reading.valueMgDl)
            .putString(KEY_LAST_TREND, reading.trend)
            .putString(KEY_LAST_SOURCE, sourceName)
            .putLong(KEY_LAST_SYNC_AT, System.currentTimeMillis())
            .putLong(KEY_LAST_READING_TIMESTAMP, reading.timestampEpochMs)
            .putString(KEY_LAST_ERROR, "")
            .putString(KEY_LAST_ERROR_CATEGORY, SyncErrorCategory.NONE.name)
            .putInt(KEY_AUTH_FAILURE_COUNT, 0)
            .putInt(KEY_CONSECUTIVE_FAILURE_COUNT, 0)
            .apply()
    }

    fun saveError(message: String, category: SyncErrorCategory) {
        val nextAuthFailureCount = if (category == SyncErrorCategory.AUTH) {
            prefs.getInt(KEY_AUTH_FAILURE_COUNT, 0) + 1
        } else {
            0
        }
        val nextFailureCount = prefs.getInt(KEY_CONSECUTIVE_FAILURE_COUNT, 0) + 1

        prefs.edit()
            .putLong(KEY_LAST_SYNC_AT, System.currentTimeMillis())
            .putString(KEY_LAST_ERROR, message)
            .putString(KEY_LAST_ERROR_CATEGORY, category.name)
            .putInt(KEY_AUTH_FAILURE_COUNT, nextAuthFailureCount)
            .putInt(KEY_CONSECUTIVE_FAILURE_COUNT, nextFailureCount)
            .apply()
    }

    fun clearSessionState() {
        prefs.edit()
            .remove(KEY_LAST_VALUE)
            .remove(KEY_LAST_TREND)
            .remove(KEY_LAST_SOURCE)
            .remove(KEY_LAST_SYNC_AT)
            .remove(KEY_LAST_READING_TIMESTAMP)
            .remove(KEY_LAST_ERROR)
            .remove(KEY_LAST_ERROR_CATEGORY)
            .remove(KEY_AUTH_FAILURE_COUNT)
            .remove(KEY_CONSECUTIVE_FAILURE_COUNT)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_sync_status"
        private const val KEY_LAST_VALUE = "last_value"
        private const val KEY_LAST_TREND = "last_trend"
        private const val KEY_LAST_SOURCE = "last_source"
        private const val KEY_LAST_SYNC_AT = "last_sync_at"
        private const val KEY_LAST_READING_TIMESTAMP = "last_reading_timestamp"
        private const val KEY_LAST_ERROR = "last_error"
        private const val KEY_LAST_ERROR_CATEGORY = "last_error_category"
        private const val KEY_AUTH_FAILURE_COUNT = "auth_failure_count"
        private const val KEY_CONSECUTIVE_FAILURE_COUNT = "consecutive_failure_count"
    }
}
