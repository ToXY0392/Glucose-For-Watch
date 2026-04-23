# Reprise Projet

Dernière mise à jour : 23 avril 2026

Ce document sert de point de reprise rapide pour continuer le projet sans relire tout l’historique du chat.

## 1. État actuel

- Projet Android `Widget G7` / `GlucoWatch`
- L’APK debug téléphone compile
- L’APK téléphone s’installe sur le `Pixel 8a`
- L’APK Wear s’installe sur la `Pixel Watch 2`
- Le dépôt n’est pas propre : il y a des modifications locales non commitées en cours
- Dernier commit de référence connu avant cette série de modifications : `ed032cc`

## 2. Ce qui est déjà en place

### Lancement et entrée dans l’app

- Le lancement de l’app est maintenant très rapide
- Le splash n’affiche plus de visuel complexe, seulement un fond simple
- Au premier lancement, l’app passe par un écran `Connexion Dexcom`

### Connexion Dexcom

- Il existe un écran d’entrée dédié `Connexion Dexcom`
- Cet écran est réservé à Dexcom
- Le texte d’aide est `Utilisez vos identifiants Dexcom`
- L’utilisateur doit accepter les textes juridiques avant de pouvoir se connecter

### Parcours juridique

- L’utilisateur doit accepter :
  - les `CGU`
  - la `Politique de confidentialité`
  - l’`Avertissement médical`
- Les textes sont ouvrables depuis l’écran d’entrée
- L’acceptation est mémorisée localement avec une version
- Si la version juridique change plus tard, le flow peut être redemandé

### Notice

- La notice utilisateur a été mise à jour pour le nouveau flow
- Elle explique que l’écran `Connexion Dexcom` apparaît avant l’accueil

### Synchronisation téléphone / montre

- La logique de sync a été rapprochée du rythme réel du Dexcom G7
- Le fallback auto côté téléphone est passé de `2 min` à `5 min`
- Le téléphone compare le `timestampEpochMs` de la mesure Dexcom avec la dernière mesure déjà poussée à la montre
- Si la mesure n’est pas nouvelle, elle n’est pas renvoyée à la montre
- Si la mesure est nouvelle, elle est poussée immédiatement à la montre
- Le refresh manuel existe toujours
- Un `PhoneSyncStateStore` suit maintenant :
  - la dernière mesure Dexcom récupérée
  - la dernière mesure réellement poussée à la montre
  - les derniers succès / échecs de push
- Côté montre, le cache affiche maintenant mieux l’âge de la donnée
- Le statut `Aucune nouvelle mesure` est géré plus proprement lors d’un refresh manuel

### Complication / Tile

- La complication affiche de nouveau `mg/dL`
- La tile et la complication affichent maintenant une information plus explicite sur la fraîcheur de la donnée

### Écran montre

- L’écran `Configuration de la montre` ne fait plus seulement un simple refresh de statut
- Le bouton principal lance maintenant un vrai test utile :
  - détection de la montre
  - vérification qu’une source Dexcom est configurée
  - récupération d’une vraie lecture si possible
  - tentative de push réel vers la montre
  - retour d’un message de résultat plus utile

### Multi-montres

- Si plusieurs montres sont connectées, l’app peut maintenant les afficher dans l’écran `Configuration de la montre`
- L’utilisateur peut choisir une `montre principale`
- Ce choix est mémorisé localement via le `nodeId`
- L’accueil et l’écran montre utilisent cette montre comme référence d’affichage

Important :

- la notion de `montre principale` est proprement gérée dans l’UI et dans le test de liaison
- mais le transport Wear n’est pas encore strictement ciblé par montre
- autrement dit, l’app sait quelle montre est la référence utilisateur, mais la sync Data Layer reste encore globale

### Design system

- Un design system a été rédigé dans [DESIGN_SYSTEM.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/DESIGN_SYSTEM.md)
- Une première passe d’application a déjà été faite sur les écrans principaux
- La direction actuelle évolue pour se rapprocher davantage du rendu visuel de l’app officielle Dexcom :
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

- pas de widget téléphone dans l’APK

### Splash avec image complexe

Plusieurs variantes de splash avec image montre ont été testées.

Décision actuelle :

- éviter les doubles visuels au lancement
- rester sur un démarrage simple et rapide

## 4. Points ouverts / sensibles

### Ressemblance visuelle avec l’app Dexcom

Nouvelle direction validée :

- l’utilisateur veut rapprocher l’interface du rendu de l’app officielle Dexcom
- il faut viser une app plus plate, plus clinique, plus sobre, moins “compagnon premium”
- priorité visuelle :
  - écran montre
  - accueil
  - connexion Dexcom
  - notice
  - écrans juridiques

