package com.glucoseforwatch.wear.tile

import android.content.Context
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper
import androidx.wear.tooling.preview.devices.WearDevices
import com.glucoseforwatch.wear.data.GlucoseSnapshot

/**
 * Wear Tiles Design-pane previews (ProtoLayout — not Compose).
 *
 * **Android Studio** (Koala Feature Drop+ / Narwhal+): module `:wear`, variant **debug**.
 * Open this file → **Split** → previews listed by [name].
 *
 * Annotation must be [androidx.wear.tiles.tooling.preview.Preview], not Compose `@Preview`.
 */
private fun sampleSnapshot(
    valueMgDl: Int,
    trend: String,
    stale: Boolean = false,
): GlucoseSnapshot =
    GlucoseSnapshot(
        valueMgDl = valueMgDl,
        trend = trend,
        deltaMgDl = 0,
        timestampEpochMs = System.currentTimeMillis(),
        stale = stale,
    )

private fun previewTile(
    context: Context,
    snapshot: GlucoseSnapshot?,
    syncLocked: Boolean,
): TilePreviewData =
    TilePreviewData(
        onTileResourceRequest = { GlucoseSimpleTileLayout.emptyResources() },
        onTileRequest = { request ->
            val device = request.deviceConfiguration
            val metrics =
                ToxyTileTheme.layoutMetrics(
                    screenWidthDp = device.screenWidthDp,
                    screenHeightDp = device.screenHeightDp,
                    screenShape = device.screenShape,
                )
            val root =
                GlucoseSimpleTileLayout.buildRoot(
                    context = context,
                    snapshot = snapshot,
                    metrics = metrics,
                    syncLocked = syncLocked,
                )
            TilePreviewHelper.singleTimelineEntryTileBuilder(root).build()
        },
    )

@Preview(name = "Tile — 120 + trend", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewInRange(context: Context): TilePreviewData =
    previewTile(context, sampleSnapshot(120, "FLAT"), syncLocked = false)

@Preview(name = "Tile — 78 two digits", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewTwoDigits(context: Context): TilePreviewData =
    previewTile(context, sampleSnapshot(78, "DOWN"), syncLocked = false)

@Preview(name = "Tile — 319 three digits", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewThreeDigits(context: Context): TilePreviewData =
    previewTile(context, sampleSnapshot(319, "UP"), syncLocked = false)

@Preview(name = "Tile — no trend", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewNoTrend(context: Context): TilePreviewData =
    previewTile(context, sampleSnapshot(118, ""), syncLocked = false)

@Preview(name = "Tile — sync locked", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewSyncLocked(context: Context): TilePreviewData =
    previewTile(context, sampleSnapshot(120, "FLAT"), syncLocked = true)

@Preview(name = "Tile — empty", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewEmpty(context: Context): TilePreviewData =
    previewTile(context, snapshot = null, syncLocked = false)
