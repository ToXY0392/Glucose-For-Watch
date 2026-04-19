package com.widgetg7.mobile.relay

import com.widgetg7.mobile.BuildConfig

data class RelayConfig(
    val baseUrl: String,
    val bearerToken: String,
) {
    fun isConfigured(): Boolean {
        if (baseUrl.isBlank()) return false
        if (!baseUrl.startsWith("https://")) return false
        if (baseUrl.contains("example.com")) return false
        return true
    }
}

object RelayConfigProvider {
    fun fromBuildConfig(): RelayConfig {
        return RelayConfig(
            baseUrl = BuildConfig.RELAY_BASE_URL.trim(),
            bearerToken = BuildConfig.RELAY_BEARER_TOKEN.trim(),
        )
    }
}
