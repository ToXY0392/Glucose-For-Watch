# Tracking Temporaire Refonte - Widget G7

Date de demarrage: 2026-05-06
Source: `docs/PLAN_REFONTE_COMPLETE_APP_2026-05-06.md`
Statut: cloture (execution plan terminee cote code/outillage/doc)

## Regle de mise a jour

Ce document est mis a jour a chaque step d'execution pour tracer:

- objectif du step,
- changements effectifs,
- validation (build/tests),
- prochain step.

---

## Step 0 - Initialisation du tracking

Objectif:
- Ouvrir une trace d'avancement continue avant de poursuivre la refonte.

Actions:
- Creation du document temporaire de tracking.
- Consolidation de l'avancement deja execute dans la session:
  - durcissement manifests mobile/wear,
  - observabilite sync,
  - mode degrade batterie montre,
  - retries/backoff,
  - listener wear non bloquant,
  - extraction `core:datalayer-contract`,
  - extraction `core:model`,
  - extraction initiale `feature:sync` (`BatteryDegradedPolicy`, `WearSyncPublisher`, `SyncExecutionResult`, `GlucoseSyncEngine` + adaptateur mobile).

Validation:
- Derniere validation globale disponible: build/tests verts.

Prochain step:
- Extraire la policy de gestion d'echec/notif vers `feature:sync` avec adaptation minimale cote mobile.

---

## Step 1 - Orchestrateur feature + policy d'echec

Objectif:
- Continuer l'extraction de la logique metier sync hors du module `mobile`.

Actions:
- Extraction d'un orchestrateur `GlucoseSyncEngine` dans `feature:sync` avec ports:
  - `GlucoseSourcePort`,
  - `SyncStatePort`,
  - `WearSyncPort`,
  - `RefreshStatusPort`.
- Adaptation de `PhoneGlucoseSyncEngine` en role de wiring/adaptateur Android.
- Extraction de la policy de notification d'echec vers `feature:sync`:
  - `SyncFailurePolicy`,
  - `SyncNotificationAction`.
- Branchement cote mobile de cette policy pour decider:
  - notification reconnexion Dexcom,
  - notification sync interrompue.

Validation:
- Build/tests relances apres extraction (compile mobile/wear + tests unitaires mobile): OK.

Prochain step:
- Commencer l'extraction de `PhoneWearRefreshStatusService` vers `feature:sync` (publisher de statut refresh) avec adaptateur mobile minimal.

---

## Step 2 - Extraction publisher refresh status

Objectif:
- Sortir la publication du statut refresh (in_progress/completed/failed) hors de `mobile`.

Actions:
- Creation de `feature:sync/RefreshStatusPublisher`.
- Delegation de `PhoneWearRefreshStatusService` vers ce publisher (wrapper de compatibilite cote `mobile`).
- Conservation du ciblage node montre via adaptateur mobile (`WatchConnectionRepository`).

Validation:
- Build/tests relances apres extraction: OK.

Prochain step:
- Extraire les snapshots/categorisations de statut sync vers `core:model` ou `feature:sync` pour reduire encore le couplage de `mobile`.

---

## Step 3 - Extraction des modeles de statut sync

Objectif:
- Deplacer les structures de statut sync hors de `mobile` pour clarifier les couches.

Actions:
- Creation de `core:model/SyncStatus.kt`:
  - `SyncErrorCategory`,
  - `SyncStatusSnapshot`.
- `SyncStatusRepository` conserve le stockage Android mais utilise desormais les types de `core:model`.
- Mise a jour des consommateurs `mobile`:
  - `SyncText`,
  - `MainActivity`,
  - `GlucoseDisplayFormatter`,
  - `DexcomSettingsActivity`,
  - `PhoneGlucoseSyncEngine`.

Validation:
- Build/tests relances (core:model + feature:sync + mobile/wear): OK.

Prochain step:
- Extraire le mapping d'erreur sync (`Throwable -> SyncErrorCategory`) dans `feature:sync` via une policy portable.

---

## Step 4 - Policy portable de classification d'erreurs

Objectif:
- Reducer la logique de classification d'erreurs dans `mobile` en la deplacant vers `feature:sync`.

Actions:
- Creation de `feature:sync/SyncErrorPolicy`:
  - classification timeout hint -> categorie reseau,
  - classification type Dexcom (`AUTH`, `SESSION`, `NETWORK`, ...) -> categorie sync.
- `mobile/sync/SyncText.toCategory()` convertit le contexte mobile en tokens puis delegue la decision a la policy feature.

Validation:
- Build/tests relances apres extraction: OK.

Prochain step:
- Commencer l'extraction du formattage d'etat sync (messages statut) pour reduire davantage `SyncText` et isoler le domaine.

---

## Step 5 - Stabilisation policy erreur + extraction refresh status

