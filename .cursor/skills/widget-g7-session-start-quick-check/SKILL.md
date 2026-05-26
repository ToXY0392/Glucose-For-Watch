---
name: widget-g7-session-start-quick-check
description: Runs a lightweight check at Cursor session start for Widget G7, in delta mode, to quickly surface critical vendor/security updates.
disable-model-invocation: true
---

# Widget G7 Session Start Quick Check

## Objective
Run a quick check when opening the project without slowing the session.

## Scope
- `widget-g7-vendor-watch` (delta)
- `widget-g7-security-bulletin` (delta)

## Workflow
1. Read last run state.
2. Check cooldown (e.g. 6h).
3. Run quick checks only if needed.
4. Return a mini digest:
   - new critical items
   - new important items
   - no changes

## Rules
- Stay non-blocking at startup.
- Limit output to essentials.
