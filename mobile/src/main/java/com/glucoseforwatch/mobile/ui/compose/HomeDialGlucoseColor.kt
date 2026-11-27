package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.ui.graphics.Color
import com.glucoseforwatch.core.model.GlucoseRangeResolver

/** Home dial border and value color: AGP medical palette only, via [GlucoseRangeResolver]. */
internal object HomeDialGlucoseColor {
    /** Null when there is no value; the caller falls back to a neutral chrome color. */
    fun color(valueMgDl: Int?): Color? =
        valueMgDl?.let { Color(GlucoseRangeResolver.resolveColor(it)) }
}

internal data class HomeDialGlucoseDisplay(
    val text: String,
    val valueMgDl: Int?,
)
