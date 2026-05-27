package com.glucoseforwatch.mobile.sync

import android.app.ForegroundServiceStartNotAllowedException
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [31])
class ActiveGlucoseSyncForegroundGateTest {
    @Test
    fun X7_fgs_exception_returns_false_without_rethrow() {
        val promoted =
            ActiveGlucoseSyncForegroundGate.promote {
                throw ForegroundServiceStartNotAllowedException("time limit exhausted")
            }

        assertFalse(promoted)
    }

    @Test
    fun X7_security_exception_returns_false_without_rethrow() {
        val promoted =
            ActiveGlucoseSyncForegroundGate.promote {
                throw SecurityException("startForeground not allowed")
            }

        assertFalse(promoted)
    }

    @Test
    fun X7_successful_promote_returns_true() {
        var called = false
        val promoted =
            ActiveGlucoseSyncForegroundGate.promote {
                called = true
            }

        assertTrue(promoted)
        assertTrue(called)
    }

    @Test(expected = IllegalStateException::class)
    fun X7_unexpected_exception_is_rethrown() {
        ActiveGlucoseSyncForegroundGate.promote {
            throw IllegalStateException("unexpected")
        }
    }
}
