# Widget G7

Widget G7 est une application Android compagnon pour :

- connecter un compte Dexcom Share
- relier un téléphone Android et une montre Wear OS
- afficher la glycémie sur un tile et une complication
- maintenir la synchronisation entre Dexcom, le téléphone et la montre

L'application mobile n'est pas un tableau de bord glucose complet. Elle sert surtout à installer, connecter et vérifier que tout fonctionne.

## Surfaces prises en charge

- application Android téléphone
- tile Wear OS
- complication Wear OS sur les cadrans compatibles

## Prérequis

- un téléphone Android compatible : voir [COMPATIBILITY.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/COMPATIBILITY.md)
- une montre Wear OS compatible, reliée au téléphone
- un compte Dexcom Share

## Installation

### Depuis un APK

1. Installer l'APK `mobile` sur le téléphone.
2. Installer l'APK `wear` sur la montre.
3. Ouvrir `Widget G7` sur le téléphone.

### Depuis le code source

1. Ouvrir le projet dans Android Studio.
2. Laisser Gradle Sync se terminer.
3. Installer `mobile` sur le téléphone.
4. Installer `wear` sur la montre.
5. Ouvrir l'application sur le téléphone.

## Première configuration

1. Ouvrir `Widget G7`.
2. Ouvrir la card `Dexcom`.
3. Appuyer sur `Se connecter`.
4. Saisir :
   - l'e-mail ou l'identifiant Dexcom
   - le mot de passe
   - la région `Europe` ou `US`
5. Appuyer sur `Enregistrer`.
6. Si la connexion est valide, un message `Connexion réussie` s'affiche.
7. Revenir à l'accueil.
8. Ouvrir les réglages de la montre depuis la card `Montre`.
9. Ajouter ensuite le `Glucose Tile` ou la complication sur la montre.

## Fonctionnement de la synchronisation

La synchronisation suit cette chaîne :

1. le téléphone interroge Dexcom Share
2. le téléphone met à jour son état local
3. le téléphone pousse la donnée vers la montre via Wear Data Layer
4. la montre met à jour son cache, le tile et la complication

### Synchronisation automatique

- le téléphone planifie des synchronisations en arrière-plan
- Android peut retarder leur exécution
- il ne faut pas attendre un rythme parfaitement fixe

### Actualisation manuelle depuis le téléphone

- l'accueil contient une icône de refresh dans la card `Montre`
- elle relance une synchronisation Dexcom depuis le téléphone

### Actualisation manuelle depuis la montre

- le tile contient une icône de refresh
- la montre envoie une demande au téléphone
- le téléphone relance une synchronisation Dexcom
- puis renvoie la valeur vers la montre

### Affichage des valeurs extrêmes

- le tile et la complication affichent `LOW` si la valeur est `<= 40 mg/dL`
- le tile et la complication affichent `HI` si la valeur est `>= 400 mg/dL`
- sinon la valeur numérique habituelle reste affichée

## Utilisation quotidienne

L'accueil répond surtout à ces questions :

- Dexcom est-il connecté ?
- la montre est-elle connectée ?
- faut-il rouvrir les réglages de la montre ?

Le parcours normal est :

1. vérifier l'état de la card `Montre`
2. vérifier l'état de la card `Dexcom`
3. utiliser l'icône de refresh si besoin
4. ouvrir les réglages de la montre si le tile ou la complication doivent être remis en place

## Ajouter le tile sur la montre

1. Vérifier que la montre est connectée.
2. Ouvrir la liste des tiles sur la montre.
3. Ajouter `Glucose Tile`.

## Ajouter la complication

1. Faire un appui long sur le cadran.
2. Ouvrir `Personnaliser`.
3. Aller dans `Complications`.
4. Choisir un slot compatible.
5. Sélectionner `Glucose`.

## Dépannage

### Dexcom ne se connecte pas

- vérifier les identifiants
- vérifier la région `Europe` ou `US`
- réessayer `Enregistrer`

### Le tile ne se met plus à jour

- vérifier que la montre est encore connectée
- vérifier la batterie de la montre
- utiliser le refresh depuis le téléphone ou la montre

### La montre semble connectée mais reste sur une vieille valeur

Cela peut arriver quand :

- la montre est en batterie faible
- le téléphone retarde les tâches en arrière-plan
- Wear OS retarde les échanges Data Layer

Dans ce cas :

1. recharger la montre
2. rouvrir les réglages utiles dans la card `Autorisations`
3. relancer une actualisation

## Confidentialité

- les identifiants Dexcom sont stockés localement sur le téléphone
- ils ne doivent pas être commités dans Git
- `gradle.properties` reste ignoré par Git dans ce projet

## Documentation

- notice simple : [docs/NOTICE_UTILISATEUR.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/NOTICE_UTILISATEUR.md)
- mode d'emploi : [docs/MODE_D_EMPLOI.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/MODE_D_EMPLOI.md)
- compatibilité Android / Wear OS : [COMPATIBILITY.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/COMPATIBILITY.md)
- notes de version : [docs/RELEASE_NOTES.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/RELEASE_NOTES.md)

## Résumé technique

- source prioritaire : Dexcom Share
- exécution de la synchronisation centralisée dans le moteur téléphone
- synchronisation téléphone -> montre via Wear Data Layer
- refresh manuel montre -> téléphone pris en charge
- état de connexion montre remonté dans l'application mobile
