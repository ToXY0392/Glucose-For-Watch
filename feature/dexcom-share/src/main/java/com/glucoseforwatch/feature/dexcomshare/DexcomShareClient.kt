package com.glucoseforwatch.feature.dexcomshare

import com.glucoseforwatch.core.model.GlucoseFreshness
import com.glucoseforwatch.core.model.GlucoseReading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt

/** Dexcom Share account settings and server region for the REST API. */
data class DexcomShareConfig(
    val username: String,
    val password: String,
    val server: String,
    val applicationId: String,
) {
    /** True when all Dexcom Share login fields and server (US/OUS) are set. */
    fun isConfigured(): Boolean {
        if (username.isBlank()) return false
        if (password.isBlank()) return false
        if (applicationId.isBlank()) return false
        return server.trim().uppercase() in setOf("US", "OUS")
    }

    fun baseUrl(): String {
        return when (server.trim().uppercase()) {
            "OUS" -> "https://shareous1.dexcom.com"
            "US" -> "https://share2.dexcom.com"
            else -> throw IllegalArgumentException("Unsupported Dexcom Share server: $server")
        }
    }
}

/** Classification of Dexcom Share failures for error policy and UI. */
enum class DexcomShareErrorKind {
    AUTH,
    SESSION,
    NETWORK,
    NO_DATA,
    UNKNOWN,
}

/** Typed failure from [DexcomShareClient] with a [DexcomShareErrorKind]. */
class DexcomShareException(
    val kind: DexcomShareErrorKind,
    override val message: String,
) : IllegalStateException(message)

/**
 * Fetches the latest glucose reading from Dexcom Share over HTTPS.
 *
 * Sessions are cached per account and renewed once when Dexcom reports expiry.
 * Staleness follows [GlucoseFreshness].
 * HTTP calls share one client and are cancelled with the calling coroutine.
 */
class DexcomShareClient(
    private val config: DexcomShareConfig,
) {
    suspend fun latest(): GlucoseReading = withContext(Dispatchers.IO) {
        require(config.isConfigured()) { "Dexcom Share config is not valid" }

        val values = readLatestValuesWithSession()
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
        val stale = GlucoseFreshness.isStale(timestampMs, System.currentTimeMillis())

        GlucoseReading(
            valueMgDl = value,
            trend = trend,
            deltaMgDl = delta,
            timestampEpochMs = timestampMs,
            stale = stale,
        )
    }

    private suspend fun readLatestValuesWithSession(): JSONArray {
        val cacheKey = config.cacheKey()
        var sessionId = DexcomShareSessionCache.sessionFor(cacheKey)
            ?: createSession().also { DexcomShareSessionCache.save(cacheKey, it) }
        var sessionRenewals = 0

        while (true) {
            try {
                return readLatestValues(sessionId)
            } catch (error: DexcomShareException) {
                if (error.kind != DexcomShareErrorKind.SESSION || sessionRenewals >= MAX_SESSION_RENEWALS) {
                    DexcomShareSessionCache.clear(cacheKey)
                    throw error
                }
                DexcomShareSessionCache.clear(cacheKey)
                sessionId = createSession()
                DexcomShareSessionCache.save(cacheKey, sessionId)
                sessionRenewals += 1
            }
        }
    }

    private suspend fun createSession(): String {
        val accountId = authenticate()
        return login(accountId)
    }

    private suspend fun authenticate(): String {
        val endpoint = "${config.baseUrl()}/ShareWebServices/Services/General/AuthenticatePublisherAccount"
        val body = JSONObject()
            .put("accountName", config.username)
            .put("password", config.password)
            .put("applicationId", config.applicationId)
            .toString()

        val raw = postJson(endpoint, body)
        return raw.trim().trim('"').takeIf { it.isNotBlank() }
            ?: throw DexcomShareException(DexcomShareErrorKind.AUTH, "Connexion Dexcom refusée.")
    }

    private suspend fun login(accountId: String): String {
        val endpoint = "${config.baseUrl()}/ShareWebServices/Services/General/LoginPublisherAccountById"
        val body = JSONObject()
            .put("accountId", accountId)
            .put("password", config.password)
            .put("applicationId", config.applicationId)
            .toString()

        val raw = postJson(endpoint, body)
        return raw.trim().trim('"').takeIf { it.isNotBlank() }
            ?: throw DexcomShareException(DexcomShareErrorKind.AUTH, "Connexion Dexcom refusée.")
    }

    private suspend fun readLatestValues(sessionId: String): JSONArray {
        val endpoint =
            "${config.baseUrl()}/ShareWebServices/Services/Publisher/ReadPublisherLatestGlucoseValues"
        val body = JSONObject()
            .put("sessionId", sessionId)
            .put("minutes", 1440)
            .put("maxCount", 2)
            .toString()

        val response = postJson(endpoint, body, "Dexcom Share read")
        DexcomShareHttpClassifier.sessionFailure(response, "Dexcom Share read")?.let { throw it }
        return JSONArray(response)
    }

    private suspend fun postJson(url: String, payload: String, label: String = "Dexcom Share auth"): String {
        val request = Request.Builder()
            .url(url)
            .post(payload.toRequestBody(JSON_MEDIA_TYPE))
            .addHeader("Accept", "application/json")
            .build()
        val (code, responseBody) = try {
            DexcomShareHttp.client.newCall(request).await().use { response ->
                response.code to response.body?.string().orEmpty()
            }
        } catch (_: IOException) {
            throw DexcomShareException(
                DexcomShareErrorKind.NETWORK,
                "Impossible de contacter Dexcom pour le moment.",
            )
        }
        if (code !in 200..299) {
            throw DexcomShareHttpClassifier.classifyFailure(code, responseBody, label)
        }
        return responseBody
    }

    companion object {
        private const val MAX_SESSION_RENEWALS = 1
        private val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }
}

