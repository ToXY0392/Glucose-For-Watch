---
name: glucose-for-watch-github-project-sync
description: Syncs Glucose For Watch GitHub Project with docs/plan/PROGRESS.md scoreboard, issue templates, and weekly ritual. Use on Monday planning, after gate updates, or when importing bloc tasks to GitHub Project.
disable-model-invocation: true
---

# Widget G7 GitHub Project Sync

## Sources of truth

| Artifact | Role |
|----------|------|
| [docs/plan/PROGRESS.md](../../../docs/plan/PROGRESS.md) | Gates + KPI scoreboard (weekly) |
| [docs/plan/ACTION-PLAN.md](../../../docs/plan/ACTION-PLAN.md) | Atomic tasks (X.5a, C.7…) |
| GitHub Project | Live status (Backlog → Done) |
| [docs/plan/GITHUB-SETUP.md](../../../docs/plan/GITHUB-SETUP.md) | Project columns + fields |

## Project structure

**Columns:** Backlog → Ready → In Progress → In Review → QA Hardware → Gate Ready → Done

**Fields:** Bloc, Gate, KPI, Sync touch, Hardware QA, Branch, Evidence

**Rule:** max 2 cards In Progress (solo dev).

## Weekly ritual (Monday, 15 min)

1. Run `@glucose-for-watch-doc-backlog-sync` (or `.\scripts\dev\sync_doc_backlog.ps1`).
2. Read PROGRESS scoreboard — update gate statuses and KPI dates.
3. Open Project view **Gates & KPI** — align card status with PROGRESS.
4. Pick **1 bloc** + **1 gate** for the week.
5. Move ready issues to **Ready**; active work to **In Progress**.
6. `adb devices -l` — confirm phone + watch if hardware week.

## New issue checklist

Use template `.github/ISSUE_TEMPLATE/bloc_task.md`:

- Title: `[bloc-x] X.5a — description`
- Labels: `bloc-x`, `gate-blocker` if blocking M7/M8
- Milestone: v0.5.0 or v0.6.0
- Evidence URL → `docs/qa/soak-runs/` or `docs/qa/incidents/` after hardware

## After PR merge

1. Update PROGRESS.md scoreboard.
2. Move Project card → **Done**.
3. Set **Evidence** field if soak/session doc exists.

## Repo naming

- GitHub: `ToXY0392/Glucose-For-Watch`
- Local folder: **`Widget G7`** (unchanged)
