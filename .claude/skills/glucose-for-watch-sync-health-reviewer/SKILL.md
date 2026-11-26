---
name: glucose-for-watch-sync-health-reviewer
description: Reviews phone→watch glucose sync health — push backoff, offline badge, ack path, tile refresh. Use when debugging sync stalls or offline/reconnect behavior.
disable-model-invocation: true
---

# Glucose For Watch Sync Health Reviewer

## Goal
Verify end-to-end sync: Dexcom Share → phone cache → Wear Data Layer → watch cache → tile/complication → ack.

## Key files
- `mobile/.../sync/PhoneGlucoseSyncEngine.kt`
- `mobile/.../sync/ActiveGlucoseSyncService.kt` (unacked repush 10/30/60/120 s; 20/45/90 s in degraded mode)
- `mobile/.../sync/PhoneSyncStateStore.kt` (`consecutiveWearPushFailures`)
- `wear/.../services/WearDataLayerListenerService.kt`
- `wear/.../sync/WatchSyncHealthMonitor.kt`
- `wear/.../tile/GlucoseTileServiceV2.kt` (45 s refresh request + sync button; tile dims after 15 min)

## Review checklist
1. Push failure increments counter; success resets it.
2. Phone UI shows offline badge after threshold (3 failures).
3. Backoff continues on failure — does not abort entire sync loop.
4. Watch saves snapshot on `PATH_LATEST` and requests tile/complication update.
5. Tile sync tap launches `GlucoseRefreshActivity` → phone refresh path.
6. Dexcom/Wear cache flags source readings stale after 2 min; this differs from
   the tile and complication 15 min display thresholds.

## Output
- Findings with file/line references
- Verified delivery/reconnect behavior and any remaining minimal fix
