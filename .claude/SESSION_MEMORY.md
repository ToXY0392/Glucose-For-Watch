# Session memory
_Màj : 2026-11-26 · branche : develop/integration_

## En cours
- Rien.

## Décisions
- 2026-11-26 **Bluetooth / G7 direct en pause** — pourquoi : Phase 0 bloquée (pas de capteur G7 de test isolé). Ne rien proposer sur `wear/.../direct/`, l'arbitre ou l'enveloppe source sans reprise explicite.
- 2026-11-26 Skills : source unique `.claude/skills/`, miroir généré `.cursor/skills/` (`scripts/dev/sync_skills.py`), CI « Verify agent skills » — PR #125, #126. Détails : `docs/plan/SKILLS-PLAN.md`.
- 2026-11-26 Hook PreToolUse : confirmation avant `installGlucoseForWatchDebug` / `adb install` / `adb uninstall` (`.claude/settings.json`).
- 2026-11-26 Plugins caveman / claude-mem non installés — pourquoi : absents du marketplace officiel ; claude-mem tiers exige worker + DB externe.
- 2026-11-26 Rapport consultant : https://claude.ai/artifact/2RjEK1kUCdnG2GEX4pmmfT
- 2026-11-26 Back-merge `main` → `develop/integration` fait (PR #127) ; plus de dérive.
- 2026-11-26 Build `:mobile` réparé : caches `.gradle/`, `.gradle-user*` et `build/` supprimés (référençaient l'ancien chemin `Desktop\THP\Projects`). `:mobile:assembleDebug :wear:assembleDebug` OK.

## Prochaines étapes
1. Compléter `.claude/settings.json` : allowlist lecture seule + tests ciblés (le hook y est déjà).
2. Décider : `DexcomShareClient.readTrend()` renvoie `Flat` si tendance absente.
3. Trier Dependabot #115 (kapt 1.8 → 2.4, majeur) et #114 (constraintlayout patch).

## Pièges connus
- `main` protégée : PR obligatoire, push direct refusé. Ce qui est fusionné sur `main` doit revenir sur `develop/integration` (back-merge), sinon dérive.
- `core.autocrlf=true` : `.gitattributes` force LF pour `.claude/**` — un SKILL.md en CRLF n'est pas lu par Claude Code (`--lint` le signale).
- Chemins longs Windows dans les caches Gradle : `Remove-Item` échoue → `[System.IO.Directory]::Delete("\\?\<chemin>", $true)`.