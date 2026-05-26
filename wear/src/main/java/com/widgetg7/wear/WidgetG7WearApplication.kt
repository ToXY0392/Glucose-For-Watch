package com.widgetg7.wear

import android.app.Application
import com.widgetg7.wear.complication.ComplicationWakeRefresh

/** Wear app entry point; registers screen-on complication refresh. */
class WidgetG7WearApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ComplicationWakeRefresh.register(this)
    }
}
