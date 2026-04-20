# Widget G7

Widget G7 est une application Android compagnon pour :

- configurer Dexcom Share sur le telephone
- relier le telephone et la montre Wear OS
- installer et maintenir le tile / la complication
- garder une synchronisation fiable entre Dexcom, le telephone et la montre

L'application mobile n'est pas pensee comme un tableau de bord glucose complet. Son role principal est d'aider a l'installation, au diagnostic et a la maintenance de la chaine de sync.

## Surfaces prises en charge

- application Android telephone
- tile Wear OS
- complication Wear OS sur les cadrans compatibles

## Ce qu'il faut

- un telephone Android compatible : voir [COMPATIBILITY.md](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/COMPATIBILITY.md>)
- une montre Wear OS compatible et reliee au telephone
- un compte Dexcom Share

## Installation

### Depuis un APK

1. installer l'APK `mobile` sur le telephone
2. installer l'APK `wear` sur la montre
3. ouvrir `Widget G7` sur le telephone

### Depuis le code source

1. ouvrir le projet dans Android Studio
2. laisser Gradle sync terminer
3. installer `mobile` sur le telephone
4. installer `wear` sur la montre
5. ouvrir l'application telephone

## Premiere configuration

1. ouvrir `Widget G7`
2. toucher `Configurer Dexcom`
3. saisir :
   - email ou identifiant Dexcom
   - mot de passe
   - region `Europe` ou `US`
4. toucher `Tester la connexion`
5. si le test reussit, toucher `Enregistrer`
6. revenir a l'accueil
7. toucher `Configurer la montre`
8. ajouter ensuite le `Glucose Tile` ou la complication sur la montre

## Fonctionnement de la sync

La sync suit cette chaine :

1. le telephone interroge Dexcom Share
2. le telephone met a jour son etat local
3. le telephone pousse la donnee vers la montre via Wear Data Layer
4. la montre met a jour son cache, le tile et la complication

### Sync automatique

- le telephone planifie des sync en arriere-plan
- la cible reste courte, mais Android peut retarder l'execution
- il ne faut pas attendre un vrai "toutes les 2 minutes pile"

### Sync manuelle depuis le telephone

- bouton `Verifier la synchronisation`
- utile pour confirmer rapidement que la chaine telephone -> montre fonctionne encore

### Refresh manuel depuis la montre

- le tile contient un bouton de refresh
- la montre envoie une demande au telephone
- le telephone relance une sync Dexcom immediate
- puis renvoie la valeur vers la montre
- le tile garde la derniere valeur visible et remplace sa ligne de statut par `Actualisation...`
- la complication est rafraichie aussi quand la nouvelle donnee arrive

### Affichage des valeurs extremes

- le tile et la complication affichent `LOW` si la valeur est `<= 40 mg/dL`
- le tile et la complication affichent `HI` si la valeur est `>= 400 mg/dL`
- sinon la valeur numerique habituelle reste affichee

## Comportement batterie

### Cote telephone

Android peut retarder les sync si l'application reste soumise aux restrictions batterie.

Pour une meilleure fiabilite :

- ouvrir `Retirer la restriction batterie` dans l'application
- ou autoriser l'app sans restriction / ne pas optimiser selon le libelle du telephone

### Cote montre

Quand la batterie de la montre devient faible, Wear OS peut laisser la connexion visible tout en degradant les echanges app -> app.

Le projet embarque maintenant un watchdog cote montre qui :

- detecte une sync probablement limitee en batterie faible
- publie cet etat au telephone
- permet a l'application mobile d'afficher une alerte claire

Exemple :

- `Montre: batterie faible (15%)`
- `Sync montre limitee`

## Utilisation quotidienne

L'accueil de l'application sert surtout a repondre a ces questions :

- Dexcom est-il bien configure ?
- la montre est-elle bien reliee ?
- la sync tourne-t-elle encore ?
- faut-il intervenir sur la batterie ou relancer une verification ?

Le parcours normal est :

1. verifier le statut principal
2. verifier les lignes `Dexcom`, `Montre`, `Etat` et `Batterie`
3. toucher `Verifier la synchronisation` si besoin
4. ouvrir `Configurer la montre` si le tile ou la complication doivent etre remis en place

## Ajouter le tile sur la montre

1. verifier que la montre est connectee
2. ouvrir la liste des tiles sur la montre
3. ajouter `Glucose Tile`

## Ajouter la complication

1. appui long sur le cadran
2. ouvrir `Personnaliser`
3. aller dans `Complications`
4. choisir un slot compatible `SHORT_TEXT`, `LONG_TEXT` ou `RANGED_VALUE`
5. selectionner `Glucose`

## Depannage

### Dexcom ne se connecte pas

- verifier les identifiants
- verifier la region `Europe` ou `US`
- relancer `Tester la connexion`

### Le tile ne se met plus a jour

- verifier que la montre est encore connectee
- verifier la batterie de la montre
- verifier l'etat batterie du telephone
- toucher `Verifier la synchronisation`

### La montre semble connectee mais reste sur une vieille valeur

Cela peut arriver quand :

- la montre est en batterie faible
- le telephone est encore restreint en batterie
- Wear OS retarde les echanges Data Layer

Dans ce cas :

1. recharger la montre
2. retirer la restriction batterie cote telephone
3. relancer `Verifier la synchronisation`

## Confidentialite

- les identifiants Dexcom sont stockes localement sur le telephone
- ils ne doivent pas etre commits dans Git
- `gradle.properties` reste ignore par Git dans ce projet

## Documentation technique

- mode d'emploi : [docs/MODE_D_EMPLOI.md](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/docs/MODE_D_EMPLOI.md>)
- compatibilite Android / Wear OS : [COMPATIBILITY.md](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/COMPATIBILITY.md>)
- notes de version : [docs/RELEASE_NOTES.md](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/docs/RELEASE_NOTES.md>)

## Resume technique

- source prioritaire : Dexcom Share
- execution de sync centralisee dans le moteur telephone
- sync telephone -> montre via Wear Data Layer
- refresh manuel montre -> telephone pris en charge
- watchdog batterie faible cote montre
- remontée de l'etat de sante de sync montre vers le telephone
