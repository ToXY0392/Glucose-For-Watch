package com.widgetg7.mobile.ui

import com.widgetg7.core.model.SyncStatusSnapshot
import com.widgetg7.mobile.sync.PhoneSyncStateSnapshot
import com.widgetg7.mobile.watch.WatchConnectionStatus

data class HomeSyncPillLabels(
    val dexcomOff: String,
    val watchUnreachable: String,
    val watchPushPending: String,
    val watchPending: String,
    val watchNotPaired: String,
    val watchInstall: String,
    val syncError: (String) -> String,
    val watchConfirmed: String,
    val syncActive: String,
    val ready: String,
)

enum class HomeSyncPillTone {
    OK,
    WARN,
    ERROR,
    NEUTRAL,
}

object HomeSyncPillResolver {
    const val WATCH_PUSH_FAILURE_THRESHOLD = 3

    fun resolve(
        dexcomConfigured: Boolean,
        activeSync: Boolean,
        syncStatus: SyncStatusSnapshot,
        watchStatus: WatchConnectionStatus,
        watchReady: Boolean,
        syncState: PhoneSyncStateSnapshot,
        watchPushPending: Boolean,
        labels: HomeSyncPillLabels,
    ): String =
        when {
            !dexcomConfigured -> labels.dexcomOff
            watchStatus.connected &&
                syncState.consecutiveWearPushFailures >= WATCH_PUSH_FAILURE_THRESHOLD ->
                labels.watchUnreachable
            watchStatus.connected && watchPushPending -> labels.watchPushPending
            watchStatus.connected &&
                syncState.lastPushSequenceId > 0L &&
                !hasWatchAck(syncState) ->
                labels.watchPending
            !watchStatus.connected -> labels.watchNotPaired
            !watchReady -> labels.watchInstall
            syncStatus.lastError.isNotBlank() ->
                labels.syncError(truncateForStatusPill(syncStatus.lastError))
            hasWatchAck(syncState) && activeSync -> labels.watchConfirmed
            activeSync && syncStatus.hasSuccessfulSync() && !watchPushPending ->
                labels.syncActive
            else -> labels.ready
        }

    fun resolveTone(
        dexcomConfigured: Boolean,
        activeSync: Boolean,
        syncStatus: SyncStatusSnapshot,
        watchStatus: WatchConnectionStatus,
        watchReady: Boolean,
        syncState: PhoneSyncStateSnapshot,
        watchPushPending: Boolean,
    ): HomeSyncPillTone =
        when {
            !dexcomConfigured -> HomeSyncPillTone.NEUTRAL
            watchStatus.connected &&
                syncState.consecutiveWearPushFailures >= WATCH_PUSH_FAILURE_THRESHOLD ->
                HomeSyncPillTone.ERROR
            watchStatus.connected && watchPushPending -> HomeSyncPillTone.WARN
            watchStatus.connected &&
                syncState.lastPushSequenceId > 0L &&
                !hasWatchAck(syncState) ->
                HomeSyncPillTone.WARN
            !watchStatus.connected -> HomeSyncPillTone.NEUTRAL
            !watchReady -> HomeSyncPillTone.WARN
            syncStatus.lastError.isNotBlank() -> HomeSyncPillTone.ERROR
            hasWatchAck(syncState) && activeSync -> HomeSyncPillTone.OK
            activeSync && syncStatus.hasSuccessfulSync() && !watchPushPending ->
                HomeSyncPillTone.OK
            else -> HomeSyncPillTone.NEUTRAL
        }

    fun hasWatchAck(state: PhoneSyncStateSnapshot): Boolean =
        state.lastAckSequenceId == state.lastPushSequenceId && state.lastAckSequenceId > 0L

    internal fun truncateForStatusPill(raw: String, maxLen: Int = 30): String {
        val trimmed = raw.trim()
        if (trimmed.length <= maxLen) return trimmed
        return trimmed.take(maxLen - 1).trimEnd { !it.isLetterOrDigit() } + "…"
    }
}
