---
name: widget-g7-workspace-guard
description: >-
  Routes Cursor agent work to the correct workspace sandbox scope skill based
  on git branch. Use at session start on workspace/* branches, before editing
  files, or when the user asks which sandbox to use.
disable-model-invocation: true
---

# Widget G7 Workspace Guard

## Goal

Enforce path scope for long-lived `workspace/*` sandbox branches.

## Step 1 — Detect branch

```bash
git branch --show-current
```

## Step 2 — Load scope skill

| Branch | Skill |
|--------|-------|
| `workspace/ui-ux-kit` | `widget-g7-ux-kit-scope` |
| `workspace/mobile-app` | `widget-g7-mobile-app-scope` |
| `workspace/wear-app` | `widget-g7-wear-app-scope` |
| `workspace/qa-hardware` | `widget-g7-qa-hardware-scope` |
| `integrate`, `main`, `feat/*`, `fix/*`, … | No scope lock · use normal skills |

Scope files: [.cursor/workspace-scopes/](../../workspace-scopes/)

## Step 3 — Refuse out-of-scope edits

If the user or task requires paths outside the active scope:

1. Stop
2. Name the correct sandbox or short-lived branch (`feat/bloc-*`)
3. Do not modify forbidden paths

## Step 4 — Pre-commit

Always run **`widget-g7-repo-hygiene`** before staging.

## Step 5 — PR to integrate

Run **`widget-g7-pr-gatekeeper`** · CI must be green on PR.

## Solo dev rules (pre v0.5.0)

- **One active sandbox:** `workspace/qa-hardware` only
- `ui-ux-kit`, `mobile-app`, `wear-app`: dormant — rebase weekly, no code commits until post-tag

## Rebase ritual (weekly)

```bash
git fetch origin
git rebase origin/integrate
```

Hub: [docs/plan/WORKSPACE.md](../../../docs/plan/WORKSPACE.md)
