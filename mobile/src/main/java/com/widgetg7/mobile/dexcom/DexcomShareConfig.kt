package com.widgetg7.mobile.dexcom

import com.widgetg7.mobile.BuildConfig

data class DexcomShareConfig(
    val username: String,
    val password: String,
    val server: String,
    val applicationId: String,
) {
    fun isConfigured(): Boolean {
        if (username.isBlank()) return false
        if (password.isBlank()) return false
        if (applicationId.isBlank()) return false
        return server.uppercase() in setOf("US", "OUS")
    }

    fun baseUrl(): String {
        return when (server.uppercase()) {
            "OUS" -> "https://shareous1.dexcom.com"
            else -> "https://share2.dexcom.com"
        }
    }
}

object DexcomShareConfigProvider {
    fun fromBuildConfig(): DexcomShareConfig {
        return DexcomShareConfig(
            username = BuildConfig.DEXCOM_SHARE_USERNAME.trim(),
            password = BuildConfig.DEXCOM_SHARE_PASSWORD.trim(),
            server = BuildConfig.DEXCOM_SHARE_SERVER.trim().ifBlank { "US" },
            applicationId = BuildConfig.DEXCOM_SHARE_APPLICATION_ID.trim(),
        )
    }
}
