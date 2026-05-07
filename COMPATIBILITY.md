# Compatibilité

## Environnement recommandé

- Android Studio : récent
- Gradle wrapper : `8.13`
- JDK Gradle : JBR Android Studio (`jbr-21`)
- Android : mobile + Wear OS

## Vérification rapide

```powershell
.\gradlew.bat help
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```

Si les commandes finissent en `BUILD SUCCESSFUL`, l'environnement est compatible.
