<h1 align="center">Design Tile Wear Verrouille</h1>

<p align="center">
  Source de verite du design de la tile Wear OS Widget G7
</p>

---

## Decision

Le design de la tile Wear OS est verrouille sur la version simple.

```text
valeur centrale
unite + tendance
bouton refresh discret
fond sombre
aucun anneau
aucune bulle sous le bouton sync
aucun logo
```

Ce choix remplace les variantes avec anneau, point blanc, cadran PNG ou cercle de progression.

---

## Source Code

Fichier source :

```text
wear/src/main/java/com/widgetg7/wear/tile/GlucoseTileService.kt
```

Version de ressources verrouillee au moment de cette decision :

```text
RESOURCES_VERSION = "21-primary-layout-safe-zones"
```

Structure : [PrimaryLayout](https://developer.android.com/reference/kotlin/androidx/wear/protolayout/material/layouts/PrimaryLayout) avec `deviceConfiguration` de la requête, `setResponsiveContentInsetEnabled(true)` (marges / cadrage écran rond). Contenu : colonne valeur + `mg/dL` + tendance. Bas : pseudo chip (Boîte + `↻`) avec `launchAction` → `GlucoseRefreshActivity`. Fond plein : `Box` racine avec la couleur tile.

Si `deviceConfiguration` est absent : tile de repli (colonne + spacers `expand` comme avant).

Référence PNG historique (non utilisée dans la timeline tile actuelle) :

```text
wear/src/main/res/drawable-nodpi/ic_tile_refresh_reference.png
```

---

## Structure Visible

```text
fond charbon #0A1A16

150

mg/dL →

icône refresh
```

Le bouton refresh reste cliquable, mais sa zone tactile doit etre invisible.

---

## Regles Visuelles

| Element | Regle |
| --- | --- |
| Fond | plein sombre, pas de carte |
| Valeur | grande, centree |
| Unite | `mg/dL` sous la valeur |
| Tendance | fleche courte apres l'unite |
| Refresh | icone seule sous l'unite |
| Zone tactile | invisible |
| Logo | interdit |
| Anneau | interdit |
| Bulle refresh | interdite |
| Texte long | interdit |

---

## Couleurs

| Usage | Couleur |
| --- | --- |
| Fond | `#0A1A16` (charbon vert, fond plat) |
| Valeur | `#F7FBFA` |
| Unite / tendance | `#35E995` |
| Refresh | `#35E995` |

Ces couleurs doivent rester coherentes avec l'APK mobile Widget G7.

---

## Interdits

```text
ne pas remettre l'anneau vert
ne pas remettre le point blanc
ne pas remettre de cadran PNG
ne pas ajouter de bulle verte sous le bouton sync
ne pas afficher le mot SYNC
ne pas afficher de logo Widget G7
ne pas afficher de status long
ne pas transformer la tile en dashboard
```

---

## Bug A Surveiller

Un carre noir peut apparaitre pendant l'ouverture/slide de la tile.

Correctif code (a valider sur montre) : clic refresh via `launchAction` + glyphe texte, sans `LoadAction` ni bitmap dans le ProtoLayout.

Hypothese complementaire :

```text
le bug vient de la transition/animation Wear OS ou du rendu de surface tile,
pas seulement du contenu visible de la tile.
```

Le design simple est conserve pour reduire les surfaces de rendu complexes.

Avant toute nouvelle correction :

1. filmer l'ouverture de la tile ;
2. verifier si le carre noir est toujours visible ;
3. tester separement animation, clickable et fond root ;
4. ne pas modifier le design visuel verrouille sans nouvelle decision.

---

## Presentation

La presentation doit afficher ce design simple pour la montre et la tile :

- [presentation-apk-widget-g7.png](assets/presentation-apk-widget-g7.png)

La presentation ne doit plus montrer d'anneau vert autour de la valeur si la tile reelle reste en version simple.
