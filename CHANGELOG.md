# Changelog

All notable changes to this project are documented in this file.

Format based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

---

## [0.6.0] — 2026-05-26

Compose phone milestone · gate **G-M8** · sideload PC install only.

### Added

- Phone UI Compose M3: Legal, Notice, Dexcom, WatchSetup, **Home** (`HomeScreen`)
- `WidgetG7Theme` + ToXY phone palette · Compose Gradle BOM
- G-F3 QA gate: `docs/qa/G-F3-checklist.md` · `scripts/qa/g-f3-gate.ps1`

### Changed

- Block F0–F5: migrated phone screens from XML to Compose (WearInstaller stays XML)
- Removed dead F1–F3 activity layouts; `HomeUiBinder` moved to test for PNG previews

### Verified

- Gates G-F0→F3 · K8 sync smoke S1–S3 · CI green post F5
- Tag `v0.6.0` on `integrate`

---

## [0.5.0] — 2026-05-26

Sideload milestone · gate **G-M7** · PC install only (`installWidgetG7Debug`).

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

- KPIs K1–K7 · incident [2026-05-25-app-crash](../docs/qa/incidents/2026-05-25-app-crash.md) closed
- Tag `v0.5.0` on `integrate`

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

[Unreleased]: https://github.com/ToXY0392/Glucose-For-Watch/compare/v0.5.0...HEAD
[0.5.0]: https://github.com/ToXY0392/Glucose-For-Watch/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/ToXY0392/Glucose-For-Watch/compare/v0.3.1...v0.4.0
[0.3.1]: https://github.com/compare/...v0.3.1
