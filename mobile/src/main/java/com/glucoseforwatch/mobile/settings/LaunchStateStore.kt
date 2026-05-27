package com.glucoseforwatch.mobile.settings

import android.content.Context

/** Tracks whether the user completed first-run Dexcom entry. */
class LaunchStateStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun hasCompletedDexcomEntry(): Boolean = prefs.getBoolean(KEY_DEXCOM_ENTRY_DONE, false)

    fun markDexcomEntryCompleted() {
        prefs.edit().putBoolean(KEY_DEXCOM_ENTRY_DONE, true).apply()
    }

    fun resetDexcomEntry() {
        prefs.edit().putBoolean(KEY_DEXCOM_ENTRY_DONE, false).apply()
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_launch_state"
        private const val KEY_DEXCOM_ENTRY_DONE = "dexcom_entry_done"
    }
}
