# Structure du dépôt Widget G7

Référence courte pour retrouver modules, code principal et documentation.

## Modules Gradle (`settings.gradle.kts`)

| Module | Chemin | Rôle |
| --- | --- | --- |
| `mobile` | `mobile/` | App téléphone : Dexcom Share, sync foreground, envoi Data Layer, assistant installation APK Wear. |
| `wear` | `wear/` | App montre : écoute Data Layer, cache glucose, tuile, complication. |
| Racine | `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties` | Plugins, tâche `installWidgetG7Debug` (détails en commentaire dans `build.gradle.kts`). |

## Code mobile (`mobile/src/main/java/com/widgetg7/mobile/`)

| Zone | Dossier | Rôle |
| --- | --- | --- |
| Sync | `sync/` | Service foreground, moteur sync, `GlucoseKeys`, ack / repush. |
| Dexcom | `dexcom/` | Client Share, configuration, erreurs. |
| Montre | `watch/` | Connexion noeuds, santé sync, install ADB / OCR (`watch/install/`). |
| UI | `ui/` | Écrans Dexcom, légal, montre, installateur Wear. |
| Données | `data/` | Fabrique source glucose téléphone. |

## Code Wear (`wear/src/main/java/com/widgetg7/wear/`)

| Zone | Fichier / dossier | Rôle |
| --- | --- | --- |
| Data Layer | `services/WearDataLayerListenerService.kt` | Réception `/glucose/latest`, ack. |
| Persistance | `data/GlucoseCache.kt` | Dernière valeur, statuts refresh. |
| Surfaces | `tile/`, `complication/` | Tuile et complication. |

## Documentation (`docs/`)

| Fichier | Usage |
| --- | --- |
| [index.md](index.md) | Table des matières. |
| [technical-wear-os-sync.md](technical-wear-os-sync.md) | Architecture sync, Data Layer, assistant install Wear. |
| [developer-handoff.md](developer-handoff.md) | Reprise du travail, état du dépôt. |
| [ref/README.md](ref/README.md) | Liens documentation fournisseurs. |
| [ref/dependency-catalog.yaml](ref/dependency-catalog.yaml) | Registre YAML (toolchain, dépendances, URLs). |
| [ref/dependency-registry.md](ref/dependency-registry.md) | Table Gradle ↔ docs officielles. |

Documentation à jour listée dans [index.md](index.md).

## Configuration utile

- `gradle.properties` — options partagées du dépôt.  
- `local.properties` — SDK Android ; serials USB optionnels pour `installWidgetG7Debug` (ne pas versionner).  
- `mobile/src/main/AndroidManifest.xml`, `wear/src/main/AndroidManifest.xml` — déclarations services et surfaces Wear.  
- `.cursor/rules/` — règles d’assistance Cursor.

## Commandes

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat installWidgetG7Debug
```

Les identifiants Dexcom se saisissent dans l’application, pas dans les fichiers versionnés.
