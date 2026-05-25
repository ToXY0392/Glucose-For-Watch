# Contributing to Glucose For Watch

Thank you for contributing. This project syncs real health data — extra care is required.

## Before you start

1. Read [dev/setup.md](docs/dev/setup.md)
2. Read [dev/architecture.md](docs/dev/architecture.md)
3. Read [AGP medical layer](toxy-ux-kit/spec/01-agp-medical-layer.md) — **never use brand colors on glucose values**

## Development setup

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
```

Build, install, and QA scripts: [dev/setup.md](docs/dev/setup.md).  
Dual IDE (WSL + Studio): [dev/setup.md#dual-ide-cursor--android-studio](docs/dev/setup.md#dual-ide-cursor--android-studio).

## Pull request guidelines

Copy the full checklist into every PR: **[docs/plan/PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md)**.

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

- Glucose values → AGP colors only ([spec](toxy-ux-kit/spec/01-agp-medical-layer.md))
- UI chrome → UX kit tokens ([spec](toxy-ux-kit/spec/02-toxy-chrome-layer.md))
- Wear tile → ≥ 48 dp touch targets ([tile spec](toxy-ux-kit/spec/components/wear-tile.md))

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
