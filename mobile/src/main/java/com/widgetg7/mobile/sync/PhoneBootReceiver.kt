package com.widgetg7.mobile.sync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PhoneBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            PhoneAutoSyncScheduler.schedule(context)
        }
    }
}
