<h1 align="center">Plan Migration Nouvelle Version APK</h1>

<p align="center">
  Nouveau design · même moteur · aucune régression CGU, Dexcom, sync
</p>

---

## Objectif

Mettre en place la nouvelle version produit sans changer le fonctionnement vital actuel.

```text
Le mobile devient un assistant clair.
La montre devient la surface principale.
Le moteur Dexcom / sync reste inchangé.
```

---

## Règle De Sécurité

```text
La refonte UI ne modifie pas le parcours CGU, Dexcom, sync active, ack montre.
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
| Logo | Remplacer par logo officiel goutte G7 |
| Écran Montre | Refaire en assistant `Installer Wear / Sync / Ack montre` |
| Texte mobile | Clarifier rôle assistant |
| Tile | Nouveau design sombre, valeur centrale, petit bouton sync |
| Complication | Style plus compact, sans logo |
| App Wear | Style sombre aligné avec présentation |
| Documentation | Aligner sur nouvelle version |

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

## Lot 1 - Logo Officiel

Objectif : intégrer le nouveau logo sans toucher au métier.

À faire :

| Fichier / zone | Travail |
| --- | --- |
| `docs/LOGO_WIDGET_G7.md` | Source de vérité déjà créée |
| Assets mobile | Ajouter logo officiel |
| Splash / header | Remplacer ancien visuel |
| README / doc | Garder cohérence |

Critère :

```text
Le logo affiché est la goutte G7 officielle.
La tile et la complication n'affichent pas le logo.
```

---

## Lot 2 - Écran Mobile Assistant

Objectif : refaire l'écran montre sans changer la sync.

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
| `WatchSetupActivity.kt` | Garder logique existante, changer affichage |
| styles/drawables | Ajouter cartes propres sans bulles vertes |

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

## Lot 3 - Ping App Wear Installée

Objectif : préparer l'installation distante sans encore la coder.

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

## Lot 4 - Tile Nouveau Design

Objectif : aligner la tile avec le visuel cible.

À modifier :

```text
wear/src/main/java/com/widgetg7/wear/tile/GlucoseTileService.kt
```

Design :

| Élément | Règle |
| --- | --- |
| Fond | vert sombre |
| Valeur | centrale, très grande |
| Unité | sous valeur |
| Anneau | arc vert autour si possible |
| Sync manuel | petit bouton icône, secondaire |
| Logo | interdit |

Ne pas modifier :

```text
/glucose/latest
/glucose/refresh/request
GlucoseCache
```

Critère :

```text
La tile lit les mêmes données qu'avant.
Le bouton sync déclenche le même refresh qu'avant.
```

---

## Lot 5 - Complication Nouveau Design

Objectif : rendre la complication plus sobre, sans logo.

À modifier :

```text
wear/src/main/java/com/widgetg7/wear/complication/GlucoseComplicationService.kt
```

Règles :

| Type | Affichage |
| --- | --- |
| Short text | valeur + unité courte |
| Long text | valeur + unité + fraîcheur courte |
| Ranged value | valeur prioritaire |

Critère :

```text
La complication garde les mêmes types supportés.
Elle n'affiche pas le logo.
Elle reste lisible sur cadran sombre et clair.
```

---

## Lot 6 - App Wear Design

Objectif : aligner l'app Wear si elle est ouverte directement.

Design :

```text
fond sombre
valeur centrale
unité
tendance
fraîcheur
petit bouton sync
```

Critère :

```text
L'app Wear est cohérente avec la tile.
La valeur reste prioritaire.
```

---

## Lot 7 - Installation Distante Wear

Objectif : seulement après stabilisation UI/sync.

Source :

- [PLAN_INSTALLATION_DISTANTE_WEAR.md](PLAN_INSTALLATION_DISTANTE_WEAR.md)

Règle :

```text
Ne pas commencer ce lot tant que les lots 0 à 5 ne sont pas stables.
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
Lot 1  Logo
Lot 2  Écran mobile assistant
Lot 3  Ping app Wear
Lot 4  Tile nouveau design
Lot 5  Complication nouveau design
Lot 6  App Wear design
Lot 7  Installation distante Wear
```

---

## Critère De Succès

```text
L'utilisateur voit une app mobile plus claire.
Le parcours CGU et Dexcom reste inchangé.
La sync existante continue de fonctionner.
La montre devient la surface centrale.
La tile et la complication reprennent le nouveau design.
Le bouton sync manuel reste disponible sur la tile.
```
