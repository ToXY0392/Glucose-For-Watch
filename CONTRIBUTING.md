# Contributing to Glucose For Watch

Thank you for contributing. This project syncs real health data — extra care is required.

## Before you start

1. Read [Getting started](docs/development/getting-started.md)
2. Read [Sync pipeline](docs/architecture/sync-pipeline.md)
3. Read [AGP glucose color standard](docs/design/glucose-color-standard.md) — **never use brand colors on glucose values**
4. Read [Master refactor plan](docs/plan/MASTER-REFACTOR-PLAN.md) for current priorities

## Development setup

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
```

Dual IDE: [Cursor + Android Studio](docs/development/dual-ide-setup.md)

## Pull request guidelines

### Scope

- One logical change per PR when possible
- Minimize unrelated formatting or refactors

### Required checks

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
```

### Sync-related changes

If you modify anything under `mobile/.../sync/`, `wear/.../tile/`, `feature/sync/`, or Data Layer contract:

**Include in PR description:**

- [ ] Built mobile + wear debug APKs successfully
- [ ] Manual 5 min phone ↔ watch sync test
- [ ] Phone value matches watch value after sync
- [ ] Ack chevron / connection state correct
- [ ] If tile changed: sync button tested on watch

For significant sync changes: **30 min regression** recommended.

### Design changes

- Glucose values → AGP colors only ([spec](docs/design/glucose-color-standard.md))
- UI chrome → UX kit tokens ([spec](docs/design/toxy-design-system.md))
- Wear tile → ≥ 48 dp touch targets ([tile spec](docs/design/wear-tile-spec.md))

### Documentation

- Update relevant docs in English when behavior changes
- Run `widget-g7-doc-drift-checker` skill if available

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
