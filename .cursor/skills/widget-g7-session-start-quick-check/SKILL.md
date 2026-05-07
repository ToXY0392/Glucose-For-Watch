---
name: widget-g7-session-start-quick-check
description: Execute un controle leger au demarrage d'une session Cursor pour Widget G7, en mode delta, afin de remonter rapidement les nouveautes critiques vendor/securite.
disable-model-invocation: true
---

# Widget G7 Session Start Quick Check

## Objectif
Lancer un check rapide a l'ouverture du projet sans ralentir la session.

## Scope
- `widget-g7-vendor-watch` (delta)
- `widget-g7-security-bulletin` (delta)

## Workflow
1. Lire l'etat du dernier run.
2. Verifier le cooldown (ex: 6h).
3. Lancer uniquement les checks quick si necessaire.
4. Retourner un mini digest :
   - nouveaux critiques
   - nouveaux importants
   - aucun changement

## Regles
- Rester non bloquant au demarrage.
- Limiter la sortie a l'essentiel.
