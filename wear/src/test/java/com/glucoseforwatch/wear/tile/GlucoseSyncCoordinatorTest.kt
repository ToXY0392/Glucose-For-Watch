package com.glucoseforwatch.wear.tile

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GlucoseSyncCoordinatorTest {

    @Before
    fun resetLock() {
        GlucoseSyncCoordinator.endSync(-10_000L)
    }

    @Test
    fun blocksParallelSyncAttempts() {
        assertTrue(GlucoseSyncCoordinator.tryBeginSync(1_000L))
        assertFalse(GlucoseSyncCoordinator.tryBeginSync(1_100L))
    }

    @Test
    fun enforcesCooldownAfterEndSync() {
        GlucoseSyncCoordinator.tryBeginSync(1_000L)
        GlucoseSyncCoordinator.endSync(2_000L)
        assertFalse(GlucoseSyncCoordinator.tryBeginSync(3_000L))
        assertTrue(GlucoseSyncCoordinator.tryBeginSync(7_500L))
    }

    @Test
    fun expiresStaleInFlightLock() {
        assertTrue(GlucoseSyncCoordinator.tryBeginSync(1_000L))
        assertFalse(GlucoseSyncCoordinator.tryBeginSync(1_500L))
        assertTrue(GlucoseSyncCoordinator.tryBeginSync(25_000L))
    }
}
