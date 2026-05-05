package com.widgetg7.mobile.watch.install

import android.content.Context
import java.io.File
import java.security.MessageDigest

/** Accès à l\'APK Wear embarqué (assets `wear/widget-g7-wear.apk`), copie cache pour partage. */
class WearEmbeddedApkRepository(private val context: Context) {

    fun embeddedApkAvailable(): Boolean {
        val list = runCatching { context.assets.list(ASSET_FOLDER) }.getOrNull() ?: return false
        return ASSET_FILE_NAME in list
    }

    fun embeddedFileSizeBytes(): Long? {
        if (!embeddedApkAvailable()) return null
        return runCatching {
            context.assets.openFd("$ASSET_FOLDER/$ASSET_FILE_NAME").use { it.declaredLength }
        }.getOrNull()
    }

    fun embeddedSha256Hex(): String? {
        if (!embeddedApkAvailable()) return null
        return runCatching {
            val digest = MessageDigest.getInstance("SHA-256")
            context.assets.open("$ASSET_FOLDER/$ASSET_FILE_NAME").use { input ->
                val buf = ByteArray(8192)
                while (true) {
                    val n = input.read(buf)
                    if (n <= 0) break
                    digest.update(buf, 0, n)
                }
            }
            digest.digest().joinToString("") { b -> "%02x".format(b) }
        }.getOrNull()
    }

    /**
     * Copie vers le cache applicatif pour partage via FileProvider.
     * Retourne null si l\'asset est absent.
     */
    fun copyToCacheForShare(): File? {
        if (!embeddedApkAvailable()) return null
        val out = File(context.cacheDir, CACHE_SHARE_NAME)
        return runCatching {
            context.assets.open("$ASSET_FOLDER/$ASSET_FILE_NAME").use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            }
            out
        }.getOrNull()
    }

    companion object {
        const val ASSET_FOLDER = "wear"
        const val ASSET_FILE_NAME = "widget-g7-wear.apk"
        private const val CACHE_SHARE_NAME = "widget-g7-wear-share.apk"

        fun formatSize(bytes: Long): String {
            if (bytes <= 0) return "0 o"
            val kb = 1024.0
            return if (bytes < kb) {
                "$bytes o"
            } else {
                val mb = bytes / kb / kb
                if (mb >= 1.0) {
                    String.format("%.1f Mo", bytes / kb / kb)
                } else {
                    String.format("%.0f Ko", bytes / kb)
                }
            }
        }
    }
}
