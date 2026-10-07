# Branch model — Glucose For Watch

Keep the permanent branch set small. Short-lived work uses `{feat|fix|chore|docs|test|qa}/bloc-*` and targets **`develop/integration`**. Releases flow from **`develop/integration`** to **`main`**.

## Model

| Branch | Role |
|--------|------|
| **`main`** | Tagged releases; protected |
| **`develop/integration`** | Daily integration and CI; protected |
| [`sandbox/mobile-app`](WORKSPACE-mobile-app.md) | Long-lived phone-app lane (`mobile/`) |
| [`sandbox/documentation`](WORKSPACE-documentation.md) | Long-lived documentation lane |

Wear, sync, UI/UX, and QA scope guides remain available, but do not have permanent branches. Use a short-lived branch and the relevant scope guide/skill when that work is scheduled.

## Workflow

1. Start from an up-to-date `develop/integration`; use `sandbox/mobile-app` or `sandbox/documentation` only for continuing work in those lanes.
2. Use a short-lived `{feat|fix|chore|docs|test|qa}/bloc-*` branch for other work and follow its scope guide.
3. Run `@glucose-for-watch-sandbox-guard` when working in a sandbox and respect `.cursor/workspace-scopes/`.
4. Open a PR to `develop/integration`; pass CI and `@glucose-for-watch-pr-gatekeeper`.
5. Delete short-lived branches after merge. Do not delete or force-push protected branches.

## GitHub Project

Single board: [Project #1](https://github.com/users/ToXY0392/projects/1) · ritual: `@glucose-for-watch-github-project-sync`

See [GITHUB-SETUP.md](GITHUB-SETUP.md) · [AGENTS.md](../../AGENTS.md)
