package com.widgetg7.mobile.sync

import android.content.Context
import com.widgetg7.feature.sync.RefreshStatusPublisher
import com.widgetg7.mobile.watch.WatchConnectionRepository

/** Publishes refresh progress, failure, and completion to the watch via Data Layer. */
class PhoneWearRefreshStatusService(private val context: Context) {
    private val publisher = RefreshStatusPublisher(context) {
        WatchConnectionRepository(context).loadStatus()
            .takeIf { it.connected }
            ?.nodeId
    }

    suspend fun pushInProgress() = publisher.pushInProgress()

    suspend fun pushFailure(message: String) = publisher.pushFailure(message)

    suspend fun pushCompleted(message: String = "") = publisher.pushCompleted(message)
}
