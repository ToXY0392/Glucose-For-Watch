package com.glucoseforwatch.wear.tile

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.TileBuilders
import com.glucoseforwatch.wear.R
import com.glucoseforwatch.wear.data.GlucoseSnapshot
import com.glucoseforwatch.wear.display.WearGlucoseSurfaceModelFactory

/** ProtoLayout builder for [GlucoseSimpleTileService] and Android Studio tile previews. */
internal object GlucoseSimpleTileLayout {

    fun buildTile(
        context: Context,
        snapshot: GlucoseSnapshot?,
        syncLocked: Boolean,
        screenWidthDp: Int,
        screenHeightDp: Int,
        screenShape: Int,
    ): TileBuilders.Tile {
        val metrics =
            ToxyTileTheme.layoutMetrics(
                screenWidthDp = screenWidthDp,
                screenHeightDp = screenHeightDp,
                screenShape = screenShape,
            )
        val root = buildRoot(context, snapshot, metrics, syncLocked)
        return TileBuilders.Tile.Builder()
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
    }

    fun emptyResources(): ResourceBuilders.Resources =
        ResourceBuilders.Resources.Builder()
            .setVersion(ToxyTileTheme.RESOURCES_VERSION)
            .build()

    fun buildRoot(
        context: Context,
        snapshot: GlucoseSnapshot?,
        metrics: ToxyTileTheme.TileLayoutMetrics,
        syncLocked: Boolean,
    ): LayoutElementBuilders.LayoutElement {
        val display = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)

        // Fixed valueSp from metrics only — never shrink/grow with digit count (layout-shift rule).
        val value =
            LayoutElementBuilders.Text.Builder()
                .setText(display.valueText)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(metrics.valueSp))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setPreferredFontFamilies("monospace")
                        .setColor(ColorBuilders.argb(display.valueColorArgb))
                        .build(),
                )
                .setMaxLines(1)
                .build()

        val valueRow =
            LayoutElementBuilders.Box.Builder()
                .setWidth(DimensionBuilders.dp(metrics.valueRowWidthDp))
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
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setWidth(DimensionBuilders.dp(metrics.trendGapDp))
                        .build(),
                )
                .addContent(buildTrendSlot(display.showTrend, display.trendArrow, display.trendColorArgb, metrics))

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
                .addContent(buildSyncButton(context, metrics, syncLocked))
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

    /**
     * Always occupies [ToxyTileTheme.TileLayoutMetrics.trendSlotWidthDp] ×
     * [ToxyTileTheme.TileLayoutMetrics.trendSlotHeightDp] so metaRow height/width
     * stay identical when [showTrend] is false.
     */
    private fun buildTrendSlot(
        showTrend: Boolean,
        trendArrow: String,
        trendColorArgb: Int,
        metrics: ToxyTileTheme.TileLayoutMetrics,
    ): LayoutElementBuilders.LayoutElement {
        val slot =
            LayoutElementBuilders.Box.Builder()
                .setWidth(DimensionBuilders.dp(metrics.trendSlotWidthDp))
                .setHeight(DimensionBuilders.dp(metrics.trendSlotHeightDp))
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)

        val arrowText = if (showTrend) trendArrow else " "
        val arrowColor = if (showTrend) trendColorArgb else ToxyTileTheme.TRANSPARENT
        slot.addContent(
            LayoutElementBuilders.Text.Builder()
                .setText(arrowText)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(metrics.trendSp))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setColor(ColorBuilders.argb(arrowColor))
                        .build(),
                )
                .setMaxLines(1)
                .build(),
        )
        return slot.build()
    }

    fun buildSyncButton(
        context: Context,
        metrics: ToxyTileTheme.TileLayoutMetrics,
        syncLocked: Boolean,
    ): LayoutElementBuilders.LayoutElement {
        // Label/colors/clickable may change with syncLocked; Box size never does.
        val label =
            if (syncLocked) {
                context.getString(R.string.tile_sync_in_progress)
            } else {
                context.getString(R.string.tile_sync_action)
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
                                .setRadius(DimensionBuilders.dp(ToxyTileTheme.SYNC_BUTTON_HEIGHT_DP / 2f))
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
                                    .setPackageName(context.packageName)
                                    .build(),
                            )
                            .build(),
                    )
                    .build(),
            )
        }

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.dp(ToxyTileTheme.SYNC_BUTTON_WIDTH_DP))
            .setHeight(DimensionBuilders.dp(ToxyTileTheme.SYNC_BUTTON_HEIGHT_DP))
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setModifiers(modifiers.build())
            .addContent(syncLabel)
            .build()
    }
}
