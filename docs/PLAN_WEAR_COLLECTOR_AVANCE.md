# Plan Wear Collector avance

Ce plan ne concerne pas l'app principale actuelle. Il sert uniquement si le mode direct `capteur G7 -> Wear OS` devient techniquement viable.

## Objectif

Construire un mode experimental ou la montre Wear OS collecte directement le capteur Dexcom G7 en Bluetooth.

Ce mode doit rester reversible et separe du mode standard :

`Dexcom Share -> telephone -> Wear OS`

## Principes

1. Le mode telephone reste prioritaire.
2. Le direct est experimental.
3. La tile, la complication et l'app Wear lisent un repository local, jamais le BLE directement.
4. Toute donnee affichee a un timestamp et un etat de fraicheur.
5. Les secrets et codes capteur ne sont jamais logues.
6. L'utilisateur peut revenir au mode telephone a tout moment.

## Architecture cible

- `SourceRouter` : selectionne `phoneRelay` ou `directSensor`.
- `G7BleScanner` : scanne les capteurs proches.
- `G7PairingManager` : gere l'association.
- `G7CollectorService` : collecte en arriere-plan avec garde-fous batterie.
- `G7PacketParser` : transforme les paquets en lectures exploitables.
- `DirectReadingRepository` : stocke la derniere lecture locale.
- `DirectModeHealthCheck` : expose l'etat du direct.

## Permissions probables

- `BLUETOOTH_SCAN`
- `BLUETOOTH_CONNECT`
- `BLUETOOTH_ADVERTISE` si necessaire
- `ACCESS_FINE_LOCATION` si requis par la pile BLE
- foreground service adapte a la collecte

Les permissions ne doivent etre demandees que dans le parcours experimental.

## Parcours utilisateur

Activation :

1. L'utilisateur ouvre un ecran avance.
2. Il choisit `Direct capteur`.
3. L'app affiche les risques : experimental, batterie, compatibilite, conflit possible.
4. L'utilisateur confirme.
5. L'app demande les permissions BLE.
6. L'app lance un scan court.
7. L'utilisateur associe le capteur si le scan est concluant.

Desactivation :

1. L'utilisateur appuie sur `Desactiver Direct capteur`.
2. Le collecteur s'arrete.
3. Les donnees sensibles locales sont supprimees.
4. L'app repasse en `Sync telephone`.

## Etats UI

- `Recherche du capteur`
- `Association requise`
- `Direct experimental actif`
- `Connexion capteur perdue`
- `Batterie faible`
- `Donnee ancienne`
- `Retour au mode telephone conseille`

## Lots de construction

### Lot 0 - Spike BLE Pixel Watch 2

But : verifier que la montre voit le capteur.

Sortie attendue : rapport dans [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md).

### Lot 1 - Abstraction source

Introduire une abstraction commune pour isoler :

- source telephone ;
- future source directe.

Le comportement actuel ne doit pas changer.

### Lot 2 - Repository local Wear

Faire lire l'app Wear, la tile et la complication depuis un repository local commun.

### Lot 3 - Pairing experimental

Ajouter un parcours cache pour associer un capteur, avec stockage securise et suppression complete.

### Lot 4 - Collecte minimale

Lire une premiere valeur, l'horodater, puis l'afficher via le repository local.

### Lot 5 - Reconnexion

Tester perte de signal, retour du signal, redemarrage montre et batterie faible.

### Lot 6 - Securite

Auditer stockage, logs, permissions et suppression des donnees.

### Lot 7 - Autonomie

Mesurer l'impact batterie sur plusieurs durees : 15 min, 1 h, 6 h, 12 h.

## Criteres go / no-go

Go prototype :

- capteur visible en scan BLE ;
- permissions maitrisees ;
- batterie stable en scan court ;
- aucun conflit visible avec Dexcom.

Go integration app principale :

- collecte stable plusieurs heures ;
- reconnexion fiable ;
- autonomie acceptable ;
- pas de fuite de donnees sensibles ;
- retour au mode telephone fiable.

No-go :

- capteur invisible ;
- batterie trop impactee ;
- conflit avec l'app Dexcom ou recepteur ;
- donnees incoherentes ;
- reconnexion instable.

## Decision

Ne pas lancer ce plan tant que le lot 0 n'est pas valide. La priorite reste la sync `telephone -> Wear OS`.