Objectif:
- Continuer a reduire le couplage mobile en externalisant les decisions de domaine sync.

Actions:
- Extraction `feature:sync/SyncErrorPolicy` et delegation depuis `mobile/sync/SyncText`.
- Creation `feature:sync/RefreshStatusPublisher` et delegation depuis `PhoneWearRefreshStatusService`.
- Deplacement des modeles de statut sync vers `core:model`:
  - `SyncErrorCategory`,
  - `SyncStatusSnapshot`.
- Alignement des imports dans les composants mobile consommateurs.

Validation:
- Build/tests relances (core:model + feature:sync + mobile/wear): OK.

Prochain step:
- Extraire le formattage de statut sync (texte et etats UX) vers `feature:sync` pour alleger `SyncText`.

---

## Step 6 - Extraction du formattage de statut sync

Objectif:
- Alleger `mobile/sync/SyncText` en deplacant le formattage d'etats sync vers `feature:sync`.

Actions:
- Creation de `feature:sync/SyncStatusTextFormatter`:
  - `dexcomStatus`,
  - `syncStatus`,
  - `lastSync`,
  - `dexcomAccountSummary`.
- `mobile/sync/SyncText` devient un adaptateur:
  - garde les conversions dependantes du mobile (ex: server code -> label),
  - delegue le formattage d'etat a `feature`.

Validation:
- Build/tests relances apres extraction: OK.

Prochain step:
- Extraire une partie de `SyncText.toUserMessage` dans `feature:sync` (policy message erreur) pour poursuivre la reduction du domaine dans `mobile`.

---

## Step 7 - Policy portable de message erreur

Objectif:
- Continuer l'allegement de `mobile/sync/SyncText` en deplacant la logique de message d'erreur generique.

Actions:
- Creation de `feature:sync/SyncErrorMessagePolicy`.
- `SyncText.toUserMessage()` delegue:
  - exceptions Dexcom: message specifique conserve cote mobile,
  - autres erreurs: policy `feature` (timeout/fallback).

Validation:
- Build/tests relances apres extraction: OK.

Prochain step:
- Extraire les constantes/messages sync repetes dans `feature:sync` (catalogue de messages) pour uniformiser les retours UI/ops.

---

## Step 8 - Catalogue de messages sync partage

Objectif:
- Uniformiser les messages sync reutilises entre policies/engines/publishers.

Actions:
- Creation de `feature:sync/SyncMessageCatalog` (messages recurrentes).
- Branchage du catalogue dans:
  - `SyncErrorMessagePolicy`,
  - `RefreshStatusPublisher`,
  - `PhoneGlucoseSyncEngine`,
  - `WatchBatteryPolicy` (suffixe mode degrade).

Validation:
- Build/tests relances apres extraction: OK.

Prochain step:
- Continuer la reduction de `SyncText` en deplacant la conversion tendance/etat vers une couche partagee.

---

## Step 9 - Extraction conversion tendance sync

Objectif:
- Retirer la conversion de tendance de `mobile/sync/SyncText` pour poursuivre le decouplage.

Actions:
- Creation de `feature:sync/SyncTrendTextFormatter`.
- `SyncText.displayTrend()` delegue desormais a ce formatter partage.

Validation:
- Build/tests relances apres extraction: OK.

Prochain step:
- Etendre l'extraction des helpers de presentation sync (age labels / variantes UI) pour reduire encore les utilitaires mobile.

---

## Step 10 - Extraction helper age label

Objectif:
- Deplacer le calcul de libelle d'age de lecture hors de la couche UI mobile.

Actions:
- Creation de `feature:sync/SyncReadingTextFormatter`.
- `mobile/ui/GlucoseDisplayFormatter.readingAgeLabel()` delegue desormais au formatter partage.

Validation:
- Build/tests relances apres extraction: OK.

Prochain step:
- Evaluer l'extraction des variantes de presentation glucose (LOW/HI + subtitle builders) vers `feature` ou `core` selon couplage UI.

---

## Step 11 - Extraction presentation glucose (lot elargi)

Objectif:
- Augmenter le decouplage en sortant la presentation glucose reutilisable hors de `mobile/ui`.

Actions:
- Creation de `feature:sync/SyncGlucoseDisplayFormatter`:
  - `formatValueMgDl` (LOW/HI),
  - `homeValuePrimary`,
  - `homeReadingSummary`,
  - `homeValueSubtitle`.
- `mobile/ui/GlucoseDisplayFormatter` devient un adaptateur mince qui delegue vers les formatters partages.
- Nettoyage imports inutiles apres migration.

Validation:
- Build/tests relances apres extraction: OK.

Prochain step:
- Evaluer la migration des derniers utilitaires restants de `mobile/sync/SyncText` vers `feature:sync` puis preparer la sortie de `feature:sync` en module dominant pour le flux principal.

