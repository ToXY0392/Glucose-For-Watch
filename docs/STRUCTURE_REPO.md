# Structure du dépôt Widget G7

Référence courte pour retrouver vite le code et la documentation.

## Modules Gradle

| Module | Chemin | Rôle |
| --- | --- | --- |
| **core:datalayer-contract** | `core/datalayer-contract/` | Contrat Data Layer partagé (paths/keys/protocoles). |
| **core:model** | `core/model/` | Modèles de domaine partagés (`GlucoseReading`, états sync). |
| **core:testing** | `core/testing/` | Fixtures utilitaires partagées pour tests de modules. |
| **feature:sync** | `feature/sync/` | Orchestrateur/policies sync + publishers Wear + repository état sync. |
| **feature:dexcom-share** | `feature/dexcom-share/` | Client Dexcom Share et erreurs de domaine associées. |
| **feature:watch-install** | `feature/watch-install/` | ADB Wi-Fi, parser OCR et accès APK Wear embarqué. |
| **mobile** | `mobile/` | App téléphone : Dexcom, sync foreground, push Data Layer vers la montre, assistant install APK Wear. |
| **wear** | `wear/` | App montre : écoute Data Layer, cache glucose, tuile et complication. |
| Racine | `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties` | Plugins communs, tâche `installWidgetG7Debug` (voir commentaire dans `build.gradle.kts`). |

## Code mobile (`mobile/src/main/java/com/widgetg7/mobile/`)

| Zone | Paquet / dossier | Rôle |
| --- | --- | --- |
| Sync | `sync/` | Adaptateurs Android (`ActiveGlucoseSyncService`, `PhoneGlucoseSyncEngine`, wrappers vers `feature:sync`). |
| Dexcom | `data/` + `ui/` | Intégration Dexcom via `feature:dexcom-share` (config et client branchés depuis la couche app). |
| Montre | `watch/` | `WatchConnectionRepository`, santé liaison, wrappers compatibilité install (`watch/install/`). |
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
| `product/README.md` | Point d'ancrage pour la doc produit cible. |
| `legal/README.md` | Point d'ancrage pour la doc légale cible. |

## Fichiers de config utiles

- `gradle.properties` — options **communes** au dépôt (ex. stratégie compilateur Kotlin). Pour une surcharge locale sans modifier ce fichier : préférences IDE ou ligne de commande `-P…`.
- `local.properties` — SDK Android + optionnellement serials (`widgetg7.adb.*`) pour `installWidgetG7Debug` (**jamais** committer).
- `mobile/src/main/AndroidManifest.xml` — services Wear listener, foreground sync.
- `wear/src/main/AndroidManifest.xml` — `WearDataLayerListenerService`, complication.
- `.cursor/rules/` — règles agent (ex. déploiement `installWidgetG7Debug`).
- `.github/workflows/ci.yml` — gate CI Linux (build/tests critiques).
- `scripts/dev/verify_ci.sh` — commande standardisée de vérification CI locale.
- `scripts/release/verify_release_artifacts.sh` — vérification release (assemble + présence APK mobile/wear).
- `scripts/release/check_legal_placeholders.sh` — blocage publication si placeholders juridiques.
- `scripts/release/release_dry_run.sh` — enchaîne gate CI, artefacts release et contrôle juridique.

## Commandes racine

```bash
# Build debug les deux APK
./gradlew :mobile:assembleDebug :wear:assembleDebug

# Install selon serials dans local.properties
./gradlew installWidgetG7Debug

# Gate CI locale (Linux/Unix)
./scripts/dev/verify_ci.sh

# Gate artefacts release (Linux/Unix)
./scripts/release/verify_release_artifacts.sh
```

La gate CI locale inclut désormais aussi `:feature:sync:testDebugUnitTest`.

Windows équivalent: `.\gradlew.bat ...`

Ne pas stocker identifiants Dexcom dans les fichiers versionnés : connexion depuis l’app (réglages utilisateur).
