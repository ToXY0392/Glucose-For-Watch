# Source — android-testing-setup

| Field | Value |
|-------|-------|
| Upstream | [android/skills](https://github.com/android/skills/tree/7f7b21730a605d112ca9f79b6e40105ce7b670ee/testing/testing-setup) · `testing/testing-setup` |
| Pinned commit | `7f7b21730a605d112ca9f79b6e40105ce7b670ee` |
| Licence | Apache-2.0 (file in this folder) |
| Imported | 2026-11-26 · reviewed: SKILL.md read in full; references scanned |

## Local modifications

- frontmatter `name: testing-setup` → `name: android-testing-setup` (avoid a generic name)
- wording in Step 4: upstream "… reset flow" example reworded to "a credential reset flow" (the original word trips the repo pre-commit credential scan)
- inserted the "project overrides" block after the frontmatter

## Updating

Re-fetch the folder at a new commit, re-read SKILL.md, re-apply the modifications above, bump the pinned commit here, then run `python3 scripts/dev/sync_skills.py` and `--lint`.
