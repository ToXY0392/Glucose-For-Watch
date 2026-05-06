<h1 align="center">Plan Migration Nouvelle Version APK</h1>

<p align="center">
  Évolution produit · même moteur · aucune régression CGU, Dexcom, sync
</p>

---

## Objectif

Mettre en place une nouvelle version produit sans changer le fonctionnement vital actuel.

```text
Le mobile reste un assistant clair.
La montre reste la surface principale de lecture.
Le moteur Dexcom / sync reste inchangé.
```

---

## Règle De Sécurité

```text
Les changements d’interface ne modifient pas le parcours CGU, Dexcom, sync active, ack montre.
Toute modification du moteur de sync doit être traitée séparément.
```

Cette règle est bloquante.

---

## Ce Qui Ne Doit Pas Changer

| Bloc | À préserver |
| --- | --- |
| CGU | Acceptation obligatoire avant Dexcom |
| Avertissement médical | Toujours visible dans le parcours légal |
| Connexion Dexcom | Identifiants, région, session |
| Stockage paramètres | `AppSettingsStore`, consentements, préférences |
| Sync active | `ActiveGlucoseSyncService` |
| Moteur sync | `PhoneGlucoseSyncEngine` |
| Push Wear | `PhoneWearSyncService` |
| Ack montre | `/glucose/watch/ack` |
| Refresh montre | `/glucose/refresh/request` |
| Dernière valeur Wear | `/glucose/latest` |
| Cache Wear | `GlucoseCache` |
| Foreground service | Notification active |

---

## Ce Qui Peut Changer

| Surface | Changement autorisé |
| --- | --- |
| Écran Montre (mobile) | Réorganiser l’assistant `Installer Wear / Sync / Ack montre` |
| Texte mobile | Clarifier le rôle assistant |
| Documentation | Aligner sur l’état du code |

---

## Architecture De Migration

```text
Couche UI
  peut changer

Couche sync
  ne change pas

Couche Dexcom
  ne change pas

Couche juridique
  ne change pas
```

---

## Lot 0 - Gel Fonctionnel

Objectif : définir les garde-fous avant de coder.

À vérifier :

| Vérification | Attendu |
| --- | --- |
| App démarre | Splash puis parcours existant |
| CGU | Toujours demandées avant Dexcom |
| Dexcom | Connexion existante inchangée |
| Sync | Service foreground actif |
| Wear | Push `/glucose/latest` fonctionne |
| Ack | `/glucose/watch/ack` reçu |

Sortie :

```text
Liste de tests manuels de non-régression validée.
```

---

## Lot 1 - Écran Mobile Assistant

Objectif : faire évoluer l’écran montre sans changer la sync.

Écran cible :

```text
Widget G7

Installer Wear
Sync
Ack montre
```

À modifier :

| Fichier | Travail |
| --- | --- |
| `activity_watch_setup.xml` | Recomposer les cartes |
| `WatchSetupActivity.kt` | Garder logique existante, adapter l’affichage |
| styles / drawables | Ajuster la présentation sans toucher au métier |

Ne pas modifier :

```text
PhoneWearSyncService
PhoneGlucoseSyncEngine
ActiveGlucoseSyncService
AppSettingsStore
LegalConsentStore
```

Critère :

```text
Le bouton de test utilise encore le push existant.
Le statut utilise encore la détection montre existante.
```

---

## Lot 2 - Ping App Wear Installée

Objectif : préparer l’installation distante sans encore la coder.

À faire :

| Côté | Travail |
| --- | --- |
| Mobile | Ajouter `WatchStatusVerifier` |
| Wear | Répondre à `/watch/status/request` |
| UI | Afficher `App Wear prête / absente` |

Critère :

```text
Le mobile sait distinguer :
- montre connectée ;
- app Wear installée ;
- app Wear non confirmée.
```

---

## Lot 3 - Installation Wear (parcours utilisateur actuel)

Objectif : évolutions **après** stabilisation UI / sync (priorité Lots 0–2).

Référence fonctionnelle : assistant ADB depuis le mobile décrit dans [technical-wear-os-sync.md](technical-wear-os-sync.md).

Règle :

```text
Ne pas commencer ce lot tant que les lots 0 à 2 ne sont pas stables.
```

---

## Tests De Non-Régression

### Juridique

| Test | Attendu |
| --- | --- |
| Premier lancement | CGU / avertissement visibles |
| Refus | Connexion Dexcom bloquée |
| Acceptation | Connexion Dexcom disponible |

### Dexcom

| Test | Attendu |
| --- | --- |
| Identifiants valides | Session créée |
| Erreur réseau | Message clair |
| Identifiants invalides | Message clair |

### Sync

| Test | Attendu |
| --- | --- |
| Sync active | Notification foreground |
| Valeur reçue | Push Wear |
| Ack reçu | Statut montre vérifié |
| Montre absente | Message non bloquant |
| Donnée ancienne | État visible |

### Wear

| Test | Attendu |
| --- | --- |
| Tile ajoutée | Valeur affichée |
| Sync manuel tile | Refresh demandé |
| Complication ajoutée | Valeur affichée |
| Redémarrage montre | Cache puis nouvelle sync |

---

## Ordre Recommandé

```text
Lot 0  Gel fonctionnel
Lot 1  Écran mobile assistant
Lot 2  Ping app Wear
Lot 3  Installation distante Wear
```

---

## Critère De Succès

```text
L’utilisateur voit un assistant mobile cohérent.
Le parcours CGU et Dexcom reste inchangé.
La sync existante continue de fonctionner.
La montre reste la surface centrale de lecture.
Le bouton sync manuel reste disponible sur la tile.
```
