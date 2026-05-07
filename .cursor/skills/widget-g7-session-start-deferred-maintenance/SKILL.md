---
name: widget-g7-session-start-deferred-maintenance
description: Lance un cycle de maintenance complet peu apres l'ouverture du projet Cursor pour Widget G7, avec aggregation des resultats et priorisation des actions.
disable-model-invocation: true
---

# Widget G7 Session Start Deferred Maintenance

## Objectif
Executer les verifications lourdes apres le demarrage de session.

## Scope full maintenance
- `widget-g7-compat-matrix-maintainer`
- `widget-g7-dependency-advisor`
- `widget-g7-doc-drift-checker`
- `widget-g7-release-notes-curator`

## Workflow
1. Attendre un delai court post-demarrage (2-5 min).
2. Verifier lock/cooldown via `widget-g7-run-coordinator`.
3. Executer le pack full.
4. Produire un bulletin consolide :
   - blocants
   - actions prioritaires
   - actions planifiees

## Regles
- Ne pas lancer si un run full est deja en cours.
- Regrouper les sorties pour eviter les notifications multiples.
