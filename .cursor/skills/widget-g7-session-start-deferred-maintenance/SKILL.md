---
name: widget-g7-session-start-deferred-maintenance
description: Runs a full maintenance cycle shortly after opening the Cursor project for Widget G7, with aggregated results and action prioritization.
disable-model-invocation: true
---

# Widget G7 Session Start Deferred Maintenance

## Objective
Run heavy checks after session startup.

## Full maintenance scope
- `widget-g7-compat-matrix-maintainer`
- `widget-g7-dependency-advisor`
- `widget-g7-doc-drift-checker`
- `widget-g7-release-notes-curator`

## Workflow
1. Wait a short delay after startup (2-5 min).
2. Check lock/cooldown via `widget-g7-run-coordinator`.
3. Execute the full pack.
4. Produce a consolidated bulletin:
   - blockers
   - priority actions
   - planned actions

## Rules
- Do not run if a full run is already in progress.
- Group outputs to avoid multiple notifications.
