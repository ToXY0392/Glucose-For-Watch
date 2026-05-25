# Import issues P0 — Glucose For Watch

> Créer via **Issues → New issue → Bloc task (plan)** ou `gh issue create` après rename repo.  
> Repo: `ToXY0392/Glucose-For-Watch` · Milestone: **v0.5.0 — Stable sideload**

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
