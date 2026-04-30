# Direct capteur G7 -> Wear OS

## Verdict

Ne pas remplacer le mode actuel par une connexion directe au capteur.

La meilleure solution produit est :

1. garder `telephone -> Wear OS` comme mode principal ;
2. renforcer ce mode en conditions reelles ;
3. etudier le direct capteur seulement comme mode experimental separe.

## Pourquoi

Dexcom documente Direct to Watch pour Apple Watch. Aucun support officiel equivalent Wear OS n'a ete trouve.

Sources Dexcom consultees :

- https://www.dexcom.com/en-us/m/faqs/what-is-direct-to-watch
- https://www.dexcom.com/en-us/faqs/what-smartwatches-have-direct-to-watch-compatibility
- https://www.dexcom.com/en-us/faqs/if-i-use-direct-to-watch-can-i-still-connect-to-aid-system

Cote Android/Wear OS, le fonctionnement fiable reste donc :

`capteur G7 -> Dexcom / telephone -> Widget G7 mobile -> Widget G7 Wear`

## Options

### Option A - Mode telephone

Mode actuel.

Avantages :

- stable ;
- compatible avec Dexcom Share ;
- pas de conflit Bluetooth direct avec le capteur ;
- secrets Dexcom conserves cote telephone ;
- consommation montre limitee.

Limite :

- la montre depend du telephone.

### Option B - Wear Collector experimental

La montre scannerait et lirait le capteur en BLE.

Avantages possibles :

- usage ponctuel sans telephone ;
- interessant pour sport ou sorties courtes.

Risques :

- pas de support officiel Dexcom Wear OS ;
- compatibilite variable selon montre et version Wear OS ;
- consommation batterie elevee ;
- conflit possible avec l'app Dexcom ou un autre recepteur ;
- maintenance technique importante.

### Option C - Source externe xDrip

Widget G7 pourrait consommer une source deja collectee par xDrip.

Avantage :

- utile pour prototype ou utilisateurs avances.

Limites :

- dependance a une app tierce ;
- parcours utilisateur plus complexe ;
- pas adapte comme mode principal.

## Decision produit

Le produit doit afficher deux modes seulement si le direct devient viable :

- `Sync telephone` : mode principal ;
- `Direct capteur` : mode experimental, cache ou reserve aux utilisateurs avances.

Le direct ne doit jamais casser le mode telephone.

## Architecture minimale si le direct est lance

- `SourceRouter` : choisit entre telephone et source directe.
- `G7BleScanner` : detecte les capteurs proches.
- `G7PairingManager` : gere l'association.
- `G7CollectorService` : maintient la collecte.
- `DirectReadingRepository` : expose une valeur locale a l'app, la tile et la complication.
- `DirectModeHealthCheck` : surveille batterie, fraicheur, erreurs et reconnexion.

## Regles obligatoires

- Aucun code capteur en clair dans les logs.
- Aucun secret Dexcom dans le module Wear.
- Donnee toujours horodatee.
- Etat ancien visible si aucune lecture recente.
- Retour simple vers `Sync telephone`.
- Suppression complete des donnees du mode direct.

## Validation avant integration

Avant tout code produit, faire le spike :

- [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md)

Le mode direct peut avancer seulement si :

1. la Pixel Watch 2 voit le capteur ;
2. le scan BLE reste stable ;
3. la batterie reste acceptable ;
4. l'app Dexcom officielle n'est pas perturbee ;
5. les donnees restent coherentes.
