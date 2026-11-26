---
name: glucose-for-watch-ui-ux-kit-scope
description: Path scope for ToXY design kit work in Glucose For Watch: toxy-ux-kit tokens, specs and docs/design only; mobile, wear and feature edits refused unless the user asks for an Android color export. Use when a change touches toxy-ux-kit/ or docs/design/.
---

# Glucose For Watch UX Kit Scope

## Activation

Path-based: applies on any short-lived branch (`feat/`, `fix/`, `qa/`, `test/`, …) whose changes touch the allowed paths below. `sandbox/ui-ux-kit` is dormant — do not recreate it without an explicit decision ([WORKSPACE.md](../../../docs/plan/WORKSPACE.md)). PR target: **`develop/integration`**.

Scope reference: [.cursor/workspace-scopes/ui-ux-kit.scope.md](../../../.cursor/workspace-scopes/ui-ux-kit.scope.md)

## Allowed

- `toxy-ux-kit/**`
- `docs/design/**`

## Forbidden (hard block)

- `mobile/**`, `wear/**`, `feature/**`, `core/**`
- `.github/**`, Gradle root changes

## Android export

Only when user explicitly asks: invoke `glucose-for-watch-toxy-theme-maintainer`.

## Verify

```bash
python3 toxy-ux-kit/tools/tokens-validate.py
```

## Security (inherit)

- Run `glucose-for-watch-repo-hygiene` before commit
- Never commit credentials, real glucose, `local.properties`, keystores
- See [SECURITY.md](../../../SECURITY.md)

## Output

List files touched · validation command result · whether tile/mobile export is needed
