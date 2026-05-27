package com.glucoseforwatch.wear

import android.app.Application
import com.glucoseforwatch.wear.complication.ComplicationWakeRefresh

/** Wear app entry point; registers screen-on complication refresh. */
class GlucoseForWatchWearApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ComplicationWakeRefresh.register(this)
    }
}
