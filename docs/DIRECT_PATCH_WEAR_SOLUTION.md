# Solution directe capteur G7 -> Wear OS

Date de recherche : 30 avril 2026

Document lie :

- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)

## Verdict

La meilleure solution raisonnable pour brancher directement le capteur Dexcom G7 sur une montre Wear OS est de construire un mode avance de type `Wear Collector`, inspire du fonctionnement xDrip.

Mais ce n'est pas la solution a mettre par defaut.

Recommandation projet :

1. garder `telephone -> Wear OS` comme mode principal, fiable et simple ;
2. ajouter plus tard un mode experimental `capteur G7 -> Wear OS` uniquement si on accepte les risques Bluetooth, batterie, compatibilite et support ;
3. ne jamais presenter ce mode comme officiel Dexcom.

## Ce qui est officiellement supporte

Dexcom confirme que Direct to Watch connecte le G7 directement a une Apple Watch via Bluetooth. La compatibilite officielle Direct to Watch est limitee a Apple Watch.

Sources :

- Dexcom, "What is Dexcom G7 Direct to Watch?"  
  https://www.dexcom.com/en-us/m/faqs/what-is-direct-to-watch
- Dexcom, "What smartwatches have Direct to Watch compatibility?"  
  https://www.dexcom.com/en-us/faqs/what-smartwatches-have-direct-to-watch-compatibility

Dexcom indique aussi que le G7 dispose de trois canaux Bluetooth :

- smartphone ;
- smartwatch ;
- recepteur Dexcom ou systeme AID.

Source :

- Dexcom, "If I use Direct to Watch, can I still connect to my AID system?"  
  https://www.dexcom.com/en-us/faqs/if-i-use-direct-to-watch-can-i-still-connect-to-aid-system

Point cle : le canal "smartwatch" est documente par Dexcom pour Apple Watch, pas pour Wear OS.

## Ce qui existe cote communautaire

xDrip documente une collecte directe depuis G7, One+ ou Stelo. Le principe est de faire de l'app Android le collecteur Bluetooth du capteur.

Source :

- xDrip, "G7, One+ or Stelo"  
  https://navid200.github.io/xDrip/docs/Dexcom/G7.html

xDrip indique aussi une limite critique : une seule app peut collecter directement depuis le transmetteur a un instant donne. Si xDrip collecte directement, l'app Dexcom officielle ne peut pas collecter directement en meme temps sur le meme canal.

Source :

- xDrip, "Dexcom Device Compatibility and Limitations"  
  https://navid200.github.io/xDrip/docs/Receiver-or-tslim-and-xDrip.html

xDrip precise que sa solution n'est pas officiellement supportee par Dexcom.

Source :

- xDrip, "xDrip & Dexcom"  
  https://navid200.github.io/xDrip/docs/Dexcom_page.html

## Options possibles

### Option A - Garder le telephone collecteur principal

Flux :

`capteur G7 -> app Dexcom/telephone -> Widget G7 mobile -> Wear OS`

Avantages :

- solution la plus stable ;
- pas de conflit direct avec Dexcom ;
- meilleure autonomie montre ;
- moins de problemes Bluetooth ;
- plus facile a deboguer ;
- compatible avec le design actuel de l'APK.

Inconvenient :

- la montre depend du telephone.

Verdict : meilleur choix production.

### Option B - Wear Collector experimental

Flux :

`capteur G7 -> Widget G7 Wear`

La montre deviendrait le collecteur Bluetooth du capteur.

Avantages :

- vraie lecture sans telephone ;
- experience proche de Direct to Watch, mais non officielle ;
- utile pour sport ou sorties courtes sans telephone.

Inconvenients :

- non supporte officiellement par Dexcom ;
- risque de conflit avec l'app Dexcom ;
- implementation Bluetooth complexe ;
- forte pression batterie sur la montre ;
- fonctionnement variable selon montre, version Wear OS, chipset BLE et restrictions background ;
- validation medicale impossible cote projet ;
- risque de casse si Dexcom change firmware/protocole.

Verdict : faisable en R&D, pas recommande comme socle principal.

### Option C - Integrer xDrip comme source externe

Flux possible :

`capteur G7 -> xDrip -> Widget G7 mobile/Wear`

Avantages :

