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
- fallback automatique telephone passe de `2 min` a `5 min` ;
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

## Etat actuel

Le produit permet aujourd'hui :

- d'installer l'application sur un telephone Android ;
- de configurer Dexcom Share ;
- d'accepter les textes juridiques avant la connexion Dexcom ;
- de synchroniser la glycemie vers une montre Wear OS ;
- de relancer manuellement une synchronisation vers la montre ;
- d'utiliser une tile glucose sur la montre ;
- de choisir une montre principale dans l'interface telephone lorsque plusieurs montres sont detectees.

Points d'attention :

- la complication depend toujours du cadran choisi et du type de slot disponible ;
- la montre principale est prise en compte par l'UI et le test de liaison, mais le transport Wear Data Layer reste encore global ;
- les textes juridiques contiennent encore des champs `[A completer]` ;
- le mode direct capteur -> Wear OS n'est pas implemente.
