# Developer handoff

> **Last updated:** 2026-05-23

## Quick state

| Item | Value |
|------|-------|
| Sync flow | Dexcom Share → phone → Wear Data Layer → watch |
| Active modules | `mobile`, `wear`, `core`, `feature` |
| Version | 0.3.1 |
| Build | Debug validated |
| Refactor plan | [MASTER-REFACTOR-PLAN.md](../plan/MASTER-REFACTOR-PLAN.md) v3.1 |
| **Progress tracker** | **[PROGRESS.md](../plan/PROGRESS.md)** |
| UX kit (standalone) | [toxy-ux-kit/](../../toxy-ux-kit/README.md) v0.1.0 |
| Doc | v1.0 EN ✅ |
| Phase −1 (doc + kit) | ✅ Done 2026-05-23 |
| Phase 0 (tile sync, AGP, offline) | ✅ Done 2026-05-23 |
| **Current phase** | **1 — kit tokens → APK** |

## Essential commands

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat installWidgetG7Debug
.\gradlew.bat test
```

## Known open items (refactor)

| Priority | Item | Phase | Statut |
|----------|------|-------|--------|
| P1 | QA manuelle 30 min + offline 1 h | 0.7 | ☐ |
| P1 | Full ToXY token migration | 1 | ☐ |
| P1 | English code comments on sync modules | 1 | ☐ |
| P2 | PendingPushQueue for offline catch-up | 2 | ☐ |
| P2 | Complication stale UI cache | 2 | ☐ |

See live tracker: [PROGRESS.md](../plan/PROGRESS.md)

## Recent incidents

| Date | Subject | Status |
|------|---------|--------|
| 2026-05-07 | Gradle IDE sync unstable | Resolved (JBR forced) |
| 2026-05-07 | Watch display frozen at 382 | Resolved (UI cache refresh) |
| 2026-05-07 | 30 min sync monitoring | Validated stable |
| 2026-05-23 | Week test: sync when watch not worn | Partial — Phase 0 done, Phase 2 pending |
| 2026-05-23 | Week test: no tile sync button | Resolved (Phase 0) |

## Session resume checklist

1. Gradle sync / `./gradlew help`
2. Build mobile + wear debug
3. Install on phone + watch
4. Verify tile + complication
5. Run 5 min sync test
6. If sync/tile changed: 30 min regression

## Cursor automation

- Skills: `.cursor/skills/` — see [Cursor automation](cursor-automation.md)
- Hooks: `.cursor/hooks.json` — session start checks, USB monitor
- USB detach incidents auto-append to this file via `widget-g7-usb-detach-handoff-writer`

## Related

- [Sync pipeline](../architecture/sync-pipeline.md)
- [Dual IDE setup](dual-ide-setup.md)
