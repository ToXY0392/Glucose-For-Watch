# Reprise Projet

Derniere mise a jour : 30 avril 2026

Ce document sert de point de reprise rapide pour continuer le projet sans relire tout l'historique.

## 1. Etat actuel

- Projet Android multi-module : `mobile` et `wear`.
- Application mobile installee et testee sur Pixel 8a.
- Application Wear installee et testee sur Pixel Watch 2.
- Sync actuelle : `Dexcom Share -> telephone -> Wear OS`.
- Mode direct `capteur G7 -> Wear OS` : documente comme piste avancee, pas code.
- Le depot contient des modifications locales non commit.

## 2. Decisions recentes

### Sync

La strategie retenue est de rendre le mode standard tres solide :

`Dexcom/telephone -> Widget G7 mobile -> Widget G7 Wear`

La montre reste un client d'affichage et un declencheur de refresh. Elle ne collecte pas directement le capteur dans l'app actuelle.

### Direct capteur

Une recherche a ete faite sur le branchement direct du G7 sur Wear OS.

Conclusion :

- Dexcom supporte officiellement Direct to Watch sur Apple Watch.
- Aucun support officiel equivalent Wear OS n'a ete trouve.
- La meilleure piste technique serait un mode avance `Wear Collector`, inspire de xDrip.
- Ce mode doit rester experimental jusqu'a preuve de stabilite Bluetooth, autonomie et coherence.

Docs a lire :

- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md)
- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)

### Design

Direction actuelle :

- design mobile plus simple, centre sur une seule grande card ;
- montre dominante visuellement ;
- base blanche clinique ;
- vert comme accent principal ;
- peu d'ombre, peu de texte, actions directes.

La partie Wear a ete retouchee pour corriger le bouton refresh, mais elle doit rester stable sauf demande explicite ou spike technique isole.

## 3. Ce qui est en place

### Connexion Dexcom

- Ecran dedie `Connexion Dexcom`.
- Acceptation des textes juridiques avant connexion.
- Identifiants Dexcom Share stockes localement.
- Region Dexcom configurable.

### Parcours juridique

Documents presents :

- [CGU.md](CGU.md)
- [POLITIQUE_CONFIDENTIALITE.md](POLITIQUE_CONFIDENTIALITE.md)
- [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md)

Attention : ces textes doivent etre relus avant diffusion publique.

### Synchronisation telephone / montre

- Rythme rapproche du fonctionnement reel du G7.
- Cycle automatique telephone resserre a `90 s`.
- A l'ouverture/reprise de l'accueil, une sync est programmee rapidement apres environ `5 s`.
- Comparaison du timestamp Dexcom avec la derniere mesure poussee.
- Refresh manuel depuis telephone.
- Refresh manuel depuis montre.
- Repush possible de la derniere donnee connue meme sans nouvelle mesure Dexcom.
- Chaque push porte maintenant un `sequenceId`.
- La montre renvoie un ack sur `/glucose/watch/ack` apres reception de `/glucose/latest`.
- Le telephone stocke le dernier ack recu avec node id, timestamp de lecture et sequence.
- La montre marque defensivement une donnee comme ancienne apres 2 minutes.
- La source Dexcom cote telephone marque aussi une lecture comme stale apres 2 minutes.
- Cache montre utilise pour tile et complication.

Fichiers importants :

- [PhoneGlucoseSyncEngine.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneGlucoseSyncEngine.kt)
- [PhoneSyncStateStore.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneSyncStateStore.kt)
- [PhoneWearSyncService.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearSyncService.kt)
- [PhoneWearRefreshRequestService.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearRefreshRequestService.kt)
- [GlucoseCache.kt](../wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt)
- [GlucoseTileService.kt](../wear/src/main/java/com/widgetg7/wear/tile/GlucoseTileService.kt)
- [GlucoseComplicationService.kt](../wear/src/main/java/com/widgetg7/wear/complication/GlucoseComplicationService.kt)

### Ecran montre

- L'ecran `Configuration de la montre` est simplifie.
- Le bouton utile restant est `Tester l'envoi`.
- L'ancien bouton d'installation Wear a ete retire.
- La card `A verifier` a ete retiree.

### Multi-montres

- L'UI peut afficher plusieurs montres connectees.
- L'utilisateur peut choisir une montre principale.
- Le choix est memorise localement.
- Limite actuelle : le transport Data Layer n'est pas encore strictement cible sur une seule montre.

## 4. Documents a jour

- [INDEX.md](INDEX.md)
- [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md)
- [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md)
- [RELEASE_NOTES.md](RELEASE_NOTES.md)
- [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md)
- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md)
- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)

## 5. Points ouverts

### Sync standard a renforcer

Priorites :

1. verifier en conditions reelles que le cycle 90 s tient hors economie d'energie profonde ;
2. rendre l'age de la donnee impossible a ignorer ;
3. mieux gerer multi-montres et montre principale ;
4. eviter toute boucle de retry agressive ;
5. documenter clairement que Dexcom G7 peut ne pas exposer une nouvelle mesure toutes les 2 minutes.

### Wear Collector avance

Ordre recommande :

1. spike BLE Pixel Watch 2 ;
2. abstraction `GlucoseSource` ;
3. repository local Wear commun ;
4. pairing experimental ;
5. lecture unique ;
6. reconnexion ;
7. audit securite ;
8. mesure batterie.

Ne pas integrer ce mode dans l'app principale sans criteres `go` valides.

### Securite

Priorites :

- aucun secret Dexcom dans le module Wear ;
- stockage chiffre des donnees sensibles ;
- logs nettoyes ;
- suppression complete possible des donnees direct capteur si ce mode existe plus tard.

## 6. Commandes utiles

PowerShell local :

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:JAVA_TOOL_OPTIONS='-Duser.home="C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7"'
```

Builds habituels :

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat :mobile:assembleRelease :wear:assembleRelease
```

ADB connu :

```powershell
$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe devices
```

Appareils connus :

- Pixel 8a : `41031JEKB03416`
- Pixel Watch 2 : `3A251RTJWWKFFD`

## 7. Reprise conseillee

Ordre de travail recommande :

1. finir la solidification du mode standard telephone -> Wear ;
2. corriger les etats stale/offline et l'affichage de fraicheur ;
3. mieux cibler la montre principale ;
4. seulement ensuite lancer le spike BLE Wear Collector ;
5. garder le mode direct capteur isole tant qu'il n'est pas prouve.
