package com.glucoseforwatch.feature.sync

/** French user-facing strings for sync feedback, refresh status, and degraded mode. */
object SyncMessageCatalog {
    const val REFRESH_IN_PROGRESS = "Actualisation..."
    const val REFRESH_NO_NEW_READING = "Aucune nouvelle mesure"
    const val REFRESH_PHONE_UP_TO_DATE_WATCH_UNAVAILABLE =
        "Téléphone à jour ; liaison montre indisponible pour l’envoi."
    const val SYNC_TIMEOUT = "Délai dépassé pendant la synchronisation."
    const val UNKNOWN_ERROR = "Erreur inconnue"
    const val SYNC_NEEDS_ATTENTION = "La synchronisation a besoin de votre attention."
    const val DEGRADED_SUFFIX = "Mode dégradé montre actif (<20 % batterie) : cadence réduite."
}
