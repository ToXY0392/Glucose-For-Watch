# Session QA hardware — 2026-05-24

| Champ | Valeur |
|-------|--------|
| Phone | Pixel 8a (`41031JEKB03416`) |
| Watch | Google Pixel Watch 2 (`3A251RTJWWKFFD`, Data Layer `d02f1f54`) |
| App | Glucose For Watch 0.4.0 (vc 23) |
| Capteur | G7 (supposé — Dexcom Share actif) |
| Glycémie hero | 235 mg/dL (dernier smoke) |

## Vérifications automatisées

| Check | Résultat | Preuve |
|-------|----------|--------|
| B.1.1.1 Phone installé | OK | `install-and-verify.ps1` v0.4.0 |
| B.1.1.2 Watch app 0.4.0 | OK | adb + `widget_g7_watch_health.xml` |
| B.1.2 Dexcom hero | OK | `hardware-smoke.ps1` last_value |
| B.1.5 Tile sync | OK | adb `GlucoseRefreshActivity` → log < 1 s |
| S1 pill / push pending | OK | watch_push_pending=false, failures=0 |
| S2 refresh → phone | OK | `watch_refresh_request` sourceNode=d02f1f54 |
| S3 ACK montre | OK | push_seq=ack_seq, ack_failure_count=0 |

### Log tile sync (B.1.5 — 2026-05-24 12:30)

```
watch_refresh_request path=/glucose/refresh/request sourceNode=d02f1f54
sync_result fromWatch=false forcePush=true result=SuccessNoNewReading  (~430 ms)
watch_ack_received nodeId=d02f1f54 sequenceId=1779618609510
```

## Captures

| Fichier | Contenu |
|---------|---------|
| `captures/2026-05-24_B1_phone_G7_hero.png` | Phone hero |
| `captures/2026-05-24_B1_watch_G7_tile.png` | Watch app (post-wake) |

## En attente (manuel)

| Cas | Action |
|-----|--------|
| B.1.8 / #3 Couleurs AGP | Valider visuellement plages AGP sur phone + tile |
| B.1.4 / #5 Offline 2 h | Mode avion montre 1–2 h |
| #6 Complication | Comparer cadran vs tile 30 min |
| #7 LOW/HI | Si période connue |
| #1–2 US/OUS | Confirmer région Share utilisée |
| B.1.1.3–4 | Tile + complication visuelles sur cadran |

## Commandes utiles

```powershell
.\scripts\qa\install-and-verify.ps1
.\scripts\qa\hardware-smoke.ps1
.\scripts\qa\tail-sync-logs.ps1 -ClearFirst
# Tile sync sans tap manuel :
adb -s <watch> shell am start -n com.widgetg7.mobile/com.widgetg7.wear.tile.GlucoseRefreshActivity
```
