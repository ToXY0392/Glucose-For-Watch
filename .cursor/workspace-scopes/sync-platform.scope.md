# Scope — sandbox/sync-platform

| Field | Value |
|-------|-------|
| **Branch** | `sandbox/sync-platform` |
| **Status** | active (sync-critical) |
| **Skill** | `glucose-for-watch-sync-platform-scope` |

## Allowed paths

- `feature/sync/**`
- `core/datalayer-contract/**`
- `core/model/**`

## Read-only

- `mobile/**/sync/**`, `wear/**` — UI fixes on `sandbox/mobile-app` or `sandbox/wear-app`

## Forbidden

- `feature/dexcom-share/**`
- `toxy-ux-kit/**`
- `.github/**`, root Gradle files without explicit review

## Verify

```bash
./gradlew :feature:sync:testDebugUnitTest :core:model:testDebugUnitTest
```
