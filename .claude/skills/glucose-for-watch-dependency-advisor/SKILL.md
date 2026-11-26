---
name: glucose-for-watch-dependency-advisor
description: Analyzes AGP, Gradle, Kotlin, JDK, Android Studio and library versions in Glucose For Watch, maintains the compatibility matrix in docs/dev/setup.md, and proposes a safe small-PR upgrade plan with rollback. Use when planning a dependency or toolchain upgrade, before merging a major Dependabot bump, or when checking whether a tooling combination is supported.
disable-model-invocation: true
---

# Glucose For Watch Dependency Advisor

Also covers the former `compat-matrix-maintainer`.

## Inputs

- `build.gradle.kts`, `settings.gradle.kts`, `**/build.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`
- `docs/dev/setup.md` (compatibility matrix), `README.md`

## Step 1 — Current state

Record the effective versions of AGP, Gradle, Kotlin, JDK, compile/target/min SDK and critical libraries (Wear Compose, Tiles, watchface).

## Step 2 — Compatibility matrix

Check each combination against official compatibility tables and mark it:

| Status | Meaning |
|--------|---------|
| `Valid` | Officially supported |
| `Risk` | Works but outside the documented range — add a mitigation |
| `Unsupported` | Must change before release |

Update the matrix in `docs/dev/setup.md` only if the user authorises editing.

## Step 3 — Upgrade plan

Low-risk order, one theme per PR (`chore/deps-*` branch):

1. Kotlin
2. AGP
3. Gradle wrapper
4. Application libraries

For each step: reason · target version · validation (`bash scripts/dev/verify_ci.sh`, module tests) · rollback (revert the single PR).

## Output

- **Current state** · **Matrix** (with `Risk`/`Unsupported` mitigations) · **Recommended target**
- **Migration plan by PR** · **Validation per PR** · **Rollback**
- Matrix patch applied: yes / no

## Rules

- Give the reason for each upgrade; avoid large version jumps.
- Build compatibility first, optimisations later.
- Never mix an upgrade with feature bloc work.
