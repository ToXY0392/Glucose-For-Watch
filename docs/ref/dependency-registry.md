# Registre plat des dépendances — liens documentation

Vue tabulaire alignée sur [dependency-catalog.yaml](dependency-catalog.yaml). À toute modification des `build.gradle.kts`, mettre à jour **les deux** fichiers et, si besoin, [README.md](README.md) et les fiches thématiques.

**Référence :** `build.gradle.kts` (racine), `mobile/build.gradle.kts`, `wear/build.gradle.kts`.

---

## Chaîne de build (racine)

| Élément | Version référencée | Documentation |
| --- | --- | --- |
| Android Gradle Plugin | 8.13.2 | https://developer.android.com/build/releases/gradle-plugin |
| Plugin Kotlin Android | 2.3.20 | https://kotlinlang.org/docs/home.html |
| JDK cible compilation | 17 | https://developer.android.com/build/jdks |
| `compileSdk` / `targetSdk` | 36 | https://developer.android.com/guide/topics/manifest/uses-sdk-element |
| Gradle (wrapper) | 8.13 (`gradle/wrapper/gradle-wrapper.properties`) | https://docs.gradle.org/current/userguide/userguide.html |
| Réduction code (release) | R8 / ProGuard rules | https://developer.android.com/build/shrink-code |
| ADB install local | `installWidgetG7Debug` | https://developer.android.com/tools/adb |

---

## Dépôts Maven (`settings.gradle.kts`)

| Dépôt | Doc / politique |
| --- | --- |
| Google Maven | https://developer.android.com/studio/build#repositories-plugin |
| Maven Central | https://central.sonatype.org/ |
| JitPack | https://docs.jitpack.io/ |

---

## Hors Maven — Dexcom Share (HTTP dans l’app)

| Sujet | Détail |
| --- | --- |
| Spécification publique `ShareWebServices` | Absente chez Dexcom ; voir [dexcom-share.md](dexcom-share.md) |
| Implémentation repo | `mobile/.../dexcom/DexcomSharePhoneGlucoseSource.kt`, `DexcomShareConfig.kt` |
| Alternative officielle | API OAuth v3 — https://developer.dexcom.com/ |

---

## Module `:mobile`

| Dépendance `implementation` | Doc principale | Fiche dossier |
| --- | --- | --- |
| `com.google.mlkit:text-recognition:16.0.1` | https://developers.google.com/ml-kit/vision/text-recognition | [google-android-jetpack.md](google-android-jetpack.md) |
| `androidx.exifinterface:exifinterface:1.4.1` | https://developer.android.com/jetpack/androidx/releases/exifinterface | [google-android-jetpack.md](google-android-jetpack.md) |
| `com.flyfishxu:kadb:2.1.1` | https://github.com/flyfishxu/kadb | [kotlin-gradle-build.md](kotlin-gradle-build.md) |
| `androidx.core:core-ktx:1.18.0` | https://developer.android.com/jetpack/androidx/releases/core | [google-android-jetpack.md](google-android-jetpack.md) |
| `androidx.core:core-splashscreen:1.0.1` | https://developer.android.com/develop/ui/views/launch/splash-screen | [google-android-jetpack.md](google-android-jetpack.md) |
| `androidx.appcompat:appcompat:1.7.1` | https://developer.android.com/jetpack/androidx/releases/appcompat | [google-android-jetpack.md](google-android-jetpack.md) |
| `androidx.constraintlayout:constraintlayout:2.2.1` | https://developer.android.com/jetpack/androidx/releases/constraintlayout | [google-android-jetpack.md](google-android-jetpack.md) |
| `com.google.android.material:material:1.13.0` | https://github.com/material-components/material-components-android · https://m3.material.io/ | [google-android-jetpack.md](google-android-jetpack.md) |
| `androidx.lifecycle:lifecycle-runtime-ktx:2.10.0` | https://developer.android.com/topic/libraries/architecture/lifecycle | [google-android-jetpack.md](google-android-jetpack.md) |
| `androidx.security:security-crypto:1.1.0` | https://developer.android.com/jetpack/androidx/releases/security | [google-android-jetpack.md](google-android-jetpack.md) |
| `com.google.android.gms:play-services-wearable:19.0.0` | https://developers.google.com/android/reference/com/google/android/gms/wearable/package-summary | [google-wear-os.md](google-wear-os.md) |
| `androidx.wear:wear-remote-interactions:1.2.0` | https://developer.android.com/jetpack/androidx/releases/wear-remote-interactions | [google-wear-os.md](google-wear-os.md) |
| `androidx.work:work-runtime-ktx:2.11.2` | https://developer.android.com/topic/libraries/architecture/workmanager | [google-android-jetpack.md](google-android-jetpack.md) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2` | https://kotlinlang.org/docs/coroutines-guide.html · https://github.com/Kotlin/kotlinx.coroutines/tree/master/integration/kotlinx-coroutines-play-services | [google-android-jetpack.md](google-android-jetpack.md) |

---

## Module `:wear`

| Dépendance `implementation` | Doc principale | Fiche dossier |
| --- | --- | --- |
| `androidx.core:core-ktx:1.18.0` | https://developer.android.com/jetpack/androidx/releases/core | [google-android-jetpack.md](google-android-jetpack.md) |
| `com.google.android.gms:play-services-wearable:19.0.0` | https://developers.google.com/android/reference/com/google/android/gms/wearable/package-summary | [google-wear-os.md](google-wear-os.md) |
| `androidx.wear:wear:1.3.0` | https://developer.android.com/jetpack/androidx/releases/wear | [google-wear-os.md](google-wear-os.md) |
| `androidx.wear.watchface:watchface-complications-data-source-ktx:1.2.1` | https://developer.android.com/training/wearables/user-interface/complications | [google-wear-os.md](google-wear-os.md) |
| `androidx.wear.tiles:tiles:1.5.0` | https://developer.android.com/training/wearables/tiles | [google-wear-os.md](google-wear-os.md) |
| `androidx.wear.tiles:tiles-material:1.5.0` | https://developer.android.com/jetpack/androidx/releases/wear-tiles | [google-wear-os.md](google-wear-os.md) |
| `com.google.guava:guava:33.2.1-android` | https://github.com/google/guava/wiki | [kotlin-gradle-build.md](kotlin-gradle-build.md) |

---

## Source de vérité structurée

- Fichier machine : [dependency-catalog.yaml](dependency-catalog.yaml)
- Point d’entrée humain : [README.md](README.md)
