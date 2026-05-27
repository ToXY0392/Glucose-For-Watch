# PR checklist — Glucose For Watch

Copy/paste into every PR description.

---

## Metadata

- **Bloc:** X / A / M / B / C / D / F0–F5 / S
- **PR #:**
- **Target gate:** G-X / G-A / … / G-M7 / G-M8
- **Touches sync?** yes / no  
  _(yes if `mobile/sync`, `wear/`, `feature/sync`, `ActiveGlucoseSyncService`)_

---

## Before merge

### Automated

- [ ] `bash scripts/dev/verify_ci.sh` — **PASS**
- [ ] `.\scripts\qa\stability-gate.ps1` — **PASS** (or `-Strict` if hardware)
- [ ] `./gradlew test` — green on touched modules

### Hardware (if mobile/wear touched or bloc gate)

- [ ] `.\gradlew.bat installGlucoseForWatchDebug` — OK phone + watch
- [ ] `.\scripts\qa\hardware-smoke.ps1` — no critical FAIL
- [ ] Push seq / Ack seq noted: _____ / _____
- [ ] 0 new `FATAL` logcat `com.glucoseforwatch.mobile`

### Code quality

- [ ] Scope limited to bloc (no gratuitous refactor)
- [ ] No credentials · no real glucose in commits/screenshots
- [ ] User-facing strings in `strings.xml` (if UI)
- [ ] Consistent LF · no large unrelated diff

---

## Gate bloc (check if applicable)

- [ ] **G-X** — X.5–X.7 · 30 min no crash
- [ ] **G-A** — disconnect entry=settings · notifications · sync feedback
- [ ] **G-M** — 6 previews · hero/tile parity · smoke S1–S3
- [ ] **G-B** — complication 30 min · tile FR
- [ ] **G-C** — stability sign-off doc
- [ ] **G-D** — Dexcom tests · install script
- [ ] **G-F*** — Compose phase · sync non-regressive

---

## After merge

- [ ] Update [PROGRESS.md](PROGRESS.md) statuses
- [ ] Gate scoreboard [PROGRESS.md](PROGRESS.md#scoreboard-weekly-update)
- [ ] Incident? → `docs/qa/incidents/YYYY-MM-DD-*.md`

---

## Rollback (if post-merge issue)

1. `git revert <commit>`
2. `installGlucoseForWatchDebug`
3. `stability-gate.ps1 -Strict`
4. Incident report + retest minimum **G-X** if sync
