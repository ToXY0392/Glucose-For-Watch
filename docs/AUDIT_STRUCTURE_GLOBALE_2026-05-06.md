# Audit Structure Globale du Projet - 2026-05-06

## Objectif

Analyser la structure globale du projet et proposer un plan concret pour une meilleure organisation (lisibilite, maintenabilite, fiabilite, vitesse de livraison).

## Vue actuelle (constat)

### Arborescence fonctionnelle

- `mobile/` : app Android telephone (UI, sync, Dexcom, installation Wear, legal, settings).
- `wear/` : app Wear OS (listener Data Layer, tile, complication, cache local).
- `docs/` : documentation produit, technique et legale.
- `tools/` : scripts utilitaires (ex: generation assets launcher).
- racine Gradle : orchestration build + task custom d'installation debug.

### Points forts

- Separation mobile/wear deja en place.
- Documentation abondante.
- Script de generation d'assets reproductible.
- Task de deploiement debug utile (`installWidgetG7Debug`).

## Problemes structurels identifies

### 1) Module `mobile` trop monolithique

Dans `mobile/`, les responsabilites melangent:
- logique metier sync (`sync/`);
- integration fournisseur glucose (`dexcom/`);
- UI/flux installation montre (`watch/install/`);
- legal/UI/settings.

**Impact**: couplage fort, regression plus probable, tests difficiles.

### 2) Couplage build mobile <-> wear fragile

- `mobile/build.gradle.kts` embarque l'APK wear avec logique orientee debug.
- automatisation dispersee entre racine Gradle, module mobile et scripts `tools/`.

**Impact**: confusion des flux debug/release, maintenance plus couteuse.

### 3) Documentation dispersee et partiellement redondante

- docs markdown + versions `res/raw` en parallele pour le legal;
- index et liens a maintenir manuellement.

**Impact**: divergence possible entre ce qui est documente et ce qui est embarque.

### 4) Socle de tests insuffisant pour les zones critiques

Peu/pas de structure visible de tests exhaustive sur sync, data layer, wear install.

**Impact**: regressions silencieuses.

### 5) Conventions d'organisation inegales

- scripts dans `tools/` sans convention globale (`scripts/assets`, `scripts/release`, etc.);
- limites de responsabilites entre modules non explicites.

**Impact**: onboarding plus lent, dette technique cumulative.

## Structure cible recommandee

Option pragmatique (progressive, sans big bang):

```text
WidgetG7/
  mobile/
  wear/
  core/
    datalayer-contract/
    model/
    testing/
  feature/
    sync/
    dexcom-share/
    watch-install/
  docs/
    architecture/
    operations/
    product/
    legal/
  scripts/
    assets/
    release/
    dev/
  tools/   (phase transitoire -> vide puis retire)
```

## Regles de structuration proposees

- `mobile/` et `wear/` restent des apps de composition UI + wiring.
- Toute logique metier portable (paths Data Layer, modeles, mapping) va dans `core/*`.
- Toute logique fonctionnelle non purement UI va dans `feature/*`.
- Les scripts doivent vivre dans `scripts/` avec sous-dossiers par usage.
- Une seule source de verite pour les documents legaux (generation vers `res/raw`).
- Chaque feature critique doit avoir son dossier de tests.

## Plan strategique de migration

## Phase 1 (1-2 jours) - Hygiene et conventions

1. Creer les dossiers cibles `docs/architecture`, `scripts/*`, `core/`, `feature/`.
2. Documenter les conventions (naming, ownership, dependances autorisees).
3. Reorganiser la doc existante sans changer le code runtime.

**Livrable**: arborescence clarifiee + conventions partagees.

## Phase 2 (3-5 jours) - Extraction du socle partage

1. Creer `core:datalayer-contract` (paths/keys/protocoles).
2. Creer `core:model` (modeles communs sync/glucose/etat).
3. Faire consommer ces modules par `mobile` et `wear`.

**Livrable**: contrats partages et moins de duplication.

## Phase 3 (1-2 semaines) - Decoupage metier mobile

1. Extraire sync dans `feature:sync`.
2. Extraire Dexcom dans `feature:dexcom-share`.
3. Extraire installation montre dans `feature:watch-install`.

**Livrable**: `mobile` recentre sur UI + orchestration.

## Phase 4 (1 semaine) - Build/release industrialises

1. Centraliser tasks custom dans convention plugin ou scripts Gradle dedies.
2. Clarifier debug vs release pour packaging wear.
3. Ajouter checks CI structurels (liens docs, presence assets obligatoires, test smoke).

**Livrable**: pipeline plus predicible, moins de surprises de build/deploiement.

## Phase 5 (continue) - Qualite durable

1. Mettre en place tests unitaires sur `core` et `feature`.
2. Ajouter tests instrumentation sur parcours critiques (sync phone-watch, tuile, complication).
3. Ajouter une checklist release structurelle.

**Livrable**: baisse des regressions, maintenance plus sereine.

## Priorites immediates (ordre conseille)

1. Fixer conventions + doc architecture (Phase 1).
2. Extraire `core:datalayer-contract` (de-risque majeur).
3. Stabiliser packaging wear debug/release.
4. Demarrer socle tests sur sync/data layer.

## Risques de migration et mitigation

- **Risque**: casser les imports/packages pendant extraction.
  - **Mitigation**: migration par petites PR atomiques + build vert a chaque etape.
- **Risque**: dette temporaire (double code old/new).
  - **Mitigation**: brancher rapidement les consommateurs puis supprimer legacy.
- **Risque**: ralentissement equipe.
  - **Mitigation**: timebox par phase et definition claire du done.

## Definition of Done (structure)

La restructuration est consideree reussie quand:
- les responsabilites des modules sont explicites et respectees;
- les flux critiques sync/tile sont couverts par tests minimaux;
- les scripts/build sont centralises et documentes;
- la doc legale/technique n'a plus de duplication ambigue.

