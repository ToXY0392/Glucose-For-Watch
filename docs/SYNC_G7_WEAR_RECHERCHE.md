# Recherche sync Dexcom G7 / Wear OS

## Conclusion

La voie fiable pour Widget G7 est :

`Dexcom Share -> telephone Android -> Wear OS`

La montre Wear OS doit rester un affichage et un declencheur de refresh. Elle ne doit pas etre consideree comme recepteur direct du capteur G7 dans l'app actuelle.

## Ce que Dexcom supporte officiellement

Dexcom documente Direct to Watch pour Apple Watch compatibles.

Sources :

- https://www.dexcom.com/en-us/m/faqs/what-is-direct-to-watch
- https://www.dexcom.com/en-us/faqs/what-smartwatches-have-direct-to-watch-compatibility
- https://provider.dexcom.com/what-device-operating-systems-do-my-patients-need-direct-watch

Aucun support officiel comparable n'a ete trouve pour Wear OS.

## Ce que cela implique

Pour Widget G7 :

- le telephone reste la source de verite applicative ;
- Wear OS recoit les donnees depuis le telephone ;
- le refresh montre envoie une demande au telephone ;
- aucune donnee sensible Dexcom ne doit etre stockee sur la montre ;
- toute valeur affichee doit garder son age visible ou detectable.

## Architecture recommandee

### Telephone

- interroger Dexcom Share ;
- conserver la derniere valeur connue ;
- distinguer mesure Dexcom, heure de recuperation et heure de push ;
- envoyer la valeur a Wear OS avec `sequenceId` ;
- recevoir l'ack montre ;
- repusher de facon bornee si l'ack manque.

### Wear OS

- recevoir la valeur ;
- ignorer les paquets non destines a la montre locale si `targetNodeId` est renseigne ;
- mettre a jour le cache local ;
- rafraichir app, tile et complication ;
- renvoyer un ack ;
- signaler une donnee ancienne.

### Wear Data Layer

Chemins principaux :

- `/glucose/latest` : derniere valeur ;
- `/glucose/refresh/request` : demande de refresh depuis la montre ;
- `/glucose/refresh/status` : statut de refresh ;
- `/glucose/watch/ack` : accuse de reception montre ;
- `/watch/status` : etat batterie / sync montre.

## Regles de robustesse

1. Ne jamais effacer une valeur juste parce qu'un refresh echoue.
2. Afficher ou prendre en compte l'age de la valeur.
3. Garder un `sequenceId` monotone.
4. Borner les repush.
5. Garder le service foreground pour la sync active.
6. Conserver un fallback `AlarmManager` / `WorkManager`.
7. Ne pas loguer de valeurs sensibles.
8. Cibler la montre principale quand plusieurs montres sont connectees.

## Risques a tester

- Dexcom Share publie une mesure avec retard.
- Le telephone passe en Doze.
- L'optimisation batterie coupe ou retarde la sync.
- La montre est deconnectee puis reconnectee.
- La batterie montre est faible.
- Plusieurs montres sont associees.
- La complication depend du cadran et du slot choisi.

## Plan de test

Tester au minimum :

1. telephone online, montre connectee ;
2. telephone online, montre deconnectee puis reconnectee ;
3. telephone offline ;
4. Dexcom sans nouvelle mesure ;
5. identifiants Dexcom expires ;
6. redemarrage telephone ;
7. redemarrage montre ;
8. veille longue telephone + montre ;
9. deux montres connectees ;
10. batterie faible montre.

## Decision projet

Le mode standard `telephone -> Wear OS` reste prioritaire.

Le mode direct `capteur G7 -> Wear OS` est une piste experimentale. Il ne doit avancer qu'apres validation du protocole :

- [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md)
