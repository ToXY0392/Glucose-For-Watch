package com.widgetg7.mobile.dexcom

import com.widgetg7.mobile.data.GlucoseReading
import com.widgetg7.mobile.data.PhoneGlucoseSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

enum class DexcomShareErrorKind {
    AUTH,
    NETWORK,
    NO_DATA,
    UNKNOWN,
}

class DexcomShareException(
    val kind: DexcomShareErrorKind,
    override val message: String,
) : IllegalStateException(message)

class DexcomSharePhoneGlucoseSource(
    private val config: DexcomShareConfig,
) : PhoneGlucoseSource {

    override val sourceName: String = "dexcom-share"

    override suspend fun latest(): GlucoseReading = withContext(Dispatchers.IO) {
        require(config.isConfigured()) { "Dexcom Share config is not valid" }

        val accountId = authenticate()
        val sessionId = login(accountId)
        val values = readLatestValues(sessionId)
        if (values.length() == 0) {
            throw DexcomShareException(DexcomShareErrorKind.NO_DATA, "Aucune mesure Dexcom disponible.")
        }

        val newest = values.getJSONObject(0)
        val previous = values.optJSONObject(1)

        val value = newest.readInt("Value", "value")
            ?: throw DexcomShareException(DexcomShareErrorKind.UNKNOWN, "Valeur glucose Dexcom manquante.")
        val trend = newest.readTrend()
        val timestampMs = newest.readTimestampMs() ?: System.currentTimeMillis()
        val delta = previous?.let {
            val prevValue = it.readInt("Value", "value")
            if (prevValue == null) 0 else value - prevValue
        } ?: 0
        val stale = System.currentTimeMillis() - timestampMs > STALE_AFTER_MS

        GlucoseReading(
            valueMgDl = value,
            trend = trend,
            deltaMgDl = delta,
            timestampEpochMs = timestampMs,
            stale = stale,
        )
    }

    private fun authenticate(): String {
        val endpoint = "${config.baseUrl()}/ShareWebServices/Services/General/AuthenticatePublisherAccount"
        val body = JSONObject()
            .put("accountName", config.username)
            .put("password", config.password)
            .put("applicationId", config.applicationId)
            .toString()

        val raw = postJson(endpoint, body)
        return raw.trim().trim('"').takeIf { it.isNotBlank() }
            ?: throw DexcomShareException(DexcomShareErrorKind.AUTH, "Connexion Dexcom refusee.")
    }

    private fun login(accountId: String): String {
        val endpoint = "${config.baseUrl()}/ShareWebServices/Services/General/LoginPublisherAccountById"
        val body = JSONObject()
            .put("accountId", accountId)
            .put("password", config.password)
            .put("applicationId", config.applicationId)
            .toString()

        val raw = postJson(endpoint, body)
        return raw.trim().trim('"').takeIf { it.isNotBlank() }
            ?: throw DexcomShareException(DexcomShareErrorKind.AUTH, "Connexion Dexcom refusee.")
    }

    private fun readLatestValues(sessionId: String): JSONArray {
        val endpoint =
            "${config.baseUrl()}/ShareWebServices/Services/Publisher/ReadPublisherLatestGlucoseValues"
        val body = JSONObject()
            .put("sessionId", sessionId)
            .put("minutes", 1440)
            .put("maxCount", 2)
            .toString()

        return JSONArray(postJson(endpoint, body, "Dexcom Share read"))
    }

    private fun postJson(url: String, payload: String, label: String = "Dexcom Share auth"): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json")
        }

        try {
            connection.outputStream.use { out ->
                out.write(payload.toByteArray(Charsets.UTF_8))
            }
            val code = connection.responseCode
            val body = readBody(if (code in 200..299) connection.inputStream else connection.errorStream)
            if (code !in 200..299) {
                throw classifyHttpFailure(code, body, label)
            }
            return body
        } catch (t: IOException) {
            throw DexcomShareException(
                DexcomShareErrorKind.NETWORK,
                "Impossible de contacter Dexcom pour le moment.",
            )
        } finally {
            connection.disconnect()
        }
    }

    private fun classifyHttpFailure(code: Int, body: String, label: String): DexcomShareException {
        val normalized = body.lowercase()
        return when {
            normalized.contains("accountpasswordinvalid") ||
                normalized.contains("invalidpassword") ||
                normalized.contains("invalid password") ||
                normalized.contains("account not found") ||
                normalized.contains("authenticatepublisheraccount") ->
                DexcomShareException(DexcomShareErrorKind.AUTH, "Identifiants Dexcom invalides.")

            normalized.contains("sessionidnotfound") ->
                DexcomShareException(DexcomShareErrorKind.AUTH, "Session Dexcom a renouveler.")

            code in 500..599 ->
                DexcomShareException(DexcomShareErrorKind.NETWORK, "Dexcom est temporairement indisponible.")

            else ->
                DexcomShareException(DexcomShareErrorKind.UNKNOWN, "$label HTTP $code")
        }
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

    companion object {
        private const val STALE_AFTER_MS = 2 * 60 * 1000L
    }
}

private fun JSONObject.readInt(vararg keys: String): Int? {
    for (key in keys) {
        if (!has(key) || isNull(key)) continue
        val value = get(key)
        when (value) {
            is Number -> return value.toInt()
            is String -> value.toDoubleOrNull()?.roundToInt()?.let { return it }
        }
    }
    return null
}

private fun JSONObject.readTimestampMs(): Long? {
    val wt = optString("WT", "").trim()
    if (wt.isNotEmpty()) {
        val digits = Regex("-?\\d+").find(wt)?.value?.toLongOrNull()
        if (digits != null) return if (digits < 10_000_000_000L) digits * 1000L else digits
    }

    val candidates = arrayOf("Timestamp", "timestamp", "DT", "ST")
    for (key in candidates) {
        if (!has(key) || isNull(key)) continue
        val raw = get(key)
        val value = when (raw) {
            is Number -> raw.toLong()
            is String -> raw.toLongOrNull()
            else -> null
        } ?: continue
        return if (value < 10_000_000_000L) value * 1000L else value
    }
    return null
}

private fun JSONObject.readTrend(): String {
    val raw = when {
        has("Trend") && !isNull("Trend") -> get("Trend")
        has("TrendDirection") && !isNull("TrendDirection") -> get("TrendDirection")
        has("trend") && !isNull("trend") -> get("trend")
        else -> "Flat"
    }

    val token = when (raw) {
        is Number -> when (raw.toInt()) {
            1 -> "DoubleUp"
            2 -> "SingleUp"
            3 -> "FortyFiveUp"
            4 -> "Flat"
            5 -> "FortyFiveDown"
            6 -> "SingleDown"
            7 -> "DoubleDown"
            else -> "Flat"
        }
        else -> raw.toString()
    }.uppercase()

    return when (token) {
        "DOUBLEUP", "DOUBLE_UP" -> "UP"
        "SINGLEUP", "SINGLE_UP" -> "UP"
        "FORTYFIVEUP", "FORTY_FIVE_UP" -> "UP_RIGHT"
        "FLAT", "NONE" -> "FLAT"
        "FORTYFIVEDOWN", "FORTY_FIVE_DOWN" -> "DOWN_RIGHT"
        "SINGLEDOWN", "SINGLE_DOWN" -> "DOWN"
        "DOUBLEDOWN", "DOUBLE_DOWN" -> "DOWN"
        else -> "FLAT"
    }
}
