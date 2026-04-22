package com.widgetg7.mobile.sync

import android.content.Context

data class PhoneSyncStateSnapshot(
    val lastFetchedReadingTimestampEpochMs: Long,
    val lastPushedReadingTimestampEpochMs: Long,
    val lastFetchAttemptEpochMs: Long,
    val lastPushSuccessEpochMs: Long,
    val lastPushFailureEpochMs: Long,
    val lastPushFailureMessage: String,
)

class PhoneSyncStateStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): PhoneSyncStateSnapshot =
        PhoneSyncStateSnapshot(
            lastFetchedReadingTimestampEpochMs = prefs.getLong(KEY_LAST_FETCHED_READING_TIMESTAMP, 0L),
            lastPushedReadingTimestampEpochMs = prefs.getLong(KEY_LAST_PUSHED_READING_TIMESTAMP, 0L),
            lastFetchAttemptEpochMs = prefs.getLong(KEY_LAST_FETCH_ATTEMPT, 0L),
            lastPushSuccessEpochMs = prefs.getLong(KEY_LAST_PUSH_SUCCESS, 0L),
            lastPushFailureEpochMs = prefs.getLong(KEY_LAST_PUSH_FAILURE, 0L),
            lastPushFailureMessage = prefs.getString(KEY_LAST_PUSH_FAILURE_MESSAGE, "").orEmpty(),
        )

    fun recordFetchAttempt(nowEpochMs: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putLong(KEY_LAST_FETCH_ATTEMPT, nowEpochMs)
            .apply()
    }

    fun recordFetchedReading(timestampEpochMs: Long) {
        prefs.edit()
            .putLong(KEY_LAST_FETCHED_READING_TIMESTAMP, timestampEpochMs)
            .apply()
    }

    fun recordPushSuccess(timestampEpochMs: Long, nowEpochMs: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putLong(KEY_LAST_PUSHED_READING_TIMESTAMP, timestampEpochMs)
            .putLong(KEY_LAST_PUSH_SUCCESS, nowEpochMs)
            .putLong(KEY_LAST_PUSH_FAILURE, 0L)
            .putString(KEY_LAST_PUSH_FAILURE_MESSAGE, "")
            .apply()
    }

    fun recordPushFailure(message: String, nowEpochMs: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putLong(KEY_LAST_PUSH_FAILURE, nowEpochMs)
            .putString(KEY_LAST_PUSH_FAILURE_MESSAGE, message)
            .apply()
    }

    fun clear() {
        prefs.edit()
            .remove(KEY_LAST_FETCHED_READING_TIMESTAMP)
            .remove(KEY_LAST_PUSHED_READING_TIMESTAMP)
            .remove(KEY_LAST_FETCH_ATTEMPT)
            .remove(KEY_LAST_PUSH_SUCCESS)
            .remove(KEY_LAST_PUSH_FAILURE)
            .remove(KEY_LAST_PUSH_FAILURE_MESSAGE)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_phone_sync_state"
        private const val KEY_LAST_FETCHED_READING_TIMESTAMP = "last_fetched_reading_timestamp"
        private const val KEY_LAST_PUSHED_READING_TIMESTAMP = "last_pushed_reading_timestamp"
        private const val KEY_LAST_FETCH_ATTEMPT = "last_fetch_attempt"
        private const val KEY_LAST_PUSH_SUCCESS = "last_push_success"
        private const val KEY_LAST_PUSH_FAILURE = "last_push_failure"
        private const val KEY_LAST_PUSH_FAILURE_MESSAGE = "last_push_failure_message"
    }
}
