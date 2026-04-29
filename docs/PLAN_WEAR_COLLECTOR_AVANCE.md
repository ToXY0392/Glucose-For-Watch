# Plan de construction - Mode avance Wear Collector

Date : 30 avril 2026

Documents lies :

- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md)
- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)

## Objectif

Construire un mode avance permettant a la montre Wear OS de collecter directement les donnees du capteur Dexcom G7 en Bluetooth, sans passer par le telephone.

Ce mode doit rester experimental tant que la stabilite, l'autonomie et la coherence des mesures ne sont pas prouvees.

## Positionnement produit

Le mode principal de Widget G7 reste :

`Dexcom/telephone -> Widget G7 mobile -> Widget G7 Wear`

Le mode avance devient :

`capteur G7 -> Widget G7 Wear`

Nom recommande dans l'interface :

`Direct capteur`

Libelle secondaire :

`Experimental`

Regle importante : l'utilisateur doit comprendre que ce mode n'est pas un Direct to Watch officiel Dexcom pour Wear OS.

## Principes de conception

1. Le mode direct ne doit jamais casser le mode telephone.
2. Le code du collecteur doit etre isole du reste de l'app Wear.
3. L'affichage Wear doit lire une source abstraite, pas parler directement au Bluetooth.
4. Les donnees doivent toujours inclure un horodatage et un etat de fraicheur.
5. Les identifiants capteur et codes d'appairage ne doivent jamais apparaitre dans les logs.
6. Le mode doit pouvoir etre desactive et nettoye completement.

## Architecture cible

### SourceRouter

Role :

- choisir la source active ;
- exposer une seule API a l'UI ;
- permettre de basculer entre `phoneRelay` et `directSensor`.

Etats :

- `PHONE_RELAY`
- `DIRECT_SENSOR_DISABLED`
- `DIRECT_SENSOR_SETUP`
- `DIRECT_SENSOR_ACTIVE`
- `DIRECT_SENSOR_ERROR`

### G7BleScanner

Role :

- scanner les appareils BLE proches ;
- filtrer les noms compatibles ;
- detecter les appareils `DXCM`, `DX01`, `DX02` ;
- exposer une liste minimale sans information sensible.

Contraintes :

- scan court ;
- timeout clair ;
- pas de scan permanent ;
- respect strict des permissions Wear OS.

### G7PairingManager

Role :

- gerer le code d'appairage ;
- declencher l'association BLE ;
- detecter les erreurs de code ;
- gerer les refus Android/Wear OS.

Etats :

- `idle`
- `waiting_code`
- `pairing`
- `paired`
- `failed`

### G7CollectorService

Role :

- maintenir la collecte directe ;
- recevoir les paquets ;
- envoyer les acquittements si necessaire ;
- relancer proprement en cas de perte.

Contraintes :

- service minimal ;
- wake locks limites ;
- backoff progressif ;
- arret automatique si batterie trop basse.

### G7PacketParser

Role :

- decoder les trames utiles ;
- extraire valeur, tendance, horodatage, statut capteur ;
- rejeter les paquets incomplets ou incoherents.

Regle :

- cette brique doit etre pure et testable sans Bluetooth.

### DirectReadingRepository

Role :

- stocker la derniere valeur valide ;
- stocker l'etat de connexion ;
- fournir les donnees au tile, a la complication et a l'ecran Wear.

Donnees minimales :

- `valueMgdl`
- `trend`
- `readingTimestamp`
- `receivedTimestamp`
- `sensorState`
- `connectionState`
- `staleReason`
- `source = direct_sensor`

### DirectModeHealthCheck

Role :

- diagnostiquer l'etat du mode direct ;
- afficher une raison claire en cas d'echec.

Diagnostics :

- Bluetooth desactive ;
- permission manquante ;
- capteur introuvable ;
- code invalide ;
- conflit collecteur probable ;
- batterie faible ;
- aucune lecture recente ;
- reconnexion en cours.

## Permissions a prevoir

Selon la version Wear OS / Android :

- `BLUETOOTH_SCAN`
- `BLUETOOTH_CONNECT`
- `BLUETOOTH_ADVERTISE` seulement si necessaire
- `ACCESS_FINE_LOCATION` si requis par la pile BLE
- permission de notification si le mode affiche des alertes
- exemption batterie uniquement si justifiee et expliquee

Les permissions doivent etre demandees au moment utile, pas au lancement de l'app.

## Parcours utilisateur

### Activation

1. L'utilisateur ouvre `Mode avance`.
2. Il choisit `Direct capteur`.
3. L'app affiche un avertissement clair :
   - mode experimental ;
   - non officiel Dexcom ;
   - peut consommer plus de batterie ;
   - peut entrer en conflit avec d'autres collecteurs.
4. L'utilisateur confirme.
5. L'app demande les permissions BLE.
6. L'utilisateur saisit le code d'appairage G7.
7. L'app scanne le capteur.
8. L'app tente l'appairage.
9. L'app affiche `connecte`, `en attente de lecture`, puis la premiere valeur.

### Desactivation

1. L'utilisateur appuie sur `Desactiver Direct capteur`.
2. L'app arrete le service.
3. L'app supprime les donnees sensibles locales.
4. L'app repasse en mode `Sync telephone`.
5. L'app indique clairement que le mode standard est actif.

## Etats UI a afficher

- `Recherche du capteur`
- `Appairage`
- `Connecte`
- `En attente de lecture`
- `Derniere lecture il y a X min`
- `Donnee ancienne`
- `Capteur perdu`
- `Bluetooth bloque`
- `Batterie faible`
- `Conflit collecteur possible`
- `Retour au mode telephone conseille`

