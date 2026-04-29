# Notice utilisateur

## Premiere configuration

1. Installer l'application sur le telephone.
2. Installer l'application Wear sur la montre.
3. Ouvrir Widget G7 sur le telephone.
4. Accepter les textes requis.
5. Se connecter avec les identifiants Dexcom Share.
6. Ouvrir les parametres montre.
7. Appuyer sur `Tester l'envoi`.
8. Ajouter la tile glucose ou la complication depuis Wear OS.

## Utilisation quotidienne

- L'accueil montre l'etat principal de la montre.
- Le bouton `Sync` relance une actualisation depuis le telephone.
- Le bouton de refresh sur la montre demande aussi une actualisation au telephone.
- Si aucune nouvelle mesure Dexcom n'est disponible, l'application peut renvoyer la derniere valeur connue a la montre.

## Ce que fait la montre

La montre affiche les donnees recues depuis le telephone. En mode actuel, elle ne se connecte pas directement au capteur G7.

## Si la valeur semble ancienne

Verifier :

- que la montre est connectee ;
- que le telephone a du reseau ;
- que Dexcom est encore connecte ;
- que la batterie de la montre n'est pas trop basse ;
- que la tile ou la complication est bien ajoutee sur la montre.

Ensuite, utiliser `Sync` sur le telephone ou le refresh depuis la montre.

## Mode direct capteur

Un mode avance `capteur G7 -> Wear OS` est etudie, mais il n'est pas actif dans l'application actuelle. Il restera experimental tant qu'il n'est pas valide sur la stabilite Bluetooth, l'autonomie et la coherence des mesures.
