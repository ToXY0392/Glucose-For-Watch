---
name: glucose-for-watch-pr-author
description: Drafts complete, documented pull requests for Glucose For Watch: branch naming, issue linking, PR body from the diff, doc update matrix, test plan and evidence paths. Use when opening a PR, preparing a merge request, or when the user asks for a full documented PR description.
---

# Glucose For Watch PR Author

## Role

Prepare **complete PR packages** before review. Pair with `glucose-for-watch-pr-gatekeeper` (pre-merge validation).

| Skill | When |
|-------|------|
| **pr-author** (this) | Draft branch, body, docs, evidence |
| **pr-gatekeeper** | Verify gates + CI before merge |
| **split-to-prs** (built-in) | Branch too large or mixed blocs |

## Hard constraints

- **Never** run `installGlucoseForWatchDebug` / `adb install` during active soak (C.7) unless user explicitly asks.
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
git diff origin/develop/integration...HEAD --stat
git log origin/develop/integration..HEAD --oneline
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
| `mobile/**/ui/`, `wear/**/ui/`, `wear/**/tile/`, `wear/**/complication/` | AGP checklist |
| `docs/` only | docs-only · no hardware |
| `scripts/qa/` | may need smoke notes |
| `core/datalayer-contract/` | sync-critical + contract doc |

Set **Touch sync?** yes/no in PR metadata.

### 3. Doc update matrix

Update docs in the **same PR** when behavior changes:

| Change | Update |
|--------|--------|
| Sync flow, FGS, schedulers | [docs/dev/architecture.md](../../../docs/dev/architecture.md) |
| Setup, scripts, QA gates | [docs/dev/setup.md](../../../docs/dev/setup.md) |
| User-facing behavior | [docs/guide/user.md](../../../docs/guide/user.md) |
| Dexcom auth/regions | [docs/guide/dexcom.md](../../../docs/guide/dexcom.md) |
| AGP colors / glucose surfaces | [docs/design/agp-medical-layer.md](../../../docs/design/agp-medical-layer.md) + [surfaces.md](../../../docs/design/surfaces.md) |
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

Use [references/pr-body-template.md](references/pr-body-template.md). Copy [PR-CHECKLIST](../../../docs/plan/PR-CHECKLIST.md) gate section for target gate.

### 7. Post-open checklist

- [ ] Move GitHub Project card → **In Review**
- [ ] Labels: `bloc-*`, `sync-critical` / `hardware-qa` if applicable
- [ ] CI green on `develop/integration` target
- [ ] Request self-review with `glucose-for-watch-pr-gatekeeper` before merge

## PR body and bloc hints

- Body template: [references/pr-body-template.md](references/pr-body-template.md)
- What each bloc's PR must include: [references/bloc-hints.md](references/bloc-hints.md)

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
5. Reminder to run `glucose-for-watch-pr-gatekeeper` before merge
