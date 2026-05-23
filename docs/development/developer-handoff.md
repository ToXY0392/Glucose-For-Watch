# Developer handoff

> **Last updated:** 2026-05-23

## Quick state

| Item | Value |
|------|-------|
| Sync flow | Dexcom Share → phone → Wear Data Layer → watch |
| Active modules | `mobile`, `wear`, `core`, `feature` |
| Version | **0.4.0** (tag `v0.4.0`) |
| Build | Debug validated · `./gradlew test` green |
| Refactor plan | [MASTER-REFACTOR-PLAN.md](../plan/MASTER-REFACTOR-PLAN.md) |
| **Progress tracker** | **[PROGRESS.md](../plan/PROGRESS.md)** |
| UX kit (standalone) | [toxy-ux-kit/](../../toxy-ux-kit/README.md) v0.1.0 |
| **Current phase** | **3 — release polish** (hardware QA deferred) |

## Essential commands

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
.\gradlew.bat installWidgetG7Debug          # phone + watch in adb
.\scripts\qa\install-and-verify.ps1         # install + checklist
```

## Deferred (not blocking now)

| Item | Phase | Note |
|------|-------|------|
| QA 30 min + offline 1–2 h | 0.7 | Run when phone + watch available |
| Matrice G6/G7 | 3.3 | [QA-MATRIX-G6-G7.md](../plan/QA-MATRIX-G6-G7.md) |
| Figma ToXY file URL | 3.1 | [FIGMA-HANDOFF.md](../../toxy-ux-kit/figma/FIGMA-HANDOFF.md) — tokens export ready |

## Known limitations

| Item | Status |
|------|--------|
| Complication text AGP tint | RANGED_VALUE 40–400 OK; text color = watch face (API 1.2.x) |
| Package id `com.widgetg7.*` | Unchanged — display name is **ToXY** |

## Session resume checklist

1. `./gradlew help` or Gradle sync
2. `./gradlew test`
3. Optional: install on hardware + fill QA checklists
4. After sync/tile changes: 30 min regression on device

## Related

- [Sync pipeline](../architecture/sync-pipeline.md)
- [Dual IDE setup](dual-ide-setup.md)
- [Cursor automation](cursor-automation.md)
