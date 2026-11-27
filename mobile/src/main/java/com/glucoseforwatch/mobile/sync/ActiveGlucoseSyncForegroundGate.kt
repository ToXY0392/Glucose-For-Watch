package com.glucoseforwatch.mobile.sync

import android.app.ForegroundServiceStartNotAllowedException
import android.os.Build
import android.util.Log

/** Attempts foreground promotion; swallows quota/security refusal without crashing. */
object ActiveGlucoseSyncForegroundGate {
    private const val TAG = "WG7.FgsGate"

    fun promote(startForeground: () -> Unit): Boolean {
        return try {
            startForeground()
            true
        } catch (error: Exception) {
            if (error is SecurityException || isForegroundStartRefused(error)) {
                Log.w(TAG, "foreground_start_refused reason=${error::class.simpleName}", error)
                false
            } else {
                throw error
            }
        }
    }

    /**
     * [ForegroundServiceStartNotAllowedException] exists from API 31 only: test it behind the
     * version check so Android 9–11 never resolves the class.
     */
    fun isForegroundStartRefused(error: Throwable): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && error is ForegroundServiceStartNotAllowedException
}
