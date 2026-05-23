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

## Known issue

Complication UI cache can lag behind tile — invalidate on cache write (app Phase 2).
