package com.glucoseforwatch.wear.tile

import androidx.annotation.Keep
import com.glucoseforwatch.core.model.AgpGlucoseColors
import com.glucoseforwatch.core.model.GlucoseRangeResolver
import com.glucoseforwatch.wear.data.GlucoseSnapshot

/**
 * Medical colors for the shared Wear surface model (complication and watch app).
 *
 * The ProtoLayout tile is styled by [GlucoseTileChrome]. Values come only from
 * [AgpGlucoseColors] / [GlucoseRangeResolver], never from theme primary or blue chrome.
 */
@Keep
object ToxyTileTheme {
    /** Glycemic value color: AGP only, never theme primary. */
    fun valueColorArgb(snapshot: GlucoseSnapshot?): Int =
        if (snapshot == null) {
            AgpGlucoseColors.UNKNOWN
        } else {
            GlucoseRangeResolver.resolveColorForReading(snapshot.valueMgDl, snapshot.stale)
        }

    fun trendColorArgb(snapshot: GlucoseSnapshot?): Int =
        when {
            snapshot == null || snapshot.stale -> AgpGlucoseColors.UNKNOWN
            else -> GlucoseRangeResolver.resolveColor(snapshot.valueMgDl)
        }
}
