# Release notes

## 2026-05-07

- Stabilisation de la sync Gradle (JBR forcé).
- Réinstallation propre mobile + wear.
- Fix incident affichage figé montre.
- Monitoring sync 30 minutes validé.
- Chaîne de build : **Android Gradle Plugin 9.2.1**, **Gradle 9.4.1** ; module **`mobile`** : assets wear embarqués (debug) via **`androidComponents`** / **`addGeneratedSourceDirectory`** pour compatibilité AGP 9 (plus de `Provider` dans `sourceSets`). Détail : `docs/android-studio.md` (section mise à jour Studio / AGP 9).
