---
name: glucose-for-watch-run-coordinator
description: Orders recurring execution of Glucose For Watch maintenance skills with locking, cooldown, deduplication, and alert strategy to limit noise.
disable-model-invocation: true
---

# Glucose For Watch Run Coordinator

## Objective
Coordinate automation of all monitoring/maintenance skills without spam.

## Responsibilities
- Execution lock (avoid duplicates)
- Cooldown between runs
- Alert deduplication
- Severity escalation

## Alert policy
- `Critical`: immediate notification + release block if applicable
- `Important`: daily digest
- `Minor`: weekly digest

## Workflow
1. Check previous state (timestamp + alert hash).
2. Choose the pack to run (quick / full / release).
3. Execute target skills in defined order.
4. Aggregate results into a single bulletin.
5. Update automation state.

## Standard packs
- `quick`: vendor-watch + security-bulletin
- `full`: compat-matrix + dependency-advisor + doc-drift + release-notes-curator
- `release`: full + blocker verification

## Rules
- Do not publish the same alert twice without a new signal.
- Always produce a final operational summary.
