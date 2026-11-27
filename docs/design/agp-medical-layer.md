# AGP medical layer

Glucose colors follow the **Ambulatory Glucose Profile (AGP)** / International
Consensus on Time in Range. References: [agpreport.org](https://www.agpreport.org/) ·
[PMC8875060](https://pmc.ncbi.nlm.nih.gov/articles/PMC8875060/).

**Source of truth:** `AgpGlucoseColors` and `GlucoseRangeResolver` in
`core/model/src/main/java/com/glucoseforwatch/core/model/GlucoseRange.kt`.
No other file defines a glucose color, in Kotlin or XML.
`scripts/dev/lint_agp_colors.py` enforces it in CI.

## Ranges

| Range | mg/dL | Constant | Color |
|-------|-------|----------|-------|
| Very low (L2) | < 54 | `VERY_LOW` | `#D32F2F` |
| Low (L1) | 54–69 | `LOW` | `#D32F2F` |
| In range | 70–180 | `IN_RANGE` | `#008000` |
| High (L1) | 181–250 | `HIGH` | `#FFCC00` |
| Very high (L2) | > 250 | `VERY_HIGH` | `#FF9900` |
| Unknown / stale | — | `UNKNOWN` | `#9A948D` |

Blue is never used for a glucose value. These colors are never lightened or darkened:
a glucose value always sits on a dark surface where they stay readable (black watch
tile, near-opaque dark glass card on the phone).

## Staleness

One rule, `GlucoseFreshness.STALE_AFTER_MS` (15 min), for the Dexcom fetch,
the Wear cache, the tile, the complication and the watch app. A stale value
uses `UNKNOWN` for its trend and is dimmed or replaced by a placeholder,
depending on the surface.

## Surfaces that must use AGP

- Wear tile: glucose number, and trend arrow while fresh
- Wear complication: value and ranged fill
- Wear status screen: glucose number
- Phone home: glucose card value, range pill, trend arrow, glow and 40–400 scale
  (`HomeGlucoseColor`, dark glass card; stale readings in `UNKNOWN`)

## Surfaces that must not use AGP

Backgrounds, sync buttons, cards, unit labels and status text use the chrome
colors: `gfw_*` resources, `GlassPalette` and `ToxyPhoneColorScheme` on the phone,
`GlucoseTileChrome` and `ToxyWearColorScheme` on the watch.

## Accessibility

- Never rely on color alone: always show the number, or `LOW` / `HI`.
- Minimum touch target on Wear: 48 dp.
