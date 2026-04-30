# Release Notes

## V1

Commit : `3b1b53b`

Premiere version publiable du projet avec :

- application compagnon Android telephone ;
- synchronisation Dexcom Share vers la montre ;
- tile Wear OS fonctionnelle ;
- complication de cadran disponible selon le cadran et le slot choisis ;
- configuration Dexcom dans l'application ;
- ecran d'aide pour la configuration de la montre.

## Mise a jour - Dexcom session handling

Commit : `bc434ef`

Ameliorations principales :

- gestion de session Dexcom plus propre ;
- distinction entre erreur d'authentification et erreur reseau ;
- messages utilisateur plus clairs ;
- bouton de deconnexion du compte Dexcom ;
- persistance de session plus robuste.

Impact utilisateur :

- l'utilisateur peut rester connecte entre les lancements ;
- une panne reseau ne force plus une fausse deconnexion ;
- si Dexcom refuse les identifiants, l'app demande clairement une reconnexion.

## Mise a jour - Avril 2026

Commit de reference avant modifications recentes : `ed032cc`

Ameliorations principales :

- demarrage telephone plus rapide avec splash simplifie ;
- ecran dedie `Connexion Dexcom` ;
- acceptation obligatoire des textes juridiques avant connexion Dexcom ;
- ajout des documents `CGU`, `Politique de confidentialite` et `Avertissement medical` ;
- notice utilisateur mise a jour ;
- synchronisation telephone / montre rapprochee du rythme reel du Dexcom G7 ;
- fallback automatique telephone calibre au rythme G7, puis resserre ensuite pour contrainte forte de fraicheur ;
- meilleur suivi local de la derniere mesure Dexcom recuperee et de la derniere mesure poussee a la montre ;
- refresh manuel plus robuste : la derniere donnee connue peut etre repoussee a la montre meme sans nouvelle mesure Dexcom ;
- ecran `Configuration de la montre` transforme en test de liaison utile ;
- prise en charge UI de plusieurs montres connectees avec choix d'une `montre principale` ;
- accueil telephone retravaille en grand hero centre sur la montre ;
- menu `Parametres / Sync` reorganise ;
- palette telephone clarifiee vers une base blanche clinique, avec vert en accent ;
- suppression des elements inutiles de l'ecran verification montre ;
- bouton refresh Wear corrige visuellement.

Validation locale connue :

- APK debug telephone compile ;
- APK telephone installe sur le `Pixel 8a` ;
- APK Wear installe sur la `Pixel Watch 2` ;
- rendu Wear corrige et valide pour la tile glucose avec `mg/dL`.

## Mise a jour documentation sync - 30 avril 2026

Nouveaux documents :

- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md)
- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)
- [INDEX.md](INDEX.md)

Decisions documentees :

- Dexcom documente Direct to Watch officiellement pour Apple Watch, pas pour Wear OS.
- Le mode fiable actuel reste `telephone -> Wear OS`.
- Le mode direct `capteur G7 -> Wear OS` est une piste avancee de type Wear Collector.
- Le Wear Collector doit rester experimental tant qu'un spike BLE Pixel Watch 2 n'a pas prouve la faisabilite.
- Toute future implementation doit isoler les sources glucose derriere une abstraction.

## Mise a jour sync solide - 30 avril 2026

Ameliorations principales :

- ajout d'un `sequenceId` sur chaque push telephone -> montre ;
- ajout d'un accuse de reception montre -> telephone sur `/glucose/watch/ack` ;
- stockage cote telephone du dernier ack recu avec timestamp, sequence et node id ;
- detection defensive cote Wear des donnees trop anciennes meme si le telephone les avait envoyees comme non stale ;
- le test d'envoi depuis l'ecran montre enregistre aussi le push dans l'etat local de sync.

Impact :

