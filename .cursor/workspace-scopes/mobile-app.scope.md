# Scope — workspace/mobile-app

| Field | Value |
|-------|-------|
| **Branch** | `workspace/mobile-app` |
| **Status** | **ACTIVE** (primary sandbox post v0.5.0 · Bloc F) |
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

**Post-tag v0.5.0 (active):**

1. F0–F3 — Compose M3 on `mobile/` (see [ACTION-PLAN §11](../../docs/plan/ACTION-PLAN.md#11-compose-v060))
2. B.4 — WatchSyncVerifier via engine (`mobile/watch/WatchSyncVerifier.kt`, sync-critical)

## Security

- Dexcom credentials: `EncryptedSharedPreferences` only — never log tokens
- See [docs/legal/privacy-policy.md](../../docs/legal/privacy-policy.md)
