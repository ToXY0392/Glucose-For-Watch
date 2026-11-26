---
name: glucose-for-watch-sync-platform-scope
description: Path scope for sync-critical work in Glucose For Watch: feature/sync, the datalayer contract and core model. Use when a change on any branch touches feature/sync, core/datalayer-contract or core/model.
---

# Glucose For Watch — sync platform scope

## Activation

Path-based: applies on any short-lived branch (`feat/`, `fix/`, `qa/`, `test/`, …) whose changes touch the allowed paths below. `sandbox/sync-platform` is dormant — do not recreate it without an explicit decision ([WORKSPACE.md](../../../docs/plan/WORKSPACE.md)). PR target: **`develop/integration`**.

Read [.cursor/workspace-scopes/sync-platform.scope.md](../../../.cursor/workspace-scopes/sync-platform.scope.md).

Before commit: **`glucose-for-watch-repo-hygiene`** · PR: **`glucose-for-watch-pr-gatekeeper`**.

Sync changes may require hardware retest — label `sync-critical` on linked issue.
