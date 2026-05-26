# C.8 — Watch battery ≤ 20 %

| Field | Value |
|-------|-------|
| Date | 2026-05-26 |
| Branch | `workspace/qa-hardware` |
| Gate | G-C · K1 |
| Status | **N/A — skipped** (battery never ≤ 20 % during QA window) |

## Decision

Low-battery watch session **skipped** for v0.5.0 sideload. Watch remained at **100 %** (charging) during entire QA window.

## Substitute evidence

| Source | Coverage |
|--------|----------|
| `BatteryDegradedPolicyTest` | sync_limited / degraded when battery low |
| `WatchBatteryPolicyTest` | phone-side policy when watch reports low battery |
| C.7 soak 8 h | phone stable with watch connected overnight |
| `hardware-smoke.ps1` | ongoing push/ack with watch online |

## Pre-flight (2026-05-26)

| Check | Result |
|-------|--------|
| Watch battery | 100 % · charging · `sync_limited=false` |
| Smoke | PASS · 0 FATAL |

Optional script if battery drops later: `scripts/qa/sample-c8-battery-session.ps1`

## Sign-off

| Role | Date | OK |
|------|------|-----|
| C.8 waived (unit tests + C.7) | 2026-05-26 | ✅ |
