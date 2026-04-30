# Spike BLE Wear Collector

Date : 30 avril 2026

Ce document cadre le premier test du mode direct `capteur G7 -> Wear OS`.

## Objectif

Verifier sur Pixel Watch 2 si la montre voit le capteur Dexcom G7 en BLE, sans modifier le parcours principal `telephone -> Wear OS`.

## Regles

- pas de code BLE dans le parcours principal ;
- pas de lecture capteur active hors ecran experimental ;
- pas de code capteur, identifiant BLE ou secret dans les logs ;
- pas de stockage de code capteur pendant le spike ;
- retour complet au mode telephone a tout moment.

## Criteres go

Le spike passe seulement si :

1. la Pixel Watch 2 detecte le capteur plusieurs fois ;
2. les permissions BLE sont claires et limitees au spike ;
3. le scan court ne degrade pas fortement la batterie ;
4. l'app Dexcom officielle et le telephone ne sont pas perturbes ;
5. les resultats sont documentes.

## Criteres stop

Arreter si :

- le capteur n'est pas visible ;
- Dexcom ou un autre recepteur est perturbe ;
- la batterie chute anormalement ;
- la reconnexion est instable ;
- une donnee sensible apparait dans les logs.

## Rapport attendu

Renseigner apres test :

- montre :
- version Wear OS :
- version capteur :
- duree du scan :
- batterie avant :
- batterie apres :
- resultat BLE :
- decision : `go`, `retry`, ou `stop`
