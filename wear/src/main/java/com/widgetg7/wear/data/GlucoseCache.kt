package com.widgetg7.wear.data

import android.content.Context
import com.widgetg7.core.datalayer.GlucoseDataLayerContract

enum class GlucoseSemanticLevel {
    NORMAL,
    ATTENTION,
    ALERT,
    STALE,
}

data class GlucoseSnapshot(
    val valueMgDl: Int,
    val trend: String,
    val deltaMgDl: Int,
    val timestampEpochMs: Long,
    val stale: Boolean,
) {
    fun displayValueText(): String = when {
        valueMgDl <= DISPLAY_LOW_MAX_MG_DL -> "LOW"
        valueMgDl >= DISPLAY_HIGH_MIN_MG_DL -> "HI"
        else -> valueMgDl.toString()
    }

    fun trendArrow(): String = when (trend) {
        "UP" -> "\u2191"
        "UP_RIGHT" -> "\u2197"
        "FLAT" -> "\u2192"
        "DOWN_RIGHT" -> "\u2198"
        "DOWN" -> "\u2193"
        else -> "?"
    }

    fun signedDelta(): String = if (deltaMgDl >= 0) "+$deltaMgDl" else deltaMgDl.toString()

    fun semanticLevel(): GlucoseSemanticLevel {
        return when {
            valueMgDl < 70 || valueMgDl > 250 -> GlucoseSemanticLevel.ALERT
            valueMgDl in 70..79 || valueMgDl in 181..250 -> GlucoseSemanticLevel.ATTENTION
            else -> GlucoseSemanticLevel.NORMAL
        }
    }

    fun semanticColorArgb(): Int = when (semanticLevel()) {
        GlucoseSemanticLevel.NORMAL -> 0xFFF5F7FA.toInt()
        GlucoseSemanticLevel.ATTENTION -> 0xFFF4B942.toInt()
        GlucoseSemanticLevel.ALERT -> 0xFFFF5A5F.toInt()
        GlucoseSemanticLevel.STALE -> 0xFFA7B0BA.toInt()
    }

    fun metadataColorArgb(): Int = when (semanticLevel()) {
        GlucoseSemanticLevel.NORMAL -> 0xFF7FDBB6.toInt()
        GlucoseSemanticLevel.ATTENTION -> 0xFFF4B942.toInt()
        GlucoseSemanticLevel.ALERT -> 0xFFFF8A8F.toInt()
        GlucoseSemanticLevel.STALE -> 0xFFA7B0BA.toInt()
    }

    fun secondaryLabel(nowEpochMs: Long): String {
        val ageLabel = ageLabel(nowEpochMs)
        return "${trendArrow()} - $ageLabel"
    }

    fun trendOnlyLabel(): String = trendArrow()

    fun compactSecondaryLabel(nowEpochMs: Long): String {
        val ageLabel = ageLabel(nowEpochMs)
        return "${trendArrow()} - $ageLabel"
    }

    fun compactTrendOnlyLabel(): String = trendArrow()

    private fun ageLabel(nowEpochMs: Long): String {
        val ageMinutes = ((nowEpochMs - timestampEpochMs).coerceAtLeast(0L) / 60_000L)
        return when {
            ageMinutes <= 0L -> "maint."
            ageMinutes == 1L -> "1 min"
            else -> "$ageMinutes min"
        }
    }

    private companion object {
        private const val DISPLAY_LOW_MAX_MG_DL = 40
        private const val DISPLAY_HIGH_MIN_MG_DL = 400
    }
}

object GlucoseKeys {
    const val PATH_LATEST = GlucoseDataLayerContract.PATH_LATEST
    const val PATH_REFRESH_REQUEST = GlucoseDataLayerContract.PATH_REFRESH_REQUEST
    const val PATH_REFRESH_STATUS = GlucoseDataLayerContract.PATH_REFRESH_STATUS
    const val PATH_WATCH_ACK = GlucoseDataLayerContract.PATH_WATCH_ACK
    const val PATH_WATCH_STATUS_REQUEST = GlucoseDataLayerContract.PATH_WATCH_STATUS_REQUEST
    const val PATH_WATCH_STATUS = GlucoseDataLayerContract.PATH_WATCH_STATUS

