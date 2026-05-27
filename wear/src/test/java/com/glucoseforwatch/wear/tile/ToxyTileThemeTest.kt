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

    fun layoutMetrics_roundScreen_usesWiderInsetAndNarrowSyncButton() {

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

        assertTrue(round.syncButtonWidthDp <= 140f)

        assertTrue(round.syncButtonWidthDp < rect.syncButtonWidthDp)

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


