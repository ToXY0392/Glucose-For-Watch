package com.widgetg7.mobile.ui

import android.content.Context
import com.widgetg7.feature.sync.SyncStatusRepository
import com.widgetg7.mobile.battery.BatteryOptimizationHelper
import com.widgetg7.mobile.settings.AppSettingsStore
import com.widgetg7.mobile.settings.DisplaySettingsStore
import com.widgetg7.mobile.sync.PendingPushQueue
import com.widgetg7.mobile.sync.PhoneSyncStateStore
import com.widgetg7.mobile.watch.WatchConnectionRepository
import com.widgetg7.mobile.watch.WatchSyncHealthRepository

/** Loads repositories and delegates to [HomeStateMapper]. */
class HomeStateLoader {
    suspend fun load(context: Context): HomeUiState {
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

        return HomeStateMapper.map(
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
