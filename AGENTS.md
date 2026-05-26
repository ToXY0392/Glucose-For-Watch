# Agent guide — Glucose For Watch (Widget G7)

Entry point for Cursor agents working on this repository.

## Docs

| Doc | Path |
|-----|------|
| Hub | [docs/index.md](docs/index.md) |
| Doc backlog | [docs/plan/DOC-BACKLOG.md](docs/plan/DOC-BACKLOG.md) |
| Docs branch | [docs/plan/DOCS-BRANCH.md](docs/plan/DOCS-BRANCH.md) · [`docs`](https://github.com/ToXY0392/Glucose-For-Watch/tree/docs) on GitHub |
| GitHub setup | [docs/plan/GITHUB-SETUP.md](docs/plan/GITHUB-SETUP.md) |
| PR checklist | [docs/plan/PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md) |
| Progress / gates | [docs/plan/PROGRESS.md](docs/plan/PROGRESS.md) |
| Architecture | [docs/dev/architecture.md](docs/dev/architecture.md) |
| Setup | [docs/dev/setup.md](docs/dev/setup.md) |

## Repository

| Item | Value |
|------|-------|
| GitHub | `ToXY0392/Glucose-For-Watch` |
| Local folder | `Widget G7` (unchanged) |
| Integration branch | `integrate` (formerly `rebuild`) |
| Release branch | `main` |

**Branch naming:** `{feat|fix|docs|test|chore|qa}/bloc-{id}-{slug}` (short-lived) · `workspace/*` (long-lived sandboxes)

## Workspace sandboxes

| Branch | Role | Skill |
|--------|------|-------|
| `workspace/qa-hardware` | QA evidence, soak, scripts/qa | `widget-g7-qa-hardware-scope` |
| `workspace/ui-ux-kit` | ToXY kit, tokens, design-reference | `widget-g7-ux-kit-scope` |
| `workspace/mobile-app` | Phone app (`mobile/`) | `widget-g7-mobile-app-scope` |
| `workspace/wear-app` | Wear tile, complication, UI | `widget-g7-wear-app-scope` |

Hub: [docs/plan/WORKSPACE.md](docs/plan/WORKSPACE.md) · Router: `widget-g7-workspace-guard`

Pre v0.5.0 tag: only **`workspace/qa-hardware`** active; others dormant (rebase weekly).

## Rules (always apply)

- [.cursor/rules/widget-g7-dual-ide-wsl.mdc](.cursor/rules/widget-g7-dual-ide-wsl.mdc)
- [.cursor/rules/widget-g7-reinstall-apks.mdc](.cursor/rules/widget-g7-reinstall-apks.mdc)
- [.cursor/rules/widget-g7-workspace-scopes.mdc](.cursor/rules/widget-g7-workspace-scopes.mdc)

## GitHub workflow skills

| Task | Skill |
|------|-------|
| Draft complete documented PR | `widget-g7-pr-author` |
| Before merge PR | `widget-g7-pr-gatekeeper` |
| Git / secrets hygiene | `widget-g7-repo-hygiene` |
| Sync DOC-BACKLOG + QA evidence | `widget-g7-doc-backlog-sync` |
| Project + PROGRESS sync | `widget-g7-github-project-sync` |
| Dependabot PRs | `widget-g7-dependabot-triage` + `widget-g7-dependency-advisor` |
| Sync debugging | `widget-g7-sync-health-reviewer` |
| Release notes | `widget-g7-release-notes-curator` |
| Workspace scope routing | `widget-g7-workspace-guard` |
| UX kit sandbox | `widget-g7-ux-kit-scope` |
| Mobile sandbox | `widget-g7-mobile-app-scope` |
| Wear sandbox | `widget-g7-wear-app-scope` |
| QA hardware sandbox | `widget-g7-qa-hardware-scope` |

## Cursor built-in skills

| Task | Skill |
|------|-------|
| PR blocked on CI / comments | `babysit` |
| Split large branch | `split-to-prs` |

## Hard constraints (active soak)

- Do **not** run `installWidgetG7Debug`, `adb install`, or `adb uninstall` unless explicitly requested.
- During C.7 soak: `stability-gate.ps1 -CheckLogcatOnly` for logcat-only checks.

## Internal IDs (do not rename casually)

- Gradle root: `WidgetG7`
- Package: `com.widgetg7.*`
- Install task: `installWidgetG7Debug`
