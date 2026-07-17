package com.glucoseforwatch.wear.complication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.Keep
import com.glucoseforwatch.wear.data.GlucoseCache

/**
 * Wear OS often drops push updates in ambient/AOD; refresh when the user wakes the display.
 * Respects the notifier rate limit (no force) so we stay within SysUI's ~5 min guidance.
 */
@Keep
internal class ComplicationWakeRefresh : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_SCREEN_ON) return
        val snapshot = GlucoseCache(context.applicationContext).load() ?: return
        Log.i(TAG, "screen_on -> complication refresh value=${snapshot.valueMgDl}")
        ComplicationUpdateNotifier.notifyReadingChanged(
            context = context,
            readingTimestampEpochMs = snapshot.timestampEpochMs,
            force = false,
        )
    }

    companion object {
        private const val TAG = "WG7.Complication"

        fun register(context: Context) {
            val appContext = context.applicationContext
            val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appContext.registerReceiver(ComplicationWakeRefresh(), filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("UnspecifiedRegisterReceiverFlag")
                appContext.registerReceiver(ComplicationWakeRefresh(), filter)
            }
        }
    }
}
