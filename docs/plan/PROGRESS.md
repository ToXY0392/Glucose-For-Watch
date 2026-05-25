# Suivi du plan — Glucose For Watch

> **Dernière MAJ :** 2026-05-25  
> **Distribution :** PC uniquement (`installWidgetG7Debug`) — pas de Play Store  
> **Docs plan :** [ACTION-PLAN.md](ACTION-PLAN.md) (opérationnel) · [STABILITY-GATES.md](STABILITY-GATES.md) · [PR-CHECKLIST.md](PR-CHECKLIST.md)

---

## Scoreboard (MAJ hebdo)

| Gate | Statut | Date | Bloque |
|------|--------|------|--------|
| **G-X** | 🔄 | 2026-05-25 | tout · X.6 ✅ · X.3 → C.7 |
| G-A | 🔄 | 2026-05-25 | M, B, C · A.4 ✅ |
| G-M | 🔄 | 2026-05-25 | B · M.1–M.3 ✅ |
| G-B | 🔄 | 2026-05-25 | C · B.1–B.3/B.5 ✅ |
| **G-C** | ☐ | | **M7** |
| G-D | 🔄 | 2026-05-25 | M7 · D.1–D.3 ✅ |
| **G-M7** v0.5.0 | ☐ | | F |
| G-M8 v0.6.0 | ☐ | | — |

| KPI | Actuel | Cible | Preuve |
|-----|--------|-------|--------|
| K1 Crash fatal | **1 incident ouvert** | 0 | [incident](../qa/incidents/2026-05-25-app-crash.md) |
| K2 Soak 8 h | ☐ | ✅ C.7 | sign-off |
| K3 Sync 30 min | ✅ | ✅ | [X.6 soak](../qa/soak-runs/2026-05-25_1458-X.6-soak.md) |
| K4 S1–S3 | ✅ session 05-24 | ✅ post-PR sync | logs |
| K5 Unit tests | CI OK + Dexcom 9 tests | 100 % | verify_ci |
| K6 QA G7 | **4/7** | 7/7 | matrice C |
| K7 Déconnexion | ✅ | ✅ | A.2 |

---

## Objectifs version

| Version | Objectif | Gate | Semaine cible |
|---------|----------|------|---------------|
| **v0.5.0** sideload | Stable · QA 7/7 · install PC | G-M7 | S4 |
| **v0.6.0** Compose | Phone UI Compose M3 · sync OK | G-M8 | S8 |

