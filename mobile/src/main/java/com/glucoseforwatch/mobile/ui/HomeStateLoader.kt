package com.glucoseforwatch.mobile.ui

import android.content.Context
import com.glucoseforwatch.feature.sync.SyncStatusRepository
import com.glucoseforwatch.mobile.battery.BatteryOptimizationHelper
import com.glucoseforwatch.mobile.settings.AppSettingsStore
import com.glucoseforwatch.mobile.settings.DisplaySettingsStore
import com.glucoseforwatch.mobile.sync.PendingPushQueue
import com.glucoseforwatch.mobile.sync.PhoneSyncStateStore
import com.glucoseforwatch.mobile.watch.WatchConnectionRepository
import com.glucoseforwatch.mobile.watch.WatchSyncHealthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Loads repositories and delegates to [HomeStateMapper]. */
class HomeStateLoader {
    /** Reads preferences (including the encrypted store) off the main thread. */
    suspend fun load(context: Context): HomeUiState = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val settings = AppSettingsStore(appContext).loadDexcomSettings()
        val syncStatus = SyncStatusRepository(appContext).load()
        val watchHealth = WatchSyncHealthRepository(appContext).load()
        val watchStatus = WatchConnectionRepository(appContext).loadStatus()
        val syncState = PhoneSyncStateStore(appContext).load()
        val batteryProtected = BatteryOptimizationHelper(appContext).loadStatus().isProtectedFromOptimization
        val watchPushPending = syncStatus.watchPushPending || PendingPushQueue(appContext).hasPending()
        val activeSyncEnabled = AppSettingsStore(appContext).isActiveSyncEnabled()
        val displayUnit = DisplaySettingsStore(appContext).loadGlucoseDisplayUnit()

        HomeStateMapper.map(
            context = appContext,
            dexcomSettings = settings,
            syncStatus = syncStatus,
            watchStatus = watchStatus,
            watchHealth = watchHealth,
            syncState = syncState,
            batteryProtected = batteryProtected,
            watchPushPending = watchPushPending,
            activeSyncEnabled = activeSyncEnabled,
            displayUnit = displayUnit,
        )
    }
}
