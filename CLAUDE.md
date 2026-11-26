# CLAUDE.md — Glucose For Watch

Guide complet agents (branches, sandboxes, contraintes soak), importé ici car Claude Code ne le charge pas seul :

@AGENTS.md

Ce fichier le complète, ne le remplace pas.

## Architecture (court)

Dexcom Share (téléphone) → `GlucoseSyncEngine` → Wear Data Layer `/glucose/latest` → montre (cache, tile, complication) → ack `/glucose/watch/ack`.
Détails : [docs/dev/architecture.md](docs/dev/architecture.md).

| Module | Rôle |
|--------|------|
| `:mobile` | UI téléphone, fetch Dexcom, orchestration sync |
| `:wear` | Cache, tile, complication, listener Data Layer |
| `:core:model` / `:core:datalayer-contract` / `:core:testing` | Modèles, contrat Data Layer, fixtures |
| `:feature:sync` / `:feature:dexcom-share` / `:feature:watch-install` | Moteur sync, client HTTP Dexcom, install APK wear |

Stack : Kotlin 2.3.20 · AGP 9.3.3 · Gradle 9.6.1 · JBR 21 · compileSdk 36 · Compose.

## Commandes (PowerShell, racine)

```powershell
# Build
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug

# Tests ciblés (préférer au `test` global)
.\gradlew.bat :feature:sync:testDebugUnitTest
.\gradlew.bat :feature:dexcom-share:testDebugUnitTest
.\gradlew.bat :mobile:testDebugUnitTest --tests "com.glucoseforwatch.mobile.<Classe>"
.\gradlew.bat :core:model:testDebugUnitTest

# Tous les tests (seulement avant PR)
.\gradlew.bat test

# Liens docs
py -3 scripts/dev/check_docs_links.py
```

Interdit sans demande explicite : `installGlucoseForWatchDebug`, `adb install`, `adb uninstall`.

## Règles de rigueur (Karpathy)

- Zéro sur-ingénierie : solution minimale qui résout la demande. Pas d'abstraction, d'option ou de refacto « au cas où ».
- Ne modifier que les fichiers strictement nécessaires à la demande. Pas de reformatage ni de renommage collatéral.
- Ne jamais inventer : API, classes, chemins, valeurs glucose, résultats de test. Lire le code avant d'affirmer ; dire « inconnu » sinon.
- Édition touchant **> 3 fichiers** → présenter la liste des fichiers + plan, attendre validation.
- Vérifier : lancer le test ciblé du module modifié ; rapporter le résultat réel.

## Style de réponse (Caveman)

- Télégraphique. Pas d'intro, pas de récap redondant, pas de politesse.
- Faits → action → résultat. Phrases courtes, listes.
- Code : complet, propre, compilable. Jamais de `// ...reste inchangé` dans un fichier livré.
- Modifs : diffs ciblés (Edit), pas de réécriture intégrale de fichier.
- Références : `chemin/fichier.kt:ligne`.

## Skills projet

`/economie` · `/plan-et-execution` · `/memoire-projet` · `/revue-et-git`
Mémoire de session : [.claude/SESSION_MEMORY.md](.claude/SESSION_MEMORY.md) — lire en début de session.