- xDrip sait deja collecter le G7 ;
- moins de reverse engineering dans notre APK ;
- communaute active ;
- permet de tester la faisabilite du direct avant de coder notre propre collecteur.

Inconvenients :

- dependance a une app tierce ;
- UX moins simple ;
- pas officiel Dexcom ;
- difficile a rendre propre pour un utilisateur non technique.

Verdict : meilleure piste de prototype, pas meilleure experience produit finale.

## Meilleure solution proposee

La meilleure solution globale est une architecture hybride :

### Mode 1 - Standard

Nom UI propose : `Sync telephone`

Flux :

`Dexcom/telephone -> Widget G7 mobile -> Widget G7 Wear`

Ce mode reste le mode par defaut.

Objectif :

- fiable ;
- simple ;
- peu energivore ;
- compatible utilisateur normal.

### Mode 2 - Direct experimental

Nom UI propose : `Direct capteur`, marque comme experimental.

Flux :

`G7 -> Widget G7 Wear`

Ce mode serait cache derriere un ecran avance avec avertissements clairs.

Conditions avant activation :

- saisir le code d'appairage G7 ;
- confirmer que l'utilisateur comprend que ce n'est pas un mode Dexcom officiel ;
- detecter si l'app Dexcom ou un autre collecteur est actif ;
- afficher les risques de perte de donnees, d'autonomie et de compatibilite ;
- permettre un retour simple au mode standard.

## Architecture technique du Wear Collector

Le module Wear aurait besoin de composants separes :

- `G7BleScanner` : scan BLE des appareils `DXCM`, `DX01`, `DX02` ;
- `G7PairingManager` : appairage avec le code a 4 chiffres ;
- `G7SessionStore` : stockage chiffre du code et de l'etat de session ;
- `G7PacketParser` : decodage des trames ;
- `G7CollectorService` : service de collecte avec contraintes batterie ;
- `ReadingRepository` : derniere valeur locale ;
- `DirectModeHealthCheck` : diagnostic de connexion, age, batterie, erreurs ;
- `SourceRouter` : choix entre source telephone et source directe.

Le `SourceRouter` est indispensable. Il permet de garder le meme affichage Wear, que la valeur vienne du telephone ou du capteur.

## Regles de securite

- Ne jamais stocker le code capteur en clair.
- Ne jamais envoyer le code capteur au telephone sans raison explicite.
- Ne jamais logger les identifiants Bluetooth complets.
- Ajouter une suppression complete du mode direct.
- Ajouter un indicateur visible `Direct experimental`.
- Conserver la derniere valeur avec son age, jamais sans horodatage.
- Afficher `donnee ancienne` si aucun paquet recent n'est recu.

## Regles de fiabilite

- Reconnexion automatique avec backoff.
- Detection des trous de 5, 10, 15 minutes.
- Etat clair : `scan`, `pairing`, `connected`, `waiting_reading`, `stale`, `lost`, `battery_saver_blocked`.
- Garde anti-spam sur refresh.
- Watch face/tile lisent depuis le repository local, pas directement depuis BLE.
- Si la batterie montre est faible, proposer de revenir en mode telephone.

## Plan de validation

Avant de coder ce mode, il faut valider :

- Pixel Watch 2 voit bien le capteur en scan BLE ;
- la montre peut maintenir une connexion BLE en arriere-plan ;
- l'autonomie reste acceptable sur 12 heures ;
- la collecte continue ecran eteint ;
- la reconnexion marche apres perte de signal ;
- aucun conflit dangereux avec app Dexcom, recepteur ou AID ;
- les donnees restent coherentes face a l'app Dexcom ou au recepteur.

## Decision recommandee

La meilleure solution produit n'est pas de basculer toute l'APK en direct capteur.

La meilleure solution est :

1. renforcer maintenant le mode `telephone -> Wear OS` ;
2. documenter et preparer une interface `GlucoseSource` propre ;
3. creer ensuite un prototype separe `Wear Collector` ;
4. ne l'activer dans l'app principale que s'il passe les tests batterie, reconnexion et coherence.

En clair : le direct capteur -> Wear OS est une piste interessante, mais il doit rester un mode avance experimental. Le mode fiable et propre pour l'utilisateur reste la sync par telephone.

## Suite documentaire

Le plan de construction detaille est dans [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md).
