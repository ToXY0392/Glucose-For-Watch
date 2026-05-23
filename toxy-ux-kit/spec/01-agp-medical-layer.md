# AGP medical layer

> Authoritative glucose color specification · tokens: `tokens/agp.glucose.json`

## Standard

**Ambulatory Glucose Profile (AGP)** / **International Consensus on Time in Range (TIR)** — same stacked-bar colors used in Dexcom Clarity, LibreView, and clinical CGM reports.

References: [agpreport.org](https://www.agpreport.org/) · [PMC8875060](https://pmc.ncbi.nlm.nih.gov/articles/PMC8875060/)

---

## Range table

| Range | mg/dL | Token | Hex |
|-------|-------|-------|-----|
| Very low (L2) | < 54 | `agp.glucose.veryLow` | `#9C0000` |
| Low (L1) | 54–69 | `agp.glucose.low` | `#E00000` |
| In range | 70–180 | `agp.glucose.inRange` | `#008000` |
| High (L1) | 181–250 | `agp.glucose.high` | `#FFCC00` |
| Very high (L2) | > 250 | `agp.glucose.veryHigh` | `#FF9900` |
| Unknown / stale | — | `agp.glucose.unknown` | `#64748B` |

---

## Resolver logic (reference)

```
mgDl < 54        → veryLow
mgDl < 70        → low
mgDl <= 180      → inRange
mgDl <= 250      → high
else             → veryHigh

display == LOW   → low (sentinel)
display == HI    → veryHigh (sentinel)
stale == true    → unknown for trend; value may stay last AGP color or mute per spec
no data          → unknown, display "--"
```

---

## Surfaces that MUST use AGP

- [ ] Wear tile — glucose number
- [ ] Wear tile — trend arrow (when not stale)
- [ ] Wear complication — value / ranged fill
- [ ] Phone home — hero glucose line
- [ ] Notification — glucose value text

---

## Surfaces that MUST NOT use AGP

- Tile background → `toxy.color.background.top`
- Sync button → `toxy.color.accent`
- Card surfaces → `toxy.color.surface.*`
- Unit label `mg/dL` → `toxy.color.text.secondary`

---

## Figma variables

Create a separate collection **「AGP Medical」** — do not merge with **「ToXY Chrome」**.

Import hex values from `tokens/agp.glucose.json`.
