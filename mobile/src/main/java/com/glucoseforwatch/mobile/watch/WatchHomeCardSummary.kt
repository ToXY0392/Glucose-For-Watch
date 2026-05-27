package com.glucoseforwatch.mobile.watch

import android.content.Context
import com.glucoseforwatch.mobile.R
import com.glucoseforwatch.mobile.settings.AppSettingsStore
import com.glucoseforwatch.mobile.sync.PhoneSyncStateSnapshot
import com.glucoseforwatch.mobile.ui.HomeSyncPillResolver

/** Subtitle and linked flag for the home watch settings row. */
data class WatchHomeCardState(
    val subtitle: String,
    val linked: Boolean,
)

/** Resolves watch row subtitle from connection, install, and ack state. */
object WatchHomeCardSummary {
    fun resolve(
        context: Context,
        watchStatus: WatchConnectionStatus,
        watchHealth: WatchSyncHealthStatus?,
        syncState: PhoneSyncStateSnapshot,
    ): WatchHomeCardState {
        if (!watchStatus.connected) {
            return WatchHomeCardState(
                subtitle = context.getString(R.string.home_watch_status_off),
                linked = false,
            )
        }

        val name = watchStatus.displayName.ifBlank { context.getString(R.string.home_watch_card_title) }
        if (watchHealth?.appInstalled != true) {
            return WatchHomeCardState(
                subtitle = context.getString(R.string.home_watch_status_install, name),
                linked = false,
            )
        }

        val ackOk = HomeSyncPillResolver.hasWatchAck(syncState)
        return WatchHomeCardState(
            subtitle =
                if (ackOk) {
                    context.getString(R.string.home_watch_status_ok, name)
                } else {
                    context.getString(R.string.home_watch_status_pending, name)
                },
            linked = ackOk,
        )
    }
}
