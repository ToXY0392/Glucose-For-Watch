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

/**
 * Static ProtoLayout for [GlucoseTileServiceV2].
 *
 * Tree shape is fixed every frame (no conditional Row/Column/Box creation).
 * Missing data fills slots with neutral placeholders; colors are explicit ARGB
 * from [GlucoseTileChrome] (no Toxy / Material theme defaults).
 */
@Keep
internal object GlucoseSimpleTileLayout {

    fun buildTile(
        context: Context,
        snapshot: GlucoseSnapshot?,
        syncLocked: Boolean,
        screenWidthDp: Int,
        screenHeightDp: Int,
        screenShape: Int,
        nowEpochMs: Long = System.currentTimeMillis(),
    ): TileBuilders.Tile {
        val metrics =
            GlucoseTileChrome.layoutMetrics(
                screenWidthDp = screenWidthDp,
                screenHeightDp = screenHeightDp,
                screenShape = screenShape,
            )
        val root = buildRoot(context, snapshot, metrics, syncLocked, nowEpochMs)
        return TileBuilders.Tile.Builder()
            .setResourcesVersion(GlucoseTileChrome.RESOURCES_VERSION)
            .setFreshnessIntervalMillis(GlucoseTileChrome.FRESHNESS_INTERVAL_MS)
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
     * Bump [GlucoseTileChrome.RESOURCES_VERSION] on every layout/resource contract change.
     */
    fun emptyResources(): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder()
            .setVersion(GlucoseTileChrome.RESOURCES_VERSION)
            .build()
    }

    fun buildRoot(
        context: Context,
        snapshot: GlucoseSnapshot?,
        metrics: GlucoseTileChrome.TileLayoutMetrics,
        syncLocked: Boolean,
        nowEpochMs: Long = System.currentTimeMillis(),
    ): LayoutElementBuilders.LayoutElement {
        val presentation =
            GlucoseTileChrome.presentation(
                snapshot = snapshot,
                syncLocked = syncLocked,
                staleStatusLabel = context.getString(R.string.wear_status_stale),
                syncActionLabel = context.getString(R.string.tile_sync_action),
                syncInProgressLabel = context.getString(R.string.tile_sync_in_progress),
                nowEpochMs = nowEpochMs,
            )

        val valueColor = ColorBuilders.ColorProp.Builder(presentation.valueArgb).build()
        val unitColor = ColorBuilders.ColorProp.Builder(presentation.unitArgb).build()
        val trendColor = ColorBuilders.ColorProp.Builder(presentation.trendArgb).build()
        val statusColor = ColorBuilders.ColorProp.Builder(presentation.statusArgb).build()

        // --- Fixed slots (always present) ---
        val valueSlot =
            LayoutElementBuilders.Box.Builder()
                .setWidth(DimensionBuilders.dp(metrics.valueRowWidthDp))
                .setHeight(DimensionBuilders.dp(metrics.valueRowHeightDp))
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(
                    LayoutElementBuilders.Text.Builder()
                        .setText(presentation.valueText)
                        .setFontStyle(
                            LayoutElementBuilders.FontStyle.Builder()
                                .setSize(DimensionBuilders.sp(metrics.valueSp))
                                .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                                .setPreferredFontFamilies("monospace")
                                .setColor(valueColor)
                                .build(),
                        )
                        .setMaxLines(1)
                        .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
                        .build(),
                )
                .build()

        val unitText =
            LayoutElementBuilders.Text.Builder()
                .setText(presentation.unitLabel)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(metrics.unitSp))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_MEDIUM)
                        .setColor(unitColor)
                        .build(),
                )
                .setMaxLines(1)
                .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
                .build()

        val trendSlot =
            LayoutElementBuilders.Box.Builder()
                .setWidth(DimensionBuilders.dp(metrics.trendSlotWidthDp))
                .setHeight(DimensionBuilders.dp(metrics.trendSlotHeightDp))
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(
                    LayoutElementBuilders.Text.Builder()
                        .setText(presentation.trendText)
                        .setFontStyle(
                            LayoutElementBuilders.FontStyle.Builder()
                                .setSize(DimensionBuilders.sp(metrics.trendSp))
                                .setWeight(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                                .setColor(trendColor)
                                .build(),
                        )
                        .setMaxLines(1)
                        .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
                        .build(),
                )
                .build()

        val metaRow =
            LayoutElementBuilders.Row.Builder()
                .setWidth(DimensionBuilders.wrap())
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(unitText)
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setWidth(DimensionBuilders.dp(metrics.trendGapDp))
                        .build(),
                )
                .addContent(trendSlot)
                .build()

        val metaRowCentered =
            LayoutElementBuilders.Box.Builder()
                .setWidth(DimensionBuilders.expand())
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                .addContent(metaRow)
                .build()

        val statusSlot = buildStatusSlot(context, presentation, statusColor)

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
                .addContent(valueSlot)
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
                .addContent(statusSlot)
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
                            .setColor(
                                ColorBuilders.ColorProp.Builder(GlucoseTileChrome.BACKGROUND).build(),
                            )
                            .build(),
                    )
                    .build(),
            )
            .addContent(contentColumn)
            .build()
    }

    /**
     * Bottom status / sync slot — always the same Box + Text + Clickable tree.
     * Label text is data-driven (sync / sync… / Donnée périmée).
     */
    private fun buildStatusSlot(
        context: Context,
        presentation: GlucoseTileChrome.TilePresentation,
        statusColor: ColorBuilders.ColorProp,
    ): LayoutElementBuilders.LayoutElement {
        val statusLabel =
            LayoutElementBuilders.Text.Builder()
                .setText(presentation.statusText)
                .setFontStyle(
                    LayoutElementBuilders.FontStyle.Builder()
                        .setSize(DimensionBuilders.sp(GlucoseTileChrome.SYNC_TEXT_SP))
                        .setWeight(LayoutElementBuilders.FONT_WEIGHT_MEDIUM)
                        .setColor(statusColor)
                        .build(),
                )
                .setMaxLines(1)
                .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
                .build()

        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.wrap())
            .setHeight(DimensionBuilders.dp(GlucoseTileChrome.STATUS_SLOT_HEIGHT_DP))
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setClickable(
                        ModifiersBuilders.Clickable.Builder()
                            .setId(GlucoseTileChrome.SYNC_CLICK_ID)
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
            .addContent(statusLabel)
            .build()
    }
}
