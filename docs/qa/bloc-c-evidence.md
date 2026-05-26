# Bloc C — plan complete (v0.5.0 / G-C)

| Field | Value |
|-------|-------|
| Date | 2026-05-26 |
| Branch | `workspace/qa-hardware` |
| Gate | **G-C closed** (sideload path) |

## Matrix (final)

| ID | Scenario | Status | Evidence |
|----|----------|--------|----------|
| C.0 | Crash reg | ✅ | [2026-05-25_1605-bloc-c-automated.md](sessions/2026-05-25_1605-bloc-c-automated.md) |
| C.1 | AGP visual | **N/A** | [waived](sessions/2026-05-26_C1-agp-checklist.md) |
| C.2 | Complication vs tile | ✅ | [2026-05-26_0449-C2-complication-sample.md](sessions/2026-05-26_0449-C2-complication-sample.md) |
| C.3 | Offline 2 h | **N/A** | [waived](sessions/2026-05-26_C3-offline-checklist.md) |
| C.4 | LOW/HI | **N/A** | [waived](sessions/2026-05-26_C4-low-hi-checklist.md) |
| C.5 | Continuous sync | ✅ | [X.6](soak-runs/2026-05-25_1458-X.6-soak.md) |
| C.6 | Reinstall + tile | **N/A** | [waived](sessions/2026-05-26_C6-reinstall-checklist.md) |
| C.7 | Overnight 8 h | ✅ | [C.7](soak-runs/2026-05-26_C.7-soak.md) |
| C.8 | Battery ≤ 20 % | **N/A** | [waived](sessions/2026-05-26_C8-battery-checklist.md) |

## Sign-off

[2026-05-26-stability-signoff.md](2026-05-26-stability-signoff.md) — **GO G-C**

## Next (J5 → J6)

1. PR `docs/qa/bloc-c-evidence` → `integrate`
2. Tag **v0.5.0** on `integrate` after merge + gatekeeper

## Scripts added (this branch)

- `scripts/qa/sample-c2-session.ps1`
- `scripts/qa/sample-c3-offline-session.ps1`
- `scripts/qa/sample-c8-battery-session.ps1`
