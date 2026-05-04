<h1 align="center">Plan Installation Distante Wear</h1>

<p align="center">
  Passer de l'APK mobile seul à l'installation guidée de l'app montre
</p>

---

## Objectif

```text
Un utilisateur installe uniquement l'APK mobile.
Depuis l'app mobile, il peut installer l'app Wear à distance, sans ordinateur.
Après installation, Widget G7 vérifie la montre puis guide la tile et la complication.
```

Résultat attendu :

| Surface | Résultat |
| --- | --- |
| Mobile | Assistant d'installation montre |
| Montre | App Wear installée |
| Tile | Disponible après installation Wear |
| Complication | Disponible après installation Wear |
| Fallback | Notification montre si installation distante impossible |

---

## État Actuel

| Bloc | Statut |
| --- | --- |
| Détection montre | Déjà présent via `WatchConnectionRepository` |
| Choix montre principale | Déjà présent |
| Push valeur vers Wear | Déjà présent via `PhoneWearSyncService` |
| Ack montre | Déjà présent via `/glucose/watch/ack` |
| Tile | Déjà fournie par `GlucoseTileService` |
| Complication | Déjà fournie par `GlucoseComplicationService` |
| Ping app Wear installée | À ajouter |
| APK Wear embarqué dans mobile | À ajouter |
| Installation distante | À prototyper |
| UI installation distante | À ajouter |

---

## Ordre De Réalisation

```text
1. Vérifier l'app Wear
2. Préparer l'APK Wear embarqué
3. Construire l'écran assistant
4. Prototyper l'installation distante
5. Vérifier après installation
6. Guider tile et complication
7. Garder notification fallback
```

L'idée : ne pas commencer par le client ADB. D'abord construire tout ce qui prouve que le parcours produit tient debout.

---

## Lot 1 - Statut Wear Installé

Objectif : savoir si l'app Wear est déjà installée et répond.

### Mobile

Créer :

```text
mobile/.../watch/WatchStatusVerifier.kt
```

Rôle :

| Action | Détail |
| --- | --- |
| Envoyer ping | Message ou DataItem `/watch/status/request` |
| Attendre réponse | Timeout court |
| Exposer état | installé / absent / timeout |

### Wear

Étendre :

```text
wear/.../services/WearDataLayerListenerService.kt
```

Ajouter réponse :

```text
/watch/status
```

Payload :

| Champ | Valeur |
| --- | --- |
| `appInstalled` | `true` |
| `wearVersionName` | `0.1.0` |
| `wearVersionCode` | `1` |
| `supportsTile` | `true` |
| `supportsComplication` | `true` |
| `respondedAt` | timestamp |

Critère de validation :

```text
Depuis le mobile, le bouton Tester la montre distingue :
- montre connectée mais app Wear absente ;
- app Wear présente et vérifiée ;
- app Wear présente mais pas d'ack.
```

---

## Lot 2 - Fallback Notification Montre

Objectif : garantir un résultat utile avec un seul APK mobile.

À faire :

| Fichier | Travail |
| --- | --- |
| `NotificationHelper.kt` | Ajouter/renforcer notification glucose persistante |
| `ActiveGlucoseSyncService.kt` | Mettre à jour la notification à chaque lecture |
| `WatchSetupActivity.kt` | Ajouter état `Affichage montre via notification` |

Critère :

```text
Sans APK Wear installé, l'utilisateur voit une notification Widget G7 claire,
qui peut être relayée sur la montre.
```

---

## Lot 3 - APK Wear Embarqué

Objectif : éviter d'envoyer deux APK à l'utilisateur.

À faire :

```text
mobile/src/main/assets/wear/widget-g7-wear.apk
```

Créer :

```text
mobile/.../watch/install/WearApkAssetProvider.kt
```

Rôle :

| Action | Détail |
| --- | --- |
| Ouvrir asset | Lire l'APK Wear embarqué |
| Copier fichier | Copier vers cache interne |
| Calculer SHA-256 | Vérifier intégrité |
| Lire taille | Afficher taille |
| Exposer version attendue | `0.1.0` au départ |

Critère :

```text
L'écran mobile affiche :
App montre incluse : Widget G7 Wear 0.1.0
Taille : ...
Hash : vérifié
```

Note build :

```text
Au début, copier l'APK Wear manuellement dans assets.
Plus tard, automatiser avec une tâche Gradle.
```

---

## Lot 4 - Écran Assistant Installation

Objectif : rendre le parcours compréhensible avant d'installer quoi que ce soit.

Créer :

```text
mobile/.../ui/WearInstallerActivity.kt
mobile/src/main/res/layout/activity_wear_installer.xml
```

Ajouter depuis :

```text
WatchSetupActivity.kt
```

Écran :

```text
Installer Widget G7 sur la montre

1. Préparer la montre
2. Activer le debug Wi-Fi
3. Connecter la montre
4. Installer l'app montre
5. Vérifier
```

Champs :

| Champ | Type |
| --- | --- |
| IP | input |
| Port jumelage | input optionnel |
| Code jumelage | input optionnel |
| Port debug | input |

Actions :

| Bouton | Effet |
| --- | --- |
| `Voir les étapes` | Affiche guide debug montre |
| `Connecter` | Lance pairing/connect |
| `Installer` | Pousse APK Wear |
| `Vérifier` | Ping `/watch/status/request` |
| `Utiliser notification` | Retour au fallback |

