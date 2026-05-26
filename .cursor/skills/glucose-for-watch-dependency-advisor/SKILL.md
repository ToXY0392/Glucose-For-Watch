---
name: glucose-for-watch-dependency-advisor
description: Analyzes AGP, Gradle, Kotlin, and dependency versions in the Widget G7 repo, then proposes a safe upgrade strategy with a small-PR plan and rollback.
disable-model-invocation: true
---

# Widget G7 Dependency Advisor

## Objective
Plan stable, auditable version upgrades.

## Inputs to analyze
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`
- `**/build.gradle.kts` modules

## Workflow
1. Build the current state of critical versions (AGP/Gradle/Kotlin/libs).
2. Evaluate tooling compatibility (Android Studio, JDK, AGP, Gradle, Kotlin).
3. Propose a low-risk upgrade order:
   1) Kotlin
   2) AGP
   3) Gradle wrapper
   4) application libraries
4. Define a rollback plan per step.
5. Generate a small-PR plan (one theme per PR).

## Expected output
- **Current state**
- **Recommended target**
- **Migration plan by PR**
- **Rollback**
- **Validation tests per PR**

## Rules
- Give the reason for each upgrade.
- Avoid massive version jumps.
- Prioritize build compatibility over optimizations.
