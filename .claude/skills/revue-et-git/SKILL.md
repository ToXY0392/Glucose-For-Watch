---
name: revue-et-git
description: Revue avant commit/push — vérifie git diff, lance les tests des modules touchés, met à jour TODO.md s'il existe, prépare un message de commit propre. Utiliser avant tout commit ou push.
---

# Revue et Git

## 1. Diff

```powershell
git status --short
git diff --stat
git diff
```

Vérifier :
- seuls les fichiers attendus sont modifiés (sinon signaler) ;
- aucun secret / `local.properties` / identifiant Dexcom / serial ADB ;
- pas de code mort, `println`, `TODO` ajouté sans raison ;
- branche ≠ `main` et ≠ `develop/integration` (sinon créer `{feat|fix|docs|test|chore}/bloc-{id}-{slug}`).

## 2. Tests des modules touchés

Déduire les modules depuis `git diff --name-only`, puis :

```powershell
.\gradlew.bat :<module>:testDebugUnitTest
```

Docs modifiées → `py -3 scripts/dev/check_docs_links.py`.
Rapporter le résultat réel. Rouge → pas de commit.

## 3. TODO.md

S'il existe : cocher les items faits, ajouter les suites découvertes. Sinon ne pas le créer.
Mettre à jour `.claude/SESSION_MEMORY.md` (skill `memoire-projet`).

## 4. Message de commit

Conventional Commits, impératif, ≤ 72 car. :

```
<type>(<scope>): <résumé>

<pourquoi, 1-3 lignes>
```

Types : `feat` `fix` `docs` `test` `chore` `refactor`. Scope = module (`sync`, `wear`, `mobile`, `dexcom-share`…).

## 5. Push

Commit et push **uniquement sur demande explicite**. Jamais `--force` ni `--no-verify`. PR → skill Cursor `glucose-for-watch-pr-author`, base `develop/integration`.
