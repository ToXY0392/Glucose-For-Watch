# Plan de configuration GitHub — Glucose For Watch

> **Rôle :** guide pas-à-pas pour configurer le dépôt GitHub et un **GitHub Project** alignés sur [ACTION-PLAN.md](ACTION-PLAN.md), [PROGRESS.md](PROGRESS.md) et [STABILITY-GATES.md](STABILITY-GATES.md).  
> **Contexte :** app sideload PC · solo dev · chemin critique v0.5.0 → v0.6.0 · sync phone→watch sacré.

---

## Table des matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Phase 0 — Hygiène repo](#2-phase-0--hygiène-repo)
3. [Phase 1 — Branches](#3-phase-1--branches)
4. [Phase 2 — Labels et milestones](#4-phase-2--labels-et-milestones)
5. [Phase 3 — Issues et PR](#5-phase-3--issues-et-pr)
6. [Phase 4 — GitHub Project](#6-phase-4--github-project)
7. [Phase 5 — Automatisations](#7-phase-5--automatisations)
7. [Phase 6 — Sécurité et release](#8-phase-6--sécurité-et-release)
8. [Phase 7 — CI et checks](#9-phase-7--ci-et-checks)
9. [Mapping blocs ↔ GitHub](#10-mapping-blocs--github)
10. [Backlog initial à importer](#11-backlog-initial-à-importer)
11. [Rituels hebdomadaires](#12-rituels-hebdomadaires)
12. [Checklist finale](#13-checklist-finale)

---

## 1. Vue d'ensemble

### Architecture cible

```
GitHub Repo (Widget G7)
├── Branches
│   ├── main              ← releases taguées (v0.5.0, v0.6.0)
│   ├── integrate         ← intégration quotidienne (ex-rebuild)
│   └── feat|fix|docs|qa/bloc-*  ← branches courtes (1–5 j)
├── Issues                ← 1 issue = 1 tâche atomique (X.5a, C.7…)
├── Pull Requests         ← 1 PR = 1 bloc ou sous-objectif mesurable
├── Milestones            ← v0.5.0 · v0.6.0
└── GitHub Project        ← orchestration Kanban + gates + KPI
         ↕ sync hebdo
docs/plan/PROGRESS.md     ← scoreboard source de vérité KPI
docs/qa/                  ← preuves hardware (soak, incidents)
```

### Principes (hérités du plan app)

| # | Principe | Implémentation GitHub |
|---|----------|------------------------|
| P1 | Stabilité avant features | Milestone v0.5.0 avant v0.6.0 ; label `gate-blocker` |
| P2 | Une PR = un objectif | Branche `fix/bloc-x-*` · issue liée · template PR |
| P3 | Preuve avant merge | Checks CI + checklist PR + colonne QA Hardware |
| P4 | Soak obligatoire | Issue C.7 · champ Evidence · milestone v0.5.0 |
| P6 | Sync = contrat sacré | Label `sync-critical` · retest S1–S3 auto-créé |
| P7 | Solo dev séquencé | Project limité · pas de colonnes inutiles |

### Documents liés

| Doc | Rôle |
|-----|------|
| [ACTION-PLAN.md](ACTION-PLAN.md) | Backlog détaillé · calendrier S1–S8 |
| [PROGRESS.md](PROGRESS.md) | Scoreboard gates/KPI · MAJ hebdo |
| [STABILITY-GATES.md](STABILITY-GATES.md) | Critères Go/No-Go par gate |
| [PR-CHECKLIST.md](PR-CHECKLIST.md) | Checklist merge (copiée dans PR) |

---

## 2. Phase 0 — Hygiène repo

**Durée :** 30 min · **Bloquant avant tout le reste**

### 0.1 `.gitignore` — ajouter

```gitignore
# Cursor runtime (état session local)
.cursor/state/
!.cursor/state/.gitkeep

# Bruit build / tooling
*.log
analytics.settings
.tmp-*.jar
.android-user-home/
```

### 0.2 Retirer du suivi git (sans supprimer localement)

```powershell
git rm -r --cached .cursor/state/
git rm --cached .android-user-home/debug.keystore.lock .tmp-protolayout-classes.jar .tmp-tiles-classes.jar analytics.settings build-last.log
```

### 0.3 Fichiers repo à ajouter

| Fichier | Contenu |
|---------|---------|
| `LICENSE` | Licence choisie (MIT, Apache-2.0, ou propriétaire sideload) |
| `SECURITY.md` | Signalement vulnérabilités · pas de glucose réel dans reports |
| `AGENTS.md` | Index skills Cursor + point d'entrée docs |
| `.editorconfig` | LF · UTF-8 · indent 4 (Kotlin/XML) |

### 0.4 Committer les docs plan manquantes

- [ ] `docs/plan/PROGRESS.md` (actuellement untracked)
- [ ] Ce fichier `docs/plan/GITHUB-SETUP.md`

---

## 3. Phase 1 — Branches

### 3.1 Modèle de branches

| Branche | Type | Rôle | Merge vers |
|---------|------|------|------------|
| `main` | longue | Releases stables · tags M7/M8 | — |
| `integrate` | longue | Intégration quotidienne · CI | `main` (post-gate) |
| `release/v0.5` | longue (temp.) | Gel bugfix avant tag v0.5.0 | `main` |
| `{type}/bloc-{id}-{slug}` | courte | Dev feature/fix | `integrate` |

**Types autorisés :** `feat` · `fix` · `docs` · `test` · `chore` · `qa` · `design`

**Exemples alignés plan :**

```
fix/bloc-x-fgs-crash          → PR #8  · gate G-X
feat/bloc-a-p0-reliability    → PR #9  · gate G-A
feat/bloc-m-home-viewmodel    → PR #10 · gate G-M
feat/bloc-b-wear-complication → PR #11 · gate G-B
docs/bloc-c-qa-evidence       → PR #12 · gate G-C
test/bloc-d-dexcom-coverage   → PR #13-14 · gate G-D
feat/bloc-f0-compose-gradle   → PR #15 · gate G-F0
feat/bloc-f3-home-compose     → PR #18 · gate G-F3
chore/bloc-s-repo-hygiene     → transverse
qa/bloc-c-soak-night          → session C.7 hardware
```

### 3.2 Migration `rebuild` → `integrate`

```powershell
git checkout rebuild
git branch -m integrate
git push origin -u integrate
# Mettre à jour CI (voir Phase 7) puis :
git push origin --delete rebuild
```

### 3.3 Branches à auditer / fermer

| Branche actuelle | Action |
|------------------|--------|
| `dev` | Merger dans `integrate` ou supprimer si stale |
| `docs` | Supprimer après merge si vide |
| `design` | Garder si travail ToXY actif |
| `phase/test` | Renommer `qa/bloc-c-soak` ou supprimer |

### 3.4 Protection de branche `main`

**Settings → Branches → Add rule → `main`**

| Option | Valeur |
|--------|--------|
| Require pull request before merging | ✅ |
| Required approvals | 0 (solo) ou 1 si co-dev |
| Require status checks | ✅ `Verify Linux Build Gates` |
| Require branches up to date | ✅ |
| Do not allow bypassing | ✅ |
| Restrict pushes | ✅ (personne sauf toi via PR) |

### 3.5 Protection `integrate` (recommandée)

| Option | Valeur |
|--------|--------|
| Require status checks | ✅ CI verify |
| Allow direct push | ✅ (solo dev quotidien) |

---

## 4. Phase 2 — Labels et milestones

### 4.1 Labels — création (GitHub UI ou CLI)

**Settings → Labels** ou avec [GitHub CLI](https://cli.github.com/) :

```bash
# Blocs (couleur par domaine)
gh label create "bloc-s"       --color "BFD4F2" --description "Stabilité transverse"
gh label create "bloc-x"       --color "D73A4A" --description "Crash FGS · gate G-X"
gh label create "bloc-a"       --color "FBCA04" --description "P0 fiabilité · gate G-A"
gh label create "bloc-m"       --color "0E8A16" --description "Mock user · gate G-M"
gh label create "bloc-b"       --color "1D76DB" --description "Sync/wear · gate G-B"
gh label create "bloc-c"       --color "5319E7" --description "QA hardware · gate G-C"
gh label create "bloc-d"       --color "006B75" --description "Qualité/tests · gate G-D"
gh label create "bloc-f"       --color "E99695" --description "Compose v0.6 · gate G-F*"

# Priorité / risque
gh label create "gate-blocker" --color "B60205" --description "Bloque tag v0.5 ou v0.6"
gh label create "incident-p0"  --color "8B0000" --description "Crash fatal ouvert"
gh label create "sync-critical" --color "D93F0B" --description "Touch sync · S1–S3 requis"

# Surface app
gh label create "area:mobile"  --color "C5DEF5" --description "Phone app"
gh label create "area:wear"    --color "C5DEF5" --description "Tile · complication"
gh label create "area:sync"     --color "C5DEF5" --description "GlucoseSyncEngine · Data Layer"
gh label create "area:dexcom"   --color "C5DEF5" --description "Share API · auth"
gh label create "area:ux-kit"    --color "C5DEF5" --description "toxy-ux-kit · AGP colors"
gh label create "area:infra"    --color "C5DEF5" --description "CI · scripts · repo"

# Workflow
gh label create "hardware-qa"  --color "D4C5F9" --description "Session adb phone+watch"
gh label create "docs-only"    --color "EDEDED" --description "Pas de build requis"
gh label create "good first issue" --color "7057FF" --description "Entrée facile"

# Existants (conserver)
# bug · enhancement
```

### 4.2 Milestones

| Milestone | Titre | Date cible | Description |
|-----------|-------|------------|-------------|
| M1 | **v0.5.0 — Stable sideload** | fin S4 | Blocs X→D · gates G-X→G-M7 · soak C.7 |
| M2 | **v0.6.0 — Compose phone** | fin S8 | Blocs F0→F5 · gate G-M8 |

**Issues par milestone :** voir [§11 Backlog initial](#11-backlog-initial-à-importer).

---

## 5. Phase 3 — Issues et PR

### 5.1 Templates d'issues (fichiers repo)

| Template | Usage |
|----------|-------|
| `bug_report.md` | Bug utilisateur / sync (existant) |
| `feature_request.md` | Amélioration (existant) |
| `bloc_task.md` | Tâche plan ACTION-PLAN (nouveau) |
| `incident_p0.md` | Crash fatal · incident ouvert (nouveau) |

Voir `.github/ISSUE_TEMPLATE/` — choix via `config.yml`.

### 5.2 Convention titres issues

```
[bloc-x] X.5a — try/catch FGS dans onCreate
[bloc-c] C.7 — soak nuit 8h + sign-off
[incident] FGS crash Pixel 8a — 2026-05-25
[bloc-f3] F3 — HomeScreen Compose + gate soak 4h
```

### 5.3 Template PR

Le template `.github/pull_request_template.md` référence [PR-CHECKLIST.md](PR-CHECKLIST.md).

**Champs obligatoires dans chaque PR :**

- Bloc / Gate / Touch sync
- Lien issue (`Closes #N` ou `Refs #N`)
- Branche source → `integrate` (ou `main` pour hotfix release)

### 5.4 Liaison issue ↔ PR ↔ docs

```
Issue #42 [bloc-x] X.5a
    ↓ PR fix/bloc-x-fgs-crash → integrate
    ↓ merge
PROGRESS.md scoreboard MAJ
docs/qa/soak-runs/… si hardware
Project card → Done
```

---

## 6. Phase 4 — GitHub Project

### 6.1 Création

1. Repo → **Projects** → **New project**
2. Template : **Team backlog** (ou Board vide)
3. Nom : **`Glucose For Watch — v0.5 → v0.6`**
4. Lier au repo `Widget G7`

### 6.2 Colonnes (Board view)

| Colonne | Definition of Ready | Definition of Done |
|---------|---------------------|------------------|
| **Backlog** | Issue créée · bloc identifié | — |
| **Ready** | Gate amont OK · branche nommée | — |
| **In Progress** | Dev actif · draft PR possible | — |
| **In Review** | PR ouverte · CI verte | — |
| **QA Hardware** | Touch sync/wear · adb requis | smoke + seq notés |
| **Gate Ready** | stability-gate PASS · critères gate cochés | — |
| **Done** | PR mergée · PROGRESS MAJ | evidence liée |

**Règle solo :** max **2** cartes In Progress (focus chemin critique).

### 6.3 Champs personnalisés (Project settings → Fields)

| Champ | Type | Options / notes |
|-------|------|-----------------|
| **Bloc** | Single select | S, X, A, M, B, C, D, F0, F1, F2, F3, F4, F5 |
| **Gate** | Single select | G-X, G-A, G-M, G-B, G-C, G-D, G-M7, G-F0…G-F3, G-M8 |
| **KPI** | Multi select | K1, K2, K3, K4, K5, K6, K7, K8 |
| **Sync touch** | Checkbox | Déclenche colonne QA Hardware |
| **Hardware QA** | Checkbox | Phone + watch requis |
| **Effort (h)** | Number | Depuis ACTION-PLAN |
| **Branch** | Text | ex. `fix/bloc-x-fgs-crash` |
| **Evidence** | URL | Lien docs/qa/ ou issue comment |

### 6.4 Vues du Project

#### Vue A — **Board** (quotidien)

- Layout : Board
- Group by : Status (colonnes §6.2)
- Filter : `status != Done` OR `updated:>@today-14d`

#### Vue B — **Gates & KPI** (hebdo)

- Layout : Table
- Columns : Title · Bloc · Gate · KPI · Sync touch · Milestone · Assignee
- Sort : Gate (custom order G-X first) · Priority
- Filter : Milestone = v0.5.0

#### Vue C — **Roadmap** (planning)

- Layout : Roadmap
- Date field : **Target date** (aligné calendrier S1–S8)
- Group : Milestone
- Items : blocs entiers + jalons C.7 · M7 · M8

#### Vue D — **QA & Incidents**

- Layout : Table
- Filter : label `hardware-qa` OR `incident-p0` OR Bloc = C
- Columns : Title · Evidence · Gate · Status

### 6.5 Sync Project ↔ PROGRESS.md

| Où | Quoi | Fréquence |
|----|------|-----------|
| GitHub Project | Statut cartes · In Progress · Done | temps réel |
| PROGRESS.md | Scoreboard gates · KPI · dates | **hebdo** (rituel lundi) |
| STABILITY-GATES.md | Critères (rarement modifié) | si nouveau gate |
| docs/qa/ | Preuves soak/incidents | après chaque session |

**Ne pas dupliquer** le détail des tâches : ACTION-PLAN reste la spec ; le Project est le **tableau de bord**.

---

## 7. Phase 5 — Automatisations

### 7.1 Project workflows (Settings → Workflows)

| Workflow | Trigger | Action |
|----------|---------|--------|
| Item added to project | Issue ajoutée | Status = Backlog |
| Item closed | Issue fermée | Status = Done |
| PR merged | Linked issue | Status = Done · commenter |
| Item labeled `sync-critical` | Label ajouté | Sync touch = checked |

### 7.2 Suggestions manuelles (solo dev)

Après merge PR touchant sync :

1. Créer issue follow-up : `[bloc-s] S1–S3 retest post-PR #N`
2. Label `sync-critical` · `hardware-qa`
3. Colonne QA Hardware

### 7.3 Dependabot (`.github/dependabot.yml`)

```yaml
version: 2
updates:
  - package-ecosystem: "gradle"
    directory: "/"
    schedule:
      interval: "weekly"
    labels:
      - "area:infra"
      - "bloc-s"
    open-pull-requests-limit: 2

  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
    labels:
      - "area:infra"
```

**Règle :** upgrades AGP/Gradle via skill `widget-g7-dependency-advisor` · PR dédiée · jamais mélangée avec bloc feature.

---

## 8. Phase 6 — Sécurité et release

### 8.1 SECURITY.md (racine repo)

Contenu minimal :

- Ne pas inclure credentials Dexcom · glucose réel · logcat complet avec PII
- Email ou GitHub Security Advisories (privé)
- Scope : app mobile/wear · scripts sideload · pas de serveur backend

### 8.2 Release workflow (manuel v0.5)

Jusqu'à CI release automatisée :

1. Gate G-M7 ✅ dans PROGRESS.md
2. Branche `release/v0.5` depuis `integrate`
3. `bash scripts/release/release_dry_run.sh`
4. Tag `v0.5.0` sur `main`
5. GitHub Release · notes depuis CHANGELOG.md · APK en artifact privé (si configuré)

### 8.3 GitHub Release checklist

- [ ] CHANGELOG.md section v0.5.0
- [ ] Legal placeholders OK (`check_legal_placeholders.sh`)
- [ ] soak C.7 sign-off dans docs/qa/
- [ ] Incident K1 = 0 (fermé)

---

## 9. Phase 7 — CI et checks

### 9.1 Branches déclenchant CI

Mettre à jour `.github/workflows/ci.yml` :

```yaml
on:
  push:
    branches:
      - main
      - integrate      # remplace rebuild
  pull_request:
  workflow_dispatch:
```

### 9.2 Status checks requis sur `main`

| Job CI | Nom affiché | Bloquant |
|--------|-------------|----------|
| `verify` | Verify Linux Build Gates | ✅ |
| `release-artifacts` | Verify Release Artifacts | ✅ (pre-release) |

### 9.3 Checks locaux (hors GitHub)

Non branchables sur GitHub sans self-hosted runner :

- `stability-gate.ps1` → checklist PR manuelle
- `hardware-smoke.ps1` → colonne QA Hardware du Project

---

## 10. Mapping blocs ↔ GitHub

| Bloc | PR plan | Gate | Milestone | Labels typiques | Branche type |
|------|---------|------|-----------|-----------------|--------------|
| S | — | transverse | les deux | `bloc-s`, `area:infra` | `chore/bloc-s-*` |
| X | #8 | G-X | v0.5.0 | `bloc-x`, `gate-blocker`, `sync-critical` | `fix/bloc-x-*` |
| A | #9 | G-A | v0.5.0 | `bloc-a`, `area:mobile` | `feat/bloc-a-*` |
| M | #10 | G-M | v0.5.0 | `bloc-m`, `area:mobile` | `feat/bloc-m-*` |
| B | #11 | G-B | v0.5.0 | `bloc-b`, `area:wear`, `sync-critical` | `feat/bloc-b-*` |
| C | #12 | G-C | v0.5.0 | `bloc-c`, `hardware-qa`, `gate-blocker` | `docs/bloc-c-*`, `qa/*` |
| D | #13–14 | G-D | v0.5.0 | `bloc-d`, `area:dexcom` | `test/bloc-d-*` |
| M7 | tag | G-M7 | v0.5.0 | — | `release/v0.5` |
| F0–F5 | #15–18 | G-F* | v0.6.0 | `bloc-f`, `area:mobile` | `feat/bloc-f*-*` |
| M8 | tag | G-M8 | v0.6.0 | — | `release/v0.6` |

### Chemin critique (ordre merge vers `integrate`)

```
X → A → (M ∥ prep B) → B → C → D → merge integrate → main → v0.5.0
                                              ↓
                                    F0 → F1 → F2 → F3 → F5 → v0.6.0
```

---

## 11. Backlog initial à importer

Créer ces issues **dans l'ordre** et les ajouter au Project (milestone v0.5.0 sauf F*).

### P0 — Bloquants immédiats

| Issue | Bloc | Gate | État PROGRESS |
|-------|------|------|---------------|
| `[bloc-x] X.3 — Repro soak FGS ou simulation quota` | X | G-X | 🔄 |
| `[bloc-x] X.7 — Test Robolectric FGS refusé → fallback Worker` | X | G-X | ☐ |
| `[bloc-c] C.7 — Soak nuit 8h + sign-off matin` | C | G-C | 🔄 |
| `[incident] FGS crash 2026-05-25 — fermer après G-X` | X | G-X | ouvert K1 |

### P1 — v0.5.0 en cours

| Issue | Bloc | Gate |
|-------|------|------|
| `[bloc-a] A.1 — Flow permission notifications` | A | G-A |
| `[bloc-a] A.3 — Snackbar feedback sync manuelle` | A | G-A |
| `[bloc-m] M.4 — design-reference companion PNG à jour` | M | G-M |
| `[bloc-b] B.4 — WatchSyncVerifier via engine + test ack` | B | G-B |
| `[bloc-c] C.2 — Complication vs tuile t/5min × 6` | C | G-C |
| `[bloc-c] C.3 — Offline watch 2h · rattrapage` | C | G-C |
| `[bloc-c] C.8 — Montre ≤20% batterie · sync dégradée` | C | G-C |

### P2 — Post G-C / pré M7

| Issue | Bloc | Gate |
|-------|------|------|
| `[bloc-d] D.6 — capture-crash-log.ps1 one-command` | D | G-D |
| `[bloc-s] Repo hygiene — gitignore + purge artefacts` | S | — |
| `[bloc-s] Commit PROGRESS.md + index docs plan` | S | — |

### v0.6.0 (milestone M2 — créer issues plus tard)

| Issue | Bloc | Gate |
|-------|------|------|
| `[bloc-f0] F0 — Gradle Compose + WidgetG7Theme` | F0 | G-F0 |
| `[bloc-f3] F3 — HomeScreen Compose + soak 4h` | F3 | G-F3 |

---

## 12. Rituels hebdomadaires

### Lundi (15 min)

- [ ] Ouvrir Project vue **Gates & KPI**
- [ ] Mettre à jour [PROGRESS.md](PROGRESS.md) scoreboard
- [ ] Choisir **1 bloc** + **1 gate** fin de semaine
- [ ] Max 2 cartes → In Progress
- [ ] `adb devices -l` · phone + watch OK

### Avant chaque PR

- [ ] Branche nommée `type/bloc-id-slug`
- [ ] Issue liée · labels bloc + area
- [ ] [PR-CHECKLIST.md](PR-CHECKLIST.md) dans description PR
- [ ] `verify_ci.sh` + `stability-gate.ps1`

### Vendredi (30 min)

- [ ] Gate bloc cochée ou reportée (commenter why sur issue)
- [ ] Cartes Done · evidence URL renseignée
- [ ] Session hardware → fichier dans `docs/qa/sessions/` ou `soak-runs/`

### Nuit soak (C.7)

- [ ] Issue C.7 → In Progress · label `hardware-qa`
- [ ] Matin : sign-off template · fermer issue · PROGRESS K2 ✅

---

## 13. Checklist finale

### Phase 0 — Hygiène
- [ ] `.gitignore` enrichi
- [ ] Artefacts retirés du suivi git
- [ ] `PROGRESS.md` commité
- [ ] `LICENSE` · `SECURITY.md` · `AGENTS.md`

### Phase 1 — Branches
- [ ] `rebuild` → `integrate`
- [ ] Protection `main` active
- [ ] Branches stale nettoyées

### Phase 2 — Labels & milestones
- [ ] Labels blocs + gates créés
- [ ] Milestones v0.5.0 et v0.6.0

### Phase 3 — Issues & PR
- [ ] Templates bloc_task + incident_p0
- [ ] PR template aligné checklist

### Phase 4 — Project
- [ ] Project créé · 4 vues
- [ ] Champs Bloc · Gate · KPI · Evidence
- [ ] Backlog P0 importé (§11)

### Phase 5–7 — Automation & CI
- [ ] Dependabot configuré
- [ ] CI sur `integrate`
- [ ] Premier rituel lundi exécuté

---

## Annexe — AGENTS.md (extrait suggéré)

```markdown
# Agent guide — Widget G7

## Docs plan
- Hub: docs/index.md
- GitHub setup: docs/plan/GITHUB-SETUP.md
- PR checklist: docs/plan/PR-CHECKLIST.md

## Branches
- integrate = daily integration
- fix|feat/bloc-{id}-{slug} = short-lived

## Skills (sync/debug)
- widget-g7-sync-health-reviewer
- widget-g7-agp-color-guard
- widget-g7-pr-gatekeeper (à créer)
```

---

*Dernière MAJ : 2026-05-26 · aligné ACTION-PLAN S1–S8 et PROGRESS scoreboard.*
