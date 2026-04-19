# Widget G7 - Installer et utiliser le produit

Widget G7 est une application Android compagnon pour afficher une glycemie Dexcom G7 sur :

- le telephone Android
- le tile de la montre Wear OS
- la complication de cadran si le cadran choisi accepte bien ce type de slot

## Apercu

![Surveillez votre glycemie](docs/assets/surveillez-votre-glycemie.png)

## Ce qu'il faut

- un telephone Android compatible : voir [COMPATIBILITY.md](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/COMPATIBILITY.md>)
- une montre Wear OS compatible et reliee au telephone
- un compte Dexcom Share
- l'application `Widget G7` installee sur le telephone

## Installation du produit

### Etape 1 - Installer l'application Android

Deux cas possibles :

1. si un APK est fourni, installer l'APK sur le telephone Android
2. si vous partez du code source, suivre la section `Installation depuis le code source` plus bas

### Etape 2 - Ouvrir l'application sur le telephone

Au premier lancement :

1. ouvrir `Widget G7`
2. toucher `Configurer Dexcom`
3. saisir :
   - email ou identifiant Dexcom
   - mot de passe Dexcom
   - region `Europe` ou `US`
4. toucher `Tester la connexion`
5. si le test reussit, toucher `Enregistrer`

## Utilisation quotidienne

Une fois Dexcom configure :

1. revenir a l'accueil
2. verifier que la glycemie s'affiche
3. toucher `Actualiser maintenant` pour une premiere synchronisation immediate
4. laisser ensuite l'application gerer l'auto-sync

L'application telephone :

- memorise le compte Dexcom localement
- synchronise automatiquement environ toutes les `2 minutes`
- affiche la derniere valeur, la tendance et l'etat de synchronisation

## Ajouter le tile sur la montre

1. verifier que la montre est connectee au telephone
2. ouvrir `Widget G7` sur le telephone
3. toucher `Configurer la montre`
4. sur la montre, ouvrir la liste des tiles
5. ajouter `Glucose Tile`

Le tile est aujourd'hui la surface la plus stable pour verifier que la synchronisation fonctionne bien.

## Ajouter la complication sur le cadran

1. faire un appui long sur le cadran de la montre
2. ouvrir `Personnaliser`
3. aller dans `Complications`
4. choisir un slot texte principal
5. selectionner `Glucose`

Important :

- certains cadrans acceptent mieux la complication que d'autres
- il faut privilegier un vrai slot texte, pas un micro-slot decoratif
- si la complication disparait au retour sur le cadran, essayer un autre slot ou un autre cadran

## Comment verifier que tout fonctionne

Le parcours conseille est :

1. verifier que l'app telephone affiche une glycemie
2. toucher `Actualiser maintenant`
3. ouvrir le tile sur la montre
4. verifier que la meme valeur apparait

Si le tile se met a jour, la chaine principale `telephone -> montre` fonctionne.

## Depannage simple

### Le telephone n'affiche rien

- verifier les identifiants Dexcom
- verifier la bonne region : `Europe` ou `US`
- relancer `Tester la connexion`

### Le tile montre n'affiche rien

- verifier que la montre est bien connectee
- toucher `Actualiser maintenant` sur le telephone
- attendre quelques secondes
- rouvrir le tile

### La complication de cadran ne reste pas affichee

- choisir un slot texte principal
- reessayer sur un autre cadran si besoin
- verifier d'abord que le tile fonctionne, car c'est le meilleur indicateur de synchronisation

## Installation depuis le code source

### Prerequis

- Android Studio
- un telephone Android branche ou connecte en debug
- une montre Wear OS branchee ou connectee en debug

### Etapes

1. ouvrir le projet dans Android Studio
2. laisser Gradle sync terminer
3. installer `mobile` sur le telephone
4. installer `wear` sur la montre
5. ouvrir l'application sur le telephone
6. configurer Dexcom dans l'app
7. toucher `Actualiser maintenant`
8. ajouter `Glucose Tile` sur la montre

## Confidentialite

- les identifiants Dexcom saisis dans l'application telephone sont stockes localement
- ils ne doivent pas etre commits dans Git
- `gradle.properties` est ignore par Git dans ce projet

## Documentation technique

- mode d'emploi : [docs/MODE_D_EMPLOI.md](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/docs/MODE_D_EMPLOI.md>)
- compatibilite Android et montres : [COMPATIBILITY.md](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/COMPATIBILITY.md>)
- notes de version : [docs/RELEASE_NOTES.md](<C:/Users/Utilisateur/Desktop/THP/Projects/Widget G7/docs/RELEASE_NOTES.md>)

## Resume technique

- source prioritaire : Dexcom Share
- fallback possible : relay backend
- sync telephone -> montre via Wear Data Layer
- tile montre : version semantique simple, sans graphe
- auto-sync telephone : cible `2 min`
- Android peut tout de meme retarder ce rythme en arriere-plan selon l'etat du telephone