### Image réelle du modèle de montre

Décision produit / technique :

- au lieu d’afficher une image générique, l’app doit afficher l’image du modèle de montre connecté
- Android / Wear OS ne fournit pas directement une photo officielle exploitable
- la solution retenue est :
  1. faire remonter par la montre ses informations réelles (`manufacturer`, `model`, éventuellement `device`)
  2. mapper ces informations côté téléphone vers un catalogue local d’images
  3. afficher une montre générique uniquement si le modèle n’est pas reconnu

Important :

- la stratégie retenue est un `mapping local par modèle`
- pas de récupération d’image depuis le système
- pas de dépendance web
- fallback générique si modèle inconnu

### Multi-montres : limite actuelle

La gestion multi-montres est meilleure qu’avant, mais incomplète techniquement.

État actuel :

- l’utilisateur peut choisir une montre principale
- l’UI suit ce choix
- le test de liaison suit ce choix

Point non encore résolu :

- la sync Wear n’est pas encore strictement ciblée sur cette montre principale

### Vérification sécurité / risques

Rien de critique n’a été repéré dans la partie locale au dernier passage, mais il reste deux risques produit / techniques :

- l’UI peut laisser croire que la montre principale est aussi la montre réellement ciblée par la sync, ce qui n’est pas encore strictement vrai
- le bouton de test montre agrège plusieurs causes d’échec possibles :
  - Dexcom
  - réseau
  - Bluetooth
  - montre

## 5. Dernier step atteint

Le dernier step effectivement atteint est :

- refonte poussée de l’accueil pour en faire un `hero screen` centré sur la montre
- la montre prend désormais l’essentiel du premier écran
- `DEXCOM` et `AUTORISATIONS` sont relégués en dessous comme modules secondaires
- l’ombre sous la montre a été supprimée
- le libellé du haut `Montre Google connectée` a été supprimé
- un asset de montre photo avec fond réellement transparent a été préparé et branché pour les `Pixel Watch`

Le point de reprise produit juste après ce checkpoint est :

- poursuivre la montée en gamme de l’accueil
- garder la montre comme élément dominant
- mieux organiser les menus autour d’elle au lieu de simples cartes empilées

## 6. Reprise conseillée

Si on reprend le projet maintenant, l’ordre conseillé est :

1. finaliser la composition de l’accueil autour de la montre
2. rendre les menus `DEXCOM` et `AUTORISATIONS` plus intégrés au hero
3. ajuster précisément la taille et la position de la montre
4. décider si le texte `Connectée` et `Google Pixel Watch 2` doit rester centré sous la montre ou se répartir autour d’elle
5. seulement ensuite réharmoniser les autres écrans pour suivre ce nouveau niveau visuel

### Direction visuelle actuelle pour l’accueil

La bonne intention validée est :

- une vraie grosse montre qui prend toute la place possible
- le menu tourne autour d’elle
- la montre ne doit pas avoir un rendu brouillon
- il faut penser l’écran comme une composition héro, pas comme une simple liste de cards

### Prochain cran visuel recommandé

Le prochain vrai chantier visuel n’est plus un simple changement de taille.

Il faut choisir entre :

1. une composition `centrée classique`
   - montre géante au centre
   - statut et modèle dessous
   - modules utilitaires en bas

2. une composition `menus autour de la montre`
   - montre encore plus dominante
   - informations et actions réparties autour de l’objet
   - logique plus design, moins “stack Android”

La demande actuelle de l’utilisateur pousse clairement vers l’option 2.

## 7. Fichiers clés à relire en priorité

- [MainActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/MainActivity.kt)
- [activity_main.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_main.xml)
- [WatchVisualResolver.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/watch/WatchVisualResolver.kt)
- [watch_photo_transparent.png](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/drawable-nodpi/watch_photo_transparent.png)
- [WatchSetupActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/ui/WatchSetupActivity.kt)
- [activity_watch_setup.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_watch_setup.xml)
- [WatchConnectionRepository.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/watch/WatchConnectionRepository.kt)
- [DESIGN_SYSTEM.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/DESIGN_SYSTEM.md)
- [PhoneGlucoseSyncEngine.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneGlucoseSyncEngine.kt)
- [PhoneSyncStateStore.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneSyncStateStore.kt)
- [PhoneWearSyncService.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearSyncService.kt)
- [GlucoseCache.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt)
- [GlucoseComplicationService.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/wear/src/main/java/com/widgetg7/wear/complication/GlucoseComplicationService.kt)
- [notice_utilisateur.txt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/raw/notice_utilisateur.txt)
