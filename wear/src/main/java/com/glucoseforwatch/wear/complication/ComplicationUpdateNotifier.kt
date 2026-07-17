package com.glucoseforwatch.wear.complication

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

@Keep
internal object ComplicationUpdateNotifier {
    private const val TAG = "WG7.Complication"
    private const val PREFS = "widget_g7_complication_push"
    private const val KEY_LAST_SEQUENCE = "last_push_sequence_id"
    private const val KEY_LAST_TIMESTAMP = "last_push_reading_ts"
    private const val KEY_LAST_PUSH_AT = "last_push_at_ms"
    /**
     * Wear OS docs: do not request updates more often than ~every 5 minutes on average,
     * or SysUI may ignore subsequent [ComplicationDataSourceUpdateRequester] calls.
     */
    private const val MIN_PUSH_INTERVAL_MS = 300_000L

    /**
     * Request complication refresh after a new glucose reading lands on the watch.
     *
     * When [force] is true, [MIN_PUSH_INTERVAL_MS] is ignored. Prefer force=false and rely
     * on [sequenceId] / [readingTimestampEpochMs] so Dexcom ~5 min cadence stays compliant.
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
        val component = ComponentName(context, GlucoseComplicationServiceV2::class.java)
        val requester = ComplicationDataSourceUpdateRequester.create(context, component)
        val instanceIds = ComplicationInstanceRegistry.activeInstanceIds(context)

        // One path only — never double-fire requestUpdate + requestUpdateAll (SysUI throttle).
        runCatching {
            if (instanceIds.isNotEmpty()) {
                requester.requestUpdate(*instanceIds)
                Log.w(TAG, "push requestUpdate instances=${instanceIds.toList()} component=${component.className}")
            } else {
                requester.requestUpdateAll()
                Log.w(
                    TAG,
                    "push requestUpdateAll (no local ids) component=${component.className} — " +
                        "re-select complication V2 on the watch face if HIT logs never appear",
                )
            }
        }.onFailure { Log.e(TAG, "complication push failed", it) }
    }
}
