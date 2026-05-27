package com.glucoseforwatch.wear.tile

import com.glucoseforwatch.wear.data.GlucoseCache
import com.glucoseforwatch.wear.data.GlucoseKeys

/** Single-flight sync lock shared by tile UI and refresh trampoline activity. */
internal object GlucoseSyncCoordinator {
    private const val MIN_GAP_MS = 5_000L
    private const val IN_FLIGHT_TIMEOUT_MS = 20_000L
    private val lock = Any()
    private var inFlight = false
    private var lastStartedAtMs = 0L

    fun tryBeginSync(nowEpochMs: Long = System.currentTimeMillis()): Boolean =
        synchronized(lock) {
            expireIfTimedOut(nowEpochMs)
            if (inFlight) return false
            if (nowEpochMs - lastStartedAtMs < MIN_GAP_MS) return false
            inFlight = true
            lastStartedAtMs = nowEpochMs
            true
        }

    fun endSync(nowEpochMs: Long = System.currentTimeMillis()) {
        synchronized(lock) {
            inFlight = false
            lastStartedAtMs = nowEpochMs
        }
    }

    fun isSyncLocked(cache: GlucoseCache, nowEpochMs: Long = System.currentTimeMillis()): Boolean {
        synchronized(lock) {
            expireIfTimedOut(nowEpochMs)
            if (inFlight) return true
        }
        val status = cache.loadRefreshStatusRaw() ?: return false
        if (status.status != GlucoseKeys.REFRESH_IN_PROGRESS) return false
        return nowEpochMs - status.updatedAtEpochMs <= IN_FLIGHT_TIMEOUT_MS
    }

    private fun expireIfTimedOut(nowEpochMs: Long) {
        if (!inFlight) return
        if (nowEpochMs - lastStartedAtMs > IN_FLIGHT_TIMEOUT_MS) {
            inFlight = false
        }
    }
}
