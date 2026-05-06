# Kotlin, Gradle et outillage de build

**Id :** `kotlin-gradle-build`  
Toolchain : `build.gradle.kts` racine, `gradle-wrapper.properties`, Java 17, modules `mobile` et `wear`.

## Documentation

| Sujet | URL |
| --- | --- |
| Kotlin | https://kotlinlang.org/docs/home.html |
| Manuel Gradle | https://docs.gradle.org/current/userguide/userguide.html |
| Android Gradle Plugin | https://developer.android.com/build |
| Notes AGP | https://developer.android.com/build/releases/gradle-plugin |
| JDK / builds Android | https://developer.android.com/build/jdks |
| R8 / réduction de code | https://developer.android.com/build/shrink-code |
| adb | https://developer.android.com/tools/adb |

## Dépendances tierces (hors table AndroidX du REGISTRY)

| Artefact | URL |
| --- | --- |
| `com.flyfishxu:kadb` | https://github.com/flyfishxu/kadb |
| `com.google.guava:guava` (variant Android, module wear) | https://github.com/google/guava/wiki |

Versions et alignement complet : [dependency-catalog.yaml](dependency-catalog.yaml) (`build_toolchain`, `gradle_plugins`, `dependencies`).
