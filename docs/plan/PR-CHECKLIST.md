# Checklist PR — Glucose For Watch

Copier/coller dans la description de chaque PR.

---

## Métadonnées

- **Bloc :** X / A / M / B / C / D / F0–F5 / S
- **PR # :**
- **Gate cible :** G-X / G-A / … / G-M7 / G-M8
- **Touch sync ?** oui / non  
  _(oui si `mobile/sync`, `wear/`, `feature/sync`, `ActiveGlucoseSyncService`)_

---

## Avant merge

### Automatisé

- [ ] `bash scripts/dev/verify_ci.sh` — **PASS**
- [ ] `.\scripts\qa\stability-gate.ps1` — **PASS** (ou `-Strict` si hardware)
- [ ] `./gradlew test` — vert sur modules touchés

### Hardware (si touch mobile/wear ou gate bloc)

- [ ] `.\gradlew.bat installWidgetG7Debug` — OK phone + watch
- [ ] `.\scripts\qa\hardware-smoke.ps1` — pas de FAIL critique
- [ ] Push seq / Ack seq notés : _____ / _____
- [ ] 0 nouveau `FATAL` logcat `com.widgetg7.mobile`

### Qualité code

- [ ] Scope limité au bloc (pas de refactor gratuit)
- [ ] Pas de credentials · pas de glucose réel dans commits/captures
- [ ] Strings user-facing dans `strings.xml` (si UI)
- [ ] LF cohérent · pas de gros diff unrelated

---

## Gate bloc (cocher si applicable)

- [ ] **G-X** — X.5–X.7 · 30 min sans crash
- [ ] **G-A** — déconnexion entry=settings · notifs · sync feedback
- [ ] **G-M** — 6 previews · parité hero/tuile · smoke S1–S3
- [ ] **G-B** — complication 30 min · tuile FR
- [ ] **G-C** — sign-off stability doc
- [ ] **G-D** — Dexcom tests · install script
- [ ] **G-F*** — phase Compose · sync non régressive

---

## Après merge

- [ ] Mettre à jour [PROGRESS.md](PROGRESS.md) statuts
- [ ] Gate scoreboard [PROGRESS.md](PROGRESS.md#scoreboard-maj-hebdo)
- [ ] Incident ? → `docs/qa/incidents/YYYY-MM-DD-*.md`

---

## Rollback (si problème post-merge)

1. `git revert <commit>`
2. `installWidgetG7Debug`
3. `stability-gate.ps1 -Strict`
4. Fiche incident + retest **G-X** minimum si sync
