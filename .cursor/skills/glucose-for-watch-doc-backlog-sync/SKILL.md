---
name: glucose-for-watch-doc-backlog-sync
description: Syncs docs/plan/DOC-BACKLOG.md with PROGRESS.md, QA evidence, GitHub issues, and repo signals. Use on Monday planning, after hardware QA sessions, before v0.5 tag, or when the user asks to update the documentation backlog.
disable-model-invocation: true
---

# Widget G7 Doc Backlog Sync

## Source of truth

| Artifact | Role |
|----------|------|
| [docs/plan/DOC-BACKLOG.md](../../../docs/plan/DOC-BACKLOG.md) | Documentation task backlog (this skill maintains it) |
| [docs/plan/PROGRESS.md](../../../docs/plan/PROGRESS.md) | Product gates/KPIs — cross-check, do not duplicate |
| [docs/plan/AUTOMATION-BACKLOG.md](../../../docs/plan/AUTOMATION-BACKLOG.md) | AUTO-* CI tasks — link only, don't merge tables |
| `docs/qa/` | Evidence for P0/P1 completion |

**Pair with:** `glucose-for-watch-github-project-sync` (Monday) · `glucose-for-watch-doc-drift-checker` (monthly)

## Quick start

```powershell
.\scripts\dev\sync_doc_backlog.ps1
```

Read the report at `.cursor/state/reports/doc-backlog-sync.md`, then edit `DOC-BACKLOG.md`.

## Workflow

### 1. Collect signals (always run script first)

```powershell
.\scripts\dev\sync_doc_backlog.ps1
```

Script scans: incident status, sign-off files, QA sessions, CONTRIBUTING/PR template for AUTO-9, design-reference freshness, `capture-crash-log.ps1` presence.

### 2. Cross-read PROGRESS + DOC-BACKLOG

For each **P0/P1 row** in DOC-BACKLOG:

| ID | Mark ✅ when |
|----|-------------|
| DOC-P0-1 | Filled sign-off exists (`docs/qa/stability-signoff-*.md`, not empty template) |
| DOC-P0-2 | Incident doc status → **closed** |
| DOC-P0-3 | PROGRESS scoreboard dates/gates match C.7 outcome |
| DOC-P0-4 | Session files exist for C.2, C.3, C.8 under `docs/qa/sessions/` |
| DOC-P1-1 | CONTRIBUTING mentions `glucose-for-watch-pr-author` + `glucose-for-watch-pr-gatekeeper` |
| DOC-P1-2 | PR template hints `@glucose-for-watch-pr-author` |
| DOC-P1-3 | design-reference PNGs/index regenerated recently |
| DOC-P1-4 | `capture-crash-log.ps1` documented in dev/setup.md |
| DOC-P1-5 | architecture SVG/PNG matches modules (manual verify) |
| DOC-P1-6 | user.md notes FR strings vs EN guide |
| DOC-P1-7 | CHANGELOG has v0.5.0 draft section |
| DOC-P1-8 | ACTION-PLAN §7 references doc-backlog-sync |

Use table status: `☐` open · `🔄` in progress · `✅` done.

### 3. Apply edits to DOC-BACKLOG

- Bump **Last updated** to today (ISO date).
- Move finished items to **Recently completed** (one line each + commit ref if known).
- Do **not** remove P0/P1 IDs — keep history in Recently completed.
- Keep P2 AUTO-* rows in sync with GitHub issue state when `gh` is available:

```powershell
gh issue list --repo ToXY0392/Glucose-For-Watch --label "bloc-s" --state all --limit 30
```

### 4. Optional PROGRESS nudge

If DOC-P0-* completion implies a gate change, add a **comment block** in sync report — do not edit PROGRESS unless user asked or it's part of the same session goal.

### 5. Output

Write or update `.cursor/state/reports/doc-backlog-sync.md`:

```markdown
# Doc backlog sync — YYYY-MM-DD

## Summary
- P0 open: N · P1 open: N
- Applied: yes/no

## Suggested status changes
| ID | Current | Suggested | Reason |
|----|---------|-----------|--------|

## PROGRESS cross-check
- G-C / K2 / incident #4: …

## Next actions (max 3)
1. …
```

## Rules

- Edit **only** `DOC-BACKLOG.md` and the sync report unless user explicitly asks to fix CONTRIBUTING, PROGRESS, or QA evidence.
- Never mark DOC-P0-1 ✅ without a real sign-off file (not the empty template).
- Preserve table structure and task IDs (DOC-P0-*, DOC-P1-*, DOC-F*).
- English prose in DOC-BACKLOG.
- During active soak (C.7): do not suggest APK reinstall in next actions.

## Weekly ritual (Monday, ~10 min)

1. `@glucose-for-watch-doc-backlog-sync`
2. `@glucose-for-watch-github-project-sync`
3. Pick top **1** open P0, else **1** P1 for the week
