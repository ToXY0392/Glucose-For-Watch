<h1 align="center">🧬 Plan Wear Collector Avancé</h1>

<p align="center">
  Un plan pour plus tard, seulement si le spike BLE passe
</p>

---

## 🧭 Position

```text
╭─ Hors parcours principal ──────────────╮
│ Mode standard : téléphone -> Wear OS   │
│ Mode direct   : expérimental           │
│ Condition     : spike BLE concluant    │
╰────────────────────────────────────────╯
```

Ce plan ne concerne pas l'app principale actuelle.

---

## 🎯 Objectif

Construire un mode expérimental où la montre Wear OS collecte directement le capteur Dexcom G7 en Bluetooth.

Le mode doit rester :

- réversible ;
- séparé du mode standard ;
- désactivable ;
- strict sur les données sensibles.

---

## 🧱 Principes

| Principe | Règle |
| --- | --- |
| Priorité | Le mode téléphone reste prioritaire |
| Statut | Le direct est expérimental |
| Architecture | La tile et la complication lisent un repository, pas le BLE |
| Fraîcheur | Toute donnée a timestamp + état |
| Sécurité | Secrets et codes capteur jamais logués |
| Retour | L'utilisateur peut revenir au téléphone |

---

## 🏗️ Architecture cible

| Composant | Rôle |
| --- | --- |
| `SourceRouter` | Sélectionne `phoneRelay` ou `directSensor` |
| `G7BleScanner` | Scanne les capteurs proches |
| `G7PairingManager` | Gère l'association |
| `G7CollectorService` | Collecte avec garde-fous batterie |
| `G7PacketParser` | Transforme les paquets en lectures |
| `DirectReadingRepository` | Stocke la dernière lecture locale |
| `DirectModeHealthCheck` | Expose l'état du direct |

---

## 🔐 Permissions probables

```text
BLUETOOTH_SCAN
BLUETOOTH_CONNECT
BLUETOOTH_ADVERTISE si nécessaire
ACCESS_FINE_LOCATION si requis
foreground service adapté
```

Les permissions ne doivent être demandées que dans le parcours expérimental.

---

## 🧭 Parcours utilisateur

| Activation | Désactivation |
| --- | --- |
| Ouvrir un écran avancé | Appuyer sur `Désactiver Direct capteur` |
| Choisir `Direct capteur` | Arrêter le collecteur |
| Lire les risques | Supprimer les données sensibles |
| Confirmer | Revenir à `Sync téléphone` |
| Autoriser BLE |  |
| Lancer un scan court |  |

---

## 🖥️ États UI

| État | Sens |
| --- | --- |
| `Recherche du capteur` | Scan en cours |
| `Association requise` | Pairing nécessaire |
| `Direct experimental actif` | Mode direct lancé |
| `Connexion capteur perdue` | Signal absent |
| `Batterie faible` | Retour téléphone conseillé |
| `Donnée ancienne` | Pas de lecture récente |

---

## 🧪 Lots de construction

| Lot | Objectif |
| --- | --- |
| 0 | Spike BLE Pixel Watch 2 |
| 1 | Abstraction de source |
| 2 | Repository local Wear |
| 3 | Pairing expérimental |
| 4 | Collecte minimale |
| 5 | Reconnexion |
| 6 | Sécurité |
| 7 | Autonomie |

> Sortie du lot 0 : rapport dans [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md).

---

## ✅ Go / No-Go

| Go prototype | Go intégration | No-go |
| --- | --- | --- |
| Capteur visible | Collecte stable plusieurs heures | Capteur invisible |
| Permissions maîtrisées | Reconnexion fiable | Batterie trop impactée |
| Batterie stable en scan court | Autonomie acceptable | Conflit Dexcom |
| Aucun conflit Dexcom | Pas de fuite sensible | Données incohérentes |

---

## 🛑 Décision

Ne pas lancer ce plan tant que le lot 0 n'est pas validé.

La priorité reste la sync :

```text
téléphone -> Wear OS
```
