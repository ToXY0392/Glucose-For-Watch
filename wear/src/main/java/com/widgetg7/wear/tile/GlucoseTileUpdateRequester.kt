package com.widgetg7.wear.tile

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.wear.tiles.TileService

/** Coalesces tile refresh requests to avoid layout flicker when sync fires several updates at once. */
internal object GlucoseTileUpdateRequester {
    private const val DEBOUNCE_MS = 900L
    private val handler = Handler(Looper.getMainLooper())
    private var pending: Runnable? = null
    private var lastScheduledAt = 0L

    fun requestUpdate(context: Context) {
        schedule(context, debounceMs = DEBOUNCE_MS)
    }

    /** Bypass debounce when sync lock state changes (button enabled/disabled). */
    fun requestUpdateImmediate(context: Context) {
        schedule(context, debounceMs = 0L)
    }

    private fun schedule(context: Context, debounceMs: Long) {
        val appContext = context.applicationContext
        pending?.let { handler.removeCallbacks(it) }
        val now = SystemClock.elapsedRealtime()
        val delay =
            if (debounceMs == 0L) {
                0L
            } else if (lastScheduledAt == 0L) {
                0L
            } else {
                (debounceMs - (now - lastScheduledAt)).coerceAtLeast(0L)
            }
        lastScheduledAt = if (delay == 0L) now else now + delay
        pending =
            Runnable {
                lastScheduledAt = 0L
                pending = null
                TileService.getUpdater(appContext).requestUpdate(GlucoseSimpleTileService::class.java)
            }.also { handler.postDelayed(it, delay) }
    }
}
