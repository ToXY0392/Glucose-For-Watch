# Scope — workspace/mobile-app

| Field | Value |
|-------|-------|
| **Branch** | `workspace/mobile-app` |
| **Status** | dormant (until v0.5.0 tag) |
| **Skill** | `widget-g7-mobile-app-scope` |

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

## Backlog

**Post-tag v0.5.0:**

1. B.4 — WatchSyncVerifier via engine (`mobile/watch/WatchSyncVerifier.kt`, sync-critical)
2. F0–F3 — Compose M3 (forbidden before v0.5.0 tag)

## Security

- Dexcom credentials: `EncryptedSharedPreferences` only — never log tokens
- See [docs/legal/privacy-policy.md](../../docs/legal/privacy-policy.md)
