# Scope — sandbox/ui-ux-kit

| Field | Value |
|-------|-------|
| **Branch** | `sandbox/ui-ux-kit` |
| **Status** | dormant (until v0.5.0 tag) |
| **Skill** | `glucose-for-watch-ui-ux-kit-scope` |

## Allowed paths

- `toxy-ux-kit/**`
- `docs/design/**`

## Read-only

- `mobile/src/main/res/values/toxy_colors.xml` (consume exports only)
- `mobile/src/main/res/values/agp_glucose_colors.xml`

## Forbidden (unless user explicitly requests Android export)

- `mobile/**` (except exported color XML above)
- `wear/**`
- `feature/**`, `core/**`
- `.github/**`, root Gradle files

## Verify

```bash
python3 toxy-ux-kit/tools/tokens-validate.py
python3 toxy-ux-kit/tools/lint-agp-colors.py
```

## Backlog (post-tag v0.5.0)

1. M.4 / DOC-P1-3 — design-reference PNG gallery
2. AUTO-2 — static preview gallery HTML

## Security

- Never commit real glucose values in design-reference assets
- See [SECURITY.md](../../SECURITY.md) and `glucose-for-watch-repo-hygiene`
