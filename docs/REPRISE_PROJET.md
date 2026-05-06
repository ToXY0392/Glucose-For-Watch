<h1 align="center">Reprise Projet</h1>

<p align="center">
  Où on en est · ce qui est décidé · quoi vérifier ensuite
</p>

---

## État actuel

```text
╭─ Widget G7 ────────────────────────────╮
│ Mobile      : Pixel 8a testé           │
│ Wear        : Pixel Watch 2 testée     │
│ Sync        : Dexcom Share -> Wear OS  │
│ Service     : foreground actif         │
│ Direct G7   : expérimental, non codé   │
╰────────────────────────────────────────╯
```

| Sujet | État |
| --- | --- |
| Modules | `mobile`, `wear` |
| Version mobile (debug) | `versionCode` 22, `versionName` 0.3.1 |
| Version wear (debug) | `versionCode` 2, `versionName` 0.3.1 (même **applicationId** que le téléphone) |
| Mode principal | `Dexcom Share -> téléphone -> Wear OS` |
| Sync active | Service foreground, ack montre, repush borné |
| Multi-montres | Montre principale ciblée avec `targetNodeId` |
| Direct capteur | Documenté, hors app principale |

---

## Outils et build (à connaître)

| Élément | Détail |
| --- | --- |
| AGP | 8.13.x (`build.gradle.kts` racine) |
| Gradle wrapper | **8.13** obligatoire (pas Gradle 9 : casse souvent la sync Android Studio). Voir `gradle/wrapper/gradle-wrapper.properties`. |
| Logs IDE | `gradle.properties` : `org.gradle.console=plain`, Kotlin `in-process` pour limiter erreurs daemon / Build vide |
| Builds verbeux | Fichier `build-last.log` à la racine du repo si tu as besoin de voir tout le détail hors panneau Build |
| Install dev phone + montre | Tâche `installWidgetG7Debug` dans `build.gradle.kts` racine : nécessite `widgetg7.adb.phone.serial`, `widgetg7.adb.watch.serial` dans `local.properties` et SDK via `sdk.dir` **ou** `ANDROID_SDK_ROOT`/`ANDROID_HOME` |

---

## Icône lanceur (mobile)

| Point | Détail |
| --- | --- |
| Adaptive | `mobile/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` → fond blanc + foreground bitmap depuis `logo_widget_g7_official` |
| Génération | `tools/generate_launcher_icon_assets.py` — défaut **`--radius-frac 0.56`** (goutte petite, marge blanche). À ajuster si besoin. |
| PNG mono | Toujours généré à côté pour usage futur, mais **`monochrome`** n’est pas référencé dans `ic_launcher.xml` pour limiter les icônes “thème” blanc-sur-pastille sur Pixel |
| Rappel | La densité du tiroir d’applications (taille globale des pastilles) dépend du lanceur / affichage système — ce n’est pas piloté par l’APK |

---

## Déploiement développement (piège à éviter)

| Appareil | APK à installer |
| --- | --- |
| **Téléphone** | `mobile/build/outputs/apk/debug/mobile-debug.apk` |
| **Montre** | `wear/build/outputs/apk/debug/wear-debug.apk` |

Même **`applicationId` `com.widgetg7.mobile`** : la dernière install sur **chaque** appareil définit quel binaire est présent. Mettre **`mobile-debug` sur la montre** peut faire disparaître tuile/complication jusqu’à réinstallation **`wear-debug`**.

Après désinstallation complète : **ré-ajouter manuellement** la tuile / la complication sur le cadran (`Personnaliser` → complication ou tuiles).

---

## Décisions à garder

### Sync standard

```text
Dexcom Share -> Widget G7 mobile -> Widget G7 Wear
```

| Mécanisme | Rôle |
| --- | --- |
| Service foreground | Maintient la surveillance |
| Boucle dans le service (~**45 s**) | Réveil régulier côté `ActiveGlucoseSyncService` (`POLL_INTERVAL_MS`) |
| Alarme fichiert (`PhoneAutoSyncScheduler`, ~**90 s**) | Filet AlarmManager (`setAndAllowWhileIdle`) |
| `sequenceId` | Trace chaque push |
| Ack montre | Confirme la livraison |
| Repush borné | Répare une livraison non confirmée |
| `WorkManager` | Filet complémentaire (receiver / worker) |

La montre reste un affichage et un déclencheur de refresh. Elle ne collecte pas directement le capteur.

### Direct capteur

| Point | Décision |
| --- | --- |
| Support officiel | Direct to Watch documenté pour Apple Watch |
| Wear OS | Aucun support officiel équivalent trouvé |
| Piste technique | `Wear Collector` expérimental |
| Règle | Pas d'intégration sans spike BLE concluant |

Documentation : [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md)

---

## Fonctionnel en place

| Bloc | Détail |
| --- | --- |
| Dexcom | Connexion, région, stockage local des identifiants |
| Juridique | Acceptation avant connexion Dexcom |
| Montre | Test d'envoi, choix montre principale, sync en veille |
| Wear | App, tile, complication (`GlucoseSimpleTileService`, `GlucoseComplicationService`) |
| Sync | Ack, repush, cache Wear, logs sensibles retirés |
| Install montre | Assistant `WearInstallerActivity` : ADB Wi‑Fi (Kadb) + APK embarqué (**debug uniquement**, voir `prepareWearApkForDebugAssets` dans `mobile/build.gradle.kts`) ; OCR photo ML Kit (préremplissage — **toujours vérifier**, saisie manuelle en secours) |

