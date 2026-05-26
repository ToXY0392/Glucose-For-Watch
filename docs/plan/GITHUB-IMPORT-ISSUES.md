> **Status 2026-05-26:** issues #1–#12 created · labels + milestones OK · Project UI manual (`project` scope required on token).

## Created issues

| # | Title |
|---|-------|
| [#1](https://github.com/ToXY0392/Glucose-For-Watch/issues/1) | [bloc-x] X.3 - Repro soak FGS |
| [#2](https://github.com/ToXY0392/Glucose-For-Watch/issues/2) | [bloc-x] X.7 - Robolectric FGS test |
| [#3](https://github.com/ToXY0392/Glucose-For-Watch/issues/3) | [bloc-c] C.7 - 8h overnight soak |
| [#4](https://github.com/ToXY0392/Glucose-For-Watch/issues/4) | [incident] FGS crash 2026-05-25 |
| [#5-#12](https://github.com/ToXY0392/Glucose-For-Watch/issues) | P1/P2 blocs A, M, B, C, D |

## GitHub Project (2 min UI)

Current git token lacks **`project`** scope. Create the board manually:

1. https://github.com/ToXY0392/Glucose-For-Watch → **Projects** → **New project**
2. Name: `Glucose For Watch v0.5 to v0.6`
3. **Board** template · columns: Backlog → Ready → In Progress → In Review → QA Hardware → Gate Ready → Done
4. **Add items** → select issues #1–#12

Or after adding `project` scope to the token: `powershell scripts/dev/bootstrap_github.ps1` (Project section).

---

## Original import (reference)

## P0 — Blockers

| Title | Bloc | Gate | Labels |
|-------|------|------|--------|
| `[bloc-x] X.3 — Repro soak FGS or quota simulation` | X | G-X | `bloc-x`, `gate-blocker`, `sync-critical`, `hardware-qa` |
| `[bloc-x] X.7 — Robolectric test FGS denied → Worker fallback` | X | G-X | `bloc-x`, `gate-blocker`, `area:mobile` |
| `[bloc-c] C.7 — 8h overnight soak + morning sign-off` | C | G-C | `bloc-c`, `gate-blocker`, `hardware-qa` |
| `[incident] FGS crash 2026-05-25 — close after G-X` | X | G-X | **Incident P0** template |

## P1 — v0.5.0

| Title | Bloc | Gate | Labels |
|-------|------|------|--------|
| `[bloc-a] A.1 — Notification permission flow` | A | G-A | `bloc-a`, `area:mobile` |
| `[bloc-a] A.3 — Manual sync snackbar feedback` | A | G-A | `bloc-a`, `area:mobile`, `area:sync` |
| `[bloc-m] M.4 — design-reference companion PNG up to date` | M | G-M | `bloc-m`, `area:ux-kit` |
| `[bloc-b] B.4 — WatchSyncVerifier via engine + ack test` | B | G-B | `bloc-b`, `area:wear`, `sync-critical` |
| `[bloc-c] C.2 — Complication vs tile every 5 min × 6` | C | G-C | `bloc-c`, `hardware-qa`, `area:wear` |
| `[bloc-c] C.3 — Watch offline 2h · catch-up` | C | G-C | `bloc-c`, `hardware-qa`, `sync-critical` |
| `[bloc-c] C.8 — Watch ≤20% battery · degraded sync` | C | G-C | `bloc-c`, `hardware-qa` |

## P2

| Title | Bloc | Gate | Labels |
|-------|------|------|--------|
| `[bloc-d] D.6 — capture-crash-log.ps1 one-command` | D | G-D | `bloc-d`, `area:infra` |

## gh CLI (after `gh auth login`)

```bash
bash .github/scripts/create-labels.sh

gh api repos/ToXY0392/Glucose-For-Watch/milestones -f title="v0.5.0 — Stable sideload" -f description="Blocs X→D · gate G-M7"
gh api repos/ToXY0392/Glucose-For-Watch/milestones -f title="v0.6.0 — Compose phone" -f description="Blocs F0→F5 · gate G-M8"

gh issue create --repo ToXY0392/Glucose-For-Watch \
  --title "[bloc-x] X.3 — Repro soak FGS or quota simulation" \
  --label "bloc-x,gate-blocker,sync-critical,hardware-qa" \
  --body "Gate G-X · see ACTION-PLAN X.3"
```

## GitHub Project

1. **Projects → New** → `Glucose For Watch — v0.5 → v0.6`
2. Columns: Backlog → Ready → In Progress → In Review → QA Hardware → Gate Ready → Done
3. Add the P0 issues above

See [GITHUB-SETUP.md](GITHUB-SETUP.md) for full details.
