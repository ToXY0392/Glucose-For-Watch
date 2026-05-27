package com.glucoseforwatch.wear.display

import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import com.glucoseforwatch.wear.tile.ToxyTileTheme

/** Shared glucose display state for tile and complication (AGP + stale rules). */
data class WearGlucoseSurfaceModel(
    val valueText: String,
    val trendArrow: String,
    val showTrend: Boolean,
    val valueColorArgb: Int,
    val trendColorArgb: Int,
    val stale: Boolean,
    val valueMgDl: Int?,
    val unitLabel: String,
)

/** Maps a cached [GlucoseSnapshot] into [WearGlucoseSurfaceModel] for tile and complication. */
object WearGlucoseSurfaceModelFactory {
    fun fromSnapshot(snapshot: GlucoseSnapshot?): WearGlucoseSurfaceModel {
        val trendArrow = snapshot?.trendArrow().orEmpty()
        val showTrend = snapshot != null && trendArrow.isNotEmpty()
        return WearGlucoseSurfaceModel(
            valueText = snapshot?.displayValueText() ?: "--",
            trendArrow = trendArrow,
            showTrend = showTrend,
            valueColorArgb = ToxyTileTheme.valueColorArgb(snapshot),
            trendColorArgb = ToxyTileTheme.trendColorArgb(snapshot),
            stale = snapshot?.stale == true,
            valueMgDl = snapshot?.valueMgDl,
            unitLabel = snapshot?.unitLabel() ?: GlucoseDisplayUnit.MG_DL.label(),
        )
    }
}
