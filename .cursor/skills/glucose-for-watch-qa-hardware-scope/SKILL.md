---
name: glucose-for-watch-qa-hardware-scope
description: >-
  Scope-limited hardware QA and evidence on sandbox/qa-hardware — docs/qa
  and scripts/qa only. Use for soak sessions, C.* matrix, captures, and
  stability sign-off. Refuses direct app code edits; routes fixes to owning
  sandbox.
disable-model-invocation: true
---

# Glucose For Watch QA Hardware Scope

## Branch

Expected: **`sandbox/qa-hardware`** (sole active sandbox pre v0.5.0 tag).

Scope reference: [.cursor/workspace-scopes/qa-hardware.scope.md](../../workspace-scopes/qa-hardware.scope.md)

## Allowed

- `docs/qa/**` — sessions, soak-runs, captures, sign-off
- `scripts/qa/**` — smoke, soak, capture-crash-log (D.6)

## Forbidden (hard block)

- Direct edits to `mobile/`, `wear/`, `feature/`, `core/`
- If a session fails: document failure · open fix on `wear-app`, `mobile-app`, or `feat/bloc-*` · re-run session

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
