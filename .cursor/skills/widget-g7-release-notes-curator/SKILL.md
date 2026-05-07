---
name: widget-g7-release-notes-curator
description: Agrege les release notes officielles des fournisseurs Android, Wear OS, AGP, Gradle, Kotlin et Dexcom, puis extrait les points applicables a Widget G7 pour pre-remplir docs/release-notes.md.
disable-model-invocation: true
---

# Widget G7 Release Notes Curator

## Objectif
Maintenir un changelog projet utile et aligne avec les evolutions upstream.

## Fichier cible principal
- `docs/release-notes.md`

## Workflow
1. Collecter les release notes recentes des fournisseurs suivis.
2. Garder uniquement les points applicables au repo.
3. Proposer une section `Upstream changes` concise :
   - impact technique
   - impact produit
   - action recommandee
4. Si edition autorisee, mettre a jour `docs/release-notes.md`.

## Format de section
- `Upstream changes`
  - source
  - changement
  - impact Widget G7
  - action

## Regles
- Eviter le bruit : ignorer les changements sans effet sur le projet.
- Garder un style lisible pour l'equipe.
- Conserver l'historique deja present dans le fichier.
