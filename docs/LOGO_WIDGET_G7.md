<h1 align="center">Logo Widget G7</h1>

<p align="center">
  Logo officiel · règles d'usage · interdits · placements
</p>

---

## Logo Officiel

Le logo officiel de Widget G7 est verrouillé.

```text
symbole : goutte vert dégradé
centre  : G7
texte   : Widget G7
fond    : blanc ou très clair
```

Il ne doit pas être remplacé par une variante générée, un pictogramme médical ou un anneau simple.

---

## Composition

| Élément | Règle |
| --- | --- |
| Symbole | Goutte arrondie vert dégradé |
| Centre | `G7` blanc ou très clair |
| Contour | filet clair interne autour du centre |
| Wordmark | `Widget G7` |
| Couleur texte | vert profond |
| Fond | blanc ou très clair |
| Style | simple, médical, calme |

---

## Usage Autorisé

| Surface | Usage |
| --- | --- |
| APK mobile | Logo complet en haut d'écran ou sur splash |
| Écran installation montre | Logo complet centré |
| Documentation | Logo complet en présentation |
| App Wear | Symbole seul possible si l'espace est limité |
| Tile Wear OS | Pas de logo |
| Complication | Pas de logo |
| Notification | Symbole possible uniquement en petite icône d'app |

---

## Interdits

```text
ne pas remplacer la goutte par un anneau simple
ne pas remplacer la goutte par une icône Dexcom
ne pas ajouter de croix médicale
ne pas ajouter de slogan au logo
ne pas utiliser le logo comme bouton
ne pas mettre le logo dans la tile
ne pas mettre le logo dans la complication
ne pas recolorer le texte hors vert profond
ne pas déformer ou étirer le symbole
```

---

## Placement Mobile

Le mobile est un assistant d'installation et de sync.

Placement recommandé :

```text
haut d'écran
centré
symbole au-dessus du wordmark
taille confortable mais non dominante
```

À éviter :

```text
logo répété dans chaque carte
logo dans les boutons
logo derrière les icônes
logo comme watermark de fond
```

---

## Placement Wear

La montre est la surface de lecture.

Règles :

| Surface Wear | Logo |
| --- | --- |
| App Wear ouverte | Symbole seul autorisé au démarrage ou état vide |
| Tile | Interdit |
| Complication | Interdit |
| Cadran tiers | Interdit sauf choix du cadran |

Pourquoi :

```text
sur Wear, la valeur doit rester prioritaire.
le logo ne doit pas prendre la place de la glycémie, de l'unité ou du bouton sync.
```

---

## Couleurs À Reproduire

| Usage | Couleur cible |
| --- | --- |
| Vert profond texte | `#073F35` ou proche |
| Vert sombre symbole | `#006B57` ou proche |
| Vert clair symbole | `#35D6A0` ou proche |
| Blanc interne | `#FFFFFF` |
| Fond | `#FFFFFF` / `#F7FBFA` |

Ces valeurs sont indicatives tant que le fichier source officiel du logo n'est pas extrait.

---

## Besoin Asset

À produire et stocker :

```text
docs/assets/logo-widget-g7.png
docs/assets/logo-widget-g7.svg
mobile/src/main/res/drawable/logo_widget_g7.*
mobile/src/main/res/mipmap-*/ic_launcher.*
```

Priorité :

1. extraire ou recréer proprement le logo officiel ;
2. générer une version PNG haute résolution ;
3. générer une version vectorielle si possible ;
4. remplacer les variantes anciennes dans l'app ;
5. vérifier mobile, splash, doc et icône launcher.

---

## Source De Vérité

```text
Toute nouvelle image ou interface doit reprendre ce logo officiel.
Si le logo doit être modifié, créer une nouvelle décision explicite dans cette doc.
```

Pour l'APK mobile, le logo affiche est verrouille dans :

- [DESIGN_MOBILE_VERROUILLE.md](DESIGN_MOBILE_VERROUILLE.md)
- `mobile/src/main/res/drawable-nodpi/logo_widget_g7_official.png`

Ce fichier est extrait de `docs/assets/presentation-apk-widget-g7.png`.
Il ne doit pas etre redessine.
