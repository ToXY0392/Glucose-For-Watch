# Structure du dépôt Widget G7

Référence courte pour retrouver vite le code et la documentation.

## Modules Gradle

| Module | Chemin | Rôle |
| --- | --- | --- |
| **mobile** | `mobile/` | App téléphone : Dexcom, sync foreground, push Data Layer vers la montre, assistant install APK Wear. |
| **wear** | `wear/` | App montre : écoute Data Layer, cache glucose, tuile et complication. |
| Racine | `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties` | Plugins communs, tâche `installWidgetG7Debug` (voir commentaire dans `build.gradle.kts`). |

## Code mobile (`mobile/src/main/java/com/widgetg7/mobile/`)

| Zone | Paquet / dossier | Rôle |
| --- | --- | --- |
| Sync | `sync/` | `ActiveGlucoseSyncService`, `PhoneGlucoseSyncEngine`, `PhoneWearSyncService`, `GlucoseKeys`, ack / repush, planning. |
| Dexcom | `dexcom/` | Client Share et erreurs associées. |
| Montre | `watch/` | `WatchConnectionRepository`, santé liaison, installer ADB / OCR (`watch/install/`). |
| UI | `ui/` | Dexcom, légal, `WatchSetupActivity`, `WearInstallerActivity`. |
| Données | `data/` | Source glucose téléphone (`PhoneGlucoseSourceFactory`). |

## Code Wear (`wear/src/main/java/com/widgetg7/wear/`)

| Zone | Paquet | Rôle |
| --- | --- | --- |
| Réception sync | `services/WearDataLayerListenerService.kt` | Entrée principale `/glucose/latest`, ack vers le téléphone. |
| Persistance | `data/GlucoseCache.kt` | Dernière valeur + statuts refresh. |
| Surfaces | `tile/`, `complication/` | Tuile et complication. |

## Documentation (`docs/`)

| Fichier | Usage |
| --- | --- |
| [INDEX.md](INDEX.md) | Plan de tous les documents. |
| [REPRISE_PROJET.md](REPRISE_PROJET.md) | État projet, fichiers critiques, vérifs. |
| [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) | Sync, chemins Data Layer, install montre (ADB / photo OCR). |

## Fichiers de config utiles

- `gradle.properties` — options **communes** au dépôt (ex. stratégie compilateur Kotlin). Pour une surcharge locale sans modifier ce fichier : préférences IDE ou ligne de commande `-P…`.
- `local.properties` — SDK Android + optionnellement serials (`widgetg7.adb.*`) pour `installWidgetG7Debug` (**jamais** committer).
- `mobile/src/main/AndroidManifest.xml` — services Wear listener, foreground sync.
- `wear/src/main/AndroidManifest.xml` — `WearDataLayerListenerService`, complication.
- `.cursor/rules/` — règles agent (ex. déploiement `installWidgetG7Debug`).

## Commandes racine

```powershell
# Build debug les deux APK
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug

# Install selon serials dans local.properties
.\gradlew.bat installWidgetG7Debug
```

Ne pas stocker identifiants Dexcom dans les fichiers versionnés : connexion depuis l’app (réglages utilisateur).