L'UI ne doit jamais afficher une valeur sans age visible ou implicite.

## Gestion des conflits

Le mode direct peut entrer en conflit avec :

- app Dexcom officielle ;
- xDrip ;
- recepteur Dexcom ;
- pompe ou systeme AID ;
- autre montre connectee directement.

Regle produit :

- ne pas tenter de forcer la prise de controle du capteur ;
- ne pas masquer le risque ;
- proposer de revenir au mode telephone si la connexion directe n'est pas stable.

## Lots de construction

### Lot 0 - Spike BLE sur Pixel Watch 2

But :

- verifier que la montre voit le G7 en scan BLE ;
- valider les permissions ;
- mesurer la frequence de detection.

Livrable :

- ecran debug local ou logs controles ;
- decision `go / no-go`.

Critere de passage :

- capteur detecte plusieurs fois de suite ;
- pas de crash ;
- permissions maitrisables.

### Lot 1 - Abstraction de source

But :

- introduire `GlucoseSource` ;
- isoler source telephone et future source directe ;
- garder l'UI existante stable.

Livrable :

- `PhoneRelaySource` ;
- `DirectSensorSource` placeholder ;
- `SourceRouter`.

Critere de passage :

- comportement actuel identique en mode telephone.

### Lot 2 - Repository local Wear

But :

- centraliser derniere lecture et etat de fraicheur ;
- eviter que tile/complication dependent du BLE.

Livrable :

- `DirectReadingRepository` ;
- modele de donnees commun ;
- persistence minimale.

Critere de passage :

- l'UI Wear peut afficher une valeur simulee avec age et etat.

### Lot 3 - Pairing experimental

But :

- saisir le code capteur ;
- scanner ;
- tenter l'appairage.

Livrable :

- ecran avance de setup ;
- gestion permissions ;
- etats de pairing.

Critere de passage :

- appairage reussi ou echec explique sans bloquer l'app.

### Lot 4 - Collecte minimale

But :

- recevoir une lecture ;
- la parser ;
- la stocker ;
- l'afficher.

Livrable :

- `G7CollectorService` ;
- `G7PacketParser` ;
- stockage derniere valeur.

Critere de passage :

- premiere valeur affichee avec horodatage ;
- donnee marquee ancienne si aucune nouvelle lecture.

### Lot 5 - Reconnexion et robustesse

But :

- gerer perte de signal ;
- backoff ;
- reprise apres veille ;
- reprise apres reboot montre.

Livrable :

- machine d'etat complete ;
- diagnostics utilisateurs ;
- tests de deconnexion.

Critere de passage :

- reconnexion automatique apres perte courte ;
- pas de boucle batterie agressive.

### Lot 6 - Securite et nettoyage

But :

- chiffrer les donnees sensibles ;
- supprimer completement le mode direct ;
- nettoyer logs.

Livrable :

- stockage securise ;
- action `oublier capteur` ;
- audit logs.

Critere de passage :

- aucun code capteur en clair dans logs ou stockage simple.

### Lot 7 - Validation autonomie

But :

- mesurer impact batterie.

Livrable :

- protocole de test 2h, 6h, 12h ;
- seuils acceptables ;
- recommandation activation/desactivation.

Critere de passage :

- autonomie compatible avec un usage reel ;
- degradation affichee clairement si batterie faible.

## Tests obligatoires

- scan sans permission ;
- scan Bluetooth desactive ;
- capteur absent ;
- mauvais code ;
- ancien capteur a proximite ;
- appairage refuse ;
- lecture recue ;
- lecture incoherente ;
- perte 5 minutes ;
- perte 15 minutes ;
- reconnexion apres retour a portee ;
- reboot montre ;
- batterie faible ;
- bascule direct -> telephone ;
- bascule telephone -> direct ;
- suppression complete du mode direct.

## Criteres go / no-go

### Go prototype

- le capteur est visible depuis la Pixel Watch 2 ;
- l'app peut scanner sans instabilite ;
- la batterie ne chute pas brutalement en scan court ;
- l'UI peut expliquer clairement les echecs.

### Go integration app principale

- collecte stable plusieurs heures ;
- reconnexion automatique fiable ;
- pas de fuite de donnees sensibles ;
- pas de conflit non maitrise avec le mode telephone ;
- age de la donnee toujours visible ;
- retour au mode standard simple.

### No-go

- scan BLE instable sur montre ;
- autonomie trop degradee ;
- connexion directe qui casse trop souvent ;
- impossible de distinguer valeur recente et ancienne ;
- conflit dangereux avec Dexcom/AID/recepteur ;
- implementation trop fragile face aux mises a jour Dexcom.

## Ordre recommande

1. Faire un spike BLE Pixel Watch 2.
2. Ajouter l'abstraction `GlucoseSource`.
3. Stabiliser le repository local Wear.
4. Prototyper l'appairage direct.
5. Prototyper une lecture unique.
6. Ajouter reconnexion et diagnostics.
7. Auditer securite.
8. Mesurer batterie.
9. Decider integration ou abandon.

## Decision

Le mode Wear Collector est interessant, mais il doit etre construit comme une branche avancee et reversible.

La priorite reste de rendre la sync telephone -> Wear impeccable. Le mode direct ne doit arriver qu'apres une preuve technique sur Pixel Watch 2.

## Synthese actionnable

Prochaine action technique recommandee :

1. creer un spike BLE separe du parcours principal ;
2. scanner les appareils `DXCM`, `DX01`, `DX02` depuis la Pixel Watch 2 ;
3. mesurer stabilite et batterie ;
4. decider seulement ensuite si le prototype merite un lot d'appairage.
