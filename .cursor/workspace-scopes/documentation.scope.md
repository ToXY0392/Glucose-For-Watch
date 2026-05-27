# Scope — sandbox/documentation

| Field | Value |
|-------|-------|
| **Branch** | `sandbox/documentation` |
| **Status** | active |
| **Skill** | `glucose-for-watch-documentation-scope` |

## Allowed paths

- `docs/plan/**`, `docs/dev/**`, `docs/guide/**`, `docs/legal/**`, `docs/index.md`
- `AGENTS.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `README.md`, `SECURITY.md`
- `.cursor/skills/**`, `.cursor/rules/**`, `.cursor/workspace-scopes/**`

## Routage

- `docs/qa/**`, `scripts/qa/**` → `sandbox/qa-hardware`
- `toxy-ux-kit/**` → `sandbox/ui-ux-kit`
- app code → owning sandbox

## Verify

```bash
python3 scripts/dev/check_docs_links.py
```
