---
name: glucose-for-watch-mobile-app-scope
description: Path scope for phone app work in Glucose For Watch: mobile/ only, shared modules read-only, wear and ux-kit tokens forbidden. Use when on sandbox/mobile-app, or when a change on any branch touches mobile/ only.
---

# Glucose For Watch Mobile App Scope

## Branch

Long-lived: **`sandbox/mobile-app`**. Also applies on a short-lived branch whose changes touch `mobile/` only.

Scope reference: [.cursor/workspace-scopes/mobile-app.scope.md](../../../.cursor/workspace-scopes/mobile-app.scope.md)

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

- `EncryptedSharedPreferences` for Dexcom credentials — never log tokens or secrets
- Run `glucose-for-watch-repo-hygiene` before commit
- [privacy-policy.md](../../../docs/legal/privacy-policy.md)

## Hard constraints (soak)

Do not run `installGlucoseForWatchDebug` / `adb install` during active soak unless user explicitly asks.

## Output

Files touched · test result · sync-critical flag if applicable
