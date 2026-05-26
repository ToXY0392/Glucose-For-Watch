---
name: glucose-for-watch-compat-matrix-maintainer
description: Maintains the Android Studio, JDK, AGP, Gradle, Kotlin, and Wear OS compatibility matrix for Widget G7, flags risky combinations, and updates docs/dev/setup.md.
disable-model-invocation: true
---

# Widget G7 Compat Matrix Maintainer

## Objective
Keep a reliable compatibility matrix for build and development.

## Inputs
- `docs/dev/setup.md`
- `README.md`
- `build.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`

## Workflow
1. Read effective repo versions.
2. Verify official tooling compatibility.
3. Update combinations with status:
   - `Valid`
   - `Risk`
   - `Unsupported`
4. Add a clear recommendation for each `Risk` status.
5. Update `docs/dev/setup.md` if editing is authorized.

## Expected output
- **Updated matrix**
- **Risky combinations** with mitigation
- **Patch applied**: yes/no
