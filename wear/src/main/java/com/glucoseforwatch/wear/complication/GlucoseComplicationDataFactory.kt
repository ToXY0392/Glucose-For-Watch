package com.glucoseforwatch.wear.complication

import android.app.PendingIntent
import androidx.annotation.Keep
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.TimeRange
import com.glucoseforwatch.core.model.GlucoseDisplayUnit
import com.glucoseforwatch.core.model.GlucoseUnitFormatter
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import com.glucoseforwatch.wear.display.WearGlucoseSurfaceModel
import com.glucoseforwatch.wear.display.WearGlucoseSurfaceModelFactory

/** Builds complication payloads shared by [GlucoseComplicationServiceV2] (tile-aligned AGP semantics). */
@Keep
internal object GlucoseComplicationDataFactory {
    /**
     * Align with tile chrome: after this age, show disconnected placeholder instead of
     * the last numeric reading (SysUI must still keep the slot visible).
     */
    const val STALE_AFTER_MS = 15L * 60_000L

    /** Clear disconnect / no-data glyph for short complication slots. */
    const val DISCONNECTED_VALUE = "---"

    @Keep
    data class RequestPayload(
        val display: WearGlucoseSurfaceModel,
        val title: String,
        val valueMgDl: Int,
        val displayUnit: GlucoseDisplayUnit,
        val timestampEpochMs: Long,
        /** True when cache empty or reading older than [STALE_AFTER_MS]. */
        val disconnected: Boolean = false,
    )

    fun fromSnapshot(
        snapshot: GlucoseSnapshot?,
        nowEpochMs: Long = System.currentTimeMillis(),
    ): RequestPayload {
        val displayUnit = snapshot?.displayUnit ?: GlucoseDisplayUnit.MG_DL
        val unitLabel = snapshot?.unitLabel() ?: GlucoseDisplayUnit.MG_DL.label()
        val disconnected = isDisconnected(snapshot, nowEpochMs)

        if (disconnected) {
            val base = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)
            return RequestPayload(
                display =
                    base.copy(
                        valueText = DISCONNECTED_VALUE,
                        trendArrow = "",
                        showTrend = false,
                        stale = true,
                        valueMgDl = null,
                        unitLabel = unitLabel,
                    ),
                title = unitLabel,
                valueMgDl = base.valueMgDl ?: 128,
                displayUnit = displayUnit,
                timestampEpochMs = nowEpochMs,
                disconnected = true,
            )
        }

        val display = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)
        return RequestPayload(
            display = display,
            title = secondaryMetadata(unitLabel, snapshot!!.compactTrendOnlyLabel()),
            valueMgDl = display.valueMgDl ?: 128,
            displayUnit = displayUnit,
            timestampEpochMs = snapshot.timestampEpochMs,
            disconnected = false,
        )
    }

    fun isDisconnected(snapshot: GlucoseSnapshot?, nowEpochMs: Long = System.currentTimeMillis()): Boolean {
        if (snapshot == null) return true
        if (snapshot.timestampEpochMs <= 0L) return true
        return (nowEpochMs - snapshot.timestampEpochMs) > STALE_AFTER_MS
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

    /**
     * Never expire relative to the medical reading timestamp — that caused SysUI to hide
     * the slot after ~6 minutes offline. Keep the complication always visible; freshness
     * is expressed in the text ("---") instead.
     */
    private fun validityRange(): TimeRange = TimeRange.ALWAYS

    private fun buildShortTextData(
        payload: RequestPayload,
        tapAction: PendingIntent?,
    ): ShortTextComplicationData {
        return ShortTextComplicationData.Builder(
            text = plainText(payload.display.valueText),
            contentDescription = PlainComplicationText.Builder("Glycémie actuelle").build(),
        ).setTitle(plainText(payload.title))
            .setValidTimeRange(validityRange())
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
            .setValidTimeRange(validityRange())
            .apply { tapAction?.let { setTapAction(it) } }
            .build()
    }

    private fun buildRangedValueData(
        payload: RequestPayload,
        tapAction: PendingIntent?,
    ): RangedValueComplicationData {
        val stale = payload.display.stale || payload.disconnected
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
            .setValidTimeRange(validityRange())
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
