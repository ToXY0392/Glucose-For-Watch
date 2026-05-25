# GitHub Project — guide UI pas a pas

> Repo: **ToXY0392/Glucose-For-Watch** · Issues **#1-#12** deja creees.

Tu es sur: **Projects** (ecran vide) → bouton vert **+ New project**.

---

## Etape 1 — Creer le board (30 sec)

1. Clique **+ New project**
2. Choisis **Board** (pas Table, pas Roadmap pour commencer)
3. Titre:
   ```
   Glucose For Watch v0.5 to v0.6
   ```
4. **Create project**

---

## Etape 2 — Colonnes Status (2 min)

GitHub Projects v2 utilise le champ **Status** comme colonnes du board.

1. Ouvre le project cree
2. En haut a droite: **...** (menu) → **Settings**
3. Section **Fields** → clique sur **Status** (built-in)
4. **Edit field** / **Manage options**
5. Remplace la liste par (ajoute avec **+ Add option**, supprime Todo/Done par defaut si doublons):

| Option (nom exact) | Couleur suggeree |
|--------------------|------------------|
| Backlog | Gray |
| Ready | Blue |
| In Progress | Yellow |
| In Review | Orange |
| QA Hardware | Purple |
| Gate Ready | Green |
| Done | Green |

6. **Save changes** → retour au board: tu dois voir **7 colonnes**

---

## Etape 3 — Ajouter les 12 issues (1 min)

1. Sur le board, clique **+ Add item** (bas d'une colonne) **ou** bouton **Add items** en haut
2. Choisis **Issues from repository** → **Glucose-For-Watch**
3. Coche **toutes** les issues ouvertes (#1 a #12) ou tape dans la recherche:
   ```
   is:issue is:open
   ```
4. **Add selected items**

Tu dois voir **12 cartes** (probablement toutes en **Backlog** ou **Todo**).

---

## Etape 4 — Placer les cartes (drag & drop)

Glisse chaque carte vers la bonne colonne:

| Colonne | Issues |
|---------|--------|
| **In Progress** | **#3** C.7 soak |
| **Ready** | **#1** X.3 · **#2** X.7 · **#4** incident |
| **Backlog** | **#5** #6 #7 #8 #9 #10 #11 #12 |

Regle solo: **1 seule** carte In Progress (#3 soak).

---

## Etape 5 — Champs custom (2 min, optionnel)

**Settings** → **Fields** → **+ New field**

| Nom | Type | Options |
|-----|------|---------|
| Bloc | Single select | S, X, A, M, B, C, D, F |
| Gate | Single select | G-X, G-A, G-M, G-B, G-C, G-D, G-M7, G-M8 |
| Sync touch | Checkbox | — |
| Hardware QA | Checkbox | — |

Remplis sur les cartes (clic carte → panneau droit):

| Issue | Bloc | Gate | Sync | Hardware |
|-------|------|------|------|----------|
| #1 | X | G-X | oui | oui |
| #2 | X | G-X | non | non |
| #3 | C | G-C | oui | oui |
| #4 | X | G-X | oui | non |
| #8 | B | G-B | oui | oui |
| #9-11 | C | G-C | varie | oui |

---

## Etape 6 — Lier au repo

1. **Settings** du project → **Manage access** / **Linked repositories**
2. **Link repository** → **Glucose-For-Watch**

Ou depuis https://github.com/ToXY0392/Glucose-For-Watch/projects → **Link a project** → selectionne le board cree.

---

## Verification

- [ ] 7 colonnes Status visibles
- [ ] 12 cartes (#1-#12)
- [ ] #3 en **In Progress**
- [ ] #1 #2 #4 en **Ready**
- [ ] Repo lie au project

**URL project:** `https://github.com/users/ToXY0392/projects/<numero>`

---

## Automatisation (apres scope `project` sur le token)

```powershell
gh auth login --scopes "project,read:project,repo"
.\scripts\dev\setup_github_project.ps1
```
