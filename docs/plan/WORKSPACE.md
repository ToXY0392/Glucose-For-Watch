# Workspace sandbox branches

Long-lived parallel work lanes (`workspace/*`) for Glucose For Watch. Distinct from short-lived `{feat|fix|docs}/bloc-*` branches.

## Model

| Branch | Status (pre v0.5.0) | Skill |
|--------|---------------------|-------|
| [`workspace/qa-hardware`](WORKSPACE-qa-hardware.md) | **ACTIVE** | `widget-g7-qa-hardware-scope` |
| [`workspace/ui-ux-kit`](WORKSPACE-ui-ux-kit.md) | dormant | `widget-g7-ux-kit-scope` |
| [`workspace/mobile-app`](WORKSPACE-mobile-app.md) | dormant | `widget-g7-mobile-app-scope` |
| [`workspace/wear-app`](WORKSPACE-wear-app.md) | dormant (triggers) | `widget-g7-wear-app-scope` |

**Phase B (create when needed):** `workspace/sync-platform` · `workspace/infrastructure` · `workspace/dexcom-share`

## Workflow

1. Checkout sandbox branch
2. Session start: `@widget-g7-workspace-guard`
3. Work within scope (see `.cursor/workspace-scopes/`)
4. Weekly: `git fetch && git rebase origin/integrate`
5. PR to `integrate` · CI green · `@widget-g7-pr-gatekeeper`

## Solo dev rule (pre-tag)

**One active sandbox:** `workspace/qa-hardware` only until v0.5.0 tag.

## Security

- Pre-commit: `.githooks/pre-commit` (optional: `git config core.hooksPath .githooks`)
- Never commit credentials, real glucose, unredacted logcat
- GitHub: enable Secret scanning + Push protection (Settings → Code security)

See [GITHUB-SETUP.md](GITHUB-SETUP.md) §3.1 · [AGENTS.md](../../AGENTS.md)
