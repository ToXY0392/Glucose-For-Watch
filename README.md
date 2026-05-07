# Widget G7

Widget G7 synchronise la glycémie Dexcom G7 vers Wear OS pour un affichage rapide sur montre (app, tuile, complication).

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-Mobile-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Wear OS" src="https://img.shields.io/badge/Wear%20OS-Watch-4285F4?style=for-the-badge&logo=wearos&logoColor=white">
  <img alt="Gradle" src="https://img.shields.io/badge/Gradle-8.13-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
  <img alt="Sync" src="https://img.shields.io/badge/Sync-Validated%2030min-22C55E?style=for-the-badge">
</p>

## Sommaire

- [Aperçu](#aperçu)
- [Highlights](#highlights)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Quick Start](#quick-start)
- [Build et APK](#build-et-apk)
- [ADB (optionnel)](#adb-optionnel)
- [Dépannage](#dépannage)
- [FAQ](#faq)
- [Documentation](#documentation)
- [Sécurité](#sécurité)
- [Avertissement médical](#avertissement-médical)

## Aperçu

Ce projet:

- lit la dernière glycémie via Dexcom Share (mobile),
- pousse la donnée vers la montre via Wear Data Layer,
- met à jour la tuile et la complication,
- conserve un état de sync (ack, timestamp, stale) pour diagnostic.

Flux principal:

`Dexcom Share -> Mobile -> Wear OS`

## Highlights

- Sync téléphone -> montre validée en conditions réelles (monitoring 30 min).
- Tuile et complication mises à jour via cache local Wear + ack Data Layer.
- Diagnostic facilité par état de sync persistant (timestamps, stale, dernier ack).
- Build mobile et wear séparés, installation ADB directe.

## Architecture

- `mobile/` : récupération Dexcom, orchestration sync, état local.
- `wear/` : cache montre, tile, complication, ack.
- `core/` : modèles partagés.
- `feature/` : logique métier de sync.
- `docs/` : guides utilisateur/dev/légal.

## Prérequis

- Android Studio récent.
- Gradle wrapper du repo: `8.13`.
- JDK Gradle: `jbr-21` (Android Studio JBR).
- SDK Android installé (`local.properties` avec `sdk.dir`).
- 1 téléphone Android + 1 montre Wear OS pour tests réels.

## Quick Start

1. Installer `mobile-debug.apk` sur le téléphone.
2. Installer `wear-debug.apk` sur la montre.
3. Ouvrir Widget G7 sur le téléphone.
4. Accepter les écrans requis.
5. Connecter Dexcom Share.
6. Lancer un test de sync.
7. Ajouter la tuile ou la complication Widget G7.

## Build et APK

```powershell
.\gradlew.bat help
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```

Sorties:

- `mobile/build/outputs/apk/debug/mobile-debug.apk`
- `wear/build/outputs/apk/debug/wear-debug.apk`

Vérification rapide:

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug --stacktrace
```

## ADB (optionnel)

Lister les appareils:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices -l
```

Installer les APK:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s <PHONE_SERIAL> install -r "mobile\build\outputs\apk\debug\mobile-debug.apk"
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s <WATCH_SERIAL> install -r "wear\build\outputs\apk\debug\wear-debug.apk"
```

Désinstaller puis réinstaller proprement:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s <PHONE_SERIAL> uninstall com.widgetg7.mobile
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s <WATCH_SERIAL> uninstall com.widgetg7.mobile
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s <PHONE_SERIAL> install -r "mobile\build\outputs\apk\debug\mobile-debug.apk"
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s <WATCH_SERIAL> install -r "wear\build\outputs\apk\debug\wear-debug.apk"
```

## Dépannage

### Sync Gradle IDE instable

Vérifier `gradle.properties`:

- `org.gradle.java.home=C:/Program Files/Android/Android Studio/jbr`
- `kotlin.compiler.execution.strategy=in-process`

Puis relancer:

```powershell
.\gradlew.bat --stop
.\gradlew.bat help
```

### Valeur figée sur montre

- Vérifier la valeur côté app mobile.
- Forcer un refresh montre.
- Si seule la complication est figée: retirer/remettre la complication.
- En dernier recours: réinstaller mobile + wear.

### Validation d'une sync saine

- valeur identique téléphone/montre,
- timestamp de lecture identique,
- `stale=false` lorsque la donnée est fraîche.

## FAQ

### La tuile est correcte mais la complication affiche une ancienne valeur

Retirer puis remettre la complication du cadran.  
La source sync est généralement bonne, c'est la surface UI qui reste en cache.

### La sync Gradle IDE échoue alors que `gradlew` passe en terminal

C'est souvent la config JDK/daemon IDE.  
Vérifier JBR + `kotlin.compiler.execution.strategy=in-process`, puis redémarrer la sync.

### Comment savoir si la sync tient dans le temps

Comparer régulièrement:

- valeur téléphone vs montre,
- timestamp lecture téléphone vs montre,
- état `stale` côté montre.

## Documentation

- `docs/developpement-double-ide-cursor-studio.md` — même dépôt WSL depuis **Cursor** et **Android Studio** (`\\wsl$\…`)
- `docs/index.md`
- `docs/user-quick-notice.md`
- `docs/user-manual.md`
- `docs/developer-handoff.md`
- `docs/technical-wear-os-sync.md`
- `docs/release-notes.md`

---

Développé pour garder la glycémie visible sur Wear OS avec un flux simple, traçable et robuste.

## Sécurité

- Ne jamais committer d'identifiants Dexcom.
- Ne jamais publier de glycémies réelles.
- Ne pas partager secrets, keystore, `local.properties`.

## Avertissement médical

Widget G7 n'est pas un dispositif médical certifié.  
Toute décision thérapeutique doit être confirmée via une solution Dexcom officielle.
