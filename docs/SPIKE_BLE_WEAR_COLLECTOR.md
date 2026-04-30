<h1 align="center">🧪 Spike BLE Wear Collector</h1>

<p align="center">
  Voir si la Pixel Watch 2 voit le capteur, sans toucher au mode principal
</p>

---

## 🎯 Objectif

```text
╭─ Spike ────────────────────────────────╮
│  scanner court                         │
│  aucune lecture produit                │
│  aucun stockage capteur                │
│  aucun secret dans les logs            │
╰────────────────────────────────────────╯
```

Vérifier si la montre voit le capteur Dexcom G7 en BLE, sans modifier le parcours `téléphone -> Wear OS`.

---

## 🛡️ Règles

| Règle | Pourquoi |
| --- | --- |
| Pas de BLE dans le parcours principal | Protéger le mode fiable |
| Pas de lecture capteur hors écran expérimental | Éviter une activation implicite |
| Pas de code capteur dans les logs | Donnée sensible |
| Pas de stockage capteur pendant le spike | Réduire le risque |
| Retour au mode téléphone | Garder une sortie simple |

---

## ✅ Go / Stop

| Go | Stop |
| --- | --- |
| Capteur détecté plusieurs fois | Capteur invisible |
| Permissions BLE claires | Dexcom perturbé |
| Batterie stable en scan court | Batterie qui chute |
| App Dexcom non perturbée | Reconnexion instable |
| Résultats documentés | Donnée sensible dans les logs |

---

## 🧾 Rapport

```text
montre :
version Wear OS :
version capteur :
durée du scan :
batterie avant :
batterie après :
résultat BLE :
décision : go / retry / stop
```
