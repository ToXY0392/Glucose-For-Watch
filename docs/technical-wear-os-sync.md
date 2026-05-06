# Technique — Wear OS et synchronisation

Synthèse architecture cible, chemins Data Layer, contraintes Dexcom, mode direct capteur (hors produit) et assistant d’installation Wear. Les **liens vers la documentation éditeurs** sont dans [ref/README.md](ref/README.md).

## Positionnement

- Chaîne nominale : **Dexcom Share → téléphone → Wear OS**. Le téléphone lit la glycémie et pousse vers la montre ; la montre affiche, accusé réception et peut demander un rafraîchissement.
- Le mode **capteur G7 → montre (BLE)** reste expérimental ; il ne doit pas être fusionné au parcours principal sans validation (spike, autonomie, sécurité).

## Contexte Dexcom (produit et limites)

Résumé métier : Dexcom documente surtout Direct to Watch dans l’écosystème Apple Watch ; un parcours officiel équivalent pour Wear OS tel que Widget G7 ne constitue pas un engagement Dexcom sur le CGM. Les URLs produit et l’API officielle documentée (OAuth v3) sont répertoriées dans [ref/dexcom-share.md](ref/dexcom-share.md).

| Frein probable | Impact |
| --- | --- |
| Dispositif médical | Une app officielle engage Dexcom sur la sécurité patient |
| Alertes critiques | Fiabilité attendue sur hypo / hyper / perte de signal |
| BLE direct | Appairage, cohabitation avec l’app Dexcom, complexité |
| Batterie Wear OS | Connexion ou scan prolongés |
| Données sensibles | Pas de secrets ou valeurs en logs versionnés |

| Point | Widget G7 | Dexcom officiel |
| --- | --- | --- |
| Rôle | Affichage compagnon | Système médical réglementé |
| Source | Share via téléphone | Écosystème capteur / apps Dexcom |
| Wear OS | Réception Data Layer | Non équivalent documenté pour ce besoin |
| Support | Projet de test / privé | Support patient à grande échelle |

---

## Architecture standard

### Téléphone

| Action | Détail |
| --- | --- |
| Lire Dexcom Share | Récupérer la dernière valeur disponible |
| Conserver | Garder la dernière valeur connue |
| Horodater | Distinguer mesure, récupération et push |
| Pousser | Envoyer avec `sequenceId` |
| Vérifier | Recevoir l'ack montre |
| Réparer | Repush borné si ack manquant |

### Interface téléphone (fichiers utiles)

Liens relatifs au dépôt : ouvrir depuis l’IDE depuis ce fichier.

| Écran | Code | Mise en page |
| --- | --- | --- |
| Accueil, état sync | [MainActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/MainActivity.kt) | [activity_main.xml](../mobile/src/main/res/layout/activity_main.xml) |
| Première connexion Dexcom | [DexcomEntryActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/ui/DexcomEntryActivity.kt) | [activity_dexcom_entry.xml](../mobile/src/main/res/layout/activity_dexcom_entry.xml) |
| Réglages Dexcom | [DexcomSettingsActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/ui/DexcomSettingsActivity.kt) | [activity_dexcom_settings.xml](../mobile/src/main/res/layout/activity_dexcom_settings.xml) |
| Configuration montre | [WatchSetupActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/ui/WatchSetupActivity.kt) | [activity_watch_setup.xml](../mobile/src/main/res/layout/activity_watch_setup.xml) |
| Assistant install montre (ADB) | [WearInstallerActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/ui/WearInstallerActivity.kt) | [activity_wear_installer.xml](../mobile/src/main/res/layout/activity_wear_installer.xml) |
| Notice | [NoticeActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/ui/NoticeActivity.kt) | [activity_notice.xml](../mobile/src/main/res/layout/activity_notice.xml) |
| Document légal | [LegalDocumentActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/ui/LegalDocumentActivity.kt) | [activity_legal_document.xml](../mobile/src/main/res/layout/activity_legal_document.xml) |
| Routing démarrage | [SplashActivity.kt](../mobile/src/main/java/com/widgetg7/mobile/SplashActivity.kt) | — |

### Wear OS

| Action | Détail |
| --- | --- |
| Recevoir | Lire `/glucose/latest` |
| Filtrer | Ignorer si `targetNodeId` cible une autre montre |
| Cacher | Mettre à jour le cache local |
| Rafraîchir | App, tile et complication |
| Confirmer | Renvoyer un ack |
| Alerter | Signaler une donnée ancienne |

---

## Chemins Wear Data Layer

| Chemin | Rôle |
| --- | --- |
| `/glucose/latest` | Dernière valeur |
| `/glucose/refresh/request` | Demande de refresh depuis la montre |
| `/glucose/refresh/status` | Statut de refresh |
| `/glucose/watch/ack` | Accusé de réception montre |
| `/watch/status` | État batterie / sync montre |

---

## Règles de robustesse

Ne pas effacer une valeur lisible sur erreur réseau ; conserver un `sequenceId` monotone ; borner les repush ; conserver le service foreground ; secours alarme / WorkManager ; pas de glycémie ou secret en log ; cibler la montre principale si besoin.

---

## Plan de test standard

| Cas | À vérifier |
| --- | --- |
| Téléphone online + montre connectée | Push + ack |
| Montre déconnectée puis reconnectée | Repush / cache |
| Téléphone offline | Statut d'erreur clair |
| Dexcom sans nouvelle mesure | Dernière valeur connue |
| Identifiants expirés | Demande de reconnexion |
| Redémarrage téléphone | Service relancé |
| Redémarrage montre | Cache puis réception |
| Veille longue | Sync stable |
| Deux montres | Ciblage `targetNodeId` |
| Batterie faible | Statut limité |

---

## Direct capteur (hors produit)

| Mode | Statut |
| --- | --- |
| Sync téléphone | Principal |
| Direct capteur | Expérimental, séparé, désactivable |

Le direct ne doit pas fragiliser le mode téléphone. Esquisse de composants si le sujet est relancé : `SourceRouter`, `G7BleScanner`, `G7PairingManager`, `G7CollectorService`, `G7PacketParser`, `DirectReadingRepository`, `DirectModeHealthCheck`. Contraintes : pas de secrets en clair ; pas de secret Dexcom sur Wear ; donnée horodatée ; état « ancien » visible ; retour propre vers la sync téléphone ; suppression complète des données du mode direct si abandon.

---

## Spike BLE

Objectif : constater la visibilité radio du G7 depuis la montre, sans produire de parcours utilisateur. Règles : scan court ; pas de lecture produit ; pas de stockage capteur ; pas de secret dans les logs. Critères go / stop : détection répétée, pas de perturbation de l’app Dexcom, batterie stable, résultats consignés sans donnée patient.

---

## Assistant installation montre (APK mobile)

Parcours sans Play Store : jumelage **ADB Wi‑Fi** (Kadb), installation de l’APK Wear **embarqué** dans l’APK mobile. **OCR photo** (ML Kit) pour préremplir IP / ports : toujours **contrôler visuellement** les champs (risque d’inversion selon la capture). Fichiers : `WearInstallerActivity.kt`, `WearDirectAdbInstaller.kt`, `WearEmbeddedApkRepository.kt`, `WearInstallOcr.kt`, `WearInstallOcrParser.kt` sous `mobile/.../watch/install/`.

---

## Principes projet (rappel)

Liste à jour et juridique : [index.md](index.md) (section *Principes à conserver*).
