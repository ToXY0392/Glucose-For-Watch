package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.ui.graphics.Color

/** Home dial glucose band for border and value coloring. */
sealed class HomeDialGlucoseBand(val color: Color) {
    data object Low : HomeDialGlucoseBand(Color(0xFFFF5252))

    data object InRange : HomeDialGlucoseBand(Color(0xFF4CAF50))

    data object High : HomeDialGlucoseBand(Color(0xFFFFC107))

    data object Unknown : HomeDialGlucoseBand(Color.Unspecified)
}

/** Resolves dial colors from mg/dL thresholds (< 70, 70–180, > 180). */
object HomeDialGlucoseColor {
    fun band(valueMgDl: Int?): HomeDialGlucoseBand =
        when {
            valueMgDl == null -> HomeDialGlucoseBand.Unknown
            valueMgDl < 70 -> HomeDialGlucoseBand.Low
            valueMgDl <= 180 -> HomeDialGlucoseBand.InRange
            else -> HomeDialGlucoseBand.High
        }
}

internal data class HomeDialGlucoseDisplay(
    val text: String,
    val valueMgDl: Int?,
)