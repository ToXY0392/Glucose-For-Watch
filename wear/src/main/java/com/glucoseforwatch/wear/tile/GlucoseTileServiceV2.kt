package com.glucoseforwatch.wear.tile

import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.glucoseforwatch.wear.data.GlucoseCache
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class GlucoseTileServiceV2 : TileService() {

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        val context = applicationContext
        val cache = GlucoseCache(context)
        val snapshot = cache.load()
        val syncLocked = GlucoseSyncCoordinator.isSyncLocked(cache)
        
        val deviceConfig = requestParams.deviceConfiguration
        val screenWidthDp = deviceConfig.screenWidthDp
        val screenHeightDp = deviceConfig.screenHeightDp
        val screenShape = deviceConfig.screenShape

        val tile = GlucoseSimpleTileLayout.buildTile(
            context = context,
            snapshot = snapshot,
            syncLocked = syncLocked,
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp,
            screenShape = screenShape
        )

        return Futures.immediateFuture(tile)
    }

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        val protoResources = GlucoseSimpleTileLayout.emptyResources()
        val resources = ResourceBuilders.Resources.Builder()
            .setVersion(protoResources.version)
            .build()
        return Futures.immediateFuture(resources)
    }
}