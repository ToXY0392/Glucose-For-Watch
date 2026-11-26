---
name: glucose-for-watch-sandbox-guard
description: Routes agent work in Glucose For Watch to the right scope skill from the current branch and the paths being edited, and refuses out-of-scope edits. Use when starting work on a branch, before editing files, or when the user asks which sandbox or scope applies.
---

# Glucose For Watch Sandbox Guard

## Goal

Keep every change inside one scope. Only two sandboxes are long-lived; the other scopes apply by **path** on short-lived branches.

## Step 1 — Branch

```bash
git branch --show-current
```

| Branch | Rule |
|--------|------|
| `sandbox/mobile-app` | Locked to `glucose-for-watch-mobile-app-scope` |
| `sandbox/documentation` | Locked to `glucose-for-watch-documentation-scope` |
| any other `sandbox/*` | Dormant — stop and ask; do not recreate without an explicit decision |
| `feat/`, `fix/`, `docs/`, `test/`, `chore/`, `qa/` (`{type}/bloc-{id}-{slug}`) | No branch lock — go to Step 2 |
| `develop/integration`, `main` | Do not commit directly — create a short-lived branch |

## Step 2 — Paths (short-lived branches)

```bash
git diff --name-only origin/develop/integration...HEAD
git status --short
```

| Paths touched | Scope skill |
|---------------|-------------|
| `mobile/**` | `glucose-for-watch-mobile-app-scope` |
| `wear/**` | `glucose-for-watch-wear-app-scope` |
| `feature/sync/**`, `core/datalayer-contract/**`, `core/model/**` | `glucose-for-watch-sync-platform-scope` |
| `toxy-ux-kit/**`, `docs/design/**` | `glucose-for-watch-ui-ux-kit-scope` |
| `docs/qa/**`, `scripts/qa/**` | `glucose-for-watch-qa-hardware-scope` |
| `docs/**` (other), `AGENTS.md`, `.claude/**`, `.cursor/**` | `glucose-for-watch-documentation-scope` |

Several scopes in one branch → the change is probably two PRs: suggest `split-to-prs`. Infra-only paths (`.github/**`, root Gradle, `scripts/dev/**`) need a dedicated `chore/` branch and the user's go-ahead.

Scope files: [.cursor/workspace-scopes/](../../../.cursor/workspace-scopes/)

## Step 3 — Refuse out-of-scope edits

Stop · name the right scope or branch · do not edit forbidden paths.

## Step 4 — Before commit

`glucose-for-watch-repo-hygiene`. If a skill changed: `python3 scripts/dev/sync_skills.py` then `--lint`.

## Step 5 — PR

Target **`develop/integration`** · `glucose-for-watch-pr-author` to draft · `glucose-for-watch-pr-gatekeeper` before merge · CI green.

## Rebase long-lived sandboxes (weekly)

```bash
git fetch origin
git rebase origin/develop/integration
```

Hub: [docs/plan/WORKSPACE.md](../../../docs/plan/WORKSPACE.md)
