package com.widgetg7.mobile.settings

import android.content.Context
import com.widgetg7.core.model.GlucoseDisplayUnit

/** Plain prefs for display options (not encrypted — no secrets). */
class DisplaySettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadGlucoseDisplayUnit(): GlucoseDisplayUnit =
        GlucoseDisplayUnit.fromStorage(prefs.getString(KEY_GLUCOSE_DISPLAY_UNIT, null))

    fun saveGlucoseDisplayUnit(unit: GlucoseDisplayUnit) {
        prefs.edit().putString(KEY_GLUCOSE_DISPLAY_UNIT, unit.name).apply()
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_display_settings"
        private const val KEY_GLUCOSE_DISPLAY_UNIT = "glucose_display_unit"
    }
}
