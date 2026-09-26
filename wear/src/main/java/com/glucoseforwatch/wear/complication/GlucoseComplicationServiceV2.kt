package com.glucoseforwatch.wear.complication

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.annotation.Keep
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.glucoseforwatch.wear.WearMainActivity
import com.glucoseforwatch.wear.data.GlucoseCache
import java.util.concurrent.TimeUnit
import com.glucoseforwatch.wear.data.GlucoseSnapshot

@Keep
class GlucoseComplicationServiceV2 : ComplicationDataSourceService() {

    override fun onCreate() {
        super.onCreate()
        Log.w(TAG, "service onCreate (provider V2 bound)")
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {
        ComplicationInstanceRegistry.register(this, complicationInstanceId)
        requestComplicationUpdate(complicationInstanceId)
    }

    override fun onComplicationDeactivated(complicationInstanceId: Int) {
        ComplicationInstanceRegistry.unregister(this, complicationInstanceId)
    }

    private fun requestComplicationUpdate(complicationInstanceId: Int) {
        runCatching {
            ComplicationDataSourceUpdateRequester
                .create(applicationContext, ComponentName(this, GlucoseComplicationServiceV2::class.java))
                .requestUpdate(complicationInstanceId)
        }
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
            ComplicationInstanceRegistry.register(this, request.complicationInstanceId)
            val ageMinutes = snapshot?.let {
                TimeUnit.MILLISECONDS.toMinutes(
                    (System.currentTimeMillis() - it.timestampEpochMs).coerceAtLeast(0L),
                )
            }
            Log.i(
                TAG,
                "onComplicationRequest instance=${request.complicationInstanceId} " +
                    "type=${request.complicationType} ageMinutes=$ageMinutes stale=${snapshot?.stale}",
            )
            val data = buildForSnapshot(
                type = request.complicationType,
                snapshot = snapshot,
                tapAction = buildTapAction(request.complicationInstanceId),
            )
            listener.onComplicationData(data)
        }.onFailure {
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
        val launchIntent = Intent(this, WearMainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        return PendingIntent.getActivity(
            this,
            instanceId and 0xFFFF,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private companion object {
        private const val TAG = "WG7.Complication"
    }
}