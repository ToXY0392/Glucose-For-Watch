est-ce qu# Reprise Projet

Derniere mise a jour : 23 avril 2026

Ce document sert de point de reprise rapide pour continuer le projet sans relire tout l'historique du chat.

## 1. Etat actuel

- Projet Android `Widget G7` / `GlucoWatch`
- L'APK debug compile
- L'APK telephone s'installe sur le `Pixel 8a`
- L'APK Wear s'installe sur la `Pixel Watch 2`
- Le depot n'est plus propre : il y a des modifications locales non committees en cours
- Dernier commit de reference connu avant cette serie de modifications : `ed032cc`

## 2. Ce qui est deja en place

### Lancement et entree dans l'app

- Le lancement de l'app est maintenant tres rapide
- Le splash n'affiche plus de visuel complexe, seulement un fond simple
- Au premier lancement, l'app passe par un ecran `Connexion Dexcom`

### Connexion Dexcom

- Il existe un ecran d'entree dedie `Connexion Dexcom`
- Cet ecran est reserve a Dexcom
- Le texte d'aide est `Utilisez vos identifiants Dexcom`
- L'utilisateur doit accepter les textes juridiques avant de pouvoir se connecter

### Parcours juridique

- L'utilisateur doit accepter :
  - les `CGU`
  - la `Politique de confidentialite`
  - l'`Avertissement medical`
- Les textes sont ouvrables depuis l'ecran d'entree
- L'acceptation est memorisee localement avec une version
- Si la version juridique change plus tard, le flow peut etre redemande

### Notice

- La notice utilisateur a ete mise a jour pour le nouveau flow
- Elle explique que l'ecran `Connexion Dexcom` apparait avant l'accueil

### Synchronisation telephone / montre

- La logique de sync a ete rapprochee du rythme reel du Dexcom G7
- Le fallback auto cote telephone est passe de `2 min` a `5 min`
- Le telephone compare le `timestampEpochMs` de la mesure Dexcom avec la derniere mesure deja poussee a la montre
- Si la mesure n'est pas nouvelle, elle n'est pas renvoyee a la montre
- Si la mesure est nouvelle, elle est poussee immediatement a la montre
- Le refresh manuel existe toujours
- Un `PhoneSyncStateStore` suit maintenant :
  - la derniere mesure Dexcom recuperee
  - la derniere mesure reellement poussee a la montre
  - les derniers succes / echecs de push
- Cote montre, le cache affiche maintenant mieux l'age de la donnee
- Le statut `Aucune nouvelle mesure` est gere plus proprement lors d'un refresh manuel

### Complication / Tile

- La complication affiche de nouveau `mg/dL`
- La tile et la complication affichent maintenant une information plus explicite sur la fraicheur de la donnee

### Ecran montre

- L'ecran `Configuration de la montre` ne fait plus seulement un simple refresh de statut
- Le bouton principal lance maintenant un vrai test utile :
  - detection de la montre
  - verification qu'une source Dexcom est configuree
  - recuperation d'une vraie lecture si possible
  - tentative de push reel vers la montre
  - retour d'un message de resultat plus utile

### Multi-montres

- Si plusieurs montres sont connectees, l'app peut maintenant les afficher dans l'ecran `Configuration de la montre`
- L'utilisateur peut choisir une `montre principale`
- Ce choix est memorise localement via le `nodeId`
- L'accueil et l'ecran montre utilisent cette montre comme reference d'affichage

Important :

- la notion de `montre principale` est proprement geree dans l'UI et dans le test de liaison
- mais le transport Wear n'est pas encore strictement cible par montre
- autrement dit, l'app sait quelle montre est la reference utilisateur, mais la sync Data Layer reste encore globale

### Documents juridiques crees

Dans `docs` :

- [CGU.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/CGU.md)
- [POLITIQUE_CONFIDENTIALITE.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/POLITIQUE_CONFIDENTIALITE.md)
- [AVERTISSEMENT_MEDICAL.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/AVERTISSEMENT_MEDICAL.md)

