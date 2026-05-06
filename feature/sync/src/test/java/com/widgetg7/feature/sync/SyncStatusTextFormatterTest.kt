package com.widgetg7.feature.sync

import com.widgetg7.core.model.SyncErrorCategory
import com.widgetg7.core.testing.SyncTestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncStatusTextFormatterTest {
    @Test
    fun dexcom_status_reports_not_configured() {
        val text = SyncStatusTextFormatter.dexcomStatus(
            dexcomConfigured = false,
            serverLabel = "Europe",
            syncStatus = SyncTestFixtures.syncStatusSnapshot(),
        )
        assertEquals("Dexcom : à configurer", text)
    }

    @Test
    fun dexcom_status_reports_reconnect_when_auth_threshold_reached() {
        val text = SyncStatusTextFormatter.dexcomStatus(
            dexcomConfigured = true,
            serverLabel = "US",
            syncStatus = SyncTestFixtures.syncStatusSnapshot(
                lastErrorCategory = SyncErrorCategory.AUTH,
                authFailureCount = 2,
            ),
        )
        assertEquals("Dexcom : reconnexion requise (US)", text)
    }

    @Test
    fun sync_status_reports_waiting_when_no_success_and_no_error() {
        val text = SyncStatusTextFormatter.syncStatus(
            SyncTestFixtures.syncStatusSnapshot(
                lastValueMgDl = null,
                lastSyncEpochMs = 0L,
                lastError = "",
            ),
        )
        assertEquals("État : en attente d'une première synchronisation", text)
    }

    @Test
    fun sync_status_reports_auth_reconnect_message() {
        val text = SyncStatusTextFormatter.syncStatus(
            SyncTestFixtures.syncStatusSnapshot(
                lastErrorCategory = SyncErrorCategory.AUTH,
                authFailureCount = 3,
            ),
        )
        assertEquals("État : reconnectez votre compte Dexcom", text)
    }

    @Test
    fun last_sync_reports_none_when_missing() {
        val text = SyncStatusTextFormatter.lastSync(
            SyncTestFixtures.syncStatusSnapshot(lastSyncEpochMs = 0L),
        )
        assertEquals("Dernière sync : aucune pour le moment", text)
    }

    @Test
    fun account_summary_reports_connected_when_successful() {
        val text = SyncStatusTextFormatter.dexcomAccountSummary(
            dexcomConfigured = true,
            serverLabel = "Europe",
            syncStatus = SyncTestFixtures.syncStatusSnapshot(
                lastValueMgDl = 110,
                lastSyncEpochMs = System.currentTimeMillis(),
            ),
        )
        assertTrue(text.startsWith("Compte Dexcom connecté (Europe)"))
    }
}
