## Description

<!-- What does this PR change and why? Link: Closes #N or Refs #N -->

## Type

- [ ] Bug fix
- [ ] Feature
- [ ] Refactor / tech debt
- [ ] Docs / UX kit only
- [ ] QA evidence / scripts only

## Plan metadata

| Field | Value |
|-------|-------|
| **Bloc** | S / X / A / M / B / C / D / F0–F5 |
| **Target gate** | G-X / G-A / … / G-M7 / G-M8 |
| **Touches sync?** | yes / no |
| **Branch** | `type/bloc-id-slug` → `develop/integration` |

> Full checklist: [docs/plan/PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md)

## Tests performed

### Automated

- [ ] `bash scripts/dev/verify_ci.sh` — PASS
- [ ] `./gradlew test` — modules touched
- [ ] `./gradlew.bat :mobile:assembleDebug :wear:assembleDebug`

### Mobile

- [ ] Phone UI / navigation OK
- [ ] Dexcom connect / disconnect (if touched)
- [ ] Notifications / FGS (if touched)

### Wear OS

- [ ] Tile renders without flicker
- [ ] Complication updates (if touched)
- [ ] Forced sync phone → watch OK

### Hardware (if sync / mobile / wear)

- [ ] `.\gradlew.bat installGlucoseForWatchDebug` — phone + watch OK
- [ ] Push seq / Ack seq: _____ / _____
- [ ] 0 new `FATAL` logcat `com.glucoseforwatch.mobile`

## Screenshots / captures (if UI)

<!-- Attach phone / tile / complication captures — no real glucose / PII -->

- [ ] N/A
- [ ] Attached below / in `docs/qa/captures/`

## AGP / ToXY (if UI)

- [ ] Glucose values use AGP colors only (`GlucoseRangeResolver` / `agp_*`)
- [ ] ToXY mint accent for chrome only
- [ ] Tokens updated in `toxy-ux-kit/` when colors change

## After merge

- [ ] Update [docs/plan/PROGRESS.md](docs/plan/PROGRESS.md) if gate/scoreboard impacted
- [ ] Close / update linked GitHub Project card
