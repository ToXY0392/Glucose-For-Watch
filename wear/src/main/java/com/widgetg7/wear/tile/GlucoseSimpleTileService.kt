package com.widgetg7.wear.tile

import android.os.SystemClock
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
import com.widgetg7.wear.complication.ComplicationUpdateNotifier
import com.widgetg7.wear.sync.WatchSyncHealthMonitor

/** Tuile glycémie minimaliste : valeur, mg/dL, flèche de tendance (sans libellé « Tendance »). */
class GlucoseSimpleTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest,
    ): ListenableFuture<TileBuilders.Tile> {
        requestComplicationsRefreshThrottled()

        val cache = GlucoseCache(this)
        val healthMonitor = WatchSyncHealthMonitor(this)
        val snapshot = cache.load()
        healthMonitor.updateAndReport(System.currentTimeMillis())

        val valueText = snapshot?.displayValueText() ?: "--"
        val trendArrow = snapshot?.trendArrow().orEmpty()
        val showTrend = snapshot != null && trendArrow.isNotEmpty()
        val trendStale = snapshot?.stale == true

        val root =
            buildRoot(
                valueText = valueText,
                trendArrow = trendArrow,
                showTrend = showTrend,
                trendStale = trendStale,
            )

        val tile =
            TileBuilders.Tile.Builder()
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

    private fun buildRoot(
        valueText: String,
        trendArrow: String,
        showTrend: Boolean,
        trendStale: Boolean,
    ): LayoutElementBuilders.LayoutElement {
        val trendColor = if (trendStale) TILE_TREND_STALE else TILE_ACCENT

        val value =
            LayoutElementBuilders.Text.Builder()
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

        val unit =
            LayoutElementBuilders.Text.Builder()
                .setText("mg/dL")
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(18f))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setColor(ColorBuilders.argb(TILE_ACCENT))
                        .build()
                )
                .setMaxLines(1)
                .build()

        val metaRow = LayoutElementBuilders.Row.Builder()
        metaRow.addContent(unit)
        if (showTrend) {
            metaRow.addContent(
                LayoutElementBuilders.Spacer.Builder()
                    .setWidth(DimensionBuilders.dp(14f))
                    .build()
            )
            metaRow.addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(trendArrow)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.sp(32f))
                            .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                            .setColor(ColorBuilders.argb(trendColor))
                            .build()
                    )
                    .setMaxLines(1)
                    .build()
            )
        }

        val column =
            LayoutElementBuilders.Column.Builder()
                .setWidth(DimensionBuilders.expand())
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .addContent(value)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(10f))
                        .build()
                )
                .addContent(metaRow.build())
                .build()

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setBackground(
                        ModifiersBuilders.Background.Builder()
                            .setColor(ColorBuilders.argb(TILE_BG))
                            .build()
                    )
                    .build()
            )
            .addContent(column)
            .build()
    }

    private fun requestComplicationsRefreshThrottled() {
        val now = SystemClock.elapsedRealtime()
        synchronized(tileComplicationRefreshLock) {
            if (now - lastTileComplicationRefreshElapsedMs < TILE_COMPLICATION_REFRESH_INTERVAL_MS) return
            lastTileComplicationRefreshElapsedMs = now
        }
        ComplicationUpdateNotifier.requestUpdateAll(this)
    }

    private companion object {
        private val tileComplicationRefreshLock = Any()
        private var lastTileComplicationRefreshElapsedMs = 0L
        private const val TILE_COMPLICATION_REFRESH_INTERVAL_MS = 45_000L

        private const val RESOURCES_VERSION = "simple-tile-v5-nocturne-pro"
        private const val TILE_BG = 0xFF0D1117.toInt()
        private const val TILE_TEXT = 0xFFF8FAFC.toInt()
        private const val TILE_ACCENT = 0xFF34D399.toInt()
        private const val TILE_TREND_STALE = 0xFF64748B.toInt()
    }
}
