package com.widgetg7.feature.watchinstall

import com.flyfishxu.kadb.Kadb
import java.io.File

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
