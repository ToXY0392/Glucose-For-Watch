package com.widgetg7.mobile.relay

import com.widgetg7.mobile.data.GlucoseReading
import com.widgetg7.mobile.data.PhoneGlucoseSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RelayPhoneGlucoseSource(
    private val config: RelayConfig,
) : PhoneGlucoseSource {

    override val sourceName: String = "relay"

    override suspend fun latest(): GlucoseReading = withContext(Dispatchers.IO) {
        require(config.isConfigured()) { "Relay config is not valid" }

        val endpoint = config.baseUrl.trimEnd('/') + "/latest-glucose"
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Accept", "application/json")
            if (config.bearerToken.isNotBlank()) {
                setRequestProperty("Authorization", "Bearer ${config.bearerToken}")
            }
        }

        try {
            val code = connection.responseCode
            val body = readBody(if (code in 200..299) connection.inputStream else connection.errorStream)
            if (code !in 200..299) {
                throw IllegalStateException("Relay HTTP $code: $body")
            }

            parseGlucoseJson(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun parseGlucoseJson(jsonText: String): GlucoseReading {
        val json = JSONObject(jsonText)

        val value = json.readInt("valueMgDl", "value_mg_dl", "value", "mgdl")
            ?: throw IllegalStateException("Missing valueMgDl")

        val trend = (json.readString("trend", "trend_arrow") ?: "FLAT").uppercase()

        val delta = json.readInt("deltaMgDl", "delta_mg_dl", "delta") ?: 0

        val rawTs = json.readLong("timestampEpochMs", "timestamp_epoch_ms", "timestamp", "ts")
            ?: System.currentTimeMillis()
        val tsMs = if (rawTs < 10_000_000_000L) rawTs * 1000L else rawTs

        val stale = json.readBoolean("stale", "isStale", "is_stale") ?: false

        return GlucoseReading(
            valueMgDl = value,
            trend = trend,
            deltaMgDl = delta,
            timestampEpochMs = tsMs,
            stale = stale,
        )
    }

    private fun readBody(stream: InputStream?): String {
        if (stream == null) return ""
        return BufferedReader(InputStreamReader(stream)).use { reader ->
            buildString {
                var line = reader.readLine()
                while (line != null) {
                    append(line)
                    line = reader.readLine()
                }
            }
        }
    }
}

private fun JSONObject.readInt(vararg keys: String): Int? {
    for (key in keys) {
        if (!has(key) || isNull(key)) continue
        val value = get(key)
        when (value) {
            is Number -> return value.toInt()
            is String -> value.toIntOrNull()?.let { return it }
        }
    }
    return null
}

private fun JSONObject.readLong(vararg keys: String): Long? {
    for (key in keys) {
        if (!has(key) || isNull(key)) continue
        val value = get(key)
        when (value) {
            is Number -> return value.toLong()
            is String -> value.toLongOrNull()?.let { return it }
        }
    }
    return null
}

private fun JSONObject.readString(vararg keys: String): String? {
    for (key in keys) {
        if (!has(key) || isNull(key)) continue
        val value = optString(key, "").trim()
        if (value.isNotEmpty()) return value
    }
    return null
}

private fun JSONObject.readBoolean(vararg keys: String): Boolean? {
    for (key in keys) {
        if (!has(key) || isNull(key)) continue
        val value = get(key)
        when (value) {
            is Boolean -> return value
            is String -> {
                val normalized = value.trim().lowercase()
                if (normalized == "true") return true
                if (normalized == "false") return false
            }
            is Number -> return value.toInt() != 0
        }
    }
    return null
}
