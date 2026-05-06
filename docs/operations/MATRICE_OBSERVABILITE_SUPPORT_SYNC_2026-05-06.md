# Matrice Observabilite Support - Sync

Date: 2026-05-06

## Objectif

Rendre les incidents sync reproductibles et actionnables en support/QA.

## Sources de diagnostic

| Source | Commande / fichier | Usage support |
| --- | --- | --- |
| Logs app phone | `adb -s <phone> logcat -d \| grep WG7` | Identifier etape sync (request/pass/failure/repush) |
| Logs app watch | `adb -s <watch> logcat -d \| grep WG7` | Verifier reception data layer / listener |
| Services Android | `adb -s <phone> shell dumpsys activity services` | Verifier foreground service actif |
| Device idle | `adb -s <phone> shell dumpsys deviceidle` | Contextualiser Doze / restrictions batterie |
| Batterie montre | `adb -s <watch> shell dumpsys battery` | Verifier mode degrade (<20% / charging) |

## Script standard

Script central de collecte:

```bash
bash ./scripts/dev/collect_sync_diagnostics.sh
```

Variables requises:
- `WIDGETG7_PHONE_SERIAL`
- `WIDGETG7_WATCH_SERIAL`

Sortie:
- `build/diagnostics/<timestamp>/...`

## Taxonomie incidents

| Incident | Signal attendu | Action |
| --- | --- | --- |
| Timeout sync | message timeout + category network | Verifier reseau/Dexcom + retries |
| Ack absent | repush attempts visibles | Verifier connectivite montre + listener |
| Mode degrade actif | suffixe degrade + intervalle ralenti | Verifier batterie/charging/syncLimited |
| Crash/erreur phone | trace erreur + fallback worker | Capturer dumpsys + logs WG7 |

## Checklist ticket support

- serial phone/watch
- heure incident (locale + UTC)
- etat batterie watch
- foreground sync actif oui/non
- logs WG7 phone/watch joints
- dossier `build/diagnostics/<timestamp>` joint
