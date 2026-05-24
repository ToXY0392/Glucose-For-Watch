package com.widgetg7.wear.complication

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

internal object ComplicationUpdateNotifier {
    private const val TAG = "WG7.Complication"
    private const val PREFS = "widget_g7_complication_push"
    private const val KEY_LAST_SEQUENCE = "last_push_sequence_id"
    private const val KEY_LAST_TIMESTAMP = "last_push_reading_ts"
    private const val KEY_LAST_PUSH_AT = "last_push_at_ms"
    /** Wear OS throttles push updates; avoid spamming requestUpdateAll. */
    private const val MIN_PUSH_INTERVAL_MS = 45_000L

    /**
     * Request complication refresh after a new glucose reading lands on the watch.
     * Always pushes when [sequenceId] or [readingTimestampEpochMs] changes; otherwise respects
     * [MIN_PUSH_INTERVAL_MS]. Use [force] for screen-on / user-visible wake paths.
     */
    fun notifyReadingChanged(
        context: Context,
        sequenceId: Long = 0L,
        readingTimestampEpochMs: Long = 0L,
        force: Boolean = false,
    ) {
        val appContext = context.applicationContext
        if (!force && !shouldPush(appContext, sequenceId, readingTimestampEpochMs)) {
            Log.d(TAG, "push skipped (rate limit) seq=$sequenceId ts=$readingTimestampEpochMs")
            return
        }
        recordPush(appContext, sequenceId, readingTimestampEpochMs)
        pushUpdates(appContext)
    }

    /** @deprecated Prefer [notifyReadingChanged]. Kept for wake / resume paths. */
    fun requestUpdateAll(context: Context) {
        notifyReadingChanged(context, force = true)
    }

    private fun shouldPush(
        context: Context,
        sequenceId: Long,
        readingTimestampEpochMs: Long,
    ): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val lastSeq = prefs.getLong(KEY_LAST_SEQUENCE, -1L)
        val lastTs = prefs.getLong(KEY_LAST_TIMESTAMP, -1L)
        val lastAt = prefs.getLong(KEY_LAST_PUSH_AT, 0L)
        val now = System.currentTimeMillis()

        if (sequenceId > 0L && sequenceId != lastSeq) return true
        if (readingTimestampEpochMs > 0L && readingTimestampEpochMs != lastTs) return true
        return now - lastAt >= MIN_PUSH_INTERVAL_MS
    }

    private fun recordPush(context: Context, sequenceId: Long, readingTimestampEpochMs: Long) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .apply {
                if (sequenceId > 0L) putLong(KEY_LAST_SEQUENCE, sequenceId)
                if (readingTimestampEpochMs > 0L) putLong(KEY_LAST_TIMESTAMP, readingTimestampEpochMs)
                putLong(KEY_LAST_PUSH_AT, System.currentTimeMillis())
            }
            .apply()
    }

    private fun pushUpdates(context: Context) {
        runCatching {
            val requester =
                ComplicationDataSourceUpdateRequester.create(
                    context,
                    ComponentName(context, GlucoseComplicationService::class.java),
                )
            val instanceIds = ComplicationInstanceRegistry.activeInstanceIds(context)
            if (instanceIds.isNotEmpty()) {
                requester.requestUpdate(*instanceIds)
                Log.i(TAG, "requestUpdate instances=${instanceIds.toList()}")
            } else {
                requester.requestUpdateAll()
                Log.i(TAG, "requestUpdateAll (no registered instances)")
            }
        }.onFailure { Log.w(TAG, "complication push failed", it) }
    }
}
