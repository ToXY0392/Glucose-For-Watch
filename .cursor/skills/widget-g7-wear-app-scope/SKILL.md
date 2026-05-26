---
name: widget-g7-wear-app-scope
description: >-
  Scope-limited work on workspace/wear-app — wear module, tile, complication,
  Compose UI only. Use when checked out on workspace/wear-app or for wear-only
  tasks. Refuses mobile and Dexcom feature code.
disable-model-invocation: true
---

# Widget G7 Wear App Scope

## Branch

Expected: **`workspace/wear-app`**. Warn if branch mismatch.

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
- Run `widget-g7-repo-hygiene` before commit

## Hard constraints (soak)

Do not run `installWidgetG7Debug` / `adb install` during active soak unless user explicitly asks.

## Output

Files touched · test result · RESOURCES_VERSION bump note if applicable
