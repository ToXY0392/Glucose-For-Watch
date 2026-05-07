# Reprise développement

## État rapide

- Sync principale : `Dexcom Share -> téléphone -> Wear OS`
- Modules actifs : `mobile`, `wear`, `core`, `feature`
- Build debug : validé

## Commandes utiles

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat installWidgetG7Debug
```

## Incidents récents

| Date | Sujet | Statut |
| --- | --- | --- |
| 2026-05-07 | Sync Gradle IDE instable | Résolu |
| 2026-05-07 | Affichage montre figé à 382 | Résolu (cache UI refresh) |
| 2026-05-07 | Monitoring sync 30 min | Validé stable |

## Reprise session suivante

1. Sync Gradle.
2. Build debug mobile + wear.
3. Vérifier tuile + complication.
4. Vérifier sync après veille.
