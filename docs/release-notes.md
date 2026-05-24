# Release notes

> See also [CHANGELOG.md](../CHANGELOG.md) at repository root.

---

## 2026-05-23 — v0.4.0 Glucose For Watch release

- Launcher name **Glucose For Watch** on phone and watch (`strings.xml`)
- Wear status screen (Compose M3) with AGP hero and sync button
- Robust offline sync: pending push queue, reconnect flush, WorkManager catch-up
- ToXY chrome + AGP medical colors integrated across tile, phone, complication
- CI GitHub Actions, PR/issue templates, expanded unit test suite

---

## 2026-05-23 — Documentation & refactor planning

- Master refactor plan published ([plan/MASTER-REFACTOR-PLAN.md](plan/MASTER-REFACTOR-PLAN.md))
- Full documentation rewrite in English (structured `docs/` tree)
- AGP medical color standard documented
- ToXY design system specification added
- Dexcom G6/G7 compatibility documented

---

## 2026-05-07 — v0.3.1 stabilization

- Gradle IDE sync stabilized (JBR forced)
- Clean mobile + wear reinstall
- Fixed watch display frozen incident
- 30-minute sync monitoring validated
- Build chain: AGP **9.2.1**, Gradle **9.4.1**
- Mobile module: embedded wear APK (debug) via `androidComponents` / `addGeneratedSourceDirectory` for AGP 9 compatibility

Details: [Android Studio guide](development/android-studio.md#agp-9-upgrade-note)
