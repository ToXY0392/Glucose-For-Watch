package com.glucoseforwatch.wear

import android.app.Application
import android.util.Log
import androidx.annotation.Keep
import com.glucoseforwatch.wear.complication.ComplicationWakeRefresh

/** Wear app entry point; registers screen-on complication refresh. */
@Keep
class GlucoseForWatchWearApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ComplicationWakeRefresh.register(this)
        // Do not requestUpdateAll here — startup spam contributes to SysUI ignoring the provider.
        Log.w("WG7.Complication", "Application.onCreate (wake refresh registered)")
    }
}
