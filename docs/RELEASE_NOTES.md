# Release Notes

## V1

Commit : `3b1b53b`

Premiere version publiable du projet avec :

- application compagnon Android pour telephone
- synchronisation Dexcom Share vers la montre
- tile Wear OS fonctionnel
- complication de cadran disponible selon le cadran et le slot choisis
- interface telephone plus simple pour un usage non technique
- configuration Dexcom directement dans l'application
- ecran d'aide pour la configuration de la montre

## Mise a jour - Dexcom session handling

Commit : `bc434ef`

Ameliorations principales :

- gestion de session Dexcom plus propre
- distinction entre erreur d'authentification et erreur reseau
- messages utilisateur plus clairs dans l'application
- bouton deconnexion du compte Dexcom
- persistance de session plus robuste

Impact utilisateur :

- l'utilisateur peut rester connecte entre les lancements
- une panne reseau ne force plus une fausse deconnexion
- si Dexcom refuse les identifiants plusieurs fois, l'app demande clairement une reconnexion

## Mise a jour - Avril 2026

Commit de reference avant modifications recentes : `ed032cc`

Ameliorations principales :

- demarrage telephone plus rapide avec splash simplifie
- nouvel ecran dedie `Connexion Dexcom`
- acceptation obligatoire des textes juridiques avant connexion Dexcom
- ajout des documents `CGU`, `Politique de confidentialite` et `Avertissement medical`
- notice utilisateur mise a jour pour le nouveau parcours
- synchronisation telephone / montre rapprochee du rythme reel du Dexcom G7
- fallback automatique telephone passe de `2 min` a `5 min`
- meilleur suivi local de la derniere mesure Dexcom recuperee et de la derniere mesure poussee a la montre
- refresh manuel plus robuste : la derniere donnee connue peut etre repoussee a la montre meme sans nouvelle mesure Dexcom
- ecran `Configuration de la montre` transforme en vrai test de liaison utile
- prise en charge UI de plusieurs montres connectees avec choix d'une `montre principale`
- accueil telephone retravaille en hero screen centre sur la montre
- menu `Parametres / Sync` reorganise avec navigation secondaire vers montre, Dexcom et autorisations
- palette telephone clarifiee vers une base blanche plus clinique, avec vert en accent

Validation locale connue :

- APK debug telephone compile
- APK telephone installe sur le `Pixel 8a`
- APK Wear installe sur la `Pixel Watch 2`
- rendu Wear corrige et valide pour la tile glucose avec `mg/dL`

Decisions produit :

- pas de widget telephone dans l'APK
- ne plus toucher a la tile, a la complication, ni au design Wear sans demande explicite
- poursuivre les travaux cote telephone uniquement pour finaliser l'accueil autour de la montre
- afficher a terme l'image du modele de montre via un catalogue local, avec fallback generique

## Etat actuel

Le produit permet aujourd'hui :

- d'installer l'application sur un telephone Android
- de configurer Dexcom Share dans l'app
- d'accepter les textes juridiques avant la connexion Dexcom
- de synchroniser la glycemie vers une montre Wear OS
- de relancer manuellement une synchronisation vers la montre
- d'utiliser une tile glucose stable sur la montre
- de choisir une montre principale dans l'interface telephone lorsque plusieurs montres sont detectees

Point d'attention :

- la complication de cadran depend toujours du cadran choisi et du type de slot disponible
- la montre principale est prise en compte par l'UI et le test de liaison, mais le transport Wear Data Layer reste encore global
- les textes juridiques contiennent encore des champs `[A completer]` et doivent etre relus avant diffusion
