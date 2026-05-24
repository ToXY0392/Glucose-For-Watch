# Plan maître — Widget G7 → ToXY

> **v3.3** · 2026-05-23 · **Phase 4 🔄** · AGP lint CI  
> **Suivi live :** [PROGRESS.md](PROGRESS.md)

---

## 1. En une page

| Item | Détail |
|------|--------|
| **Objectif** | Glycémie Dexcom (G6/G7) sur Wear OS — fiable, couleurs AGP, sync visible |
| **Approche** | Refonte incrémentale ; `GlucoseSyncEngine` conservé |
| **Kit UX** | [`toxy-ux-kit/`](../../toxy-ux-kit/README.md) séparé de l’app |
| **Doc** | [`docs/index.md`](../index.md) — sans doublons design |
| **App** | **ToXY** v0.4.0 (tag `v0.4.0`) |

### Jalons

| Date | Jalon | Statut |
|------|-------|--------|
| 2026-05-23 | M−1 Doc + kit v0.1 | ✅ |
| 2026-05-31 | M0 P0 tile sync + AGP + offline badge | ✅ |
| 2026-06-14 | M1 Tokens intégrés dans APK | ✅ |
| 2026-06-28 | M2 Queue offline | ✅ code · ⏸ QA |
| 2026-07-19 | M3 Release 0.4.0 | ✅ tag · ⏸ QA sign-off |

---

## 2. Architecture

```
toxy-ux-kit/  ──export──►  mobile/ + wear/
Dexcom Share ──► phone ──► Data Layer ──► tile + complication ──► ack
                      └──► PendingPushQueue
```

---

## 3. Règles design

- **Glycémie** → couleurs AGP (`GlucoseRangeResolver`, kit `agp.glucose.json`)
- **UI chrome** → ToXY (`toxy.color.json`)
- **Interdit** : mint `#34D399` sur une valeur glycémique

---

## 4. Phases −1 → 2 ✅

Voir [PROGRESS.md](PROGRESS.md) pour le détail. Code complet ; tests unitaires / mock OK.

---

## 5. Phase 3 🔄 — Release 0.4.0

| ID | Tâche | Statut |
|----|-------|--------|
| 3.1 | Design reference HTML (Figma optionnel) | ✅ |
| 3.2 | Wear status Compose M3 + `ToxyWearColorScheme` | ✅ |
| 3.3 | QA matrice G6/G7 | ⏸ reportée |
| 3.4 | CI GitHub Actions | ✅ |
| 3.5 | Templates Issue / PR | ✅ |
| 3.6 | Rebrand ToXY + tag v0.4.0 | ✅ |

---

## 6. Tests régression

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
```

R1–R8 : voir [`CONTRIBUTING.md`](../../CONTRIBUTING.md)

---

## 7. Phase 4 — Maintenance

| ID | Tâche | Statut |
|----|-------|--------|
| 4.1 | AGP color lint + CI | ✅ |
| 4.2 | Token JSON validate + CI | ✅ |
| 4.3 | CI sur branche `rebuild` | ✅ |
| 4.4 | Pill semantic colors → kit v0.2 | ✅ |

---

## 8. Prochaine action

**QA hardware** quand dispo — refonte **terminée** (design: `toxy-ux-kit/design-reference/index.html`)

---

*Plan vivant — [`docs/index.md`](../index.md)*
