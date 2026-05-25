# Gates de stabilité — Glucose For Watch

> **Rôle :** critères **bloquants** entre chaque bloc du [plan PROGRESS.md](PROGRESS.md).  
> **Règle :** aucun tag (`v0.5.0`, `v0.6.0`) sans **Go** signé sur la gate correspondante.

---

## KPI stabilité (Definition of Stable)

| KPI | Seuil v0.5.0 | Mesure |
|-----|--------------|--------|
| **K1 Crash fatal phone** | 0 sur scénarios obligatoires | `adb logcat AndroidRuntime:E` · pas de « a cessé de fonctionner » |
| **K2 Soak nuit** | 8 h phone en charge, écran off, Dexcom ON | Logcat + hero glucose le matin |
| **K3 Sync 30 min** | push/ack seq alignés · drift phone/watch ≤ 1 lecture | `hardware-smoke.ps1` + observation |
| **K4 Sync S1–S3** | ✅ après chaque PR touchant `mobile/sync/` ou `wear/` | Session hardware ou smoke enrichi |
| **K5 Tests unitaires** | 100 % verts | `./gradlew test` + `verify_ci.sh` |
| **K6 QA matrice G7** | 7/7 + session 0 crash | [Bloc C](PROGRESS.md#bloc-c--qa-hardware-stabilité) |
| **K7 Déconnexion Dexcom** | sync stoppée · prefs cohérentes · pas de FGS fantôme | Test manuel post A.2 |
| **K8 Compose (v0.6)** | sync inchangée après chaque phase F | Smoke S1–S3 sans régression |

---

## Pyramide de tests

```
                    ┌─────────────────────┐
                    │  Soak 8h + QA 7/7   │  ← tag v0.5.0 (M7)
                    └──────────┬──────────┘
               ┌───────────────┴───────────────┐
               │  Hardware sessions C.0–C.3    │  ← hebdo / par bloc
               └───────────────┬───────────────┘
          ┌────────────────────┴────────────────────┐
          │  stability-gate.ps1 (smoke + logcat)      │  ← chaque PR
          └────────────────────┬────────────────────┘
     ┌─────────────────────────┴─────────────────────────┐
     │  verify_ci.sh (unit + compile)                     │  ← chaque commit
     └─────────────────────────────────────────────────────┘
```

---

## Gate de sortie par bloc

### G-X — après PR #8 (Bloc X)

| # | Critère | Commande / procédure |
|---|---------|----------------------|
| 1 | Test unitaire : FGS refusé → pas de crash, fallback Worker | `:mobile:test` (test à ajouter X.7) |
| 2 | Repro soak : 8 h charge + écran off OU repro quota FGS artificiel | X.3 |
| 3 | 0 `FATAL EXCEPTION` logcat post-soak | `stability-gate.ps1 -CheckLogcat` |
| 4 | Sync manuelle + auto OK le lendemain | hero + tuile |

**Bloquant M7 si un seul ☐**

---

### G-A — après PR #9 (Bloc A)

| # | Critère |
|---|---------|
| 1 | G-X toujours ✅ |
| 2 | Notif permission : accord/refus géré sans crash |
| 3 | Déconnexion entry **et** settings : sync off, `PhoneSyncStateStore` vide |
| 4 | Sync manuelle : Snackbar succès **ou** erreur (jamais faux succès) |
| 5 | `verify_ci.sh` vert |

---

### G-M — après PR #10 (Bloc M)

| # | Critère |
|---|---------|
| 1 | G-A ✅ |
| 2 | 6 états preview exportables |
| 3 | Parité hero/tuile sur 1 session visuelle (120 mg/dL min.) |
| 4 | Pas de régression sync (smoke S1–S3) |

---

### G-B — après PR #11 (Bloc B)

| # | Critère |
|---|---------|
| 1 | G-M ✅ |
| 2 | Complication = tuile après sync forcée (30 min sample) |
| 3 | Tuile FR · pas de crash wear |
| 4 | smoke push/ack seq |

---

### G-C — après PR #12 (Bloc C) — **gate principale v0.5.0**

Sessions obligatoires :

| ID | Scénario | Durée | KPI |
|----|----------|-------|-----|
| C.0 | Crash reg · sync répétée · cycle vie app | 30 min | K1 |
| C.1 | AGP 60/120/200 phone + tile | 2 h | visuel |
| C.2 | Complication vs tuile | 30 min | K3 |
| C.3 | Offline montre 2 h · phone actif · reconnect | 2–3 h | rattrapage |
| C.4 | LOW / HI si disponible | — | affichage |
| C.5 | Sync continue | 30 min | K3 |
| C.6 | Réinstall APK PC + re-add tuile/complication | 1 h | K4 |
| **C.7** | **Soak nuit** phone charge · Dexcom · sync auto | **8 h** | **K2** |
| C.8 | Montre batterie ≤ 20 % · mode dégradé | 1 h | pas de crash phone |

Livrable : `docs/qa/YYYY-MM-DD-stability-signoff.md` + captures + extrait logcat

---

### G-D — après PR #13–14 (Bloc D)

| # | Critère |
|---|---------|
| 1 | G-C ✅ (dont C.7 soak) |
| 2 | Tests `DexcomShareClient` verts |
| 3 | `install-and-verify.ps1` + checks push/ack auto |
| 4 | `stability-gate.ps1` documenté dans dev.md |

---

### G-M7 — tag v0.5.0

Checklist **Go / No-Go** :

- [ ] Gates G-X → G-D toutes ✅
- [ ] KPI K1–K7 validés
- [ ] Incident [2026-05-25-app-crash.md](../qa/incidents/2026-05-25-app-crash.md) clos (X.6 + C.7)
- [ ] `./gradlew test` + `verify_ci.sh` vert sur commit tagué
- [ ] `hardware-smoke.ps1` OK (pas de FAIL critiques)
- [ ] Matrice G7 7/7 signée

---

## Gates Compose (v0.6.0)

| Gate | Après | Critère minimal |
|------|-------|-----------------|
| **G-F0** | PR #15 | Compile · tests verts · **aucun écran migré** · sync identique |
| **G-F1** | PR #16 | Legal/Notice Compose · navigation OK · smoke |
| **G-F2** | PR #17 | Dexcom connect/disconnect · WatchSetup · **G-A retest déconnexion** |
| **G-F3** | PR #18 | Home Compose · sync 30 min · S1–S3 · **pas de régression K2** (soak 4 h min.) |
| **G-M8** | tag v0.6.0 | G-F0→F3 ✅ · K8 · smoke complet |

**Règle Compose :** 1 écran migré = 1 gate · pas de merge F2+F3 en une PR.

---

## Outils

| Script | Usage |
|--------|--------|
| `bash scripts/dev/verify_ci.sh` | Gate CI · chaque PR |
| `.\scripts\qa\stability-gate.ps1` | CI + smoke + FATAL logcat |
| `.\scripts\qa\capture-crash-log.ps1` | Post-crash · alimenter fiche incident |
| `.\scripts\qa\hardware-smoke.ps1` | Push/ack, hero, watch health |
| `.\scripts\qa\install-and-verify.ps1` | Install + checklist |
| `.\scripts\qa\tail-sync-logs.ps1` | Debug sync / crash |

Plan opérationnel : [ACTION-PLAN.md](ACTION-PLAN.md) · Checklist merge : [PR-CHECKLIST.md](PR-CHECKLIST.md)

---

## Politique rollback

| Situation | Action |
|-----------|--------|
| FATAL post-merge PR sync | Revert PR · incident doc · G-X retest |
| Soak C.7 échoue | Pas de tag M7 · retour Bloc X/B |
| Compose F* casse sync | Revert écran migré · XML restauré |
| WARN smoke non critique | Noter · fix avant M7 si touché sync |

---

## Sign-off

| Gate | Date | Validateur | OK |
|------|------|------------|-----|
| G-X | | | ☐ |
| G-A | | | ☐ |
| G-M | | | ☐ |
| G-B | | | ☐ |
| G-C | | | ☐ |
| G-D | | | ☐ |
| G-M7 v0.5.0 | | | ☐ |
| G-M8 v0.6.0 | | | ☐ |
