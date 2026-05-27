---
name: glucose-for-watch-sandbox-guard
description: >-
  Routes Cursor agent work to the correct sandbox scope skill based on git branch.
  Use at session start on sandbox/* branches, before editing files, or when the user asks which sandbox to use.
disable-model-invocation: true
---

# Glucose For Watch Sandbox Guard

## Goal

Enforce path scope for long-lived `sandbox/*` branches.

## Step 1 — Detect branch

```bash
git branch --show-current
```

## Step 2 — Load scope skill

| Branch | Skill |
|--------|-------|
| `sandbox/mobile-app` | `glucose-for-watch-mobile-app-scope` |
| `sandbox/wear-app` | `glucose-for-watch-wear-app-scope` |
| `sandbox/ui-ux-kit` | `glucose-for-watch-ui-ux-kit-scope` |
| `sandbox/sync-platform` | `glucose-for-watch-sync-platform-scope` |
| `sandbox/documentation` | `glucose-for-watch-documentation-scope` |
| `sandbox/qa-hardware` | `glucose-for-watch-qa-hardware-scope` |
| `develop/integration`, `main`, `feat/*`, `fix/*`, … | No scope lock |

Scope files: [.cursor/workspace-scopes/](../../workspace-scopes/)

## Step 3 — Refuse out-of-scope edits

Stop · name correct sandbox · do not edit forbidden paths.

## Step 4 — Pre-commit

**`glucose-for-watch-repo-hygiene`**

## Step 5 — PR

Target **`develop/integration`** · **`glucose-for-watch-pr-gatekeeper`** · CI green.

## Rebase (weekly)

```bash
git fetch origin
git rebase origin/develop/integration
```

Hub: [docs/plan/WORKSPACE.md](../../../docs/plan/WORKSPACE.md)
