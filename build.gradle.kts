import java.io.File
import java.util.Properties

// Arborescence modules + paquets : docs/STRUCTURE_REPO.md

plugins {
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
}

/**
 * Déploiement debug local (voir local.properties) :
 *
 *   widgetg7.adb.phone.serial=<id adb du téléphone>
 *   widgetg7.adb.watch.serial=<id adb de la montre>
 *
 * Ou variables d'environnement : WIDGETG7_PHONE_SERIAL, WIDGETG7_WATCH_SERIAL
 *
 * Commande : ./gradlew installWidgetG7Debug   (Windows : gradlew.bat installWidgetG7Debug)
 */
tasks.register("installWidgetG7Debug") {
    group = "widget g7"
    description = "assembleDebug (mobile + wear) puis adb install sur phone et montre configurés"
    dependsOn(":mobile:assembleDebug", ":wear:assembleDebug")

    doLast {
        val props =
            Properties().apply {
                val f = rootProject.layout.projectDirectory.file("local.properties").asFile
                if (f.isFile) f.inputStream().use { load(it) }
            }
        val sdkDir =
            props.getProperty("sdk.dir")?.trim()
                ?: error("sdk.dir manquant dans local.properties")
        val sdkRoot = File(sdkDir)
        val adbName =
            if (System.getProperty("os.name").orEmpty().contains("windows", ignoreCase = true)) {
                "adb.exe"
            } else {
                "adb"
            }
        val adb = File(sdkRoot, "platform-tools").resolve(adbName)
        if (!adb.isFile) {
            error("adb introuvable : ${adb.absolutePath}")
        }

        val phoneSerial =
            props.getProperty("widgetg7.adb.phone.serial")?.trim()
                ?: System.getenv("WIDGETG7_PHONE_SERIAL")?.trim()
                ?: error(
                    "Série phone manquante : ajoutez widgetg7.adb.phone.serial dans local.properties " +
                        "(liste : adb devices -l)",
                )
        val watchSerial =
            props.getProperty("widgetg7.adb.watch.serial")?.trim()
                ?: System.getenv("WIDGETG7_WATCH_SERIAL")?.trim()
                ?: error(
                    "Série montre manquante : ajoutez widgetg7.adb.watch.serial dans local.properties " +
                        "(liste : adb devices -l)",
                )

        val mobileApk =
            rootProject.layout.projectDirectory.file("mobile/build/outputs/apk/debug/mobile-debug.apk").asFile
        val wearApk =
            rootProject.layout.projectDirectory.file("wear/build/outputs/apk/debug/wear-debug.apk").asFile
        if (!mobileApk.isFile) error("APK mobile introuvable : ${mobileApk.absolutePath}")
        if (!wearApk.isFile) error("APK wear introuvable : ${wearApk.absolutePath}")

        fun adb(vararg args: String) {
            val pb = ProcessBuilder(adb.absolutePath, *args)
            pb.redirectErrorStream(true)
            val proc = pb.start()
            proc.inputStream.bufferedReader().use { println(it.readText()) }
            check(proc.waitFor() == 0) { "Commande adb échouée : adb ${args.joinToString(" ")}" }
        }

        println(">>> adb install mobile → $phoneSerial")
        adb("-s", phoneSerial, "install", "-t", "-r", mobileApk.absolutePath)
        println(">>> adb install wear → $watchSerial")
        adb("-s", watchSerial, "install", "-t", "-r", wearApk.absolutePath)
        println(">>> installWidgetG7Debug terminé")
    }
}
