<h1 align="center">🔁 Recherche Sync Dexcom G7 / Wear OS</h1>

<p align="center">
  Le téléphone collecte · Wear OS affiche · le direct reste expérimental
</p>

---

## 🟢 Conclusion

```text
╭─ Voie fiable ──────────────────────────╮
│ Dexcom Share -> téléphone -> Wear OS   │
╰────────────────────────────────────────╯
```

La montre Wear OS reste un affichage et un déclencheur de refresh. Elle ne doit pas être traitée comme récepteur direct du capteur G7 dans l'app actuelle.

---

## 📌 Support Dexcom

| Sujet | État |
| --- | --- |
| Direct to Watch | Documenté pour Apple Watch compatibles |
| Wear OS direct | Aucun support officiel équivalent trouvé |
| Android / Wear classique | Montre dépendante du téléphone |

Sources :

- https://www.dexcom.com/en-us/m/faqs/what-is-direct-to-watch
- https://www.dexcom.com/en-us/faqs/what-smartwatches-have-direct-to-watch-compatibility
- https://provider.dexcom.com/what-device-operating-systems-do-my-patients-need-direct-watch

---

## 🧭 Implications Produit

| Règle | Pourquoi |
| --- | --- |
| Le téléphone reste source de vérité | Dexcom Share est consommé côté mobile |
| Wear OS reçoit depuis le téléphone | C'est le mode fiable |
| Le refresh montre demande au téléphone | La montre ne force pas une lecture capteur |
| Aucun secret Dexcom sur Wear | Réduit le risque sensible |
| Âge de donnée visible ou détectable | Évite une valeur ancienne présentée comme fraîche |

---

## 🏗️ Architecture Recommandée

### Téléphone

| Action | Détail |
| --- | --- |
| Lire Dexcom Share | Récupérer la dernière valeur disponible |
| Conserver | Garder la dernière valeur connue |
| Horodater | Distinguer mesure, récupération et push |
| Pousser | Envoyer avec `sequenceId` |
| Vérifier | Recevoir l'ack montre |
| Réparer | Repush borné si ack manquant |

### Wear OS

| Action | Détail |
| --- | --- |
| Recevoir | Lire `/glucose/latest` |
| Filtrer | Ignorer si `targetNodeId` cible une autre montre |
| Cacher | Mettre à jour le cache local |
| Rafraîchir | App, tile et complication |
| Confirmer | Renvoyer un ack |
| Alerter | Signaler une donnée ancienne |

---

## 📡 Chemins Wear Data Layer

| Chemin | Rôle |
| --- | --- |
| `/glucose/latest` | Dernière valeur |
| `/glucose/refresh/request` | Demande de refresh depuis la montre |
| `/glucose/refresh/status` | Statut de refresh |
| `/glucose/watch/ack` | Accusé de réception montre |
| `/watch/status` | État batterie / sync montre |

---

## 🛡️ Règles De Robustesse

```text
ne pas effacer une valeur sur erreur
garder un sequenceId monotone
borner les repush
conserver le foreground service
garder AlarmManager / WorkManager en secours
ne pas loguer de valeur sensible
cibler la montre principale si besoin
```

---

## 🧪 Plan De Test

| Cas | À vérifier |
| --- | --- |
| Téléphone online + montre connectée | Push + ack |
| Montre déconnectée puis reconnectée | Repush / cache |
| Téléphone offline | Statut d'erreur clair |
| Dexcom sans nouvelle mesure | Dernière valeur connue |
| Identifiants expirés | Demande de reconnexion |
| Redémarrage téléphone | Service relancé |
| Redémarrage montre | Cache puis réception |
| Veille longue | Sync stable |
| Deux montres | Ciblage `targetNodeId` |
| Batterie faible | Statut limité |

---

## ✅ Décision Projet

Le mode standard `téléphone -> Wear OS` reste prioritaire.

Le mode direct `capteur G7 -> Wear OS` ne peut avancer qu'après validation du protocole :

- [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md)
