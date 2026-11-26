---
name: glucose-for-watch-qa-hardware-scope
description: Path scope for hardware QA evidence in Glucose For Watch: docs/qa and scripts/qa only, no app code edits, fixes routed to the owning area. Use when recording soak sessions, the C.* matrix, captures or stability sign-off, on a qa/ branch or any branch touching docs/qa or scripts/qa.
---

# Glucose For Watch QA Hardware Scope

## Activation

Path-based: applies on any short-lived branch (`feat/`, `fix/`, `qa/`, `test/`, …) whose changes touch the allowed paths below. `sandbox/qa-hardware` is dormant — do not recreate it without an explicit decision ([WORKSPACE.md](../../../docs/plan/WORKSPACE.md)). PR target: **`develop/integration`**.

Scope reference: [.cursor/workspace-scopes/qa-hardware.scope.md](../../../.cursor/workspace-scopes/qa-hardware.scope.md)

## Allowed

- `docs/qa/**` — sessions, soak-runs, captures, sign-off
- `scripts/qa/**` — smoke, soak, capture-crash-log (D.6)

## Forbidden (hard block)

- Direct edits to `mobile/`, `wear/`, `feature/`, `core/`
- If a session fails: document failure · open the fix on `sandbox/mobile-app` or a `fix/bloc-*` branch · re-run session

## PR strategy

One batch PR `docs/qa/bloc-c-evidence` → `develop/integration` after all C.* sessions complete.

## Verify

```powershell
.\scripts\qa\stability-gate.ps1 -Strict
.\scripts\qa\hardware-smoke.ps1
```

During C.7 soak: `stability-gate.ps1 -CheckLogcatOnly` only.

## Security (strict)

- **Never** commit Dexcom credentials, real glucose values, unredacted logcat
- Redact serial numbers and PII in session notes
- See [PR-CHECKLIST.md](../../../docs/plan/PR-CHECKLIST.md) capture rules

## Before PR

1. `glucose-for-watch-repo-hygiene`
2. `glucose-for-watch-pr-gatekeeper` when opening PR to develop/integration

## Output

Session file path · KPI checklist · redaction confirmation
