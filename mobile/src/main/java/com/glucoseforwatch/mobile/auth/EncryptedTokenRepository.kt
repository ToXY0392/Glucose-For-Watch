package com.glucoseforwatch.mobile.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.glucoseforwatch.core.auth.TokenRepository
import android.util.Log

/**
 * EncryptedSharedPreferences-backed TokenRepository. Performs a safe migration from a set of
 * legacy SharedPreferences names if tokens are found.
 *
 * Migration strategy:
 *  - iterate a candidate list of legacy prefs names and check for keys ACCESS_TOKEN / REFRESH_TOKEN
 *  - if found, copy values to encrypted prefs and only then clear the legacy prefs
 */
class EncryptedTokenRepository(private val context: Context) : TokenRepository {
    companion object {
        private const val TAG = "WG7.TokenRepo"
        private const val PREFS_NAME = "gfw_encrypted_tokens"
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
    }

    private val prefs: SharedPreferences by lazy { createEncryptedPreferences(context.applicationContext) }

    init {
        try {
            migrateFromLegacyIfNeeded()
        } catch (t: Throwable) {
            Log.w(TAG, "token migration failed: ${'$'}t")
        }
    }

    private fun createEncryptedPreferences(ctx: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(ctx)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            ctx,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun migrateFromLegacyIfNeeded() {
        for (name in legacyCandidates(ctx = context)) {
            val legacy = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            if (legacy.contains(KEY_ACCESS) || legacy.contains(KEY_REFRESH)) {
                val access = legacy.getString(KEY_ACCESS, null)
                val refresh = legacy.getString(KEY_REFRESH, null)

                // only proceed if at least one token present
                if (access.isNullOrBlank() && refresh.isNullOrBlank()) continue

                // write to encrypted prefs
                val wrote = try {
                    prefs.edit().apply {
                        access?.let { putString(KEY_ACCESS, it) }
                        refresh?.let { putString(KEY_REFRESH, it) }
                    }.commit()
                } catch (t: Throwable) {
                    Log.w(TAG, "failed to write encrypted tokens: ${'$'}t")
                    false
                }

                if (wrote) {
                    // verify write
                    val verifyAccess = prefs.getString(KEY_ACCESS, null)
                    val verifyRefresh = prefs.getString(KEY_REFRESH, null)
                    if ((access == null || access == verifyAccess) && (refresh == null || refresh == verifyRefresh)) {
                        // clear legacy storage safely
                        legacy.edit().clear().commit()
                        Log.i(TAG, "migrated tokens from legacy prefs '$name'")
                    } else {
                        Log.w(TAG, "verification failed after writing encrypted tokens for legacy '$name'")
                    }
                }

                // migration attempted for this prefs name, do not continue scanning further sources
                return
            }
        }
    }

    private fun legacyCandidates(ctx: Context): List<String> {
        // common legacy names + the conventional default preferences name
        val defaultPrefsName = ctx.packageName + "_preferences"
        return listOf("widget_g7_settings", "gfw_tokens", "widget_g7_tokens", defaultPrefsName)
    }

    override fun getAccessToken(): String? = prefs.getString(KEY_ACCESS, null)

    override fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    override fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit().putString(KEY_ACCESS, accessToken).putString(KEY_REFRESH, refreshToken).apply()
    }

    override fun clearTokens() { prefs.edit().remove(KEY_ACCESS).remove(KEY_REFRESH).apply() }

    override fun hasToken(): Boolean = !getAccessToken().isNullOrBlank()
}