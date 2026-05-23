## Summary

<!-- What does this PR change and why? -->

## Type

- [ ] Bug fix
- [ ] Feature
- [ ] Refactor / tech debt
- [ ] Docs / UX kit only

## Test plan

- [ ] `./gradlew test`
- [ ] `./gradlew :mobile:assembleDebug :wear:assembleDebug`
- [ ] Phone + watch manual check (if sync/UI/watch surfaces changed)

## AGP / ToXY checklist (if UI touched)

- [ ] Glucose values use AGP colors only (`GlucoseRangeResolver` / `agp_*`)
- [ ] ToXY mint accent used for chrome only (buttons, sync UI, backgrounds)
- [ ] Design tokens updated in `toxy-ux-kit/` when colors change

## Dexcom / hardware (if applicable)

- [ ] Tested with G6 and/or G7 Share
- [ ] Offline → reconnect scenario checked
