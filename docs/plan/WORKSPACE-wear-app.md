# Workspace — wear-app

| Field | Value |
|-------|-------|
| **Branch** | `workspace/wear-app` |
| **Status** | dormant (trigger-based) |
| **Skill** | `widget-g7-wear-app-scope` |
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

Document failure on `workspace/qa-hardware` first, then fix here.

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

## Rebase (weekly while dormant)

```bash
git fetch origin && git rebase origin/integrate
```
