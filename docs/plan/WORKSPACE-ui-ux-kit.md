# Workspace scope — ui-ux-kit

| Field | Value |
|-------|-------|
| **Branch** | No permanent branch; use a short-lived `feat/bloc-*` or `docs/bloc-*` branch |
| **Status** | Available on demand |
| **Skill** | `glucose-for-watch-ui-ux-kit-scope` |
| **Scope file** | [.cursor/workspace-scopes/ui-ux-kit.scope.md](../../.cursor/workspace-scopes/ui-ux-kit.scope.md) |

## Allowed paths

- `toxy-ux-kit/**`
- `docs/design/**`

## Backlog (post-tag v0.5.0)

| # | ID | Task | Est. |
|---|-----|------|------|
| 1 | M.4 / DOC-P1-3 | design-reference PNG + gallery | 2h |
| 2 | AUTO-2 | Static preview gallery HTML | 6h |

Not required for G-M7 gate (previews + hero/tile parity already ✅).

## Verify

```bash
python3 toxy-ux-kit/tools/tokens-validate.py
python3 toxy-ux-kit/tools/export-design-reference.py
```

## Android export

Only on explicit request → `@glucose-for-watch-toxy-theme-maintainer`

Start temporary work branches from `develop/integration`; the scope and validation tools remain available without a permanent sandbox branch.
