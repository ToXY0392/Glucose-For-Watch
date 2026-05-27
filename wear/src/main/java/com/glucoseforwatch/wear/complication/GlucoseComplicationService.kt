package com.glucoseforwatch.wear.complication

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService.ComplicationRequestListener
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.glucoseforwatch.wear.WearMainActivity
import com.glucoseforwatch.wear.data.GlucoseCache
import com.glucoseforwatch.wear.data.GlucoseSnapshot

/**
 * Watch face complication for current glucose.
 *
 * Uses [ComplicationDataSourceService] (callback API) for reliable binding on Wear OS 5.
 * Hybrid refresh: system polling (manifest 300 s) + push on new readings + screen-on wake.
 */
class GlucoseComplicationService : ComplicationDataSourceService() {

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {
        ComplicationInstanceRegistry.register(this, complicationInstanceId)
        Log.i(TAG, "activated instance=$complicationInstanceId type=$type")
        requestComplicationUpdate(complicationInstanceId)
    }

    override fun onComplicationDeactivated(complicationInstanceId: Int) {
        ComplicationInstanceRegistry.unregister(this, complicationInstanceId)
        Log.i(TAG, "deactivated instance=$complicationInstanceId")
    }

    private fun requestComplicationUpdate(complicationInstanceId: Int) {
        runCatching {
            ComplicationDataSourceUpdateRequester
                .create(
                    applicationContext,
                    ComponentName(applicationContext, GlucoseComplicationService::class.java),
                ).requestUpdate(complicationInstanceId)
        }.onFailure { Log.w(TAG, "requestUpdate failed instance=$complicationInstanceId", it) }
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        val snapshot = GlucoseCache(this).load() ?: previewFallbackSnapshot()
        return buildForSnapshot(type, snapshot, tapAction = null)
    }

    private fun previewFallbackSnapshot(): GlucoseSnapshot =
        GlucoseSnapshot(
            valueMgDl = 120,
            trend = "FLAT",
            deltaMgDl = 0,
            timestampEpochMs = System.currentTimeMillis(),
            stale = false,
        )

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener,
    ) {
        runCatching {
            val snapshot = GlucoseCache(this).load()
            Log.i(
                TAG,
                "onComplicationRequest instance=${request.complicationInstanceId} " +
                    "type=${request.complicationType} value=${snapshot?.valueMgDl} " +
                    "stale=${snapshot?.stale}",
            )
            val data =
                buildForSnapshot(
                    type = request.complicationType,
                    snapshot = snapshot,
                    tapAction = buildTapAction(request.complicationInstanceId),
                )
            listener.onComplicationData(data)
        }.onFailure { e ->
            Log.e(TAG, "onComplicationRequest failed", e)
            listener.onComplicationData(
                buildForSnapshot(
                    type = request.complicationType,
                    snapshot = null,
                    tapAction = buildTapAction(request.complicationInstanceId),
                ),
            )
        }
    }

    private fun buildForSnapshot(
        type: ComplicationType,
        snapshot: GlucoseSnapshot?,
        tapAction: PendingIntent?,
    ): ComplicationData? {
        val payload = GlucoseComplicationDataFactory.fromSnapshot(snapshot)
        return GlucoseComplicationDataFactory.buildData(type, payload, tapAction)
    }

    private fun buildTapAction(instanceId: Int): PendingIntent {
        val launchIntent =
            Intent(this, WearMainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        val requestCode = instanceId and 0xFFFF
        return PendingIntent.getActivity(
            this,
            requestCode,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private companion object {
        private const val TAG = "WG7.Complication"
    }
}
