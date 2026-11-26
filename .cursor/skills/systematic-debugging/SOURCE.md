# Source — systematic-debugging

| Field | Value |
|-------|-------|
| Upstream | [obra/superpowers](https://github.com/obra/superpowers/tree/22f0b463e7a5e579904dd6211a520321035526f7/skills/systematic-debugging) · `skills/systematic-debugging` |
| Pinned commit | `22f0b463e7a5e579904dd6211a520321035526f7` |
| Licence | MIT (file in this folder) |
| Imported | 2026-11-26 · reviewed: SKILL.md read in full; scripts scanned for network, exec and delete calls |

## Local modifications

- inserted the "project overrides" block after the frontmatter
- removed JavaScript-only helpers: `find-polluter.sh`, `condition-based-waiting-example.ts`

## Updating

Re-fetch the folder at a new commit, re-read SKILL.md, re-apply the modifications above, bump the pinned commit here, then run `python3 scripts/dev/sync_skills.py` and `--lint`.
