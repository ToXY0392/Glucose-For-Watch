package com.widgetg7.mobile.watch.install

import com.flyfishxu.kadb.Kadb
import java.io.File

/**
 * Installation de l’APK Wear via le débogage Wi‑Fi (protocole ADB), sans PC.
 * Nécessite jumelage (Android 11+) puis connexion sur le port affiché par la montre.
 */
object WearDirectAdbInstaller {
    suspend fun pair(host: String, pairPort: Int, pairingCode: String) {
        Kadb.pair(host, pairPort, pairingCode)
    }

    suspend fun installApk(host: String, adbPort: Int, apkFile: File) {
        Kadb.create(host, adbPort).use { kadb ->
            kadb.install(apkFile)
        }
    }
}
