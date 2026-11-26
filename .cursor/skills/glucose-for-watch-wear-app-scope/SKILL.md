---
name: glucose-for-watch-wear-app-scope
description: Path scope for watch app work in Glucose For Watch: wear/ only (tile, complication, Compose UI), shared contract read-only, mobile and Dexcom code forbidden. Use when a change on any branch touches wear/ only.
---

# Glucose For Watch Wear App Scope

## Activation

Path-based: applies on any short-lived branch (`feat/`, `fix/`, `qa/`, `test/`, …) whose changes touch the allowed paths below. `sandbox/wear-app` is dormant — do not recreate it without an explicit decision ([WORKSPACE.md](../../../docs/plan/WORKSPACE.md)). PR target: **`develop/integration`**.

Scope reference: [.cursor/workspace-scopes/wear-app.scope.md](../../../.cursor/workspace-scopes/wear-app.scope.md)

## Allowed

- `wear/**`

## Read-only

- `core/datalayer-contract/**`, `core/model/**`

## Forbidden (hard block)

- `mobile/**`
- `feature/dexcom-share/**`
- `.github/**`, root Gradle files

## Tile resources

Bump `RESOURCES_VERSION` when tile drawables or protolayout resources change.

## Verify

```bash
./gradlew :wear:assembleDebug :wear:test
```

## Security

- No credentials on watch — cache only
- Run `glucose-for-watch-repo-hygiene` before commit

## Hard constraints (soak)

Do not run `installGlucoseForWatchDebug` / `adb install` during active soak unless user explicitly asks.

## Output

Files touched · test result · RESOURCES_VERSION bump note if applicable
