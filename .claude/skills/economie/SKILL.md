---
name: economie
description: Économie de quotas/tokens — routage de modèle léger, lectures ciblées, pas de scans globaux, /compact ou /clear entre tâches. Utiliser en début de tâche ou quand le contexte grossit.
---

# Économie de quotas

## Routage modèle

| Tâche | Modèle |
|-------|--------|
| Défaut (code, debug, édition) | **Sonnet** |
| Recherche simple, lecture, grep, résumé de fichier | **Haiku** (subagent `Explore` / `model: "haiku"`) |
| Architecture complexe, raisonnement lourd | **Opus — uniquement sur demande explicite** |

Changer de modèle : `/model sonnet`. Ne pas lancer de subagent Opus de sa propre initiative.

## Lecture ciblée

- Interdit : `find .` / `ls -R` / Grep sans `path` sur toute la racine (dossiers `.gradle-user*`, `build/`, `.venv/`, `.idea/` = bruit massif).
- Cibler : `path` = module concerné (`mobile/src/main`, `feature/sync/src`…), `glob` = `*.kt`.
- Lire avec `offset`/`limit` quand la zone est connue. Ne pas relire un fichier déjà lu.
- Ne pas lire `build/`, APK, logs complets ; filtrer (`Select-String`, `-Tail`).
- Gradle : tâche ciblée du module (`:feature:sync:testDebugUnitTest`), jamais `test` global en itération.

## Contexte

- Fin de tâche → mettre à jour `.claude/SESSION_MEMORY.md` (skill `memoire-projet`), puis suggérer `/clear`.
- Même tâche mais contexte long → suggérer `/compact`.
- Sorties d'outils volumineuses → résumer, ne pas recopier.
