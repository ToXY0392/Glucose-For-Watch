# Mode d'emploi

## 1. Installer

1. Installer l'APK `mobile` sur le telephone.
2. Installer l'APK `wear` sur la montre.
3. Ouvrir Widget G7 sur le telephone.

## 2. Connecter Dexcom

1. Ouvrir l'ecran Dexcom.
2. Accepter les textes requis.
3. Saisir les identifiants Dexcom Share.
4. Choisir la region Dexcom.
5. Enregistrer.

Une fois Dexcom configure, Widget G7 lance une sync active cote telephone. Une notification permanente indique que la glycemie est synchronisee vers la montre.

## 3. Configurer la montre

1. Ouvrir les parametres montre.
2. Verifier que la montre est detectee.
3. Si plusieurs montres sont connectees, choisir la montre principale.
4. Appuyer sur `Tester l'envoi`.
5. Autoriser la sync en veille si Android le propose.
6. Ajouter la tile glucose ou la complication depuis Wear OS.

## 4. Utiliser au quotidien

- `Synchroniser` sur le telephone force une actualisation.
- Le bouton refresh sur la montre demande aussi une actualisation au telephone.
- Si Dexcom ne fournit pas encore de nouvelle mesure, l'app peut renvoyer la derniere valeur connue.
- La montre affiche les donnees recues du telephone. Elle ne lit pas directement le capteur G7.

## 5. Comprendre les statuts

- `Connectee` : la montre est visible par le telephone.
- `Sync active` : le service foreground maintient la surveillance.
- `Montre verifiee` : la derniere livraison a ete confirmee par ack.
- `Aucune nouvelle mesure` : Dexcom Share n'a pas encore publie de valeur plus recente.
- `Erreur` : verifier Dexcom, le reseau, Bluetooth ou la montre.

## 6. Pour une sync plus stable

Verifier que :

- le telephone a du reseau ;
- la montre est connectee ;
- la notification permanente Widget G7 est active ;
- l'optimisation batterie est desactivee pour Widget G7 ;
- la batterie de la montre n'est pas trop basse.

## 7. Mode direct capteur

Le mode `capteur G7 -> Wear OS` n'est pas disponible dans l'app actuelle. Il est documente comme piste experimentale et ne doit pas etre integre sans validation BLE, autonomie et securite.
