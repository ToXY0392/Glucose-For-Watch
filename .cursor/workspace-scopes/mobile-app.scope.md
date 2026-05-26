# Scope — sandbox/mobile-app

| Field | Value |
|-------|-------|
| **Branch** | `sandbox/mobile-app` |
| **Status** | **ACTIVE** (post v0.6.0) |
| **Skill** | `glucose-for-watch-mobile-app-scope` |

## Allowed paths

- `mobile/**`

## Read-only

- `core/**`
- `feature/sync/**`
- `feature/dexcom-share/**`
- `feature/watch-install/**`

## Forbidden

- `wear/**`
- `toxy-ux-kit/tokens/**` (use exported `@color/toxy_*` only)
- `.github/**`, root `build.gradle.kts`, `settings.gradle.kts`
- `feature/dexcom-share/**` credential storage changes without explicit review

## Verify

```bash
./gradlew :mobile:assembleDebug :mobile:test
```
