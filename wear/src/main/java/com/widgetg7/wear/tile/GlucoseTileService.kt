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

private const val RESOURCES_VERSION = "2"

class GlucoseTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest,
    ): ListenableFuture<TileBuilders.Tile> {
        val cache = GlucoseCache(this)
        val healthMonitor = WatchSyncHealthMonitor(this)
        val snapshot = cache.load()
        val nowEpochMs = System.currentTimeMillis()
        val watchHealth = healthMonitor.updateAndReport(nowEpochMs)
        val refreshStatus = cache.loadRefreshStatus(nowEpochMs)
        val valueText = snapshot?.valueMgDl?.toString() ?: "--"
        val valueColor = snapshot?.semanticColorArgb() ?: 0xFFA7B0BA.toInt()
        val metadataColor = snapshot?.metadataColorArgb() ?: 0xFFA7B0BA.toInt()
        val statusText = when {
            watchHealth.syncLimited -> watchHealth.message
            refreshStatus != null -> refreshStatus.displayText(nowEpochMs)
            snapshot == null -> "No data"
            else -> snapshot.secondaryLabel()
        }
        val refreshAction = ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity.Builder()
                    .setPackageName(packageName)
                    .setClassName(ComponentName(this, GlucoseRefreshActivity::class.java).className)
                    .build()
            )
            .build()

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
            .setText(statusText)
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(14f))
                    .setColor(ColorBuilders.argb(metadataColor))
                    .build()
            )
            .setMaxLines(2)
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

        val refreshGlyph = LayoutElementBuilders.Text.Builder()
            .setText("\u21BB")
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.sp(20f))
                    .setColor(ColorBuilders.argb(0xFFF5F7FA.toInt()))
                    .build()
            )
            .setMaxLines(1)
            .build()

        val refreshButton = LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.dp(44f))
            .setHeight(DimensionBuilders.dp(44f))
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(
                        ModifiersBuilders.Clickable.Builder()
                            .setId("refresh")
                            .setOnClick(refreshAction)
                            .build()
                    )
                    .setSemantics(
                        ModifiersBuilders.Semantics.Builder()
                            .setContentDescription("Actualiser")
                            .build()
                    )
                    .setBackground(
                        ModifiersBuilders.Background.Builder()
                            .setColor(ColorBuilders.argb(0x3348D1CC))
                            .setCorner(
                                ModifiersBuilders.Corner.Builder()
                                    .setRadius(DimensionBuilders.dp(22f))
                                    .build()
                            )
                            .build()
                    )
                    .setPadding(
                        ModifiersBuilders.Padding.Builder()
                            .setTop(DimensionBuilders.dp(14f))
                            .build()
                    )
                    .build()
            )
            .addContent(refreshGlyph)
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
