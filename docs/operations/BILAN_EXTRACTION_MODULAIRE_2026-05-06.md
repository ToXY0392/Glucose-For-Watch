# Bilan Extraction Modulaire - Widget G7

Date: 2026-05-06
Source: `docs/PLAN_REFONTE_COMPLETE_APP_2026-05-06.md` (Phase 2)

## Etat global

La phase d'extraction modulaire planifiee sur:
- `core:datalayer-contract`,
- `core:model`,
- `core:testing`,
- `feature:sync`,
- `feature:dexcom-share`,
- `feature:watch-install`

est implementee et active dans le wiring courant.

`mobile` est maintenant majoritairement une couche application:
- UI Android,
- orchestration des services/activities,
- bridges Android framework.

## Modules et responsabilites finales

- `core:datalayer-contract`
  - Contrat Data Layer unique (paths/keys protocolaires).
- `core:model`
  - Modeles metier partages (`GlucoseReading`, `SyncStatusSnapshot`, categories erreur).
- `core:testing`
  - Fixtures utilitaires transverses pour tests de modules.
- `feature:sync`
  - Moteur sync, policies (failures, erreurs, texte), publishers Wear, repository statut sync.
- `feature:dexcom-share`
  - Client Dexcom Share, config, erreurs de domaine.
- `feature:watch-install`
  - Pair/install ADB Wi-Fi, OCR photo Wear, parser OCR, gestion APK Wear embarque.
- `mobile`
  - UI + orchestration; plus de wrappers `typealias` residuels pour ces sous-domaines.
- `wear`
  - Listener data layer, cache, tile/complication, health monitor.

## Nettoyages effectues

- Suppression des facades de compatibilite temporaires `typealias`:
  - sync status repository,
  - install watch (ADB/parser/repository),
  - dexcom wrappers.
- Suppression des anciens fichiers `mobile/dexcom` et des anciennes passerelles `mobile/watch/install/*` extraites.
- Alignement des documents techniques sur les nouveaux chemins `feature/*`.

## Checkpoints Linux/CI

- Commandes Linux/Unix priorisees dans la doc (`./gradlew ...`).
- Tache `installWidgetG7Debug` robuste:
  - resolution SDK via `sdk.dir` puis `ANDROID_SDK_ROOT` puis `ANDROID_HOME`,
  - serials ADB via `local.properties` ou variables d'environnement.
- Validation recurrente executee sur lot modulaire:
  - `:feature:*:assembleDebug`,
  - `:mobile:testDebugUnitTest`,
  - `:mobile:compileDebugKotlin`,
  - `:wear:compileDebugKotlin`.

## Ecart restant par rapport a l'architecture cible du plan

Le plan cible mentionne aussi:
- dossiers `docs/product`, `docs/legal`,
- scripts `scripts/dev`, `scripts/release`, `scripts/assets`.

Ces points ne sont pas requis pour clore l'extraction modulaire des domaines critiques, mais restent des chantiers de la suite (industrialisation/qualite).

## Recommendation immediate

Passer du mode "extraction" au mode "durcissement":
- figer les boundaries modules actuelles,
- ajouter une gate CI de verification des taches critiques,
- renforcer les tests sur flux sync incidents (timeouts, retry, batterie degradee, ack absent).
