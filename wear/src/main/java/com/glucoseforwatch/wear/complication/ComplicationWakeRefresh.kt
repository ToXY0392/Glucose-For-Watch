package com.glucoseforwatch.wear.complication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.glucoseforwatch.wear.data.GlucoseCache

/**
 * Wear OS 5 often ignores push updates in ambient/AOD; refresh when the user wakes the display.
 * Register once from [GlucoseForWatchWearApplication].
 */
internal class ComplicationWakeRefresh : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_SCREEN_ON) return
        val snapshot = GlucoseCache(context.applicationContext).load() ?: return
        Log.i(TAG, "screen_on -> complication refresh value=${snapshot.valueMgDl}")
        ComplicationUpdateNotifier.notifyReadingChanged(
            context = context,
            readingTimestampEpochMs = snapshot.timestampEpochMs,
            force = true,
        )
    }

    companion object {
        private const val TAG = "WG7.Complication"

        fun register(context: Context) {
            val appContext = context.applicationContext
            appContext.registerReceiver(
                ComplicationWakeRefresh(),
                IntentFilter(Intent.ACTION_SCREEN_ON),
            )
        }
    }
}
