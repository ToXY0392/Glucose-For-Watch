---
name: glucose-for-watch-doc-drift-checker
description: Compares Glucose For Watch internal documentation against recent official Android, Wear OS, AGP, Gradle and Kotlin docs, reports obsolescence by priority and applies doc patches when editing is authorised. Use when running the monthly doc drift check, after a toolchain upgrade, or when the user suspects docs are outdated.
disable-model-invocation: true
---

# Glucose For Watch Doc Drift Checker

## Objective
Keep project documentation aligned with vendor recommendations.

## Scope
- `README.md`
- `docs/index.md`
- `docs/plan/DOC-BACKLOG.md`
- `docs/dev/setup.md`
- `docs/dev/architecture.md`
- `CHANGELOG.md`

## Workflow
1. Inventory versions, commands, and recommendations present in internal docs.
2. Check recent official sources (Android/Wear/AGP/Gradle/Kotlin/Dexcom).
3. Classify detected gaps:
   - `Critical` (incorrect or breaking information)
   - `Important` (significant obsolescence)
   - `Minor` (wording, links, precision)
4. Propose a prioritized patch file by file.
5. If the mode allows edits, apply doc patches (Critical then Important priority).
6. Verify cross-consistency between `README.md` and `docs/*`.

## Output format
- **Obsolescence report** (Critical / Important / Minor)
- **Patch plan** (file, section, justification, source)
- **Execution**: `Applied: yes/no`

## Rules
- Do not invent versions or recommendations.
- Modify only documentation in this skill.
- Respect Windows/WSL project constraints described in `docs/dev/setup.md`.
