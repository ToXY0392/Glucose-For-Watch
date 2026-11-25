---
name: plan-et-execution
description: Workflow en 2 phases strictes — plan court (3-5 points) validé par l'utilisateur avant toute édition, puis exécution incrémentale vérifiée par tests unitaires ciblés. Utiliser pour toute feature, fix ou refacto non trivial.
---

# Plan puis exécution

## Phase 1 — Plan (aucune édition)

1. Lire uniquement le code concerné (voir skill `economie`).
2. Produire un plan **3 à 5 points** max :
   - objectif en 1 ligne
   - fichiers touchés (chemins exacts)
   - étapes numérotées
   - test(s) ciblé(s) qui prouveront le résultat
   - risques / inconnues
3. **STOP. Attendre validation explicite.** Pas d'Edit/Write avant « ok ».

Plan > 3 fichiers → le signaler en tête (règle CLAUDE.md).

## Phase 2 — Exécution incrémentale

Pour chaque étape du plan :
1. Edit ciblé (diff minimal).
2. Test ciblé du module :
   `.\gradlew.bat :<module>:testDebugUnitTest --tests "<FQCN>"`
3. Vert → étape suivante. Rouge → corriger avant d'avancer ; 2 échecs consécutifs → stop, rapporter sortie réelle.

Écart au plan (fichier imprévu, approche différente) → revenir en Phase 1, redemander validation.

## Fin

- Rapport : étapes faites, tests lancés + résultat réel, reste à faire.
- Mettre à jour `.claude/SESSION_MEMORY.md` (skill `memoire-projet`).
