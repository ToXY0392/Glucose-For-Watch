---
name: glucose-for-watch-documentation-scope
description: Path scope for documentation and agent-infra work in Glucose For Watch: docs/plan, guides, AGENTS.md, .claude/skills, .cursor rules and scopes. Use when on sandbox/documentation or on a docs/ branch, or before editing docs and agent configuration on any branch.
---

# Glucose For Watch — documentation scope

Branch: **`sandbox/documentation`** (long-lived) or a short-lived `docs/` branch · PR target: **`develop/integration`**

Read [.cursor/workspace-scopes/documentation.scope.md](../../../.cursor/workspace-scopes/documentation.scope.md).

**No published `docs` mirror branch** — documentation lives on `develop/integration` and `main`.

Skills: edit **`.claude/skills/`** only, then run `python3 scripts/dev/sync_skills.py` (regenerates `.cursor/skills/`) and `--lint`.

Before commit: **`glucose-for-watch-repo-hygiene`** · run `python3 scripts/dev/check_docs_links.py`.

QA evidence → `glucose-for-watch-qa-hardware-scope` on a `qa/` branch.
