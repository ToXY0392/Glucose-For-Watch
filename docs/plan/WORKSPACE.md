# Sandbox branches — Glucose For Watch

Long-lived parallel lanes (`sandbox/*`). Short-lived work: `{feat|fix|chore|docs}/bloc-*` → PR **`develop/integration`**. Releases: **`develop/integration`** → **`main`**.

## Model

| Branch | Role | Skill |
|--------|------|-------|
| [`sandbox/mobile-app`](WORKSPACE-mobile-app.md) | Phone app (`mobile/`) | `glucose-for-watch-mobile-app-scope` |
| [`sandbox/wear-app`](WORKSPACE-wear-app.md) | Wear tile, complication | `glucose-for-watch-wear-app-scope` |
| [`sandbox/ui-ux-kit`](WORKSPACE-ui-ux-kit.md) | ToXY kit, tokens | `glucose-for-watch-ui-ux-kit-scope` |
| [`sandbox/sync-platform`](WORKSPACE-sync-platform.md) | `feature/sync`, `core/model` | `glucose-for-watch-sync-platform-scope` |
| [`sandbox/documentation`](WORKSPACE-documentation.md) | Plan, guides, agent skills | `glucose-for-watch-documentation-scope` |
| [`sandbox/qa-hardware`](WORKSPACE-qa-hardware.md) | QA evidence, `scripts/qa` | `glucose-for-watch-qa-hardware-scope` |

| Branch | Role |
|--------|------|
| **`develop/integration`** | Daily integration · CI |
| **`main`** | Tagged releases |

## Workflow

1. Checkout sandbox branch
2. `@glucose-for-watch-sandbox-guard`
3. Work within scope (`.cursor/workspace-scopes/`)
4. Weekly: `git fetch && git rebase origin/develop/integration`
5. PR → `develop/integration` · CI · `@glucose-for-watch-pr-gatekeeper`

## GitHub Project

Single board: [Project #1](https://github.com/users/ToXY0392/projects/1) · ritual: `@glucose-for-watch-github-project-sync`

See [GITHUB-SETUP.md](GITHUB-SETUP.md) · [AGENTS.md](../../AGENTS.md)
