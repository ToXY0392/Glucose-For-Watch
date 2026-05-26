# Component — Wear tile

> Platform: Wear OS · Protolayout / Material Tiles 1.5  
> Reference: [assets/references/tile_dial_reference.png](../assets/references/tile_dial_reference.png)

## Layout

```
┌─────────────────────────┐
│      [ToXY background]  │
│                         │
│         120             │  ← agp.glucose.* (by range)
│       mg/dL  ↗          │  ← toxy text.secondary + agp trend
│                         │
│    ┌─────────────────┐  │
│    │    ↻ Sync       │  │  ← toxy.accent icon, ≥48dp
│    └─────────────────┘  │
└─────────────────────────┘
```

## Tokens

| Element | Token |
|---------|-------|
| Background | `toxy.color.background.top` |
| Value | `agp.glucose.*` |
| Unit | `toxy.color.text.secondary` |
| Trend (fresh) | same AGP as value |
| Trend (stale) | `agp.glucose.unknown` |
| Sync button | `toxy.color.accent.default` |

## Interaction

- Sync button → `GlucoseRefreshActivity` → phone fetch
- Freshness: `toxy.motion.tile.freshnessIntervalMs` (45 s)

## States

See [sync-states.md](sync-states.md)

## Google guidelines

- ≥ 48 dp touch target
- One primary action (sync edge button)
- [Tile best practices](https://developer.android.com/design/ui/wear/guides/surfaces/tiles/bestpractices)
