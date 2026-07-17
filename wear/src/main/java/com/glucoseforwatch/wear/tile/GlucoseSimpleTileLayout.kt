package com.glucoseforwatch.wear.tile

import android.content.Context
import androidx.annotation.Keep
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

/** ProtoLayout builder for [GlucoseTileServiceV2] and Android Studio tile previews. */
@Keep
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

    /**
     * Layout is text/ARGB-only (no ProtoLayout Image). Resource map must stay empty —
     * never register drawables here (Wear caches them across versions).
     * Bump [ToxyTileTheme.RESOURCES_VERSION] on every layout/resource contract change.
     */
    fun emptyResources(): ResourceBuilders.Resources {
        // Explicit empty map + fresh version string forces Wear to drop stale Image mappings.
        return ResourceBuilders.Resources.Builder()
            .setVersion(ToxyTileTheme.RESOURCES_VERSION)
            .build()
    }

    fun buildRoot(
        context: Context,
        snapshot: GlucoseSnapshot?,
        metrics: ToxyTileTheme.TileLayoutMetrics,
        syncLocked: Boolean,
    ): LayoutElementBuilders.LayoutElement {
        val display = WearGlucoseSurfaceModelFactory.fromSnapshot(snapshot)
        // AGP ARGB must be opaque 0xAARRGGBB — never theme / Material primary / transparent.
        val glucoseArgb = opaqueArgb(display.valueColorArgb)
        val trendArgb = opaqueArgb(display.trendColorArgb)
        val trendColorProp = ColorBuilders.ColorProp.Builder(trendArgb).build()
        val unitColorProp = ColorBuilders.ColorProp.Builder(ToxyTileTheme.UNIT_TEXT).build()

        val value =
            LayoutElementBuilders.Text.Builder()
                .setText(display.valueText)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(metrics.valueSp))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                        .setPreferredFontFamilies("monospace")
                        .setColor(ColorBuilders.ColorProp.Builder(glucoseArgb).build())
                        .build(),
                )
                .setMaxLines(1)
                .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
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
                                .setColor(unitColorProp)
                                .build(),
                        )
                        .setMaxLines(1)
                        .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
                        .build(),
                )
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setWidth(DimensionBuilders.dp(metrics.trendGapDp))
                        .build(),
                )
                .addContent(
                    buildTrendSlot(
                        showTrend = display.showTrend,
                        trendArrow = display.trendArrow,
                        trendColorProp = trendColorProp,
                        metrics = metrics,
                    ),
                )
                .build()

        val metaRowCentered =
            LayoutElementBuilders.Box.Builder()
                .setWidth(DimensionBuilders.expand())
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(metaRow)
                .build()

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
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.expand())
                        .build(),
                )
                .addContent(valueRow)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(metrics.valueMetaGapDp))
                        .build(),
                )
                .addContent(metaRowCentered)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.expand())
                        .build(),
                )
                .addContent(buildSyncAction(context, syncLocked))
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(DimensionBuilders.dp(metrics.bottomPadDp))
                        .build(),
                )
                .build()

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
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
                            .setColor(ColorBuilders.ColorProp.Builder(ToxyTileTheme.BACKGROUND).build())
                            .build(),
                    )
                    .build(),
            )
            .addContent(contentColumn)
            .build()
    }

    private fun buildTrendSlot(
        showTrend: Boolean,
        trendArrow: String,
        trendColorProp: ColorBuilders.ColorProp,
        metrics: ToxyTileTheme.TileLayoutMetrics,
    ): LayoutElementBuilders.LayoutElement {
        val arrowText = if (showTrend && trendArrow.isNotEmpty() && trendArrow != "?") {
            trendArrow
        } else {
            ""
        }
        val colorProp =
            if (arrowText.isNotEmpty()) {
                trendColorProp
            } else {
                ColorBuilders.ColorProp.Builder(ToxyTileTheme.TRANSPARENT).build()
            }

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.dp(metrics.trendSlotWidthDp))
            .setHeight(DimensionBuilders.dp(metrics.trendSlotHeightDp))
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(arrowText)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setSize(DimensionBuilders.sp(metrics.trendSp))
                            .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                            .setColor(colorProp)
                            .build(),
                    )
                    .setMaxLines(1)
                    .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
                    .build(),
            )
            .build()
    }

    /** Force opaque alpha so ProtoLayout never paints white/transparent AGP values. */
    private fun opaqueArgb(argb: Int): Int =
        if ((argb ushr 24) == 0) argb or 0xFF000000.toInt() else argb

    fun buildSyncAction(
        context: Context,
        syncLocked: Boolean,
    ): LayoutElementBuilders.LayoutElement {
        val label =
            if (syncLocked) {
                context.getString(R.string.tile_sync_in_progress)
            } else {
                context.getString(R.string.tile_sync_action)
            }
        val textColor =
            if (syncLocked) ToxyTileTheme.SYNC_LOCKED_ACCENT else ToxyTileTheme.SYNC_ACCENT

        val syncLabel =
            LayoutElementBuilders.Text.Builder()
                .setText(label)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(ToxyTileTheme.SYNC_TEXT_SP))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_MEDIUM)
                        .setColor(ColorBuilders.ColorProp.Builder(textColor).build())
                        .build(),
                )
                .setMaxLines(1)
                .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
                .build()

        val box =
            LayoutElementBuilders.Box.Builder()
                .setWidth(DimensionBuilders.wrap())
                .setHeight(DimensionBuilders.dp(ToxyTileTheme.SYNC_TEXT_SLOT_HEIGHT_DP))
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(syncLabel)

        if (!syncLocked) {
            box.setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(
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
                    .build(),
            )
        }

        return box.build()
    }
}
