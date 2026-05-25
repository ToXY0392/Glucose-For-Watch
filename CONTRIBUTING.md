# Contributing to Glucose For Watch

Thank you for contributing. This project syncs real health data — extra care is required.

## Before you start

1. Read [dev.md](docs/dev.md)
2. Read [architecture.md](docs/architecture.md)
3. Read [AGP medical layer](toxy-ux-kit/spec/01-agp-medical-layer.md) — **never use brand colors on glucose values**

## Development setup

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
```

Dual IDE: [dev.md](docs/dev.md#dual-ide-cursor--android-studio)

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
