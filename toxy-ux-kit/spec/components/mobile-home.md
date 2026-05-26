# Component — Mobile home

> Platform: Android phone · Material 3 XML

## Regions

| Region | Layer | Notes |
|--------|-------|-------|
| Hero glucose value | **AGP** | Large line, colored by range |
| Hero subtitle (trend, age) | ToXY | text.secondary |
| Sync button | ToXY | accent filled |
| Watch status ring | ToXY | sync.ok / sync.warn / sync.error dot |
| Status pills | ToXY | Semantic backgrounds |
| Background canvas | ToXY | gradient background.top → bottom |

## Spacing

- Screen padding: `toxy.spacing.screenPadH/V`
- Touch min: `toxy.spacing.touchMin`

## Reference

Legacy implementation: `mobile/src/main/res/layout/activity_main.xml`  
Target tokens: export from `toxy-ux-kit/tokens/`