    const val VALUE_MG_DL = GlucoseDataLayerContract.VALUE_MG_DL
    const val TREND = GlucoseDataLayerContract.TREND
    const val DELTA_MG_DL = GlucoseDataLayerContract.DELTA_MG_DL
    const val TIMESTAMP_EPOCH_MS = GlucoseDataLayerContract.TIMESTAMP_EPOCH_MS
    const val STALE = GlucoseDataLayerContract.STALE
    const val SEQUENCE_ID = GlucoseDataLayerContract.SEQUENCE_ID
    const val TARGET_NODE_ID = GlucoseDataLayerContract.TARGET_NODE_ID
    const val HISTORY = "history"
    const val REFRESH_STATUS = GlucoseDataLayerContract.REFRESH_STATUS
    const val REFRESH_MESSAGE = GlucoseDataLayerContract.REFRESH_MESSAGE
    const val REFRESH_UPDATED_AT = GlucoseDataLayerContract.REFRESH_UPDATED_AT
    const val REFRESH_IN_PROGRESS = GlucoseDataLayerContract.REFRESH_IN_PROGRESS
    const val REFRESH_COMPLETED = GlucoseDataLayerContract.REFRESH_COMPLETED
    const val REFRESH_FAILED = GlucoseDataLayerContract.REFRESH_FAILED
    const val WATCH_BATTERY_LEVEL = GlucoseDataLayerContract.WATCH_BATTERY_LEVEL
    const val WATCH_IS_CHARGING = GlucoseDataLayerContract.WATCH_IS_CHARGING
    const val WATCH_LOW_POWER = GlucoseDataLayerContract.WATCH_LOW_POWER
    const val WATCH_SYNC_LIMITED = GlucoseDataLayerContract.WATCH_SYNC_LIMITED
    const val WATCH_STATUS_MESSAGE = GlucoseDataLayerContract.WATCH_STATUS_MESSAGE
    const val WATCH_STATUS_UPDATED_AT = GlucoseDataLayerContract.WATCH_STATUS_UPDATED_AT
    const val WATCH_MANUFACTURER = GlucoseDataLayerContract.WATCH_MANUFACTURER
    const val WATCH_MODEL = GlucoseDataLayerContract.WATCH_MODEL
    const val WATCH_DEVICE = GlucoseDataLayerContract.WATCH_DEVICE
    const val WATCH_APP_INSTALLED = GlucoseDataLayerContract.WATCH_APP_INSTALLED
    const val WATCH_APP_VERSION_NAME = GlucoseDataLayerContract.WATCH_APP_VERSION_NAME
    const val WATCH_APP_VERSION_CODE = GlucoseDataLayerContract.WATCH_APP_VERSION_CODE
    const val WATCH_SUPPORTS_TILE = GlucoseDataLayerContract.WATCH_SUPPORTS_TILE
    const val WATCH_SUPPORTS_COMPLICATION = GlucoseDataLayerContract.WATCH_SUPPORTS_COMPLICATION
    const val ACK_READING_TIMESTAMP_EPOCH_MS = GlucoseDataLayerContract.ACK_READING_TIMESTAMP_EPOCH_MS
    const val ACK_SEQUENCE_ID = GlucoseDataLayerContract.ACK_SEQUENCE_ID
    const val ACK_RECEIVED_AT = GlucoseDataLayerContract.ACK_RECEIVED_AT
}

data class RefreshStatusSnapshot(
    val status: String,
    val message: String,
    val updatedAtEpochMs: Long,
) {
    fun shouldDisplay(nowEpochMs: Long): Boolean {
        val ageMs = nowEpochMs - updatedAtEpochMs
        return when (status) {
            GlucoseKeys.REFRESH_IN_PROGRESS -> ageMs <= TIMEOUT_VISIBLE_MS
            GlucoseKeys.REFRESH_COMPLETED -> ageMs <= COMPLETED_VISIBLE_MS
            GlucoseKeys.REFRESH_FAILED -> ageMs <= FAILURE_VISIBLE_MS
            else -> false
        }
    }

    fun displayText(nowEpochMs: Long): String {
        val ageMs = nowEpochMs - updatedAtEpochMs
        if (status == GlucoseKeys.REFRESH_IN_PROGRESS && ageMs > PENDING_VISIBLE_MS) {
            return "Delai depasse"
        }
        return message.ifBlank {
            when (status) {
                GlucoseKeys.REFRESH_IN_PROGRESS -> "Actualisation..."
                GlucoseKeys.REFRESH_COMPLETED -> "À jour"
                GlucoseKeys.REFRESH_FAILED -> "Echec de synchro"
                else -> ""
            }
        }
    }

    companion object {
        private const val PENDING_VISIBLE_MS = 10_000L
        private const val TIMEOUT_VISIBLE_MS = 45_000L
        private const val COMPLETED_VISIBLE_MS = 30_000L
        private const val FAILURE_VISIBLE_MS = 90_000L
    }
}

