# Scope — workspace/qa-hardware

| Field | Value |
|-------|-------|
| **Branch** | `workspace/qa-hardware` |
| **Status** | **ACTIVE** (sole sandbox pre v0.5.0 tag) |
| **Skill** | `widget-g7-qa-hardware-scope` |

## Allowed paths

- `docs/qa/**`
- `scripts/qa/**`

## Read-only (run, do not modify app code here)

- `mobile/**`, `wear/**`, `feature/**` — fixes go to the owning sandbox or `feat/bloc-*` on integrate

## Forbidden

- Direct edits to `mobile/`, `wear/`, `feature/`, `core/` (open fix PR on correct sandbox first)
- Credentials, real glucose, unredacted logcat in commits

## Verify

```powershell
.\scripts\qa\stability-gate.ps1 -Strict
.\scripts\qa\hardware-smoke.ps1
```

## Calendar (pre-tag)

| Day | Session |
|-----|---------|
| J0 | DOC-P0-1 C.7 sign-off + integrate chores DOC-P0-2/3 |
| J1 | C.1 AGP 60/120/200 |
| J2 | C.2 complication vs tile · C.6 reinstall |
| J3 | **C.3 offline 2h** (dedicated day — no other dev) |
| J4 | C.4 LOW/HI · C.8 battery ≤20% |
| J5 | D.6 · **one PR** `docs/qa/bloc-c-evidence` → integrate |
| J6 | Tag v0.5.0 on integrate |

## Security

- Redact PII/PHI in `docs/qa/sessions/` and `docs/qa/soak-runs/`
- No Dexcom credentials in captures or notes
