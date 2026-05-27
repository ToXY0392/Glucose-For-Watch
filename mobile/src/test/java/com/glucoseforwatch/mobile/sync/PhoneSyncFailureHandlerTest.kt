package com.glucoseforwatch.mobile.sync

import com.glucoseforwatch.core.model.SyncErrorCategory
import com.glucoseforwatch.core.testing.SyncTestFixtures
import com.glucoseforwatch.feature.dexcomshare.DexcomShareErrorKind
import com.glucoseforwatch.feature.dexcomshare.DexcomShareException
import com.glucoseforwatch.feature.sync.SyncMessageCatalog
import com.glucoseforwatch.feature.sync.SyncNotificationAction
import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneSyncFailureHandlerTest {
    @Test
    fun timeout_failure_maps_to_network_without_notification() {
        val status = SyncTestFixtures.syncStatusSnapshot(
            authFailureCount = 0,
            consecutiveFailureCount = 0,
        )

        val outcome = PhoneSyncFailureHandler.evaluate(
            error = IllegalStateException("request timed out"),
            currentStatus = status,
        )

        assertEquals(SyncMessageCatalog.SYNC_TIMEOUT, outcome.message)
        assertEquals(SyncErrorCategory.NETWORK, outcome.category)
        assertEquals(null, outcome.notificationAction)
    }

    @Test
    fun auth_failure_threshold_triggers_reconnect_notification() {
        val status = SyncTestFixtures.syncStatusSnapshot(
            authFailureCount = 1,
            consecutiveFailureCount = 0,
        )

        val outcome = PhoneSyncFailureHandler.evaluate(
            error = DexcomShareException(DexcomShareErrorKind.AUTH, "auth failed"),
            currentStatus = status,
        )

        assertEquals(SyncErrorCategory.AUTH, outcome.category)
        assertEquals(SyncNotificationAction.DEXCOM_RECONNECT_REQUIRED, outcome.notificationAction)
    }

    @Test
    fun consecutive_failures_trigger_sync_interrupted_notification() {
        val status = SyncTestFixtures.syncStatusSnapshot(
            authFailureCount = 0,
            consecutiveFailureCount = 2,
        )

        val outcome = PhoneSyncFailureHandler.evaluate(
            error = IllegalStateException("network unavailable"),
            currentStatus = status,
        )

        assertEquals(SyncErrorCategory.OTHER, outcome.category)
        assertEquals(SyncNotificationAction.SYNC_INTERRUPTED, outcome.notificationAction)
    }
}
