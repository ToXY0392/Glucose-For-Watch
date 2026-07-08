package com.glucoseforwatch.mobile.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class NotificationHelperTest {
    private lateinit var context: Context
    private lateinit var stateStore: InMemoryNotificationStateStore
    private lateinit var helper: NotificationHelper

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        stateStore = InMemoryNotificationStateStore()
        helper = NotificationHelper(context, stateStore)
    }

    @Test
    fun duplicate_sync_interrupted_alert_is_not_reposted_while_active() {
        helper.notifySyncInterrupted("Erreur reseau")
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shadowManager = Shadows.shadowOf(manager)
        assertEquals(1, shadowManager.size())

        helper.notifySyncInterrupted("Erreur reseau")

        assertEquals(1, shadowManager.size())
        assertEquals(
            LastNotificationAlert("Synchronisation interrompue", "Erreur reseau"),
            stateStore.getLastAlert(1002),
        )
    }

    @Test
    fun changed_message_reposts_sync_interrupted_alert() {
        helper.notifySyncInterrupted("Erreur reseau")
        helper.notifySyncInterrupted("Session Dexcom expiree")

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        assertEquals(1, Shadows.shadowOf(manager).size())
        assertEquals(
            LastNotificationAlert("Synchronisation interrompue", "Session Dexcom expiree"),
            stateStore.getLastAlert(1002),
        )
    }

    @Test
    fun cancel_sync_alerts_clears_persisted_state() {
        helper.notifyDexcomReconnectRequired()
        assertNotNull(stateStore.getLastAlert(1001))

        helper.cancelSyncAlerts()

        assertNull(stateStore.getLastAlert(1001))
        assertNull(stateStore.getLastAlert(1002))
    }

    private class InMemoryNotificationStateStore : NotificationStateStore {
        private val alerts = mutableMapOf<Int, LastNotificationAlert>()

        override fun getLastAlert(notificationId: Int): LastNotificationAlert? = alerts[notificationId]

        override fun saveLastAlert(notificationId: Int, title: String, message: String) {
            alerts[notificationId] = LastNotificationAlert(title, message)
        }

        override fun clearAlert(notificationId: Int) {
            alerts.remove(notificationId)
        }

        override fun clearAllAlerts() {
            alerts.clear()
        }
    }

    private companion object {
        private const val ID_SYNC_INTERRUPTED = 1002
        private const val ID_DEXCOM_AUTH = 1001
    }
}
