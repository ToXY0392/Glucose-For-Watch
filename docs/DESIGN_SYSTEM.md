<h1 align="center">🎨 Design System Widget G7</h1>

<p align="center">
  Clair · médical · calme · lisible vite
</p>

---

## 🟢 Direction

```text
╭─ Intention UI ─────────────────────────╮
│  médical sans froideur                 │
│  simple sans pauvreté                  │
│  fiable sans bruit                     │
│  centré sur la montre et la valeur     │
╰────────────────────────────────────────╯
```

Widget G7 ne doit pas ressembler à une landing page. L'interface sert à vérifier la sync et lire l'état courant.

Le logo est verrouillé : goutte vert dégradé avec `G7` au centre + texte `Widget G7` vert profond. Ne pas le réinterpréter dans les écrans ou visuels.

---

## 🎨 Palette

| Usage | Couleur |
| --- | --- |
| Fond | Blanc clinique |
| Secondaire | Gris très clair |
| Sync OK | Vert |
| Attention | Orange |
| Erreur | Rouge |
| Indisponible | Gris |

> Le vert reste un accent. Il ne doit pas envahir tout l'écran.

---

## 🔤 Typographie

| Élément | Règle |
| --- | --- |
| Titres | Courts |
| Texte | Minimal |
| Glycémie | Très lisible |
| Statuts | Courts et explicites |
| Wear | Encore plus dense |

---

## 🧩 Composants

| Composant | Usage |
| --- | --- |
| Card statut | État principal |
| Card Dexcom | Connexion et région |
| Card montre | Choix et test |
| Bouton plein | Action principale |
| Bouton contour | Action secondaire |
| Icône | Navigation ou outil simple |

Éviter les cards imbriquées.

---

## 🟢 Statuts

| Statut | Sens |
| --- | --- |
| `Sync active` | Le service tourne |
| `Montre vérifiée` | Ack reçu |
| `Aucune nouvelle mesure` | Dexcom n'a pas publié plus récent |
| `Vérifier Dexcom` | Auth ou réseau à contrôler |
| `Donnée ancienne` | Fraîcheur insuffisante |

---

## 📱 Écrans

Regles detaillees : [DESIGN_MOBILE_VERROUILLE.md](DESIGN_MOBILE_VERROUILLE.md) (mobile), [DESIGN_TILE_WEAR_VERROUILLE.md](DESIGN_TILE_WEAR_VERROUILLE.md) (tile).

| Écran | Objectif | Contenu |
| --- | --- | --- |
| Splash | Lancer l'app | Fond clair + logo officiel centre |
| Accueil | Voir l'état montre | Logo + trois cartes (Installer / Sync / Ack) |
| Connexion Dexcom | Configurer Dexcom Share | Logo entete, identifiants, région, juridique |
| Montre | Vérifier la liaison | Logo entete, détection, test, batterie |
| Wear | Lire vite | Valeur, tendance, fraîcheur, refresh |
| Notice | Rassurer et guider | Logo entete, texte court, avertissement clair |
| Juridique | Lire un document | Logo entete + contenu scrollable |

---

## 🧪 Mode direct capteur

Si ce mode existe un jour :

```text
Direct capteur = experimental
risques visibles
âge de donnée visible
retour simple vers Sync téléphone
```

---

## 🚫 À éviter

- écrans trop bavards ;
- décoration gratuite ;
- gradients dominants ;
- texte qui répète les docs ;
- âge de donnée caché ;
- mode direct présenté comme officiel.

---

## 🖼️ Références

- [LOGO_WIDGET_G7.md](LOGO_WIDGET_G7.md)
- [DESIGN_MOBILE_VERROUILLE.md](DESIGN_MOBILE_VERROUILLE.md)
- [DESIGN_TILE_WEAR_VERROUILLE.md](DESIGN_TILE_WEAR_VERROUILLE.md)
- [DESIGN_CIBLE_APP_ET_WEAR.md](DESIGN_CIBLE_APP_ET_WEAR.md)
- [presentation-apk-widget-g7.png](assets/presentation-apk-widget-g7.png)
- [logo-widget-g7.png](assets/logo-widget-g7.png)
- [design-widget-g7-cockpit.png](design-widget-g7-cockpit.png)
