# Reprise Projet

Dernière mise à jour : 23 avril 2026

Ce document sert de point de reprise rapide pour continuer le projet sans relire tout l'historique du chat.

## 1. État actuel

- Projet Android `Widget G7` / `GlucoWatch`
- L'APK debug compile
- L'APK téléphone s'installe sur le `Pixel 8a`
- L'APK Wear s'installe sur la `Pixel Watch 2`
- Le dépôt n'est plus propre : il y a des modifications locales non commitées en cours
- Dernier commit de référence connu avant cette série de modifications : `ed032cc`

## 2. Ce qui est déjà en place

### Lancement et entrée dans l'app

- Le lancement de l'app est maintenant très rapide
- Le splash n'affiche plus de visuel complexe, seulement un fond simple
- Au premier lancement, l'app passe par un écran `Connexion Dexcom`

### Connexion Dexcom

- Il existe un écran d'entrée dédié `Connexion Dexcom`
- Cet écran est réservé à Dexcom
- Le texte d'aide est `Utilisez vos identifiants Dexcom`
- L'utilisateur doit accepter les textes juridiques avant de pouvoir se connecter

### Parcours juridique

- L'utilisateur doit accepter :
  - les `CGU`
  - la `Politique de confidentialité`
  - l'`Avertissement médical`
- Les textes sont ouvrables depuis l'écran d'entrée
- L'acceptation est mémorisée localement avec une version
- Si la version juridique change plus tard, le flow peut être redemandé

### Notice

- La notice utilisateur a été mise à jour pour le nouveau flow
- Elle explique que l'écran `Connexion Dexcom` apparaît avant l'accueil

### Synchronisation téléphone / montre

- La logique de sync a été rapprochée du rythme réel du Dexcom G7
- Le fallback auto côté téléphone est passé de `2 min` à `5 min`
- Le téléphone compare le `timestampEpochMs` de la mesure Dexcom avec la dernière mesure déjà poussée à la montre
- Si la mesure n'est pas nouvelle, elle n'est pas renvoyée à la montre
- Si la mesure est nouvelle, elle est poussée immédiatement à la montre
- Le refresh manuel existe toujours
- Un `PhoneSyncStateStore` suit maintenant :
  - la dernière mesure Dexcom récupérée
  - la dernière mesure réellement poussée à la montre
  - les derniers succès / échecs de push
- Côté montre, le cache affiche maintenant mieux l'âge de la donnée
- Le statut `Aucune nouvelle mesure` est géré plus proprement lors d'un refresh manuel

### Complication / Tile

- La complication affiche de nouveau `mg/dL`
- La tile et la complication affichent maintenant une information plus explicite sur la fraîcheur de la donnée

### Écran montre

- L'écran `Configuration de la montre` ne fait plus seulement un simple refresh de statut
- Le bouton principal lance maintenant un vrai test utile :
  - détection de la montre
  - vérification qu'une source Dexcom est configurée
  - récupération d'une vraie lecture si possible
  - tentative de push réel vers la montre
  - retour d'un message de résultat plus utile

### Multi-montres

- Si plusieurs montres sont connectées, l'app peut maintenant les afficher dans l'écran `Configuration de la montre`
- L'utilisateur peut choisir une `montre principale`
- Ce choix est mémorisé localement via le `nodeId`
- L'accueil et l'écran montre utilisent cette montre comme référence d'affichage

Important :

- la notion de `montre principale` est proprement gérée dans l'UI et dans le test de liaison
- mais le transport Wear n'est pas encore strictement ciblé par montre
- autrement dit, l'app sait quelle montre est la référence utilisateur, mais la sync Data Layer reste encore globale

### Design system

- Un design system a été rédigé dans [DESIGN_SYSTEM.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/DESIGN_SYSTEM.md)
- Une première passe d'application a déjà été faite sur les écrans principaux
- La direction actuelle est en train d'évoluer pour se rapprocher davantage du rendu visuel de l'app officielle Dexcom :
  - fond très clair crème vers vert pâle
  - gros titres noirs très lourds
  - sous-textes gris-vert doux
  - cards plates, calmes, avec contour fin vert grisé
  - boutons verts pleins, très arrondis

### Documents juridiques créés

Dans `docs` :

- [CGU.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/CGU.md)
- [POLITIQUE_CONFIDENTIALITE.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/POLITIQUE_CONFIDENTIALITE.md)
- [AVERTISSEMENT_MEDICAL.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/AVERTISSEMENT_MEDICAL.md)

