<h1 align="center">🧪 Direct Capteur G7 -> Wear OS</h1>

<p align="center">
  Intéressant · risqué · expérimental · jamais prioritaire sur la sync téléphone
</p>

---

## 🟠 Verdict

```text
╭─ Décision ─────────────────────────────╮
│ Ne pas remplacer le mode téléphone.    │
│ Étudier le direct comme mode séparé.   │
╰────────────────────────────────────────╯
```

La meilleure solution produit :

1. garder `téléphone -> Wear OS` comme mode principal ;
2. renforcer ce mode en conditions réelles ;
3. étudier le direct capteur seulement comme expérimentation.

---

## 📌 Pourquoi

| Point | État |
| --- | --- |
| Direct to Watch | Support officiel Apple Watch |
| Wear OS direct | Pas de support officiel trouvé |
| Mode fiable Android | Capteur -> Dexcom / téléphone -> Widget G7 -> Wear |

Sources Dexcom :

- https://www.dexcom.com/en-us/m/faqs/what-is-direct-to-watch
- https://www.dexcom.com/en-us/faqs/what-smartwatches-have-direct-to-watch-compatibility
- https://www.dexcom.com/en-us/faqs/if-i-use-direct-to-watch-can-i-still-connect-to-aid-system

---

## 🧩 Options

| Option | Avantage | Limite |
| --- | --- | --- |
| `Sync téléphone` | Stable, simple, compatible Dexcom Share | Dépend du téléphone |
| `Wear Collector` | Usage possible sans téléphone | Batterie, BLE, support non officiel |
| `Source xDrip` | Utile pour prototype avancé | Dépendance tierce, parcours complexe |

---

## 🎯 Décision Produit

| Mode | Statut |
| --- | --- |
| `Sync téléphone` | Principal |
| `Direct capteur` | Expérimental, caché ou avancé |

> Le direct ne doit jamais casser le mode téléphone.

---

## 🏗️ Architecture Minimale

| Composant | Rôle |
| --- | --- |
| `SourceRouter` | Choisir téléphone ou source directe |
| `G7BleScanner` | Détecter les capteurs proches |
| `G7PairingManager` | Gérer l'association |
| `G7CollectorService` | Maintenir la collecte |
| `DirectReadingRepository` | Exposer la valeur locale |
| `DirectModeHealthCheck` | Surveiller batterie, fraîcheur, erreurs |

---

## 🛡️ Règles Obligatoires

```text
pas de code capteur en clair dans les logs
pas de secret Dexcom dans Wear
donnée toujours horodatée
état ancien visible
retour simple vers Sync téléphone
suppression complète des données directes
```

---

## ✅ Validation Avant Intégration

Avant tout code produit :

- [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md)

| Critère | Attendu |
| --- | --- |
| Détection | Pixel Watch 2 voit le capteur |
| BLE | Scan stable |
| Batterie | Impact acceptable |
| Dexcom officiel | Non perturbé |
| Données | Cohérentes |