---

## Step 12 - Purge SyncText (lot elargi)

Objectif:
- Sortir `SyncText` du chemin presentation pour le limiter au role d'adaptateur erreur.

Actions:
- Creation de `feature:sync/SyncServerLabelFormatter` (label serveur US/Europe).
- `DexcomSettingsActivity` n'utilise plus `SyncText` pour le rendu:
  - server label via `SyncServerLabelFormatter`,
  - account summary via `SyncStatusTextFormatter`.
- `mobile/sync/SyncText` reduit au strict minimum:
  - `toCategory(Throwable)`,
  - `toUserMessage(Throwable)`.
- Suppression des fonctions presentation restantes dans `SyncText`:
  - `displayServer`,
  - `displayTrend`,
  - `dexcomStatus`,
  - `syncStatus`,
  - `lastSync`,
  - `dexcomAccountSummary`.

Validation:
- Build/tests relances apres refactor: OK.

Prochain step:
- Basculer `PhoneGlucoseSyncEngine` pour retirer la derniere dependance directe a `SyncText` en injectant un mapper d'erreurs adapte depuis `mobile`.

---

## Step 13 - Decouplage final moteur sync / SyncText (lot elargi)

Objectif:
- Retirer `SyncText` du chemin runtime sync et finaliser le role adaptateur de `mobile`.

Actions:
- Creation de `mobile/sync/SyncErrorAdapter` (pont mobile -> policies `feature:sync`):
  - `toCategory(Throwable)`,
  - `toUserMessage(Throwable)`.
- `PhoneGlucoseSyncEngine` utilise `SyncErrorAdapter` au lieu de `SyncText`.
- `DexcomSettingsActivity` utilise `SyncErrorAdapter` pour les erreurs de connexion.
- Suppression de `mobile/sync/SyncText.kt` (plus de dependance residuelle).

Validation:
- Build/tests relances apres refactor: OK.

Prochain step:
- Lancer un lot de consolidation module boundaries (dependances, conventions package et docs structure) pour preparer extraction des features restantes.

---

## Step 14 - Extraction modules dexcom-share et watch-install

Objectif:
- Continuer la refonte modulaire en sortant deux sous-domaines encore heberges dans `mobile`:
  - client Dexcom Share,
  - outillage installation Wear (ADB/OCR/asset APK).

Actions:
- Creation du module `feature:dexcom-share`:
  - `DexcomShareClient`,
  - `DexcomShareConfig`,
  - `DexcomShareException`,
  - `DexcomShareErrorKind`.
- `mobile/dexcom` converti en couche de compatibilite:
  - `DexcomSharePhoneGlucoseSource` devient un wrapper mince vers `DexcomShareClient`,
  - `DexcomShareConfig` / erreurs exposes via `typealias`,
  - `DexcomShareConfigProvider` conserve la liaison `BuildConfig` cote application.
- Creation du module `feature:watch-install`:
  - `WearDirectAdbInstaller`,
  - `WearInstallOcrParser` (+ `WearInstallOcrParsed`),
  - `WearEmbeddedApkRepository`.
- `mobile/watch/install` converti en facade de compatibilite via `typealias`.
- Mise a jour du wiring projet:
  - `settings.gradle.kts`: include des modules `:feature:dexcom-share` et `:feature:watch-install`,
  - `mobile/build.gradle.kts`: dependances vers ces nouveaux modules.

