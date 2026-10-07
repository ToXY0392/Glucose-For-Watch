# Workspace scope — wear-app

| Field | Value |
|-------|-------|
| **Branch** | No permanent branch; use a short-lived `feat/bloc-*` or `fix/bloc-*` branch |
| **Status** | Available on demand |
| **Skill** | `glucose-for-watch-wear-app-scope` |
| **Scope file** | [.cursor/workspace-scopes/wear-app.scope.md](../../.cursor/workspace-scopes/wear-app.scope.md) |

## Allowed paths

- `wear/**`

Read-only: `core/datalayer-contract/**`, `core/model/**`

## Triggers (dormant → active)

| QA session | Failure | Fix |
|------------|---------|-----|
| C.2 | Complication ≠ tile | `wear/complication/`, `wear/tile/` |
| C.6 | Tile missing after reinstall | tile service, manifest |
| C.4 | LOW/HI colors wrong on watch | `AgpComplicationColorRamp.kt` |

Record QA evidence using the QA scope, then fix the issue on a short-lived Wear branch.

## Backlog (post-v0.5.0)

| ID | Task | When |
|----|------|------|
| AUTO-6 | Paparazzi wear tile | v0.6 |

No planned work unless QA triggers fire.

## Verify

```bash
./gradlew :wear:assembleDebug :wear:test
```

## Tile rule

Bump `RESOURCES_VERSION` when tile resources change.
