# Suivi du plan — Widget G7 → ToXY

> **Dernière MAJ :** 2026-05-23  
> **Phase en cours :** **clôture** — refonte terminée · QA hardware en attente  
> **Plan détaillé :** [MASTER-REFACTOR-PLAN.md](MASTER-REFACTOR-PLAN.md)

---

## Progression globale

```
Phase −1  ████████████████████  100%  ✅ Fondations
Phase 0   ████████████████████  100%  ✅ Code · QA hardware reportée
Phase 1   ████████████████████  100%  ✅ Kit intégré
Phase 2   ████████████████████  100%  ✅ Sync robuste + tests mock
Phase 3   ████████████████████  100%  ✅ Design ref HTML
Phase 4   ████████████████████  100%  ✅ Maintenance
─────────────────────────────────────────────
Total     ████████████████████  100%  (32/32 · QA hardware en attente)
```

| Indicateur | Valeur |
|------------|--------|
| App | v0.4.0 |
| Kit UX | v0.2.0 |
| Prochaine tâche | QA hardware quand dispo ([script](../../scripts/qa/install-and-verify.ps1)) |
| Jalon cible | **M2** QA · **M3** 2026-07-19 |

---

## Timeline jalons

| Jalon | Date cible | Contenu | Statut |
|-------|------------|---------|--------|
| **M−1** | 2026-05-23 | Doc EN + kit UX v0.1 | ✅ |
| **M0** | 2026-05-31 | Tile sync + AGP + badge offline | ✅ |
| **M1** | 2026-06-14 | Tokens ToXY intégrés dans l’APK | ✅ |
| **M2** | 2026-06-28 | Rattrapage offline 2 h | ✅ code · ⏸ QA reportée |
| **M3** | 2026-07-19 | Release v0.4.0 | ✅ tag · ⏸ QA sign-off reporté |

---

## Phase −1 — Fondations ✅

| # | Tâche | Statut |
|---|-------|--------|
| −1.1 | Documentation EN ([`docs/index.md`](../index.md)) | ✅ |
| −1.2 | Kit UX autonome [`toxy-ux-kit/`](../../toxy-ux-kit/README.md) v0.1 | ✅ |
| −1.3 | Tokens JSON (ToXY + AGP) | ✅ |
| −1.4 | Specs composants (tile, home, sync) | ✅ |
| −1.5 | Script export Android colors | ✅ |
| −1.6 | Suppression doublons doc | ✅ |

---

## Phase 0 — Correctifs P0 ✅ (code)

| # | Tâche | Statut | Vérification |
|---|-------|--------|--------------|
| 0.1 | Bouton sync sur la tile | ✅ | Tap `↻ Sync` → refresh phone |
| 0.2 | Freshness tile 45 s | ✅ | Tile se rafraîchit périodiquement |
| 0.3 | Badge montre hors portée (phone) | ✅ | Message après 3 push ratés |
| 0.4 | Repush backoff 10/30/60/120 s | ✅ | Ne s’arrête plus au 1er échec |
| 0.5 | `GlucoseRangeResolver` + AGP tile/phone | ✅ | 120→vert, 200→jaune, 60→rouge |
| 0.6 | Tests unitaires resolver | ✅ | `./gradlew :core:model:test` |
| 0.7 | QA manuelle 30 min + offline 1 h | ⏸ | Reportée — [install-and-verify.ps1](../../scripts/qa/install-and-verify.ps1) |

### Checklist QA Phase 0 (0.7) — ⏸ reportée

À cocher quand phone + montre disponibles :

- [ ] Install : phone + watch connectés en adb
- [ ] Tile : bouton sync visible et fonctionnel
- [ ] Valeur tile colorée AGP (pas mint)
- [ ] Phone hero coloré AGP
- [ ] Sync 30 min continue sans dérive
- [ ] Montre offline 1 h → badge phone clair → reconnect → rattrapage

---

## Phase 1 — Intégration kit → app ✅

| # | Tâche | Statut | Livrable |
|---|-------|--------|----------|
| 1.1 | Export XML tokens | ✅ | `mobile/.../toxy_colors.xml`, `agp_glucose_colors.xml` |
| 1.2 | Retirer legacy `wg7_*` glycémie | ✅ | `GlucoseSnapshot` AGP ; `wg7_*` → alias chrome |
| 1.3 | AGP sur complication | ✅ | RANGED_VALUE 40–400 ; teinte texte = cadran (limit API) |
| 1.4 | `ToxyTileTheme.kt` (wear) | ✅ | Chrome centralisé |
| 1.5 | KDoc EN sync / model / tile | ✅ | Fichiers sync + tile touchés |
| 1.6 | Corriger `dependency-catalog.yaml` | ✅ | Chemins `feature/dexcom-share/` |
| 1.7 | Skills Cursor (theme, sync, AGP) | ✅ | `.cursor/skills/` |

