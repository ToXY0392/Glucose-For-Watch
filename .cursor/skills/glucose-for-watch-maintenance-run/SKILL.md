---
name: glucose-for-watch-maintenance-run
description: Orchestrates recurring Glucose For Watch maintenance packs (quick, full, release) with cooldown, deduplication and one aggregated bulletin. Use when the user asks for a maintenance run, a session-start check, a weekly or pre-release sweep, or schedules one with /loop or /schedule.
disable-model-invocation: true
---

# Glucose For Watch Maintenance Run

Replaces `run-coordinator`, `session-start-quick-check` and `session-start-deferred-maintenance`.

## Packs

| Pack | Skills, in order | Cooldown |
|------|------------------|----------|
| `quick` | `glucose-for-watch-vendor-security-watch` (mode `delta`, vendor + security) | 6 h |
| `full` | `glucose-for-watch-dependency-advisor` (incl. compat matrix) → `glucose-for-watch-doc-drift-checker` → `glucose-for-watch-vendor-security-watch` (mode `changelog`) | 24 h |
| `release` | `full` + `glucose-for-watch-pr-gatekeeper` blocker check | none |

## How it is triggered

- **Cursor:** `.cursor/hooks.json` session-start hooks write trigger reports to `.cursor/state/reports/` (gitignored); the agent then runs the matching pack.
- **Claude Code:** run on demand (`/glucose-for-watch-maintenance-run quick`), or schedule it (`/loop`, `/schedule`). No session-start automation.

## Workflow

1. Read `.cursor/state/run_state.json` if present (last run time per pack, last alert hashes). Missing file = first run.
2. Skip the pack if its cooldown has not elapsed, unless the user forces it.
3. Run the pack's skills in order; stop on a `Critical` blocker in `release`.
4. Aggregate into **one** bulletin and drop alerts whose hash was already reported without a new signal.
5. Update `run_state.json` (stays local; never commit `.cursor/state/*`).

## Alert policy

- `Critical` → report immediately; blocks a release.
- `Important` → daily digest.
- `Minor` → weekly digest.

## Output

- Pack run · skipped (cooldown) or executed
- Blockers · priority actions · planned actions
- New vs already-known alerts

## Rules

- Stay non-blocking: never hold up the user's main task.
- Read-only by default; edits only through the sub-skill's own authorisation rules.
