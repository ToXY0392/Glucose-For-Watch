package com.widgetg7.wear.tile

import com.widgetg7.core.model.AgpGlucoseColors
import com.widgetg7.core.model.GlucoseRangeResolver
import com.widgetg7.wear.data.GlucoseSnapshot

/**
 * ToXY chrome tokens and AGP glucose coloring for the Wear tile.
 *
 * Chrome colors mirror [toxy-ux-kit/tokens/toxy.color.json].
 * Glucose value/trend colors use [GlucoseRangeResolver] — never mint accent.
 */
object ToxyTileTheme {
    const val RESOURCES_VERSION = "simple-tile-v7-toxy-theme"
    const val FRESHNESS_INTERVAL_MS = 45_000L
    const val COMPLICATION_REFRESH_INTERVAL_MS = 45_000L
    const val SYNC_CLICK_ID = "sync"

    /** Tile canvas background (`toxy.color.background.top`). */
    const val BACKGROUND = 0xFF0D1117.toInt()

    /** Secondary label chrome (`toxy.color.text.secondary`). */
    const val UNIT_TEXT = 0xFF94A3B8.toInt()

    /** Sync action label (`toxy.color.accent.default`). */
    const val SYNC_ACCENT = 0xFF34D399.toInt()

    /** Sync button container — tonal accent with alpha. */
    const val SYNC_BG = 0x331634D3

    /** AGP color for the primary glucose value. */
    fun valueColorArgb(snapshot: GlucoseSnapshot?): Int =
        if (snapshot == null) {
            AgpGlucoseColors.UNKNOWN
        } else {
            GlucoseRangeResolver.resolveColorForReading(snapshot.valueMgDl, snapshot.stale)
        }

    /** AGP color for trend arrow; muted when reading is stale. */
    fun trendColorArgb(snapshot: GlucoseSnapshot?): Int =
        when {
            snapshot == null || snapshot.stale -> AgpGlucoseColors.UNKNOWN
            else -> GlucoseRangeResolver.resolveColor(snapshot.valueMgDl)
        }
}
