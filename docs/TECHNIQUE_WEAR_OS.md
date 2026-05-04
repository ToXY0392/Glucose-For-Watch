<h1 align="center">Technique Wear OS</h1>

<p align="center">
  Sync fiable · audit Dexcom · direct capteur · spike BLE
</p>

---

## Conclusion

```text
╭─ Voie fiable ─────────────────────────╮
│ Dexcom Share -> téléphone -> Wear OS   │
╰────────────────────────────────────────╯
```

Le téléphone reste la source principale. La montre Wear OS affiche la dernière valeur reçue, confirme la réception et peut demander un refresh.

Le mode `capteur G7 -> Wear OS` reste expérimental. Il ne doit pas entrer dans l'app principale tant qu'un spike BLE n'a pas validé la détection, l'autonomie, la sécurité et l'absence de perturbation Dexcom.

---

## Pourquoi Dexcom Ne Fournit Pas L'Équivalent Wear OS

| Sujet | État observé |
| --- | --- |
| Direct to Watch | Documenté officiellement pour Apple Watch compatible |
| Wear OS direct | Pas de support officiel équivalent identifié |
| Android / Wear classique | Montre traitée comme extension du téléphone |
| Share / Follow | Dépend encore de conditions réseau ou téléphone selon le mode |

Sources Dexcom :

- https://www.dexcom.com/en-us/faqs/what-is-direct-to-watch
- https://www.dexcom.com/compatibility/g7
- https://provider.dexcom.com/what-direct-watch
- https://provider.dexcom.com/share-available-my-patients-watch-when-direct-watch-mode

| Frein probable | Impact |
| --- | --- |
| Dispositif médical | Une app officielle engage Dexcom sur la sécurité patient |
| Alertes critiques | Hypo, hyper, perte de signal et retard doivent être fiables |
| BLE direct | Appairage, reconnexion, coexistence téléphone/receiver/AID complexes |
| Batterie Wear OS | Scan ou connexion continue peuvent vider la montre |
| Fragmentation Android | Beaucoup de montres et versions à tester |
| Support client | Chaque bug devient un cas médical potentiel |
| Données sensibles | Secrets, sessions, valeurs et logs doivent être verrouillés |

Différence avec Widget G7 :

| Point | Widget G7 | Dexcom officiel |
| --- | --- | --- |
| Rôle | Affichage compagnon | Système médical certifié |
| Source | Dexcom Share via téléphone | Capteur, téléphone, montre, écosystème Dexcom |
| Wear OS | Réception Data Layer | Récepteur critique potentiel |
| Alertes | À cadrer prudemment | Obligation de fiabilité forte |
| Support | Projet privé / test | Support patient à grande échelle |

---

## Architecture Standard

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

## Chemins Wear Data Layer

| Chemin | Rôle |
| --- | --- |
| `/glucose/latest` | Dernière valeur |
| `/glucose/refresh/request` | Demande de refresh depuis la montre |
| `/glucose/refresh/status` | Statut de refresh |
| `/glucose/watch/ack` | Accusé de réception montre |
| `/watch/status` | État batterie / sync montre |

---

## Règles De Robustesse

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

## Plan De Test Standard

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

## Direct Capteur

| Mode | Statut |
| --- | --- |
| `Sync téléphone` | Principal |
| `Direct capteur` | Expérimental, séparé, désactivable |

Le direct ne doit jamais casser le mode téléphone.

Architecture minimale si le direct devient viable :

| Composant | Rôle |
| --- | --- |
| `SourceRouter` | Choisir téléphone ou source directe |
| `G7BleScanner` | Détecter les capteurs proches |
| `G7PairingManager` | Gérer l'association |
| `G7CollectorService` | Maintenir la collecte |
| `G7PacketParser` | Transformer les paquets en lectures |
| `DirectReadingRepository` | Exposer la valeur locale |
| `DirectModeHealthCheck` | Surveiller batterie, fraîcheur, erreurs |

Règles obligatoires :

```text
pas de code capteur en clair dans les logs
pas de secret Dexcom dans Wear
donnée toujours horodatée
état ancien visible
retour simple vers Sync téléphone
suppression complète des données directes
```

---

## Spike BLE

Objectif : vérifier si la montre voit le capteur Dexcom G7 en BLE, sans modifier le parcours `téléphone -> Wear OS`.

```text
scanner court
aucune lecture produit
aucun stockage capteur
aucun secret dans les logs
```

| Go | Stop |
| --- | --- |
| Capteur détecté plusieurs fois | Capteur invisible |
| Permissions BLE claires | Dexcom perturbé |
| Batterie stable en scan court | Batterie qui chute |
| App Dexcom non perturbée | Reconnexion instable |
| Résultats documentés | Donnée sensible dans les logs |

Rapport attendu :

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

---

## Décision Projet

1. Garder `Dexcom Share -> téléphone -> Wear OS` comme mode principal.
2. Documenter Widget G7 comme app compagnon, pas comme remplacement Dexcom.
3. Renforcer le mode téléphone en conditions réelles.
4. Garder le direct capteur comme expérimentation séparée.
5. Ne pas stocker de secret Dexcom sur Wear OS.
6. Ne pas promettre d'alerte médicale garantie.
