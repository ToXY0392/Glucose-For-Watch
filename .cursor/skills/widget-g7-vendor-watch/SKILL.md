---
name: widget-g7-vendor-watch
description: Scans Android, Wear OS, AGP, Gradle, Kotlin, and Dexcom updates, then classifies real impact for Widget G7 into immediate, planned, or ignored actions.
disable-model-invocation: true
---

# Widget G7 Vendor Watch

## Objective
Provide actionable vendor monitoring for the Widget G7 repo.

## Sources to monitor
- Android Developers (Android + Jetpack)
- Wear OS documentation
- Android Gradle Plugin release notes
- Gradle release notes
- Kotlin release notes
- Dexcom Share documentation

## Repo inputs to read
- `README.md`
- `docs/dev/setup.md`
- `build.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`

## Workflow
1. Record current repo versions and assumptions.
2. Collect vendor updates since the last run (delta mode).
3. Identify only real impacts on Widget G7:
   - breaking changes
   - deprecations
   - min SDK / tooling compatibility
   - API behavior changes
4. Produce a sorted bulletin with:
   - `Do now`
   - `Plan`
   - `Ignore`

## Output format
- **Executive summary** (3-5 lines)
- **Do now** (impact + action + urgency)
- **Plan** (recommended window)
- **Ignore** (explicit reason)
- **Sources consulted** (links)

## Rules
- Do not list updates without concrete impact on the repo.
- Cite a source for each important point.
- Prefer minimal, reversible actions.
