package com.glucoseforwatch.mobile.sync

import android.content.Context

/** Persisted phone sync pipeline state (fetch, push, ack). */
data class PhoneSyncStateSnapshot(
    val lastFetchedReadingTimestampEpochMs: Long,
    val lastPushedReadingTimestampEpochMs: Long,
    val lastPushSequenceId: Long,
    val lastFetchAttemptEpochMs: Long,
    val lastPushSuccessEpochMs: Long,
    val lastPushFailureEpochMs: Long,
    val lastPushFailureMessage: String,
    val lastAckReadingTimestampEpochMs: Long,
    val lastAckSequenceId: Long,
    val lastAckEpochMs: Long,
    val lastAckNodeId: String,
    val lastPushedValueMgDl: Int?,
    val lastPushedTrend: String,
    val lastPushedDeltaMgDl: Int,
    val lastPushedStale: Boolean,
    val unackedRepushCount: Int,
    val consecutiveWearPushFailures: Int,
    val activeServiceState: String,
    val activeServiceUpdatedAtEpochMs: Long,
)

/** SharedPreferences store for phone-side sync telemetry. */
class PhoneSyncStateStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): PhoneSyncStateSnapshot =
        PhoneSyncStateSnapshot(
            lastFetchedReadingTimestampEpochMs = prefs.getLong(KEY_LAST_FETCHED_READING_TIMESTAMP, 0L),
            lastPushedReadingTimestampEpochMs = prefs.getLong(KEY_LAST_PUSHED_READING_TIMESTAMP, 0L),
            lastPushSequenceId = prefs.getLong(KEY_LAST_PUSH_SEQUENCE_ID, 0L),
            lastFetchAttemptEpochMs = prefs.getLong(KEY_LAST_FETCH_ATTEMPT, 0L),
            lastPushSuccessEpochMs = prefs.getLong(KEY_LAST_PUSH_SUCCESS, 0L),
            lastPushFailureEpochMs = prefs.getLong(KEY_LAST_PUSH_FAILURE, 0L),
            lastPushFailureMessage = prefs.getString(KEY_LAST_PUSH_FAILURE_MESSAGE, "").orEmpty(),
            lastAckReadingTimestampEpochMs = prefs.getLong(KEY_LAST_ACK_READING_TIMESTAMP, 0L),
            lastAckSequenceId = prefs.getLong(KEY_LAST_ACK_SEQUENCE_ID, 0L),
            lastAckEpochMs = prefs.getLong(KEY_LAST_ACK_AT, 0L),
            lastAckNodeId = prefs.getString(KEY_LAST_ACK_NODE_ID, "").orEmpty(),
            lastPushedValueMgDl = if (prefs.contains(KEY_LAST_PUSHED_VALUE)) {
                prefs.getInt(KEY_LAST_PUSHED_VALUE, 0)
            } else {
                null
            },
            lastPushedTrend = prefs.getString(KEY_LAST_PUSHED_TREND, "").orEmpty(),
            lastPushedDeltaMgDl = prefs.getInt(KEY_LAST_PUSHED_DELTA, 0),
            lastPushedStale = prefs.getBoolean(KEY_LAST_PUSHED_STALE, true),
            unackedRepushCount = prefs.getInt(KEY_UNACKED_REPUSH_COUNT, 0),
            consecutiveWearPushFailures = prefs.getInt(KEY_CONSECUTIVE_WEAR_PUSH_FAILURES, 0),
            activeServiceState = prefs.getString(KEY_ACTIVE_SERVICE_STATE, "stopped").orEmpty(),
            activeServiceUpdatedAtEpochMs = prefs.getLong(KEY_ACTIVE_SERVICE_UPDATED_AT, 0L),
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

    /** Uses wall-clock millis as the push sequence id. */
    fun nextSequenceId(nowEpochMs: Long = System.currentTimeMillis()): Long = nowEpochMs

    fun recordPushSuccess(
        timestampEpochMs: Long,
        sequenceId: Long,
        valueMgDl: Int? = null,
        trend: String = "",
        deltaMgDl: Int = 0,
        stale: Boolean = true,
        nowEpochMs: Long = System.currentTimeMillis(),
    ) {
        val editor = prefs.edit()
            .putLong(KEY_LAST_PUSHED_READING_TIMESTAMP, timestampEpochMs)
            .putLong(KEY_LAST_PUSH_SEQUENCE_ID, sequenceId)
            .putLong(KEY_LAST_PUSH_SUCCESS, nowEpochMs)
            .putLong(KEY_LAST_PUSH_FAILURE, 0L)
            .putString(KEY_LAST_PUSH_FAILURE_MESSAGE, "")
        if (valueMgDl != null) {
            editor
                .putInt(KEY_LAST_PUSHED_VALUE, valueMgDl)
                .putString(KEY_LAST_PUSHED_TREND, trend)
                .putInt(KEY_LAST_PUSHED_DELTA, deltaMgDl)
                .putBoolean(KEY_LAST_PUSHED_STALE, stale)
        }
        editor.apply()
        recordWearPushDelivered()
    }

    fun recordWearPushDelivered() {
        prefs.edit()
            .putInt(KEY_CONSECUTIVE_WEAR_PUSH_FAILURES, 0)
            .apply()
    }

    fun recordWearPushUndelivered() {
        val next = prefs.getInt(KEY_CONSECUTIVE_WEAR_PUSH_FAILURES, 0) + 1
        prefs.edit()
            .putInt(KEY_CONSECUTIVE_WEAR_PUSH_FAILURES, next)
            .apply()
    }

    fun recordWatchAck(
        readingTimestampEpochMs: Long,
        sequenceId: Long,
        nodeId: String,
        nowEpochMs: Long = System.currentTimeMillis(),
    ) {
        prefs.edit()
            .putLong(KEY_LAST_ACK_READING_TIMESTAMP, readingTimestampEpochMs)
            .putLong(KEY_LAST_ACK_SEQUENCE_ID, sequenceId)
            .putLong(KEY_LAST_ACK_AT, nowEpochMs)
            .putString(KEY_LAST_ACK_NODE_ID, nodeId)
            .putInt(KEY_UNACKED_REPUSH_COUNT, 0)
            .putInt(KEY_CONSECUTIVE_WEAR_PUSH_FAILURES, 0)
            .apply()
    }

    fun recordRepushAttempt(count: Int) {
        prefs.edit()
            .putInt(KEY_UNACKED_REPUSH_COUNT, count)
            .apply()
    }

    fun recordActiveServiceState(state: String, nowEpochMs: Long = System.currentTimeMillis()) {
        prefs.edit()
            .putString(KEY_ACTIVE_SERVICE_STATE, state)
            .putLong(KEY_ACTIVE_SERVICE_UPDATED_AT, nowEpochMs)
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
            .remove(KEY_LAST_PUSH_SEQUENCE_ID)
            .remove(KEY_LAST_FETCH_ATTEMPT)
            .remove(KEY_LAST_PUSH_SUCCESS)
            .remove(KEY_LAST_PUSH_FAILURE)
            .remove(KEY_LAST_PUSH_FAILURE_MESSAGE)
            .remove(KEY_LAST_ACK_READING_TIMESTAMP)
            .remove(KEY_LAST_ACK_SEQUENCE_ID)
            .remove(KEY_LAST_ACK_AT)
            .remove(KEY_LAST_ACK_NODE_ID)
            .remove(KEY_LAST_PUSHED_VALUE)
            .remove(KEY_LAST_PUSHED_TREND)
            .remove(KEY_LAST_PUSHED_DELTA)
            .remove(KEY_LAST_PUSHED_STALE)
            .remove(KEY_UNACKED_REPUSH_COUNT)
            .remove(KEY_CONSECUTIVE_WEAR_PUSH_FAILURES)
            .remove(KEY_ACTIVE_SERVICE_STATE)
            .remove(KEY_ACTIVE_SERVICE_UPDATED_AT)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "gfw_phone_sync_state"
        private const val KEY_LAST_FETCHED_READING_TIMESTAMP = "last_fetched_reading_timestamp"
        private const val KEY_LAST_PUSHED_READING_TIMESTAMP = "last_pushed_reading_timestamp"
        private const val KEY_LAST_PUSH_SEQUENCE_ID = "last_push_sequence_id"
        private const val KEY_LAST_FETCH_ATTEMPT = "last_fetch_attempt"
        private const val KEY_LAST_PUSH_SUCCESS = "last_push_success"
        private const val KEY_LAST_PUSH_FAILURE = "last_push_failure"
        private const val KEY_LAST_PUSH_FAILURE_MESSAGE = "last_push_failure_message"
        private const val KEY_LAST_ACK_READING_TIMESTAMP = "last_ack_reading_timestamp"
        private const val KEY_LAST_ACK_SEQUENCE_ID = "last_ack_sequence_id"
        private const val KEY_LAST_ACK_AT = "last_ack_at"
        private const val KEY_LAST_ACK_NODE_ID = "last_ack_node_id"
        private const val KEY_LAST_PUSHED_VALUE = "last_pushed_value"
        private const val KEY_LAST_PUSHED_TREND = "last_pushed_trend"
        private const val KEY_LAST_PUSHED_DELTA = "last_pushed_delta"
        private const val KEY_LAST_PUSHED_STALE = "last_pushed_stale"
        private const val KEY_UNACKED_REPUSH_COUNT = "unacked_repush_count"
        private const val KEY_CONSECUTIVE_WEAR_PUSH_FAILURES = "consecutive_wear_push_failures"
        private const val KEY_ACTIVE_SERVICE_STATE = "active_service_state"
        private const val KEY_ACTIVE_SERVICE_UPDATED_AT = "active_service_updated_at"
    }
}
