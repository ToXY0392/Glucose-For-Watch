package com.widgetg7.mobile.data

import android.content.Context
import com.widgetg7.mobile.BuildConfig
import com.widgetg7.mobile.dexcom.DexcomShareConfigProvider
import com.widgetg7.mobile.dexcom.DexcomSharePhoneGlucoseSource
import com.widgetg7.mobile.settings.AppSettingsStore

object PhoneGlucoseSourceFactory {
    fun create(context: Context): PhoneGlucoseSource {
        val settingsConfig = AppSettingsStore(context)
            .toDexcomShareConfig(BuildConfig.DEXCOM_SHARE_APPLICATION_ID.trim())
        val buildConfig = DexcomShareConfigProvider.fromBuildConfig()
        val dexcomShareConfig = if (settingsConfig.isConfigured()) settingsConfig else buildConfig

        return if (dexcomShareConfig.isConfigured()) {
            DexcomSharePhoneGlucoseSource(dexcomShareConfig)
        } else {
            throw IllegalStateException("Aucune source glucose configurée.")
        }
    }
}
