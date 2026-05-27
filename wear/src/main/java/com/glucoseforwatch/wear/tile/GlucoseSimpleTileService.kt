package com.glucoseforwatch.wear.tile

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
import com.glucoseforwatch.wear.data.GlucoseCache
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import com.glucoseforwatch.wear.display.WearGlucoseSurfaceModelFactory
import com.glucoseforwatch.wear.R

/** Glucose tile: AGP-colored value, unit, trend, and sync action. */
class GlucoseSimpleTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest,
    ): ListenableFuture<TileBuilders.Tile> {
        val cache = GlucoseCache(this)
        val snapshot = cache.load()
        val syncLocked = GlucoseSyncCoordinator.isSyncLocked(cache)

        val deviceConfiguration = requestParams.deviceConfiguration
        val metrics =
            ToxyTileTheme.layoutMetrics(
                screenWidthDp = deviceConfiguration.screenWidthDp,
                screenHeightDp = deviceConfiguration.screenHeightDp,
                screenShape = deviceConfiguration.screenShape,
            )
        val root = buildRoot(snapshot, metrics, syncLocked)

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

    private fun buildRoot(
        snapshot: GlucoseSnapshot?,
        metrics: ToxyTileTheme.TileLayoutMetrics,
        syncLocked: Boolean,
    ): LayoutElementBuilders.LayoutElement {
        val display = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)

        val value =
            LayoutElementBuilders.Text.Builder()
                .setText(display.valueText)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(metrics.valueSp))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setColor(ColorBuilders.argb(display.valueColorArgb))
                        .build(),
                )
                .setMaxLines(1)
                .build()

        val valueRow =
            LayoutElementBuilders.Box.Builder()
                .setWidth(DimensionBuilders.expand())
                .setHeight(DimensionBuilders.dp(metrics.valueRowHeightDp))
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(value)
                .build()

        val metaRow =
            LayoutElementBuilders.Row.Builder()
                .setWidth(DimensionBuilders.wrap())
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(
                    LayoutElementBuilders.Text.Builder()
                        .setText(display.unitLabel)
                        .setFontStyle(
                            LayoutElementBuilders.FontStyle.Builder()
                                .setSize(DimensionBuilders.sp(metrics.unitSp))
                                .setWeight(LayoutElementBuilders.FONT_WEIGHT_MEDIUM)
                                .setColor(ColorBuilders.argb(ToxyTileTheme.UNIT_TEXT))
                                .build(),
                        )
                        .setMaxLines(1)
                        .build(),
                )
        if (display.showTrend) {
            metaRow.addContent(
                LayoutElementBuilders.Spacer.Builder()
                    .setWidth(DimensionBuilders.dp(metrics.trendGapDp))
                    .build(),
            )
            metaRow.addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(display.trendArrow)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.sp(metrics.trendSp))
                            .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                            .setColor(ColorBuilders.argb(display.trendColorArgb))
                            .build(),
                    )
                    .setMaxLines(1)
                    .build(),
            )
        }

        val contentColumn =
            LayoutElementBuilders.Column.Builder()
                .setWidth(DimensionBuilders.expand())
                .setHeight(DimensionBuilders.expand())
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(metrics.topPadDp))
                        .build(),
                )
                .addContent(valueRow)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(metrics.valueMetaGapDp))
                        .build(),
                )
                .addContent(metaRow.build())
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.expand())
                        .build(),
                )
                .addContent(buildSyncButton(metrics, syncLocked))
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(metrics.bottomPadDp))
                        .build(),
                )
                .build()

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setPadding(
                        ModifiersBuilders.Padding.Builder()
                            .setStart(DimensionBuilders.dp(metrics.horizontalPadDp))
                            .setEnd(DimensionBuilders.dp(metrics.horizontalPadDp))
                            .build(),
                    )
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

    private fun buildSyncButton(
        metrics: ToxyTileTheme.TileLayoutMetrics,
        syncLocked: Boolean,
    ): LayoutElementBuilders.LayoutElement {
        val label =
            if (syncLocked) {
                getString(R.string.tile_sync_in_progress)
            } else {
                getString(R.string.tile_sync_action)
            }
        val textColor = if (syncLocked) ToxyTileTheme.SYNC_LOCKED_ACCENT else ToxyTileTheme.SYNC_ACCENT
        val bgColor = if (syncLocked) ToxyTileTheme.SYNC_LOCKED_BG else ToxyTileTheme.SYNC_BG

        val syncLabel =
            LayoutElementBuilders.Text.Builder()
                .setText(label)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(14f))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setColor(ColorBuilders.argb(textColor))
                        .build(),
                )
                .setMaxLines(1)
                .build()

        val modifiers =
            ModifiersBuilders.Modifiers.Builder()
                .setBackground(
                    ModifiersBuilders.Background.Builder()
                        .setColor(ColorBuilders.argb(bgColor))
                        .setCorner(
                            ModifiersBuilders.Corner.Builder()
                                .setRadius(DimensionBuilders.dp(metrics.syncButtonHeightDp / 2f))
                                .build(),
                        )
                        .build(),
                )

        if (!syncLocked) {
            modifiers.setClickable(
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
        }

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.dp(metrics.syncButtonWidthDp))
            .setHeight(DimensionBuilders.dp(metrics.syncButtonHeightDp))
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setModifiers(modifiers.build())
            .addContent(syncLabel)
            .build()
    }
}
