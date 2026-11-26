---
name: glucose-for-watch-memoire-projet
description: Maintains .claude/SESSION_MEMORY.md (current work, decisions, next steps, known pitfalls) so context survives /clear. Use when starting a session (read it), and when finishing a task or before /clear or /compact (update it).
---

# Mémoire projet

Fichier : `.claude/SESSION_MEMORY.md` (≤ 60 lignes, télégraphique).

## Début de session

Lire `.claude/SESSION_MEMORY.md` + `git status` + `git log --oneline -5`. Signaler toute incohérence (branche différente, étape déjà commitée).

## Mise à jour (fin de tâche, avant /clear ou /compact)

Réécrire le fichier avec cette structure exacte :

```markdown
# Session memory
_Màj : AAAA-MM-JJ · branche : <branche>_

## En cours
- <tâche> — <état : fait / bloqué / en cours> — <fichiers clés>

## Décisions
- <date> <décision> — pourquoi : <raison>

## Prochaines étapes
1. <action concrète + commande/test>

## Pièges connus
- <piège> → <contournement>
```

## Règles

- Dates absolues uniquement.
- Pas de code, pas de logs : pointeurs (`fichier.kt:ligne`, commit SHA).
- Supprimer ce qui est terminé et commité (git l'enregistre déjà). Garder les décisions encore valides.
- Ne jamais y écrire secrets, identifiants Dexcom, serials ADB.
