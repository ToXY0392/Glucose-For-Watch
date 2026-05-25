---
name: widget-g7-dependency-advisor
description: Analyse les versions AGP, Gradle, Kotlin et dependances du repo Widget G7, puis propose une strategie d'upgrade sure avec plan par petits PRs et rollback.
disable-model-invocation: true
---

# Widget G7 Dependency Advisor

## Objectif
Planifier des montées de version stables et auditables.

## Entrees a analyser
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`
- modules `**/build.gradle.kts`

## Workflow
1. Construire l'etat actuel des versions critiques (AGP/Gradle/Kotlin/libs).
2. Evaluer la compatibilite outillage (Android Studio, JDK, AGP, Gradle, Kotlin).
3. Proposer un ordre d'upgrade faible risque :
   1) Kotlin
   2) AGP
   3) Gradle wrapper
   4) bibliotheques applicatives
4. Definir un plan de rollback par etape.
5. Generer un plan de petits PRs (un theme par PR).

## Sortie attendue
- **Etat actuel**
- **Cible recommandee**
- **Plan de migration par PR**
- **Rollback**
- **Tests de validation par PR**

## Regles
- Donner la raison de chaque upgrade.
- Eviter les sauts de version massifs.
- Prioriser la compatibilite du build avant les optimisations.
