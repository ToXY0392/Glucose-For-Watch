<h1 align="center">Design Mobile Verrouille</h1>

<p align="center">
  Source de verite de l'ecran principal APK mobile Widget G7
</p>

---

## Decision

Le design de l'APK mobile est verrouille.

Il ne faut plus proposer, generer ou coder une variante de l'ecran principal sans nouvelle decision explicite.

```text
Le mobile prepare.
Le mobile installe.
Le mobile synchronise.
Le mobile verifie l'ack montre.
Le mobile n'affiche pas un dashboard glycemie.
```

---

## Source De Verite

La reference visuelle reste :

- [presentation-apk-widget-g7.png](assets/presentation-apk-widget-g7.png)

Le logo utilise dans l'APK est extrait directement de cette image :

- `mobile/src/main/res/drawable-nodpi/logo_widget_g7_official.png`

Regle absolue :

```text
Ne pas redessiner le logo.
Ne pas recreer le logo en vectoriel.
Ne pas remplacer le logo par une interpretation.
Ne pas separer le symbole et le texte.
```

Le logo mobile doit toujours etre affiche comme une seule image complete :

```text
symbole G7 + texte Widget G7
fond transparent
aucun rectangle gris visible
```

---

## Ecran Principal Verrouille

Fichier :

```text
mobile/src/main/res/layout/activity_main.xml
```

Structure visible :

```text
logo officiel centre

carte 1 : icone montre + Installer Wear + check
carte 2 : icone sync + Sync + check
carte 3 : icone montre/ack + Ack montre + check
```

Rien d'autre ne doit etre visible sur l'ecran principal mobile.

---

## Dimensions Cibles

| Element | Valeur verrouillee |
| --- | --- |
| Padding horizontal | `32dp` |
| Padding haut | `70dp` |
| Logo | `170dp x 118dp` |
| Espacement logo/cartes | `22dp` |
| Hauteur carte | `104dp` |
| Espacement entre cartes | `18dp` |
| Padding carte gauche | `24dp` |
| Padding carte droite | `22dp` |
| Icones cartes | `44dp x 44dp` |
| Checks | `34dp x 34dp` |
| Texte cartes | `22sp`, gras |

Ces valeurs peuvent seulement etre ajustees pour corriger un bug d'affichage sur un appareil precis.

---

## Palette Mobile

| Usage | Couleur |
| --- | --- |
| Fond haut | `#FFFFFF` |
| Fond bas | `#F7FBFA` |
| Carte | `#FFFFFF` |
| Bord carte | `#E4EDEA` |
| Texte | `#1E2A28` |
| Vert profond | `#0B4A3D` |
| Vert action/check | `#198C6C` |

---

## Interdits

```text
pas de valeur glycemie au centre de l'ecran mobile
pas de graph
pas d'historique
pas de dashboard
pas de montre hero
pas de texte d'explication visible sous le logo
pas de bouton visible en bas
pas de Notice visible sur l'accueil
pas de titre dynamique type Wear pret / Montre detectee
pas de logo redessine
pas de fond gris derriere le logo
pas de bulle verte derriere les icones
pas de halo decoratif
```

Les etats techniques peuvent exister dans le code, mais ils ne doivent pas modifier la composition visuelle verrouillee de l'accueil.

---

## Interactions Cachees

Pour conserver les fonctionnalites existantes sans casser le design :

| Element technique | Regle |
| --- | --- |
| Menu parametres | Peut rester cache en zone tactile invisible |
| Sync manuelle | Peut rester branchee en interne |
| Notice | Peut rester accessible ailleurs, pas visible sur l'accueil |
| Etats install/sync/ack | Peuvent alimenter la logique, pas remplacer les libelles verrouilles |

---

## Validation Avant Release

Avant de livrer une APK mobile :

1. Ouvrir l'accueil sur un Pixel.
2. Verifier que le logo vient bien de `logo_widget_g7_official.png`.
3. Verifier qu'aucun rectangle gris n'apparait derriere le logo.
4. Verifier que le titre visible est uniquement dans l'image logo.
5. Verifier les trois cartes : `Installer Wear`, `Sync`, `Ack montre`.
6. Verifier qu'aucune valeur glycemie n'est visible.
7. Verifier qu'aucun texte dynamique ne remplace la maquette.

Si un point echoue, le design mobile n'est pas valide.
