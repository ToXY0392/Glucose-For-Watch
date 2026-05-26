# C.3 — Watch offline 2 h

| Field | Value |
|-------|-------|
| Date | 2026-05-26 |
| Branch | `workspace/qa-hardware` |
| Gate | G-C · catch-up |
| Status | **N/A — skipped** (dedicated 2 h session not run) |

## Decision

Dedicated 2 h airplane-mode session **skipped** for v0.5.0 sideload. Script started but watch did not go offline within operator window.

## Substitute evidence

| Source | Coverage |
|--------|----------|
| `OfflineOnlineSyncScenarioTest` | offline enqueue → online push |
| `WatchReconnectDetectorTest` | offline→online transition |
| C.7 soak 8 h | phone+watch stable overnight · 0 FATAL |
| C.2 sample | phone=watch cache sync over 25 min |
| C.5 / X.6 | 30 min continuous sync PASS |

Optional future run: `scripts/qa/sample-c3-offline-session.ps1`

## Sign-off

| Role | Date | OK |
|------|------|-----|
| C.3 waived (automated + C.7) | 2026-05-26 | ✅ |
