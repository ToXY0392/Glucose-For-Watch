---
name: glucose-for-watch-session-start-deferred-maintenance
description: Runs a full maintenance cycle shortly after opening the Cursor project for Glucose For Watch, with aggregated results and action prioritization.
disable-model-invocation: true
---

# Glucose For Watch Session Start Deferred Maintenance

## Objective
Run heavy checks after session startup.

## Full maintenance scope
- `glucose-for-watch-compat-matrix-maintainer`
- `glucose-for-watch-dependency-advisor`
- `glucose-for-watch-doc-drift-checker`
- `glucose-for-watch-release-notes-curator`

## Workflow
1. Wait a short delay after startup (2-5 min).
2. Check lock/cooldown via `glucose-for-watch-run-coordinator`.
3. Execute the full pack.
4. Produce a consolidated bulletin:
   - blockers
   - priority actions
   - planned actions

## Rules
- Do not run if a full run is already in progress.
- Group outputs to avoid multiple notifications.
