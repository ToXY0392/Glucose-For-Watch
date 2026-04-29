# Mode d'emploi

Ce mode d'emploi decrit le parcours utile de Widget G7.

## 1. Installer

- Installer l'APK mobile sur le telephone.
- Installer l'APK Wear sur la montre.
- Ouvrir l'application mobile.

## 2. Connecter Dexcom

- Ouvrir l'ecran Dexcom.
- Accepter les textes requis.
- Saisir les identifiants Dexcom Share.
- Choisir la region.
- Enregistrer.

Apres une connexion reussie, l'application peut tenter d'envoyer la derniere glycemie vers la montre.

## 3. Configurer la montre

- Ouvrir `Parametres`.
- Ouvrir `Parametres montre`.
- Verifier que la montre est detectee.
- Lancer `Tester l'envoi`.
- Ajouter ensuite la tile glucose ou la complication depuis Wear OS.

## 4. Actualiser

- Depuis l'accueil telephone, utiliser `Sync`.
- Depuis la montre, utiliser le bouton refresh de la tile.

Le refresh montre demande au telephone de relancer la synchronisation. En mode actuel, la montre ne lit pas directement le capteur.

## 5. Comprendre les statuts

- `Connectee` : la montre est visible par le telephone.
- `Derniere sync` : une donnee a ete envoyee recemment.
- `Aucune nouvelle mesure` : Dexcom n'a pas encore fourni de valeur plus recente.
- `Donnee ancienne` : la derniere valeur connue existe, mais elle doit etre consideree comme stale.
- `Erreur` : Dexcom, le reseau ou la liaison montre demande une verification.

## 6. Mode direct capteur

Le mode direct `capteur G7 -> Wear OS` n'est pas encore implemente. Il est documente comme mode avance experimental dans :

- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)

La priorite reste de rendre la sync `telephone -> Wear OS` parfaitement fiable.
