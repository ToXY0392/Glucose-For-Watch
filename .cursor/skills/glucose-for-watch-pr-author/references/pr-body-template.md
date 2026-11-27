# PR body template — Glucose For Watch

Loaded by [glucose-for-watch-pr-author](../SKILL.md) step 6. Copy the gate section from [PR-CHECKLIST](../../../../docs/plan/PR-CHECKLIST.md).


```markdown
## Summary

[2–4 sentences: problem, solution, user impact. Link: Closes #N]

## Plan metadata

| Field | Value |
|-------|-------|
| **Bloc** | [X / A / M / B / C / D / F0–F5 / S] |
| **Target gate** | [G-X / … / G-M8] |
| **Touch sync?** | [yes / no] |
| **Branch** | `[type]/bloc-[id]-[slug]` → `develop/integration` |
| **Issue** | Closes #N |

## What changed

- [Bullet per logical unit — file/module level]
- [No wall of filenames; group by concern]

## Documentation

- [ ] [list updated doc paths, or "None — reason"]
- [ ] PROGRESS.md update planned post-merge: [yes/no + what rows]

## Test plan

| Check | Result | Notes |
|-------|--------|-------|
| `verify_ci.sh` | PASS / FAIL | |
| `./gradlew test` | PASS / FAIL | modules: … |
| `stability-gate.ps1` | PASS / FAIL / N/A | soak: logcat-only if C.7 |
| Manual / hardware | N/A / done | push/ack seq: _ / _ |

## AGP / ToXY (if UI)

- [ ] Glucose values → AGP only (`agp_*`)
- [ ] ToXY mint → chrome only
- [ ] `python3 scripts/dev/lint_agp_colors.py` passes
- [ ] Preview PNG: `docs/qa/captures/` or export-app-preview

## Gate [G-XX] criteria

[Paste relevant rows from STABILITY-GATES — mark each with evidence]

## Evidence

- [Links to docs/qa/…, soak log, screenshot path — no real glucose]
- [Logcat excerpt if crash fix — redact PII]

## Rollback

[1–2 sentences: revert commit + retest gate if sync touched]

## After merge (author todo)

- [ ] PROGRESS.md scoreboard
- [ ] GitHub Project → Done
- [ ] Close incident issue if applicable
```

