# Glucose surfaces

Behavior of each surface that shows a glucose reading. Colors:
[AGP medical layer](agp-medical-layer.md).

## Wear tile

- `GlucoseTileServiceV2`, layout in `GlucoseSimpleTileLayout`, chrome in
  `GlucoseTileChrome`.
- Black background; value in AGP color; unit in neutral grey; trend arrow in
  the value color while fresh.
- Sync action is neutral grey text (no filled button). It opens
  `GlucoseRefreshActivity`, which asks the phone for a fetch.
- Freshness interval 60 s; a reading older than 15 min is dimmed and its status
  line shows its age.
- The clickable status slot is 28 dp high: below the 48 dp target, known gap.
- Tile preview is a plain black icon; launcher and complication picker icons
  use the app logo.

## Wear complication

- Sole provider `GlucoseComplicationServiceV2`, labelled **Glycémie**.
- Types: `SHORT_TEXT`, `LONG_TEXT`, `RANGED_VALUE` (AGP color ramp).
- No data or stale: placeholder, `UNKNOWN` color.
- Refreshed when the Wear cache receives a new reading, throttled to the
  Wear OS 5 min guidance; the validity range is unbounded so System UI never
  hides the slot.

## Wear status screen

- `WearStatusScreen` (Compose Material 3, `ToxyWearColorScheme`).
- Same cache and staleness rule as the tile; glucose number in AGP color.
- Status line: up to date, stale, refreshing, failed, or no data.

## Phone home

- `HomeScreen`: dial whose border and value use the AGP color of the reading,
  neutral when there is no value.
- Refreshed on screen and every 30 s while visible.

## Sync states

| State | Phone | Watch tile |
|-------|-------|------------|
| Fresh, acked | sync pill OK | value in AGP color |
| Push pending, no ack | pill warning | last value |
| Watch offline | pill warning | last value |
| Dexcom auth error | error notification after 2 failures | last value |
| Refreshing | — | sync action disabled |
| Stale | — | dimmed value, age in status line |
| No data | — | `--` |

Watch refresh messages stay visible 45 s while in progress, 30 s once
completed and 90 s after a failure (`RefreshStatusSnapshot`).
