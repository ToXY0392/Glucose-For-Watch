# Matrice QA G6 / G7 — ToXY v0.4.0

> Cocher après test **réel** sur capteur G6 ou G7 avec Dexcom Share activé.  
> Install : `.\scripts\qa\install-and-verify.ps1` ou `.\gradlew.bat installWidgetG7Debug`

| Date | Testeur | Phone | Watch | App version |
|------|---------|-------|-------|-------------|
| | | | | 0.4.0 |

---

## Prérequis communs

- Dexcom Share activé sur le compte test
- ToXY installé phone + watch (même `applicationId`)
- Tile **Glycémie** + complication ajoutées sur la montre
- Phone : batterie / sync en arrière-plan autorisés pour ToXY

---

## Matrice

| # | Cas | G6 | G7 | Procédure | OK si |
|---|-----|----|----|-----------|-------|
| 1 | Share US | ☐ | ☐ | Compte Share région US · login ToXY · sync 15 min | Valeur + tendance sur phone, tile, complication |
| 2 | Share OUS | ☐ | ☐ | Compte Share hors US (EU/OUS) · même flux | Idem |
| 3 | Couleurs AGP | ☐ | ☐ | Comparer visuellement 60 / 120 / 200 mg/dL (ou plages proches) | Vert cible · jaune haut · rouge bas — **pas** mint `#34D399` sur le chiffre |
| 4 | Tile + sync | ☐ | ☐ | Tap **↻ Sync** sur tile | Refresh &lt; 30 s · phone fetch visible |
| 5 | Offline → reconnect | ☐ | ☐ | Montre mode avion 1–2 h · phone continue fetch · réactiver radio | Badge phone puis rattrapage auto sans réinstall |
| 6 | Complication | ☐ | ☐ | Complication cadran actif vs tile | Même valeur / unité / tendance (teinte texte = limit API) |
| 7 | LOW / HI | ☐ | ☐ | Période LOW ou HI connue (ou simulation compte test) | Libellé LOW/HI · couleur AGP very_low / very_high |

---

## Tests couleur AGP (détail cas 3)

| mg/dL | Range attendu | Couleur token |
|-------|---------------|---------------|
| ≤ 54 | VERY_LOW | `agp_glucose_very_low` |
| 70–180 | IN_RANGE | `agp_glucose_in_range` |
| 181–250 | HIGH | `agp_glucose_high` |
| &gt; 250 | VERY_HIGH | `agp_glucose_very_high` |

---

## Sign-off Phase 3.3

- [ ] Toutes les lignes G6 **ou** G7 cochées pour au moins un capteur de chaque famille testé
- [ ] `docs/plan/PROGRESS.md` mis à jour (3.3 ✅)
- [ ] Anomalies ouvertes en GitHub Issue si échec

---

*Voir aussi checklist 0.7 dans [PROGRESS.md](PROGRESS.md).*
