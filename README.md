# Widget G7

Widget G7 est une application Android compagnon pour afficher une glycemie Dexcom G7 sur une montre Wear OS.

Le mode principal actuel est :

`Dexcom Share -> telephone Android -> Wear OS`

La montre affiche les donnees recues depuis le telephone via Wear Data Layer. Elle peut aussi demander un refresh manuel au telephone.

## Statut du projet

- Application mobile Android : en place.
- Application Wear OS : en place.
- Tile Wear OS : en place.
- Complication Wear OS : en place selon les cadrans compatibles.
- Sync telephone -> montre : renforcee par surveillance active avec service foreground, ack montre et repush.
- Mode direct capteur -> Wear OS : documente comme piste avancee, pas implemente.

## Surfaces prises en charge

- application Android telephone ;
- application Wear OS ;
- tile glucose Wear OS ;
- complication glucose Wear OS.

## Prerequis

- un telephone Android compatible ;
- une montre Wear OS connectee au telephone ;
- un compte Dexcom Share ;
- les APK `mobile` et `wear` installes sur les bons appareils.

Voir aussi : [COMPATIBILITY.md](COMPATIBILITY.md)

## Installation

### Depuis un APK

1. Installer l'APK `mobile` sur le telephone.
2. Installer l'APK `wear` sur la montre.
3. Ouvrir Widget G7 sur le telephone.
4. Se connecter a Dexcom.
5. Tester l'envoi vers la montre.
6. Ajouter la tile ou la complication depuis Wear OS.

### Depuis le code source

1. Ouvrir le projet dans Android Studio.
2. Laisser Gradle Sync se terminer.
3. Installer `mobile` sur le telephone.
4. Installer `wear` sur la montre.
5. Ouvrir l'application mobile.

## Configuration

1. Ouvrir Widget G7 sur le telephone.
2. Accepter les textes requis.
3. Saisir les identifiants Dexcom Share.
4. Choisir la region Dexcom.
5. Revenir a l'accueil.
6. Ouvrir les parametres montre.
7. Appuyer sur `Tester l'envoi`.
8. Ajouter la tile ou la complication sur la montre.

## Synchronisation actuelle

La chaine actuelle est :

1. le telephone interroge Dexcom Share ;
2. le telephone normalise et garde la derniere valeur connue ;
3. le telephone pousse la valeur vers Wear OS avec un `sequenceId` ;
4. la montre met a jour son cache, sa tile et sa complication ;
5. la montre renvoie un ack au telephone ;
6. le telephone repousse la derniere valeur si l'ack attendu n'arrive pas.

Quand Dexcom est configure, la sync active utilise un service foreground avec notification permanente. Le refresh manuel depuis la montre envoie une demande au telephone. La montre ne lit pas directement le capteur G7 dans le mode actuel.

## Strategie sync cible

Le projet retient deux niveaux :

- mode standard : `telephone -> Wear OS`, deja renforce par sync active et a valider en veille longue ;
- mode avance experimental : `capteur G7 -> Wear OS`, a prototyper uniquement apres validation BLE sur Pixel Watch 2.

Le mode standard reste prioritaire, car Dexcom ne documente officiellement le Direct to Watch que pour Apple Watch, pas pour Wear OS.

## Documentation principale

- Index documentaire : [docs/INDEX.md](docs/INDEX.md)
- Recherche sync G7 / Wear OS : [docs/SYNC_G7_WEAR_RECHERCHE.md](docs/SYNC_G7_WEAR_RECHERCHE.md)
- Solution directe capteur -> Wear OS : [docs/DIRECT_PATCH_WEAR_SOLUTION.md](docs/DIRECT_PATCH_WEAR_SOLUTION.md)
- Plan Wear Collector avance : [docs/PLAN_WEAR_COLLECTOR_AVANCE.md](docs/PLAN_WEAR_COLLECTOR_AVANCE.md)
- Reprise projet : [docs/REPRISE_PROJET.md](docs/REPRISE_PROJET.md)
- Mode d'emploi : [docs/MODE_D_EMPLOI.md](docs/MODE_D_EMPLOI.md)
- Notice utilisateur : [docs/NOTICE_UTILISATEUR.md](docs/NOTICE_UTILISATEUR.md)
- Notes de version : [docs/RELEASE_NOTES.md](docs/RELEASE_NOTES.md)
- Design system : [docs/DESIGN_SYSTEM.md](docs/DESIGN_SYSTEM.md)

## Points d'attention

- Widget G7 n'est pas un dispositif medical officiel.
- Les decisions de traitement doivent rester confirmees dans l'application Dexcom G7 ou le recepteur Dexcom.
- Les identifiants Dexcom doivent rester stockes localement et ne jamais etre commites.
- Le mode direct capteur -> Wear OS est une piste experimentale non officielle.

## Resume technique

- Modules : `mobile`, `wear`.
- Source principale actuelle : Dexcom Share cote telephone.
- Transport montre : Wear Data Layer avec push urgent, ack et repush.
- Affichage montre : cache local, tile, complication.
- Direction technique : isoler les sources glucose derriere une abstraction avant tout prototype Wear Collector.
