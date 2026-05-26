---
name: glucose-for-watch-ui-ux-kit-scope
description: >-
  Scope-limited work on sandbox/ui-ux-kit — toxy-ux-kit tokens, specs,
  design-reference only. Use when checked out on sandbox/ui-ux-kit or when
  editing ToXY kit paths. Refuses mobile/wear/feature changes unless user
  explicitly requests Android color export.
disable-model-invocation: true
---

# Widget G7 UX Kit Scope

## Branch

Expected: **`sandbox/ui-ux-kit`**. If on another branch, warn and confirm before editing kit paths.

Scope reference: [.cursor/workspace-scopes/ui-ux-kit.scope.md](../../workspace-scopes/ui-ux-kit.scope.md)

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
