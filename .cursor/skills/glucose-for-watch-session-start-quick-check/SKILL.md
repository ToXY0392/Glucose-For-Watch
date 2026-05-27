---
name: glucose-for-watch-session-start-quick-check
description: Runs a lightweight check at Cursor session start for Glucose For Watch, in delta mode, to quickly surface critical vendor/security updates.
disable-model-invocation: true
---

# Glucose For Watch Session Start Quick Check

## Objective
Run a quick check when opening the project without slowing the session.

## Scope
- `glucose-for-watch-vendor-watch` (delta)
- `glucose-for-watch-security-bulletin` (delta)

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
