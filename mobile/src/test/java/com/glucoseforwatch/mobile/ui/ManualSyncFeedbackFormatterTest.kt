package com.glucoseforwatch.mobile.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.glucoseforwatch.feature.sync.SyncExecutionResult
import com.glucoseforwatch.feature.sync.WatchDeliveryStatus
import com.glucoseforwatch.mobile.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class ManualSyncFeedbackFormatterTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun A3_delivered_shows_success() {
        val message =
            ManualSyncFeedbackFormatter.format(
                context,
                SyncExecutionResult.SuccessNewReading("dexcom", WatchDeliveryStatus.DELIVERED),
            )

        assertEquals(context.getString(R.string.manual_sync_success), message)
    }

    @Test
    fun A3_watch_unavailable_shows_pending() {
        val message =
            ManualSyncFeedbackFormatter.format(
                context,
                SyncExecutionResult.SuccessNoNewReading("dexcom", WatchDeliveryStatus.WATCH_UNAVAILABLE),
            )

        assertEquals(context.getString(R.string.manual_sync_pending), message)
    }

    @Test
    fun A3_failure_shows_error() {
        val message =
            ManualSyncFeedbackFormatter.format(
                context,
                SyncExecutionResult.Failure("Session expirée"),
            )

        assertEquals(context.getString(R.string.manual_sync_error, "Session expirée"), message)
    }
}
