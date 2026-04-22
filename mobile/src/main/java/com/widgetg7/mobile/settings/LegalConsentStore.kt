package com.widgetg7.mobile.settings

import android.content.Context

class LegalConsentStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun hasAcceptedCurrentVersion(): Boolean {
        return prefs.getString(KEY_ACCEPTED_VERSION, null) == CURRENT_LEGAL_VERSION
    }

    fun acceptedAtEpochMs(): Long = prefs.getLong(KEY_ACCEPTED_AT_EPOCH_MS, 0L)

    fun markAcceptedCurrentVersion() {
        prefs.edit()
            .putString(KEY_ACCEPTED_VERSION, CURRENT_LEGAL_VERSION)
            .putLong(KEY_ACCEPTED_AT_EPOCH_MS, System.currentTimeMillis())
            .apply()
    }

    companion object {
        const val CURRENT_LEGAL_VERSION = "2026-04-22"

        private const val PREFS_NAME = "widget_g7_legal_consent"
        private const val KEY_ACCEPTED_VERSION = "accepted_version"
        private const val KEY_ACCEPTED_AT_EPOCH_MS = "accepted_at_epoch_ms"
    }
}
