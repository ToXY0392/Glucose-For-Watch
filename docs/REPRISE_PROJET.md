<h1 align="center">🧭 Reprise Projet</h1>

<p align="center">
  Où on en est · ce qui est décidé · quoi vérifier ensuite
</p>

---

## 🟢 État Actuel

```text
╭─ Widget G7 ────────────────────────────╮
│ Mobile      : Pixel 8a testé           │
│ Wear        : Pixel Watch 2 testée     │
│ Sync        : Dexcom Share -> Wear OS  │
│ Service     : foreground actif         │
│ Direct G7   : expérimental, non codé   │
╰────────────────────────────────────────╯
```

| Sujet | État |
| --- | --- |
| Modules | `mobile`, `wear` |
| Mode principal | `Dexcom Share -> téléphone -> Wear OS` |
| Sync active | Service foreground, ack montre, repush borné |
| Multi-montres | Montre principale ciblée avec `targetNodeId` |
| Direct capteur | Documenté, hors app principale |

---

## 🔁 Décisions À Garder

### Sync Standard

```text
Dexcom Share -> Widget G7 mobile -> Widget G7 Wear
```

| Mécanisme | Rôle |
| --- | --- |
| Service foreground | Maintient la surveillance |
| Polling `45 s` | Rapproche la sync du rythme G7 |
| `sequenceId` | Trace chaque push |
| Ack montre | Confirme la livraison |
| Repush borné | Répare une livraison non confirmée |
| `AlarmManager` / `WorkManager` | Filet de secours |

La montre reste un affichage et un déclencheur de refresh. Elle ne collecte pas directement le capteur.

### Direct Capteur

| Point | Décision |
| --- | --- |
| Support officiel | Direct to Watch documenté pour Apple Watch |
| Wear OS | Aucun support officiel équivalent trouvé |
| Piste technique | `Wear Collector` expérimental |
| Règle | Pas d'intégration sans spike BLE concluant |

Docs utiles :

- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md)
- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md)
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md)
- [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md)

---

## 🧩 Fonctionnel En Place

| Bloc | Détail |
| --- | --- |
| Dexcom | Connexion, région, stockage local des identifiants |
| Juridique | Acceptation avant connexion Dexcom |
| Montre | Test d'envoi, choix montre principale, sync en veille |
| Wear | App, tile, complication |
| Sync | Ack, repush, cache Wear, logs sensibles retirés |

---

## 🗂️ Fichiers Importants

| Fichier | Rôle |
| --- | --- |
| [ActiveGlucoseSyncService.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/ActiveGlucoseSyncService.kt) | Service foreground |
| [PhoneGlucoseSyncEngine.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneGlucoseSyncEngine.kt) | Moteur de sync |
| [PhoneSyncStateStore.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneSyncStateStore.kt) | État local téléphone |
| [PhoneWearSyncService.kt](../mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearSyncService.kt) | Push vers Wear |
| [WatchConnectionRepository.kt](../mobile/src/main/java/com/widgetg7/mobile/watch/WatchConnectionRepository.kt) | Montre principale |
| [WearDataLayerListenerService.kt](../wear/src/main/java/com/widgetg7/wear/services/WearDataLayerListenerService.kt) | Réception Wear |
| [GlucoseCache.kt](../wear/src/main/java/com/widgetg7/wear/data/GlucoseCache.kt) | Cache local Wear |

---

## ✅ Points Ouverts

| Priorité | Travail |
| --- | --- |
| 1 | Valider la sync active en veille longue |
| 2 | Vérifier l'exemption batterie sur appareils réels |
| 3 | Tester avec deux montres connectées |
| 4 | Surveiller que le repush reste borné |
| 5 | Documenter les retards possibles de Dexcom Share |
| 6 | Compléter les champs juridiques avant diffusion |

---

## ⚙️ Commandes Utiles

> PowerShell local

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:JAVA_TOOL_OPTIONS='-Duser.home="C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7"'
```

> Builds

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat :mobile:assembleRelease :wear:assembleRelease
```

> ADB

```powershell
$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe devices
```

---

## 🚦 Reprise Conseillée

```text
╭─ Prochaine session ────────────────────╮
│  > build debug                         │
│  > installer mobile + wear             │
│  > vérifier foreground service         │
│  > tester ack montre                   │
│  > laisser tourner en veille longue    │
│  > tester multi-montres                │
╰────────────────────────────────────────╯
```
