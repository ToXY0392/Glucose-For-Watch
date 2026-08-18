package com.glucoseforwatch.wear.tile

import androidx.wear.protolayout.DeviceParametersBuilders
import com.glucoseforwatch.core.model.AgpGlucoseColors
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GlucoseTileChromeTest {

    @Test
    fun layoutMetrics_usesSmallerTypographyOnCompactRoundScreens() {
        val compact = GlucoseTileChrome.layoutMetrics(screenWidthDp = 200, screenHeightDp = 200)
        val large = GlucoseTileChrome.layoutMetrics(screenWidthDp = 240, screenHeightDp = 240)

        assertTrue(compact.valueSp < large.valueSp)
        assertTrue(compact.horizontalPadDp >= large.horizontalPadDp)
    }

    @Test
    fun layoutMetrics_roundScreen_usesWiderInset() {
        val round =
            GlucoseTileChrome.layoutMetrics(
                screenWidthDp = 200,
                screenHeightDp = 200,
                screenShape = DeviceParametersBuilders.SCREEN_SHAPE_ROUND,
            )
        val rect =
            GlucoseTileChrome.layoutMetrics(
                screenWidthDp = 240,
                screenHeightDp = 240,
                screenShape = DeviceParametersBuilders.SCREEN_SHAPE_RECT,
            )

        assertEquals(26f, round.horizontalPadDp)
        assertTrue(round.horizontalPadDp > rect.horizontalPadDp)
    }

    @Test
    fun layoutMetrics_valueSlots_areFixedConstants() {
        val compact = GlucoseTileChrome.layoutMetrics(screenWidthDp = 200, screenHeightDp = 200)
        val large = GlucoseTileChrome.layoutMetrics(screenWidthDp = 240, screenHeightDp = 240)

        assertEquals(GlucoseTileChrome.VALUE_ROW_WIDTH_DP, compact.valueRowWidthDp)
        assertEquals(GlucoseTileChrome.VALUE_ROW_WIDTH_DP, large.valueRowWidthDp)
        assertEquals(GlucoseTileChrome.SYNC_BUTTON_WIDTH_DP, compact.syncButtonWidthDp)
        assertEquals(GlucoseTileChrome.SYNC_BUTTON_HEIGHT_DP, compact.syncButtonHeightDp)
    }

    @Test
    fun background_isAbsoluteBlack() {
        assertEquals(0xFF000000.toInt(), GlucoseTileChrome.BACKGROUND)
    }

    @Test
    fun isStale_usesFifteenMinuteRule() {
        val now = 1_000_000_000L
        assertFalse(GlucoseTileChrome.isStale(now - (14 * 60 * 1000L), now))
        assertTrue(GlucoseTileChrome.isStale(now - (15 * 60 * 1000L + 1L), now))
    }

    @Test
    fun presentation_stale_keepsStructure_fadesValue_showsAgeLabel() {
        val now = 2_000_000_000L
        val snapshot =
            GlucoseSnapshot(
                valueMgDl = 118,
                trend = "FLAT",
                deltaMgDl = 0,
                timestampEpochMs = now - (22 * 60 * 1000L),
                stale = false,
            )
        val presentation =
            GlucoseTileChrome.presentation(
                snapshot = snapshot,
                syncLocked = false,
                staleStatusLabel = "Donnée périmée",
                syncActionLabel = "sync",
                syncInProgressLabel = "sync…",
                nowEpochMs = now,
            )

        assertTrue(presentation.stale)
        assertEquals(snapshot.displayValueText(), presentation.valueText)
        assertEquals("\u2192", presentation.trendText)
        assertEquals(
            GlucoseTileChrome.withOpacity(AgpGlucoseColors.IN_RANGE, GlucoseTileChrome.STALE_OPACITY),
            presentation.valueArgb,
        )
        assertEquals("Il y a 22 min", presentation.statusText)
    }

    @Test
    fun presentation_fresh_usesMedicalGreenForInRange() {
        val now = 2_000_000_000L
        val snapshot =
            GlucoseSnapshot(
                valueMgDl = 120,
                trend = "FLAT",
                deltaMgDl = 0,
                timestampEpochMs = now - 60_000L,
                stale = false,
            )
        val presentation =
            GlucoseTileChrome.presentation(
                snapshot = snapshot,
                syncLocked = false,
                staleStatusLabel = "Donnée périmée",
                syncActionLabel = "sync",
                syncInProgressLabel = "sync…",
                nowEpochMs = now,
            )

        assertFalse(presentation.stale)
        assertEquals(AgpGlucoseColors.IN_RANGE, presentation.valueArgb)
        assertEquals("\u2192", presentation.trendText)
        assertEquals("sync", presentation.statusText)
    }

    @Test
    fun presentation_missing_usesNeutralPlaceholders() {
        val presentation =
            GlucoseTileChrome.presentation(
                snapshot = null,
                syncLocked = false,
                staleStatusLabel = "Donnée périmée",
                syncActionLabel = "sync",
                syncInProgressLabel = "sync…",
            )

        assertEquals("---", presentation.valueText)
        assertEquals("", presentation.trendText)
        assertEquals("sync", presentation.statusText)
    }

    @Test
    fun medicalColor_mapsBands() {
        assertEquals(AgpGlucoseColors.LOW, GlucoseTileChrome.medicalColorArgb(55))
        assertEquals(AgpGlucoseColors.IN_RANGE, GlucoseTileChrome.medicalColorArgb(110))
        assertEquals(GlucoseTileChrome.ALERT_ORANGE, GlucoseTileChrome.medicalColorArgb(210))
        assertEquals(GlucoseTileChrome.ALERT_ORANGE, GlucoseTileChrome.medicalColorArgb(300))
    }
}
