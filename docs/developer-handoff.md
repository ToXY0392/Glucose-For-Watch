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

## Automatisation Cursor

- Les skills projet sont documentés dans `docs/cursor-skills-automation.md`.
- Emplacement skills : `.cursor/skills/`.
- Hooks d'ouverture de session : `.cursor/hooks.json` + scripts `.cursor/hooks/`.
- Les deconnexions USB detectees peuvent alimenter automatiquement la table `Incidents recents`.
- Un monitor USB periodique tourne pendant la session (verification toutes les 5 minutes).
