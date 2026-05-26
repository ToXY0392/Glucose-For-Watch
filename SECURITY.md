# Security Policy

## Supported versions

Glucose For Watch is distributed via sideload (debug APK). Security fixes apply to the latest commit on `integrate` and release tags (`v0.5.0`, etc.).

## Reporting a vulnerability

**Do not** open public issues for security-sensitive reports.

Prefer [GitHub Security Advisories](https://github.com/ToXY0392/Glucose-For-Watch/security/advisories/new) (private) or contact the maintainer directly.

## What to include

- App version / commit SHA
- Phone and Wear OS versions (no serial numbers required)
- Steps to reproduce
- Impact assessment

## What NOT to include

- Dexcom Share credentials or session tokens
- Real glucose values or patient-identifiable health data
- Full unredacted logcat (trim to relevant stack traces)
- Keystore files or `local.properties`

## Scope

In scope: mobile/wear APK, sync pipeline, sideload scripts, GitHub Actions CI.

Out of scope: Dexcom upstream services, Wear OS platform, third-party devices not listed in [docs/dev/setup.md](docs/dev/setup.md).
