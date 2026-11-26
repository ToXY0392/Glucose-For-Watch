# Agent skills — layout, policy and optimisation log

How agent skills are organised for Claude Code and Cursor, and what changed in the 2026-11-26 optimisation (bloc S).

## Layout

| Path | Role |
|------|------|
| [`.claude/skills/`](../../.claude/skills/) | **Canonical source.** Read by Claude Code. Edit skills here only. |
| [`.cursor/skills/`](../../.cursor/skills/) | Generated mirror for Cursor. Never edit by hand. |
| [`scripts/dev/sync_skills.py`](../../scripts/dev/sync_skills.py) | Mirror (`sync_skills.py`), stale-mirror check (`--check`), validation (`--lint`). |
| [`.claude/settings.json`](../../.claude/settings.json) + [`.claude/hooks/`](../../.claude/hooks/) | Claude Code hooks (device-install guard). |
| [`.cursor/rules/`](../../.cursor/rules/) | Cursor rules; their content is summarised in [AGENTS.md](../../AGENTS.md) for Claude Code. |

After changing a skill:

```bash
python3 scripts/dev/sync_skills.py          # regenerate .cursor/skills
python3 scripts/dev/sync_skills.py --lint   # what CI runs
```

CI job **Verify agent skills** (`.github/workflows/ci.yml`) runs `--lint`, `--check` and a smoke test of the guard hook on every push and PR.

## Policy

**Description:** each project skill says what it does, then `Use when …` (lint-enforced). Max 1024 characters.

**Invocation:**

| Auto (agent may load it) | Manual only (`disable-model-invocation: true`) |
|--------------------------|-----------------------------------------------|
| Guards, scopes, PR author/gatekeeper, repo hygiene, sync health, theme, Dependabot triage, third-party skills | Skills that hit the network, GitHub or ADB, or run long: dependency advisor, vendor/security watch, doc drift, doc backlog sync, GitHub project sync, maintenance run, USB handoff |

**Scopes:** only `sandbox/mobile-app` and `sandbox/documentation` lock by branch. Every other scope applies by the paths a short-lived branch touches. `glucose-for-watch-sandbox-guard` routes.

**Hard constraint enforcement:** `installGlucoseForWatchDebug`, `adb install` and `adb uninstall` trigger a Claude Code confirmation prompt (PreToolUse hook). Cursor relies on the rule text.

**Third-party skills:** each one sits in its own folder with the upstream `LICENSE` and a `SOURCE.md` (repo, pinned commit, local modifications, update steps). A "project overrides" block at the top of `SKILL.md` aligns the skill with repo constraints. Licence rules: permissive only (Apache-2.0, MIT). No GPL. CC-BY-SA content is not imported.

## 2026-11-26 optimisation log

| Step | Change | Commit |
|------|--------|--------|
| 1 | Skills moved to `.claude/skills/` + Cursor mirror + sync/lint script; device-install guard hook; `reinstall-apks` rule no longer contradicts the soak constraint | `5a9f0e2` |
| 2 | 26 → 21 project skills: `vendor-security-watch` (vendor-watch + security-bulletin + release-notes-curator), `maintenance-run` (run-coordinator + 2 session-start skills), compat matrix folded into `dependency-advisor` | `d6fee7b` |
| 3 | Descriptions with "Use when"; 16 auto / 5 manual project skills; path-based scopes; `sandbox-guard` rewritten; `pr-author` split into `references/` and its diff base fixed | `87acd67` |
| 4 | Imported `android-testing-setup`, `wear-compose-m3`, `android-permissions-security` (android/skills) and `verification-before-completion` (obra/superpowers), pinned and annotated | `7b09c91` |
| 5 | CI job, AGENTS.md catalogue, doc references, this page | this PR |

### Removed skill names → replacement

| Old | New |
|-----|-----|
| `glucose-for-watch-vendor-watch` | `glucose-for-watch-vendor-security-watch` (mode `vendor`) |
| `glucose-for-watch-security-bulletin` | `glucose-for-watch-vendor-security-watch` (mode `security`) |
| `glucose-for-watch-release-notes-curator` | `glucose-for-watch-vendor-security-watch` (mode `changelog`) |
| `glucose-for-watch-run-coordinator` | `glucose-for-watch-maintenance-run` |
| `glucose-for-watch-session-start-quick-check` | `glucose-for-watch-maintenance-run` (pack `quick`) |
| `glucose-for-watch-session-start-deferred-maintenance` | `glucose-for-watch-maintenance-run` (pack `full`) |
| `glucose-for-watch-compat-matrix-maintainer` | `glucose-for-watch-dependency-advisor` |

## Open follow-ups

- **Trigger evals:** measure whether skills fire on realistic prompts (for example with Anthropic's `skill-creator` eval loop). Not done yet.
- **Cursor maintenance hooks** (`.cursor/hooks/*.ps1`) only report target-file ages; they do not run the skills. Either make them real or drop them.
- **Upstream refresh:** check the pinned commits of third-party skills quarterly (procedure in each `SOURCE.md`).
- Candidates not imported yet: `wear/wear-widgets` (only if Tiles move to Glance Wear), `performance/anr-audit-and-fix`, `security/android-intent-security` (android/skills).
