# Cursor skills automation

Ce document reference les skills Cursor projet pour la veille technique, la maintenance documentaire et l'automatisation recurrente.

## Emplacement

Les skills sont stockes dans `.cursor/skills/`.

## Skills disponibles

### Demarrage de session Cursor

- `widget-g7-session-start-quick-check`: check delta rapide (vendor + securite) a l'ouverture.
- `widget-g7-session-start-deferred-maintenance`: maintenance complete differree apres ouverture.
- `widget-g7-run-coordinator`: verrouillage, cooldown, deduplication et orchestration des runs.
- `widget-g7-usb-detach-handoff-writer`: detection detach USB et ecriture d'incident dans `docs/developer-handoff.md`.

### Veille et maintenance continue

- `widget-g7-vendor-watch`: veille Android/Wear/AGP/Gradle/Kotlin/Dexcom, classement par impact.
- `widget-g7-security-bulletin`: suivi CVE/avis securite et priorisation.
- `widget-g7-compat-matrix-maintainer`: maintenance de `COMPATIBILITY.md`.
- `widget-g7-dependency-advisor`: strategie d'upgrade sure + plan en petits PRs.
- `widget-g7-doc-drift-checker`: detection obsolescence doc + patch priorise, application si edition autorisee.
- `widget-g7-release-notes-curator`: alimentation de `docs/release-notes.md` avec les changements upstream pertinents.

## Cadence recommandee

- Toutes les 6h: `widget-g7-session-start-quick-check` (ou equivalent quick pack).
- Quotidien: `widget-g7-vendor-watch` + `widget-g7-security-bulletin` (mode delta).
- 2 fois/semaine: `widget-g7-compat-matrix-maintainer`.
- Hebdomadaire: `widget-g7-dependency-advisor` + `widget-g7-doc-drift-checker`.
- Avant release: `widget-g7-release-notes-curator` + verification des blocants.

## Politique d'alerte

- Critique: action immediate.
- Important: digest quotidien.
- Mineur: digest hebdomadaire.

## Notes

- Les skills utilisent `disable-model-invocation: true`: ils sont appeles explicitement.
- En mode read-only, les skills proposent des patchs sans ecrire.
- En mode edition autorisee, les skills de documentation peuvent appliquer les mises a jour.

## Hooks Cursor (ouverture projet)

Configuration projet:

- `.cursor/hooks.json`
- `.cursor/hooks/session_start_quick_check.ps1`
- `.cursor/hooks/session_start_deferred_maintenance.ps1`
- `.cursor/hooks/run_full_maintenance.ps1`
- `.cursor/hooks/usb_detach_handoff.ps1`
- `.cursor/hooks/start_usb_monitor.ps1`
- `.cursor/hooks/usb_monitor_loop.ps1`

Comportement:

- `session_start_quick_check.ps1`: lance un controle rapide au demarrage (cooldown 6h), puis ecrit un rapport dans `.cursor/state/reports/quick-check.md`.
- `session_start_deferred_maintenance.ps1`: programme une maintenance complete differree (180s) avec cooldown 24h.
- `run_full_maintenance.ps1`: produit un rapport consolide dans `.cursor/state/reports/full-maintenance.md` et applique un lock anti-doublon.
- `usb_detach_handoff.ps1`: detecte la deconnexion USB phone/watch via ADB et ajoute un incident dans `docs/developer-handoff.md` (dedupe 30 min).
- `start_usb_monitor.ps1`: demarre un monitor USB en arriere-plan au debut de session (avec anti-doublon PID).
- `usb_monitor_loop.ps1`: execute `usb_detach_handoff.ps1` toutes les 5 minutes pendant la session.

Etat local:

- `.cursor/state/run_state.json` conserve les timestamps de derniers runs quick/full.
- `.cursor/state/usb-state.json` conserve le dernier etat USB observe et les timestamps de dedupe.
- `.cursor/state/usb-monitor.pid` identifie le monitor USB en cours.
- `.cursor/state/reports/usb-monitor-loop.log` journalise les executions periodiques.
