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
│      À jour             │  ← sync status (ok / warn / error)
│   Batterie 72%          │  ← optional health line
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
| Fresh in-range | AGP green + trend | « À jour » · secondary |
| Stale | AGP + unknown trend | « Donnée périmée » · sync.warn |
| No data | `--` · unknown | « Configurez Dexcom… » |
| Refreshing | last value | « Actualisation… » |
| Error | last value | « Échec de synchro » · sync.error |

See [sync-states.md](sync-states.md).

## Parity with tile

Same data source as tile + complication (`WearGlucoseSurfaceModelFactory`). Do not use mint on glucose numerals.
