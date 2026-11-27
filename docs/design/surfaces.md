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

- `HomeScreen`, "Menthe & Citron": Bento of glass cards over a mint-to-lemon
  background (`GlassBackground`, `GlassSurface`, `GlassPalette`), Plus Jakarta Sans.
- Glucose card: same light glass as the other tiles (product choice; the standard
  yellow and orange have low contrast on it). Value, range pill, trend arrow, corner glow and a 40–400 mg/dL bar with the
  70 / 180 / 250 thresholds placed to scale, all in the standard AGP colors
  (`HomeGlucoseColor`). A reading older than 15 min turns grey ("Donnée ancienne")
  and its marker is hidden. Trend `NONE` draws no arrow.
- Tiles: watch (status, battery), sync, Dexcom, unit; list: background sync,
  permissions, about.
- Motion: staggered entrance, breathing halos, value count-up with a short pop,
  marker slide, trend rotation, sync spin then check after a successful manual sync,
  battery fill, pressed cards scale to 96 % under a light white veil. Off when the
  system animator scale is 0, in Studio previews and in snapshot exports.
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
