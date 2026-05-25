package com.widgetg7.mobile.ui

import com.widgetg7.core.model.SyncStatusSnapshot

/**
 * Single timestamp for home hero age, companion sync-age strip, and stale alignment (M.2).
 * Prefer Dexcom reading time; fall back to last successful sync clock.
 */
object HomeReadingTimeResolver {
    fun displayEpochMs(snapshot: SyncStatusSnapshot): Long =
        when {
            snapshot.lastReadingTimestampEpochMs > 0L -> snapshot.lastReadingTimestampEpochMs
            snapshot.lastSyncEpochMs > 0L -> snapshot.lastSyncEpochMs
            else -> 0L
        }
}
