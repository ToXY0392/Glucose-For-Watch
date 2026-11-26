---
name: glucose-for-watch-pr-gatekeeper
description: Validates Glucose For Watch PR readiness against bloc gates, verify_ci.sh, stability-gate.ps1, and PR-CHECKLIST before merge. Use before merging PRs, when reviewing pull requests, or when the user asks if a gate is ready.
disable-model-invocation: true
---

# Glucose For Watch PR Gatekeeper

## Goal

Block merge until bloc gate criteria and project checklist are satisfied.

## Hard constraints (soak / hardware session active)

- **Never** run `installGlucoseForWatchDebug`, `adb install`, `adb uninstall`, or `./gradlew install*` unless the user explicitly requests install outside an active soak.
- During C.7 soak: use `stability-gate.ps1 -CheckLogcatOnly` for logcat checks only.

## References

- [docs/plan/PR-CHECKLIST.md](../../../docs/plan/PR-CHECKLIST.md)
- [docs/plan/STABILITY-GATES.md](../../../docs/plan/STABILITY-GATES.md)
- [docs/plan/PROGRESS.md](../../../docs/plan/PROGRESS.md)

## Workflow

1. Read PR metadata: **Bloc**, **Gate**, **Touch sync?**, linked issue.
2. Confirm scope is one measurable objective (ACTION-PLAN P2).
3. Run automated checks (repo root):
   ```bash
   bash scripts/dev/verify_ci.sh
   ```
   ```powershell
   .\scripts\qa\stability-gate.ps1
   ```
   Use `-Strict` only when hardware is available and soak is not active.
4. If **touch sync** (`mobile/sync`, `wear/`, `feature/sync`, `ActiveGlucoseSyncService`):
   - Require hardware section in PR description
   - Note push/ack seq before/after
   - Flag follow-up S1–S3 retest issue if merged
5. Match gate criteria from STABILITY-GATES for the target gate (G-X … G-M8).
6. Output verdict:
   - **Go** — all blockers cleared
   - **No-Go** — list missing items with file/command to run

## Output template

```markdown
## PR Gate Review — [gate]

**Verdict:** Go / No-Go

### Automated
- [ ] verify_ci.sh
- [ ] stability-gate.ps1

### Gate [G-X]
- [ ] criterion 1 …

### Blockers
- …
```
