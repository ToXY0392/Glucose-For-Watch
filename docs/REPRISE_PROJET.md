# Reprise projet

Derniere mise a jour : 30 avril 2026

Ce document sert a reprendre le projet rapidement.

## Etat actuel

- Projet Android multi-module : `mobile` et `wear`.
- App mobile testee sur Pixel 8a.
- App Wear testee sur Pixel Watch 2.
- Mode principal : `Dexcom Share -> telephone -> Wear OS`.
- Sync active cote telephone avec service foreground, ack montre et repush borne.
- Multi-montres : choix d'une montre principale, puis ciblage defensif des paquets Wear avec `targetNodeId`.
- Mode direct `capteur G7 -> Wear OS` : documente, experimental, pas implemente.

## Decisions a garder

### Sync standard

La priorite reste le mode :

`Dexcom Share -> Widget G7 mobile -> Widget G7 Wear`

La montre est un affichage et un declencheur de refresh. Elle ne collecte pas directement le capteur.

La sync standard utilise :

- un service foreground avec notification permanente ;
- un polling Dexcom rapproche, environ `45 s` ;
- une session Dexcom Share reutilisee en memoire ;
- un push Wear urgent avec `sequenceId` ;
- un ack montre sur `/glucose/watch/ack` ;
- un repush automatique si l'ack attendu manque ;
- `AlarmManager` / `WorkManager` comme filet de secours.

### Mode direct capteur

Conclusion actuelle :

- Dexcom supporte officiellement Direct to Watch sur Apple Watch.
- Aucun support officiel equivalent Wear OS n'a ete trouve.
- La piste Wear OS serait un `Wear Collector` experimental.
- Ne pas coder ce mode dans l'app principale sans spike BLE Pixel Watch 2 concluant.

Docs utiles :

- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md)
- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)
- [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md)

### Design

Direction actuelle :

- interface mobile simple ;
- grande presence visuelle de la montre ;
- fond blanc clinique ;
- vert en accent principal ;
- peu d'ombre, peu de texte, actions directes.

La partie Wear doit rester stable sauf correction ciblee ou spike technique isole.

## Fonctionnel en place

### Dexcom

- Ecran `Connexion Dexcom`.
- Acceptation des textes juridiques avant connexion.
- Identifiants Dexcom Share stockes localement.
- Region Dexcom configurable.

### Montre

- Ecran `Montre` simplifie.
- Test d'envoi.
- Choix de montre principale si plusieurs montres sont connectees.
- Action pour autoriser la sync en veille.
- Tile glucose.
- Complication glucose selon cadran compatible.

### Sync

- Boucle active telephone environ toutes les `45 s`.
- Refresh manuel depuis telephone.
- Refresh manuel depuis montre.
- Repush de la derniere valeur connue si besoin.
- Ack montre avec node id, timestamp et sequence.
- Ciblage defensif de la montre principale via `targetNodeId`.
- Cache Wear commun pour app, tile et complication.
- Logs sensibles retires.

Fichiers importants :

- [ActiveGlucoseSyncService.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/ActiveGlucoseSyncService.kt)
- [PhoneGlucoseSyncEngine.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneGlucoseSyncEngine.kt)
- [PhoneSyncStateStore.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneSyncStateStore.kt)
- [PhoneWearSyncService.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearSyncService.kt)
- [WatchConnectionRepository.kt](../mobile/src/main/java/com/widgetg7/mobile/watch/WatchConnectionRepository.kt)
- [WearDataLayerListenerService.kt](../wear/src/main/java/com/widgetg7/wear/services/WearDataLayerListenerService.kt)
- [GlucoseCache.kt](../wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt)
- [GlucoseTileService.kt](../wear/src/main/java/com/widgetg7/wear/tile/GlucoseTileService.kt)
- [GlucoseComplicationService.kt](../wear/src/main/java/com/widgetg7/wear/complication/GlucoseComplicationService.kt)

## Points ouverts

1. Valider la sync active en veille longue sur plusieurs heures.
2. Verifier l'exemption batterie sur appareils reels.
3. Tester le ciblage avec deux montres connectees.
4. Surveiller que le repush ack reste borne.
5. Documenter clairement les retards possibles de Dexcom Share.
6. Completer les champs juridiques avant diffusion publique.

## Juridique

Documents :

- [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md)
- [CGU.md](CGU.md)
- [POLITIQUE_CONFIDENTIALITE.md](POLITIQUE_CONFIDENTIALITE.md)
- [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md)

Ne pas publier l'APK hors test prive tant que les champs `[A completer]` ne sont pas renseignes.

## Commandes utiles

PowerShell local :

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:JAVA_TOOL_OPTIONS='-Duser.home="C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7"'
```

Builds :

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat :mobile:assembleRelease :wear:assembleRelease
```

ADB :

```powershell
$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe devices
```

Appareils de test connus :

- Pixel 8a ;
- Pixel Watch 2.

## Reprise conseillee

1. Lancer un build debug.
2. Installer sur telephone et montre.
3. Verifier que le service foreground tourne.
4. Tester l'ack montre.
5. Laisser tourner en veille longue.
6. Tester ensuite le cas multi-montres.
