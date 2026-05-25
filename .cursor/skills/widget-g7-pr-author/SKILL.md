---
name: widget-g7-pr-author
description: Drafts complete, documented pull requests for Glucose For Watch — branch naming, issue linking, PR body from diff, doc updates, test plan, and evidence paths. Use when opening a PR, preparing a merge request, or when the user asks for a full documented PR description.
disable-model-invocation: true
---

# Widget G7 PR Author

## Role

Prepare **complete PR packages** before review. Pair with `widget-g7-pr-gatekeeper` (pre-merge validation).

| Skill | When |
|-------|------|
| **pr-author** (this) | Draft branch, body, docs, evidence |
| **pr-gatekeeper** | Verify gates + CI before merge |
| **split-to-prs** (built-in) | Branch too large or mixed blocs |

## Hard constraints

- **Never** run `installWidgetG7Debug` / `adb install` during active soak (C.7) unless user explicitly asks.
- One PR = one measurable bloc objective ([ACTION-PLAN P2](../../../docs/plan/ACTION-PLAN.md)).
- No credentials, real glucose, or `local.properties` in commits/captures.

## References

- [docs/plan/PR-CHECKLIST.md](../../../docs/plan/PR-CHECKLIST.md)
- [.github/pull_request_template.md](../../../.github/pull_request_template.md)
- [docs/plan/STABILITY-GATES.md](../../../docs/plan/STABILITY-GATES.md)
- [CONTRIBUTING.md](../../../CONTRIBUTING.md)

---

## Workflow

### 1. Scope check

```bash
git diff integrate...HEAD --stat
git log integrate..HEAD --oneline
```

Confirm:
- Single bloc (X, A, M, B, C, D, F*, S)
- Branch name: `{feat|fix|docs|test|chore|qa}/bloc-{id}-{slug}`
- Linked issue exists (`Closes #N` or `Refs #N`)

If mixed blocs → stop and suggest `split-to-prs`.

### 2. Classify touch surfaces

| Path pattern | Flags |
|--------------|-------|
| `mobile/**/sync/`, `feature/sync/`, `wear/**/tile/` | **sync-critical** · hardware QA |
| `mobile/**/ui/`, `wear/**/ui/`, `toxy-ux-kit/` | AGP/ToXY checklist |
| `docs/` only | docs-only · no hardware |
| `scripts/qa/` | may need smoke notes |
| `core/datalayer-contract/` | sync-critical + contract doc |

Set **Touch sync?** oui/non in PR metadata.

### 3. Doc update matrix

Update docs in the **same PR** when behavior changes:

| Change | Update |
|--------|--------|
| Sync flow, FGS, schedulers | [docs/dev/architecture.md](../../../docs/dev/architecture.md) |
| Setup, scripts, QA gates | [docs/dev/setup.md](../../../docs/dev/setup.md) |
| User-facing behavior | [docs/guide/user.md](../../../docs/guide/user.md) |
| Dexcom auth/regions | [docs/guide/dexcom.md](../../../docs/guide/dexcom.md) |
| ToXY tokens / AGP colors | [toxy-ux-kit/](../../../toxy-ux-kit/README.md) + specs |
| Gate/bloc completion | [docs/plan/PROGRESS.md](../../../docs/plan/PROGRESS.md) (post-merge note in PR) |
| Hardware session | New file in `docs/qa/soak-runs/` or `docs/qa/sessions/` |
| Incident fix | Update `docs/qa/incidents/` + close issue |

If no doc update needed, state **why** in PR body (e.g. internal refactor, no user-visible change).

### 4. Run checks (record results in PR)

```bash
bash scripts/dev/verify_ci.sh
./gradlew test
```

```powershell
.\scripts\qa\stability-gate.ps1          # or -CheckLogcatOnly if soak active
.\scripts\qa\export-app-preview.ps1      # if mobile UI / HomeUiState touched
```

Paste PASS/FAIL summary into Test plan section.

### 5. Draft PR title

Format: `{type}({bloc}): {imperative summary}`

Examples:
- `fix(bloc-x): catch FGS start failure in ActiveGlucoseSyncService`
- `feat(bloc-a): add notification permission flow on first launch`
- `docs(bloc-s): add automation backlog for preview gallery`

### 6. Fill PR body

Use template below. Copy [PR-CHECKLIST](../../../docs/plan/PR-CHECKLIST.md) gate section for target gate.

### 7. Post-open checklist

- [ ] Move GitHub Project card → **In Review**
- [ ] Labels: `bloc-*`, `sync-critical` / `hardware-qa` if applicable
- [ ] CI green on `integrate` target
- [ ] Request self-review with `widget-g7-pr-gatekeeper` before merge

---

## PR body template

```markdown
## Summary

[2–4 sentences: problem, solution, user impact. Link: Closes #N]

## Plan metadata

| Field | Value |
|-------|-------|
| **Bloc** | [X / A / M / B / C / D / F0–F5 / S] |
| **Gate cible** | [G-X / … / G-M8] |
| **Touch sync?** | [oui / non] |
| **Branch** | `[type]/bloc-[id]-[slug]` → `integrate` |
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
- [ ] Tokens updated in `toxy-ux-kit/` if colors changed
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

---

## Bloc-specific hints

| Bloc | PR must include |
|------|-----------------|
| **X** | FGS fallback explanation · X.7 test ref · no FATAL in logcat |
| **A** | Notif permission flow · disconnect parity entry/settings |
| **M** | HomeUiState fields · preview PNG paths (6 states) |
| **B** | Complication vs tile timing · wear `strings.xml` FR |
| **C** | Link `docs/qa/soak-runs/` or sign-off template |
| **D** | Test count delta · script usage example |
| **F*** | Compose phase · sync non-regression note |
| **S** | No feature creep · infra/docs scope clear |

---

## Commit hygiene (before PR)

- Imperative subject, ≤72 chars
- Body explains **why**, not just what
- No `fixup` commits in PR — squash or rebase if messy
- LF line endings

---

## Output to user

Deliver:
1. Suggested **PR title**
2. Complete **PR body** (markdown ready to paste)
3. List of **doc files** to commit if not yet updated
4. **Missing items** before opening PR
5. Reminder to run `widget-g7-pr-gatekeeper` before merge
