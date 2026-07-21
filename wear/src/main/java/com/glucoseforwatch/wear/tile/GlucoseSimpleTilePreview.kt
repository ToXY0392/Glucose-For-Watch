package com.glucoseforwatch.wear.tile

import android.content.Context
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tooling.preview.devices.WearDevices
import com.glucoseforwatch.wear.data.GlucoseSnapshot

/**
 * Wear Tiles Design-pane previews (ProtoLayout — not Compose).
 *
 * Clinical cases for [GlucoseSimpleTileLayout] / [GlucoseTileChrome]:
 * target (green), hypo (red), hyper 210 (orange alert), stale (fade + age label).
 *
 * **Android Studio**: module `:wear`, variant **debug** → open this file → **Split** / **Design**.
 *
 * Annotation must be [androidx.wear.tiles.tooling.preview.Preview], not Compose `@Preview`.
 */
private fun sampleSnapshot(
    valueMgDl: Int,
    trend: String,
    timestampEpochMs: Long = System.currentTimeMillis(),
    stale: Boolean = false,
): GlucoseSnapshot =
    GlucoseSnapshot(
        valueMgDl = valueMgDl,
        trend = trend,
        deltaMgDl = 0,
        timestampEpochMs = timestampEpochMs,
        stale = stale,
    )

private fun previewTile(
    context: Context,
    snapshot: GlucoseSnapshot?,
    syncLocked: Boolean = false,
    nowEpochMs: Long = System.currentTimeMillis(),
): TilePreviewData =
    TilePreviewData(
        onTileResourceRequest = { GlucoseSimpleTileLayout.emptyResources() },
        onTileRequest = { request ->
            val device = request.deviceConfiguration
            GlucoseSimpleTileLayout.buildTile(
                context = context,
                snapshot = snapshot,
                syncLocked = syncLocked,
                screenWidthDp = device.screenWidthDp,
                screenHeightDp = device.screenHeightDp,
                screenShape = device.screenShape,
                nowEpochMs = nowEpochMs,
            )
        },
    )

/** Normoglycémie / cible — vert AGP, tendance stable. */
@Preview(name = "Tile — Normoglycémie / Cible (110)", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewNormoglycemia(context: Context): TilePreviewData =
    previewTile(context, sampleSnapshot(valueMgDl = 110, trend = "FLAT"))

/** Hypoglycémie critique — rouge urgence, flèche bas. */
@Preview(name = "Tile — Hypoglycémie critique (55)", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewHypoglycemiaCritical(context: Context): TilePreviewData =
    previewTile(context, sampleSnapshot(valueMgDl = 55, trend = "DOWN"))

/** Hyperglycémie modérée — orange d'alerte (pas jaune), flèche haut. */
@Preview(name = "Tile — Hyperglycémie (210 orange)", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewHyperglycemia(context: Context): TilePreviewData =
    previewTile(context, sampleSnapshot(valueMgDl = 210, trend = "UP"))

/**
 * Donnée périmée — âge 22 min (>15): valeur/unité/tendance à ~50% alpha,
 * bas = "Il y a 22 min" (pas de "!" ni "Donnée périmée").
 */
@Preview(name = "Tile — Donnée périmée / Stale (118)", device = WearDevices.SMALL_ROUND)
fun GlucoseTilePreviewStale(context: Context): TilePreviewData {
    val nowEpochMs = 2_000_000_000L
    val snapshot =
        sampleSnapshot(
            valueMgDl = 118,
            trend = "FLAT",
            timestampEpochMs = nowEpochMs - (22 * 60 * 1000L),
            stale = false,
        )
    return previewTile(context, snapshot, nowEpochMs = nowEpochMs)
}
