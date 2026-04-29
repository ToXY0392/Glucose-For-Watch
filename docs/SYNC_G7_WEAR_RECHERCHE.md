# Recherche sync Dexcom G7 / Wear OS

Date de recherche : 30 avril 2026

Document lie :

- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)

## Conclusion courte

Pour rendre la sync vraiment solide, l'APK doit garder une architecture `telephone -> montre`.

La liaison directe `capteur G7 -> montre` existe officiellement chez Dexcom pour des Apple Watch compatibles, mais je n'ai pas trouve de support officiel equivalent pour Wear OS. Cote Android/Wear OS, Dexcom decrit la montre comme une extension du telephone : la montre recoit les donnees depuis le smartphone, pas directement depuis le capteur.

Donc la strategie fiable pour notre APK est :

1. le telephone reste le collecteur principal des donnees Dexcom ;
2. la montre Wear OS reste un affichage rapide et un declencheur de refresh ;
3. la sync doit etre idempotente, horodatee, persistante, testable hors-ligne, et tolerante aux deconnexions Bluetooth/Wi-Fi.

## Nombre d'appareils autour d'un capteur G7

Dexcom indique que le G7 peut afficher les donnees sur plusieurs appareils en meme temps : telephone, smartwatch, et recepteur Dexcom ou systeme AID compatible.

Point important : une page Dexcom Provider precise que le G7 dispose de trois canaux Bluetooth :

- smartphone ;
- smartwatch ;
- recepteur Dexcom ou systeme AID.

Source : Dexcom Provider, "If my patient uses the Direct to Watch feature, can they still connect to their AID system?"  
https://provider.dexcom.com/if-my-patient-uses-direct-watch-feature-can-they-still-connect-their-aid-system

Dexcom confirme aussi que l'utilisateur peut utiliser uniquement l'application G7, uniquement le recepteur, ou les deux en meme temps.

Source : Dexcom, "Do I need to use my receiver with Dexcom G7 CGM?"  
https://www.dexcom.com/en-us/faqs/do-i-need-to-use-my-receiver-with-dexcom-g7

Interpretation pour notre projet :

- il ne faut pas essayer de remplacer l'app Dexcom officielle comme recepteur Bluetooth du capteur ;
- il ne faut pas supposer qu'un capteur peut alimenter librement plusieurs telephones Android ;
- notre APK doit consommer une source autorisee/disponible cote telephone, puis relayer proprement vers Wear OS.

## Direct to Watch

Dexcom documente Direct to Watch pour Apple Watch. Les prerequis officiels cites sont Apple Watch 6 ou plus recent, watchOS 10 ou plus recent, iPhone sous iOS 17, et application Dexcom G7 version 2.1 minimum.

Source : Dexcom Provider, "What Device & Operating systems do my patients need for Direct to Watch?"  
https://provider.dexcom.com/what-device-operating-systems-do-my-patients-need-direct-watch

La page de compatibilite G7 indique que certaines montres peuvent voir la glucose sans telephone, mais la liste affichee est composee d'Apple Watch compatibles. La meme page precise que, pour l'usage smartwatch classique, la montre doit etre connectee au telephone et a portee du telephone.

Source : Dexcom, "Dexcom G7 Compatible Phones and Smartwatches"  
https://www.dexcom.com/compatibility/g7

Interpretation pour Wear OS :

- aucune preuve officielle trouvee d'une liaison directe `Dexcom G7 -> Wear OS` comparable a Direct to Watch Apple Watch ;
- Wear OS doit etre considere comme dependant du telephone ;
- si Dexcom ajoute un Direct to Watch Wear OS plus tard, il faudra l'isoler derriere une interface de source alternative, sans casser l'architecture actuelle.

## Wear OS / Android

Dexcom indique pour Android Wear que la montre communique avec le smartphone Android, pas avec le transmetteur Dexcom, et que les donnees peuvent avoir un leger delai avant d'apparaitre sur la montre.

Source : Dexcom Canada, "Can I view my Dexcom CGM data on an Android Wear watch?"  
https://www.dexcom.com/en-CA/faqs/can-i-view-my-dexcom-cgm-data-android-wear-watch

Meme si cette page cite le G6 dans son texte, elle est utile pour le principe Android Wear documente par Dexcom : la montre n'est pas le recepteur direct du capteur, elle synchronise les donnees depuis le telephone.

## Share / Follow

Dexcom Share permet de partager les donnees avec jusqu'a 10 followers. C'est une couche cloud de monitoring, pas une connexion Bluetooth directe au capteur.

Source : Dexcom, "How do I share my Dexcom G7 glucose data with family and followers?"  
https://www.dexcom.com/en-us/m/faqs/how-do-i-share-my-dexcom-g7-glucose-data-with-followers

Consequence :

- Share/Follow peut inspirer le modele "une source principale, plusieurs consommateurs" ;
- mais l'APK ne doit pas presenter la montre comme un dispositif medical principal ;
- les decisions de traitement doivent rester confirmees dans l'application Dexcom G7 ou le recepteur.

## Architecture recommandee pour notre sync

### Telephone

Le telephone doit etre la source de verite applicative.

Responsabilites :

