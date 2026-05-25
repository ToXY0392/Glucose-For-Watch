# Documentation backlog — Glucose For Watch

> **Living checklist** for docs, QA evidence, and doc-adjacent automation.  
> **Not** the product backlog — see [PROGRESS.md](PROGRESS.md) and [ACTION-PLAN.md](ACTION-PLAN.md).  
> **Maintained by:** `@widget-g7-doc-backlog-sync` · **Last updated:** 2026-05-26

---

## How to use

| When | Action |
|------|--------|
| **Monday** | Run `@widget-g7-doc-backlog-sync` (or `.\scripts\dev\sync_doc_backlog.ps1 -Apply`) |
| **Before PR merge** | If behavior changed → update relevant doc · check [PR-CHECKLIST](PR-CHECKLIST.md) |
| **After hardware QA** | Add evidence under `docs/qa/` · re-run doc backlog sync |
| **Monthly** | Pair with `@widget-g7-doc-drift-checker` |

**Related:** [AUTOMATION-BACKLOG.md](AUTOMATION-BACKLOG.md) (AUTO-* CI tasks) · [docs/index.md](../index.md) (hub)

---

## Recently completed

- [x] **English docs** — plan, QA templates, GitHub templates, Cursor rules/skills, UX kit specs (`integrate` · `dab9db2`)
- [x] **Docs hub** — [docs/index.md](../index.md) with plan + GitHub links
- [x] **PR checklist** — [PR-CHECKLIST.md](PR-CHECKLIST.md) + [.github/pull_request_template.md](../../.github/pull_request_template.md)
- [x] **Stability docs** — [STABILITY-GATES.md](STABILITY-GATES.md) · [stability-signoff-template.md](../qa/stability-signoff-template.md)
- [x] **GitHub setup guides** — [GITHUB-SETUP.md](GITHUB-SETUP.md) · [GITHUB-PROJECT-UI-GUIDE.md](GITHUB-PROJECT-UI-GUIDE.md)
- [x] **pr-author skill** — `.cursor/skills/widget-g7-pr-author/` · tracked as AUTO-9 ([#21](https://github.com/ToXY0392/Glucose-For-Watch/issues/21))

---

## P0 — Release-blocking (v0.5.0)

Blocks **G-C** / **G-M7** tag until done.

| ID | Task | Owner | Status | Link / notes |
|----|------|-------|--------|--------------|
| DOC-P0-1 | **C.7 soak sign-off** — fill [stability-signoff-template.md](../qa/stability-signoff-template.md) after 8 h run | QA | ☐ | Issue [#3](https://github.com/ToXY0392/Glucose-For-Watch/issues/3) · `soak-monitor -DurationMinutes 480` |
| DOC-P0-2 | **Close incident doc** — update [2026-05-25-app-crash.md](../qa/incidents/2026-05-25-app-crash.md) status → closed after G-X + C.7 | Dev | ☐ | Issue [#4](https://github.com/ToXY0392/Glucose-For-Watch/issues/4) |
| DOC-P0-3 | **PROGRESS scoreboard** — refresh gates G-X/G-C/K2/K6 after C.7 | Dev | ☐ | [PROGRESS.md](PROGRESS.md#scoreboard-weekly-update) |
| DOC-P0-4 | **QA matrix evidence** — session notes for C.2, C.3, C.8 (complication, offline, low battery) | QA | ☐ | `docs/qa/sessions/` |

---

## P1 — v0.5.0 polish

Should ship with or shortly after **v0.5.0**; not all block the tag.

| ID | Task | Effort | Status | Link / notes |
|----|------|--------|--------|--------------|
| DOC-P1-1 | **AUTO-9** — reference `pr-author` + `pr-gatekeeper` in [CONTRIBUTING.md](../../CONTRIBUTING.md) | 30m | ☐ | [#21](https://github.com/ToXY0392/Glucose-For-Watch/issues/21) |
| DOC-P1-2 | **AUTO-9** — PR template hint: invoke `@widget-g7-pr-author` before opening | 15m | ☐ | `.github/pull_request_template.md` |
| DOC-P1-3 | **M.4** — refresh `toxy-ux-kit/design-reference/` PNGs + regenerate gallery | 2h | ☐ | `py -3 toxy-ux-kit/tools/export-design-reference.py` |
| DOC-P1-4 | **D.6** — document `capture-crash-log.ps1` in [dev/setup.md](../dev/setup.md) (or finish script + doc together) | 1h | ☐ | PROGRESS D.6 |
| DOC-P1-5 | **Architecture diagram** — verify [widget-g7-architecture.svg](../assets/widget-g7-architecture.svg) matches code · re-export PNG | 1h | AUTO-8 |
| DOC-P1-6 | **User guide ↔ app strings** — app UI is FR (`strings.xml`) · user guide is EN · add note in [guide/user.md](../guide/user.md) or plan i18n | 1h | ☐ | Decision: EN UI vs FR UI for v0.5 |
| DOC-P1-7 | **CHANGELOG v0.5.0** — draft release notes when gates pass | 1h | ☐ | `@widget-g7-release-notes-curator` |
| DOC-P1-8 | **S.6 / S.8 sync** — align ACTION-PLAN calendar with current PROGRESS statuses | 30m | 🔄 | Bloc S |

---

## P2 — Automation & hygiene

Tracked in [AUTOMATION-BACKLOG.md](AUTOMATION-BACKLOG.md); implement when bandwidth allows.

| ID | Task | Milestone | Status | GitHub |
|----|------|-----------|--------|--------|
| AUTO-1 | CI export PNG previews on mobile UI PRs | v0.5 | ☐ | #13 |
| AUTO-2 | Static preview gallery HTML (offline sideload doc) | v0.5 | ☐ | #14 |
| AUTO-4 | CI markdown link checker on `docs/` | v0.5 | ☐ | #16 |
| AUTO-5 | Weekly doc-drift scheduled issue | v0.5 | ☐ | #17 |
| AUTO-7 | Project workflow: PR opened → In Review column | v0.5 | ☐ | #19 |
| AUTO-8 | Architecture diagram export in CI | v0.5 | ☐ | #20 |
| DOC-P2-1 | Close duplicate GitHub Project **#2** (keep Project **#1**) | — | ☐ | [Project #1](https://github.com/users/ToXY0392/projects/1) |
| DOC-P2-2 | **doc-backlog-sync skill** in ACTION-PLAN §7 weekly ritual | — | ✅ | `@widget-g7-doc-backlog-sync` |

---

## v0.6.0 — Compose phase (deferred)

Do **not** start until **G-M7** ✅.

| ID | Task | When | Status |
|----|------|------|--------|
| DOC-F0-1 | Document Showkase setup + `@ShowkaseComposable` conventions | F0 start | ☐ |
| DOC-F0-2 | Update [architecture.md](../dev/architecture.md) for Compose phone module layout | F0 | ☐ |
| DOC-F0-3 | **AUTO-3** — Showkase browser task in dev/setup.md | F0 | ☐ |
| DOC-F3-1 | Home Compose screen spec in `toxy-ux-kit/spec/components/` | F3 | ☐ |
| DOC-F3-2 | Migration note: XML → Compose mapping (screens + strings) | F3 | ☐ |

---

## Weekly doc ritual (≈15 min)

```powershell
# Automated signals + optional apply
.\scripts\dev\sync_doc_backlog.ps1
.\scripts\dev\sync_doc_backlog.ps1 -Apply   # agent/skill: update Last updated + report only

# Or invoke skill (reads report + edits DOC-BACKLOG + PROGRESS cross-checks)
# @widget-g7-doc-backlog-sync

# GitHub Project (pair with widget-g7-github-project-sync)
.\scripts\dev\update_github_project_board.ps1 -ProjectNumber 1
```

**Checklist:**

- [ ] PROGRESS gates/KPIs match reality
- [ ] Open incidents linked and status accurate
- [ ] New QA sessions filed under `docs/qa/`
- [ ] Broken internal links fixed
- [ ] DOC-BACKLOG P0/P1 priorities still correct

---

## Quick links

| Doc | Purpose |
|-----|---------|
| [docs/index.md](../index.md) | Documentation hub |
| [PROGRESS.md](PROGRESS.md) | Gates · blocks · KPIs |
| [ACTION-PLAN.md](ACTION-PLAN.md) | Operational calendar |
| [PR-CHECKLIST.md](PR-CHECKLIST.md) | Every PR |
| [AUTOMATION-BACKLOG.md](AUTOMATION-BACKLOG.md) | AUTO-* automation |
| [AGENTS.md](../../AGENTS.md) | Cursor agent entry |

---

*Next review: Monday · bump "Last updated" when `@widget-g7-doc-backlog-sync` runs.*