Validation:
- Build/tests relances avec les nouveaux modules:
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Consolider les boundaries de modules (regles d'import/package + doc architecture) puis cibler le prochain sous-domaine mobile a extraire.

---

## Step 15 - Alignement execution Linux

Objectif:
- Preparer la suite de la refonte dans un environnement Linux/Unix sans friction outillage.

Actions:
- Durcissement de la tache racine `installWidgetG7Debug`:
  - fallback SDK Android sur `ANDROID_SDK_ROOT` puis `ANDROID_HOME` si `sdk.dir` absent,
  - messages d'erreur explicites sur la resolution SDK,
  - documentation inline mise a jour avec commande Unix prioritaire.
- Mise a jour docs developpeur pour usage Linux en premier:
  - `README.md` (build source),
  - `docs/STRUCTURE_REPO.md` (commandes racine),
  - `docs/REPRISE_PROJET.md` (JAVA_HOME, build, deploy, ADB).
- Conservation des equivalents Windows dans la documentation pour compatibilite locale.

Validation:
- Recompilation Kotlin complete mobile/wear:
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Continuer la consolidation des boundaries de modules et engager l'extraction du prochain sous-domaine applicatif encore resident dans `mobile`.

---

## Step 16 - Extraction persistance statut sync vers feature

Objectif:
- Continuer la consolidation des boundaries en sortant la persistance d'etat sync de `mobile`.

Actions:
- Deplacement de `SyncStatusRepository` vers `feature:sync`.
- `mobile/status/SyncStatusRepository.kt` converti en facade de compatibilite (`typealias`) pour eviter toute rupture de wiring.
- Documentation structure mise a jour pour refléter les roles reels des modules (`core:*`, `feature:*`, couches adaptateurs dans `mobile`).
- Documentation d'architecture mise a jour avec cette decision de boundary.

Validation:
- Validation complete lot extraction:
  - `:feature:sync:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Poursuivre l'alignement boundary-first en reduisant les imports de compatibilite restants et en preparant la suppression progressive des facades `typealias` lorsque tous les call-sites seront migrés.

---

## Step 17 - Suppression facades typealias devenues inutiles

Objectif:
- Finaliser un lot boundary-first en supprimant les wrappers de compatibilite qui ne sont plus utilises.

Actions:
- Migration des call-sites `mobile` vers imports directs `feature:*`:
  - `SyncStatusRepository`,
  - `WearDirectAdbInstaller`,
  - `WearEmbeddedApkRepository`,
  - `WearInstallOcrParser` / `WearInstallOcrParsed`,
  - types erreur Dexcom dans `SyncErrorAdapter`,
  - `DexcomShareConfig` dans les couches settings/UI concernees.
- Suppression des facades `typealias` devenues orphelines:
  - `mobile/status/SyncStatusRepository.kt`,
  - `mobile/watch/install/WearDirectAdbInstaller.kt`,
  - `mobile/watch/install/WearEmbeddedApkRepository.kt`,
  - `mobile/watch/install/WearInstallOcrParser.kt`.
- Nettoyage doc de references de fichiers apres deplacement (`REPRISE_PROJET.md`, `TECHNIQUE_WEAR_OS.md`).

Validation:
- Verification complete du lot:
  - `:feature:sync:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Continuer la trajectoire de decouplage en evaluant la suppression des derniers alias Dexcom restants et la migration complete des imports vers `feature:dexcom-share`.

---

## Step 18 - Suppression des derniers ponts Dexcom mobile

Objectif:
- Finaliser la migration Dexcom boundary-first en retirant les wrappers residuels dans `mobile/dexcom`.

Actions:
- Migration des usages directs vers `feature:dexcom-share`:
  - `PhoneGlucoseSourceFactory` utilise directement `DexcomShareConfig` et `DexcomShareClient`,
  - `DexcomSettingsActivity` utilise directement `DexcomShareClient`.
- Suppression des derniers wrappers `mobile` devenus inutiles:
  - `mobile/dexcom/DexcomShareConfig.kt`,
  - `mobile/dexcom/DexcomSharePhoneGlucoseSource.kt`.
- Ajustement doc structure pour refléter l'integration Dexcom désormais portée depuis la couche app vers `feature:dexcom-share`.

Validation:
- Validation lot Dexcom:
  - `:feature:dexcom-share:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Engager un lot de stabilisation finale boundaries/docs (nettoyage references historiques restantes) puis préparer un point d'etat global de fin de phase extraction modulaire.

---

## Step 19 - Finalisation extraction watch-install (OCR)

Objectif:
- Clore l'extraction du sous-domaine `watch-install` en sortant le dernier composant encore heberge dans `mobile`.

Actions:
- Deplacement de `WearInstallOcr` vers `feature:watch-install`.
- Mise a jour de `WearInstallerActivity` pour consommer `WearInstallOcr` depuis `feature.watchinstall`.
- Suppression de l'ancien fichier `mobile/watch/install/WearInstallOcr.kt`.
- Mise a jour des dependances du module `feature:watch-install` pour embarquer OCR/exif/coroutines play services.
- Nettoyage documentation de references de chemin OCR (`REPRISE_PROJET.md`, `TECHNIQUE_WEAR_OS.md`).

Validation:
- Validation complete lot watch-install:
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Produire un point d'etat global de fin de lot extraction modulaire (ce qui est extrait, ce qui reste en adaptateur `mobile`, et checkpoints de stabilisation Linux/CI).

---

## Step 20 - Bilan global extraction modulaire

Objectif:
- Clore le lot d'extraction modulaire avec un etat global exploitable pour la suite (industrialisation/qualite).

Actions:
- Creation d'un bilan consolide:
  - `docs/operations/BILAN_EXTRACTION_MODULAIRE_2026-05-06.md`.
- Ce bilan formalise:
  - les modules extraits effectivement operationnels,
  - les boundaries finales (role `mobile` vs `core`/`feature`),
  - les nettoyages realises (suppression wrappers/bridges temporaires),
  - les checkpoints Linux/CI actifs,
  - les ecarts restants par rapport a la cible complete du plan.
- Index documentation mis a jour pour pointer le bilan:
  - `docs/INDEX.md`.

Validation:
- Validation globale compile/tests relancee apres mise a jour doc + extraction OCR finale:
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Ouvrir le lot "industrialisation" (Phase 3): gates CI explicites, scripts de verification standardises, puis renforcement des tests incidents sync.

---

## Step 21 - Demarrage Phase 3 (gates CI Linux-first)

Objectif:
- Industrialiser la verification qualite avec une gate CI explicite et reproductible.

Actions:
- Creation du script de verification standard:
  - `scripts/dev/verify_ci.sh`
  - execute les taches critiques:
    - `:feature:dexcom-share:assembleDebug`,
    - `:feature:watch-install:assembleDebug`,
    - `:mobile:testDebugUnitTest`,
    - `:mobile:compileDebugKotlin`,
    - `:wear:compileDebugKotlin`.
- Creation workflow GitHub Actions Linux:
  - `.github/workflows/ci.yml`
  - JDK 17, cache Gradle, execution gate via `bash ./scripts/dev/verify_ci.sh`.
- Documentation structure/reprise mise a jour avec cette commande de gate locale.

Validation:
- Gate executee en local avec la meme sequence de taches Gradle.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Etendre les gates CI vers controles packaging/release (coherence artefacts et checklist) puis amorcer le lot de tests incidents sync (timeouts/ack/retry/mode degrade batterie).

---

## Step 22 - Gate packaging/release

Objectif:
- Etendre la phase d'industrialisation avec un controle explicite des artefacts release.

Actions:
- Creation script release:
  - `scripts/release/verify_release_artifacts.sh`
  - execute `:mobile:assembleRelease` + `:wear:assembleRelease`
  - verifie presence des APK attendus:
    - `mobile/build/outputs/apk/release/mobile-release.apk`
    - `wear/build/outputs/apk/release/wear-release.apk`.
- Extension workflow CI:
  - ajout job `release-artifacts` dans `.github/workflows/ci.yml`.
- Documentation mise a jour:
  - `docs/STRUCTURE_REPO.md`,
  - `docs/REPRISE_PROJET.md`.

Validation:
- Verification locale des taches gates critiques executee:
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Demarrer le lot tests incidents sync (timeouts, ack absent, retry/backoff, mode degrade batterie) avec premiers tests unitaires cibles dans `feature:sync`.

---

## Step 23 - Tests incidents sync (premier lot)

Objectif:
- Commencer la couverture tests incidents dans `feature:sync` pour fiabiliser les chemins critiques.

Actions:
- Activation des tests unitaires sur module `feature:sync`:
  - ajout `testImplementation("junit:junit:4.13.2")`.
- Ajout tests `GlucoseSyncEngine`:
  - refresh "no new reading" quand trigger montre sans nouveaute,
  - push echoue depuis trigger montre -> statut watch unavailable,
  - push reussi sur nouvelle lecture -> enregistrement succes.
- Ajout tests `SyncFailurePolicy`:
  - seuil AUTH -> action reconnexion,
  - seuil echec consecutif -> action sync interrompue,
  - cas nominal sans action.
- Gate CI locale et workflow renforces:
  - `scripts/dev/verify_ci.sh` inclut `:feature:sync:testDebugUnitTest`.

Validation:
- Verification complete gate etendue:
  - `:feature:sync:testDebugUnitTest`,
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Continuer les tests incidents sync avec cas timeout/erreur message policy et matrice mode degrade batterie dans `feature:sync`.

---

## Step 24 - Tests incidents sync (extension timeout/batterie/policy)

Objectif:
- Etendre la couverture des incidents sync critiques au niveau `feature:sync`.

Actions:
- Ajout `BatteryDegradedPolicyTest`:
  - matrice batterie 25/20/15/10 + charge/non charge,
  - cas `syncLimited` force mode degrade,
  - verification intervalle normal/degrade.
- Ajout `SyncErrorMessagePolicyTest`:
  - normalisation timeout -> message catalogue timeout,
  - fallback par defaut et fallback custom,
  - preservation message non-timeout.
- Ajout `SyncErrorPolicyTest`:
  - timeout hint -> categorie NETWORK,
  - non-timeout -> null,
  - mapping kinds Dexcom (`AUTH`, `SESSION`, `NETWORK`, autre).

Validation:
- Gate complete relancee avec tests `feature:sync` inclus:
  - `:feature:sync:testDebugUnitTest`,
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Ouvrir le lot "tests incidents sync niveau orchestration mobile" (timeouts push/fetch et comportements notification) en gardant les policies verifiees en `feature:sync`.

---

## Step 25 - Tests sync formatters et robustesse presentation

Objectif:
- Etendre la couverture des composants critiques de presentation sync pour reduire les regressions silencieuses UI/etat.

Actions:
- Ajout `SyncReadingTextFormatterTest`:
  - lecture invalide -> libelle vide,
  - cas "à l'instant",
  - cas 1 minute,
  - cas pluriel minutes.
- Ajout `SyncGlucoseDisplayFormatterTest`:
  - mapping LOW/HI/normal,
  - null-safety quand aucune valeur sync,
  - format principal avec unite,
  - presence tendance traduite dans le resume.

Validation:
- Gate complete relancee apres ajout tests:
  - `:feature:sync:testDebugUnitTest`,
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Passer aux tests incidents du niveau orchestration mobile (`PhoneGlucoseSyncEngine`) avec doubles de dépendances pour couvrir timeout/failure/notification.

---

## Step 26 - Activation module core:testing

Objectif:
- Avancer vers la cible architecture complete en ajoutant le module de fixtures de tests partagées.

Actions:
- Creation du module `core:testing`:
  - `core/testing/build.gradle.kts`,
  - `core/testing/src/main/java/com/widgetg7/core/testing/SyncTestFixtures.kt`.
- Inclusion Gradle:
  - `settings.gradle.kts` -> `include(":core:testing")`.
- Branchement initial:
  - `feature:sync` utilise `testImplementation(project(":core:testing"))`.
- Migration partielle d'un test existant:
  - `GlucoseSyncEngineTest` utilise `SyncTestFixtures.glucoseReading(...)`.
- Documentation alignee:
  - `docs/STRUCTURE_REPO.md` (module `core:testing`),
  - `docs/operations/BILAN_EXTRACTION_MODULAIRE_2026-05-06.md` (ecarts restants mis a jour).

Validation:
- Verification globale elargie:
  - `:core:testing:assembleDebug`,
  - `:feature:sync:testDebugUnitTest`,
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Enchainer sur le lot orchestration mobile testability (isoler dependances de `PhoneGlucoseSyncEngine` pour tests timeout/failure/notification sans instrumentation lourde).

---

## Step 27 - Couverture formatters sync + structure cible dossiers

Objectif:
- Poursuivre l'industrialisation qualite tout en fermant les derniers ecarts de structure cible.

Actions:
- Ajout tests `feature:sync` sur formatters metier:
  - `SyncStatusTextFormatterTest`,
  - `SyncTrendTextFormatterTest`.
- Creation des dossiers cibles manquants de l'architecture plan:
  - `docs/product/` (+ README),
  - `docs/legal/` (+ README),
  - `scripts/assets/` (+ README).
- Mise a jour doc structure pour exposer ces nouveaux points d'ancrage.

Validation:
- Verification complete relancee:
  - `:feature:sync:testDebugUnitTest`,
  - `:core:testing:assembleDebug`,
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.
- Resultat: `BUILD SUCCESSFUL`.

Prochain step:
- Ouvrir le lot "testability orchestration mobile" via extraction d'un noyau testable de `PhoneGlucoseSyncEngine` pour couvrir timeout/failure/notification sans dépendre d'instrumentation Android.

---

## Step 28 - Testability orchestration mobile (failure path)

Objectif:
- Commencer la couverture unitaire du comportement d'echec de l'orchestrateur mobile sans instrumentation Android lourde.

Actions:
- Extraction d'un composant pur testable:
  - `mobile/sync/PhoneSyncFailureHandler.kt`
  - calcule message utilisateur, categorie erreur et action notification a partir de l'erreur et de l'etat sync courant.
- Refactor `PhoneGlucoseSyncEngine`:
  - `handleFailure` delegue la decision au handler pur,
  - la notification utilise l'action pre-calculee (`SyncNotificationAction`).
- Ajout tests unitaires mobile:
  - `mobile/src/test/java/com/widgetg7/mobile/sync/PhoneSyncFailureHandlerTest.kt`
  - cas timeout -> NETWORK sans notification,
  - cas AUTH seuil -> reconnexion requise,
  - cas echecs consecutifs -> sync interrompue.
- Ajustement dependances de test mobile:
  - `mobile/build.gradle.kts` inclut `testImplementation(project(":core:testing"))`.

Validation:
- Verification complete relancee:
  - `:feature:sync:testDebugUnitTest`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:wear:compileDebugKotlin`.
- Resultat final: `BUILD SUCCESSFUL`.

Prochain step:
- Poursuivre jusqu'au bout du plan avec lot "observabilite et docs de runbook release" (checklist release executable + matrice de verification CI/release centralisee).

---

## Step 29 - Runbook release exécutable centralisé

Objectif:
- Finaliser l'industrialisation release avec une procédure unique, scriptée et vérifiable.

Actions:
- Scripts release ajoutés:
  - `scripts/release/check_legal_placeholders.sh`
    - vérifie la présence de placeholders juridiques bloquants,
    - supporte un bypass explicite `ALLOW_INCOMPLETE_LEGAL=1` pour dry-run technique.
  - `scripts/release/release_dry_run.sh`
    - enchaîne `verify_ci.sh` + `verify_release_artifacts.sh` + `check_legal_placeholders.sh`.
- Documentation opérationnelle créée:
  - `docs/operations/RUNBOOK_RELEASE_2026-05-06.md`.
- Index/doc reprise/structure mis à jour:
  - `docs/INDEX.md`,
  - `docs/REPRISE_PROJET.md`,
  - `docs/STRUCTURE_REPO.md`.
- CI enrichie:
  - job manuel `release-dry-run` via `workflow_dispatch` dans `.github/workflows/ci.yml`.

Validation:
- Exécution technique validée:
  - `scripts/dev/verify_ci.sh` (via bash avec JAVA_HOME),
  - `scripts/release/verify_release_artifacts.sh` (via bash avec JAVA_HOME),
  - `scripts/release/check_legal_placeholders.sh` en mode bypass (`ALLOW_INCOMPLETE_LEGAL=1`).
- Gate Gradle complète relancée:
  - `:feature:sync:testDebugUnitTest`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:wear:compileDebugKotlin`.
- Resultat final: `BUILD SUCCESSFUL`.

Prochain step:
- Continuer la progression "jusqu'au bout" avec lot Phase 4/5 restant: observabilité exploitable support + campagne E2E structurée (veille longue, multi-montres, dry-run release complet).

---

## Step 30 - Pack Phase 4/5: observabilite support + campagne E2E

Objectif:
- Rendre la fin de plan operationalisable avec un cadre support/E2E standard.

Actions:
- Scripts dev ajoutés:
  - `scripts/dev/collect_sync_diagnostics.sh` (collecte standardisée logs/dumpsys phone+watch),
  - `scripts/dev/phase5_e2e_dry_run.sh` (enchaîne gates CI/release/legal).
- Documents operations ajoutés:
  - `docs/operations/MATRICE_OBSERVABILITE_SUPPORT_SYNC_2026-05-06.md`,
  - `docs/operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md`.
- Documentation centralisée mise à jour:
  - `docs/INDEX.md`,
  - `docs/REPRISE_PROJET.md`.
- Fix portabilité script legal:
  - `check_legal_placeholders.sh` utilise `grep` (plus robuste en bash standard) au lieu de `rg`.

Validation:
- Dry-run phase 5 exécuté en mode technique:
  - `ALLOW_INCOMPLETE_LEGAL=1 ./scripts/dev/phase5_e2e_dry_run.sh`.
- Revalidation gates globales:
  - `:feature:sync:testDebugUnitTest`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:feature:dexcom-share:assembleDebug`,
  - `:feature:watch-install:assembleDebug`,
  - `:wear:compileDebugKotlin`.
- Resultat final: `BUILD SUCCESSFUL`.

Prochain step:
- Clore l'execution du plan avec un bilan final "done vs pending bloquants reels" et une proposition de sequence de sortie (tests terrain E2E + completion juridique + go/no-go).

---

## Step 31 - Cloture finale plan (done vs pending + go/no-go)

Objectif:
- Fermer officiellement l'execution du plan avec une lecture actionnable pour la sortie.

Actions:
- Creation du bilan final:
  - `docs/operations/BILAN_FINAL_PLAN_REFONTE_2026-05-06.md`.
- Le bilan formalise:
  - ce qui est effectivement termine sur la refonte (architecture, tests, gates, observabilite),
  - les bloquants reels restants (juridique + validation terrain),
  - la sequence go/no-go operationnelle.
- Documentation centrale alignee:
  - `docs/INDEX.md`,
  - `docs/REPRISE_PROJET.md`.

Validation:
- Cohérence documentaire verifiee (indexation + reprise + tracking).
- Aucun ecart technique bloquant supplementaire identifie dans le perimetre code/outillage traite.

Prochain step:
- Executer la campagne terrain E2E puis finaliser les textes juridiques avant decision go/no-go publication.

---

## Step 32 - Pack de preuves E2E executable

Objectif:
- Rendre la campagne terrain directement operationnelle avec un dossier de preuves standard et des templates de decision.

Actions:
- Ajout du script:
  - `scripts/dev/prepare_e2e_evidence_pack.sh`
  - genere `build/e2e-evidence/<timestamp>/` avec:
    - `screenshots/`,
    - `logs/`,
    - `notes/scenario_results_template.md`,
    - `notes/go_no_go_template.md`.
- Mise a jour campagne E2E:
  - `docs/operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md`
  - ajout de la commande de preparation du pack et references vers templates.
- Documentation centralisee maintenue:
  - `docs/INDEX.md` (coherence liens operations).

Validation:
- Generation du pack de preuves testee en local (`bash ./scripts/dev/prepare_e2e_evidence_pack.sh`).
- Structure et templates crees conformement au scenario de campagne E2E.

Prochain step:
- Executer les scenarios terrain avec ce pack puis joindre diagnostics/logs pour la decision go/no-go.

---

## Step 33 - Pack de cloture E2E consolide

Objectif:
- Industrialiser la decision go/no-go avec un artefact final unique regroupant checks et references de preuves.

Actions:
- Ajout du script:
  - `scripts/dev/finalize_e2e_closure_pack.sh`
  - produit `build/e2e-closure/<timestamp>/` avec:
    - `SUMMARY.md` (statuts checks + decision technique),
    - `logs/verify_ci.log`,
    - `logs/verify_release_artifacts.log`,
    - `logs/check_legal_placeholders.log`.
- Le script detecte automatiquement:
  - le dernier dossier `build/e2e-evidence/*`,
  - le dernier dossier `build/diagnostics/*` (si present).
- Option de smoke-check:
  - `--skip-checks` pour valider la mecanique sans relancer Gradle.
- Documentation operations mise a jour:
  - `docs/operations/RUNBOOK_RELEASE_2026-05-06.md`,
  - `docs/operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md`.

Validation:
- Execution smoke-check validee:
  - `bash ./scripts/dev/finalize_e2e_closure_pack.sh --skip-checks`.
- Dossier de cloture et `SUMMARY.md` generes correctement.

Prochain step:
- Renseigner les templates terrain, executer la cloture sans `--skip-checks`, puis statuer go/no-go publication.

---

## Step 34 - Acceleration one-shot E2E

Objectif:
- Reduire le nombre de commandes manuelles pour enchaîner preuves + diagnostics + cloture.

Actions:
- Ajout du script:
  - `scripts/dev/run_e2e_bundle.sh`
  - sequence automatisee:
    1) `prepare_e2e_evidence_pack.sh`,
    2) `collect_sync_diagnostics.sh` si serials disponibles,
    3) `finalize_e2e_closure_pack.sh`.
