# Source — play-policy-insights

| Field | Value |
|-------|-------|
| Upstream | [android/skills](https://github.com/android/skills/tree/7f7b21730a605d112ca9f79b6e40105ce7b670ee/play/play-policy-insights) · `play/play-policy-insights` |
| Pinned commit | `7f7b21730a605d112ca9f79b6e40105ce7b670ee` |
| Licence | Apache-2.0 (file in this folder) |
| Imported | 2026-11-26 · reviewed: SKILL.md read in full; scripts scanned for network, exec and delete calls |

## Local modifications

- inserted the "project overrides" block after the frontmatter
- added `disable-model-invocation: true` (manual only: network or long run)

## Updating

Re-fetch the folder at a new commit, re-read SKILL.md, re-apply the modifications above, bump the pinned commit here, then run `python3 scripts/dev/sync_skills.py` and `--lint`.
