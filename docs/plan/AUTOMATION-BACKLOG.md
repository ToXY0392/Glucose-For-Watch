# Automation backlog — Glucose For Watch

> **Cross-cutting** tasks (bloc S) for docs, UI preview, CI, and GitHub Project.  
> Android **Storybook** equivalent: `@Preview` + PNG export (XML phase) → **Showkase** (Compose v0.6 phase).

---

## Current state (already in place)

| Tool | Role | Path |
|------|------|------|
| **AppPreviewExporterTest** | 6 Home states → PNG | `mobile/.../preview/AppPreviewExporterTest.kt` |
| **export-app-preview.ps1** | Run export without hardware | `scripts/qa/export-app-preview.ps1` |
| **Showkase** | Compose preview catalog (debug) | `mobile/src/debug/` · `@ShowkaseComposable` |
| **Wear @Preview** | Tile / status Compose | `wear/.../WearStatusScreenPreview.kt` |
| **design-reference** | Static mockups | `toxy-ux-kit/design-reference/` |
| **check_docs_links.py** | Relative link lint on `docs/` | `scripts/dev/check_docs_links.py` |
| **CI verify_ci** | Unit + compile + doc links | `.github/workflows/ci.yml` |
| **Dependabot** | Gradle + Actions | `.github/dependabot.yml` |
| **Cursor skills** | doc-drift, doc-backlog-sync, pr-gatekeeper, pr-author, … | `.cursor/skills/` |

**Storybook** = web ecosystem (React). Relevant equivalent here:

| Phase | Recommended tool |
|-------|------------------|
| v0.5 (phone XML) | Robolectric PNG + HTML gallery |
| v0.6 (Compose phone) | **Showkase** + `@Preview` |
| Wear (already Compose) | Studio @Preview + optional Showkase wear |

---

## GitHub issues (AUTO-*)

Created via `scripts/dev/create_automation_issues.ps1`:

| ID | Title | Milestone | Effort |
|----|-------|-----------|--------|
| AUTO-1 | CI export PNG previews on mobile UI PR | v0.5 | done · `.github/workflows/preview-export.yml` |
| AUTO-2 | Static preview gallery HTML | v0.5 | done · `scripts/qa/generate_preview_gallery.py` |
| AUTO-3 | Showkase for Compose v0.6 | v0.6 | done · PR #39 |
| AUTO-4 | CI markdown link checker docs/ | v0.5 | done · `check_docs_links.py` |
| AUTO-5 | Weekly doc-drift scheduled issue | v0.5 | done · `doc-drift-weekly.yml` |
| AUTO-6 | Paparazzi wear tile (optional) | v0.6 | 2d |
| AUTO-7 | Project workflow PR → In Review | v0.5 | done · `project-pr-in-review.yml` |
| AUTO-8 | Architecture diagram export CI | v0.5 | done · `architecture-diagram.yml` |
| AUTO-10 | Docs-only branch CI (`docs`) | v0.5 | done · [DOCS-BRANCH.md](DOCS-BRANCH.md) |
| AUTO-9 | PR author skill + CONTRIBUTING integration ([#21](https://github.com/ToXY0392/Glucose-For-Watch/issues/21)) | v0.5 | 2h · skill ✅ |

---

## Remaining v0.5 plan tasks (outside AUTO)

From [PROGRESS.md](PROGRESS.md):

| Bloc | Remaining | Gate |
|------|-----------|------|
| X | X.3 repro, close #4 incident | G-X |
| C | C.7 sign-off, C.2/C.3/C.8 | G-C |
| A | A.1, A.3 | G-A |
| M | M.4 design-reference PNG | G-M |
| B | B.4 WatchSyncVerifier | G-B | done · PR #40 |
| D | D.6 capture-crash-log | G-D |
| S | S.6 PR checklist sync, S.8 ACTION-PLAN | cross-cutting |

---

## Automatable weekly ritual

| Day | Action | Tool |
|-----|--------|------|
| Monday | DOC-BACKLOG + PROGRESS scoreboard | skill `glucose-for-watch-doc-backlog-sync` |
| Monday | GitHub Project columns | skill `glucose-for-watch-github-project-sync` |
| Monday | Doc drift check | skill `glucose-for-watch-doc-drift-checker` |
| Each UI PR | export-app-preview / CI artifact | AUTO-1 ✅ · AUTO-2 ✅ |
| Post-merge doc | Link check | AUTO-4 |

---

## Scripts

```powershell
# Doc backlog signals
.\scripts\dev\sync_doc_backlog.ps1

# Update Project #1 columns
.\scripts\dev\update_github_project_board.ps1

# Create AUTO issues (dry-run)
.\scripts\dev\create_automation_issues.ps1 -DryRun
.\scripts\dev\create_automation_issues.ps1
```

---

## Showkase vs Storybook (v0.6 note)

When bloc **F0** starts:

1. `implementation("com.airbnb.android:showkase:…")` on `:mobile`
2. Annotate Compose screens `@ShowkaseComposable`
3. Task `./gradlew :mobile:showkaseBrowserDebug` → catalog APK on emulator
4. Complements **AUTO-2** HTML gallery for offline sideload docs

---

*Updated: 2026-05-26*