- la chaine d'envoi est maintenant tracable de bout en bout ;
- le telephone peut savoir qu'une montre a bien recu une valeur ;
- la montre evite d'afficher trop longtemps une valeur propre visuellement mais ancienne ;
- les refresh manuels restent compatibles avec la derniere valeur connue.

## Mise a jour contrainte 2 minutes - 30 avril 2026

Changement de contrainte :

- l'application ne doit plus laisser une sync ou une donnee paraitre fraiche au-dela de 2 minutes.

Ameliorations :

- cycle automatique telephone resserre a 90 secondes ;
- lancement d'une sync rapide environ 5 secondes apres ouverture/reprise de l'accueil si Dexcom est configure ;
- seuil stale Dexcom cote telephone ramene a 2 minutes ;
- seuil stale cote Wear ramene a 2 minutes ;
- diagnostic batterie/sync limitee cote Wear aligne sur 2 minutes.

Note importante :

- Dexcom G7 ne fournit pas forcement une nouvelle mesure toutes les 2 minutes.
- L'app peut garantir qu'elle tente de synchroniser plus souvent et qu'elle signale une valeur trop ancienne.
- Elle ne peut pas inventer une nouvelle mesure si Dexcom n'en expose pas encore.

## Mise a jour sync active stable - 30 avril 2026

Objectif :

- remplacer la dependance principale au cycle `AlarmManager` / `WorkManager` par une surveillance active telephone -> montre.

Ameliorations principales :

- ajout d'un `ActiveGlucoseSyncService` foreground avec notification permanente ;
- boucle de sync active autour de `45 s` quand Dexcom est configure ;
- session Dexcom Share reutilisee en memoire, avec relogin seulement si la session expire ou est refusee ;
- bouton manuel et refresh montre branches sur le meme moteur de sync active ;
- conservation de `AlarmManager` / `WorkManager` comme filet de secours ;
- repush automatique de la derniere valeur si le dernier `sequenceId` pousse n'est pas ack par la montre ;
- activation automatique de la sync active pour les comptes Dexcom deja configures ;
- retrait des logs bavards ou sensibles dans les APK ;
- labels APK simplifies en `Widget G7` ;
- retrait des messages utilisateur `Mesure ancienne`, `Donnee agee` et `Mesure il y a ...` sur les surfaces principales.

Validation locale :

- APK mobile debug compile et installe sur Pixel 8a ;
- APK Wear debug compile et installe sur Pixel Watch 2 ;
- service foreground confirme par `dumpsys` avec notification `Widget G7 synchronise la glycemie` ;
- reception montre et ack confirmes ;
- une nouvelle valeur Dexcom a ete captee, poussee vers la montre et ackee.

Limites restantes :

- Dexcom Share peut publier les mesures avec retard ;
- Android peut encore limiter l'app sans exemption batterie ;
- le transport Wear Data Layer reste global si plusieurs montres sont connectees ;
- une validation en veille longue sur plusieurs heures reste necessaire.

## Etat actuel

Le produit permet aujourd'hui :

- d'installer l'application sur un telephone Android ;
- de configurer Dexcom Share ;
- d'accepter les textes juridiques avant la connexion Dexcom ;
- de synchroniser la glycemie vers une montre Wear OS ;
- de maintenir une surveillance active telephone -> montre avec notification permanente ;
- de relancer manuellement une synchronisation vers la montre ;
- d'utiliser une tile glucose sur la montre ;
- de choisir une montre principale dans l'interface telephone lorsque plusieurs montres sont detectees.

Points d'attention :

- la complication depend toujours du cadran choisi et du type de slot disponible ;
- la montre principale est prise en compte par l'UI et le test de liaison, mais le transport Wear Data Layer reste encore global ;
- la sync active est plus stable que le cycle 90 s precedent, mais doit encore etre testee en veille longue ;
- les textes juridiques contiennent encore des champs `[A completer]` ;
- le mode direct capteur -> Wear OS n'est pas implemente.
