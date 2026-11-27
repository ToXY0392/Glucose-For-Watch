---
name: glucose-for-watch-agp-color-guard
description: Enforces AGP medical color rules in Glucose For Watch: glucose values, range bands and fresh trend arrows use agp_* tokens via GlucoseRangeResolver, never ToXY mint. Use when adding or changing glucose UI on the phone, wear tile or complications, or when reviewing a diff that touches glucose colors.
---

# Glucose For Watch AGP Color Guard

## Rule
**Medical layer:** glucose numbers, range bands, trend arrows (when fresh) → `GlucoseRangeResolver` / `agp_*` colors.

**Chrome layer:** backgrounds, buttons, sync UI → `wg7_*` / `gfw_*` resources, `GlucoseTileChrome` chrome constants.

## Thresholds (mg/dL)
Source of truth: `AgpGlucoseColors` in `core/model/GlucoseRange.kt`. Values below mirror it.

| Range | Color token |
|-------|-------------|
| &lt; 54 | veryLow `#D32F2F` |
| 54–69 | low `#D32F2F` |
| 70–180 | inRange `#008000` |
| 181–250 | high `#FFCC00` |
| &gt; 250 | veryHigh `#FF9900` |
| stale / no data | unknown `#9A948D` |

## Enforcement grep
```bash
# Should return 0 hits on glucose TextViews / tile value text
rg "wg7_accent|toxy_accent" --glob "*.kt" wear/ mobile/ | rg -i "glucose|value|hero"
```

## Touch points
- `core/model/GlucoseRange.kt`
- `mobile/.../MainActivity.kt` (hero value)
- `wear/.../tile/GlucoseTileChrome.kt`
- `wear/.../complication/GlucoseComplicationServiceV2.kt`
- `wear/.../data/GlucoseSnapshot.semanticColorArgb()`

## Output
- Pass/fail on AGP compliance
- List of violations with suggested fix
