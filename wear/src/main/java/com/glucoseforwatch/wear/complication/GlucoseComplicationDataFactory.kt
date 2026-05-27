package com.glucoseforwatch.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.core.model.GlucoseUnitFormatter
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import com.glucoseforwatch.wear.display.WearGlucoseSurfaceModel
import com.glucoseforwatch.wear.display.WearGlucoseSurfaceModelFactory

/** Builds complication payloads shared by [GlucoseComplicationService] (tile-aligned AGP semantics). */
internal object GlucoseComplicationDataFactory {
    data class RequestPayload(
        val display: WearGlucoseSurfaceModel,
        val title: String,
        val valueMgDl: Int,
        val displayUnit: GlucoseDisplayUnit,
    )

    fun fromSnapshot(snapshot: GlucoseSnapshot?): RequestPayload {
        val display = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)
        val displayUnit = snapshot?.displayUnit ?: GlucoseDisplayUnit.MG_DL
        val unitLabel = snapshot?.unitLabel() ?: GlucoseDisplayUnit.MG_DL.label()
        val title =
            if (snapshot == null) {
                unitLabel
            } else {
                secondaryMetadata(unitLabel, snapshot.compactTrendOnlyLabel())
            }
        return RequestPayload(
            display = display,
            title = title,
            valueMgDl = display.valueMgDl ?: 128,
            displayUnit = displayUnit,
        )
    }

    fun buildData(
        type: ComplicationType,
        payload: RequestPayload,
        tapAction: PendingIntent?,
    ): ComplicationData? =
        when (type) {
            ComplicationType.SHORT_TEXT ->
                buildShortTextData(payload, tapAction)
            ComplicationType.LONG_TEXT ->
                buildLongTextData(payload, tapAction)
            ComplicationType.RANGED_VALUE ->
                buildRangedValueData(payload, tapAction)
            else -> null
        }

    private fun secondaryMetadata(unitLabel: String, metadata: String): String =
        if (metadata.isBlank()) unitLabel else "$unitLabel $metadata"

    private fun buildShortTextData(
        payload: RequestPayload,
        tapAction: PendingIntent?,
    ): ShortTextComplicationData {
        return ShortTextComplicationData.Builder(
            text = plainText(payload.display.valueText),
            contentDescription = PlainComplicationText.Builder("Glycémie actuelle").build(),
        ).setTitle(plainText(payload.title))
            .apply { tapAction?.let { setTapAction(it) } }
            .build()
    }

    private fun buildLongTextData(
        payload: RequestPayload,
        tapAction: PendingIntent?,
    ): LongTextComplicationData {
        return LongTextComplicationData.Builder(
            text = plainText("${payload.display.valueText} ${payload.title}"),
            contentDescription = PlainComplicationText.Builder("Glycémie actuelle").build(),
        ).setTitle(plainText(payload.display.valueText))
            .apply { tapAction?.let { setTapAction(it) } }
            .build()
    }

    private fun buildRangedValueData(
        payload: RequestPayload,
        tapAction: PendingIntent?,
    ): RangedValueComplicationData {
        val stale = payload.display.stale
        val unit = payload.displayUnit
        val rangedMin = GlucoseUnitFormatter.rangedMin(unit)
        val rangedMax = GlucoseUnitFormatter.rangedMax(unit)
        val rangedValue =
            if (stale) {
                GlucoseUnitFormatter.rangedUnknownPlaceholder(unit)
            } else {
                GlucoseUnitFormatter
                    .toRangedDisplayValue(payload.valueMgDl, unit)
                    .coerceIn(rangedMin, rangedMax)
            }
        return RangedValueComplicationData.Builder(
            value = rangedValue,
            min = rangedMin,
            max = rangedMax,
            contentDescription = PlainComplicationText.Builder("Glycémie actuelle").build(),
        ).setText(plainText(payload.display.valueText))
            .setTitle(plainText(payload.title))
            .setColorRamp(
                AgpComplicationColorRamp.forGlucoseRange(
                    minMgDl = GlucoseUnitFormatter.DISPLAY_LOW_MAX_MG_DL.toFloat(),
                    maxMgDl = GlucoseUnitFormatter.DISPLAY_HIGH_MIN_MG_DL.toFloat(),
                    stale = stale,
                ),
            )
            .apply { tapAction?.let { setTapAction(it) } }
            .build()
    }

    private fun plainText(text: String) = PlainComplicationText.Builder(text).build()
}
