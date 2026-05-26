---
name: glucose-for-watch-mobile-app-scope
description: >-
  Scope-limited work on sandbox/mobile-app — mobile module only. Use when
  checked out on sandbox/mobile-app or when the user asks for phone-only
  changes. Refuses wear, ux-kit tokens, and shared module edits.
disable-model-invocation: true
---

# Widget G7 Mobile App Scope

## Branch

Expected: **`sandbox/mobile-app`**. Warn if branch mismatch.

Scope reference: [.cursor/workspace-scopes/mobile-app.scope.md](../../workspace-scopes/mobile-app.scope.md)

## Allowed

- `mobile/**`

## Read-only

- `core/**`, `feature/sync/**`, `feature/dexcom-share/**`, `feature/watch-install/**`

## Forbidden (hard block)

- `wear/**`
- `toxy-ux-kit/tokens/**`
- `.github/**`, root Gradle files
- Changes to Dexcom credential storage without explicit user review

## Cross-boundary (B.4, sync)

If `feature/sync/**` must change → stop · use `feat/bloc-b-*` or `workspace/sync-platform` (Phase B).

## Verify

```bash
./gradlew :mobile:assembleDebug :mobile:test
```

## Security

- `EncryptedSharedPreferences` for Dexcom credentials — never log tokens/passwords
- Run `glucose-for-watch-repo-hygiene` before commit
- [privacy-policy.md](../../../docs/legal/privacy-policy.md)

## Hard constraints (soak)

Do not run `installWidgetG7Debug` / `adb install` during active soak unless user explicitly asks.

## Output

Files touched · test result · sync-critical flag if applicable
