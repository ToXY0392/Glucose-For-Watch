package com.glucoseforwatch.mobile.ui

import com.glucoseforwatch.core.testing.SyncTestFixtures
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeReadingTimeResolverTest {
    @Test
    fun M2_prefers_reading_timestamp_over_sync_clock() {
        val snapshot =
            SyncTestFixtures.syncStatusSnapshot(
                lastReadingTimestampEpochMs = 100L,
                lastSyncEpochMs = 200L,
            )

        assertEquals(100L, HomeReadingTimeResolver.displayEpochMs(snapshot))
    }

    @Test
    fun M2_falls_back_to_sync_when_no_reading_timestamp() {
        val snapshot =
            SyncTestFixtures.syncStatusSnapshot(
                lastReadingTimestampEpochMs = 0L,
                lastSyncEpochMs = 200L,
            )

        assertEquals(200L, HomeReadingTimeResolver.displayEpochMs(snapshot))
    }
}
