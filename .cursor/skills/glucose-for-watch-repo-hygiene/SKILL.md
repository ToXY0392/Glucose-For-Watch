---
name: glucose-for-watch-repo-hygiene
description: Audits Glucose For Watch git hygiene — gitignore, cached runtime artefacts, secrets scan, LF consistency. Use before commits, Phase 0 cleanup, or when the user asks to clean the repository.
disable-model-invocation: true
---

# Widget G7 Repo Hygiene

## Never commit

- `local.properties`, keystores (`*.jks`, `*.keystore`)
- Dexcom credentials, real glucose values in logs/captures
- `.cursor/state/` runtime files (run_state.json, usb-monitor.pid, reports)
- `*.log`, `analytics.settings`, `.tmp-*.jar`, `.android-user-home/`

## gitignore baseline

Ensure [.gitignore](../../../.gitignore) includes:

```gitignore
.cursor/state/
!.cursor/state/.gitkeep
*.log
analytics.settings
.tmp-*.jar
.android-user-home/
```

## Purge from git index (keep local files)

```powershell
git rm -r --cached .cursor/state/
git rm --cached analytics.settings build-last.log .tmp-*.jar 2>$null
```

## Pre-commit scan

```powershell
git diff --cached --name-only
```

Reject if staged paths match: `local.properties`, `*.jks`, `.cursor/state/`, credentials patterns.

## LF

Project uses LF per CONTRIBUTING.md. Do not mass-reformat unrelated files.

## Output

List findings as **Critical** (must fix before commit) or **Cleanup** (git rm --cached).
