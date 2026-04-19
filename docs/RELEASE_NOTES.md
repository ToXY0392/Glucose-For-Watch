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

## Etat actuel

Le produit permet aujourd'hui :

- d'installer l'application sur un telephone Android
- de configurer Dexcom Share dans l'app
- de synchroniser la glycemie vers une montre Wear OS
- d'utiliser un tile glucose stable sur la montre

Point d'attention :

- la complication de cadran depend toujours du cadran choisi et du type de slot disponible
