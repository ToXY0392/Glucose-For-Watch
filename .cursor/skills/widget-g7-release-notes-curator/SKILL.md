---
name: widget-g7-release-notes-curator
description: Aggregates official release notes from Android, Wear OS, AGP, Gradle, Kotlin, and Dexcom vendors, then extracts points applicable to Widget G7 to pre-fill CHANGELOG.md.
disable-model-invocation: true
---

# Widget G7 Release Notes Curator

## Objective
Maintain a useful changelog aligned with upstream changes.

## Primary target file
- `CHANGELOG.md`

## Workflow
1. Collect recent release notes from monitored vendors.
2. Keep only points applicable to the repo.
3. Propose a concise `Upstream changes` section:
   - technical impact
   - product impact
   - recommended action
4. If editing is authorized, update `CHANGELOG.md`.

## Section format
- `Upstream changes`
  - source
  - change
  - Widget G7 impact
  - action

## Rules
- Avoid noise: ignore changes with no effect on the project.
- Keep a readable style for the team.
- Preserve existing history in the file.
