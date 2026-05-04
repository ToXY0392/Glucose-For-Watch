<h1 align="center">Design Cible App Et Wear</h1>

<p align="center">
  Reproduire le design de la présentation dans l'APK mobile, l'app Wear, la tile et la complication
</p>

---

## Décision

```text
Le visuel de présentation devient la référence UI.
Le mobile n'est pas un dashboard glycémie.
Le mobile est un assistant : installer Wear, synchroniser, vérifier l'ack.
La montre est la surface de lecture : app Wear, tile, complication.
```

Référence :

- [presentation-apk-widget-g7.png](assets/presentation-apk-widget-g7.png)
- [LOGO_WIDGET_G7.md](LOGO_WIDGET_G7.md)

---

## Logo Verrouillé

Le logo Widget G7 est verrouillé. La source de vérité détaillée est :

- [LOGO_WIDGET_G7.md](LOGO_WIDGET_G7.md)

Référence visuelle :

```text
goutte vert dégradé
contour blanc
texte G7 au centre
wordmark "Widget G7"
texte vert profond
fond blanc
```

Règles :

| Élément | Décision |
| --- | --- |
| Symbole | Goutte arrondie vert dégradé avec `G7` au centre |
| Texte | `Widget G7` |
| Couleur texte | vert profond |
| Fond | blanc ou très clair |
| Usage mobile | logo centré ou haut d'écran |
| Usage Wear | symbole autorisé dans l'app Wear seulement |
| Usage tile | interdit |
| Usage complication | interdit |

À ne pas faire :

```text
ne pas redessiner le logo en icône médicale
ne pas remplacer la goutte officielle par un anneau simple
ne pas changer le nom
ne pas ajouter de slogan dans le logo
ne pas utiliser le logo comme bouton
ne pas afficher le logo dans la tile
ne pas afficher le logo dans la complication
```

Les futurs visuels doivent reprendre ce logo officiel, pas en générer une variante.

---

## Rôles D'Écran

| Surface | Rôle | À éviter |
| --- | --- | --- |
| APK mobile | Installer, configurer, synchroniser, vérifier | Graph glycémie, historique, dashboard médical |
| App Wear | Lire vite la dernière valeur | Menus longs |
| Tile | Valeur immédiate + fraîcheur + refresh | Texte bavard |
| Complication | Valeur compacte intégrée au cadran | Décor ou détail inutile |

Le mobile peut afficher des états de sync, mais pas devenir une copie de l'app officielle Dexcom.

---

## Direction Visuelle

```text
fond blanc clinique
cartes blanches arrondies
vert profond pour les actions validées
icônes simples
beaucoup d'espace
montre sombre, valeur très lisible
```

Palette cible :

| Token | Couleur | Usage |
| --- | --- | --- |
| `wg7_bg_top` | `#FFFFFF` | fond haut mobile |
| `wg7_bg_bottom` | `#F7FBFA` | fond bas mobile |
| `wg7_surface` | `#FCFEFE` | cartes |
| `wg7_surface_alt` | `#F2F8F6` | zones secondaires |
| `wg7_outline` | `#D8E5E1` | contours doux |
| `wg7_text_primary` | `#10231E` | texte principal |
| `wg7_text_secondary` | `#6A7875` | texte secondaire |
| `wg7_accent` | `#198C6C` | actions |
| `wg7_accent_dark` | `#0B4A3D` | titres / icônes fortes |
| `wg7_accent_soft` | `#DDF3EC` | pastilles |
| `wg7_watch_bg` | `#062A24` | fond Wear |
| `wg7_watch_ring` | `#61E692` | anneau Wear |
| `wg7_watch_text` | `#FFFFFF` | valeur Wear |

---

## APK Mobile

### Écran Montre / Installation

Le mobile doit ressembler à l'écran du visuel :

```text
logo Widget G7
carte Installer Wear
carte Sync
carte Ack montre
```

Pas de courbe glycémie.

Structure :

| Bloc | Contenu |
| --- | --- |
| Header | `Widget G7` ou `Montre` |
| Statut global | Montre détectée / app Wear absente / app Wear prête |
| Carte 1 | `Installer Wear` + état |
| Carte 2 | `Sync` + état |
| Carte 3 | `Ack montre` + état |
| Action principale | `Installer sur la montre` ou `Tester l'envoi` |
| Action secondaire | `Utiliser notification` uniquement si besoin |

États visuels :

| État | Icône | Couleur |
| --- | --- | --- |
| OK | check simple | vert |
| En cours | sync ligne | vert profond |
| À faire | download / watch ligne | vert profond |
| Erreur | warning | orange/rouge discret |

### Règles Mobile

```text
pas de graph glycémie
pas de gros affichage historique
pas de promesse médicale
pas de texte long dans les cartes
pas de bulle vert clair derrière les icônes
pas de halo décoratif autour des pictogrammes
```

Le mobile peut afficher une valeur seulement dans un contexte de test :