- recuperer la derniere valeur glucose ;
- normaliser l'unite, la tendance, l'horodatage et l'etat de fraicheur ;
- persister la derniere valeur valide ;
- pousser la derniere valeur connue vers Wear OS ;
- signaler explicitement les erreurs et l'age des donnees.

La valeur ne doit jamais etre effacee juste parce qu'un refresh echoue. En cas d'erreur, l'UI doit afficher la derniere valeur connue avec un etat `stale`, `hors ligne`, `auth expiree`, ou `aucune donnee recente`.

### Wear OS

La montre doit etre un client leger.

Responsabilites :

- afficher la derniere valeur recue ;
- afficher l'age de la donnee ;
- afficher clairement l'etat de sync ;
- envoyer une demande de refresh au telephone ;
- ne jamais inventer de valeur ni masquer le fait qu'une donnee est ancienne.

Le bouton refresh de la montre doit declencher une demande, pas forcer une lecture capteur directe.

### Data Layer Wear OS

Chemins recommandes :

- `/glucose/latest` : derniere valeur complete ;
- `/glucose/refresh/request` : demande manuelle depuis la montre ;
- `/glucose/refresh/status` : etat du refresh ;
- `/glucose/watch/ack` : accuse de reception optionnel.

Payload minimal pour `/glucose/latest` :

- `valueMgdl` ;
- `unit` ;
- `trend` ;
- `readingTimestamp` ;
- `phoneSyncTimestamp` ;
- `source` ;
- `isStale` ;
- `staleReason` ;
- `sequenceId`.

Le `sequenceId` doit permettre d'ignorer les anciens messages arrives en retard.

## Regles de robustesse

1. Idempotence : pousser deux fois la meme valeur ne doit pas creer deux etats contradictoires.
2. Horodatage strict : distinguer l'heure de mesure Dexcom, l'heure de recuperation telephone, et l'heure de reception montre.
3. Stale visible : au-dela d'un seuil, la montre affiche que la donnee est ancienne.
4. Retry controle : backoff progressif, pas de boucle agressive.
5. Manual refresh prioritaire : un tap utilisateur peut lancer un refresh immediat, avec garde anti-spam.
6. Derniere valeur persistante : continuer a afficher la derniere valeur connue quand le reseau ou Bluetooth tombe.
7. Multi-montres : ne pas supposer une seule montre connectee ; envoyer a tous les noeuds capables ou choisir un noeud prefere explicitement.
8. Reconnexion : au retour de la montre, repusher immediatement la derniere valeur connue.
9. Etats explicites : `idle`, `refreshing`, `success`, `stale`, `offline`, `auth_error`, `source_error`.
10. Securite : stocker les secrets dans un stockage chiffre cote telephone, jamais dans le module Wear.

Contrainte projet ajoutee le 30 avril 2026 :

- viser une verification automatique toutes les 90 secondes ;
- signaler toute donnee de plus de 2 minutes comme ancienne ;
- ne jamais presenter une valeur de plus de 2 minutes comme parfaitement fraiche.

Limite technique :

- Dexcom G7 peut ne pas produire ou exposer une nouvelle lecture toutes les 2 minutes.
- L'app doit donc garantir la frequence de tentative et la transparence de fraicheur, pas promettre une mesure nouvelle inexistante.

## Risques a surveiller

- Dependances Dexcom cloud : indisponibilite reseau, expiration de session, changement d'API.
- Limites Bluetooth : distance telephone/montre, economie d'energie, Doze, montre non portee.
- Ambiguite medicale : l'APK doit rester un affichage pratique, pas une source de decision therapeutique.
- Source non officielle : si l'APK utilise des endpoints non publics, risque de compatibilite et de conformite.
- Donnees anciennes : le plus gros danger UX est d'afficher une valeur propre visuellement mais trop vieille.

## Plan de tests sync

Scenarios a tester avant de considerer la sync solide :

- telephone online, montre connectee ;
- telephone online, montre deconnectee puis reconnectee ;
- telephone offline, montre connectee ;
- Dexcom auth expiree ;
- aucune valeur recente ;
- valeur identique recue plusieurs fois ;
- refresh manuel spamme rapidement ;
- batterie faible / mode economie d'energie ;
- redemarrage telephone ;
- redemarrage montre ;
- installation nouvelle version mobile seule ;
- installation nouvelle version wear seule ;
- recepteur Dexcom utilise en parallele ;
- plusieurs montres Wear OS associees au meme telephone.

## Decision projet

La voie la plus solide pour Widget G7 est de consolider la sync telephone -> Wear OS, pas de tenter une liaison directe capteur -> Wear OS.

Le design technique doit privilegier :

- une source unique cote telephone ;
- une derniere valeur persistante ;
- une propagation Wear OS simple et previsible ;
- un etat de fraicheur impossible a ignorer ;
- aucune donnee sensible Dexcom stockee sur la montre ;
- aucune promesse de Direct to Watch Wear OS tant que Dexcom ne le supporte pas officiellement.

## Prochaine etape conseillee

Avant toute implementation du mode direct, lancer un spike BLE isole sur Pixel Watch 2 pour verifier que la montre voit le capteur, que les permissions sont maitrisables et que la batterie ne chute pas brutalement.
