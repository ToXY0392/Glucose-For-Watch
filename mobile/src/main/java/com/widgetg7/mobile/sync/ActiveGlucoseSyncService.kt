package com.widgetg7.mobile.sync

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.widgetg7.core.model.GlucoseReading
import com.widgetg7.mobile.notifications.NotificationHelper
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.watch.WatchSyncHealthRepository
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
    private var foregroundActive = false
    private lateinit var reconnectDetector: WatchReconnectDetector

    override fun onCreate() {
        super.onCreate()
        reconnectDetector = WatchReconnectDetector(this)
        foregroundActive = promoteToForeground()
        if (!foregroundActive) {
            BackgroundSyncFallback.activate(this)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!foregroundActive) {
            stopSelf()
            return START_NOT_STICKY
        }
        when (intent?.action ?: ACTION_START) {
            ACTION_START -> {
                Log.i(TAG, "service_action_start")
                AppSettingsStore(this).setActiveSyncEnabled(true)
                if (startLoop()) {
                    requestImmediateSync()
                }
            }

            ACTION_SYNC_NOW -> {
                Log.i(TAG, "service_action_sync_now")
                startLoop()
                requestImmediateSync()
            }

            ACTION_STOP -> {
                Log.i(TAG, "service_action_stop")
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
                delay(nextPollIntervalMs())
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

            reconnectDetector.onBeforeSyncPass {
                flushPendingOnReconnect()
            }

            PhoneSyncStateStore(this).recordActiveServiceState("syncing")
            val result = PhoneGlucoseSyncEngine(this).run(
                triggeredFromWatch = triggeredFromWatch,
                forcePushCurrentReading = forcePushCurrentReading,
            )
            Log.i(
                TAG,
                "sync_pass_result triggeredFromWatch=$triggeredFromWatch forcePush=$forcePushCurrentReading result=${result::class.simpleName}",
            )
            repairUnackedDelivery()
            PhoneSyncStateStore(this).recordActiveServiceState("running")
        }
    }

    private suspend fun flushPendingOnReconnect() {
        if (!PendingPushQueue(this).hasPending()) return
        Log.i(TAG, "reconnect_flush")
        PendingPushFlusher.flush(this)
    }

    private suspend fun repairUnackedDelivery() {
        val delays =
            if (isWatchInDegradedBatteryMode()) {
                longArrayOf(20_000L, 45_000L, 90_000L)
            } else {
                longArrayOf(10_000L, 30_000L, 60_000L, 120_000L)
            }
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
            val pushed =
                runCatching {
                    PhoneWearSyncService(this).pushLatest(reading, sequenceId)
                }.getOrElse { error ->
                    PhoneSyncStateStore(this).recordPushFailure(error.message.orEmpty())
                    Log.w(TAG, "repush_failed error=${error.message}", error)
                    false
                }
            if (!pushed) {
                PhoneSyncStateStore(this).recordWearPushUndelivered()
                Log.w(TAG, "repush_skipped push_unavailable repushCount=$nextRepushCount")
                continue
            }
            PhoneSyncStateStore(this).recordPushSuccess(
                timestampEpochMs = reading.timestampEpochMs,
                sequenceId = sequenceId,
                valueMgDl = reading.valueMgDl,
                trend = reading.trend,
                deltaMgDl = reading.deltaMgDl,
                stale = reading.stale,
            )
            PhoneSyncStateStore(this).recordRepushAttempt(nextRepushCount)
            Log.i(
                TAG,
                "repush_success sequenceId=$sequenceId repushCount=$nextRepushCount readingTs=${reading.timestampEpochMs}",
            )
        }
    }

    private fun nextPollIntervalMs(): Long {
        val health = WatchSyncHealthRepository(this).load()
        val intervalMs = WatchBatteryPolicy.pollIntervalMs(health)
        if (intervalMs == WatchBatteryPolicy.POLL_INTERVAL_DEGRADED_MS) {
            Log.i(TAG, "poll_interval_degraded intervalMs=$POLL_INTERVAL_DEGRADED_MS")
        }
        return intervalMs
    }

    private fun isWatchInDegradedBatteryMode(): Boolean {
        val health = WatchSyncHealthRepository(this).load()
        return WatchBatteryPolicy.isDegraded(health)
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

    private fun promoteToForeground(): Boolean {
        return ActiveGlucoseSyncForegroundGate.promote {
            PhoneSyncStateStore(this).recordActiveServiceState("starting")
            startForeground(
                NotificationHelper.ID_ACTIVE_SYNC,
                NotificationHelper(this).buildActiveSyncNotification(),
            )
            PhoneSyncStateStore(this).recordActiveServiceState("running")
        }
    }

    private fun stopActiveSync() {
        AppSettingsStore(this).setActiveSyncEnabled(false)
        PhoneSyncStateStore(this).recordActiveServiceState("stopped")
        loopJob?.cancel()
        if (foregroundActive) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
    }

    companion object {
        private const val TAG = "WG7.ActiveSyncService"
        private const val ACTION_START = "com.widgetg7.mobile.sync.action.START_ACTIVE_SYNC"
        private const val ACTION_SYNC_NOW = "com.widgetg7.mobile.sync.action.SYNC_NOW"
        private const val ACTION_STOP = "com.widgetg7.mobile.sync.action.STOP_ACTIVE_SYNC"
        private const val POLL_INTERVAL_DEGRADED_MS = WatchBatteryPolicy.POLL_INTERVAL_DEGRADED_MS
        private const val FALLBACK_RESTART_MS = 30_000L

        fun startIntent(context: Context): Intent =
            Intent(context, ActiveGlucoseSyncService::class.java).setAction(ACTION_START)

        fun syncNowIntent(context: Context): Intent =
            Intent(context, ActiveGlucoseSyncService::class.java).setAction(ACTION_SYNC_NOW)

        fun stopIntent(context: Context): Intent =
            Intent(context, ActiveGlucoseSyncService::class.java).setAction(ACTION_STOP)
    }
}
