package com.glucoseforwatch.wear.display

import com.glucoseforwatch.core.model.AgpGlucoseColors
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WearGlucoseSurfaceModelFactoryTest {
    @Test
    fun stale_snapshot_uses_unknown_agp_colors() {
        val snapshot =
            GlucoseSnapshot(
                valueMgDl = 120,
                trend = "FLAT",
                deltaMgDl = 0,
                timestampEpochMs = System.currentTimeMillis(),
                stale = true,
            )

        val model = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)

        assertTrue(model.stale)
        assertEquals(AgpGlucoseColors.UNKNOWN, model.valueColorArgb)
        assertEquals(AgpGlucoseColors.UNKNOWN, model.trendColorArgb)
    }

    @Test
    fun in_range_reading_uses_green_agp_value_color() {
        val snapshot =
            GlucoseSnapshot(
                valueMgDl = 120,
                trend = "FLAT",
                deltaMgDl = 0,
                timestampEpochMs = System.currentTimeMillis(),
                stale = false,
            )

        val model = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)

        assertEquals("120", model.valueText)
        assertEquals("mg/dL", model.unitLabel)
        assertEquals(AgpGlucoseColors.IN_RANGE, model.valueColorArgb)
    }

    @Test
    fun mmol_unit_formats_value_text() {
        val snapshot =
            GlucoseSnapshot(
                valueMgDl = 120,
                trend = "FLAT",
                deltaMgDl = 0,
                timestampEpochMs = System.currentTimeMillis(),
                stale = false,
                displayUnit = com.glucoseforwatch.core.model.GlucoseDisplayUnit.MMOL_L,
            )

        val model = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)

        assertEquals("6.7", model.valueText)
        assertEquals("mmol/L", model.unitLabel)
    }

    @Test
    fun low_sentinel_displays_low_text() {
        val snapshot =
            GlucoseSnapshot(
                valueMgDl = 40,
                trend = "DOWN",
                deltaMgDl = -2,
                timestampEpochMs = System.currentTimeMillis(),
                stale = false,
            )

        val model = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)

        assertEquals("LOW", model.valueText)
        assertEquals(AgpGlucoseColors.VERY_LOW, model.valueColorArgb)
    }
}
