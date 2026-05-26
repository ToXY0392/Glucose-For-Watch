## Summary

<!-- What does this PR change and why? Link issue: Closes #N or Refs #N -->
<!-- Tip: invoke @glucose-for-watch-pr-author in Cursor for bloc/gate PRs before opening -->

## Plan metadata

| Field | Value |
|-------|-------|
| **Bloc** | S / X / A / M / B / C / D / F0–F5 |
| **Target gate** | G-X / G-A / … / G-M7 / G-M8 |
| **Touches sync?** | yes / no |
| **Branch** | `type/bloc-id-slug` → `develop/integration` |

> Full checklist: [docs/plan/PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md)

## Type

- [ ] Bug fix
- [ ] Feature
- [ ] Refactor / tech debt
- [ ] Docs / UX kit only
- [ ] QA evidence / scripts only

## Test plan

- [ ] `bash scripts/dev/verify_ci.sh` — PASS
- [ ] `./gradlew test` — modules touched
- [ ] `.\scripts\qa\stability-gate.ps1` — PASS (or `-Strict` if hardware)
- [ ] `./gradlew.bat :mobile:assembleDebug :wear:assembleDebug`

## Hardware (if sync / mobile / wear touched)

- [ ] `.\gradlew.bat installWidgetG7Debug` — phone + watch OK
- [ ] `.\scripts\qa\hardware-smoke.ps1` — no critical FAIL
- [ ] Push seq / Ack seq: _____ / _____
- [ ] 0 new `FATAL` logcat `com.widgetg7.mobile`

## AGP / ToXY (if UI touched)

- [ ] Glucose values use AGP colors only (`GlucoseRangeResolver` / `agp_*`)
- [ ] ToXY mint accent for chrome only (buttons, sync UI, backgrounds)
- [ ] Design tokens updated in `toxy-ux-kit/` when colors change

## Gate bloc (check if applicable)

- [ ] **G-X** — FGS fallback · 30 min no crash
- [ ] **G-A** — disconnect entry=settings · notifications · sync feedback
- [ ] **G-M** — 6 previews · hero/tile parity
- [ ] **G-B** — complication 30 min · tile FR
- [ ] **G-C** — stability sign-off linked
- [ ] **G-D** — Dexcom tests · install script
- [ ] **G-F*** — Compose phase · sync non-regressive

## After merge

- [ ] Update [docs/plan/PROGRESS.md](docs/plan/PROGRESS.md) scoreboard
- [ ] QA evidence in `docs/qa/` if hardware session
- [ ] Close / update linked GitHub Project card

## Dexcom / hardware (if applicable)

- [ ] Tested with G6 and/or G7 Share
- [ ] Offline → reconnect scenario checked