data class WatchSyncHealthSnapshot(
    val batteryLevel: Int,
    val isCharging: Boolean,
    val lowPowerMode: Boolean,
    val syncLimited: Boolean,
    val message: String,
    val updatedAtEpochMs: Long,
) {
    fun shouldDisplay(nowEpochMs: Long): Boolean {
        if (updatedAtEpochMs <= 0L) return false
        if (nowEpochMs - updatedAtEpochMs > HEALTH_VISIBLE_MS) return false
        return syncLimited || batteryLevel in 0..20
    }

    companion object {
        private const val HEALTH_VISIBLE_MS = 6 * 60 * 60 * 1000L
    }
}

class GlucoseCache(context: Context) {
    private val prefs = context.getSharedPreferences("glucose_cache", Context.MODE_PRIVATE)

    fun save(snapshot: GlucoseSnapshot, receivedAtEpochMs: Long = System.currentTimeMillis()) {
        val history = updatedHistory(snapshot)
        prefs.edit()
            .putInt(GlucoseKeys.VALUE_MG_DL, snapshot.valueMgDl)
            .putString(GlucoseKeys.TREND, snapshot.trend)
            .putInt(GlucoseKeys.DELTA_MG_DL, snapshot.deltaMgDl)
            .putLong(GlucoseKeys.TIMESTAMP_EPOCH_MS, snapshot.timestampEpochMs)
            .putBoolean(GlucoseKeys.STALE, snapshot.stale)
            .putString(GlucoseKeys.HISTORY, history.joinToString(","))
            .putLong(KEY_LAST_PHONE_UPDATE_RECEIVED_AT, receivedAtEpochMs)
            .apply()
    }

    fun load(): GlucoseSnapshot? {
        if (!prefs.contains(GlucoseKeys.TIMESTAMP_EPOCH_MS)) return null
        val timestampEpochMs = prefs.getLong(GlucoseKeys.TIMESTAMP_EPOCH_MS, 0L)
        val ageMs = System.currentTimeMillis() - timestampEpochMs
        val isTooOld = timestampEpochMs <= 0L || ageMs > STALE_AFTER_MS
        return GlucoseSnapshot(
            valueMgDl = prefs.getInt(GlucoseKeys.VALUE_MG_DL, 0),
            trend = prefs.getString(GlucoseKeys.TREND, "FLAT").orEmpty(),
            deltaMgDl = prefs.getInt(GlucoseKeys.DELTA_MG_DL, 0),
            timestampEpochMs = timestampEpochMs,
            stale = prefs.getBoolean(GlucoseKeys.STALE, true) || isTooOld,
        )
    }

    fun loadRefreshStatus(nowEpochMs: Long = System.currentTimeMillis()): RefreshStatusSnapshot? {
        if (!prefs.contains(KEY_REFRESH_UPDATED_AT)) return null

        val snapshot = RefreshStatusSnapshot(
            status = prefs.getString(KEY_REFRESH_STATUS, "").orEmpty(),
            message = prefs.getString(KEY_REFRESH_MESSAGE, "").orEmpty(),
            updatedAtEpochMs = prefs.getLong(KEY_REFRESH_UPDATED_AT, 0L),
        )
        return snapshot.takeIf { it.shouldDisplay(nowEpochMs) }
    }

    fun loadRefreshStatusRaw(): RefreshStatusSnapshot? {
        if (!prefs.contains(KEY_REFRESH_UPDATED_AT)) return null
        return RefreshStatusSnapshot(
            status = prefs.getString(KEY_REFRESH_STATUS, "").orEmpty(),
            message = prefs.getString(KEY_REFRESH_MESSAGE, "").orEmpty(),
            updatedAtEpochMs = prefs.getLong(KEY_REFRESH_UPDATED_AT, 0L),
        )
    }

    fun markRefreshPending(message: String = "Actualisation...") {
        prefs.edit()
            .putString(KEY_REFRESH_STATUS, GlucoseKeys.REFRESH_IN_PROGRESS)
            .putString(KEY_REFRESH_MESSAGE, message)
            .putLong(KEY_REFRESH_UPDATED_AT, System.currentTimeMillis())
            .apply()
    }

