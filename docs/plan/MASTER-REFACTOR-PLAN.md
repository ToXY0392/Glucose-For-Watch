# Plan maître — Widget G7 → ToXY

> **v3.1** · 2026-05-23 · **Phase 3 🔄** · Wear status Compose  
> **Suivi live :** [PROGRESS.md](PROGRESS.md)

---

## 1. En une page

| Item | Détail |
|------|--------|
| **Objectif** | Glycémie Dexcom (G6/G7) sur Wear OS — fiable, couleurs AGP, sync visible |
| **Approche** | Refonte incrémentale ; `GlucoseSyncEngine` conservé |
| **Kit UX** | [`toxy-ux-kit/`](../../toxy-ux-kit/README.md) séparé de l’app |
| **Doc** | [`docs/index.md`](../index.md) — sans doublons design |

### Jalons

| Date | Jalon | Statut |
|------|-------|--------|
| 2026-05-23 | M−1 Doc + kit v0.1 | ✅ |
| 2026-05-31 | M0 P0 tile sync + AGP + offline badge | ✅ |
| 2026-06-14 | M1 Tokens intégrés dans APK | ☐ |
| 2026-06-28 | M2 Queue offline | ☐ |
| 2026-07-19 | M3 Release 0.4.0 | ☐ |

---

## 2. Architecture

```
toxy-ux-kit/  ──export──►  mobile/ + wear/
Dexcom Share ──► phone ──► Data Layer ──► tile + complication ──► ack
                      └──► PendingPushQueue (Phase 2)
```

---

## 3. Règles design

- **Glycémie** → couleurs AGP (`GlucoseRangeResolver`, kit `agp.glucose.json`)
- **UI chrome** → ToXY (`toxy.color.json`)
- **Interdit** : mint `#34D399` sur une valeur glycémique

---

## 4. Phase −1 ✅

Doc EN · `toxy-ux-kit/` v0.1 · suppression doublons doc

---

## 5. Phase 0 ✅ (2026-05-23)

| ID | Tâche | Statut |
|----|-------|--------|
| 0.1 | Bouton sync tile → `GlucoseRefreshActivity` | ✅ |
| 0.2 | Freshness tile 45 s | ✅ |
| 0.3 | Badge montre hors portée (phone) | ✅ |
| 0.4 | Repush backoff 10/30/60/120 s | ✅ |
| 0.5 | `GlucoseRangeResolver` + AGP tile + phone hero | ✅ |
| 0.6 | Tests unitaires resolver | ✅ |
| 0.7 | QA manuelle 30 min + offline | ☐ hardware |

**Fichiers** : `GlucoseSimpleTileService.kt`, `GlucoseRange.kt`, `MainActivity.kt`, `ActiveGlucoseSyncService.kt`, `PhoneSyncStateStore.kt`

---

## 6. Phase 1 ☐ — Intégration kit → app

| ID | Tâche |
|----|-------|
| 1.1 | Export XML → `mobile/res/values/toxy_colors.xml` + `agp_glucose_colors.xml` |
| 1.2 | Retirer legacy `wg7_*` pour glycémie |
| 1.3 | AGP complication |
| 1.4 | `ToxyTileTheme.kt` |
| 1.5 | KDoc EN sync/model/tile |
| 1.6 | Corriger `dependency-catalog.yaml` |
| 1.7 | Skills Cursor theme + sync + AGP guard |

---

## 7. Phase 2 ☐ — Sync robuste

| ID | Tâche |
|----|-------|
| 2.1 | `PendingPushQueue` |
| 2.2 | `WatchConnectivityMonitor` |
| 2.3 | Flush à la reconnexion |
| 2.4 | WorkManager catch-up |
| 2.5 | Fix complication stale |
| 2.6 | Tests offline/online |

---

## 8. Phase 3 ☐ — Release 0.4.0

Figma ToXY · QA G6/G7 · CI · Compose wear status · rebrand

---

## 9. Tests régression

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
```

R1–R8 : voir [`CONTRIBUTING.md`](../../CONTRIBUTING.md)

---

## 10. Prochaine action

**Phase 3** — QA hardware (0.7) + rebrand ToXY v0.4.0

---

*Plan vivant — [`docs/index.md`](../index.md)*
