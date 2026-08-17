package com.glucoseforwatch.core.utils

import android.os.Build

/**
 * Runtime helpers for Android 17 (API 37+) behavior.
 *
 * Keep new API-37-only paths behind [isAndroid17OrHigher] / [runWithFallback]
 * so production devices on Android 16 (API 36) and below keep working.
 */
object Android17Compat {
    /** Android 17 platform API level (preview / final). */
    const val API_LEVEL: Int = 37

    /**
     * True when the device runs Android 17 (API 37) or higher.
     */
    val isAndroid17OrHigher: Boolean
        get() = Build.VERSION.SDK_INT >= API_LEVEL

    /**
     * Runs [onAndroid17] only on Android 17+, otherwise [fallback]
     * for Android 16 and below.
     */
    inline fun runWithFallback(
        onAndroid17: () -> Unit,
        fallback: () -> Unit,
    ) {
        if (isAndroid17OrHigher) {
            onAndroid17()
        } else {
            fallback()
        }
    }
}
