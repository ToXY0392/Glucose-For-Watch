# Component — Wear complication

> Platform: Wear OS watch face

## Types supported

| Type | Value color | Metadata color |
|------|-------------|----------------|
| SHORT_TEXT | AGP | ToXY secondary |
| LONG_TEXT | AGP | ToXY secondary |
| RANGED_VALUE | AGP fill | ToXY secondary label |

## Rules

- Value portion uses `agp.glucose.*`
- `--` when no data → `agp.glucose.unknown`
- Do not use `toxy.accent` for complication value
- The sole registered provider is `GlucoseComplicationServiceV2`, labelled
  **Glycémie**.
- The provider is refreshed when the Wear glucose cache changes; Android
  System UI can still retain a cached picker preview.
- Readings older than 15 min display the no-data placeholder. The underlying
  cache stale flag can be set earlier, after 2 min.
- The complication picker icon uses the app logo; the tile preview is
  intentionally logo-free.
