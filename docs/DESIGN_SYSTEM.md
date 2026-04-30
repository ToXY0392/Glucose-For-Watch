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

| Écran | Objectif | Contenu |
| --- | --- | --- |
| Accueil | Voir l'état montre | Montre, statut, `Synchroniser` |
| Connexion Dexcom | Configurer Dexcom Share | Identifiants, région, juridique |
| Montre | Vérifier la liaison | Détection, montre principale, test, batterie |
| Wear | Lire vite | Valeur, tendance, fraîcheur, refresh |
| Notice | Rassurer et guider | Texte court, avertissement clair |

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

- [design-widget-g7-cockpit.png](design-widget-g7-cockpit.png)
- [reference_design_configurer_montre.png](reference_design_configurer_montre.png)
