---
name: widget-g7-sync-health-reviewer
description: Reviews phone→watch glucose sync health — push backoff, offline badge, ack path, tile refresh. Use when debugging sync stalls or offline/reconnect behavior.
disable-model-invocation: true
---

# Widget G7 Sync Health Reviewer

## Goal
Verify end-to-end sync: Dexcom Share → phone cache → Wear Data Layer → watch cache → tile/complication → ack.

## Key files
- `mobile/.../sync/PhoneGlucoseSyncEngine.kt`
- `mobile/.../sync/ActiveGlucoseSyncService.kt` (repush backoff 10/30/60/120 s)
- `mobile/.../sync/PhoneSyncStateStore.kt` (`consecutiveWearPushFailures`)
- `wear/.../services/WearDataLayerListenerService.kt`
- `wear/.../sync/WatchSyncHealthMonitor.kt`
- `wear/.../tile/GlucoseSimpleTileService.kt` (45 s freshness + sync button)

## Review checklist
1. Push failure increments counter; success resets it.
2. Phone UI shows offline badge after threshold (3 failures).
3. Backoff continues on failure — does not abort entire sync loop.
4. Watch saves snapshot on `PATH_LATEST` and requests tile/complication update.
5. Tile sync tap launches `GlucoseRefreshActivity` → phone refresh path.

## Output
- Root cause hypothesis with file/line references
- Minimal fix proposal (Phase 2: `PendingPushQueue`, reconnect flush)
