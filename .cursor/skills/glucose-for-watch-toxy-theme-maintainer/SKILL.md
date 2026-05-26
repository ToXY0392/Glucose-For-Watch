---
name: glucose-for-watch-toxy-theme-maintainer
description: Keeps ToXY chrome tokens in sync between toxy-ux-kit JSON, exported XML, and Android resources. Use when changing brand colors, accents, or migrating wg7_* aliases.
disable-model-invocation: true
---

# Widget G7 ToXY Theme Maintainer

## Goal
Single source of truth: `toxy-ux-kit/tokens/toxy.color.json` → export → `mobile/src/main/res/values/toxy_colors.xml`.

## Workflow
1. Edit tokens in `toxy-ux-kit/tokens/toxy.color.json`.
2. Run: `wsl python3 toxy-ux-kit/tools/export-android-colors.py`
3. Copy or diff `toxy-ux-kit/tools/export/output/toxy_colors.xml` into `mobile/src/main/res/values/`.
4. Keep `colors.xml` `wg7_*` entries as **aliases** to `toxy_*` (chrome only).
5. Update `wear/.../tile/ToxyTileTheme.kt` if tile chrome constants changed.
6. Bump `toxy-ux-kit/CHANGELOG.md` and tile `RESOURCES_VERSION` if watch surfaces change.

## Rules
- **Never** use ToXY mint (`toxy_accent_*`) on glucose numeric values.
- Glucose colors live in `agp.glucose.json` / `GlucoseRangeResolver`, not ToXY tokens.
- Prefer `@color/toxy_*` in new layouts; legacy `wg7_*` is alias-only.

## Output
- Updated token files and Android XML
- Short note of what changed and whether tile version bump is needed
