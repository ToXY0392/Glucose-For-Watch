package com.glucoseforwatch.wear.display

import androidx.wear.watchface.complications.data.ComplicationType
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.core.model.GlucoseUnitFormatter
import com.glucoseforwatch.wear.complication.GlucoseComplicationDataFactory
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import org.junit.Assert.assertEquals
import org.junit.Test

class GlucoseComplicationDataFactoryTest {

    @Test
    fun buildsShortTextFromSnapshot() {
        val snapshot =
            GlucoseSnapshot(
                valueMgDl = 166,
                trend = "DOWN",
                deltaMgDl = -5,
                timestampEpochMs = System.currentTimeMillis(),
                stale = false,
            )
        val payload = GlucoseComplicationDataFactory.fromSnapshot(snapshot)

        assertEquals("166", payload.display.valueText)
        assertEquals(166, payload.valueMgDl)
        assertEquals(GlucoseDisplayUnit.MG_DL, payload.displayUnit)
    }

    @Test
    fun rangedValue_uses_mmol_scale_when_display_unit_is_mmol() {
        val snapshot =
            GlucoseSnapshot(
                valueMgDl = 120,
                trend = "FLAT",
                deltaMgDl = 0,
                timestampEpochMs = System.currentTimeMillis(),
                stale = false,
                displayUnit = GlucoseDisplayUnit.MMOL_L,
            )
        val payload = GlucoseComplicationDataFactory.fromSnapshot(snapshot)
        val data =
            GlucoseComplicationDataFactory.buildData(
                type = ComplicationType.RANGED_VALUE,
                payload = payload,
                tapAction = null,
            )

        assertEquals("6.7", payload.display.valueText)
        assertEquals(GlucoseDisplayUnit.MMOL_L, payload.displayUnit)
        val ranged = data as androidx.wear.watchface.complications.data.RangedValueComplicationData
        assertEquals(
            GlucoseUnitFormatter.toRangedDisplayValue(120, GlucoseDisplayUnit.MMOL_L),
            ranged.value,
            0.001f,
        )
        assertEquals(GlucoseUnitFormatter.rangedMin(GlucoseDisplayUnit.MMOL_L), ranged.min, 0.001f)
        assertEquals(GlucoseUnitFormatter.rangedMax(GlucoseDisplayUnit.MMOL_L), ranged.max, 0.001f)
    }
}
