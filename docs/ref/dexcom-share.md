# Dexcom Share et écosystème Dexcom

**Id :** `dexcom-share`  
**Usage dans le repo:** glucose via `DexcomSharePhoneGlucoseSource` (`mobile/.../dexcom/`), hôtes `share2.dexcom.com` / `shareous1.dexcom.com`, chemins `ShareWebServices/...`.

## 1. Protocole Share HTTP (celui implémenté dans Widget G7)

| Sujet | Lien ou note |
| --- | --- |
| Documentation **officielle** des endpoints `ShareWebServices` | **Non publiée** par Dexcom comme API produit pour tiers. Le comportement est celui attendu par l’écosystème « Dexcom Share » ; toute évolution côté serveur peut impacter l’app sans préavis documenté. |
| API **officielle** alternative (OAuth, REST v3) | [Dexcom Developer — accueil](https://developer.dexcom.com/) |
| Aperçu endpoints v3 | [Endpoint overview (v3)](https://developer.dexcom.com/docs/dexcomv3/endpoint-overview) |
| Authentification v3 | [Authentication](https://developer.dexcom.com/docs/dexcom/authentication/) |

> Widget G7 **n’utilise pas** aujourd’hui l’API OAuth v3 ci-dessus ; les liens servent de référence **officielle** pour comparaison ou évolution future.

## 2. Documentation produit Dexcom (contexte G7 / montre)

| Sujet | Lien |
| --- | --- |
| Direct to Watch (FAQ) | https://www.dexcom.com/en-us/faqs/what-is-direct-to-watch |
| Compatibilité G7 | https://www.dexcom.com/compatibility/g7 |
| Direct to Watch (professionnels de santé) | https://provider.dexcom.com/what-direct-watch |
| Share et montre (professionnels de santé) | https://provider.dexcom.com/share-available-my-patients-watch-when-direct-watch-mode |

## 3. Points d’accès HTTP (implémentation actuelle)

Constantes **non secrètes** ; le détail des corps JSON et en-têtes est dans le code source.

| Rôle | Méthode / chemin (relatif à `baseUrl()`) |
| --- | --- |
| Authentification compte Publisher | POST `ShareWebServices/Services/General/AuthenticatePublisherAccount` |
| Login par identifiant | POST `ShareWebServices/Services/General/LoginPublisherAccountById` |
| Lecture dernières valeurs glucose | POST `ShareWebServices/Services/Publisher/ReadPublisherLatestGlucoseValues` |

`baseUrl()` : voir `DexcomShareConfig.baseUrl()` — `share2.dexcom.com` (US) ou `shareous1.dexcom.com` (OUS).

## 4. Code interne à croiser

| Rôle | Fichier (relatif au dépôt) |
| --- | --- |
| Client Share, session, erreurs | `mobile/src/main/java/com/widgetg7/mobile/dexcom/DexcomSharePhoneGlucoseSource.kt` |
| Configuration (serveur US/OUS, application id) | `mobile/src/main/java/com/widgetg7/mobile/dexcom/DexcomShareConfig.kt` |
| Synthèse architecture et sources Dexcom (doc projet) | `docs/technical-wear-os-sync.md` |
| Registre machine (entrée `dexcom-share-http`) | `docs/ref/dependency-catalog.yaml` |