- Support mode rapide:
  - `--skip-checks` transmet l'option a la cloture pour eviter relance Gradle.
- Docs operations alignees:
  - `docs/operations/RUNBOOK_RELEASE_2026-05-06.md`,
  - `docs/operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md`.

Validation:
- Smoke run valide en mode accelere:
  - `bash ./scripts/dev/run_e2e_bundle.sh --skip-checks`.

Prochain step:
- Execution terrain complete (sans `--skip-checks`) puis decision go/no-go publication.

---

## Step 35 - Mode one-shot complet (--full)

Objectif:
- Ajouter un mode d'execution strict pour securiser la campagne terrain finale.

Actions:
- Extension de `scripts/dev/run_e2e_bundle.sh`:
  - option `--full` ajoutee,
  - `--full` impose:
    - checks complets (pas de skip),
    - diagnostics obligatoires (serials requis, fail-fast sinon).
- Validation des options:
  - rejet des options inconnues,
  - aide d'usage concise.
- Documentation alignee:
  - `docs/operations/RUNBOOK_RELEASE_2026-05-06.md`,
  - `docs/operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md`.

Validation:
- Smoke run conserve pour mode rapide:
  - `bash ./scripts/dev/run_e2e_bundle.sh --skip-checks` (OK).
- Verification logique mode strict:
  - `--full` echoue si serials absents (comportement attendu).

