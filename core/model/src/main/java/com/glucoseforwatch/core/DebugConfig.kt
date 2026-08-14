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
        // 1) attempt to read application BuildConfig.DEBUG via reflection
        try {
            val cls = Class.forName("com.glucoseforwatch.mobile.BuildConfig")
            val field = cls.getDeclaredField("DEBUG")
            val value = field.getBoolean(null)
            if (value) return@lazy true
        } catch (_: Throwable) {
            // ignore and try next
        }

        // 2) allow forcing debug via system property gfw.forceDebug (settable with adb shell setprop)
        try {
            val sp = Class.forName("android.os.SystemProperties")
            val getBoolean = sp.getMethod("getBoolean", String::class.java, java.lang.Boolean.TYPE)
            val forced = getBoolean.invoke(null, "gfw.forceDebug", false) as Boolean
            if (forced) return@lazy true
        } catch (_: Throwable) {
            // ignore
        }

        // 3) fallback: non-user builds considered debug
        try {
            android.os.Build.TYPE != "user"
        } catch (_: Throwable) {
            false
        }
    }
}
