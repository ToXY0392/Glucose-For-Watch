package com.widgetg7.wear.tile

import android.content.ComponentName
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
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.sync.WatchSyncHealthMonitor

private const val RESOURCES_VERSION = "9"

class GlucoseTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest,
    ): ListenableFuture<TileBuilders.Tile> {
        val cache = GlucoseCache(this)
        val healthMonitor = WatchSyncHealthMonitor(this)
        val snapshot = cache.load()
        val nowEpochMs = System.currentTimeMillis()
        healthMonitor.updateAndReport(nowEpochMs)
        val valueText = snapshot?.displayValueText() ?: "--"
        val trendText = snapshot?.takeUnless { it.stale }?.trendArrow().orEmpty()
        val valueColor = snapshot?.semanticColorArgb() ?: 0xFFA7B0BA.toInt()
        val metadataColor = snapshot?.metadataColorArgb() ?: 0xFFA7B0BA.toInt()
        val statusText = when {
            snapshot == null -> "No data"
            snapshot.stale -> snapshot.trendOnlyLabel()
            else -> ""
        }
        val refreshAction = ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity.Builder()
                    .setPackageName(packageName)
                    .setClassName(ComponentName(this, GlucoseRefreshActivity::class.java).className)
                    .build()
            )
            .build()

        val value = LayoutElementBuilders.Row.Builder()
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(valueText)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.sp(44f))
                            .setColor(ColorBuilders.argb(valueColor))
                            .build()
                    )
                    .setMaxLines(1)
                    .build()
            )
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(trendText)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.sp(22f))
                            .setColor(ColorBuilders.argb(metadataColor))
                            .build()
                    )
                    .setMaxLines(1)
                    .setModifiers(
                        ModifiersBuilders.Modifiers.Builder()
                            .setPadding(
                                ModifiersBuilders.Padding.Builder()
                                    .setStart(DimensionBuilders.dp(6f))
                                    .setTop(DimensionBuilders.dp(4f))
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        val unit = LayoutElementBuilders.Text.Builder()
            .setText("mg/dL")
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(14f))
                    .setColor(ColorBuilders.argb(metadataColor))
                    .build()
            )
            .setMaxLines(1)
            .build()

        val metadata = LayoutElementBuilders.Text.Builder()
            .setText(statusText)
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(13f))
                    .setColor(ColorBuilders.argb(metadataColor))
                    .build()
            )
            .setMaxLines(1)
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setPadding(
                        ModifiersBuilders.Padding.Builder()
                            .setTop(DimensionBuilders.dp(4f))
                            .build()
                    )
                    .build()
            )
            .build()

        val refreshGlyph = LayoutElementBuilders.Text.Builder()
            .setText("SYNC")
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(10f))
                    .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                    .setColor(ColorBuilders.argb(0xFF15936F.toInt()))
                    .build()
            )
            .setMaxLines(1)
            .build()

        val refreshButton = LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.dp(50f))
            .setHeight(DimensionBuilders.dp(26f))
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .addContent(refreshGlyph)
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setBackground(
                        ModifiersBuilders.Background.Builder()
                            .setColor(ColorBuilders.argb(0x2215936F))
                            .setCorner(
                                ModifiersBuilders.Corner.Builder()
                                    .setRadius(DimensionBuilders.dp(13f))
                                    .build()
                            )
                            .build()
                    )
                    .setClickable(
                        ModifiersBuilders.Clickable.Builder()
                            .setId("refresh")
                            .setOnClick(refreshAction)
                            .setMinimumClickableWidth(DimensionBuilders.dp(48f))
                            .setMinimumClickableHeight(DimensionBuilders.dp(48f))
                            .setVisualFeedbackEnabled(false)
                            .build()
                    )
                    .setSemantics(
                        ModifiersBuilders.Semantics.Builder()
                            .setContentDescription("Actualiser")
                            .build()
                    )
                    .setPadding(
                        ModifiersBuilders.Padding.Builder()
                            .setTop(DimensionBuilders.dp(10f))
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
                            .setTop(DimensionBuilders.dp(24f))
                            .setBottom(DimensionBuilders.dp(16f))
                            .build()
                    )
                    .build()
            )
            .addContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(value)
                    .addContent(unit)
                    .addContent(metadata)
                    .addContent(refreshButton)
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

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build(),
        )
    }
}
