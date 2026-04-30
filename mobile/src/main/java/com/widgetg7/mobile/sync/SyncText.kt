package com.widgetg7.mobile.sync

import com.widgetg7.mobile.dexcom.DexcomShareErrorKind
import com.widgetg7.mobile.dexcom.DexcomShareException
import com.widgetg7.mobile.settings.DexcomUserSettings
import com.widgetg7.mobile.status.SyncErrorCategory
import com.widgetg7.mobile.status.SyncStatusSnapshot
import java.text.DateFormat
import java.util.Date
import kotlin.math.max

object SyncText {
    fun displayServer(server: String): String = if (server.equals("US", true)) "US" else "Europe"

    fun displayTrend(trend: String): String = when (trend) {
        "UP" -> "en hausse"
        "UP_RIGHT" -> "en hausse légère"
        "FLAT" -> "stable"
        "DOWN_RIGHT" -> "en baisse légère"
        "DOWN" -> "en baisse"
        else -> trend
    }

    fun toCategory(t: Throwable): SyncErrorCategory {
        if (t is DexcomShareException) {
            return when (t.kind) {
                DexcomShareErrorKind.AUTH -> SyncErrorCategory.AUTH
                DexcomShareErrorKind.SESSION -> SyncErrorCategory.NETWORK
                DexcomShareErrorKind.NETWORK -> SyncErrorCategory.NETWORK
                DexcomShareErrorKind.NO_DATA -> SyncErrorCategory.OTHER
                DexcomShareErrorKind.UNKNOWN -> SyncErrorCategory.OTHER
            }
        }
        return SyncErrorCategory.OTHER
    }

    fun toUserMessage(t: Throwable): String {
        if (t.message?.contains("timed out", ignoreCase = true) == true) {
            return "Délai dépassé pendant la synchronisation."
        }
        return when {
            t is DexcomShareException -> t.message
            else -> t.message ?: "Erreur inconnue"
        }
    }

    fun dexcomStatus(settings: DexcomUserSettings, syncStatus: SyncStatusSnapshot): String {
        if (!settings.isConfigured()) return "Dexcom : à configurer"
        return when {
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                "Dexcom : reconnexion requise (${displayServer(settings.server)})"

            syncStatus.hasSuccessfulSync() -> "Dexcom : connecté (${displayServer(settings.server)})"
            else -> "Dexcom : configuré (${displayServer(settings.server)})"
        }
    }

    fun syncStatus(syncStatus: SyncStatusSnapshot): String {
        return when {
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                "État : reconnectez votre compte Dexcom"

            syncStatus.lastError.isNotBlank() ->
                "État : ${syncStatus.lastError}${ageSuffix(syncStatus.lastSyncEpochMs)}"

            syncStatus.hasSuccessfulSync() ->
                "État : synchronisation active (${syncStatus.lastSourceName})${ageSuffix(syncStatus.lastSyncEpochMs)}"

            else -> "État : en attente d'une première synchronisation"
        }
    }

    fun lastSync(syncStatus: SyncStatusSnapshot): String {
        if (syncStatus.lastSyncEpochMs <= 0L) return "Dernière sync : aucune pour le moment"
        val formatted = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            .format(Date(syncStatus.lastSyncEpochMs))
        return "Dernière sync : $formatted${ageSuffix(syncStatus.lastSyncEpochMs)}"
    }

    fun dexcomAccountSummary(settings: DexcomUserSettings, syncStatus: SyncStatusSnapshot): String {
        return when {
            !settings.isConfigured() -> "Compte Dexcom non configuré"
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                "Compte Dexcom configuré, reconnexion requise"

            syncStatus.hasSuccessfulSync() -> "Compte Dexcom connecté (${displayServer(settings.server)})"
            else -> "Compte Dexcom configuré (${displayServer(settings.server)})"
        }
    }

    private fun ageSuffix(epochMs: Long): String {
        if (epochMs <= 0L) return ""
        val ageMinutes = max(0L, (System.currentTimeMillis() - epochMs) / 60_000L)
        return when (ageMinutes) {
            0L -> " - à l'instant"
            1L -> " - il y a 1 min"
            else -> " - il y a $ageMinutes min"
        }
    }
}
