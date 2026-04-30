package com.widgetg7.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.widgetg7.wear.data.GlucoseCache

class GlucoseComplicationService : SuspendingComplicationDataSourceService() {
    private fun secondaryMetadata(metadata: String): String =
        if (metadata.isBlank()) "mg/dL" else "mg/dL $metadata"

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT ->
                buildShortTextData("128", secondaryMetadata("↗"))

            ComplicationType.LONG_TEXT ->
                buildLongTextData("128", secondaryMetadata("↗"))

            ComplicationType.RANGED_VALUE ->
                buildRangedValueData(
                    displayText = "128",
                    title = secondaryMetadata("↗"),
                    valueMgDl = 128,
                )

            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val snapshot = GlucoseCache(this).load()

        val text = if (snapshot == null) "--" else snapshot.displayValueText()
        val title =
            if (snapshot == null) {
                "mg/dL"
            } else {
                secondaryMetadata(snapshot.compactTrendOnlyLabel())
            }

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> buildShortTextData(text, title)
            ComplicationType.LONG_TEXT -> buildLongTextData(text, title)
            ComplicationType.RANGED_VALUE ->
                buildRangedValueData(
                    displayText = text,
                    title = title,
                    valueMgDl = snapshot?.valueMgDl ?: 128,
                )
            else -> null
        }
    }

    private fun buildShortTextData(text: String, title: String): ShortTextComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder("Current glucose").build(),
        ).setTitle(PlainComplicationText.Builder(title).build())
            .setTapAction(buildTapAction())
            .build()
    }

    private fun buildLongTextData(text: String, title: String): LongTextComplicationData {
        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder("$text $title").build(),
            contentDescription = PlainComplicationText.Builder("Current glucose").build(),
        ).setTitle(PlainComplicationText.Builder(text).build())
            .setTapAction(buildTapAction())
            .build()
    }

    private fun buildRangedValueData(
        displayText: String,
        title: String,
        valueMgDl: Int,
    ): RangedValueComplicationData {
        return RangedValueComplicationData.Builder(
            value = valueMgDl.coerceIn(RANGED_MIN_MG_DL, RANGED_MAX_MG_DL).toFloat(),
            min = RANGED_MIN_MG_DL.toFloat(),
            max = RANGED_MAX_MG_DL.toFloat(),
            contentDescription = PlainComplicationText.Builder("Current glucose").build(),
        ).setText(PlainComplicationText.Builder(displayText).build())
            .setTitle(PlainComplicationText.Builder(title).build())
            .setTapAction(buildTapAction())
            .build()
    }

    private fun buildTapAction(): PendingIntent? {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: return null
        return PendingIntent.getActivity(
            this,
            0,
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private companion object {
        private const val RANGED_MIN_MG_DL = 40
        private const val RANGED_MAX_MG_DL = 400
    }
}
