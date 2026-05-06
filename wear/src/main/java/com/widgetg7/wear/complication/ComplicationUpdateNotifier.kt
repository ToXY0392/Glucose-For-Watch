package com.widgetg7.wear.complication

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

internal object ComplicationUpdateNotifier {
    private const val TAG = "WG7_Complication"

    fun requestUpdateAll(context: Context) {
        runCatching {
            ComplicationDataSourceUpdateRequester
                .create(
                    context.applicationContext,
                    ComponentName(context.applicationContext, GlucoseComplicationService::class.java),
                ).requestUpdateAll()
        }.onFailure { Log.w(TAG, "requestUpdateAll failed", it) }
    }
}
