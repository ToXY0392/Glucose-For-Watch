# Decisions - Sync/Wear Reliability

Date: 2026-05-06

## Contexte

Ce lot execute les priorites immediates de la refonte sur la fiabilite refresh/sync phone <-> watch, sans attendre un decoupage par phase.

## Decisions techniques appliquees

1. **Cadence degradee sous 20% batterie montre**
   - Le service de sync actif adapte sa boucle:
     - normal: 45s,
     - mode degrade (batterie <=20% ou syncLimited): 120s.
   - Les repush sans ACK utilisent des delais plus conservateurs en mode degrade.

2. **Messaging explicite mode degrade**
   - Les statuts de refresh renvoyes a la montre incluent un message explicite quand le mode degrade est actif:
     - "Mode degrade montre actif (<20% batterie): cadence reduite."

3. **Listener Wear non bloquant**
   - Suppression du chemin bloquant dans `WearDataLayerListenerService` (plus de resolution node via appel bloquant a chaque event).
   - Mise en cache asynchrone du `localNodeId` au demarrage du service.

4. **Retries/backoff controles**
   - Worker de fallback avec backoff exponentiel (15s base).
   - Limitation du nombre de retries Worker a 4 tentatives avant echec.

5. **Observabilite sync**
   - Logs structures ajoutes sur:
     - refresh request montre,
     - ACK montre,
     - etat batterie/sync montre,
     - resultat de passe sync,
     - succes/echec repush et erreurs sync.

## Impact attendu

- Meilleure robustesse en batterie faible cote montre.
- Reduction des risques de blocage/latence dans le listener data layer Wear.
- Diagnostics terrain plus actionnables pour les crashs et incidents de sync.

## Avancement modularisation (lot atomique)

- Creation de `core:datalayer-contract` pour centraliser les paths/keys du data layer.
- Creation de `core:model` avec le modele partage `GlucoseReading`.
- Creation de `feature:sync` avec une policy sync/batterie extraite (`BatteryDegradedPolicy`).
- Extraction du publisher Data Layer vers `feature:sync` (`WearSyncPublisher`), avec wrapper de compatibilite cote `mobile`.
- Deplacement du resultat de sync partage vers `feature:sync` (`SyncExecutionResult`) pour decoupler progressivement le moteur.
- Extraction d'un orchestrateur de sync dans `feature:sync` (`GlucoseSyncEngine`) avec ports (source, etat, publication wear, refresh status), et adaptation cote `mobile`.
- `mobile` et `wear` alignes sur les nouveaux modules, avec build/tests valides apres migration.
- Creation de `feature:dexcom-share` pour isoler le client Dexcom Share:
  - `DexcomShareClient`,
  - `DexcomShareConfig`,
  - `DexcomShareException` / `DexcomShareErrorKind`.
- Creation de `feature:watch-install` pour isoler le sous-domaine installation Wear:
  - `WearDirectAdbInstaller`,
  - `WearInstallOcrParser`,
  - `WearEmbeddedApkRepository`.
- Conservation d'une compatibilite API cote `mobile` via wrappers/typealias afin d'eviter une rupture de wiring UI pendant la migration.
- Deplacement de `SyncStatusRepository` vers `feature:sync` pour sortir la persistance d'etat sync du module `mobile`, en conservant une facade de compatibilite (`typealias`) cote application.

## Note transition assistants

Lot implemente apres reprise Codex -> Cursor, avec trace explicite des choix pour continuites de sessions futures.
