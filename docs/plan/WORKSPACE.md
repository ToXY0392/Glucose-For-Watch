# Workspace sandbox branches

Long-lived parallel work lanes (`workspace/*`) for Glucose For Watch. Distinct from short-lived `{feat|fix|docs}/bloc-*` branches.

## Model

| Branch | Status (post v0.5.0) | Skill |
|--------|----------------------|-------|
| [`workspace/mobile-app`](WORKSPACE-mobile-app.md) | **ACTIVE** (Compose F0) | `widget-g7-mobile-app-scope` |
| [`workspace/qa-hardware`](WORKSPACE-qa-hardware.md) | on-demand | `widget-g7-qa-hardware-scope` |
| [`workspace/wear-app`](WORKSPACE-wear-app.md) | dormant | `widget-g7-wear-app-scope` |
| [`workspace/ui-ux-kit`](WORKSPACE-ui-ux-kit.md) | dormant | `widget-g7-ux-kit-scope` |

**Phase B (create when needed):** `workspace/sync-platform` · `workspace/infrastructure` · `workspace/dexcom-share`

## Workflow

1. Checkout sandbox branch
2. Session start: `@widget-g7-workspace-guard`
3. Work within scope (see `.cursor/workspace-scopes/`)
4. Weekly: `git fetch && git rebase origin/integrate`
5. PR to `integrate` · CI green · `@widget-g7-pr-gatekeeper`

## Solo dev rule (post-tag v0.5.0)

**Primary sandbox:** `workspace/mobile-app` (Bloc F · Compose v0.6.0). Rebase sandboxes weekly on `integrate`.

## Security

- Pre-commit: `.githooks/pre-commit` (optional: `git config core.hooksPath .githooks`)
- Never commit credentials, real glucose, unredacted logcat
- GitHub: enable Secret scanning + Push protection (Settings → Code security)

See [GITHUB-SETUP.md](GITHUB-SETUP.md) §3.1 · [AGENTS.md](../../AGENTS.md)
