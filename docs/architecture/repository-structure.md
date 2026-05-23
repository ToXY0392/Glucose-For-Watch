# Repository structure

> **Last updated:** 2026-05-23

## Top-level layout

```
Widget G7/
├── mobile/              # Phone APK (applicationId: com.widgetg7.mobile)
├── wear/                # Wear companion APK
├── core/
│   ├── datalayer-contract/
│   ├── model/
│   └── testing/
├── feature/
│   ├── sync/
│   ├── dexcom-share/
│   └── watch-install/
├── docs/                # Documentation (see docs/index.md)
├── scripts/             # Dev, release, Windows helpers
├── .cursor/             # Rules, skills, hooks
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── CHANGELOG.md
├── CONTRIBUTING.md
└── COMPATIBILITY.md
```

## Build outputs

| Artifact | Path |
|----------|------|
| Mobile debug APK | `mobile/build/outputs/apk/debug/mobile-debug.apk` |
| Wear debug APK | `wear/build/outputs/apk/debug/wear-debug.apk` |
| Embedded wear APK (debug) | Generated into mobile debug assets |

## Configuration (not in git)

| File | Purpose |
|------|---------|
| `local.properties` | `sdk.dir`, ADB serials |
| `gradle.properties` | Dexcom Share credentials, application ID |

Use `gradle.properties.example` as template.

## Custom Gradle task

`installWidgetG7Debug` (root `build.gradle.kts`):

1. Assembles mobile + wear debug APKs
2. Installs via ADB using serials from `local.properties`:
   - `widgetg7.adb.phone.serial`
   - `widgetg7.adb.watch.serial`

## Source highlights

| Concern | Path |
|---------|------|
| Dexcom client | `feature/dexcom-share/.../DexcomShareClient.kt` |
| Sync engine | `feature/sync/.../GlucoseSyncEngine.kt` |
| Phone sync | `mobile/.../sync/` |
| Data Layer contract | `core/datalayer-contract/.../GlucoseDataLayerContract.kt` |
| Wear listener | `wear/.../services/WearDataLayerListenerService.kt` |
| Tile | `wear/.../tile/GlucoseSimpleTileService.kt` |
| Complication | `wear/.../complication/GlucoseComplicationService.kt` |

## Tests

| Location | Coverage |
|----------|----------|
| `feature/sync/src/test/` | Engine, policies, formatters |
| `mobile/src/test/` | Phone-specific policies |

## Related

- [Architecture overview](overview.md)
- [Getting started](../development/getting-started.md)