```text
Dernier test envoyé : 145 mg/dL
```

Mais la valeur ne doit pas être le centre de l'écran mobile.

---

## App Wear

L'app Wear doit être très proche de la montre du visuel :

```text
fond vert très sombre
anneau de progression ou anneau décoratif utile
valeur au centre
unité sous la valeur
tendance compacte
fraîcheur discrète
```

Layout cible :

| Zone | Contenu |
| --- | --- |
| Centre | `145` |
| Sous-centre | `mg/dL` |
| Côté / bas | tendance courte |
| Bas | fraîcheur ou sync |
| Interaction | bouton icône sync = refresh manuel |

Couleurs :

| Élément | Couleur |
| --- | --- |
| Fond | `#062A24` |
| Anneau | `#61E692` |
| Valeur | `#FFFFFF` |
| Unité | `#DDF3EC` |
| Métadonnée | `#8EE8B5` |
| Donnée ancienne | `#F6B86A` |
| Erreur | `#F87171` |

---

## Tile Wear OS

La tile doit être la surface prioritaire.

Objectif :

```text
Lire la valeur en moins d'une seconde.
```

Design cible :

```text
fond vert sombre
valeur très grande
unité juste dessous
anneau ou arc vert autour si possible
status court en bas
bouton icône sync discret mais visible
aucun logo
```

Contenu :

| Priorité | Élément |
| --- | --- |
| 1 | Valeur |
| 2 | Unité |
| 3 | Tendance |
| 4 | Fraîcheur |
| 5 | Bouton icône sync manuel |

Bouton sync :

| Élément | Règle |
| --- | --- |
| Forme | rond ou pastille ronde |
| Icône | flèche circulaire / refresh |
| Position | bas de tile, sous la valeur |
| Taille | visuel compact 28-32dp, zone tactile 40dp si possible |
| Couleur | vert doux sur fond sombre |
| Action | déclenche `/glucose/refresh/request` |
| Texte | aucun texte si l'icône est claire |
| Hiérarchie | toujours secondaire face à la valeur |

À modifier dans :

- `wear/src/main/java/com/widgetg7/wear/tile/GlucoseTileService.kt`

Règles :

```text
remplacer le style clair actuel par un style sombre Wear
supprimer les textes longs
garder une seule action : bouton icône sync
augmenter la valeur autour de 48sp si l'espace le permet
le bouton sync ne doit jamais rivaliser avec la valeur
ne pas afficher le logo Widget G7 dans la tile
```

---

## Complication

La complication doit rester minimaliste.

Design cible :

| Type | Affichage |
| --- | --- |
| `SHORT_TEXT` | `145` + `mg/dL` ou tendance |
| `LONG_TEXT` | `145 mg/dL` + fraîcheur courte |
| `RANGED_VALUE` | anneau + `145` |

À modifier dans :

- `wear/src/main/java/com/widgetg7/wear/complication/GlucoseComplicationService.kt`

Règles :

```text
pas de phrase
pas de texte médical long
pas de logo Widget G7
valeur toujours prioritaire
unité visible si possible
donnée ancienne explicite mais courte
```

---

## Composants À Créer Ou Harmoniser

### Mobile

| Composant | Usage |
| --- | --- |
| `SetupStatusCard` | Installer Wear / Sync / Ack montre |
| `StatusIcon` | icône ligne sobre, sans bulle |
| `CheckBadge` | petit check vert à droite |
| `WatchInstallStepCard` | étape d'installation distante |

### Wear

| Composant | Usage |
| --- | --- |
| `WearGlucoseValueBlock` | valeur + unité |
| `WearStatusLine` | fraîcheur / tendance |
| `WearRefreshAction` | tap refresh |
| `WearSyncIconButton` | bouton manuel sync sur la tile |
| `WearRingFrame` | anneau autour de la valeur |

---

## Critères De Validation Visuelle

| Surface | Critère |
| --- | --- |
| Mobile | On comprend que l'app installe et vérifie la montre |
| Mobile | Aucun graph glycémie visible |
| Mobile | Aucune bulle vert clair derrière les icônes |
| Wear app | La valeur est le centre absolu |
| Tile | Lisible en un coup d'œil |
| Tile | Bouton sync manuel visible et touchable |
| Complication | Lisible sur cadran sombre ou clair |
| Ensemble | Même vert, mêmes arrondis, même calme visuel que la présentation |

---

## Ordre D'Implémentation Design

1. Refaire l'écran `WatchSetupActivity` en assistant à trois cartes.
2. Ajouter l'écran `WearInstallerActivity` avec le même style.
3. Refaire la tile en thème sombre vert.
4. Ajuster les complications pour être plus compactes.
5. Ajouter un écran Wear app si nécessaire autour du même bloc valeur.
6. Tester sur téléphone et montre réelle.

---

## Référence Produit

Phrase à garder :

```text
Le téléphone prépare et vérifie.
La montre affiche.
Dexcom reste l'app officielle de référence.
```
