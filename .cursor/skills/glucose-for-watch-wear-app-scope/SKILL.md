---
name: glucose-for-watch-wear-app-scope
description: >-
  Scope-limited work on sandbox/wear-app — wear module, tile, complication,
  Compose UI only. Use when checked out on sandbox/wear-app or for wear-only
  tasks. Refuses mobile and Dexcom feature code.
disable-model-invocation: true
---

# Glucose For Watch Wear App Scope

## Branch

Expected: **`sandbox/wear-app`**. Warn if branch mismatch.

Scope reference: [.cursor/workspace-scopes/wear-app.scope.md](../../workspace-scopes/wear-app.scope.md)

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

Do not run `installWidgetG7Debug` / `adb install` during active soak unless user explicitly asks.

## Output

Files touched · test result · RESOURCES_VERSION bump note if applicable