Attention :

- ces textes sont une base solide
- ils contiennent encore des champs `[À compléter]`
- ils doivent idéalement être relus par un juriste avant diffusion à des tiers

## 3. Ce qui a été essayé puis annulé

### Widget téléphone

Un widget Android pour le téléphone a été créé, puis retiré.

Décision finale actuelle :

- pas de widget téléphone dans l'APK

### Splash avec image complexe

Plusieurs variantes de splash avec image montre ont été testées.

Décision actuelle :

- éviter les doubles visuels au lancement
- rester sur un démarrage simple et rapide

## 4. Points ouverts / sensibles

### Ressemblance visuelle avec l'app Dexcom

Nouvelle direction validée :

- l'utilisateur veut rapprocher l'interface du rendu de l'app officielle Dexcom
- il faut viser une app plus plate, plus clinique, plus sobre, moins "compagnon premium"
- priorité visuelle :
  - écran montre
  - accueil
  - connexion Dexcom
  - notice
  - écrans juridiques

### Image réelle du modèle de montre

Nouvelle décision produit / technique :

- au lieu d'afficher une image générique, l'app devra afficher l'image du modèle de montre connecté
- Android / Wear OS ne fournit pas directement une photo officielle exploitable
- la bonne solution retenue est :
  1. faire remonter par la montre ses informations réelles (`manufacturer`, `model`, éventuellement `device`)
  2. mapper ces informations côté téléphone vers un catalogue local d'images
  3. afficher une montre générique uniquement si le modèle n'est pas reconnu

Important :

- la stratégie retenue est un `mapping local par modèle`
- pas de récupération d'image depuis le système
- pas de dépendance web
- fallback générique si modèle inconnu

### Multi-montres : limite actuelle

La gestion multi-montres est meilleure qu'avant, mais incomplète techniquement.

État actuel :

- l'utilisateur peut choisir une montre principale
- l'UI suit ce choix
- le test de liaison suit ce choix

Point non encore résolu :

- la sync Wear n'est pas encore strictement ciblée sur cette montre principale

### Vérification sécurité / risques

Rien de critique n'a été repéré dans la partie locale au dernier passage, mais il reste deux risques produit / techniques :

- l'UI peut laisser croire que la montre principale est aussi la montre réellement ciblée par la sync, ce qui n'est pas encore strictement vrai
- le bouton de test montre agrège plusieurs causes d'échec possibles :
  - Dexcom
  - réseau
  - Bluetooth
  - montre

## 5. Dernier step atteint

Le dernier step effectivement atteint est :

- application d'une première passe du design system
- correction d'une partie importante des textes visibles utilisateur
- rebuild
- réinstallation de l'APK téléphone sur le `Pixel 8a`

Le point de reprise produit juste après ce checkpoint est :

- rapprocher visuellement l'app du style Dexcom officiel
- commencer par l'écran montre
- puis dérouler le reste dans le même style

## 6. Reprise conseillée

Si on reprend le projet maintenant, l'ordre conseillé est :

1. implémenter l'identification réelle du modèle de montre côté Wear
2. créer un mapping local `modèle -> image`
3. afficher cette image sur l'écran montre côté téléphone
4. refondre visuellement l'écran montre pour le rapprocher au maximum du style Dexcom
5. dérouler ensuite le même style sur l'accueil, la connexion Dexcom, la notice et les écrans juridiques

Note utile à reprendre telle quelle dans le prochain échange :

- Si tu veux, je peux maintenant passer au code et commencer par l'écran montre, puis dérouler le reste dans le même style.

## 7. Fichiers clés à relire en priorité

- [WatchSetupActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/ui/WatchSetupActivity.kt)
- [activity_watch_setup.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_watch_setup.xml)
- [WatchConnectionRepository.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/watch/WatchConnectionRepository.kt)
- [MainActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/MainActivity.kt)
- [activity_main.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_main.xml)
- [DESIGN_SYSTEM.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/DESIGN_SYSTEM.md)
- [PhoneGlucoseSyncEngine.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneGlucoseSyncEngine.kt)
- [PhoneSyncStateStore.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneSyncStateStore.kt)
- [PhoneWearSyncService.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearSyncService.kt)
- [GlucoseCache.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt)
- [GlucoseComplicationService.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/wear/src/main/java/com/widgetg7/wear/complication/GlucoseComplicationService.kt)
- [notice_utilisateur.txt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/raw/notice_utilisateur.txt)
