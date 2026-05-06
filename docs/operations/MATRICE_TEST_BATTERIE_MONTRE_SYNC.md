# Matrice Test Batterie Montre - Sync

Date: 2026-05-06

## Scope

Validation du comportement refresh/sync selon:

- niveau batterie montre: `25%`, `20%`, `15%`, `10%`
- etat chargeur: `non`, `oui`

## Regle attendue

- Mode degrade **actif** si `batterie <= 20%` **et** montre **non chargee**.
- Mode degrade **inactif** si montre chargee (meme sous 20%), sauf si `syncLimited=true`.

## Cas de test

1. `25%` + `non chargee` -> mode degrade: `non`
2. `25%` + `chargee` -> mode degrade: `non`
3. `20%` + `non chargee` -> mode degrade: `oui`
4. `20%` + `chargee` -> mode degrade: `non`
5. `15%` + `non chargee` -> mode degrade: `oui`
6. `15%` + `chargee` -> mode degrade: `non`
7. `10%` + `non chargee` -> mode degrade: `oui`
8. `10%` + `chargee` -> mode degrade: `non`
9. `25%` + `chargee` + `syncLimited=true` -> mode degrade: `oui`

## Couverture implémentée

- Test unitaire: `mobile/src/test/java/com/widgetg7/mobile/sync/WatchBatteryPolicyTest.kt`
- Policy runtime: `mobile/src/main/java/com/widgetg7/mobile/sync/WatchBatteryPolicy.kt`
