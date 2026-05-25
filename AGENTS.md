# Agent guide — Glucose For Watch (Widget G7)

Entry point for Cursor agents working on this repository.

## Docs

| Doc | Path |
|-----|------|
| Hub | [docs/index.md](docs/index.md) |
| GitHub setup | [docs/plan/GITHUB-SETUP.md](docs/plan/GITHUB-SETUP.md) |
| PR checklist | [docs/plan/PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md) |
| Progress / gates | [docs/plan/PROGRESS.md](docs/plan/PROGRESS.md) |
| Architecture | [docs/dev/architecture.md](docs/dev/architecture.md) |
| Setup | [docs/dev/setup.md](docs/dev/setup.md) |

## Repository

| Item | Value |
|------|-------|
| GitHub | `ToXY0392/glucose-for-watch` |
| Local folder | `Widget G7` (unchanged) |
| Integration branch | `integrate` (formerly `rebuild`) |
| Release branch | `main` |

**Branch naming:** `{feat|fix|docs|test|chore|qa}/bloc-{id}-{slug}`

## Rules (always apply)

- [.cursor/rules/widget-g7-dual-ide-wsl.mdc](.cursor/rules/widget-g7-dual-ide-wsl.mdc)
- [.cursor/rules/widget-g7-reinstall-apks.mdc](.cursor/rules/widget-g7-reinstall-apks.mdc)

## GitHub workflow skills

| Task | Skill |
|------|-------|
| Before merge PR | `widget-g7-pr-gatekeeper` |
| Git / secrets hygiene | `widget-g7-repo-hygiene` |
| Project + PROGRESS sync | `widget-g7-github-project-sync` |
| Dependabot PRs | `widget-g7-dependabot-triage` + `widget-g7-dependency-advisor` |
| Sync debugging | `widget-g7-sync-health-reviewer` |
| Release notes | `widget-g7-release-notes-curator` |

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
