---
name: widget-g7-doc-drift-checker
description: Compare la documentation interne Widget G7 aux documentations officielles recentes, detecte l'obsolescence, propose un patch priorise et applique les mises a jour doc quand l'edition est autorisee.
disable-model-invocation: true
---

# Widget G7 Doc Drift Checker

## Objectif
Maintenir la documentation du projet alignee avec les recommandations fournisseurs.

## Portee
- `README.md`
- `docs/index.md`
- `docs/dev.md`
- `docs/architecture.md`
- `CHANGELOG.md`

## Workflow
1. Inventorier versions, commandes et recommandations presentes dans la doc interne.
2. Verifier les sources officielles recentes (Android/Wear/AGP/Gradle/Kotlin/Dexcom).
3. Classer les ecarts detectes :
   - `Critique` (information incorrecte ou cassante)
   - `Important` (obsolescence forte)
   - `Mineur` (formulation, liens, precision)
4. Proposer un patch priorise fichier par fichier.
5. Si le mode autorise les edits, appliquer les patchs docs (priorite Critique puis Important).
6. Verifier la coherence transversale entre `README.md` et `docs/*`.

## Format de sortie
- **Rapport d'obsolescence** (Critique / Important / Mineur)
- **Patch plan** (fichier, section, justification, source)
- **Execution** : `Applied: oui/non`

## Regles
- Ne pas inventer de version ni de recommandation.
- Ne modifier que la documentation dans cette skill.
- Respecter les contraintes du projet Windows/WSL decrites dans `docs/dev.md`.
