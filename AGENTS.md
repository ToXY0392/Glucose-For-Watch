# Agent guide — Glucose For Watch

Entry point for coding agents (Claude Code and Cursor) working on this repository.

**Skills:** canonical source is [.claude/skills/](.claude/skills/) (read by Claude Code). [.cursor/skills/](.cursor/skills/) is a generated mirror for Cursor — never edit it by hand; run `python3 scripts/dev/sync_skills.py` after changing a skill (CI checks the mirror with `--check` and validates skills with `--lint`).

## Docs

| Doc | Path |
|-----|------|
| Hub | [docs/index.md](docs/index.md) |
| Doc backlog | [docs/plan/DOC-BACKLOG.md](docs/plan/DOC-BACKLOG.md) |
| Branch model | [docs/plan/WORKSPACE.md](docs/plan/WORKSPACE.md) · integration: `develop/integration` |
| GitHub setup | [docs/plan/GITHUB-SETUP.md](docs/plan/GITHUB-SETUP.md) |
| PR checklist | [docs/plan/PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md) |
| Progress / gates | [docs/plan/PROGRESS.md](docs/plan/PROGRESS.md) |
| Architecture | [docs/dev/architecture.md](docs/dev/architecture.md) |
| Setup | [docs/dev/setup.md](docs/dev/setup.md) |

## Repository

| Item | Value |
|------|-------|
| GitHub | `ToXY0392/Glucose-For-Watch` |
| Local folder | `Glucose-For-Watch` |
| Integration branch | `develop/integration` (formerly `rebuild`) |
| Release branch | `main` |

**Branch naming:** `{feat|fix|docs|test|chore|qa}/bloc-{id}-{slug}` (short-lived). Avoid the legacy `feature/` prefix.

## Long-lived workspace sandboxes

| Branch | Role | Skill |
|--------|------|-------|
| `sandbox/mobile-app` | Phone app (`mobile/`) | `glucose-for-watch-mobile-app-scope` |
| `sandbox/documentation` | Plan, guides, skills, rules | `glucose-for-watch-documentation-scope` |

Hub: [docs/plan/WORKSPACE.md](docs/plan/WORKSPACE.md) · Router: `glucose-for-watch-sandbox-guard`

Only `sandbox/mobile-app` and `sandbox/documentation` are long-lived. Use the corresponding scope and skill for other areas on a short-lived `feat/`, `fix/`, `qa/`, or `docs/` branch; create another persistent sandbox only by explicit decision.

## Rules (always apply)

- [.cursor/rules/glucose-for-watch-dual-ide-wsl.mdc](.cursor/rules/glucose-for-watch-dual-ide-wsl.mdc)
- [.cursor/rules/glucose-for-watch-reinstall-apks.mdc](.cursor/rules/glucose-for-watch-reinstall-apks.mdc)
- [.cursor/rules/glucose-for-watch-sandbox-scopes.mdc](.cursor/rules/glucose-for-watch-sandbox-scopes.mdc)

Cursor loads these `.mdc` files itself. Claude Code does not, so their content is summarised here:

- **Single working copy (WSL + Android Studio):** one editable clone under WSL, opened by Android Studio through `\\wsl$`; `local.properties` stays out of git; LF line endings; avoid concurrent Gradle syncs.
- **After `mobile/` or `wear/` changes:** compile and run module tests. Install on devices **only on explicit user request** (see hard constraints). Claude Code asks for confirmation through the hook in [.claude/settings.json](.claude/settings.json).
- **Scopes:** before editing, check the branch and the paths you touch with `glucose-for-watch-sandbox-guard`; refuse out-of-scope edits; run `glucose-for-watch-repo-hygiene` before commit; PR to `develop/integration` with `glucose-for-watch-pr-gatekeeper`.

## GitHub workflow skills

| Task | Skill |
|------|-------|
| Draft complete documented PR | `glucose-for-watch-pr-author` |
| Before merge PR | `glucose-for-watch-pr-gatekeeper` |
| Git / secrets hygiene | `glucose-for-watch-repo-hygiene` |
| Sync DOC-BACKLOG + QA evidence | `glucose-for-watch-doc-backlog-sync` |
| Project + PROGRESS sync | `glucose-for-watch-github-project-sync` |
| Dependabot PRs | `glucose-for-watch-dependabot-triage` + `glucose-for-watch-dependency-advisor` |
| Sync debugging | `glucose-for-watch-sync-health-reviewer` |
| Release notes | `glucose-for-watch-release-notes-curator` |
| Workspace scope routing | `glucose-for-watch-sandbox-guard` |
| UX kit sandbox | `glucose-for-watch-ui-ux-kit-scope` |
| Mobile sandbox | `glucose-for-watch-mobile-app-scope` |
| Wear sandbox | `glucose-for-watch-wear-app-scope` |
| QA hardware sandbox | `glucose-for-watch-qa-hardware-scope` |
| Sync platform sandbox | `glucose-for-watch-sync-platform-scope` |
| Documentation sandbox | `glucose-for-watch-documentation-scope` |

Agent skill prefix: **`glucose-for-watch-*`** · Android package **`com.glucoseforwatch.*`** · Gradle **`GlucoseForWatch`** · install **`installGlucoseForWatchDebug`**

## Cursor built-in skills

| Task | Skill |
|------|-------|
| PR blocked on CI / comments | `babysit` |
| Split large branch | `split-to-prs` |

## Hard constraints (active soak)

- Do **not** run `installGlucoseForWatchDebug`, `adb install`, or `adb uninstall` unless explicitly requested.
- During C.7 soak: `stability-gate.ps1 -CheckLogcatOnly` for logcat-only checks.

## Internal IDs (do not rename casually)

- Gradle root: `GlucoseForWatch`
- Package: `com.glucoseforwatch.*`
- Install task: `installGlucoseForWatchDebug`
