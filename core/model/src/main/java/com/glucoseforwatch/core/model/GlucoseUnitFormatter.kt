package com.glucoseforwatch.core.model

import java.util.Locale

/** Formats mg/dL readings for display in mg/dL or mmol/L (Dexcom Share stays mg/dL internally). */
object GlucoseUnitFormatter {
    const val DISPLAY_LOW_MAX_MG_DL = 40
    const val DISPLAY_HIGH_MIN_MG_DL = 400

    /** mg/dL per mmol/L (ISO 15197 / clinical CGM conversion). */
    private const val MG_DL_PER_MMOL = 18.0182

    fun formatValue(valueMgDl: Int, unit: GlucoseDisplayUnit): String =
        when {
            valueMgDl <= DISPLAY_LOW_MAX_MG_DL -> "LOW"
            valueMgDl >= DISPLAY_HIGH_MIN_MG_DL -> "HI"
            unit == GlucoseDisplayUnit.MG_DL -> valueMgDl.toString()
            else -> formatMmol(valueMgDl)
        }

    fun unitLabel(unit: GlucoseDisplayUnit): String = unit.label()

    fun formatWithUnit(valueMgDl: Int, unit: GlucoseDisplayUnit): String =
        "${formatValue(valueMgDl, unit)} ${unitLabel(unit)}"

    /** Min/max for [androidx.wear.watchface.complications.data.RangedValueComplicationData] in display units. */
    fun rangedMin(unit: GlucoseDisplayUnit): Float =
        when (unit) {
            GlucoseDisplayUnit.MG_DL -> DISPLAY_LOW_MAX_MG_DL.toFloat()
            GlucoseDisplayUnit.MMOL_L -> (DISPLAY_LOW_MAX_MG_DL / MG_DL_PER_MMOL).toFloat()
        }

    fun rangedMax(unit: GlucoseDisplayUnit): Float =
        when (unit) {
            GlucoseDisplayUnit.MG_DL -> DISPLAY_HIGH_MIN_MG_DL.toFloat()
            GlucoseDisplayUnit.MMOL_L -> (DISPLAY_HIGH_MIN_MG_DL / MG_DL_PER_MMOL).toFloat()
        }

    fun toRangedDisplayValue(valueMgDl: Int, unit: GlucoseDisplayUnit): Float =
        when (unit) {
            GlucoseDisplayUnit.MG_DL -> valueMgDl.toFloat()
            GlucoseDisplayUnit.MMOL_L -> (valueMgDl / MG_DL_PER_MMOL).toFloat()
        }

    /** Neutral placeholder when reading is stale (120 mg/dL equivalent). */
    fun rangedUnknownPlaceholder(unit: GlucoseDisplayUnit): Float = toRangedDisplayValue(120, unit)

    private fun formatMmol(valueMgDl: Int): String {
        val mmol = valueMgDl / MG_DL_PER_MMOL
        return String.format(Locale.US, "%.1f", mmol)
    }
}
