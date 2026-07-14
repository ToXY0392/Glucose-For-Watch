package com.glucoseforwatch.wear.tile

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable
import com.glucoseforwatch.wear.data.GlucoseCache
import com.glucoseforwatch.wear.data.GlucoseKeys
import com.glucoseforwatch.wear.sync.PhoneNode
import com.glucoseforwatch.wear.sync.PhoneTargetResolver
import com.glucoseforwatch.wear.sync.WatchSyncHealthMonitor

/** Sends refresh request to phone; activity exits immediately after dispatching here. */
internal object GlucoseSyncRequestExecutor {
    private const val TAG = "WG7.WearDataLayer"

    fun dispatch(context: Context) {
        val appContext = context.applicationContext
        val cache = GlucoseCache(appContext)
        val healthMonitor = WatchSyncHealthMonitor(appContext)

        Wearable.getNodeClient(appContext).connectedNodes
            .addOnSuccessListener { nodes ->
                val phoneNodes = nodes.map { PhoneNode(it.id, it.displayName, it.isNearby) }
                val nodeId = PhoneTargetResolver.selectPhoneNodeId(phoneNodes, cache.lastPhoneNodeId())
                if (nodeId == null) {
                    fail(appContext, cache, healthMonitor, "Téléphone indisponible")
                    return@addOnSuccessListener
                }

                if (phoneNodes.size > 1 && cache.lastPhoneNodeId() == null) {
                    Log.w(
                        TAG,
                        "multiple_phones_no_history selected=$nodeId candidates=${phoneNodes.map { it.id }}",
                    )
                }

                Wearable.getMessageClient(appContext)
                    .sendMessage(nodeId, GlucoseKeys.PATH_REFRESH_REQUEST, ByteArray(0))
                    .addOnSuccessListener {
                        Log.d(TAG, "refresh_request_sent node=$nodeId")
                    }
                    .addOnFailureListener {
                        fail(appContext, cache, healthMonitor, "Échec de synchro")
                    }
            }
            .addOnFailureListener {
                fail(appContext, cache, healthMonitor, "Téléphone indisponible")
            }
    }

    private fun fail(
        context: Context,
        cache: GlucoseCache,
        healthMonitor: WatchSyncHealthMonitor,
        message: String,
    ) {
        cache.markRefreshFailed(message)
        healthMonitor.updateAndReport()
        GlucoseSyncCoordinator.endSync()
        GlucoseTileUpdateRequester.requestUpdateImmediate(context)
    }
}
