# Backlog automatisation — Glucose For Watch

> Tâches **transverses** (bloc S) pour doc, UI preview, CI et GitHub Project.  
> Équivalent **Storybook** sur Android : `@Preview` + export PNG (phase XML) → **Showkase** (phase Compose v0.6).

---

## État actuel (déjà en place)

| Outil | Rôle | Chemin |
|-------|------|--------|
| **AppPreviewExporterTest** | 6 états Home → PNG | `mobile/.../preview/AppPreviewExporterTest.kt` |
| **export-app-preview.ps1** | Lance export sans hardware | `scripts/qa/export-app-preview.ps1` |
| **Wear @Preview** | Tile / status Compose | `wear/.../WearStatusScreenPreview.kt` |
| **design-reference** | Maquettes statiques | `toxy-ux-kit/design-reference/` |
| **CI verify_ci** | Unit + compile | `.github/workflows/ci.yml` |
| **Dependabot** | Gradle + Actions | `.github/dependabot.yml` |
| **Skills Cursor** | doc-drift, pr-gatekeeper, pr-author, … | `.cursor/skills/` |

**Storybook** = écosystème web (React). Ici l’équivalent pertinent :

| Phase | Outil recommandé |
|-------|------------------|
| v0.5 (phone XML) | Robolectric PNG + galerie HTML |
| v0.6 (Compose phone) | **Showkase** + `@Preview` |
| Wear (déjà Compose) | @Preview Studio + option Showkase wear |

---

## Issues GitHub (AUTO-*)

Créées via `scripts/dev/create_automation_issues.ps1` :

| ID | Titre | Milestone | Effort |
|----|-------|-----------|--------|
| AUTO-1 | CI export PNG previews on mobile UI PR | v0.5 | 4h |
| AUTO-2 | Static preview gallery HTML | v0.5 | 6h |
| AUTO-3 | Showkase pour Compose v0.6 | v0.6 | 1j |
| AUTO-4 | CI markdown link checker docs/ | v0.5 | 2h |
| AUTO-5 | Weekly doc-drift scheduled issue | v0.5 | 3h |
| AUTO-6 | Paparazzi wear tile (optionnel) | v0.6 | 2j |
| AUTO-7 | Project workflow PR → In Review | v0.5 | 1h |
| AUTO-8 | Architecture diagram export CI | v0.5 | 2h |
| AUTO-9 | PR author skill + CONTRIBUTING integration ([#21](https://github.com/ToXY0392/Glucose-For-Watch/issues/21)) | v0.5 | 2h · skill ✅ |

---

## Tâches plan v0.5 encore nécessaires (hors AUTO)

D’après [PROGRESS.md](PROGRESS.md) :

| Bloc | Reste | Gate |
|------|-------|------|
| X | X.3 repro, fermer #4 incident | G-X |
| C | C.7 sign-off, C.2/C.3/C.8 | G-C |
| A | A.1, A.3 | G-A |
| M | M.4 design-reference PNG | G-M |
| B | B.4 WatchSyncVerifier | G-B |
| D | D.6 capture-crash-log | G-D |
| S | S.6 PR checklist sync, S.8 ACTION-PLAN | transverse |

---

## Rituel automatisable (hebdo)

| Jour | Action | Outil |
|------|--------|-------|
| Lundi | Scoreboard PROGRESS + Project | skill `widget-g7-github-project-sync` |
| Lundi | Doc drift check | skill `widget-g7-doc-drift-checker` |
| Chaque PR UI | export-app-preview | AUTO-1 (CI) |
| Post-merge doc | Link check | AUTO-4 |

---

## Scripts

```powershell
# Mettre à jour colonnes Project #1
.\scripts\dev\update_github_project_board.ps1

# Créer issues AUTO (dry-run)
.\scripts\dev\create_automation_issues.ps1 -DryRun
.\scripts\dev\create_automation_issues.ps1
```

---

## Showkase vs Storybook (note v0.6)

Quand le bloc **F0** démarre :

1. `implementation("com.airbnb.android:showkase:…")` sur `:mobile`
2. Annoter écrans Compose `@ShowkaseComposable`
3. Task `./gradlew :mobile:showkaseBrowserDebug` → APK catalogue sur émulateur
4. Complète **AUTO-2** galerie HTML pour sideload doc offline

---

*MAJ : 2026-05-26*
