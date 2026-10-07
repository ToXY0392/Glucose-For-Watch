# Workspace — mobile-app

| Field | Value |
|-------|-------|
| **Branch** | `sandbox/mobile-app` |
| **Status** | long-lived lane; activate for scoped mobile work |
| **Skill** | `glucose-for-watch-mobile-app-scope` |
| **Scope file** | [.cursor/workspace-scopes/mobile-app.scope.md](../../.cursor/workspace-scopes/mobile-app.scope.md) |

## Allowed paths

- `mobile/**`

Read-only: `core/**`, `feature/sync/**`, `feature/dexcom-share/**`, `feature/watch-install/**`

## Backlog

### Backlog

| # | ID | Task | Est. | Notes |
|---|-----|------|------|-------|
| 1 | B.4 | WatchSyncVerifier → engine | 4h | sync-critical · `mobile/watch/` |
| 2 | F0 | Compose foundations | 2–3d | |
| 3 | F1–F3 | Legal, Dexcom, Home Compose | 3–4 weeks | After F0 |
| 4 | AUTO-3 | Showkase | 1d | v0.6 |

B.4 not required for G-B gate (complication, FR tile, smoke already ✅).

## Cross-boundary

If B.4 touches `feature/sync/**`, use a short-lived `feat/bloc-*` branch with the sync-platform scope and skill.

## Verify

```bash
./gradlew :mobile:assembleDebug :mobile:test
```

## Rebase (weekly while active)

```bash
git fetch origin && git rebase origin/develop/integration
```
