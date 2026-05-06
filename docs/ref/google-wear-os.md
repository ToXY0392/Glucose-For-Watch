# Wear OS et liaison téléphone / montre

**Id :** `google-wear-os`  
Wear OS, Play services Wearable, AndroidX Wear (`wear/build.gradle.kts` et `wear-remote-interactions`, `play-services-wearable` sur le mobile).

## Guides

| Sujet | URL |
| --- | --- |
| Formation Wear OS | https://developer.android.com/training/wearables |
| Data Layer API | https://developer.android.com/training/wearables/data/events |
| Principes UI | https://developer.android.com/design/ui/wear |
| Tuiles | https://developer.android.com/training/wearables/tiles |
| Complications | https://developer.android.com/training/wearables/user-interface/complications |
| Référence Wearable (`com.google.android.gms.wearable`) | https://developers.google.com/android/reference/com/google/android/gms/wearable/package-summary |

## Dépendances et liens par artefact

[dependency-registry.md](dependency-registry.md) (sections `:mobile` / `:wear`) — [dependency-catalog.yaml](dependency-catalog.yaml).

## Code interne

| Sujet | Emplacement |
| --- | --- |
| Côté téléphone (noeuds, Data Client) | `mobile/.../watch/` |
| Réception et chemins `/glucose/*` | `wear/.../WearDataLayerListenerService.kt` |
| Schéma des chemins | `docs/technical-wear-os-sync.md` |
