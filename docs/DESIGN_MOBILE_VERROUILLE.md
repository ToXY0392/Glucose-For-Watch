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
- [logo-widget-g7.png](assets/logo-widget-g7.png) (copie documentaire du PNG officiel)

Le logo utilise dans l'APK est extrait directement de cette image :

- `mobile/src/main/res/drawable-nodpi/logo_widget_g7_official.png`

Regle absolue :

```text
Ne pas redessiner le logo.
Ne pas recreer le logo en vectoriel pour remplacer ce PNG a l'ecran.
Ne pas remplacer le logo par une interpretation.
Ne pas separer le symbole et le texte.
```

Un vectoriel `ic_widget_g7_drop_logo` (symbole seul) peut exister pour des usages techniques (ex. layers launcher), mais **n'est pas** le logo complet affiche sur les ecrans listes ci-dessous : ceux-ci utilisent toujours `logo_widget_g7_official.png`.

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
logo officiel aligne debut (scaleType fitStart)

pilule de statut (une ligne, texte dynamique, fond wg7_accent_soft)

carte 1 : fond bg_step_card_modern (liseret vert 4dp gauche) + icone + Installer Wear + chevron
carte 2 : meme fond + icone + Synchroniser + chevron
carte 3 : meme fond + icone + Confirmer la montre + chevron
```

Les chevrons changent de teinte (accent si etape OK, gris sinon). Pas de coches visibles.

Reference maquette : [design-proposition-alt-phone-et-tile.png](design-proposition-alt-phone-et-tile.png)

Rien d'autre ne doit etre visible sur l'ecran principal mobile.

---

## Splash (lanceur)

Fichier :

```text
mobile/src/main/res/drawable/bg_splash_screen.xml
```

Theme : `Theme.WidgetG7.Phone.Launcher` sur `SplashActivity`.

Composition : fond plein `wg7_bg_top` + **meme logo officiel** centre, **170dp x 118dp** (layer-list).

---

## Autres ecrans (logo en entete)

Meme ressource `@drawable/logo_widget_g7_official`, taille **140dp x 98dp**, **14dp** sous le logo avant le titre de page :

| Layout | Ecran |
| --- | --- |
| `activity_watch_setup.xml` | Montre |
| `activity_notice.xml` | Notice |
| `activity_dexcom_entry.xml` | Connexion Dexcom |
| `activity_dexcom_settings.xml` | Dexcom |
| `activity_legal_document.xml` | Document legal |

---

## Dimensions Cibles

| Element | Valeur verrouillee |
| --- | --- |
| Padding horizontal | `28dp` |
| Padding haut | `56dp` |
| Logo | `170dp x 118dp` |
| Espacement logo / pilule | `16dp` |
| Pilule | padding `12dp` vertical, `16dp` horizontal, coins pilule |
| Espacement pilule / cartes | `22dp` |
| Carte | min `104dp` haut, coins `20dp`, `bg_step_card_modern`, elevation `2dp` |
| Espacement entre cartes | `16dp` |
| Padding interne carte | `20dp` start, `16dp` top/bottom/end |
| Icones cartes | `44dp x 44dp` |
| Chevron | `28dp` |
| Texte cartes | `20sp`, gras |

Ces valeurs peuvent seulement etre ajustees pour corriger un bug d'affichage sur un appareil precis.

---

## Palette Mobile

| Usage | Couleur |
| --- | --- |
| Fond haut | `#FFFFFF` |
| Fond bas | `#F7FBFA` |
| Carte surface | `#FCFEFE` (`wg7_surface`) |
| Bord carte | `wg7_outline` |
| Liseret gauche carte | `wg7_accent` (`#198C6C`) |
| Pilule statut | `wg7_accent_soft` |
| Texte pilule | `wg7_accent_dark` |
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
pas de texte long sous le logo en dehors de la pilule resume
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
| Etats install/sync/ack | Alimentent la pilule et la teinte des chevrons |

---

## Validation Avant Release

Avant de livrer une APK mobile :

1. Ouvrir l'accueil sur un Pixel.
2. Verifier que le logo vient bien de `logo_widget_g7_official.png`.
3. Lancer l'app depuis le lanceur : le splash affiche le meme logo centre.
4. Verifier les ecrans secondaires (Notice, Dexcom, Montre, legal) : logo entete present.
5. Verifier qu'aucun rectangle gris n'apparait derriere le logo.
6. Verifier que les libelles des cartes sont fixes ; seul le texte de la pilule est dynamique.
7. Verifier les trois cartes : `Installer Wear`, `Synchroniser`, `Confirmer la montre`.
8. Verifier la pilule de statut (message court, une ligne).
9. Verifier qu'aucune valeur glycemie n'est visible.
10. Verifier que la composition reste limitee au logo, a la pilule et aux trois cartes.

Si un point echoue, le design mobile n'est pas valide.
