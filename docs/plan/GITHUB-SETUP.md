# GitHub Setup Plan — Glucose For Watch

> **Role:** step-by-step guide to configure the GitHub repository and a **GitHub Project** aligned with [ACTION-PLAN.md](ACTION-PLAN.md), [PROGRESS.md](PROGRESS.md), and [STABILITY-GATES.md](STABILITY-GATES.md).  
> **Context:** sideload PC app · solo dev · critical path v0.5.0 → v0.6.0 · phone→watch sync is sacred.

---

## Table of contents

1. [Overview](#1-overview)
2. [Phase 0 — Repo hygiene](#2-phase-0--repo-hygiene)
3. [Phase 1 — Branches](#3-phase-1--branches)
4. [Phase 2 — Labels and milestones](#4-phase-2--labels-and-milestones)
5. [Phase 3 — Issues and PRs](#5-phase-3--issues-and-prs)
6. [Phase 4 — GitHub Project](#6-phase-4--github-project)
7. [Phase 5 — Automations](#7-phase-5--automations)
7. [Phase 6 — Security and release](#8-phase-6--security-and-release)
8. [Phase 7 — CI and checks](#9-phase-7--ci-and-checks)
9. [Block mapping ↔ GitHub](#10-block-mapping--github)
10. [Initial backlog to import](#11-initial-backlog-to-import)
11. [Weekly rituals](#12-weekly-rituals)
12. [Final checklist](#13-final-checklist)

---

## 1. Overview

### Target architecture

```
GitHub Repo (Glucose For Watch)
├── Branches
│   ├── main              ← tagged releases (v0.5.0, v0.6.0)
│   ├── integrate         ← daily integration (formerly rebuild)
│   └── feat|fix|docs|qa/bloc-*  ← short-lived branches (1–5 d)
├── Issues                ← 1 issue = 1 atomic task (X.5a, C.7…)
├── Pull Requests         ← 1 PR = 1 block or measurable sub-goal
├── Milestones            ← v0.5.0 · v0.6.0
└── GitHub Project        ← Kanban orchestration + gates + KPI
         ↕ weekly sync
docs/plan/PROGRESS.md     ← KPI source-of-truth scoreboard
docs/qa/                  ← hardware evidence (soak, incidents)
```

### Principles (inherited from the app plan)

| # | Principle | GitHub implementation |
|---|----------|------------------------|
| P1 | Stability before features | Milestone v0.5.0 before v0.6.0; label `gate-blocker` |
| P2 | One PR = one goal | Branch `fix/bloc-x-*` · linked issue · PR template |
| P3 | Proof before merge | CI checks + PR checklist + QA Hardware column |
| P4 | Soak required | Issue C.7 · Evidence field · milestone v0.5.0 |
| P6 | Sync = sacred contract | Label `sync-critical` · S1–S3 retest auto-created |
| P7 | Solo dev sequenced | Limited Project · no unnecessary columns |

### Related documents

| Doc | Role |
|-----|------|
| [ACTION-PLAN.md](ACTION-PLAN.md) | Detailed backlog · S1–S8 calendar |
| [PROGRESS.md](PROGRESS.md) | Gates/KPI scoreboard · weekly updates |
| [STABILITY-GATES.md](STABILITY-GATES.md) | Go/No-Go criteria per gate |
| [PR-CHECKLIST.md](PR-CHECKLIST.md) | Merge checklist (copied into PR) |

---

## 2. Phase 0 — Repo hygiene

**Duration:** 30 min · **Blocking before everything else**

### 0.1 `.gitignore` — add

```gitignore
# Cursor runtime (local session state)
.cursor/state/
!.cursor/state/.gitkeep

# Build / tooling noise
*.log
analytics.settings
.tmp-*.jar
.android-user-home/
```

### 0.2 Remove from git tracking (without deleting locally)

```powershell
git rm -r --cached .cursor/state/
git rm --cached .android-user-home/debug.keystore.lock .tmp-protolayout-classes.jar .tmp-tiles-classes.jar analytics.settings build-last.log
```

### 0.3 Repo files to add

| File | Content |
|---------|---------|
| `LICENSE` | Chosen license (MIT, Apache-2.0, or proprietary sideload) |
| `SECURITY.md` | Vulnerability reporting · no real glucose in reports |
| `AGENTS.md` | Cursor skills index + docs entry point |
| `.editorconfig` | LF · UTF-8 · indent 4 (Kotlin/XML) |

### 0.4 Commit missing plan docs

- [ ] `docs/plan/PROGRESS.md` (currently untracked)
- [ ] This file `docs/plan/GITHUB-SETUP.md`

---

## 3. Phase 1 — Branches

### 3.1 Branch model

| Branch | Type | Role | Merge into |
|---------|------|------|------------|
| `main` | long-lived | Stable releases · M7/M8 tags | — |
| `develop/integration` | long-lived | Daily integration · CI | `main` (post-gate) |
| `docs` | long-lived | Docs-only mirror (auto-sync, no direct edits) | — |
| `sandbox/qa-hardware` | long-lived | Hardware QA · evidence · scripts/qa | `develop/integration` |
| `sandbox/ui-ux-kit` | long-lived | ToXY kit · tokens · design-reference | `develop/integration` |
| `sandbox/mobile-app` | long-lived | Phone app (`mobile/`) | `develop/integration` |
| `sandbox/wear-app` | long-lived | Wear tile · complication · UI | `develop/integration` |
| `release/v0.5` | long-lived (temp.) | Bugfix freeze before v0.5.0 tag | `main` |
| `{type}/bloc-{id}-{slug}` | short-lived | Single-bloc PR from develop/integration | `develop/integration` |

See [DOCS-BRANCH.md](DOCS-BRANCH.md) for the `docs` branch workflow · [WORKSPACE.md](WORKSPACE.md) for sandbox backlog and scopes.

**Phase B (create when needed):** `workspace/sync-platform` · `workspace/infrastructure` · `workspace/dexcom-share`

**Allowed types:** `feat` · `fix` · `docs` · `test` · `chore` · `qa` · `design`

**Plan-aligned examples:**

```
fix/bloc-x-fgs-crash          → PR #8  · gate G-X
feat/bloc-a-p0-reliability    → PR #9  · gate G-A
feat/bloc-m-home-viewmodel    → PR #10 · gate G-M
feat/bloc-b-wear-complication → PR #11 · gate G-B
docs/bloc-c-qa-evidence       → PR #12 · gate G-C
test/bloc-d-dexcom-coverage   → PR #13-14 · gate G-D
feat/bloc-f0-compose-gradle   → PR #15 · gate G-F0
feat/bloc-f3-home-compose     → PR #18 · gate G-F3
chore/bloc-s-repo-hygiene     → cross-cutting
qa/bloc-c-soak-night          → C.7 hardware session
```

### 3.2 Migration `rebuild` → `develop/integration`

```powershell
git checkout rebuild
git branch -m integrate
git push origin -u integrate
# Update CI (see Phase 7) then:
git push origin --delete rebuild
```

### 3.3 Branches to audit / close

| Current branch | Action |
|------------------|--------|
| `design` | **Replaced** by `sandbox/ui-ux-kit` (rebased on `develop/integration`) · delete |
| `dev` | Delete (stale, 0 unique commits) |
| `phase/test` | Delete · QA → `sandbox/qa-hardware` |
| `rebuild` | Delete if still on origin (renamed to `develop/integration`) |
| `docs` | Keep (auto-sync mirror) |

### 3.3.1 Workspace security

| Measure | Detail |
|---------|--------|
| PR `sandbox/*` → `develop/integration` | CI `Verify Linux Build Gates` required |
| Secret scanning | GitHub Settings → Code security → enable + push protection |
| Pre-commit | `.githooks/pre-commit` · `git config core.hooksPath .githooks` |
| Scope skills | `.cursor/skills/glucose-for-watch-*-scope` · `glucose-for-watch-sandbox-guard` |
| Sensitive paths | [.github/CODEOWNERS](../../.github/CODEOWNERS) |

### 3.4 Branch protection for `main`

**Settings → Branches → Add rule → `main`**

| Option | Value |
|--------|--------|
| Require pull request before merging | ✅ |
| Required approvals | 0 (solo) or 1 if co-dev |
| Require status checks | ✅ `Verify Linux Build Gates` |
| Require branches up to date | ✅ |
| Do not allow bypassing | ✅ |
| Restrict pushes | ✅ (nobody except you via PR) |

### 3.5 Protection for `develop/integration` (recommended)

| Option | Value |
|--------|--------|
| Require status checks | ✅ CI verify |
| Allow direct push | ✅ (solo dev daily) |

---

## 4. Phase 2 — Labels and milestones

### 4.1 Labels — creation (GitHub UI or CLI)

**Settings → Labels** or with [GitHub CLI](https://cli.github.com/):

```bash
# Blocks (color by domain)
gh label create "bloc-s"       --color "BFD4F2" --description "Cross-cutting stability"
gh label create "bloc-x"       --color "D73A4A" --description "FGS crash · gate G-X"
gh label create "bloc-a"       --color "FBCA04" --description "P0 reliability · gate G-A"
gh label create "bloc-m"       --color "0E8A16" --description "Mock user · gate G-M"
gh label create "bloc-b"       --color "1D76DB" --description "Sync/wear · gate G-B"
gh label create "bloc-c"       --color "5319E7" --description "Hardware QA · gate G-C"
gh label create "bloc-d"       --color "006B75" --description "Quality/tests · gate G-D"
gh label create "bloc-f"       --color "E99695" --description "Compose v0.6 · gate G-F*"

# Priority / risk
gh label create "gate-blocker" --color "B60205" --description "Blocks v0.5 or v0.6 tag"
gh label create "incident-p0"  --color "8B0000" --description "Open fatal crash"
gh label create "sync-critical" --color "D93F0B" --description "Touches sync · S1–S3 required"

# App surface
gh label create "area:mobile"  --color "C5DEF5" --description "Phone app"
gh label create "area:wear"    --color "C5DEF5" --description "Tile · complication"
gh label create "area:sync"     --color "C5DEF5" --description "GlucoseSyncEngine · Data Layer"
gh label create "area:dexcom"   --color "C5DEF5" --description "Share API · auth"
gh label create "area:ux-kit"    --color "C5DEF5" --description "toxy-ux-kit · AGP colors"
gh label create "area:qa"       --color "C5DEF5" --description "Hardware QA · docs/qa"
gh label create "area:infra"    --color "C5DEF5" --description "CI · scripts · repo"

# Workflow
gh label create "hardware-qa"  --color "D4C5F9" --description "adb phone+watch session"
gh label create "docs-only"    --color "EDEDED" --description "No build required"
gh label create "good first issue" --color "7057FF" --description "Easy entry point"

# Existing (keep)
# bug · enhancement
```

### 4.2 Milestones

| Milestone | Title | Target date | Description |
|-----------|-------|------------|-------------|
| M1 | **v0.5.0 — Stable sideload** | end S4 | Blocks X→D · gates G-X→G-M7 · soak C.7 |
| M2 | **v0.6.0 — Compose phone** | end S8 | Blocks F0→F5 · gate G-M8 |

**Issues per milestone:** see [§11 Initial backlog](#11-initial-backlog-to-import).

---

## 5. Phase 3 — Issues and PRs

### 5.1 Issue templates (repo files)

| Template | Usage |
|----------|-------|
| `bug_report.md` | User bug / sync (existing) |
| `feature_request.md` | Enhancement (existing) |
| `bloc_task.md` | ACTION-PLAN task (new) |
| `incident_p0.md` | Fatal crash · open incident (new) |

See `.github/ISSUE_TEMPLATE/` — choice via `config.yml`.

### 5.2 Issue title convention

```
[bloc-x] X.5a — try/catch FGS in onCreate
[bloc-c] C.7 — 8h overnight soak + sign-off
[incident] FGS crash Pixel 8a — 2026-05-25
[bloc-f3] F3 — HomeScreen Compose + G-F3 gate (30m sync · C.7 K2 baseline)
```

### 5.3 PR template

The template `.github/pull_request_template.md` references [PR-CHECKLIST.md](PR-CHECKLIST.md).

**Required fields in each PR:**

- Block / Gate / Sync touch
- Linked issue (`Closes #N` or `Refs #N`)
- Source branch → `develop/integration` (or `main` for release hotfix)

### 5.4 Issue ↔ PR ↔ docs linkage

```
Issue #42 [bloc-x] X.5a
    ↓ PR fix/bloc-x-fgs-crash → integrate
    ↓ merge
PROGRESS.md scoreboard updated
docs/qa/soak-runs/… if hardware
Project card → Done
```

---

## 6. Phase 4 — GitHub Project

### 6.1 Creation

1. Repo → **Projects** → **New project**
2. Template: **Team backlog** (or empty Board)
3. Name: **`Glucose For Watch — post-v0.6`**
4. Link to repo `Glucose-For-Watch`

### 6.2 Columns (Board view)

| Column | Definition of Ready | Definition of Done |
|---------|---------------------|------------------|
| **Backlog** | Issue created · block identified | — |
| **Ready** | Upstream gate OK · branch named | — |
| **In Progress** | Active dev · draft PR possible | — |
| **In Review** | PR open · CI green | — |
| **QA Hardware** | Sync/wear touch · adb required | smoke + seq noted |
| **Gate Ready** | stability-gate PASS · gate criteria checked | — |
| **Done** | PR merged · PROGRESS updated | evidence linked |

**Solo rule:** max **2** In Progress cards (critical path focus).

### 6.3 Custom fields (Project settings → Fields)

| Field | Type | Options / notes |
|-------|------|-----------------|
| **Bloc** | Single select | S, X, A, M, B, C, D, F0, F1, F2, F3, F4, F5 |
| **Gate** | Single select | G-X, G-A, G-M, G-B, G-C, G-D, G-M7, G-F0…G-F3, G-M8 |
| **KPI** | Multi select | K1, K2, K3, K4, K5, K6, K7, K8 |
| **Sync touch** | Checkbox | Triggers QA Hardware column |
| **Hardware QA** | Checkbox | Phone + watch required |
| **Effort (h)** | Number | From ACTION-PLAN |
| **Branch** | Text | e.g. `fix/bloc-x-fgs-crash` |
| **Evidence** | URL | Link to docs/qa/ or issue comment |

### 6.4 Project views

#### View A — **Board** (daily)

- Layout: Board
- Group by: Status (columns §6.2)
- Filter: `status != Done` OR `updated:>@today-14d`

#### View B — **Gates & KPI** (weekly)

- Layout: Table
- Columns: Title · Bloc · Gate · KPI · Sync touch · Milestone · Assignee
- Sort: Gate (custom order G-X first) · Priority
- Filter: Milestone = v0.5.0

#### View C — **Roadmap** (planning)

- Layout: Roadmap
- Date field: **Target date** (aligned with S1–S8 calendar)
- Group: Milestone
- Items: whole blocks + milestones C.7 · M7 · M8

#### View D — **QA & Incidents**

- Layout: Table
- Filter: label `hardware-qa` OR `incident-p0` OR Bloc = C
- Columns: Title · Evidence · Gate · Status

### 6.5 Project ↔ PROGRESS.md sync

| Where | What | Frequency |
|----|------|-----------|
| GitHub Project | Card status · In Progress · Done | real-time |
| PROGRESS.md | Gates scoreboard · KPI · dates | **weekly** (Monday ritual) |
| STABILITY-GATES.md | Criteria (rarely modified) | if new gate |
| docs/qa/ | Soak/incident evidence | after each session |

**Do not duplicate** task detail: ACTION-PLAN remains the spec; the Project is the **dashboard**.

---

## 7. Phase 5 — Automations

### 7.1 Project workflows (Settings → Workflows)

| Workflow | Trigger | Action |
|----------|---------|--------|
| Item added to project | Issue added | Status = Backlog |
| Item closed | Issue closed | Status = Done |
| PR merged | Linked issue | Status = Done · comment |
| Item labeled `sync-critical` | Label added | Sync touch = checked |

### 7.2 Manual suggestions (solo dev)

After merging a PR that touches sync:

1. Create follow-up issue: `[bloc-s] S1–S3 retest post-PR #N`
2. Label `sync-critical` · `hardware-qa`
3. QA Hardware column

### 7.3 Dependabot (`.github/dependabot.yml`)

```yaml
version: 2
updates:
  - package-ecosystem: "gradle"
    directory: "/"
    schedule:
      interval: "weekly"
    labels:
      - "area:infra"
      - "bloc-s"
    open-pull-requests-limit: 2

  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
    labels:
      - "area:infra"
```

**Rule:** AGP/Gradle upgrades via skill `glucose-for-watch-dependency-advisor` · dedicated PR · never mixed with feature block.

---

## 8. Phase 6 — Security and release

### 8.1 SECURITY.md (repo root)

Minimal content:

- Do not include Dexcom credentials · real glucose · full logcat with PII
- Email or GitHub Security Advisories (private)
- Scope: mobile/wear app · sideload scripts · no backend server

### 8.2 Release workflow (manual v0.5)

Until automated release CI:

1. Gate G-M7 ✅ in PROGRESS.md
2. Branch `release/v0.5` from `develop/integration`
3. `bash scripts/release/release_dry_run.sh`
4. Tag `v0.5.0` on `main`
5. GitHub Release · notes from CHANGELOG.md · APK as private artifact (if configured)

### 8.3 GitHub Release checklist

- [ ] CHANGELOG.md section v0.5.0
- [ ] Legal placeholders OK (`check_legal_placeholders.sh`)
- [ ] soak C.7 sign-off in docs/qa/
- [ ] Incident K1 = 0 (closed)

---

## 9. Phase 7 — CI and checks

### 9.1 Branches triggering CI

Update `.github/workflows/ci.yml`:

```yaml
on:
  push:
    branches:
      - main
      - develop/integration      # replaces rebuild
  pull_request:
  workflow_dispatch:
```

### 9.2 Required status checks on `main`

| CI job | Display name | Blocking |
|--------|-------------|----------|
| `verify` | Verify Linux Build Gates | ✅ |
| `release-artifacts` | Verify Release Artifacts | ✅ (pre-release) |

### 9.3 Local checks (outside GitHub)

Not enforceable on GitHub without self-hosted runner:

- `stability-gate.ps1` → manual PR checklist
- `hardware-smoke.ps1` → Project QA Hardware column

### 9.4 GitHub Actions (public repo)

Repo visibility: **public** ([ToXY0392/Glucose-For-Watch](https://github.com/ToXY0392/Glucose-For-Watch)). Public repositories use **standard GitHub-hosted runners** with **generous free minute allowances** (see [GitHub Actions billing](https://docs.github.com/en/billing/managing-billing-for-github-actions/about-billing-for-github-actions)).

| Symptom | Likely cause | Action |
|---------|--------------|--------|
| Job fails in **~2 s**, **0 steps**, `runner_id: 0` | Transient outage, org policy, or (if repo made private again) quota exhausted | Re-run workflow · [Actions usage](https://github.com/settings/billing) if private |
| Full CI ~10 min then **success** | Normal (Kotlin tree changed) | — |
| Docs/QA PR: **Verify docs and QA scripts** ~5 s | Normal (`ci.yml` skips Gradle when only `docs/**` or `scripts/qa/**` change) | — |
| Architecture PNG drift | Regenerate on Linux: `bash scripts/assets/export-architecture-diagram.sh` | Commit PNG |

**2026-05-27:** repo switched from private → public after repeated `runner_id: 0` failures (private quota exhausted). Prefer **green CI before merge**; local fallback: `./gradlew` + `hardware-smoke.ps1`.

---

## 10. Block mapping ↔ GitHub

| Block | Plan PR | Gate | Milestone | Typical labels | Branch type |
|------|---------|------|-----------|-----------------|--------------|
| S | — | cross-cutting | both | `bloc-s`, `area:infra` | `chore/bloc-s-*` |
| X | #8 | G-X | v0.5.0 | `bloc-x`, `gate-blocker`, `sync-critical` | `fix/bloc-x-*` |
| A | #9 | G-A | v0.5.0 | `bloc-a`, `area:mobile` | `feat/bloc-a-*` |
| M | #10 | G-M | v0.5.0 | `bloc-m`, `area:mobile` | `feat/bloc-m-*` |
| B | #11 | G-B | v0.5.0 | `bloc-b`, `area:wear`, `sync-critical` | `feat/bloc-b-*` |
| C | #12 | G-C | v0.5.0 | `bloc-c`, `hardware-qa`, `gate-blocker` | `docs/bloc-c-*`, `qa/*` |
| D | #13–14 | G-D | v0.5.0 | `bloc-d`, `area:dexcom` | `test/bloc-d-*` |
| M7 | tag | G-M7 | v0.5.0 | — | `release/v0.5` |
| F0–F5 | #15–18 | G-F* | v0.6.0 | `bloc-f`, `area:mobile` | `feat/bloc-f*-*` |
| M8 | tag | G-M8 | v0.6.0 | — | `release/v0.6` |

### Critical path (merge order into `develop/integration`)

```
X → A → (M ∥ prep B) → B → C → D → merge integrate → main → v0.5.0
                                              ↓
                                    F0 → F1 → F2 → F3 → F5 → v0.6.0
```

---

## 11. Initial backlog to import

Create these issues **in order** and add them to the Project (milestone v0.5.0 except F*).

### P0 — Immediate blockers

| Issue | Block | Gate | PROGRESS status |
|-------|------|------|---------------|
| `[bloc-x] X.3 — Repro soak FGS or quota simulation` | X | G-X | 🔄 |
| `[bloc-x] X.7 — Robolectric test FGS denied → Worker fallback` | X | G-X | ☐ |
| `[bloc-c] C.7 — 8h overnight soak + morning sign-off` | C | G-C | 🔄 |
| `[incident] FGS crash 2026-05-25 — close after G-X` | X | G-X | open K1 |

### P1 — v0.5.0 in progress

| Issue | Block | Gate |
|-------|------|------|
| `[bloc-a] A.1 — Notification permission flow` | A | G-A |
| `[bloc-a] A.3 — Manual sync snackbar feedback` | A | G-A |
| `[bloc-m] M.4 — design-reference companion PNG up to date` | M | G-M |
| `[bloc-b] B.4 — WatchSyncVerifier via engine + ack test` | B | G-B |
| `[bloc-c] C.2 — Complication vs tile every 5 min × 6` | C | G-C |
| `[bloc-c] C.3 — Offline watch 2h · catch-up` | C | G-C |
| `[bloc-c] C.8 — Watch ≤20% battery · degraded sync` | C | G-C |

### P2 — Post G-C / pre M7

| Issue | Block | Gate |
|-------|------|------|
| `[bloc-d] D.6 — capture-crash-log.ps1 one-command` | D | G-D |
| `[bloc-s] Repo hygiene — gitignore + purge artifacts` | S | — |
| `[bloc-s] Commit PROGRESS.md + plan docs index` | S | — |

### v0.6.0 (milestone M2 — create issues later)

| Issue | Block | Gate |
|-------|------|------|
| `[bloc-f0] F0 — Gradle Compose + GlucoseForWatchTheme` | F0 | G-F0 |
| `[bloc-f3] F3 — HomeScreen Compose + G-F3 gate` | F3 | G-F3 |

---

## 12. Weekly rituals

### Monday (15 min)

- [ ] Open Project **Gates & KPI** view
- [ ] Update [PROGRESS.md](PROGRESS.md) scoreboard
- [ ] Pick **1 block** + **1 gate** for end of week
- [ ] Max 2 cards → In Progress
- [ ] `adb devices -l` · phone + watch OK

### Before each PR

- [ ] Branch named `type/bloc-id-slug`
- [ ] Linked issue · block + area labels
- [ ] [PR-CHECKLIST.md](PR-CHECKLIST.md) in PR description
- [ ] `verify_ci.sh` + `stability-gate.ps1`

### Friday (30 min)

- [ ] Block gate checked or deferred (comment why on issue)
- [ ] Done cards · evidence URL filled in
- [ ] Hardware session → file in `docs/qa/sessions/` or `soak-runs/`

### Overnight soak (C.7)

- [ ] Issue C.7 → In Progress · label `hardware-qa`
- [ ] Morning: sign-off template · close issue · PROGRESS K2 ✅

---

## 13. Final checklist

### Phase 0 — Hygiene
- [ ] `.gitignore` enriched
- [ ] Artifacts removed from git tracking
- [ ] `PROGRESS.md` committed
- [ ] `LICENSE` · `SECURITY.md` · `AGENTS.md`

### Phase 1 — Branches
- [ ] `rebuild` → `develop/integration`
- [ ] `main` protection active
- [ ] Stale branches cleaned up

### Phase 2 — Labels & milestones
- [ ] Block + gate labels created
- [ ] Milestones v0.5.0 and v0.6.0

### Phase 3 — Issues & PRs
- [ ] Templates bloc_task + incident_p0
- [ ] PR template aligned with checklist

### Phase 4 — Project
- [ ] Project created · 4 views
- [ ] Bloc · Gate · KPI · Evidence fields
- [ ] P0 backlog imported (§11)

### Phase 5–7 — Automation & CI
- [ ] Dependabot configured
- [ ] CI on `develop/integration`
- [ ] First Monday ritual executed

---

## Appendix — AGENTS.md (suggested excerpt)

```markdown
# Agent guide — Glucose For Watch

## Plan docs
- Hub: docs/index.md
- GitHub setup: docs/plan/GITHUB-SETUP.md
- PR checklist: docs/plan/PR-CHECKLIST.md

## Branches
- develop/integration = daily integration
- fix|feat/bloc-{id}-{slug} = short-lived

## Skills (sync/debug)
- glucose-for-watch-sync-health-reviewer
- glucose-for-watch-agp-color-guard
- glucose-for-watch-pr-gatekeeper (to create)
```

---

*Last updated: 2026-05-26 · aligned with ACTION-PLAN S1–S8 and PROGRESS scoreboard.*
