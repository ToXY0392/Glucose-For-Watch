package com.glucoseforwatch.wear.tile

import androidx.wear.protolayout.DeviceParametersBuilders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToxyTileThemeTest {

    @Test
    fun layoutMetrics_usesSmallerTypographyOnCompactRoundScreens() {
        val compact = ToxyTileTheme.layoutMetrics(screenWidthDp = 200, screenHeightDp = 200)
        val large = ToxyTileTheme.layoutMetrics(screenWidthDp = 240, screenHeightDp = 240)

        assertTrue(compact.valueSp < large.valueSp)
        assertTrue(compact.horizontalPadDp >= large.horizontalPadDp)
    }

    @Test
    fun layoutMetrics_roundScreen_usesWiderInset() {
        val round =
            ToxyTileTheme.layoutMetrics(
                screenWidthDp = 200,
                screenHeightDp = 200,
                screenShape = DeviceParametersBuilders.SCREEN_SHAPE_ROUND,
            )
        val rect =
            ToxyTileTheme.layoutMetrics(
                screenWidthDp = 240,
                screenHeightDp = 240,
                screenShape = DeviceParametersBuilders.SCREEN_SHAPE_RECT,
            )

        assertEquals(26f, round.horizontalPadDp)
        assertTrue(round.horizontalPadDp > rect.horizontalPadDp)
    }

    @Test
    fun layoutMetrics_syncAndValueSlots_areFixedConstants() {
        val compact = ToxyTileTheme.layoutMetrics(screenWidthDp = 200, screenHeightDp = 200)
        val large = ToxyTileTheme.layoutMetrics(screenWidthDp = 240, screenHeightDp = 240)

        assertEquals(ToxyTileTheme.SYNC_BUTTON_WIDTH_DP, compact.syncButtonWidthDp)
        assertEquals(ToxyTileTheme.SYNC_BUTTON_WIDTH_DP, large.syncButtonWidthDp)
        assertEquals(ToxyTileTheme.SYNC_BUTTON_HEIGHT_DP, compact.syncButtonHeightDp)
        assertEquals(ToxyTileTheme.SYNC_BUTTON_HEIGHT_DP, large.syncButtonHeightDp)
        assertEquals(ToxyTileTheme.VALUE_ROW_WIDTH_DP, compact.valueRowWidthDp)
        assertEquals(ToxyTileTheme.VALUE_ROW_WIDTH_DP, large.valueRowWidthDp)
    }

    @Test
    fun valueSpForText_shrinksThreeDigitReadings() {
        assertEquals(
            34f,
            ToxyTileTheme.valueSpForText(baseSp = 38f, valueText = "120"),
        )
        assertEquals(
            38f,
            ToxyTileTheme.valueSpForText(baseSp = 38f, valueText = "HI"),
        )
    }
}
