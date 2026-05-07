---
name: widget-g7-run-coordinator
description: Ordonne les executions recurrentes des skills de maintenance Widget G7 avec verrouillage, cooldown, deduplication et strategie d'alerte pour limiter le bruit.
disable-model-invocation: true
---

# Widget G7 Run Coordinator

## Objectif
Coordonner l'automatisation de tous les skills de veille/maintenance sans spam.

## Responsabilites
- Lock d'execution (eviter doublons)
- Cooldown entre runs
- Deduplication des alertes
- Escalade par severite

## Politique d'alerte
- `Critique` : notification immediate + blocage release si applicable
- `Important` : digest quotidien
- `Mineur` : digest hebdomadaire

## Workflow
1. Verifier l'etat precedent (timestamp + hash des alertes).
2. Choisir le pack a lancer (quick / full / release).
3. Executer les skills cibles dans l'ordre defini.
4. Agreger les resultats dans un bulletin unique.
5. Mettre a jour l'etat d'automatisation.

## Packs standards
- `quick`: vendor-watch + security-bulletin
- `full`: compat-matrix + dependency-advisor + doc-drift + release-notes-curator
- `release`: full + verification des blocants

## Regles
- Ne pas publier deux fois la meme alerte sans nouveau signal.
- Toujours produire un resume operationnel final.