Critère :

```text
L'utilisateur accompagné peut suivre l'écran sans lire la doc développeur.
```

---

## Lot 5 - Prototype Installation Distante

Objectif : valider la faisabilité avant de rendre l'UX complète.

Options à tester dans cet ordre :

| Option | Pourquoi |
| --- | --- |
| Installateur tiers | Plus rapide pour valider le parcours |
| Client ADB minimal intégré | Solution autonome cible |

### Option A - Installateur Tiers

But :

```text
L'app mobile prépare l'APK Wear,
puis guide vers un outil d'installation Wear existant.
```

Critère :

```text
On confirme que l'APK Wear embarqué est installable sans ordinateur.
```

### Option B - Client ADB Intégré

Créer :

```text
mobile/.../watch/install/WearRemoteInstaller.kt
mobile/.../watch/install/WearAdbInstaller.kt
```

Interface cible :

```kotlin
interface WearRemoteInstaller {
    suspend fun pair(host: String, port: Int, code: String): InstallResult
    suspend fun connect(host: String, port: Int): InstallResult
    suspend fun install(apkPath: String): InstallResult
    suspend fun disconnect(): InstallResult
}
```

Erreurs :

| Code | Sens |
| --- | --- |
| `PAIRING_REFUSED` | Code faux ou expiré |
| `CONNECTION_TIMEOUT` | Montre inaccessible |
| `INSTALL_REJECTED` | Installation refusée |
| `APK_INCOMPATIBLE` | APK Wear incompatible |
| `SIGNATURE_CONFLICT` | Signature différente |
| `VERIFY_FAILED` | Installée mais pas de réponse |

Critère :

```text
Depuis le téléphone, installer l'APK Wear sur une Pixel Watch de test,
sans ordinateur,
puis recevoir `/watch/status`.
```

---

## Lot 6 - Vérification Et Surfaces Wear

Objectif : une fois l'app installée, guider la vraie configuration montre.

À faire :

| Bloc | Travail |
| --- | --- |
| Ping Wear | Afficher version et capacités |
| Test glucose | Envoyer valeur fictive ou dernière valeur |
| Tile | Guide court d'ajout |
| Complication | Guide court d'ajout |
| Ack | Afficher `Montre vérifiée` |

Critère :

```text
Après installation distante :
1. app Wear répond ;
2. push test reçu ;
3. tile disponible ;
4. complication disponible ;
5. utilisateur sait quoi faire ensuite.
```

---

## Lot 7 - Sécurité Et Nettoyage

Objectif : ne pas laisser une porte debug ouverte sans explication.

À faire :

| Point | Travail |
| --- | --- |
| Consentement | Confirmation avant install |
| Cible | Afficher IP/port avant envoi |
| Hash | Vérifier APK avant install |
| Debug | Afficher rappel de désactivation |
| Logs | Ne pas loguer IP/code en clair |
| Échec | Fallback notification clair |

Critère :

```text
Après succès, l'app affiche :
Widget G7 Wear est installé.
Vous pouvez désactiver le debug Wi-Fi sur la montre.
```

---

## Lot 8 - Automatisation Build

Objectif : éviter de copier l'APK Wear à la main.

À faire plus tard :

```text
Gradle task:
1. build :wear:assembleDebug ou release
2. copie l'APK Wear vers mobile/src/main/assets/wear/
3. écrit metadata JSON : version, sha256, taille
4. build mobile
```

Fichier metadata :

```json
{
  "versionName": "0.1.0",
  "versionCode": 1,
  "sha256": "...",
  "fileName": "widget-g7-wear.apk"
}
```

Critère :

```text
Un build mobile contient toujours l'APK Wear correspondant.
```

---

## Priorité MVP

| Priorité | Lot | Pourquoi |
| --- | --- | --- |
| P0 | Lot 1 | Savoir si Wear est installé |
| P0 | Lot 2 | Garantir un fallback utile |
| P1 | Lot 3 | Un seul APK utilisateur |
| P1 | Lot 4 | Parcours compréhensible |
| P1 | Lot 5A | Valider installation sans ordinateur |
| P2 | Lot 5B | Autonomie complète avec client ADB |
| P2 | Lot 6 | Finaliser tile/complication guidées |
| P2 | Lot 7 | Sécurité |
| P3 | Lot 8 | Build propre |

---

## Critère De Succès Global

```text
Un utilisateur reçoit seulement l'APK mobile.
Il installe Widget G7 sur son téléphone.
Il ouvre Configurer la montre.
Il suit l'installation distante.
L'app Wear s'installe sur la montre.
Le mobile reçoit l'ack.
La tile et la complication deviennent disponibles.
Si l'installation échoue, la notification montre reste utilisable.
```

---

## Première Itération Conseillée

```text
1. Ajouter `/watch/status/request` et `/watch/status`
2. Ajouter l'état "App Wear installée / absente"
3. Ajouter le fallback notification montre dans l'écran
4. Ajouter l'écran assistant sans installation réelle
5. Copier l'APK Wear en asset
6. Tester installation via outil tiers
7. Revenir coder le client ADB intégré si le parcours vaut le coup
```

Cette itération donne vite une preuve produit sans engloutir tout le projet dans le protocole ADB.
