# Session memory
_Màj : 2026-11-26 · branche : chore/bloc-s-backmerge-main_

## En cours
- Back-merge `main` → `develop/integration` + renommage des 4 skills Claude (préfixe `glucose-for-watch-`) — branche `chore/bloc-s-backmerge-main`

## Décisions
- 2026-11-26 **Bluetooth / G7 direct en pause** — pourquoi : Phase 0 bloquée (pas de capteur G7 de test isolé). Ne rien proposer sur `wear/.../direct/`, l'arbitre ou l'enveloppe source sans reprise explicite.
- 2026-11-26 Skills : source unique `.claude/skills/`, miroir généré `.cursor/skills/` (`scripts/dev/sync_skills.py`), CI « Verify agent skills » — PR #125, #126. Détails : `docs/plan/SKILLS-PLAN.md`.
- 2026-11-26 Hook PreToolUse : confirmation avant `installGlucoseForWatchDebug` / `adb install` / `adb uninstall` (`.claude/settings.json`).
- 2026-11-26 Plugins caveman / claude-mem non installés — pourquoi : absents du marketplace officiel ; claude-mem tiers exige worker + DB externe.
- 2026-11-26 Rapport consultant : https://claude.ai/artifact/2RjEK1kUCdnG2GEX4pmmfT

## Prochaines étapes
1. Réparer `:mobile:assembleDebug` (cache Gradle de l'ancien chemin `Desktop\THP\Projects`) — supprime des caches, accord requis.
2. Compléter `.claude/settings.json` : allowlist lecture seule + tests ciblés (le hook y est déjà).
3. Retirer `tmp_launch_check.png` (commité à la racine).
4. Décider : `DexcomShareClient.readTrend()` renvoie `Flat` si tendance absente.

## Pièges connus
- `main` protégée : PR obligatoire, push direct refusé. Ce qui est fusionné sur `main` doit revenir sur `develop/integration` (back-merge), sinon dérive.
- `core.autocrlf=true` : `.gitattributes` force LF pour `.claude/**` — un SKILL.md en CRLF n'est pas lu par Claude Code (`--lint` le signale).
- 9 dossiers `.gradle-user*` + `build/` à la racine (≥ 1,1 Go) → exclure des recherches.
