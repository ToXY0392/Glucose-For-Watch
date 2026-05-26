package com.widgetg7.wear.display

import com.widgetg7.wear.complication.GlucoseComplicationDataFactory
import com.widgetg7.wear.data.GlucoseSnapshot
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
    }
}