    fun markRefreshFailed(message: String) {
        prefs.edit()
            .putString(KEY_REFRESH_STATUS, GlucoseKeys.REFRESH_FAILED)
            .putString(KEY_REFRESH_MESSAGE, message)
            .putLong(KEY_REFRESH_UPDATED_AT, System.currentTimeMillis())
            .apply()
    }

    fun markRefreshCompleted(message: String = "À jour") {
        prefs.edit()
            .putString(KEY_REFRESH_STATUS, GlucoseKeys.REFRESH_COMPLETED)
            .putString(KEY_REFRESH_MESSAGE, message)
            .putLong(KEY_REFRESH_UPDATED_AT, System.currentTimeMillis())
            .apply()
    }

    fun clearRefreshStatus() {
        prefs.edit()
            .remove(KEY_REFRESH_STATUS)
            .remove(KEY_REFRESH_MESSAGE)
            .remove(KEY_REFRESH_UPDATED_AT)
            .apply()
    }

    fun saveWatchSyncHealth(snapshot: WatchSyncHealthSnapshot) {
        prefs.edit()
            .putInt(KEY_WATCH_BATTERY_LEVEL, snapshot.batteryLevel)
            .putBoolean(KEY_WATCH_IS_CHARGING, snapshot.isCharging)
            .putBoolean(KEY_WATCH_LOW_POWER, snapshot.lowPowerMode)
            .putBoolean(KEY_WATCH_SYNC_LIMITED, snapshot.syncLimited)
            .putString(KEY_WATCH_STATUS_MESSAGE, snapshot.message)
            .putLong(KEY_WATCH_STATUS_UPDATED_AT, snapshot.updatedAtEpochMs)
            .apply()
    }

    fun loadWatchSyncHealth(nowEpochMs: Long = System.currentTimeMillis()): WatchSyncHealthSnapshot? {
        if (!prefs.contains(KEY_WATCH_STATUS_UPDATED_AT)) return null
        val snapshot = WatchSyncHealthSnapshot(
            batteryLevel = prefs.getInt(KEY_WATCH_BATTERY_LEVEL, -1),
            isCharging = prefs.getBoolean(KEY_WATCH_IS_CHARGING, false),
            lowPowerMode = prefs.getBoolean(KEY_WATCH_LOW_POWER, false),
            syncLimited = prefs.getBoolean(KEY_WATCH_SYNC_LIMITED, false),
            message = prefs.getString(KEY_WATCH_STATUS_MESSAGE, "").orEmpty(),
            updatedAtEpochMs = prefs.getLong(KEY_WATCH_STATUS_UPDATED_AT, 0L),
        )
        return snapshot.takeIf { it.shouldDisplay(nowEpochMs) }
    }

    fun loadHistory(): List<Int> {
        val raw = prefs.getString(GlucoseKeys.HISTORY, "").orEmpty()
        if (raw.isBlank()) return emptyList()

        return raw.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .takeLast(HISTORY_LIMIT)
    }

    private fun updatedHistory(snapshot: GlucoseSnapshot): List<Int> {
        val existing = loadHistory().toMutableList()
        if (existing.lastOrNull() != snapshot.valueMgDl) {
            existing += snapshot.valueMgDl
        }
        return existing.takeLast(HISTORY_LIMIT)
    }

    companion object {
        private const val HISTORY_LIMIT = 12
        private const val STALE_AFTER_MS = 2 * 60 * 1000L
        private const val KEY_REFRESH_STATUS = "refresh_status"
        private const val KEY_REFRESH_MESSAGE = "refresh_message"
        private const val KEY_REFRESH_UPDATED_AT = "refresh_updated_at"
        private const val KEY_LAST_PHONE_UPDATE_RECEIVED_AT = "last_phone_update_received_at"
        private const val KEY_WATCH_BATTERY_LEVEL = "watch_battery_level"
        private const val KEY_WATCH_IS_CHARGING = "watch_is_charging"
        private const val KEY_WATCH_LOW_POWER = "watch_low_power"
        private const val KEY_WATCH_SYNC_LIMITED = "watch_sync_limited"
        private const val KEY_WATCH_STATUS_MESSAGE = "watch_status_message"
        private const val KEY_WATCH_STATUS_UPDATED_AT = "watch_status_updated_at"
    }
}
