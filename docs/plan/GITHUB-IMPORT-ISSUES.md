> **Statut 2026-05-26 :** issues #1-#12 creees · labels + milestones OK · Project UI manuel (scope `project` requis sur le token).

## Issues creees

| # | Title |
|---|-------|
| [#1](https://github.com/ToXY0392/Glucose-For-Watch/issues/1) | [bloc-x] X.3 - Repro soak FGS |
| [#2](https://github.com/ToXY0392/Glucose-For-Watch/issues/2) | [bloc-x] X.7 - Test Robolectric FGS |
| [#3](https://github.com/ToXY0392/Glucose-For-Watch/issues/3) | [bloc-c] C.7 - Soak nuit 8h |
| [#4](https://github.com/ToXY0392/Glucose-For-Watch/issues/4) | [incident] FGS crash 2026-05-25 |
| [#5-#12](https://github.com/ToXY0392/Glucose-For-Watch/issues) | P1/P2 blocs A, M, B, C, D |

## GitHub Project (2 min UI)

Le token git actuel n'a pas le scope **`project`**. Creer le board manuellement :

1. https://github.com/ToXY0392/Glucose-For-Watch → **Projects** → **New project**
2. Nom : `Glucose For Watch v0.5 to v0.6`
3. Template **Board** · colonnes : Backlog → Ready → In Progress → In Review → QA Hardware → Gate Ready → Done
4. **Add items** → selectionner issues #1-#12

Ou apres avoir ajoute le scope `project` au token : `powershell scripts/dev/bootstrap_github.ps1` (section Project).

---

## Import original (reference)

## P0 — Bloquants

| Title | Bloc | Gate | Labels |
|-------|------|------|--------|
| `[bloc-x] X.3 — Repro soak FGS ou simulation quota` | X | G-X | `bloc-x`, `gate-blocker`, `sync-critical`, `hardware-qa` |
| `[bloc-x] X.7 — Test Robolectric FGS refusé → fallback Worker` | X | G-X | `bloc-x`, `gate-blocker`, `area:mobile` |
| `[bloc-c] C.7 — Soak nuit 8h + sign-off matin` | C | G-C | `bloc-c`, `gate-blocker`, `hardware-qa` |
| `[incident] FGS crash 2026-05-25 — fermer après G-X` | X | G-X | template **Incident P0** |

## P1 — v0.5.0

| Title | Bloc | Gate | Labels |
|-------|------|------|--------|
| `[bloc-a] A.1 — Flow permission notifications` | A | G-A | `bloc-a`, `area:mobile` |
| `[bloc-a] A.3 — Snackbar feedback sync manuelle` | A | G-A | `bloc-a`, `area:mobile`, `area:sync` |
| `[bloc-m] M.4 — design-reference companion PNG à jour` | M | G-M | `bloc-m`, `area:ux-kit` |
| `[bloc-b] B.4 — WatchSyncVerifier via engine + test ack` | B | G-B | `bloc-b`, `area:wear`, `sync-critical` |
| `[bloc-c] C.2 — Complication vs tuile t/5min × 6` | C | G-C | `bloc-c`, `hardware-qa`, `area:wear` |
| `[bloc-c] C.3 — Offline watch 2h · rattrapage` | C | G-C | `bloc-c`, `hardware-qa`, `sync-critical` |
| `[bloc-c] C.8 — Montre ≤20% batterie · sync dégradée` | C | G-C | `bloc-c`, `hardware-qa` |

## P2

| Title | Bloc | Gate | Labels |
|-------|------|------|--------|
| `[bloc-d] D.6 — capture-crash-log.ps1 one-command` | D | G-D | `bloc-d`, `area:infra` |

## gh CLI (après `gh auth login`)

```bash
bash .github/scripts/create-labels.sh

gh api repos/ToXY0392/Glucose-For-Watch/milestones -f title="v0.5.0 — Stable sideload" -f description="Blocs X→D · gate G-M7"
gh api repos/ToXY0392/Glucose-For-Watch/milestones -f title="v0.6.0 — Compose phone" -f description="Blocs F0→F5 · gate G-M8"

gh issue create --repo ToXY0392/Glucose-For-Watch \
  --title "[bloc-x] X.3 — Repro soak FGS ou simulation quota" \
  --label "bloc-x,gate-blocker,sync-critical,hardware-qa" \
  --body "Gate G-X · voir ACTION-PLAN X.3"
```

## GitHub Project

1. **Projects → New** → `Glucose For Watch — v0.5 → v0.6`
2. Colonnes: Backlog → Ready → In Progress → In Review → QA Hardware → Gate Ready → Done
3. Ajouter les issues P0 ci-dessus

Voir [GITHUB-SETUP.md](GITHUB-SETUP.md) pour le détail complet.
