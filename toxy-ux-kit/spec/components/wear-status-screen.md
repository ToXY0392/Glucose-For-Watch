# Component — Wear status screen (Compose M3)

> Platform: Wear OS · `WearStatusScreen.kt` · round 450×450 dp reference  
> Chrome: `ToxyWearColorScheme` · glucose: AGP only

## Layout

```
┌─────────────────────────┐
│   [background #0D1117]  │
│                         │
│         120             │  ← agp.glucose.* (48sp bold)
│       mg/dL  ↗          │  ← toxy text.secondary + agp trend
│                         │
│      Up to date         │  ← sync status (ok / warn / error)
│   Battery 72%           │  ← optional health line
│                         │
│    ┌─────────────────┐  │
│    │    ↻ Sync       │  │  ← M3 Button · primary mint
│    └─────────────────┘  │
└─────────────────────────┘
```

## Tokens

| Element | Token |
|---------|-------|
| Screen background | `toxy.color.background.top` |
| Glucose value | `agp.glucose.*` |
| Unit label | `toxy.color.text.secondary` |
| Trend arrow (fresh) | same AGP as value |
| Sync status OK | `toxy.color.text.secondary` |
| Sync status stale | `toxy.color.sync.warn` |
| Sync status error | `toxy.color.sync.error` |
| Sync button fill | `toxy.color.accent.default` |
| Sync button label | `toxy.color.accent.on` |

## Figma frame

| Property | Value |
|----------|-------|
| Artboard | 450 × 450 (round clip) |
| Value type | 48 / Bold |
| Unit + trend | 14–22 / Medium |
| Status labels | 12 / Regular |
| Button width | 85% of content width, min height 48 dp |

## States

| State | Hero | Status line |
|-------|------|-------------|
| Fresh in-range | AGP green + trend | "Up to date" · secondary |
| Stale | AGP + unknown trend | "Stale data" · sync.warn |
| No data | `--` · unknown | "Set up Dexcom Share on phone…" |
| Refreshing | last value | "Refreshing…" |
| Error | last value | "Sync failed" · sync.error |

See [sync-states.md](sync-states.md).

## Parity with tile

Same data source as tile + complication (`WearGlucoseSurfaceModelFactory`). Do not use mint on glucose numerals.
