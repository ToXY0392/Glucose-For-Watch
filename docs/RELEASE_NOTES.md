# Release notes

## V1

Commit : `3b1b53b`

Premiere version publiable :

- application Android telephone ;
- application Wear OS ;
- synchronisation Dexcom Share vers Wear OS ;
- tile glucose ;
- complication selon cadran compatible ;
- configuration Dexcom ;
- aide de configuration montre.

## Dexcom session handling

Commit : `bc434ef`

Changements :

- session Dexcom mieux geree ;
- distinction entre erreur reseau et erreur d'identifiants ;
- messages utilisateur plus clairs ;
- bouton de deconnexion ;
- persistance de session plus robuste.

## Mise a jour avril 2026

Commit de reference : `ed032cc`

Changements principaux :

- splash simplifie ;
- ecran dedie `Connexion Dexcom` ;
- textes juridiques acceptes avant connexion Dexcom ;
- documents CGU, confidentialite et avertissement medical ;
- sync telephone -> montre rapprochee du rythme G7 ;
- refresh manuel plus robuste ;
- ecran montre transforme en test de liaison ;
- UI multi-montres avec choix d'une montre principale ;
- accueil telephone centre sur la montre ;
- menu `Parametres / Sync` simplifie ;
- palette blanche clinique avec vert en accent ;
- bouton refresh Wear corrige.

Validation connue :

- APK mobile debug compile et installe sur Pixel 8a ;
- APK Wear debug compile et installe sur Pixel Watch 2 ;
- tile glucose validee avec `mg/dL`.

## Documentation sync - 30 avril 2026

Documents ajoutes :

- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md)
- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)
- [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md)

Decision :

- le mode fiable reste `telephone -> Wear OS` ;
- Dexcom documente Direct to Watch pour Apple Watch, pas pour Wear OS ;
- le mode direct Wear OS reste experimental ;
- un spike BLE Pixel Watch 2 est obligatoire avant toute suite.

## Sync solide - 30 avril 2026

Changements :

- `sequenceId` sur chaque push telephone -> montre ;
- ack montre -> telephone sur `/glucose/watch/ack` ;
- stockage du dernier ack cote telephone ;
- repush borne si ack manquant ;
- detection defensive des donnees anciennes cote Wear ;
- service foreground actif avec polling environ `45 s` ;
- refresh manuel et refresh montre branches sur le meme moteur ;
- `AlarmManager` / `WorkManager` gardes comme filet de secours ;
- logs sensibles retires.

Validation :

- APK mobile debug compile et installe ;
- APK Wear debug compile et installe ;
- service foreground confirme par `dumpsys` ;
- reception montre et ack confirmes.

Limites :

- Dexcom Share peut publier une mesure avec retard ;
- Android peut limiter l'app sans exemption batterie ;
- une validation en veille longue reste necessaire.

## Reprise 1-5 - 30 avril 2026

Changements :

- permission Android pour demander l'exemption batterie ;
- bouton `Autoriser la sync en veille` dans l'ecran montre ;
- ciblage defensif de la montre principale via `targetNodeId` ;
- filtrage cote Wear des paquets non destines a la montre locale ;
- checklist juridique avant diffusion publique.

Validation :

- build debug mobile + wear OK ;
- APK installes sur Pixel 8a et Pixel Watch 2 ;
- `ActiveGlucoseSyncService` confirme en foreground ;
- app ajoutee a la whitelist batterie via ADB ;
- dernier push et ack confirmes dans les preferences debug.

Restes a faire :

- test veille longue ;
- test avec deux montres connectees ;
- champs juridiques `[A completer]` a renseigner.
