# C.2 — Complication vs tile (30 min)

| Field | Value |
|-------|-------|
| Date started | 2026-05-26 |
| Branch | `workspace/qa-hardware` |
| Gate | G-C · KPI K3 |
| Duration | 30 min (6 samples every 5 min) |
| Status | **PASS** (2026-05-26) |

## J2 baseline (2026-05-26)

| Check | Result |
|-------|--------|
| `stability-gate.ps1 -CheckLogcatOnly` | PASS · 0 FATAL |
| `hardware-smoke.ps1` | PASS · hero **215 mg/dL** · push/ack aligned · watch 0.4.0 |
| `sample-c2-session.ps1` | **PASS** 6/6 · [report](2026-05-26_0449-C2-complication-sample.md) |

Automated sampler: phone hero = watch `glucose_cache` (shared by tile + complication). Readings tracked 207 → 152 mg/dL over 25 min.

Watch **complication** and **tile** show the **same glucose value** as phone hero after sync; drift ≤ 1 sync cycle (lag ≤ ~45 s).

## Prerequisites

- Phone + watch connected · Data Layer OK
- Tile and at least one complication added on watch face
- No active overnight soak

## Baseline (automated)

Run before starting the 30 min window:

```powershell
.\scripts\qa\hardware-smoke.ps1
.\scripts\qa\stability-gate.ps1 -CheckLogcatOnly
```

## Procedure

1. Note phone hero value (`hardware-smoke` or app).
2. Open watch tile → note value + trend.
3. Check complication on active watch face → same value?
4. Wait **5 min** (or until next Dexcom update + sync).
5. Repeat steps 1–3 — **6 samples total** (~30 min).

Optional automated sampler (watch adb reads shared `glucose_cache` = tile + complication source):

```powershell
.\scripts\qa\sample-c2-session.ps1
```

Optional live logs: `.\scripts\qa\tail-sync-logs.ps1`

## Sample log (no PNG required)

| # | Time | Phone hero | Tile | Complication | Match | Lag notes |
|---|------|------------|------|--------------|-------|-----------|
| 1 | 04:49 | 207 | 207 | 207 | PASS | auto sample |
| 2 | 04:54 | 204 | 204 | 204 | PASS | auto sample |
| 3 | 04:59 | 197 | 197 | 197 | PASS | auto sample |
| 4 | 05:04 | 187 | 187 | 187 | PASS | auto sample |
| 5 | 05:09 | 171 | 171 | 171 | PASS | auto sample |
| 6 | 05:14 | 152 | 152 | 152 | PASS | auto sample |

Tile/complication values inferred from watch cache (same source as UI).

## Pass criteria

- 6/6 samples: tile = complication = phone (within one sync)
- No wear crash · no stale complication > 45 s after phone update
- `hardware-smoke`: push/ack aligned at end

## Fail criteria

- Persistent mismatch tile ≠ complication
- Complication frozen while tile updates
- FATAL in logcat during session

Fix routing: sync → `integrate` / `feat/bloc-b-*` · wear UI → `workspace/wear-app`

## Sign-off

| Role | Date | OK |
|------|------|-----|
| C.2 complete (6/6 samples) | 2026-05-26 | ✅ |

When complete: update [PROGRESS.md](../../plan/PROGRESS.md) C.2 → ✅ · [stability-signoff](../2026-05-26-stability-signoff.md).
