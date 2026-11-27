# Contributing to Glucose For Watch

Thank you for contributing. This project syncs real health data — extra care is required.

## Before you start

1. Read [dev/setup.md](docs/dev/setup.md)
2. Read [dev/architecture.md](docs/dev/architecture.md)
3. Read [AGP medical layer](docs/design/agp-medical-layer.md) — **never use brand colors on glucose values**

## Development setup

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
```

Build, install, and QA scripts: [dev/setup.md](docs/dev/setup.md).  
Dual IDE (WSL + Studio): [dev/setup.md#dual-ide-cursor--android-studio](docs/dev/setup.md#dual-ide-cursor--android-studio).

## Workspace sandboxes

Long-lived branches for parallel work — see **[docs/plan/WORKSPACE.md](docs/plan/WORKSPACE.md)**.

| Branch | Use for |
|--------|---------|
| `sandbox/qa-hardware` | Hardware QA, `docs/qa/`, `scripts/qa/` |
| `sandbox/mobile-app` | `mobile/` module only |
| `sandbox/wear-app` | `wear/` module only |

1. Checkout the sandbox branch
2. Use `@glucose-for-watch-sandbox-guard` at session start
3. Rebase weekly: `git fetch && git rebase origin/develop/integration`
4. Open PR to **`develop/integration`** (never direct to `main`)
5. CI must pass · copy [PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md)

Short-lived `{feat|fix|docs}/bloc-*` branches remain the default for single-bloc PRs from `develop/integration`.

### Pre-commit secrets hook (optional)

```bash
git config core.hooksPath .githooks
```

Rejects staged `local.properties`, keystores, and `.cursor/state/` runtime files.

## Pull request guidelines

Copy the full checklist into every PR: **[docs/plan/PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md)**.

For documented PRs (plan bloc, gates, QA evidence), invoke **`@glucose-for-watch-pr-author`** in Cursor before opening. Before merge, use **`@glucose-for-watch-pr-gatekeeper`** when CI or review comments block the PR.

### Scope

- One logical change per PR when possible
- Minimize unrelated formatting or refactors

### Required checks

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
```

Stability gate and hardware smoke (when touching sync): see [dev/setup.md](docs/dev/setup.md#qa-and-stability-gates).

### Sync-related changes

If you modify anything under `mobile/.../sync/`, `wear/.../tile/`, `feature/sync/`, or Data Layer contract, complete the **Hardware** and **Gate bloc** sections in [PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md). For significant sync changes, run a **30 min regression** on hardware.

### Design changes

- Glucose values → AGP colors only, through `GlucoseRangeResolver` ([spec](docs/design/agp-medical-layer.md)); `scripts/dev/lint_agp_colors.py` checks it
- UI chrome → `wg7_*` / `gfw_*` resources and the Compose color schemes
- Wear tile → ≥ 48 dp touch targets ([surfaces](docs/design/surfaces.md))

### Documentation

- Update relevant docs when behavior changes

### Code style

- Kotlin, JVM 17 target
- **English** comments and KDoc for new/changed public APIs
- Match existing naming and module boundaries
- LF line endings

## Security

- **Never** commit Dexcom credentials, real glucose data, keystores, or `local.properties`
- Redact health data from logs and screenshots in issues/PRs

## Commit messages

Use clear, imperative sentences:

```
Fix tile sync button launch action

Wire iconEdgeButton to GlucoseRefreshActivity per wear tile spec.
```

## Questions

Open an issue with:

- Environment (OS, Studio version, watch model)
- Steps to reproduce
- Expected vs actual behavior

**No real glucose values or passwords in issues.**
