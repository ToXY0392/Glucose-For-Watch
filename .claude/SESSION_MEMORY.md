# Session memory
_Màj : 2026-11-26 · branche : chore/bloc-hygiene-config_

## En cours
- Hygiène config Claude (import `@AGENTS.md`, cette mémoire) — branche `chore/bloc-hygiene-config` (base `origin/main`)
- Log glycémie en clair `ComplicationWakeRefresh.kt:21` — branche `fix/bloc-redact-complication-log` (base `origin/develop/integration`)

## Décisions
- 2026-11-26 **Bluetooth / G7 direct en pause** — pourquoi : Phase 0 bloquée (pas de capteur G7 de test isolé). Ne rien proposer sur `wear/.../direct/`, l'arbitre ou l'enveloppe source sans reprise explicite.
- 2026-11-26 Config Claude (`CLAUDE.md`, `.claude/`) mergée sur `main` via PR #122.
- 2026-11-26 Plugins caveman / claude-mem non installés — pourquoi : absents du marketplace officiel ; claude-mem tiers exige worker + DB externe.
- 2026-11-26 Rapport consultant : https://claude.ai/artifact/2RjEK1kUCdnG2GEX4pmmfT

## Prochaines étapes
1. Réparer `:mobile:assembleDebug` (cache Gradle de l'ancien chemin `Desktop\THP\Projects`) — supprime des caches, accord requis.
2. Ajouter `.claude/settings.json` (allowlist lecture seule + tests ciblés).
3. Retirer `tmp_launch_check.png` (commité à la racine).
4. Décider : `DexcomShareClient.readTrend()` renvoie `Flat` si tendance absente.

## Pièges connus
- `develop/integration` a 24 commits de retard sur `main` (dont `CLAUDE.md`) au 2026-11-26.
- `main` protégée : PR obligatoire, push direct refusé.
- 9 dossiers `.gradle-user*` + `build/` à la racine (≥ 1,1 Go) → exclure des recherches.
