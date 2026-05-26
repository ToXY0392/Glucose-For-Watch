# Workspace — qa-hardware

| Field | Value |
|-------|-------|
| **Branch** | `workspace/qa-hardware` |
| **Status** | **ACTIVE** (sole sandbox pre v0.5.0 tag) |
| **Skill** | `widget-g7-qa-hardware-scope` |
| **Scope file** | [.cursor/workspace-scopes/qa-hardware.scope.md](../../.cursor/workspace-scopes/qa-hardware.scope.md) |

## Allowed paths

- `docs/qa/**`
- `scripts/qa/**`

App fixes → owning sandbox or `feat/bloc-*` on `integrate`, not here.

## Backlog (ordered)

| # | ID | Task | Est. |
|---|-----|------|------|
| 1 | DOC-P0-1 | C.7 sign-off template | 30m |
| 2 | C.1 | AGP 60/120/200 phone + tile | 2h |
| 3 | C.2 | Complication vs tile | 30m |
| 4 | C.6 | APK reinstall + tile | 1h |
| 5 | **C.3** | Watch offline 2h (**dedicated day**) | 2–3h |
| 6 | C.4 | LOW / HI display | — |
| 7 | C.8 | Watch battery ≤20% | 1h |
| 8 | DOC-P0-4 | Evidence sessions C.2, C.3, C.8 | — |
| 9 | D.6 | capture-crash-log doc/script | 1h |
| 10 | — | **One PR** `docs/qa/bloc-c-evidence` → integrate | — |

## Calendar

| Day | Work |
|-----|------|
| J0 | Sign-off + integrate chores (DOC-P0-2/3) |
| J1 | C.1 |
| J2 | C.2 + C.6 |
| J3 | C.3 only |
| J4 | C.4 + C.8 |
| J5 | D.6 + batch PR |
| J6 | Tag v0.5.0 on integrate |

## Verify

```powershell
.\scripts\qa\stability-gate.ps1 -Strict
.\scripts\qa\hardware-smoke.ps1
```

## Rebase

```bash
git fetch origin && git rebase origin/integrate
```

Weekly while sandbox is active.
