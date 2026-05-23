package com.widgetg7.wear.complication

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.display.WearGlucoseSurfaceModelFactory

/**
 * Watch face complication for current glucose.
 *
 * Display semantics match the tile via [WearGlucoseSurfaceModelFactory]. Short/long text colors are
 * chosen by the watch face (API 1.2.x). [ComplicationType.RANGED_VALUE] maps mg/dL to 40–400 mg/dL.
 */
class GlucoseComplicationService : SuspendingComplicationDataSourceService() {

    private fun secondaryMetadata(metadata: String): String =
        if (metadata.isBlank()) "mg/dL" else "mg/dL $metadata"

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT ->
                buildShortTextData("128", secondaryMetadata("↗"), PreviewComplicationInstanceId)

            ComplicationType.LONG_TEXT ->
                buildLongTextData("128", secondaryMetadata("↗"), PreviewComplicationInstanceId)

            ComplicationType.RANGED_VALUE ->
                buildRangedValueData(
                    displayText = "128",
                    title = secondaryMetadata("↗"),
                    valueMgDl = 128,
                    instanceId = PreviewComplicationInstanceId,
                    stale = false,
                )

            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        return runCatching {
                val snapshot = GlucoseCache(this).load()
                val display = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)
                val title =
                    if (snapshot == null) {
                        "mg/dL"
                    } else {
                        secondaryMetadata(snapshot.compactTrendOnlyLabel())
                    }
                val valueMgDl = display.valueMgDl ?: 128

                when (request.complicationType) {
                    ComplicationType.SHORT_TEXT ->
                        buildShortTextData(display.valueText, title, request.complicationInstanceId)
                    ComplicationType.LONG_TEXT ->
                        buildLongTextData(display.valueText, title, request.complicationInstanceId)
                    ComplicationType.RANGED_VALUE ->
                        buildRangedValueData(
                            displayText = display.valueText,
                            title = title,
                            valueMgDl = valueMgDl,
                            instanceId = request.complicationInstanceId,
                            stale = display.stale,
                        )

                    else -> null
                }
            }
            .getOrElse { e ->
                Log.e(TAG, "onComplicationRequest", e)
                when (request.complicationType) {
                    ComplicationType.SHORT_TEXT ->
                        buildShortTextData("--", "mg/dL", request.complicationInstanceId)
                    ComplicationType.LONG_TEXT ->
                        buildLongTextData("--", "mg/dL", request.complicationInstanceId)
                    ComplicationType.RANGED_VALUE ->
                        buildRangedValueData("--", "mg/dL", 128, request.complicationInstanceId, stale = true)
                    else -> null
                }
            }
    }

    private fun buildShortTextData(
        text: String,
        title: String,
        instanceId: Int,
    ): ShortTextComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder("Current glucose").build(),
        ).setTitle(PlainComplicationText.Builder(title).build())
            .setTapAction(buildTapAction(instanceId))
            .build()
    }

    private fun buildLongTextData(
        text: String,
        title: String,
        instanceId: Int,
    ): LongTextComplicationData {
        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder("$text $title").build(),
            contentDescription = PlainComplicationText.Builder("Current glucose").build(),
        ).setTitle(PlainComplicationText.Builder(text).build())
            .setTapAction(buildTapAction(instanceId))
            .build()
    }

    /**
     * Ranged arc uses AGP clinical scale (40–400 mg/dL). Value placement reflects hypo / in-range / hyper.
     */
    private fun buildRangedValueData(
        displayText: String,
        title: String,
        valueMgDl: Int,
        instanceId: Int,
        stale: Boolean,
    ): RangedValueComplicationData {
        val rangedValue =
            if (stale) {
                RANGED_UNKNOWN_PLACEHOLDER
            } else {
                valueMgDl.coerceIn(RANGED_MIN_MG_DL, RANGED_MAX_MG_DL).toFloat()
            }
        return RangedValueComplicationData.Builder(
            value = rangedValue,
            min = RANGED_MIN_MG_DL.toFloat(),
            max = RANGED_MAX_MG_DL.toFloat(),
            contentDescription = PlainComplicationText.Builder("Current glucose").build(),
        ).setText(PlainComplicationText.Builder(displayText).build())
            .setTitle(PlainComplicationText.Builder(title).build())
            .setTapAction(buildTapAction(instanceId))
            .build()
    }

    /** Copy launch intent before wrapping in PendingIntent (system intent is reusable). */
    private fun buildTapAction(instanceId: Int): PendingIntent? {
        val base = packageManager.getLaunchIntentForPackage(packageName) ?: return null
        val launchIntent =
            Intent(base).apply {
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
        private const val TAG = "WG7_Complication"
        private const val PreviewComplicationInstanceId = 0
        private const val RANGED_MIN_MG_DL = 40
        private const val RANGED_MAX_MG_DL = 400
        /** Center of arc when reading is stale or missing (AGP unknown band). */
        private const val RANGED_UNKNOWN_PLACEHOLDER = 120f
    }
}
