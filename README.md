# Widget G7

Widget G7 affiche la glycémie Dexcom G7 sur Wear OS en passant par le téléphone.

## Ce que fait le projet

- Récupère la dernière glycémie depuis Dexcom Share (côté mobile).
- Synchronise la donnée vers la montre via Wear Data Layer.
- Met à jour l'app Wear, la tuile et la complication.
- Garde un état de sync local (ack, dernière valeur, état stale).

## Architecture rapide

- `mobile/` : app téléphone, source Dexcom, moteur de sync.
- `wear/` : app montre, cache local, tile, complication.
- `core/` : modèles partagés et contrats.
- `feature/` : logique métier modulaire.
- `docs/` : documentation projet.

Flux principal:

`Dexcom Share -> mobile -> Wear OS`

## Prérequis

- Android Studio (version récente).
- Gradle wrapper du repo (`8.13`).
- JDK Gradle: JBR Android Studio (`jbr-21` recommandé).
- SDK Android installé (`local.properties` avec `sdk.dir`).
- Un téléphone Android + une montre Wear OS (pour tests réels).

## Installation utilisateur (APK)

1. Installer `mobile-debug.apk` sur le téléphone.
2. Installer `wear-debug.apk` sur la montre.
3. Ouvrir l'app mobile.
4. Accepter les écrans requis.
5. Connecter Dexcom Share.
6. Lancer un test de sync vers la montre.
7. Ajouter la tuile/complication Widget G7 au cadran.

## Build depuis le code source

```powershell
.\gradlew.bat help
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```

APK générés:

- Mobile: `mobile/build/outputs/apk/debug/mobile-debug.apk`
- Wear: `wear/build/outputs/apk/debug/wear-debug.apk`

## Déploiement ADB (optionnel)

Lister les appareils:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" devices -l
```

Installer les APK:

```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s <PHONE_SERIAL> install -r "mobile\build\outputs\apk\debug\mobile-debug.apk"
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" -s <WATCH_SERIAL> install -r "wear\build\outputs\apk\debug\wear-debug.apk"
```

## Dépannage rapide

### Sync Gradle IDE instable

- Vérifier `gradle.properties`:
  - `org.gradle.java.home=C:/Program Files/Android/Android Studio/jbr`
  - `kotlin.compiler.execution.strategy=in-process`
- Relancer:

```powershell
.\gradlew.bat --stop
.\gradlew.bat help
```

### Valeur figée sur montre

- Vérifier la valeur côté mobile (dans l'app).
- Forcer un refresh depuis la montre.
- Si la tuile est correcte mais pas la complication: retirer/remettre la complication.
- Si besoin: réinstaller proprement mobile + wear.

### Validation sync

Une sync saine présente:

- mêmes valeurs téléphone/montre
- mêmes timestamps de lecture
- `stale=false` quand lecture fraîche

## Documentation

- `docs/index.md` : index global.
- `docs/user-quick-notice.md` : installation express.
- `docs/user-manual.md` : utilisation détaillée.
- `docs/developer-handoff.md` : état projet et incidents.
- `docs/technical-wear-os-sync.md` : détails techniques sync.
- `docs/release-notes.md` : historique de changements.

## Sécurité et confidentialité

- Ne jamais committer d'identifiants Dexcom.
- Ne jamais publier de données glycémie réelles.
- Ne pas partager `local.properties`, secrets, keystores.

## Avertissement médical

Widget G7 n'est pas un dispositif médical certifié.  
Toujours confirmer une décision thérapeutique avec une solution Dexcom officielle.
