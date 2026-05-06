package com.widgetg7.mobile.data

import android.content.Context
import com.widgetg7.feature.dexcomshare.DexcomShareClient
import com.widgetg7.feature.dexcomshare.DexcomShareConfig
import com.widgetg7.mobile.BuildConfig
import com.widgetg7.mobile.settings.AppSettingsStore

object PhoneGlucoseSourceFactory {
    fun create(context: Context): PhoneGlucoseSource {
        val settingsConfig = AppSettingsStore(context)
            .toDexcomShareConfig(BuildConfig.DEXCOM_SHARE_APPLICATION_ID.trim())
        val buildConfig = DexcomShareConfig(
            username = "",
            password = "",
            server = "OUS",
            applicationId = BuildConfig.DEXCOM_SHARE_APPLICATION_ID.trim(),
        )
        val dexcomShareConfig = if (settingsConfig.isConfigured()) settingsConfig else buildConfig

        return if (dexcomShareConfig.isConfigured()) {
            object : PhoneGlucoseSource {
                override val sourceName: String = "dexcom-share"
                private val client = DexcomShareClient(dexcomShareConfig)

                override suspend fun latest() = client.latest()
            }
        } else {
            throw IllegalStateException("Aucune source glucose configurée.")
        }
    }
}
