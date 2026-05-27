package com.glucoseforwatch.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class GlucoseUnitFormatterTest {
    @Test
    fun format_value_mg_dl_handles_sentinels_and_integers() {
        assertEquals("LOW", GlucoseUnitFormatter.formatValue(40, GlucoseDisplayUnit.MG_DL))
        assertEquals("HI", GlucoseUnitFormatter.formatValue(400, GlucoseDisplayUnit.MG_DL))
        assertEquals("123", GlucoseUnitFormatter.formatValue(123, GlucoseDisplayUnit.MG_DL))
    }

    @Test
    fun format_value_mmol_converts_with_one_decimal() {
        assertEquals("LOW", GlucoseUnitFormatter.formatValue(40, GlucoseDisplayUnit.MMOL_L))
        assertEquals("HI", GlucoseUnitFormatter.formatValue(400, GlucoseDisplayUnit.MMOL_L))
        assertEquals("6.7", GlucoseUnitFormatter.formatValue(120, GlucoseDisplayUnit.MMOL_L))
    }

    @Test
    fun format_with_unit_appends_label() {
        assertEquals("120 mg/dL", GlucoseUnitFormatter.formatWithUnit(120, GlucoseDisplayUnit.MG_DL))
        assertEquals("6.7 mmol/L", GlucoseUnitFormatter.formatWithUnit(120, GlucoseDisplayUnit.MMOL_L))
    }

    @Test
    fun from_storage_parses_mmol_alias() {
        assertEquals(GlucoseDisplayUnit.MMOL_L, GlucoseDisplayUnit.fromStorage("MMOL_L"))
        assertEquals(GlucoseDisplayUnit.MMOL_L, GlucoseDisplayUnit.fromStorage("mmol/L"))
        assertEquals(GlucoseDisplayUnit.MG_DL, GlucoseDisplayUnit.fromStorage(null))
    }

    @Test
    fun ranged_scale_uses_display_unit() {
        assertEquals(40f, GlucoseUnitFormatter.rangedMin(GlucoseDisplayUnit.MG_DL))
        assertEquals(400f, GlucoseUnitFormatter.rangedMax(GlucoseDisplayUnit.MG_DL))
        assertEquals(6.659f, GlucoseUnitFormatter.toRangedDisplayValue(120, GlucoseDisplayUnit.MMOL_L), 0.001f)
        assertEquals(2.220f, GlucoseUnitFormatter.rangedMin(GlucoseDisplayUnit.MMOL_L), 0.001f)
        assertEquals(22.199f, GlucoseUnitFormatter.rangedMax(GlucoseDisplayUnit.MMOL_L), 0.001f)
    }
}
