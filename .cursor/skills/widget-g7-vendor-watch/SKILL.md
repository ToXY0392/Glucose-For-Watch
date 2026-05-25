---
name: widget-g7-vendor-watch
description: Scrute les mises à jour Android, Wear OS, AGP, Gradle, Kotlin et Dexcom, puis classe l'impact réel pour Widget G7 en actions immédiates, planifiées ou ignorées.
disable-model-invocation: true
---

# Widget G7 Vendor Watch

## Objectif
Faire une veille fournisseur actionnable pour le repo Widget G7.

## Sources à surveiller
- Android Developers (Android + Jetpack)
- Wear OS documentation
- Android Gradle Plugin release notes
- Gradle release notes
- Kotlin release notes
- Dexcom Share documentation

## Entrées repo à lire
- `README.md`
- `docs/dev.md`
- `build.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`

## Workflow
1. Relever les versions et hypothèses actuelles du repo.
2. Collecter les nouveautés fournisseur depuis le dernier run (mode delta).
3. Identifier uniquement les impacts réels sur Widget G7 :
   - breaking changes
   - dépréciations
   - min SDK / compatibilité outillage
   - changements de comportement API
4. Produire un bulletin trié en :
   - `A faire maintenant`
   - `A planifier`
   - `A ignorer`

## Format de sortie
- **Resume executif** (3-5 lignes)
- **A faire maintenant** (impact + action + urgence)
- **A planifier** (fenetre recommandee)
- **A ignorer** (raison explicite)
- **Sources consultees** (liens)

## Regles
- Ne pas lister des updates sans impact concret sur le repo.
- Citer une source pour chaque point important.
- Privilegier des actions minimales et reversibles.
