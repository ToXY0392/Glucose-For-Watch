@file:Suppress("DEPRECATION")

package com.glucoseforwatch.mobile.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.glucoseforwatch.feature.dexcomshare.DexcomShareConfig

/** Dexcom Share account settings and server region. */
data class DexcomUserSettings(
    val username: String,
    val password: String,
    val server: String,
) {
    fun isConfigured(): Boolean = username.isNotBlank() && password.isNotBlank()
}

/**
 * Encrypted storage for Dexcom settings and active-sync toggle.
 * The encrypted preferences are created once per process: building the master key and
 * EncryptedSharedPreferences hits the Android Keystore, and this store is instantiated
 * from many short-lived callers (services, receivers, UI).
 */
class AppSettingsStore(context: Context) {
    private val prefs: SharedPreferences = sharedPreferences(context.applicationContext)

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
            .putBoolean(KEY_ACTIVE_SYNC_ENABLED, false)
            .apply()
    }

    fun isActiveSyncEnabled(): Boolean =
        prefs.getBoolean(KEY_ACTIVE_SYNC_ENABLED, loadDexcomSettings().isConfigured())

    fun setActiveSyncEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_ACTIVE_SYNC_ENABLED, enabled)
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

    companion object {
        @Volatile
        private var cachedPrefs: SharedPreferences? = null

        private fun sharedPreferences(appContext: Context): SharedPreferences =
            cachedPrefs ?: synchronized(this) {
                cachedPrefs ?: createPreferences(appContext).also { cachedPrefs = it }
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

        private const val PREFS_NAME = "widget_g7_settings"
        private const val KEY_DEXCOM_USERNAME = "dexcom_username"
        private const val KEY_DEXCOM_PASSWORD = "dexcom_password"
        private const val KEY_DEXCOM_SERVER = "dexcom_server"
        private const val KEY_ACTIVE_SYNC_ENABLED = "active_sync_enabled"
        private const val DEFAULT_SERVER = "OUS"
    }
}
