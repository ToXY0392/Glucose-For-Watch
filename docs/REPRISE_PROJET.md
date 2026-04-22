# Reprise Projet

Dernière mise à jour : 22 avril 2026

Ce document sert de point de reprise rapide pour continuer le projet sans relire tout l'historique.

## 1. État actuel

- Projet Android `Widget G7` / `GlucoWatch`
- Dépôt propre au moment de l'écriture de ce document
- Dernier commit visible : `ed032cc` `feat: Add initial strings resource file for app name definition`
- L'APK debug compile et s'installe sur le téléphone Android connecté

## 2. Ce qui est déjà en place

### Lancement et entrée dans l'app

- Le lancement de l'app est maintenant très rapide
- Le splash n'affiche plus de visuel complexe, seulement un fond simple
- Au premier lancement, l'app passe par un écran `Connexion Dexcom`

### Connexion Dexcom

- Il existe un écran d'entrée dédié `Connexion Dexcom`
- Cet écran est prévu uniquement pour Dexcom
- Le texte d'aide est : `Utilisez vos identifiants Dexcom`
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
- Elle explique maintenant que l'écran `Connexion Dexcom` apparaît avant l'accueil

### Synchronisation téléphone / montre

- La logique a été rapprochée du rythme réel du Dexcom G7
- Le rythme auto côté téléphone a été passé de `2 min` à `5 min`
- Le téléphone compare `timestampEpochMs` de la dernière mesure
- Si la mesure n'est pas nouvelle, elle n'est pas renvoyée à la montre
- Si la mesure est nouvelle, elle est poussée immédiatement à la montre
- Le refresh manuel existe toujours

### Documents juridiques créés

Dans `docs` :

- [CGU.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/CGU.md)
- [POLITIQUE_CONFIDENTIALITE.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/POLITIQUE_CONFIDENTIALITE.md)
- [AVERTISSEMENT_MEDICAL.md](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/docs/AVERTISSEMENT_MEDICAL.md)

Attention :

- ces textes sont une base solide
- ils contiennent encore des champs `[A compléter]`
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

## 4. Point sensible encore en cours

### Écran d'accueil trop grand

Le sujet n'est pas totalement clos.

Contexte :

- l'utilisateur voulait que l'écran d'accueil tienne sans scroll
- plusieurs compactages ont été faits sur la carte montre et la section `AUTORISATIONS`
- l'utilisateur a indiqué que ça scrollait encore

Donc le prochain vrai sujet UI encore ouvert est :

- faire tenir l'écran d'accueil sans scroll sur le téléphone cible

Piste la plus probable pour la suite :

- ne plus seulement compacter visuellement
- revoir la structure de `AUTORISATIONS`
- éventuellement réduire cette section à des consignes très courtes + boutons
- ou sortir certains éléments secondaires de cet écran

## 5. Dernier step atteint

Le dernier step effectivement validé est :

- suppression complète du widget téléphone
- rebuild
- réinstallation de l'APK sur le Pixel 8a

Autrement dit, si on reprend maintenant, on repart après l'annulation du widget téléphone.

## 6. Reprise conseillée

Si on reprend le projet, l'ordre conseillé est :

1. vérifier l'écran d'accueil sur le téléphone
2. résoudre définitivement le problème de scroll
3. relire les textes visibles utilisateur avec les dernières modifs
4. seulement ensuite ouvrir un nouveau chantier produit

## 7. Fichiers clés à relire en priorité

- [MainActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/MainActivity.kt)
- [activity_main.xml](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/layout/activity_main.xml)
- [DexcomEntryActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/ui/DexcomEntryActivity.kt)
- [DexcomSettingsActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/ui/DexcomSettingsActivity.kt)
- [SplashActivity.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/SplashActivity.kt)
- [PhoneGlucoseSyncEngine.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneGlucoseSyncEngine.kt)
- [PhoneAutoSyncScheduler.kt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/java/com/widgetg7/mobile/sync/PhoneAutoSyncScheduler.kt)
- [notice_utilisateur.txt](C:/Users/Utilisateur/Desktop/THP/Projects/Widget%20G7/mobile/src/main/res/raw/notice_utilisateur.txt)

