package com.widgetg7.mobile.sync

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.widgetg7.mobile.data.GlucoseReading
import com.widgetg7.mobile.notifications.NotificationHelper
import com.widgetg7.mobile.settings.AppSettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ActiveGlucoseSyncService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    private var loopJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        PhoneSyncStateStore(this).recordActiveServiceState("starting")
        startForeground(
            NotificationHelper.ID_ACTIVE_SYNC,
            NotificationHelper(this).buildActiveSyncNotification(),
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action ?: ACTION_START) {
            ACTION_START -> {
                AppSettingsStore(this).setActiveSyncEnabled(true)
                if (startLoop()) {
                    requestImmediateSync()
                }
            }

            ACTION_SYNC_NOW -> {
                startLoop()
                requestImmediateSync()
            }

            ACTION_STOP -> {
                stopActiveSync()
                return START_NOT_STICKY
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        loopJob?.cancel()
        PhoneSyncStateStore(this).recordActiveServiceState("stopped")
        if (
            AppSettingsStore(this).isActiveSyncEnabled() &&
            AppSettingsStore(this).loadDexcomSettings().isConfigured()
        ) {
            PhoneAutoSyncScheduler.schedule(this, delayMs = FALLBACK_RESTART_MS)
        }
        super.onDestroy()
    }

    private fun startLoop(): Boolean {
        if (loopJob?.isActive == true) return false
        PhoneSyncStateStore(this).recordActiveServiceState("running")
        loopJob = serviceScope.launch {
            while (isActive) {
                runSyncPass(triggeredFromWatch = false, forcePushCurrentReading = false)
                delay(POLL_INTERVAL_MS)
            }
        }
        return true
    }

    private fun requestImmediateSync() {
        serviceScope.launch {
            runSyncPass(triggeredFromWatch = false, forcePushCurrentReading = true)
        }
    }

    private suspend fun runSyncPass(
        triggeredFromWatch: Boolean,
        forcePushCurrentReading: Boolean,
    ) {
        syncMutex.withLock {
            if (!AppSettingsStore(this).loadDexcomSettings().isConfigured()) {
                stopActiveSync()
                return
            }

            PhoneSyncStateStore(this).recordActiveServiceState("syncing")
            val result = PhoneGlucoseSyncEngine(this).run(
                triggeredFromWatch = triggeredFromWatch,
                forcePushCurrentReading = forcePushCurrentReading,
            )
            repairUnackedDelivery()
            PhoneSyncStateStore(this).recordActiveServiceState("running")
        }
    }

    private suspend fun repairUnackedDelivery() {
        val delays = longArrayOf(10_000L, 20_000L, 30_000L)
        for (delayMs in delays) {
            val state = PhoneSyncStateStore(this).load()
            if (state.lastPushSequenceId <= 0L || state.lastAckSequenceId == state.lastPushSequenceId) {
                return
            }

            delay(delayMs)
            val delayedState = PhoneSyncStateStore(this).load()
            if (delayedState.lastAckSequenceId == delayedState.lastPushSequenceId) return

            val reading = delayedState.toLastPushedReading() ?: return
            val nextRepushCount = delayedState.unackedRepushCount + 1
            val sequenceId = PhoneSyncStateStore(this).nextSequenceId()
            runCatching {
                PhoneWearSyncService(this).pushLatest(reading, sequenceId)
                PhoneSyncStateStore(this).recordPushSuccess(
                    timestampEpochMs = reading.timestampEpochMs,
                    sequenceId = sequenceId,
                    valueMgDl = reading.valueMgDl,
                    trend = reading.trend,
                    deltaMgDl = reading.deltaMgDl,
                    stale = reading.stale,
                )
                PhoneSyncStateStore(this).recordRepushAttempt(nextRepushCount)
            }.onFailure { error ->
                PhoneSyncStateStore(this).recordPushFailure(error.message.orEmpty())
                return
            }
        }
    }

    private fun PhoneSyncStateSnapshot.toLastPushedReading(): GlucoseReading? {
        val value = lastPushedValueMgDl ?: return null
        if (lastPushedReadingTimestampEpochMs <= 0L) return null
        return GlucoseReading(
            valueMgDl = value,
            trend = lastPushedTrend,
            deltaMgDl = lastPushedDeltaMgDl,
            timestampEpochMs = lastPushedReadingTimestampEpochMs,
            stale = lastPushedStale,
        )
    }

    private fun stopActiveSync() {
        AppSettingsStore(this).setActiveSyncEnabled(false)
        PhoneSyncStateStore(this).recordActiveServiceState("stopped")
        loopJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        private const val ACTION_START = "com.widgetg7.mobile.sync.action.START_ACTIVE_SYNC"
        private const val ACTION_SYNC_NOW = "com.widgetg7.mobile.sync.action.SYNC_NOW"
        private const val ACTION_STOP = "com.widgetg7.mobile.sync.action.STOP_ACTIVE_SYNC"
        private const val POLL_INTERVAL_MS = 45_000L
        private const val FALLBACK_RESTART_MS = 30_000L

        fun startIntent(context: Context): Intent =
            Intent(context, ActiveGlucoseSyncService::class.java).setAction(ACTION_START)

        fun syncNowIntent(context: Context): Intent =
            Intent(context, ActiveGlucoseSyncService::class.java).setAction(ACTION_SYNC_NOW)

        fun stopIntent(context: Context): Intent =
            Intent(context, ActiveGlucoseSyncService::class.java).setAction(ACTION_STOP)
    }
}
