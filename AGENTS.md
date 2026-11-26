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

## Skills catalogue

Source: [.claude/skills/](.claude/skills/) · plan and policy: [docs/plan/SKILLS-PLAN.md](docs/plan/SKILLS-PLAN.md). **Auto** = the agent may load it on its own; **manual** = invoke it by name (`/name` in Claude Code, `@name` in Cursor).

### Workflow and guards (auto)

| Task | Skill |
|------|-------|
| Scope routing (branch + paths) | `glucose-for-watch-sandbox-guard` |
| Draft complete documented PR | `glucose-for-watch-pr-author` |
| Before merge PR | `glucose-for-watch-pr-gatekeeper` |
| Git / secrets hygiene before commit | `glucose-for-watch-repo-hygiene` |
| Glucose colours (AGP medical layer) | `glucose-for-watch-agp-color-guard` |
| ToXY chrome tokens | `glucose-for-watch-toxy-theme-maintainer` |
| Sync debugging | `glucose-for-watch-sync-health-reviewer` |
| Dependabot PRs | `glucose-for-watch-dependabot-triage` |

### Claude Code working method (auto)

| Task | Skill |
|------|-------|
| Token / quota economy (model routing, targeted reads) | `glucose-for-watch-economie` |
| Plan (3-5 points, validated) then incremental execution | `glucose-for-watch-plan-et-execution` |
| Session memory (`.claude/SESSION_MEMORY.md`) | `glucose-for-watch-memoire-projet` |
| Pre-commit review + commit message | `glucose-for-watch-revue-et-git` |

### Scopes (auto)

| Area | Skill |
|------|-------|
| `mobile/` · long-lived `sandbox/mobile-app` | `glucose-for-watch-mobile-app-scope` |
| docs + agent config · long-lived `sandbox/documentation` | `glucose-for-watch-documentation-scope` |
| `wear/` | `glucose-for-watch-wear-app-scope` |
| `feature/sync`, datalayer contract, core model | `glucose-for-watch-sync-platform-scope` |
| `toxy-ux-kit/`, `docs/design/` | `glucose-for-watch-ui-ux-kit-scope` |
| `docs/qa/`, `scripts/qa/` | `glucose-for-watch-qa-hardware-scope` |

### Planning and maintenance (manual)

| Task | Skill |
|------|-------|
| Toolchain / dependency upgrades + compat matrix | `glucose-for-watch-dependency-advisor` |
| Vendor watch, CVEs, CHANGELOG upstream section | `glucose-for-watch-vendor-security-watch` |
| Doc drift vs official docs | `glucose-for-watch-doc-drift-checker` |
| Sync DOC-BACKLOG + QA evidence | `glucose-for-watch-doc-backlog-sync` |
| Project board + PROGRESS sync | `glucose-for-watch-github-project-sync` |
| Recurring maintenance packs (quick / full / release) | `glucose-for-watch-maintenance-run` |
| USB detach → developer handoff (soak sessions) | `glucose-for-watch-usb-detach-handoff-writer` |

### Third-party (auto, pinned, with project overrides)

| Task | Skill | Upstream |
|------|-------|----------|
| Android test strategy / harness | `android-testing-setup` | android/skills (Apache-2.0) |
| Wear Compose Material3 UI | `wear-compose-m3` | android/skills (Apache-2.0) |
| Manifest / IPC / runtime permission audit | `android-permissions-security` | android/skills (Apache-2.0) |
| Evidence before "done" claims | `verification-before-completion` | obra/superpowers (MIT) |
| AGP 9 migration (built-in Kotlin, new DSL) | `agp-9-upgrade` | android/skills (Apache-2.0) |
| Intent / exported component / PendingIntent audit | `android-intent-security` | android/skills (Apache-2.0) |
| ANR risk audit, ANR trace triage | `anr-audit-and-fix` | android/skills (Apache-2.0) |
| Wear OS widgets (Glance Wear), only on request | `wear-widgets` | android/skills (Apache-2.0) |
| Root cause before any fix | `systematic-debugging` | obra/superpowers (MIT) |
| Test first for logic changes | `test-driven-development` | obra/superpowers (MIT) |
| Play policy / Data safety audit (**manual**) | `play-policy-insights` | android/skills (Apache-2.0) |
| R8 keep-rule analysis (**manual**) | `r8-analyzer` | android/skills (Apache-2.0) |

Provenance and local changes: `SOURCE.md` in each folder.

Agent skill prefix: **`glucose-for-watch-*`** · Android package **`com.glucoseforwatch.*`** · Gradle **`GlucoseForWatch`** · install **`installGlucoseForWatchDebug`**

## Built-in skills

| Task | Cursor | Claude Code |
|------|--------|-------------|
| PR blocked on CI / comments | `babysit` | `/steward` (user-typed) |
| Split large branch | `split-to-prs` | — (ask the agent to split) |
| Code review of the branch | — | `/code-review`, `/security-review` |

## Hard constraints (active soak)

- Do **not** run `installGlucoseForWatchDebug`, `adb install`, or `adb uninstall` unless explicitly requested.
- During C.7 soak: `stability-gate.ps1 -CheckLogcatOnly` for logcat-only checks.

## Internal IDs (do not rename casually)

- Gradle root: `GlucoseForWatch`
- Package: `com.glucoseforwatch.*`
- Install task: `installGlucoseForWatchDebug`
