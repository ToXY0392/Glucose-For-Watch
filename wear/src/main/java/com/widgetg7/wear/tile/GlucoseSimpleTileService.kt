package com.widgetg7.wear.tile

import android.os.SystemClock
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
import com.widgetg7.wear.complication.ComplicationUpdateNotifier
import com.widgetg7.wear.data.GlucoseCache
import com.widgetg7.wear.data.GlucoseSnapshot
import com.widgetg7.wear.display.WearGlucoseSurfaceModelFactory
import com.widgetg7.wear.sync.WatchSyncHealthMonitor

/** Glucose tile: AGP-colored value, unit, trend, and sync action. */
class GlucoseSimpleTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest,
    ): ListenableFuture<TileBuilders.Tile> {
        requestComplicationsRefreshThrottled()

        val cache = GlucoseCache(this)
        val healthMonitor = WatchSyncHealthMonitor(this)
        val snapshot = cache.load()
        healthMonitor.updateAndReport(System.currentTimeMillis())

        val root = buildRoot(snapshot)

        val tile =
            TileBuilders.Tile.Builder()
                .setResourcesVersion(ToxyTileTheme.RESOURCES_VERSION)
                .setFreshnessIntervalMillis(ToxyTileTheme.FRESHNESS_INTERVAL_MS)
                .setTileTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(Layout.Builder().setRoot(root).build())
                                .build(),
                        )
                        .build(),
                )
                .build()

        return Futures.immediateFuture(tile)
    }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(ToxyTileTheme.RESOURCES_VERSION)
                .build(),
        )
    }

    private fun buildRoot(snapshot: GlucoseSnapshot?): LayoutElementBuilders.LayoutElement {
        val display = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)
        val valueText = display.valueText
        val trendArrow = display.trendArrow
        val showTrend = display.showTrend
        val valueColor = display.valueColorArgb
        val trendColor = display.trendColorArgb

        val value =
            LayoutElementBuilders.Text.Builder()
                .setText(valueText)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(56f))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setColor(ColorBuilders.argb(valueColor))
                        .build(),
                )
                .setMaxLines(1)
                .build()

        val unit =
            LayoutElementBuilders.Text.Builder()
                .setText("mg/dL")
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(18f))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setColor(ColorBuilders.argb(ToxyTileTheme.UNIT_TEXT))
                        .build(),
                )
                .setMaxLines(1)
                .build()

        val metaRow = LayoutElementBuilders.Row.Builder()
        metaRow.addContent(unit)
        if (showTrend) {
            metaRow.addContent(
                LayoutElementBuilders.Spacer.Builder()
                    .setWidth(DimensionBuilders.dp(14f))
                    .build(),
            )
            metaRow.addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(trendArrow)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.sp(32f))
                            .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                            .setColor(ColorBuilders.argb(trendColor))
                            .build(),
                    )
                    .setMaxLines(1)
                    .build(),
            )
        }

        val readingColumn =
            LayoutElementBuilders.Column.Builder()
                .setWidth(DimensionBuilders.expand())
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .addContent(value)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(10f))
                        .build(),
                )
                .addContent(metaRow.build())
                .build()

        val syncButton = buildSyncButton()

        val contentColumn =
            LayoutElementBuilders.Column.Builder()
                .setWidth(DimensionBuilders.expand())
                .setHeight(DimensionBuilders.expand())
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(8f))
                        .build(),
                )
                .addContent(readingColumn)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.expand())
                        .build(),
                )
                .addContent(syncButton)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(12f))
                        .build(),
                )
                .build()

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setBackground(
                        ModifiersBuilders.Background.Builder()
                            .setColor(ColorBuilders.argb(ToxyTileTheme.BACKGROUND))
                            .build(),
                    )
                    .build(),
            )
            .addContent(contentColumn)
            .build()
    }

    private fun buildSyncButton(): LayoutElementBuilders.LayoutElement {
        val syncLabel =
            LayoutElementBuilders.Text.Builder()
                .setText("\u21BB Sync")
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(16f))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setColor(ColorBuilders.argb(ToxyTileTheme.SYNC_ACCENT))
                        .build(),
                )
                .setMaxLines(1)
                .build()

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.dp(48f))
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setBackground(
                        ModifiersBuilders.Background.Builder()
                            .setColor(ColorBuilders.argb(ToxyTileTheme.SYNC_BG))
                            .setCorner(
                                ModifiersBuilders.Corner.Builder()
                                    .setRadius(DimensionBuilders.dp(24f))
                                    .build(),
                            )
                            .build(),
                    )
                    .setClickable(
                        ModifiersBuilders.Clickable.Builder()
                            .setId(ToxyTileTheme.SYNC_CLICK_ID)
                            .setOnClick(
                                ActionBuilders.LaunchAction.Builder()
                                    .setAndroidActivity(
                                        ActionBuilders.AndroidActivity.Builder()
                                            .setClassName(GlucoseRefreshActivity::class.java.name)
                                            .setPackageName(packageName)
                                            .build(),
                                    )
                                    .build(),
                            )
                            .build(),
                    )
                    .build(),
            )
            .addContent(syncLabel)
            .build()
    }

    private fun requestComplicationsRefreshThrottled() {
        val now = SystemClock.elapsedRealtime()
        synchronized(tileComplicationRefreshLock) {
            if (now - lastTileComplicationRefreshElapsedMs < ToxyTileTheme.COMPLICATION_REFRESH_INTERVAL_MS) return
            lastTileComplicationRefreshElapsedMs = now
        }
        ComplicationUpdateNotifier.requestUpdateAll(this)
    }

    private companion object {
        private val tileComplicationRefreshLock = Any()
        private var lastTileComplicationRefreshElapsedMs = 0L
    }
}