Attention :

- ces textes sont une base solide
- ils contiennent encore des champs `[A completer]`
- ils doivent idealement etre relus par un juriste avant diffusion a des tiers

## 3. Ce qui a ete essaye puis annule

### Widget telephone

Un widget Android pour le telephone a ete cree, puis retire.

Decision finale actuelle :

- pas de widget telephone dans l'APK

### Splash avec image complexe

Plusieurs variantes de splash avec image montre ont ete testees.

Decision actuelle :

- eviter les doubles visuels au lancement
- rester sur un demarrage simple et rapide

## 4. Points ouverts / sensibles

### Texte Dexcom sur l'accueil

Le bloc `DEXCOM` affiche actuellement un temps du type `Connecte - X min`.

Decision utilisateur demandee :

- enlever ce temps de l'onglet Dexcom

### Passe grammaire / orthographe

Il reste une vraie passe de relecture a faire sur toute l'APK.

Points visibles a relire en priorite :

- [MainActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/MainActivity.kt)
- [activity_main.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_main.xml)
- [WatchSetupActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/ui/WatchSetupActivity.kt)
- [activity_watch_setup.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_watch_setup.xml)
- [activity_dexcom_entry.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_dexcom_entry.xml)
- [activity_dexcom_settings.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_dexcom_settings.xml)
- les textes dans `mobile/src/main/res/raw/`

### Multi-montres : limite actuelle

La gestion multi-montres est meilleure qu'avant, mais incomplète techniquement.

Etat actuel :

- l'utilisateur peut choisir une montre principale
- l'UI suit ce choix
- le test de liaison suit ce choix

Point non encore resolu :

- la sync Wear n'est pas encore strictement ciblee sur cette montre principale

### Verification securite / risques

Rien de critique n'a ete repere dans la partie locale au dernier passage, mais il reste deux risques produit/techniques :

- l'UI peut laisser croire que la montre principale est aussi la montre reellement ciblee par la sync, ce qui n'est pas encore strictement vrai
- le bouton de test montre agrege plusieurs causes d'echec possibles :
  - Dexcom
  - reseau
  - Bluetooth
  - montre

## 5. Dernier step atteint

Le dernier step effectivement atteint est :

- ajout d'une gestion UI de la multi-montre
- ajout d'un choix de montre principale
- rebuild
- reinstallation de l'APK telephone sur le `Pixel 8a`

Le tout dernier correctif applique juste avant ce checkpoint :

- retour de `mg/dL` dans la complication Wear
- rebuild de `wear`
- reinstallation de l'APK Wear sur la `Pixel Watch 2`

## 6. Reprise conseillee

Si on reprend le projet maintenant, l'ordre conseille est :

1. mettre a jour les textes visibles utilisateur
2. enlever le temps dans le bloc `DEXCOM`
3. faire une passe complete grammaire / orthographe dans toute l'app
4. verifier les failles / ambiguities restantes
5. decider ensuite si on pousse la gestion multi-montres jusqu'au vrai ciblage technique de la sync

## 7. Fichiers cles a relire en priorite

- [MainActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/MainActivity.kt)
- [activity_main.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_main.xml)
- [WatchSetupActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/ui/WatchSetupActivity.kt)
- [activity_watch_setup.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_watch_setup.xml)
- [PhoneGlucoseSyncEngine.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneGlucoseSyncEngine.kt)
- [PhoneSyncStateStore.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneSyncStateStore.kt)
- [PhoneWearSyncService.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearSyncService.kt)
- [WatchConnectionRepository.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/watch/WatchConnectionRepository.kt)
- [GlucoseCache.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt)
- [GlucoseComplicationService.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/wear/src/main/java/com/widgetg7/wear/complication/GlucoseComplicationService.kt)
- [notice_utilisateur.txt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/raw/notice_utilisateur.txt)
