package com.widgetg7.wear.tile

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.Wearable
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.widgetg7.wear.R
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseKeys
import com.widgetg7.wear.sync.WatchSyncHealthMonitor

private const val RESOURCES_VERSION = "18-maquette-flat-bg"

class GlucoseTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest,
    ): ListenableFuture<TileBuilders.Tile> {
        val cache = GlucoseCache(this)
        val healthMonitor = WatchSyncHealthMonitor(this)
        val snapshot = cache.load()
        val nowEpochMs = System.currentTimeMillis()
        healthMonitor.updateAndReport(nowEpochMs)

        if (requestParams.currentState.lastClickableId == REFRESH_CLICK_ID) {
            requestPhoneRefresh(cache, healthMonitor)
        }

        val valueText = snapshot?.displayValueText() ?: "--"
        val trendText = snapshot?.takeUnless { it.stale }?.trendArrow().orEmpty()
        val refreshAction = ActionBuilders.LoadAction.Builder().build()

        val value = LayoutElementBuilders.Text.Builder()
            .setText(valueText)
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(56f))
                    .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                    .setColor(ColorBuilders.argb(TILE_TEXT))
                    .build()
            )
            .setMaxLines(1)
            .build()

        val unit = LayoutElementBuilders.Text.Builder()
            .setText("mg/dL$trendText")
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(18f))
                    .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                    .setColor(ColorBuilders.argb(TILE_ACCENT))
                    .build()
            )
            .setMaxLines(1)
            .build()

        val refreshIcon = LayoutElementBuilders.Image.Builder()
            .setResourceId(RES_REFRESH_ICON)
            .setWidth(DimensionBuilders.dp(24f))
            .setHeight(DimensionBuilders.dp(24f))
            .build()

        val refreshButton = LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.dp(52f))
            .setHeight(DimensionBuilders.dp(52f))
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .addContent(refreshIcon)
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(
                        ModifiersBuilders.Clickable.Builder()
                            .setId(REFRESH_CLICK_ID)
                            .setOnClick(refreshAction)
                            .setMinimumClickableWidth(DimensionBuilders.dp(52f))
                            .setMinimumClickableHeight(DimensionBuilders.dp(52f))
                            .setVisualFeedbackEnabled(false)
                            .build()
                    )
                    .setSemantics(
                        ModifiersBuilders.Semantics.Builder()
                            .setContentDescription("Actualiser")
                            .build()
                    )
                    .build()
            )
            .build()

        val content = LayoutElementBuilders.Column.Builder()
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .addContent(value)
            .addContent(
                LayoutElementBuilders.Spacer.Builder()
                    .setHeight(DimensionBuilders.dp(8f))
                    .build()
            )
            .addContent(unit)
            .addContent(
                LayoutElementBuilders.Spacer.Builder()
                    .setHeight(DimensionBuilders.dp(2f))
                    .build()
            )
            .addContent(refreshButton)
            .build()

        val root = LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setBackground(
                        ModifiersBuilders.Background.Builder()
                            .setColor(ColorBuilders.argb(TILE_BG))
                            .build()
                    )
                    .build()
            )
            .addContent(content)
            .build()

        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder()
                            .setLayout(Layout.Builder().setRoot(root).build())
                            .build()
                    )
                    .build()
            )
            .build()

        return Futures.immediateFuture(tile)
    }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .addIdToImageMapping(
                    RES_REFRESH_ICON,
                    ResourceBuilders.ImageResource.Builder()
                        .setAndroidResourceByResId(
                            ResourceBuilders.AndroidImageResourceByResId.Builder()
                                .setResourceId(R.drawable.ic_tile_refresh_reference)
                                .build()
                        )
                        .build()
                )
                .build(),
        )
    }

    private fun requestPhoneRefresh(
        cache: GlucoseCache,
        healthMonitor: WatchSyncHealthMonitor,
    ) {
        cache.markRefreshPending()
        healthMonitor.updateAndReport()
        TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)

        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                val node = nodes.firstOrNull()
                if (node == null) {
                    cache.markRefreshFailed("Telephone indisponible")
                    healthMonitor.updateAndReport()
                    TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)
                    return@addOnSuccessListener
                }

                Wearable.getMessageClient(this)
                    .sendMessage(node.id, GlucoseKeys.PATH_REFRESH_REQUEST, ByteArray(0))
                    .addOnFailureListener {
                        cache.markRefreshFailed("Echec de synchro")
                        healthMonitor.updateAndReport()
                        TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)
                    }
            }
            .addOnFailureListener {
                cache.markRefreshFailed("Telephone indisponible")
                healthMonitor.updateAndReport()
                TileService.getUpdater(this).requestUpdate(GlucoseTileService::class.java)
            }
    }

    private companion object {
        private const val RES_REFRESH_ICON = "tile_refresh_reference"
        private const val REFRESH_CLICK_ID = "refresh"
        private const val TILE_BG = 0xFF0A1A16.toInt()
        private const val TILE_TEXT = 0xFFF7FBFA.toInt()
        private const val TILE_ACCENT = 0xFF35E995.toInt()
    }
}
