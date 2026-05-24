# Changelog

All notable changes to this project are documented in this file.

Format based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Changed

- User-facing documentation rebranded to **Glucose For Watch** (launcher name already set in `strings.xml`)

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

[Unreleased]: https://github.com/compare/v0.4.0...HEAD
[0.4.0]: https://github.com/compare/v0.3.1...v0.4.0
[0.3.1]: https://github.com/compare/...v0.3.1
