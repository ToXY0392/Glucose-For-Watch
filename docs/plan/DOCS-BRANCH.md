# Docs-only branch — Glucose For Watch

> **`docs`** branch · auto-synced documentation mirror · no app source code.

---

## Purpose

The [`docs`](https://github.com/ToXY0392/Glucose-For-Watch/tree/docs) branch contains **documentation only** — readable on GitHub without Gradle, mobile, or wear modules.

Use cases:

- Share doc links with collaborators who only need guides/specs
- Browse UX kit + plan docs in a lightweight tree
- Optional GitHub Pages source (future)

**Do not edit the `docs` branch directly.** Changes are overwritten on the next sync.

---

## What is published

Paths listed in [`scripts/dev/docs_branch_manifest.txt`](../../scripts/dev/docs_branch_manifest.txt):

| Area | Paths |
|------|-------|
| Project docs | `docs/` |
| Root guides | `README.md`, `CONTRIBUTING.md`, `AGENTS.md`, `CHANGELOG.md`, `SECURITY.md`, `LICENSE` |
| UX kit | `toxy-ux-kit/` (specs, tokens, design-reference — not Python tools) |
| GitHub templates | `.github/ISSUE_TEMPLATE/`, PR template |
| Agent skills | `.cursor/skills/` |

---

## Automation

**Workflow:** [`.github/workflows/sync-docs-branch.yml`](../../.github/workflows/sync-docs-branch.yml)

| Trigger | Behavior |
|---------|----------|
| Push to **`integrate`**, **`main`**, or **any feature branch** | Sync when a published path changes |
| Push to **`docs`** | Ignored (no loop) |
| **Manual** | Actions → *Sync docs branch* → optional `source_ref` (default `integrate`) |

Each run:

1. Exports manifest paths from the **source commit**
2. Rebuilds branch **`docs`** (orphan-style full replace)
3. Writes root `README.md` with source SHA + sync time
4. Commits + pushes if diff

---

## Manual publish (local / WSL)

```bash
# From integrate (or any branch) after doc edits
git checkout integrate
bash scripts/dev/publish_docs_branch.sh

# Dry run
DRY_RUN=1 bash scripts/dev/publish_docs_branch.sh

# Commit locally without push
SKIP_PUSH=1 bash scripts/dev/publish_docs_branch.sh
```

Requires push access to `origin/docs`.

---

## Canonical source

| Branch | Role |
|--------|------|
| **`integrate`** | Daily doc edits (preferred source) |
| **`main`** | Release-tagged doc snapshot |
| **`docs`** | Published mirror (read-only for humans) |

After merging doc PRs into `integrate`, CI updates `docs` within minutes.

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| Workflow did not run | Confirm changed files match workflow `paths` filter |
| Stale `docs` branch | Actions → *Sync docs branch* → Run workflow → `source_ref: integrate` |
| Missing path on `docs` | Add to `docs_branch_manifest.txt` · merge to `integrate` |
| Permission denied on push | Workflow needs `contents: write` (configured) |

---

*See also: [DOC-BACKLOG.md](DOC-BACKLOG.md) · [GITHUB-SETUP.md](GITHUB-SETUP.md)*
