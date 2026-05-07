# Technique sync Wear OS

## Flux

1. Le téléphone lit Dexcom Share.
2. Le téléphone pousse la donnée vers la montre.
3. La montre met à jour cache + tuile + complication.
4. La montre envoie un ack.

## Composants clés

- `PhoneGlucoseSyncEngine`
- `PhoneWearSyncService`
- `WearDataLayerListenerService`
- `GlucoseSimpleTileService`

## Vérification

- Build OK
- Ack montre reçu
- Cache montre et téléphone alignés
- `stale=false` quand la donnée est fraîche

## Commande debug

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```
