package com.glucoseforwatch.mobile.ui.compose

import androidx.compose.ui.graphics.Color
import com.glucoseforwatch.core.model.AgpGlucoseColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeDialGlucoseColorTest {
    @Test
    fun everyBand_usesTheAgpMedicalPalette() {
        assertEquals(Color(AgpGlucoseColors.VERY_LOW), HomeDialGlucoseColor.color(50))
        assertEquals(Color(AgpGlucoseColors.LOW), HomeDialGlucoseColor.color(60))
        assertEquals(Color(AgpGlucoseColors.IN_RANGE), HomeDialGlucoseColor.color(120))
        assertEquals(Color(AgpGlucoseColors.HIGH), HomeDialGlucoseColor.color(200))
        assertEquals(Color(AgpGlucoseColors.VERY_HIGH), HomeDialGlucoseColor.color(300))
    }

    @Test
    fun missingValue_hasNoMedicalColor() {
        assertNull(HomeDialGlucoseColor.color(null))
    }
}