/** One process-wide client: a single connection pool and TLS session cache for every Dexcom call. */
internal object DexcomShareHttp {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}

/** Suspends on an OkHttp call; cancelling the coroutine cancels the HTTP request. */
private suspend fun Call.await(): Response =
    suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { cancel() }
        enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response) { _, value, _ -> value.close() }
                }

                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
            },
        )
    }

private object DexcomShareSessionCache {
    private val sessions = mutableMapOf<String, CachedSession>()

    @Synchronized
    fun sessionFor(cacheKey: String): String? {
        val session = sessions[cacheKey] ?: return null
        val ageMs = System.currentTimeMillis() - session.createdAtEpochMs
        return session.sessionId.takeIf { ageMs < SESSION_TTL_MS }
    }

    @Synchronized
    fun save(cacheKey: String, sessionId: String) {
        sessions[cacheKey] = CachedSession(sessionId, System.currentTimeMillis())
    }

    @Synchronized
    fun clear(cacheKey: String) {
        sessions.remove(cacheKey)
    }

    private data class CachedSession(
        val sessionId: String,
        val createdAtEpochMs: Long,
    )

    private const val SESSION_TTL_MS = 30 * 60 * 1000L
}

private fun DexcomShareConfig.cacheKey(): String =
    "${server.trim().uppercase()}|${username.trim().lowercase()}|${applicationId.trim()}"

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
        else -> null
    }
    return mapDexcomTrend(raw)
}

/**
 * Maps a raw Dexcom trend (numeric code or name) to the app token.
 * Missing, NotComputable, RateOutOfRange or unknown trends map to "NONE" (no arrow), never to "FLAT".
 */
internal fun mapDexcomTrend(raw: Any?): String {
    val token = when (raw) {
        null -> return "NONE"
        is Number -> when (raw.toInt()) {
            1 -> "DoubleUp"
            2 -> "SingleUp"
            3 -> "FortyFiveUp"
            4 -> "Flat"
            5 -> "FortyFiveDown"
            6 -> "SingleDown"
            7 -> "DoubleDown"
            else -> "None"
        }
        else -> raw.toString()
    }.uppercase()

    return when (token) {
        "DOUBLEUP", "DOUBLE_UP" -> "UP"
        "SINGLEUP", "SINGLE_UP" -> "UP"
        "FORTYFIVEUP", "FORTY_FIVE_UP" -> "UP_RIGHT"
        "FLAT" -> "FLAT"
        "FORTYFIVEDOWN", "FORTY_FIVE_DOWN" -> "DOWN_RIGHT"
        "SINGLEDOWN", "SINGLE_DOWN" -> "DOWN"
        "DOUBLEDOWN", "DOUBLE_DOWN" -> "DOWN"
        else -> "NONE"
    }
}
