# Scope — sandbox/wear-app

| Field | Value |
|-------|-------|
| **Branch** | `sandbox/wear-app` |
| **Status** | dormant (trigger-based) |
| **Skill** | `glucose-for-watch-wear-app-scope` |

## Allowed paths

- `wear/**`

## Read-only

- `core/datalayer-contract/**`
- `core/model/**`

## Forbidden

- `mobile/**`
- `feature/dexcom-share/**` (no credentials on watch)
- `.github/**`, root Gradle files

## Verify

```bash
./gradlew :wear:assembleDebug :wear:test
```

## Triggers (dormant → active)

| QA failure | Fix location |
|------------|--------------|
| C.2 complication ≠ tile | `wear/complication/`, `wear/tile/` |
| C.6 tile missing after reinstall | `wear/.../tile/`, manifest |
| C.4 LOW/HI colors wrong on watch | `AgpComplicationColorRamp.kt` |

## Backlog (post-v0.5.0)

- AUTO-6 — Paparazzi wear tile (v0.6)

## Tile rule

Bump `RESOURCES_VERSION` in tile service when drawable/tile resources change.
