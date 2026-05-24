# Captures QA hardware

Dossier pour les screenshots de la matrice [QA-MATRIX-G6-G7.md](../../plan/QA-MATRIX-G6-G7.md).

## Nommage

```
{date}_{cas}_{capteur}_{device}_{description}.png
```

Exemples :

- `2026-05-24_04_G7_phone_tile-sync-after-tap.png`
- `2026-05-24_05_G7_phone_pill-watch-push-pending.png`
- `2026-05-24_03_G7_watch_agp-high-value.png`

| Segment | Valeurs |
|---------|---------|
| `cas` | `01`–`07` (matrice) ou `S1`–`S4` (sync P0) |
| `capteur` | `G6` ou `G7` |
| `device` | `phone` ou `watch` |

## Contenu recommandé par cas

| Cas | Phone | Watch |
|-----|-------|-------|
| 1–2 | Hero Dexcom + pill statut | Tile + complication |
| 3 | Hero couleur AGP | Tile chiffre coloré |
| 4 | Pill pendant refresh | Tile avant/après tap ↻ |
| 5 | Pill « envoi en attente » / « hors portée » | Tile stale puis rattrapage |
| 6 | — | Complication vs tile côte à côte (photo montre) |
| 7 | LOW/HI si visible | LOW/HI sur tile |

Ne pas committer de données personnelles (email Dexcom, nom complet). Recadrer si nécessaire.