**Chemin critique :** X.5 → G-X → … → **C.7 soak** → G-M7 → F3 → G-M8  
→ Détail : [ACTION-PLAN §2](ACTION-PLAN.md#2-chemin-critique)

---

## État actuel

| Indicateur | Valeur |
|------------|--------|
| App | v0.4.0 (vc 23) |
| Phone | Pixel 8a · Android 14+ |
| Watch | Pixel Watch 2 (session 05-24) |
| Crash P0 | FGS mitigé · X.6 0 FATAL | |
| Phone UI | XML · Material 3 |
| Wear UI | Compose M3 ✅ |

---

## Architecture du plan — 9 blocs · 12 gates · 2 jalons

```
S  Stabilité (transverse, chaque PR)
├── v0.5.0 ─────────────────────────
│   X  Crash          PR #8   → G-X      ★ CRITIQUE
│   A  P0 fiabilité   PR #9   → G-A
│   M  Mock user      PR #10  → G-M      ║ parallèle après G-A
│   B  Sync/wear      PR #11  → G-B      ║
│   C  QA + soak      PR #12  → G-C      ★ C.7 bloque M7
│   D  Qualité        PR #13-14 → G-D
│   M7 tag v0.5.0
└── v0.6.0 ─────────────────────────
    F  Compose M3     PR #15-18 → G-F* → M8
```

**Ordre strict v0.5.0 :** S · X → A → (M ∥ prep B) → B → C → D → M7  
**Règle d'or :** [PR-CHECKLIST.md](PR-CHECKLIST.md) + `stability-gate.ps1` avant **chaque** merge

---

## Bloc S — Stabilité transverse

| ID | Tâche | Statut |
|----|-------|--------|
| S.1 | STABILITY-GATES.md | 🔄 |
| S.2 | stability-gate.ps1 | 🔄 |
| S.3 | stability-signoff-template | 🔄 |
| S.4 | Test FGS fallback (X.7) | ✅ |
| S.5 | Gate dans dev/setup.md / CONTRIBUTING | ✅ |
| S.6 | PR-CHECKLIST.md | 🔄 |
| S.7 | hardware-smoke : FAIL si push≠ack (pas WARN) | ✅ |
| S.8 | ACTION-PLAN.md (calendrier + risques) | 🔄 |

```powershell
.\scripts\qa\stability-gate.ps1 -Strict    # avant merge
.\scripts\qa\capture-crash-log.ps1         # apres crash
```

---

## Bloc X — Crash phone (P0) · PR #8 · Gate G-X

> [Incident](../qa/incidents/2026-05-25-app-crash.md) · veille · charge · `ActiveGlucoseSyncService:32`

| ID | Tâche | Statut | Est. |
|----|-------|--------|------|
| X.1 | Logcat capturé | ✅ | — |
| X.2 | Fiche veille + charge | ✅ | — |
| X.3 | Repro soak / quota FGS | ☐ | 8h |
| X.4 | Cause racine FGS | ✅ | — |
| X.5a | try/catch startForeground | ✅ | 2h |
| X.5b | Fallback Worker/alarm | ✅ | 3h |
| X.5c | Dédupliquer schedulers FGS | ✅ | 4h |
| X.6 | 30 min sans crash | ✅ | [soak](../qa/soak-runs/2026-05-25_1458-X.6-soak.md) |
| X.7 | Test unitaire FGS refusé | ✅ | 3h |

**DoD G-X :** X.5a–c + X.6 + X.7 + 0 FATAL · [critères](STABILITY-GATES.md#g-x--après-pr-8-bloc-x)

---

## Bloc A — P0 fiabilité · PR #9 · Gate G-A

| ID | Tâche | Statut | Est. |
|----|-------|--------|------|
| A.1 | POST_NOTIFICATIONS runtime | ✅ | 2h |
| A.2 | Déconnexion entry = settings | ✅ | 1h |
| A.3 | Sync manuelle → result réel | ✅ | 2h |
| A.4 | Strings → strings.xml | ✅ | Dexcom entry/settings |

**DoD G-A :** G-X ✅ + K7 déconnexion + notifs + [STABILITY-GATES § G-A](STABILITY-GATES.md#g-a--après-pr-9-bloc-a)

---

## Bloc M — Mock utilisateur · PR #10 · Gate G-M

| ID | Tâche | Statut | Est. |
|----|-------|--------|------|
| M.1 | HomeUiState + HomeViewModel (pont F0.3) | ✅ | 6h |
| M.2 | Parité hero ↔ tuile (temps, stale) | ✅ | 4h |
| M.3 | 6 états preview exportables | ✅ | 3h |
| M.4 | design-reference companion | ☐ | 2h |
| M.5 | Doc previews dev/setup.md | ✅ | dev/setup.md |

**DoD G-M :** G-A ✅ + previews + smoke S1–S3 · peut merger **avant** B si B pas prêt

---

## Bloc B — Sync & wear · PR #11 · Gate G-B

| ID | Tâche | P | Statut | Est. |
|----|-------|---|--------|------|
| B.1 | Cache complication invalidé | P1 | ✅ | 4h |
| B.2 | UI push vs ack / erreur visible | P1 | ✅ | 3h |
| B.5 | Tuile wear FR | P1 | ✅ | 30m |
| B.3 | Doc scheduler unique | P2 | ✅ | 2h |
| B.4 | WatchSyncVerifier → engine | P2 | ☐ | 4h |

**DoD G-B :** complication ≤45s lag · tuile FR · smoke seq · [STABILITY-GATES § G-B](STABILITY-GATES.md#g-b--après-pr-11-bloc-b)

---

## Bloc C — QA hardware · PR #12 · Gate G-C ★

> **Sans C.7 (soak 8 h) = pas de tag v0.5.0**

| ID | Scénario | Durée | KPI | Statut |
|----|----------|-------|-----|--------|
| C.0 | Crash reg · kill/relaunch · sync ×10 | 30m | K1 | 🔄 | auto 3+10 + [session](../qa/sessions/2026-05-25_1605-bloc-c-automated.md) |
| C.1 | AGP 60/120/200 | 2h | visuel | ☐ |
| C.2 | Complication vs tuile | 30m | K3 | ☐ |
| C.3 | Offline montre 2h | 2–3h | rattrapage | ☐ |
| C.4 | LOW / HI | — | affichage | ☐ |
| C.5 | Sync continue | 30m | K3 | 🔄 | X.6 ✅ · C.5 auto optional |
| C.6 | Réinstall APK + tuile | 1h | K4 | ☐ |
| **C.7** | **Soak nuit charge** | **8h** | **K2** | ☐ |
| C.8 | Montre bat. ≤20% | 1h | K1 | ☐ |

**Procédure C.7 :** [ACTION-PLAN §7](ACTION-PLAN.md#7-rituel-hebdomadaire) · livrable [sign-off](../qa/stability-signoff-template.md)

---

## Bloc D — Qualité · PR #13–14 · Gate G-D → M7

| ID | Tâche | Statut | Est. |
|----|-------|--------|------|
| D.1 | Tests DexcomShareClient (5+ cas) | ✅ | 6h |
| D.2 | Cleanup code mort home | ✅ | 2h |
| D.3 | install-and-verify push/ack auto | ✅ | 4h |
| D.4 | Doc install PC unique | ✅ | dev/setup.md |
| D.5 | Plan docs index | 🔄 | — |
| D.6 | capture-crash-log.ps1 | 🔄 | 1h |
| D.7 | stability-gate dans dev/setup.md | ✅ | dev/setup.md |

**Tag v0.5.0 :** [G-M7 checklist](STABILITY-GATES.md#g-m7--tag-v050)

---

## Bloc F — Compose M3 · PR #15–18 · Gate G-M8

| Phase | PR | Gate | Durée |
|-------|-----|------|-------|
| F0 Fondations | #15 | G-F0 | 2–3j |
| F1 Legal/Notice | #16 | G-F1 | 2–3j |
| F2 Dexcom/Watch | #17 | G-F2 | 1 sem |
| F3 **Home** | #18 | G-F3 + soak 4h | 1–1,5 sem |
| F4 Installer (opt.) | — | — | opt. |
| F5 Cleanup + icônes | #18+ | G-M8 | 2–3j |

Détail écrans + tâches : [ACTION-PLAN §11](ACTION-PLAN.md#11-compose-v060) · [PROGRESS Bloc F historique](ACTION-PLAN.md)

**Interdit avant G-M7 :** toucher MainActivity Compose

---

## Registre décisions

| Date | Décision | Raison |
|------|----------|--------|
| 2025-05-25 | Pas Play Store · sideload PC | Usage perso / dev |
| 2025-05-25 | C.7 soak 8h bloque M7 | Crash veille reproduit |
| 2025-05-25 | Compose après v0.5.0 | Stabilité d'abord |
| 2025-05-25 | WearInstaller reste XML (F4 opt.) | OCR = risque |

---

## Workflow quotidien

```powershell
# Dev loop
.\gradlew.bat installWidgetG7Debug

# Avant merge PR
.\scripts\qa\stability-gate.ps1 -Strict

# Session QA
.\scripts\qa\hardware-smoke.ps1
.\scripts\qa\qa-session-c.ps1
.\scripts\qa\tail-sync-logs.ps1

# Soak X.6 (30 min) ou C.7 (8 h)
.\scripts\qa\soak-monitor.ps1                      # X.6
.\scripts\qa\soak-monitor.ps1 -DurationMinutes 480 -Label C.7
# matin :
.\scripts\qa\capture-crash-log.ps1
.\scripts\qa\stability-gate.ps1 -CheckLogcatOnly
```

---

## Backlog (post-v0.6)

Play Store · OAuth Dexcom v3 · mmol/L · watch face · G6 QA · Installer Compose