---

## Fichiers importants

| Fichier | Rôle |
| --- | --- |
| [ActiveGlucoseSyncService.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/ActiveGlucoseSyncService.kt) | Service foreground |
| [PhoneGlucoseSyncEngine.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneGlucoseSyncEngine.kt) | Moteur de sync |
| [PhoneSyncStateStore.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneSyncStateStore.kt) | État local téléphone |
| [PhoneWearSyncService.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearSyncService.kt) | Push vers Wear |
| [WatchConnectionRepository.kt](../mobile/src/main/java/com/widgetg7/mobile/watch/WatchConnectionRepository.kt) | Montre principale |
| [WearDataLayerListenerService.kt](../wear/src/main/java/com/widgetg7/wear/services/WearDataLayerListenerService.kt) | Réception Wear |
| [GlucoseCache.kt](../wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt) | Cache local Wear |
| [WearInstallerActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/ui/WearInstallerActivity.kt) | Assistant installation montre |
| [WearDirectAdbInstaller.kt](../feature/watch-install/src/main/java/com/widgetg7/feature/watchinstall/WearDirectAdbInstaller.kt) | Pair / install Kadb |
| [WearInstallOcr.kt](../feature/watch-install/src/main/java/com/widgetg7/feature/watchinstall/WearInstallOcr.kt) | ML Kit — lecture image |
| [generate_launcher_icon_assets.py](../tools/generate_launcher_icon_assets.py) | Régénération PNG lanceur (radius fraction) |

---

## Audits et suite documentaire récents

| Document | Contenu |
| --- | --- |
| [AUDIT_TECHNIQUE_2026-05-06.md](AUDIT_TECHNIQUE_2026-05-06.md) | Risques produit/sync, backlog priorisé, plan par phases |
| [AUDIT_STRUCTURE_GLOBALE_2026-05-06.md](AUDIT_STRUCTURE_GLOBALE_2026-05-06.md) | Structure cible, modularisation progressive, conventions |
| [operations/BILAN_FINAL_PLAN_REFONTE_2026-05-06.md](operations/BILAN_FINAL_PLAN_REFONTE_2026-05-06.md) | Clôture d'exécution du plan (done/pending bloquants + go/no-go) |

Indexer aussi : [DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md), [STRUCTURE_REPO.md](STRUCTURE_REPO.md)

---

## Points ouverts

| Priorité | Travail |
| --- | --- |
| 1 | Valider la sync active en veille longue |
| 2 | Vérifier l'exemption batterie sur appareils réels |
| 3 | Tester avec deux montres connectées |
| 4 | Surveiller que le repush reste borné |
| 5 | Documenter les retards possibles de Dexcom Share |
| 6 | Compléter les champs juridiques avant diffusion |
| — | OCR assistant montre : valider sur vraies captures ; revue humaine IP / ports (cf. [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md)) |
| — | Aligner versioning **wear vs mobile** (aujourd'hui codes différents malgré même `applicationId`) pour le support utilisateur |

---

## Commandes utiles

> Linux / Unix local (JDK)

```bash
export JAVA_HOME="/path/to/android-studio/jbr"
export PATH="$JAVA_HOME/bin:$PATH"
```

> Builds

```bash
./gradlew :mobile:assembleDebug :wear:assembleDebug
./gradlew :mobile:assembleRelease :wear:assembleRelease
```

> Déploiement debug (avec `local.properties` rempli)

```bash
./gradlew installWidgetG7Debug
```

> Gate CI locale (Linux/Unix)

```bash
./scripts/dev/verify_ci.sh
```

> Collecte diagnostics support sync (optionnel)

```bash
WIDGETG7_PHONE_SERIAL="<serial_phone>" WIDGETG7_WATCH_SERIAL="<serial_watch>" bash ./scripts/dev/collect_sync_diagnostics.sh
```

> Gate artefacts release (Linux/Unix)

```bash
./scripts/release/verify_release_artifacts.sh
```

> Dry-run release complet (technique + juridique)

```bash
./scripts/release/release_dry_run.sh
```

> Campagne E2E phase 5 (template)
>
> Voir `docs/operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md`.

> Preparation du pack de preuves E2E

```bash
bash ./scripts/dev/prepare_e2e_evidence_pack.sh
```

> Icône lanceur

```bash
python3 ./tools/generate_launcher_icon_assets.py --radius-frac 0.56
./gradlew :mobile:assembleDebug
```

> ADB

```bash
adb devices -l
```

> Windows équivalent rapide
>
> `.\gradlew.bat ...`, `py -3 .\tools\generate_launcher_icon_assets.py ...`, et `adb.exe devices -l`.

---

## Reprise conseillée (session suivante)

```text
╭─ Prochaine session ────────────────────╮
│  > sync Gradle (Gradle 8.13)           │
│  > assembleDebug mobile + wear         │
│  > mobile-debug sur téléphone          │
│  > wear-debug sur montre               │
│  > vérifier tuile + complication       │
│  > foreground + ack après veille longue │
│  > multi-montres                       │
╰────────────────────────────────────────╯
```
