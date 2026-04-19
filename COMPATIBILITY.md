# Android Compatibility

Cette note resume les versions Android compatibles avec le projet `Widget G7`.

## Resume

- `mobile` : Android `9` et plus
- `wear` : Android `11` et plus

## Detail par module

### Mobile

- module : `mobile`
- `minSdk = 28`
- `targetSdk = 35`
- correspondance `minSdk 28` : Android `9` (Pie)

Conclusion :

- compatible a partir de `Android 9`
- non compatible avec `Android 8.1` et versions anterieures

Reference :

- [mobile/build.gradle.kts](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/mobile/build.gradle.kts:1>)

### Wear

- module : `wear`
- `minSdk = 30`
- `targetSdk = 35`
- correspondance `minSdk 30` : Android `11`

Conclusion :

- compatible a partir de `Android 11`
- pour les montres, cela vise un environnement Wear OS moderne base sur Android `11+`

Reference :

- [wear/build.gradle.kts](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/wear/build.gradle.kts:1>)

## Tableau rapide

| Module | minSdk | Version minimale | targetSdk |
|---|---:|---|---:|
| `mobile` | 28 | Android 9 | 35 |
| `wear` | 30 | Android 11 | 35 |

## Montres compatibles

Cette liste couvre les montres Wear OS modernes les plus directement compatibles avec le projet.
Elle est volontairement pratique plutot qu'exhaustive : elle recense les familles/modeles confirmes comme Wear OS, donc adaptes au module `wear`.

### Google Pixel Watch

- Pixel Watch
- Pixel Watch 2
- Pixel Watch 3
- Pixel Watch 4

Reference :

- https://support.google.com/googlepixelwatch/answer/12652073?hl=en

### Samsung Galaxy Watch (Wear OS)

- Galaxy Watch4
- Galaxy Watch4 Classic
- Galaxy Watch5
- Galaxy Watch5 Pro
- Galaxy Watch6
- Galaxy Watch6 Classic
- Galaxy Watch7
- Galaxy Watch Ultra

Reference :

- https://www.samsung.com/us/support/answer/ANS10003348/

### OnePlus Watch

- OnePlus Watch 2
- OnePlus Watch 2R

References :

- https://www.oneplus.com/es/press/press-release/introducing-oneplus-watch-2
- https://www.oneplus.com/us/oneplus-watch-2r/specs

### OPPO Watch

- OPPO Watch X2

Reference :

- https://www.oppo.com/en/newsroom/press/oppo-unveils-oppo-watch-x-2/

### Mobvoi TicWatch

- TicWatch Pro 5
- TicWatch Pro 5 Enduro
- TicWatch Atlas

References :

- https://www.mobvoi.com/us/pages/ticwatchpro5
- https://www.mobvoi.com/pages/ticwatchpro5enduro
- https://www.mobvoi.com/in/pages/ticwatchatlas

## Regle pratique

Si une montre :

- tourne sous Wear OS moderne
- est au moins au niveau Android 11 / Wear OS 3+
- supporte les complications Wear OS et l'installation d'apps Wear

alors elle est un bon candidat pour le module `wear`.

## Notes

- La compatibilite reelle d'une montre depend aussi de la version de Wear OS et du cadran utilise pour afficher la complication.
- Le projet est actuellement calibre pour la Pixel Watch 2, mais la borne minimale cote code reste celle definie par `minSdk`.
- La liste des modeles ci-dessus est une liste de compatibilite pratique et confirmee a partir de sources constructeurs, pas un catalogue complet de toutes les montres Wear OS du marche.