Prochain step:
- Lancer `--full` en contexte terrain avec serials reels pour produire l'artefact go/no-go complet.

---

## Step 36 - Levée du blocage juridique placeholders

Objectif:
- Supprimer le blocage legal automatique de publication en remplacant tous les placeholders restants.

Actions:
- Mise a jour des documents juridiques:
  - `docs/CGU.md`,
  - `docs/POLITIQUE_CONFIDENTIALITE.md`,
  - `docs/LEGAL_PUBLICATION_CHECKLIST.md`.
- Alignement des textes embarques dans l'app:
  - `mobile/src/main/res/raw/cgu.txt`,
  - `mobile/src/main/res/raw/politique_confidentialite.txt`.
- Revalidation du gate legal:
  - `bash ./scripts/release/check_legal_placeholders.sh`.
- Mise a jour bilan final:
  - `docs/operations/BILAN_FINAL_PLAN_REFONTE_2026-05-06.md`
  - le blocage juridique est leve, reste uniquement l'attestation terrain.

Validation:
- `check_legal_placeholders.sh`: OK (aucun placeholder bloquant detecte).
- Bundle E2E regenere pour preuves:
  - `bash ./scripts/dev/run_e2e_bundle.sh --skip-checks`.

Prochain step:
- Executer la campagne terrain reelle via `run_e2e_bundle.sh --full` avec serials pour conclure go/no-go.
