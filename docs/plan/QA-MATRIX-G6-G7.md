# Matrice QA G6 / G7 — Glucose For Watch v0.4.0

> Cocher après test **réel** sur capteur G6 ou G7 avec Dexcom Share activé.  
> **Install + vérif auto :** `.\scripts\qa\install-and-verify.ps1`  
> **Smoke auto :** `.\scripts\qa\hardware-smoke.ps1`  
> **Logs sync (tile ↻) :** `.\scripts\qa\tail-sync-logs.ps1`  
> **Build seul :** `.\gradlew.bat installWidgetG7Debug` (serials dans `local.properties`)

| Date | Testeur | Phone | Watch | App version | Sprint sync P0 |
|------|---------|-------|-------|-------------|----------------|
| 2026-05-24 | auto+manuel | Pixel 8a | Pixel Watch 2 | 0.4.0 (vc 23) | PR #1–#4 ✅ · session [log](../qa/2026-05-24-hardware-session.md) |

---

## Prérequis communs

- Dexcom Share activé sur le compte test (G7 prioritaire ; G6 si disponible)
- **Glucose For Watch** installé phone + watch (`applicationId` `com.widgetg7.mobile`)
- Tile **Glycémie** + complication SHORT_TEXT sur la montre
- Phone : batterie / sync arrière-plan autorisés pour l’app
- `local.properties` : `sdk.dir`, `widgetg7.adb.phone.serial`, `widgetg7.adb.watch.serial`
- Tile layout attendu : `simple-tile-v9-round-centered` (Wear OS rond, petits écrans)

---

## Matrice principale (7 cas)

| # | Cas | G6 | G7 | Procédure (ref. B.1) | OK si |
|---|-----|----|----|----------------------|-------|
| 1 | Share US | ☐ | ☐ | B.1.2 · compte Share US · login Dexcom · sync 15 min | Valeur + tendance phone, tile, complication |
| 2 | Share OUS | ☐ | ◐ | B.1.2 · compte Share EU/OUS · même flux | Idem — auto 2026-05-24 : hero 235 mg/dL |
| 3 | Couleurs AGP | ☐ | ◐ | B.1.8 · plages ~60 / 120 / 200 mg/dL | Visuel AGP — captures phone/watch |
| 4 | Tile + sync | ☐ | ✅ | B.1.5 · tap **↻ Sync** · `tail-sync-logs.ps1` | Refresh &lt; 30 s — **2026-05-24 adb : 430 ms** |
| 5 | Offline → reconnect | ☐ | ☐ | B.1.4 · montre mode avion 1–2 h | Pill pending puis rattrapage ≤ 5 min |
| 6 | Complication | ☐ | ☐ | B.1.1.4 + observation 30 min | Même valeur / tendance que tile |
| 7 | LOW / HI | ☐ | ☐ | Période LOW/HI connue | Libellé LOW/HI · couleur AGP |

◐ = partiel (automatisé ou Data Layer ; compléter visuel / tile tap)

---

## Vérifications sync P0 (post-audit — à noter dans colonne Notes)

| # | Cas | G6 | G7 | Comment tester | OK si |
|---|-----|----|----|----------------|-------|
| S1 | Pill « envoi en attente » | ☐ | ✅ | `hardware-smoke.ps1` | Hero à jour · pill ≠ sync active — **2026-05-24 OK** |
| S2 | Refresh tile → bon phone | ☐ | ✅ | B.1.5 · tap tile | Log `watch_refresh_request` — **2026-05-24 OK** |
| S3 | ACK montre | ☐ | ✅ | smoke + logcat | `watch_ack_received` · ack_failure=0 — **2026-05-24 OK** |
| S4 | Dexcom setup | ☐ | ◐ | Settings Dexcom | Message fetch vs montre — **à valider UI** |

---

## Install smoke (B.1.1)

| Step | Action | Auto | OK si |
|------|--------|------|-------|
| B.1.1.1 | Phone installé | `install-and-verify.ps1` | Launcher « Glucose For Watch » — **2026-05-24 OK** |
| B.1.1.2 | Watch installée | `hardware-smoke.ps1` | Package `com.widgetg7.mobile` v0.4.0 — **2026-05-24 OK (adb + Data Layer)** |
| B.1.1.3 | Tile Glycémie | manuel | Tile visible · bouton sync ≥ 48 dp |
| B.1.1.4 | Complication | manuel | Slot SHORT_TEXT rempli |

---

## Sync 30 min (B.1.3)

| Min | Observation | Noter |
|-----|-------------|-------|
| 0 | Valeur initiale V0 | |
| 5 | V1, delta temps | |
| 15 | Pas de crash, pas de `--` | |
| 30 | ≥ 4 mises à jour, dérive ≤ 10 min | |

---

## Couleurs AGP (détail cas 3)

| mg/dL | Range attendu | Token |
|-------|---------------|-------|
| ≤ 54 | VERY_LOW | `agp_glucose_very_low` |
| 70–180 | IN_RANGE | `agp_glucose_in_range` |
| 181–250 | HIGH | `agp_glucose_high` |
| &gt; 250 | VERY_HIGH | `agp_glucose_very_high` |

---

## Captures & anomalies

- Screenshots : `docs/qa/captures/` — voir [README](../qa/captures/README.md) pour le nommage
- Échec → issue GitHub avec tag `qa-hardware` + numéro de cas (#1–7 ou S1–S4)

---

## Sign-off Phase 3.3 / M5

- [ ] Au moins une colonne G6 **ou** G7 complète (7/7) sur hardware réel
- [ ] Cas S1–S4 validés sur G7 (sync P0)
- [ ] Offline 2 h (cas 5) validé au moins une fois
- [ ] `docs/plan/PROGRESS.md` mis à jour (3.3 ✅)
- [ ] 0 issue P0 ouverte liée à la matrice

---

*Procédure détaillée : [AUDIT-ACTION-PLAN.md § B.1](AUDIT-ACTION-PLAN.md#tâche-b1--qa-hardware-procédure-pas-à-pas)*
