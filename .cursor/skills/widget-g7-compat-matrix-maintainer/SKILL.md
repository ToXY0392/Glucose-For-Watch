---
name: widget-g7-compat-matrix-maintainer
description: Maintient la matrice de compatibilite Android Studio, JDK, AGP, Gradle, Kotlin et Wear OS pour Widget G7, puis signale les combinaisons a risque et met a jour docs/dev/setup.md.
disable-model-invocation: true
---

# Widget G7 Compat Matrix Maintainer

## Objectif
Garder une matrice de compatibilite fiable pour le build et le developpement.

## Entrees
- `docs/dev/setup.md`
- `README.md`
- `build.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`

## Workflow
1. Lire les versions effectives du repo.
2. Verifier la compatibilite outillage officielle.
3. Mettre a jour les combinaisons avec statut :
   - `Valide`
   - `Risque`
   - `Non supporte`
4. Ajouter une recommandation claire pour chaque statut `Risque`.
5. Mettre a jour `docs/dev/setup.md` si edition autorisee.

## Sortie attendue
- **Matrice a jour**
- **Combinaisons a risque** avec mitigation
- **Patch applique** : oui/non
