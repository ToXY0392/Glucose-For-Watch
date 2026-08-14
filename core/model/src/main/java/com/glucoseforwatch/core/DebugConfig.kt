package com.glucoseforwatch.core

/**
 * Runtime debug flag.
 *
 * Attempts to read the application's BuildConfig.DEBUG via reflection so feature modules
 * don't need a compile-time dependency on the mobile app. Falls back to checking
 * android.os.Build.TYPE to detect non-production environments.
 */
object DebugConfig {
    val DEBUG: Boolean by lazy {
        try {
            val cls = Class.forName("com.glucoseforwatch.mobile.BuildConfig")
            val field = cls.getDeclaredField("DEBUG")
            field.getBoolean(null)
        } catch (t: Throwable) {
            // Fallback: treat non-user builds as debug (userdebug/eng)
            try {
                android.os.Build.TYPE != "user"
            } catch (_: Throwable) {
                false
            }
        }
    }
}
