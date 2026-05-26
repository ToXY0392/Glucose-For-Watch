# C.1 — AGP 60 / 120 / 200 visual QA

| Field | Value |
|-------|-------|
| Date started | 2026-05-26 |
| Branch | `workspace/qa-hardware` |
| Gate | G-C · KPI visual |
| Duration | ~2 h (may span multiple Dexcom readings) |
| Status | **in progress** (1/3 plateaus) |

## J1 session log (2026-05-26)

| Check | Result |
|-------|--------|
| `stability-gate.ps1 -CheckLogcatOnly` | PASS · 0 FATAL |
| `hardware-smoke.ps1` | PASS · hero **221 mg/dL** · push/ack aligned |
| Phone capture 200 mg/dL | **Retry needed** — AOD/lock screen; unlock phone then `capture-c1-agp-session.ps1 -ValueMgDl 200 -LaunchApp` |
| Watch capture | **Pending** — wireless adb offline (`connect-watch-adb.ps1`) |

**Reading 221 mg/dL** matches high band (181-250) — valid plateau for **200** once app hero is visible.

## Objective

Verify AGP medical colors on **phone hero** and **watch tile/complication** at three plateaus:

| mg/dL | AGP band | Expected color |
|-------|----------|----------------|
| **60** | Low (L1) | Red `#E00000` |
| **120** | In range | Green `#008000` |
| **200** | High (L1) | Amber `#FFCC00` |

Spec: [toxy-ux-kit/spec/01-agp-medical-layer.md](../../toxy-ux-kit/spec/01-agp-medical-layer.md)

## Prerequisites

- Phone + watch connected (Data Layer sync OK)
- Dexcom Share active · readings updating
- `local.properties`: `widgetg7.adb.phone.serial`, `widgetg7.adb.watch.serial`
- No active overnight soak running

## Procedure

1. Baseline smoke (optional):

   ```powershell
   .\scripts\qa\hardware-smoke.ps1
   ```

2. For each target (60, 120, 200) — **when Dexcom shows a reading in that band**:

   ```powershell
   .\scripts\qa\capture-c1-agp-session.ps1 -ValueMgDl 120
   # or all three when readings available:
   .\scripts\qa\capture-c1-agp-session.ps1 -All
   ```

3. Open watch tile + complication; confirm colors match phone hero.

4. Mark **Visual OK** in capture table below and in generated `*_C1-agp-captures.md`.

## Captures

| mg/dL | Phone | Watch tile | Complication | Visual OK | Notes |
|-------|-------|------------|--------------|-----------|-------|
| 60 | pending | pending | pending | pending | wait for low reading |
| 120 | pending | pending | pending | pending | wait for in-range reading |
| 200 | retry | pending | pending | pending | smoke 221 mg/dL OK; unlock phone + recapture hero |

Existing reference captures: [2026-05-24_B1_phone_G7_hero.png](../captures/2026-05-24_B1_phone_G7_hero.png) · [2026-05-24_B1_watch_G7_tile.png](../captures/2026-05-24_B1_watch_G7_tile.png)

## Robolectric fallback (phone layout only — not a C.1 substitute)

Six home **states** (not AGP plateaus):

```powershell
.\gradlew.bat :mobile:testDebugUnitTest --tests "com.widgetg7.mobile.preview.AppPreviewExporterTest"
```

Output: `mobile/build/app-previews/*.png` — useful for layout, **not** gate C.1 evidence.

## Fail criteria

- Wrong AGP color on hero vs spec
- Tile/complication color differs from phone for same reading
- Stale/unknown color when data is fresh

Fix routing: color token → `workspace/ui-ux-kit` · wear render → `workspace/wear-app`

## Sign-off

| Role | Date | OK |
|------|------|-----|
| C.1 complete (3/3 plateaus) | | ☐ |

When complete: update [PROGRESS.md](../../plan/PROGRESS.md) C.1 → ✅ · include in batch PR J5.
