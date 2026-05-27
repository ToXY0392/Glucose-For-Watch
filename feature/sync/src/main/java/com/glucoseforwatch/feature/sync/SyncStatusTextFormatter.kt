package com.glucoseforwatch.feature.sync

import com.glucoseforwatch.core.model.SyncErrorCategory
import com.glucoseforwatch.core.model.SyncStatusSnapshot
import java.text.DateFormat
import java.util.Date
import kotlin.math.max

/** Builds French status lines for Dexcom account and sync state on the phone home screen. */
object SyncStatusTextFormatter {
    fun dexcomStatus(
        dexcomConfigured: Boolean,
        serverLabel: String,
        syncStatus: SyncStatusSnapshot,
    ): String {
        if (!dexcomConfigured) return "Dexcom : à configurer"
        return when {
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                "Dexcom : reconnexion requise ($serverLabel)"

            syncStatus.hasSuccessfulSync() -> "Dexcom : connecté ($serverLabel)"
            else -> "Dexcom : configuré ($serverLabel)"
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

    fun dexcomAccountSummary(
        dexcomConfigured: Boolean,
        serverLabel: String,
        syncStatus: SyncStatusSnapshot,
    ): String {
        return when {
            !dexcomConfigured -> "Compte Dexcom non configuré"
            syncStatus.lastErrorCategory == SyncErrorCategory.AUTH && syncStatus.authFailureCount >= 2 ->
                "Compte Dexcom configuré, reconnexion requise"

            syncStatus.hasSuccessfulSync() -> "Compte Dexcom connecté ($serverLabel)"
            else -> "Compte Dexcom configuré ($serverLabel)"
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
