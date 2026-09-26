# Component — Wear tile

> Platform: Wear OS · Protolayout / Material Tiles 1.5  
> Reference: [assets/references/tile_dial_reference.png](../assets/references/tile_dial_reference.png)

## Layout

```
┌─────────────────────────┐
│      [black canvas]     │
│                         │
│         120             │  ← agp.glucose.* (by range)
│       mg/dL  ↗          │  ← toxy text.secondary + agp trend
│                         │
│       ↻ sync            │  ← clickable neutral status text
└─────────────────────────┘
```

## Tokens

| Element | Token |
|---------|-------|
| Background | Absolute black (`#000000`) |
| Value | `agp.glucose.*` |
| Unit | `toxy.color.text.secondary` |
| Trend (fresh) | same AGP as value |
| Trend (stale) | `agp.glucose.unknown` |
| Sync action | Neutral gray text (`#D1D5DB`); no filled button |

## Interaction

- Sync button → `GlucoseRefreshActivity` → phone fetch
- Tile requests refresh at the 45 s cadence. It visually dims readings older
  than 15 min; this is separate from the 2 min stale flag in the Dexcom/Wear
  cache.
- The current clickable status slot is 28 dp high. The 48 dp touch-target
  recommendation below remains a design target and is not yet met by this tile.

The scrolling tile preview is a plain black icon with no logo. The Wear app
launcher icon and complication picker icon use the phone app logo.

## States

See [sync-states.md](sync-states.md)

## Google guidelines

- ≥ 48 dp touch target
- One primary action (sync edge button)
- [Tile best practices](https://developer.android.com/design/ui/wear/guides/surfaces/tiles/bestpractices)
