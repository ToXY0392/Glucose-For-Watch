package com.widgetg7.wear.complication

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.widgetg7.wear.data.GlucoseCache

class GlucoseComplicationService : SuspendingComplicationDataSourceService() {
    private val logTag = "WidgetG7Wear"

    // Keep glucose in the primary text slot so it remains the most prominent
    // element regardless of how third-party watch faces size title vs text.
    private fun primaryGlucoseText(valueMgDl: Int): String = valueMgDl.toString()

    private fun secondaryMetadata(metadata: String): String = metadata

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT ->
                buildShortTextData(primaryGlucoseText(128), secondaryMetadata("mg/dL ↗"))

            ComplicationType.LONG_TEXT ->
                buildLongTextData(primaryGlucoseText(128), secondaryMetadata("mg/dL ↗"))

            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val snapshot = GlucoseCache(this).load()
        Log.d(
            logTag,
            "Complication request type=${request.complicationType} hasSnapshot=${snapshot != null}",
        )

        val text = if (snapshot == null) "--" else primaryGlucoseText(snapshot.valueMgDl)
        val title = if (snapshot == null) "No data" else secondaryMetadata(snapshot.secondaryLabel())

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> buildShortTextData(text, title)
            ComplicationType.LONG_TEXT -> buildLongTextData(text, title)
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

    private fun buildTapAction(): PendingIntent? {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: return null
        return PendingIntent.getActivity(
            this,
            0,
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}
