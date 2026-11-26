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
- 2026-11-26 Allowlist Claude Code (`.claude/settings.json`) : git/gh lecture seule + `:<module>:testDebugUnitTest` — PR #129. `git branch` exclu (permet `-D`).
- 2026-11-26 Tendance Dexcom absente / NotComputable / RateOutOfRange / inconnue → `NONE` (pas de flèche montre, « tendance indisponible » téléphone), plus jamais `FLAT` — pourquoi : fausse flèche « stable ». PR #130.
- 2026-11-26 Dependabot : #114 (constraintlayout 2.2.2) fusionnée ; #115 (kapt 2.4.21) fermée — plugin kapt déclaré `apply false`, jamais appliqué ; sa version doit suivre Kotlin.

## Prochaines étapes
1. Optionnel : retirer la déclaration kapt inutilisée de `build.gradle.kts` (évite de futures PR Dependabot).
2. Optionnel : `target-branch: develop/integration` dans `.github/dependabot.yml` pour éviter les back-merges.

## Pièges connus
- `main` protégée : PR obligatoire, push direct refusé. Ce qui est fusionné sur `main` doit revenir sur `develop/integration` (back-merge), sinon dérive.
- `core.autocrlf=true` : `.gitattributes` force LF pour `.claude/**` — un SKILL.md en CRLF n'est pas lu par Claude Code (`--lint` le signale).
- PowerShell 5.1 + `git commit -m` / `gh --body` avec guillemets internes : arguments cassés → passer par `-F` / `--body-file` (fichier UTF-8 **sans BOM**, sinon BOM dans le titre du commit).
- Chemins longs Windows dans les caches Gradle : `Remove-Item` échoue → `[System.IO.Directory]::Delete("\\?\<chemin>", $true)`.