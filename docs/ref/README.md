# Références fournisseurs

Documentation **externe** des éditeurs et plateformes pour les briques du projet.

## Inventaire

| Fichier | Contenu |
| --- | --- |
| [dependency-catalog.yaml](dependency-catalog.yaml) | Source structurée : toolchain Gradle, plugins, coordonnées Maven, URLs officielles, modules concernés. |
| [dependency-registry.md](dependency-registry.md) | Table alignée sur les `build.gradle.kts` pour lecture rapide hors YAML. |

À chaque mise à jour de dépendances : modifier **ensemble** `dependency-catalog.yaml` et `dependency-registry.md`, puis les fiches thématiques si le contexte change.

## Guides thématiques

Identifiants utiles pour la recherche dans le dépôt.

| Id | Fichier | Sujet |
| --- | --- | --- |
| `dexcom-share` | [dexcom-share.md](dexcom-share.md) | Share HTTP, API Dexcom Developer, liens produit. |
| `google-wear-os` | [google-wear-os.md](google-wear-os.md) | Wear OS, Data Layer, Tiles, complications, Play services. |
| `google-android-jetpack` | [google-android-jetpack.md](google-android-jetpack.md) | Android / Jetpack côté module mobile. |
| `kotlin-gradle-build` | [kotlin-gradle-build.md](kotlin-gradle-build.md) | Kotlin, Gradle, AGP, R8, dépendances build spécifiques. |

## Documentation interne (comportement produit)

| Sujet | Fichier |
| --- | --- |
| Index doc | [../index.md](../index.md) |
| Sync Wear, Data Layer, assistant install | [../technical-wear-os-sync.md](../technical-wear-os-sync.md) |
