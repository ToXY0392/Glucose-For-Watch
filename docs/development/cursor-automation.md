# Cursor automation

> **Last updated:** 2026-05-23

Project-specific Cursor skills and hooks for vendor watch, documentation maintenance, and session automation.

---

## Skills location

`.cursor/skills/` — each skill has a `SKILL.md` with `disable-model-invocation: true` (explicit invocation only).

---

## Available skills

### Session start

| Skill | Purpose |
|-------|---------|
| `widget-g7-session-start-quick-check` | Quick vendor + security delta (6h cooldown) |
| `widget-g7-session-start-deferred-maintenance` | Full maintenance deferred 180s (24h cooldown) |
| `widget-g7-run-coordinator` | Lock, cooldown, deduplication |
| `widget-g7-usb-detach-handoff-writer` | USB detach → incident in developer handoff |

### Continuous maintenance

| Skill | Purpose |
|-------|---------|
| `widget-g7-vendor-watch` | Android / Wear / AGP / Gradle / Kotlin / Dexcom watch |
| `widget-g7-security-bulletin` | CVE and security advisories |
| `widget-g7-compat-matrix-maintainer` | Maintains compatibility docs |
| `widget-g7-dependency-advisor` | Safe upgrade strategy |
| `widget-g7-doc-drift-checker` | Detects obsolete documentation |
| `widget-g7-release-notes-curator` | Updates release notes |

### Planned (refactor)

| Skill | Purpose |
|-------|---------|
| `widget-g7-toxy-theme-maintainer` | ToXY token consistency |
| `widget-g7-sync-health-reviewer` | Sync / tile change review |
| `widget-g7-agp-color-guard` | Blocks brand colors on glucose values |
| `widget-g7-tile-ux-checker` | Wear tile guideline compliance |
| `widget-g7-dexcom-compat-validator` | G6/G7 Share checklist |

---

## Recommended cadence

| Frequency | Skills |
|-----------|--------|
| Every 6h | `session-start-quick-check` |
| Daily | `vendor-watch`, `security-bulletin` |
| Twice weekly | `compat-matrix-maintainer` |
| Weekly | `dependency-advisor`, `doc-drift-checker` |
| Pre-release | `release-notes-curator` |

---

## Hooks

Configuration: `.cursor/hooks.json`

| Script | Behavior |
|--------|----------|
| `session_start_quick_check.ps1` | Quick check on session start |
| `session_start_deferred_maintenance.ps1` | Deferred full maintenance |
| `run_full_maintenance.ps1` | Consolidated maintenance report |
| `usb_detach_handoff.ps1` | USB disconnect detection |
| `start_usb_monitor.ps1` | Background USB monitor |
| `usb_monitor_loop.ps1` | Periodic USB check (5 min) |

### State files

- `.cursor/state/run_state.json` — last run timestamps
- `.cursor/state/usb-state.json` — USB dedupe state
- `.cursor/state/reports/` — quick-check and maintenance reports

---

## Alert policy

| Severity | Action |
|----------|--------|
| Critical | Immediate |
| Important | Daily digest |
| Minor | Weekly digest |

---

## Related

- [Developer handoff](developer-handoff.md)
- [Master refactor plan](../plan/MASTER-REFACTOR-PLAN.md) — skills roadmap
