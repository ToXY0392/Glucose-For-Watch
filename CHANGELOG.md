# Changelog

Toutes les modifications notables de ce projet seront documentées dans ce fichier.

Format basé sur [Keep a Changelog](https://keepachangelog.com/fr/1.1.0/).

## [Unreleased]

---

## [0.6.0] - 2026-07-17

### 🚀 Améliorations (Added & Changed)

- **[Wear] Refonte du bouton Synchro :** Design minimaliste sans fond avec texte d'accentuation et rééquilibrage de l'espace vertical.
- **[Phone/Core] Lisibilité accrue :** Intensification de la couleur rouge pour les alertes de glycémie (seuils `VERY_LOW` / `LOW` vers `#D32F2F`) pour un meilleur contraste en extérieur.
- **[Repo] Standardisation :** Refonte du `README.md`, ajout des templates GitHub (PR, Issues) et renforcement du `.gitignore`.

### 🐛 Corrections de bugs (Fixed)

- **[Wear] Fix Layout Flicker :** Figeage des dimensions dans le `ProtoLayout` de la tuile pour empêcher les recalculs visuels lors de la mise à jour des données.
- **[Wear] Caractère invalide :** La tendance manquante n'affiche plus un "?" (remplacement par une chaîne vide `""`).
- **[Phone] Insets (Edge-to-Edge) :** Suppression d'un double padding (`statusBarsPadding`) sur la `HomeScreen` mobile.

### 🛡️ Sécurité & Stabilité (Security & Performance)

- **[Tech] Compatibilité Android 16 (API 36) :** Typage explicite du Foreground Service (`FOREGROUND_SERVICE_TYPE_DATA_SYNC`).
- **[Tech] Optimisation Batterie/Quotas :** Le `WorkManager` (fallback) ne se lance plus si le service de synchronisation est déjà actif.
- **[Tech] Survie en Release :** Ajout de règles ProGuard spécifiques pour empêcher la suppression agressive du `WearableListenerService` (Data Layer) lors de la minification.

### Milestone Compose (2026-05-26 · G-M8)

- Phone UI Compose M3 : Legal, Notice, Dexcom, WatchSetup, Home
- `GlucoseForWatchTheme` + palette ToXY · Compose Gradle BOM
- Gates G-F0→F3 · sync smoke S1–S3 · tag `v0.6.0`

---

## [0.5.0] — 2026-05-26

Sideload milestone · gate **G-M7** · PC install only (`installGlucoseForWatchDebug`).

### Added

- Bloc C QA evidence pack (C.7 8 h soak PASS, G7 matrix 7/7 with documented waivers)
- QA automation scripts: `sample-c2/c3/c8-session.ps1`, `capture-c1-agp-session.ps1`
- English KDoc on public Kotlin APIs (developer comments; UI stays French)
- Draft `cgu.txt` for release build; Windows folder rename helper script

### Fixed

- FGS crash mitigated (Bloc X): fallback schedulers, deduplicated FGS starts
- CI release artifact verification accepts unsigned release APKs
- Wear status preview moved to debug source set for release builds

### Verified

- KPIs K1–K7 · incident [2026-05-25-app-crash](docs/qa/incidents/2026-05-25-app-crash.md) closed
- Tag `v0.5.0` on `develop/integration`

---

## [0.4.0] — 2026-05-23

### Added

- Wear status screen (Compose Material 3): AGP hero, sync button, battery, refresh state
- `PendingPushQueue` + flush on watch reconnect and WorkManager catch-up
- Offline/online sync scenario tests, pending push queue tests, reconnect detector tests
- GitHub CI workflow, PR template, issue templates
- Cursor skills: ToXY theme maintainer, sync health reviewer, AGP color guard

### Changed

- User-facing app name **ToXY** (phone + watch launcher labels)
- ToXY chrome tokens integrated; AGP colors on tile, phone hero, complication range
- Wear tile: sync button, 45 s freshness, centralized `ToxyTileTheme`
- Phone: watch unreachable badge, repush backoff 10/30/60/120 s
- `GlucoseRangeResolver` in `:core:model` with unit tests

### Fixed

- Complication display aligned with tile via `WearGlucoseSurfaceModelFactory`

---

## [0.3.1] — 2026-05-07

### Fixed

- Gradle IDE sync stability (JBR forced)
- Watch display frozen (UI cache refresh)

### Changed

- AGP 9.2.1 migration: embedded wear APK via `androidComponents` / `addGeneratedSourceDirectory`
- Gradle 9.4.1 wrapper

### Verified

- 30-minute continuous sync monitoring stable
- Clean mobile + wear reinstall validated

---

[Unreleased]: https://github.com/ToXY0392/Glucose-For-Watch/compare/v0.6.0...HEAD
[0.6.0]: https://github.com/ToXY0392/Glucose-For-Watch/compare/v0.5.0...v0.6.0
[0.5.0]: https://github.com/ToXY0392/Glucose-For-Watch/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/ToXY0392/Glucose-For-Watch/compare/v0.3.1...v0.4.0
[0.3.1]: https://github.com/ToXY0392/Glucose-For-Watch/releases/tag/v0.3.1