---

## Phase 2 — Sync robuste ✅

| # | Tâche | Statut | Critère OK |
|---|-------|--------|------------|
| 2.1 | `PendingPushQueue` | ✅ | Lecture persistée si push fail |
| 2.2 | `WatchReconnectDetector` | ✅ | Détecte reconnect (sync pass + peer) |
| 2.3 | Flush queue à la reconnexion | ✅ | `PendingPushFlusher` + `onPeerConnected` |
| 2.4 | WorkManager catch-up | ✅ | `PhoneGlucoseSyncWorker` flush pending |
| 2.5 | Fix complication stale | ✅ | `WearGlucoseSurfaceModelFactory` = tile |
| 2.6 | Tests mock offline/online | ✅ | 10 tests sync + queue + reconnect + wear display |
| **0.7** | **QA manuelle 30 min + offline 1–2 h** | ⏸ reportée | Quand hardware dispo |

**Gate M2 :** montre offline 2 h → reconnect → sync auto sans action manuelle.

---

## Phase 3 — Release v0.4.0 🔄

| # | Tâche | Statut |
|---|-------|--------|
| 3.1 | Design reference (HTML, remplace Figma) | ✅ | `design-reference/index.html` · Figma optionnel |
| 3.2 | Wear status screen (Compose M3) | ✅ | `WearStatusScreen` + `ToxyWearColorScheme` |
| 3.3 | QA matrice G6 + G7 (14 cas) | ⏸ | [QA-MATRIX-G6-G7.md](QA-MATRIX-G6-G7.md) — reportée |
| 3.4 | CI GitHub Actions | ✅ | `.github/workflows/ci.yml` + `verify_ci.sh` étendu |
| 3.5 | Templates Issue / PR | ✅ | `.github/ISSUE_TEMPLATE/`, `pull_request_template.md` |
| 3.6 | Rebrand ToXY + tag v0.4.0 | ✅ | `ToXY` v0.4.0 · tag `v0.4.0` |

### Matrice QA G6/G7 (Phase 3.3)

Procédures détaillées : **[QA-MATRIX-G6-G7.md](QA-MATRIX-G6-G7.md)** · install : `.\scripts\qa\install-and-verify.ps1`

| Cas | G6 | G7 |
|-----|----|----|
| Share US | ☐ | ☐ |
| Share OUS | ☐ | ☐ |
| Couleurs AGP | ☐ | ☐ |
| Tile + sync | ☐ | ☐ |
| Offline → reconnect | ☐ | ☐ |
| Complication | ☐ | ☐ |
| LOW / HI | ☐ | ☐ |

---

## Tests à chaque étape

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
.\gradlew.bat installWidgetG7Debug   # si hardware dispo
```

| # | Test | Phase | Statut |
|---|------|-------|--------|
| T1 | Install fresh | Toutes | ☐ |
| T2 | Dexcom Share connect | Toutes | ☐ |
| T3 | Sync 30 min | 0, 2 | ☐ |
| T4 | Offline 1 h / 2 h | 0, 2 | ☐ |
| T5 | Tap sync tile < 30 s | 0 | ☐ |
| T6 | Complication = tile | 2 | ☐ |
| T7 | LOW / HI AGP | 1 | ☐ |
| T8 | 60/120/200/300 mg/dL couleurs | 0 | ☐ |

---

## Phase 4 — Maintenance ✅ (code)

| # | Tâche | Statut |
|---|-------|--------|
| 4.1 | `lint-agp-colors.py` + CI | ✅ |
| 4.2 | `tokens-validate.py` + CI | ✅ |
| 4.3 | CI branche `rebuild` | ✅ |
| 4.4 | Mobile semantic colors → tokens | ✅ | kit v0.2 · pills + aurora |

---

## Definition of Done (refonte complète)

- [x] Bouton sync tile ≥ 48 dp
- [x] AGP sur tile + phone hero (resolver)
- [x] AGP sur complication (RANGED_VALUE ; teinte texte = cadran)
- [x] Kit tokens intégrés (pas de hardcode legacy glycémie)
- [x] Offline queue + reconnect flush (code)
- [ ] Offline 2 h → auto catch-up (⏸ QA hardware reportée)
- [ ] Matrice G6/G7 signée (⏸ reportée)
- [x] CI green · v0.4.0 taguée

---

## Comment mettre à jour ce fichier

1. Cocher une tâche terminée : remplacer `☐` par `✅`
2. Mettre à jour la barre de progression en tête
3. Ajuster **Phase en cours** et **Prochaine tâche**
4. Commit avec message du type : `docs: progress — phase 1.1 done`

---

*Vue de suivi — ne remplace pas le [plan maître](MASTER-REFACTOR-PLAN.md).*
