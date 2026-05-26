# C.4 — LOW / HI display

| Field | Value |
|-------|-------|
| Date | pending |
| Branch | `workspace/qa-hardware` |
| Gate | G-C · display |
| Duration | when available |
| Status | **N/A — skipped** (no LOW/HI observed during test window) |

## Decision

Manual LOW/HI display check **skipped** — no Dexcom LOW/HI event during v0.5.0 QA window. Acceptable per gate wording (*if available*).

Substitute: `hardware-smoke.ps1` confirms numeric hero + sync; unit tests cover range resolver edge cases.

When Dexcom reports **LOW** or **HI**, phone hero and watch surfaces display the correct labels/colors (not a numeric value out of range without context).

## Prerequisites

- Real LOW/HI reading from Dexcom **or** documented skip if unavailable during test window
- Phone + watch online

## Procedure

1. When LOW or HI occurs naturally (or use test account if available):
   - Note phone hero text + color
   - Note watch tile + complication
2. Confirm AGP / alert styling matches spec for extreme values.
3. Run smoke after event:

   ```powershell
   .\scripts\qa\hardware-smoke.ps1
   ```

## Session log (no PNG)

| Event | Time | Phone | Watch tile | Complication | Visual OK | Notes |
|-------|------|-------|------------|--------------|-----------|-------|
| LOW | — | — | — | — | **N/A** | not observed |
| HI | — | — | — | — | **N/A** | not observed |

## Skip policy

If no LOW/HI during v0.5.0 test window: mark **N/A — not observed** in sign-off with date range searched. Document operator note; gate may accept partial with follow-up post-release.

## Pass criteria

- LOW/HI readable on phone + watch
- No crash · no blank/stale unknown for fresh alert

## Sign-off

| Role | Date | OK |
|------|------|-----|
| C.4 complete or N/A | 2026-05-26 | ✅ N/A |

Spec: [AGP medical layer](../../toxy-ux-kit/spec/01-agp-medical-layer.md)
