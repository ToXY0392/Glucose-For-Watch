@file:Suppress("DEPRECATION")

package com.widgetg7.mobile.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.widgetg7.mobile.dexcom.DexcomShareConfig

data class DexcomUserSettings(
    val username: String,
    val password: String,
    val server: String,
) {
    fun isConfigured(): Boolean = username.isNotBlank() && password.isNotBlank()
}

class AppSettingsStore(context: Context) {
    private val prefs: SharedPreferences = createPreferences(context)

    fun loadDexcomSettings(): DexcomUserSettings {
        return DexcomUserSettings(
            username = prefs.getString(KEY_DEXCOM_USERNAME, "").orEmpty().trim(),
            password = prefs.getString(KEY_DEXCOM_PASSWORD, "").orEmpty().trim(),
            server = prefs.getString(KEY_DEXCOM_SERVER, DEFAULT_SERVER).orEmpty().trim().ifBlank { DEFAULT_SERVER },
        )
    }

    fun saveDexcomSettings(settings: DexcomUserSettings) {
        prefs.edit()
            .putString(KEY_DEXCOM_USERNAME, settings.username.trim())
            .putString(KEY_DEXCOM_PASSWORD, settings.password.trim())
            .putString(KEY_DEXCOM_SERVER, settings.server.trim().ifBlank { DEFAULT_SERVER })
            .apply()
    }

    fun clearDexcomSettings() {
        prefs.edit()
            .remove(KEY_DEXCOM_USERNAME)
            .remove(KEY_DEXCOM_PASSWORD)
            .remove(KEY_DEXCOM_SERVER)
            .apply()
    }

    fun toDexcomShareConfig(applicationId: String): DexcomShareConfig {
        val settings = loadDexcomSettings()
        return DexcomShareConfig(
            username = settings.username,
            password = settings.password,
            server = settings.server,
            applicationId = applicationId,
        )
    }

    private fun createPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    companion object {
        private const val PREFS_NAME = "widget_g7_settings"
        private const val KEY_DEXCOM_USERNAME = "dexcom_username"
        private const val KEY_DEXCOM_PASSWORD = "dexcom_password"
        private const val KEY_DEXCOM_SERVER = "dexcom_server"
        private const val DEFAULT_SERVER = "OUS"
    }
}
