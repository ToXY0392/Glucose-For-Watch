# Workspace — mobile-app

| Field | Value |
|-------|-------|
| **Branch** | `workspace/mobile-app` |
| **Status** | dormant (until v0.5.0 tag) |
| **Skill** | `widget-g7-mobile-app-scope` |
| **Scope file** | [.cursor/workspace-scopes/mobile-app.scope.md](../../.cursor/workspace-scopes/mobile-app.scope.md) |

## Allowed paths

- `mobile/**`

Read-only: `core/**`, `feature/sync/**`, `feature/dexcom-share/**`, `feature/watch-install/**`

## Backlog

### Post-tag v0.5.0

| # | ID | Task | Est. | Notes |
|---|-----|------|------|-------|
| 1 | B.4 | WatchSyncVerifier → engine | 4h | sync-critical · `mobile/watch/` |
| 2 | F0 | Compose foundations | 2–3d | **Forbidden before v0.5.0 tag** |
| 3 | F1–F3 | Legal, Dexcom, Home Compose | 3–4 weeks | After F0 |
| 4 | AUTO-3 | Showkase | 1d | v0.6 |

B.4 not required for G-B gate (complication, FR tile, smoke already ✅).

## Cross-boundary

If B.4 touches `feature/sync/**` → `feat/bloc-b-watch-sync-verifier` or `workspace/sync-platform` (Phase B).

## Verify

```bash
./gradlew :mobile:assembleDebug :mobile:test
```

## Rebase (weekly while dormant)

```bash
git fetch origin && git rebase origin/integrate
```
