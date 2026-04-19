# Mode d'emploi

## A quoi sert l'application

`Widget G7` permet d'afficher votre glycemie Dexcom G7 :

- sur votre telephone Android
- sur le tile de votre montre Wear OS
- sur une complication de cadran si votre cadran la prend bien en charge

## Avant de commencer

Vous avez besoin de :

- un telephone Android compatible
- une montre Wear OS connectee a ce telephone
- un compte Dexcom Share actif
- l'application `Widget G7` installee sur le telephone

## Premiere installation

### 1. Ouvrir l'application

Sur le telephone :

1. ouvrir `Widget G7`
2. attendre l'affichage de l'ecran d'accueil

### 2. Configurer Dexcom

1. toucher `Configurer Dexcom`
2. entrer :
   - votre email ou identifiant Dexcom
   - votre mot de passe Dexcom
   - votre region : `Europe` ou `US`
3. toucher `Tester la connexion`
4. si la connexion reussit, toucher `Enregistrer`

Si vous etes en France ou en Europe, choisissez `Europe`.

### 3. Configurer la montre

1. revenir sur l'accueil
2. toucher `Configurer la montre`
3. verifier que la montre est detectee

## Ajouter le tile sur la montre

Sur la montre :

1. ouvrir la liste des tiles
2. ajouter `Glucose Tile`

Le tile est la facon la plus simple et la plus fiable de voir si la synchronisation fonctionne.

## Ajouter la glycemie au cadran

Sur la montre :

1. faire un appui long sur le cadran
2. ouvrir `Personnaliser`
3. aller dans `Complications`
4. choisir un slot texte principal
5. selectionner `Glucose`

Si la complication disparait ensuite, essayez :

- un autre slot
- un autre cadran

## Utilisation quotidienne

Une fois l'installation terminee :

1. ouvrir l'application sur le telephone si besoin
2. verifier que la valeur glucose apparait
3. toucher `Actualiser maintenant` pour forcer une sync immediate
4. laisser ensuite l'application synchroniser automatiquement

L'application tente une synchronisation environ toutes les `2 minutes`.

## Comprendre l'ecran d'accueil

L'ecran principal affiche :

- la glycemie actuelle
- la tendance
- la derniere heure de synchronisation
- l'etat du compte Dexcom
- l'etat de la montre
- l'etat batterie pour savoir si Android peut ralentir la synchronisation

## Boutons utiles

### `Actualiser maintenant`

Force une synchronisation immediate entre Dexcom, le telephone et la montre.

### `Ameliorer la synchronisation`

Ouvre les reglages Android utiles pour limiter les coupures en arriere-plan dues a l'optimisation batterie.

### `Configurer Dexcom`

Permet de :

- modifier les identifiants
- tester la connexion
- deconnecter le compte

### `Configurer la montre`

Permet de :

- verifier que la montre est detectee
- revoir les etapes de configuration du tile

## Notifications

L'application peut afficher une notification si :

- votre compte Dexcom doit etre reconnecte
- la synchronisation echoue plusieurs fois de suite

## Depannage rapide

### Je ne vois aucune glycemie sur le telephone

1. ouvrir `Configurer Dexcom`
2. verifier les identifiants
3. verifier la region `Europe` ou `US`
4. relancer `Tester la connexion`

### Le tile ne se met pas a jour

1. verifier que la montre est connectee
2. toucher `Actualiser maintenant`
3. attendre quelques secondes
4. rouvrir le tile

### La synchronisation semble lente

1. toucher `Ameliorer la synchronisation`
2. autoriser l'application a mieux fonctionner en arriere-plan
3. verifier que les notifications ne signalent pas un blocage Dexcom

### L'application demande une reconnexion Dexcom

Cela signifie generalement que :

- le mot de passe a change
- Dexcom refuse a nouveau l'authentification

Dans ce cas :

1. ouvrir `Configurer Dexcom`
2. verifier les identifiants
3. tester la connexion
4. enregistrer a nouveau

## Confidentialite

- vos identifiants Dexcom sont stockes localement sur le telephone
- ils ne sont pas publies dans le depot Git
- vous pouvez supprimer le compte depuis l'ecran `Configurer Dexcom`
