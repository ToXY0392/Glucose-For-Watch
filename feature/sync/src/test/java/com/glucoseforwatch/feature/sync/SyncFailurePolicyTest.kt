package com.glucoseforwatch.feature.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncFailurePolicyTest {
    @Test
    fun auth_failures_trigger_reconnect_action() {
        val action = SyncFailurePolicy.decideNotificationAction(
            lastErrorCategory = "AUTH",
            authFailureCount = 2,
            consecutiveFailureCount = 1,
        )
        assertEquals(SyncNotificationAction.DEXCOM_RECONNECT_REQUIRED, action)
    }

    @Test
    fun repeated_failures_trigger_sync_interrupted_action() {
        val action = SyncFailurePolicy.decideNotificationAction(
            lastErrorCategory = "NETWORK",
            authFailureCount = 0,
            consecutiveFailureCount = 3,
        )
        assertEquals(SyncNotificationAction.SYNC_INTERRUPTED, action)
    }

    @Test
    fun low_failure_counts_do_not_trigger_notification() {
        val action = SyncFailurePolicy.decideNotificationAction(
            lastErrorCategory = "OTHER",
            authFailureCount = 0,
            consecutiveFailureCount = 1,
        )
        assertEquals(null, action)
    }
}
