# Android et Jetpack (module mobile)

**Id :** `google-android-jetpack`  
AndroidX, bibliothèques d’architecture et ML Kit utilisés dans `mobile/build.gradle.kts`.

## Point d’entrée

| Ressource | URL |
| --- | --- |
| Documentation Android | https://developer.android.com/ |
| Bibliothèques d’architecture (Lifecycle, guiding) | https://developer.android.com/topic/libraries/architecture |
| WorkManager | https://developer.android.com/topic/libraries/architecture/workmanager |
| Sécurité (Security Crypto / AndroidX Security) | https://developer.android.com/jetpack/androidx/releases/security |
| SplashScreen | https://developer.android.com/develop/ui/views/launch/splash-screen |
| Material 3 | https://m3.material.io/ |

## Dépendances et liens précis par artefact

Vue Gradle : [dependency-registry.md](dependency-registry.md) (section **:mobile**) ; détails : [dependency-catalog.yaml](dependency-catalog.yaml) (`dependencies`, module `:mobile`).

## Cas d’usage dans le projet

| Sujet | Emplacement dans le repo |
| --- | --- |
| Sync, planification | `mobile/.../sync/` |
| Assistant install montre (OCR) | `docs/technical-wear-os-sync.md`, `mobile/.../watch/install/` |
