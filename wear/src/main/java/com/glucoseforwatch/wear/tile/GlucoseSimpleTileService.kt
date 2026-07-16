package com.glucoseforwatch.wear.tile

import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.glucoseforwatch.wear.data.GlucoseCache

/** Glucose tile: AGP-colored value, unit, trend, and sync action. */
class GlucoseSimpleTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest,
    ): ListenableFuture<TileBuilders.Tile> {
        val cache = GlucoseCache(this)
        val snapshot = cache.load()
        val syncLocked = GlucoseSyncCoordinator.isSyncLocked(cache)
        val deviceConfiguration = requestParams.deviceConfiguration

        val tile =
            GlucoseSimpleTileLayout.buildTile(
                context = this,
                snapshot = snapshot,
                syncLocked = syncLocked,
                screenWidthDp = deviceConfiguration.screenWidthDp,
                screenHeightDp = deviceConfiguration.screenHeightDp,
                screenShape = deviceConfiguration.screenShape,
            )

        return Futures.immediateFuture(tile)
    }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(GlucoseSimpleTileLayout.emptyResources())
    }
}
