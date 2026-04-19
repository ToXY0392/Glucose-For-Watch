package com.widgetg7.wear.tile

import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.widgetg7.wear.data.GlucoseCache

private const val RESOURCES_VERSION = "1"

class GlucoseTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest,
    ): ListenableFuture<TileBuilders.Tile> {
        val snapshot = GlucoseCache(this).load()
        val valueText = snapshot?.valueMgDl?.toString() ?: "--"
        val valueColor = snapshot?.semanticColorArgb() ?: 0xFFA7B0BA.toInt()
        val metadataColor = snapshot?.metadataColorArgb() ?: 0xFFA7B0BA.toInt()
        val trendText = when {
            snapshot == null -> "No data"
            snapshot.stale -> "stale"
            else -> "mg/dL ${snapshot.trendArrow()}"
        }

        val value = LayoutElementBuilders.Text.Builder()
            .setText(valueText)
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(44f))
                    .setColor(ColorBuilders.argb(valueColor))
                    .build()
            )
            .setMaxLines(1)
            .build()

        val metadata = LayoutElementBuilders.Text.Builder()
            .setText(trendText)
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(14f))
                    .setColor(ColorBuilders.argb(metadataColor))
                    .build()
            )
            .setMaxLines(1)
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setPadding(
                        ModifiersBuilders.Padding.Builder()
                            .setTop(DimensionBuilders.dp(8f))
                            .build()
                    )
                    .build()
            )
            .build()

        val root = LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setPadding(
                        ModifiersBuilders.Padding.Builder()
                            .setStart(DimensionBuilders.dp(20f))
                            .setEnd(DimensionBuilders.dp(20f))
                            .setTop(DimensionBuilders.dp(28f))
                            .setBottom(DimensionBuilders.dp(20f))
                            .build()
                    )
                    .build()
            )
            .addContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(value)
                    .addContent(metadata)
                    .build()
            )
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

    override fun onResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build(),
        )
    }
}
