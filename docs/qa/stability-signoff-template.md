# Stability sign-off — Glucose For Watch

> Copier ce fichier : `docs/qa/YYYY-MM-DD-stability-signoff.md`  
> Référence gates : [STABILITY-GATES.md](../plan/STABILITY-GATES.md)

---

## Contexte

| Champ | Valeur |
|-------|--------|
| Date | |
| Version app | |
| Commit / tag | |
| Phone | modèle · serial · Android |
| Watch | modèle · Wear OS |
| Opérateur | |
| Gate visée | G-X / G-A / G-C / G-M7 / G-M8 |

---

## KPI

| KPI | Seuil | Résultat | OK |
|-----|-------|----------|-----|
| K1 Crash fatal | 0 | | ☐ |
| K2 Soak 8 h (C.7) | hero matin + 0 FATAL | | ☐ |
| K3 Sync 30 min | push/ack OK | | ☐ |
| K5 verify_ci | PASS | | ☐ |

---

## Scénarios Bloc C

| ID | Scénario | Résultat | Evidence |
|----|----------|----------|----------|
| C.0 | Crash reg 30 min | | |
| C.1 | AGP couleurs | | |
| C.2 | Complication 30 min | | |
| C.3 | Offline 2 h | | |
| C.4 | LOW/HI | | |
| C.5 | Sync 30 min | | |
| C.6 | Réinstall + tuile | | |
| C.7 | **Soak nuit 8 h** | | |
| C.8 | Batterie montre ≤ 20 % | | |

---

## Automatisé

```text
stability-gate.ps1 : PASS / FAIL
hardware-smoke.ps1 : PASS / FAIL / WARN
logcat FATAL       : none / (coller extrait)
```

---

## Incidents ouverts

| Date | Incident | Statut |
|------|----------|--------|
| | | |

---

## Décision

- [ ] **GO** — gate validée
- [ ] **NO-GO** — bloquer tag / merge

Rationale :

Sign-off :
