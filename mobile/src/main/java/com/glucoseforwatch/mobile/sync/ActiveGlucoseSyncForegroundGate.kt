package com.glucoseforwatch.mobile.sync

import android.app.ForegroundServiceStartNotAllowedException
import android.util.Log

/** Attempts foreground promotion; swallows quota/security refusal without crashing. */
object ActiveGlucoseSyncForegroundGate {
    private const val TAG = "WG7.FgsGate"

    fun promote(startForeground: () -> Unit): Boolean {
        return try {
            startForeground()
            true
        } catch (error: Exception) {
            when (error) {
                is ForegroundServiceStartNotAllowedException,
                is SecurityException,
                -> {
                    Log.w(TAG, "foreground_start_refused reason=${error::class.simpleName}", error)
                    false
                }
                else -> throw error
            }
        }
    }
}
