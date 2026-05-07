---
name: widget-g7-security-bulletin
description: Suit les CVE et avis securite des dependances Android et outils build utilises par Widget G7, puis priorise les corrections selon gravite et exploitabilite.
disable-model-invocation: true
---

# Widget G7 Security Bulletin

## Objectif
Fournir une veille securite orientee action pour mobile + wear.

## Cibles
- Dependances runtime Android/Wear
- Dependances build (AGP, Gradle, plugins)
- Outils et chaines de build critiques

## Workflow
1. Recenser les composants utilises par le repo.
2. Collecter les avis securite et CVE pertinentes.
3. Evaluer chaque point selon :
   - gravite
   - exploitabilite dans le contexte Widget G7
   - exposition reelle (build-time / runtime)
4. Classer :
   - `Critique`
   - `Important`
   - `Mineur`
5. Proposer correction minimale + fenetre de release.

## Sortie attendue
- **Tableau de priorite** (CVE, composant, score/gravite, impact)
- **Actions immediates**
- **Actions planifiees**
- **Risques acceptes temporairement**

## Regles
- Ne pas signaler des CVE hors contexte technique du repo.
- Citer les sources des avis.
- Toujours indiquer un chemin de correction realiste.
