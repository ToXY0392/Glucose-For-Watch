---
name: widget-g7-agp-color-guard
description: Enforces AGP medical color rules — glucose values use agp.glucose tokens only, never ToXY mint. Use when adding glucose UI on phone, wear tile, or complications.
disable-model-invocation: true
---

# Widget G7 AGP Color Guard

## Rule
**Medical layer:** glucose numbers, range bands, trend arrows (when fresh) → `GlucoseRangeResolver` / `agp_*` colors.

**Chrome layer:** backgrounds, buttons, sync UI → `toxy_*` / `ToxyTileTheme` mint accent OK.

## Thresholds (mg/dL)
| Range | Color token |
|-------|-------------|
| &lt; 54 | veryLow `#9C0000` |
| 54–69 | low `#E00000` |
| 70–180 | inRange `#008000` |
| 181–250 | high `#FFCC00` |
| &gt; 250 | veryHigh `#FF9900` |
| stale / no data | unknown `#64748B` |

## Enforcement grep
```bash
# Should return 0 hits on glucose TextViews / tile value text
rg "wg7_accent|toxy_accent" --glob "*.kt" wear/ mobile/ | rg -i "glucose|value|hero"
```

## Touch points
- `core/model/GlucoseRange.kt`
- `mobile/.../MainActivity.kt` (hero value)
- `wear/.../tile/ToxyTileTheme.kt`
- `wear/.../complication/GlucoseComplicationService.kt`
- `wear/.../data/GlucoseSnapshot.semanticColorArgb()`

## Output
- Pass/fail on AGP compliance
- List of violations with suggested fix
