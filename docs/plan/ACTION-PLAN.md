# Plan d'action opérationnel — Glucose For Watch

> **Document maître** · complète [PROGRESS.md](PROGRESS.md) (suivi) et [STABILITY-GATES.md](STABILITY-GATES.md) (critères).  
> **MAJ :** 2026-05-25 · **Distribution :** sideload PC · **Cible :** v0.5.0 stable → v0.6.0 Compose

---

## Table des matières

1. [Principes](#1-principes)
2. [Chemin critique](#2-chemin-critique)
3. [Calendrier 8 semaines](#3-calendrier-8-semaines)
4. [Matrice blocs](#4-matrice-blocs)
5. [Tâches atomiques](#5-tâches-atomiques)
6. [Parallélisation](#6-parallélisation)
7. [Rituel hebdomadaire](#7-rituel-hebdomadaire)
8. [Registre des risques](#8-registre-des-risques)
9. [Automatisation vs manuel](#9-automatisation-vs-manuel)
10. [Checklist PR](#10-checklist-pr)
11. [Compose v0.6.0](#11-compose-v060)

---

## 1. Principes

| # | Principe | Application |
|---|----------|-------------|
| P1 | **Stabilité avant features** | Aucun tag sans gate · crash veille = P0 |
| P2 | **Une PR = un objectif mesurable** | Pas de mélange sync + UI + refactor |
| P3 | **Preuve avant merge** | `stability-gate.ps1` + gate bloc |
| P4 | **Soak obligatoire** | C.7 8 h bloque M7 · 4 h bloque M8 |
| P5 | **Revert rapide** | PR sync/UI revertible en < 15 min |
| P6 | **Sync = contrat sacré** | S1–S3 retest si touch `mobile/sync`, `wear/`, `feature/sync` |
| P7 | **Solo dev : séquencer** | Pas de big bang Compose |

---

## 2. Chemin critique

```
X.5 fix FGS ──► G-X ──► A P0 ──► G-A ──► B wear ──► G-B
                              └──► M mock (parallèle après G-A)
                                        │
C.7 soak 8h ◄── C.0–C.6 ◄── G-B + G-M ◄┘
    │
    ▼
G-C ──► D tests ──► G-D ──► G-M7 (v0.5.0)
                              │
                              ▼
                    F0 → F1 → F2 → F3 → G-M8 (v0.6.0)
```

**Bottleneck #1 :** PR #8 (crash FGS) — bloque tout.  
**Bottleneck #2 :** C.7 soak 8 h — bloque tag v0.5.0.  
**Bottleneck #3 :** F3 home Compose — bloque tag v0.6.0.

---

## 3. Calendrier 8 semaines

| Sem | Jours | Bloc | Livrable | Gate |
|-----|-------|------|----------|------|
| **S1** | Lun–Mer | **X** | PR #8 merge · test X.7 | **G-X** |
| S1 | Jeu–Ven | **A** | PR #9 · notifs + déconnexion | G-A |
| **S2** | Lun–Mar | **M** | PR #10 · HomeViewModel + previews | G-M |
| S2 | Mer–Ven | **B** | PR #11 · complication + i18n tuile | G-B |
| **S3** | Lun | **C.0–C.1** | Crash reg + AGP visuel | — |
| S3 | Mar–Mer | **C.2–C.3** | Complication 30m + offline 2h | — |
| S3 | Jeu | **C.4–C.6** | LOW/HI · sync 30m · réinstall | — |
| S3 | Ven soir | **C.7 start** | Lancer soak nuit | — |
| **S4** | Lun | **C.7 fin + C.8** | Logcat matin · bat. montre ≤20% | **G-C** |
| S4 | Mar–Jeu | **D** | PR #13–14 · Dexcom tests · scripts | G-D |
| S4 | Ven | **M7** | Tag `v0.5.0` | **G-M7** |
| **S5** | | **F0** | PR #15 · Gradle Compose + theme | G-F0 |
| S5–6 | | **F1–F2** | PR #16–17 · écrans simples + Dexcom | G-F1, G-F2 |
| **S7** | | **F3** | PR #18 · HomeScreen Compose | G-F3 |
| S7 | nuit | soak 4h | Post-F3 | — |
| **S8** | | **F5 + M8** | Cleanup XML · icônes · tag `v0.6.0` | **G-M8** |

*Buffer : +1 sem si C.7 ou F3 slip.*

---

## 4. Matrice blocs

| Bloc | PR | Effort | Gate | KPI clés | Fichiers chauds |
|------|-----|--------|------|----------|-----------------|
| **S** | — | continu | chaque PR | K5 | scripts/qa/* |
| **X** | #8 | 2–3 j | G-X | K1, K2 | `ActiveGlucoseSyncService.kt`, schedulers |
| **A** | #9 | 2 j | G-A | K7 | `MainActivity`, `DexcomEntryActivity`, notifs |
| **M** | #10 | 3 j | G-M | — | `MainActivity`, preview tests |
| **B** | #11 | 4 j | G-B | K3, K4 | `ComplicationUpdateNotifier`, tile wear |
| **C** | #12 | 4 sessions | **G-C** | K1–K6 | hardware · docs/qa/ |
| **D** | #13–14 | 3 j | G-D | K5 | `DexcomShareClient`, install scripts |
| **F** | #15–18 | 3–4 sem | G-M8 | K8 | `mobile/ui/*`, layouts |

---

## 5. Tâches atomiques

### Bloc X — Crash (PR #8)

| ID | Tâche | DoD | Est. |
|----|-------|-----|------|
| X.5a | `try/catch` `ForegroundServiceStartNotAllowedException` dans `onCreate` | Service démarre ou fallback sans FATAL | 2h |
| X.5b | Flag « FGS indisponible » → sync via Worker/alarm uniquement | Log explicite · pas de boucle crash | 3h |
| X.5c | Dédupliquer démarrage FGS (boot vs alarm vs MainActivity) | 1 seul owner documenté | 4h |
| X.5d | `PhoneSyncStateStore` expose état FGS degradé (optionnel UI) | Lisible dans smoke | 2h |
| X.7 | Test Robolectric/Espresso : FGS throw → service vivant | `:mobile:test` vert | 3h |
| X.3 | Repro nuit OU simulation quota | Fiche incident complétée | 8h |
| X.6 | 30 min sync répétée post-fix | 0 FATAL | 30m |

### Bloc A — P0 (PR #9)

| ID | Tâche | DoD | Est. |
|----|-------|-----|------|
| A.1 | Flow permission notifs (splash ou 1er besoin) | Accord/refus sans crash | 2h |
| A.2a | `DexcomEntryActivity` : stop sync + clear state | Identique settings | 1h |
| A.2b | Test manuel déconnexion entry → pas de FGS fantôme | K7 OK | 15m |
| A.3 | `runManualSync()` → Snackbar selon result | 3 états : OK / pending / erreur | 2h |
| A.4 | Externaliser 15+ strings Kotlin | grep strings hardcodés = 0 zones ciblées | 2h |

### Bloc M — Mock (PR #10)

| ID | Tâche | DoD | Est. |
|----|-------|-----|------|
| M.1a | `HomeUiState` data class | Tous champs hero + rows | 2h |
| M.1b | `HomeViewModel` / binder partagé | MainActivity + test | 4h |
| M.2 | Règle temps unique documentée + impl | Hero = statut = tuile (stale) | 4h |
| M.3 | 6 tests export PNG ou `@Preview` XML | `export-app-preview -AllStates` | 3h |
| M.4 | design-reference companion | PNG à jour | 2h |

### Bloc B — Wear (PR #11)

| ID | Tâche | DoD | Est. |
|----|-------|-----|------|
| B.1 | `ComplicationUpdateNotifier.notifyReadingChanged` on cache write | Tuile = complication < 45s | 4h |
| B.2 | Ligne erreur sync home OU statut ack explicite | User voit erreur Dexcom/montre | 3h |
| B.5 | `strings.xml` wear tile sync FR | Plus de "Sync" EN | 30m |
| B.3 | Documenter scheduler unique (FGS vs alarm) | architecture.md | 2h |
| B.4 | `WatchSyncVerifier` via engine | Test sync attend ack ou timeout | 4h |

### Bloc C — QA (PR #12 doc)

| ID | Procédure | Pass | Evidence |
|----|-----------|------|----------|
| C.0 | 10× ouvrir/kill app · 10× sync manuelle | 0 FATAL | logcat |
| C.1 | Screenshots 60/120/200 phone+tile | Couleurs AGP OK | docs/qa/captures/ |
| C.2 | Complication vs tuile t/5min × 6 | Écart ≤ 1 sync | notes |
| C.3 | Airplane watch 2h · phone actif | Rattrapage < 2 min | logs |
| C.7 | Charge · écran off · 8h · Dexcom ON | Hero matin · 0 FATAL | **sign-off** |
| C.8 | Montre ≤20% · sync phone | Pas crash · message dégradé | notes |

### Bloc D — Qualité (PR #13–14)

| ID | Tâche | DoD | Est. |
|----|-------|-----|------|
| D.1 | Tests auth 401, timeout, US/OUS | 5+ tests verts | 6h |
| D.2 | Supprimer popup_home_menu + strings mortes | grep unused OK | 2h |
| D.3 | install-and-verify : push/ack seq auto | FAIL si seq mismatch | 4h |
| D.6 | capture-crash-log.ps1 | 1 commande post-crash | 1h |

---

## 6. Parallélisation

| Après gate | Peut avancer en parallèle |
|------------|---------------------------|
| **G-A** | **M** (mock) et **B** (wear) — dev séparés, merge séquentiel M puis B |
| **G-B** | **C.0–C.6** pendant préparation **D.1** tests (code) |
| **G-M7** | **F0** (Gradle Compose) sans toucher écrans |
| Pendant **C.7** soak | **D.2** cleanup · **D.6** scripts · doc only |

**Interdit en parallèle :** X avec tout · C.7 avec PR touchant sync · F3 avec autre UI phone.

---

## 7. Rituel hebdomadaire

### Début de semaine (15 min)

- [ ] Lire [PROGRESS.md](PROGRESS.md) · mettre à jour statuts
- [ ] Choisir **1 bloc** cible · **1 gate** fin de semaine
- [ ] Vérifier phone+watch adb : `adb devices -l`

### Avant chaque PR

```powershell
.\scripts\qa\stability-gate.ps1 -Strict
.\gradlew.bat installWidgetG7Debug   # si touch mobile/wear
```

- [ ] Remplir [PR-CHECKLIST.md](PR-CHECKLIST.md)
- [ ] Si touch sync : noter push/ack seq avant/après

### Fin de semaine (30 min)

- [ ] Gate bloc cochée ou reportée (noter why)
- [ ] `docs/qa/` capture si session hardware
- [ ] Incident ouvert ? → fiche dans `docs/qa/incidents/`

### Nuit soak (C.7)

- [ ] Phone charge · Dexcom OK · montre connectée
- [ ] `adb logcat -c` avant coucher
- [ ] Matin : `stability-gate.ps1 -CheckLogcatOnly` + hero screenshot

---

## 8. Registre des risques

| ID | Risque | Prob. | Impact | Mitigation | Owner |
|----|--------|-------|--------|------------|-------|
| R1 | FGS quota re-crash post-fix | M | Critique | X.7 test + C.7 | X |
| R2 | Dexcom API change | F | Critique | D.1 tests · monitoring logs | D |
| R3 | Complication toujours stale | M | Moyen | B.1 + C.2 | B |
| R4 | Soak C.7 flake | M | Bloque M7 | Re-run 2 nuits · logcat | C |
| R5 | Compose F3 casse sync | M | Critique | F3 gate 4h soak · revert | F |
| R6 | QA hardware indispo | M | Retarde C | smoke strict · repro partiel | C |
| R7 | Double Gradle WSL+Studio | M | Corrupt build | 1 sync à la fois · dev.md | S |
| R8 | Batterie montre <20% crash phone | F | Moyen | C.8 | C |

---

## 9. Automatisation vs manuel

| Contrôle | Auto | Manuel | Fréquence |
|----------|------|--------|-----------|
| Unit tests | `verify_ci.sh` | — | chaque PR |
| Compile mobile/wear | verify_ci | — | chaque PR |
| Push/ack seq | hardware-smoke | — | si adb |
| FATAL logcat | stability-gate | — | chaque PR |
| Install APK | installWidgetG7Debug | — | post-change |
| AGP couleurs | — | C.1 | 1× gate |
| Soak 8h | — | C.7 | 1× M7 |
| Complication 30m | — | C.2 | 1× G-B/C |
| Offline 2h | — | C.3 | 1× G-C |
| Dexcom LOW/HI | — | C.4 | si dispo |

**Objectif S.7 :** enrichir `hardware-smoke.ps1` pour FAIL (pas WARN) sur seq mismatch.

---

## 10. Checklist PR

Voir **[PR-CHECKLIST.md](PR-CHECKLIST.md)** — copier dans description PR.

Résumé :

1. Scope une feature · lien bloc ID
2. `verify_ci.sh` vert
3. `stability-gate.ps1` vert
4. Touch sync ? → smoke + seq notés
5. Gate bloc prête ou explicitement partielle
6. Pas de secret · pas de glucose réel dans captures

---

## 11. Compose v0.6.0

| Phase | Écran | Risque sync | Gate extra |
|-------|-------|-------------|------------|
| F0 | aucun | nul | compile only |
| F1 | Legal, Notice | nul | navigation |
| F2 | Dexcom, WatchSetup | **moyen** | retest G-A déconnexion |
| F3 | **Home** | **élevé** | 30m sync + 4h soak |
| F4 | Installer | OCR | option XML |
| F5 | cleanup | faible | full smoke |

**Réutiliser :** `HomeViewModel` (Bloc M) · `WidgetG7Theme` tokens toxy · `@Preview` remplace Robolectric.

---

## Documents liés

| Doc | Rôle |
|-----|------|
| [PROGRESS.md](PROGRESS.md) | Suivi statuts · scoreboard |
| [STABILITY-GATES.md](STABILITY-GATES.md) | Critères gates détaillés |
| [PR-CHECKLIST.md](PR-CHECKLIST.md) | Template merge |
| [../qa/stability-signoff-template.md](../qa/stability-signoff-template.md) | Sign-off C.7 |
| [../qa/incidents/2026-05-25-app-crash.md](../qa/incidents/2026-05-25-app-crash.md) | Incident P0 |
| [../design/material-icons.md](../design/material-icons.md) | Icônes F5 |

---

## Scoreboard (mettre à jour chaque semaine)

| Gate | Statut | Date |
|------|--------|------|
| G-X | ☐ | |
| G-A | ☐ | |
| G-M | ☐ | |
| G-B | ☐ | |
| G-C | ☐ | |
| G-D | ☐ | |
| G-M7 | ☐ | |
| G-M8 | ☐ | |

| KPI | Actuel | Cible |
|-----|--------|-------|
| K1 FATAL | ouvert | 0 |
| K2 soak 8h | ☐ | ✅ |
| K3 sync 30m | partiel | ✅ |
| K6 QA 7/7 | 4/7 | 7/7 |
