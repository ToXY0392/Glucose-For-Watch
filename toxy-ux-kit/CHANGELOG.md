# Changelog — ToXY UX Kit

Independent from Glucose For Watch app releases.

## [0.2.0] — 2026-05-23

### Added

- Status pill soft backgrounds: `sync.okSoft`, `warnSoft`, `errorSoft`, `okStroke`
- Wear installer aurora gradient: `overlay.auroraStart/Center/End`
- Standalone HTML design reference (`design-reference/index.html`) — Figma optional
- AGP color lint + token validation scripts for CI

### Changed

- Mobile `wg7_*` pill/aurora colors now alias kit tokens (no hardcoded hex)

---

## [0.1.0] — 2026-05-23

### Added

- Standalone kit package at repository root (`toxy-ux-kit/`)
- Design tokens: ToXY chrome + AGP medical glucose colors
- Component specs: mobile home, wear tile, complication, sync states
- Figma import guide
- Reference PNGs (tile dial, sync button, reading preview)
- Android color export script (`tools/export-android-colors.py`)
- `kit.manifest.json` with layer separation rules

### Notes

- App modules still use legacy `wg7_*` XML colors until Phase 1 integration
- Source of truth for new design work is this kit, not `mobile/res/values/colors.xml`
