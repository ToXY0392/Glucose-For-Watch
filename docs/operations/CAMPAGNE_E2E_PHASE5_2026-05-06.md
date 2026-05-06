# Campagne E2E Phase 5 - Widget G7

Date: 2026-05-06

## Scope

Validation E2E finale du flux principal:
- installation,
- sync veille longue,
- multi-montres,
- reinstall,
- dry-run release.

## Preflight

```bash
bash ./scripts/dev/phase5_e2e_dry_run.sh
```

Preparation du dossier de preuves standard:

```bash
bash ./scripts/dev/prepare_e2e_evidence_pack.sh
```

Si dry-run technique (sans readiness juridique):

```bash
ALLOW_INCOMPLETE_LEGAL=1 bash ./scripts/dev/phase5_e2e_dry_run.sh
```

## Scenarios obligatoires

1. Install debug phone + watch
2. Premiere sync Dexcom -> watch visible
3. Veille longue (>= 8h) avec verification ack/repush
4. Batterie watch 25/20/15/10 (charge/non charge)
5. Multi-montres (switch montre principale)
6. Reinstall complete et re-ajout tile/complication
7. Dry-run release artefacts + legal gate

## Grille de resultat

| Scenario | Resultat | Notes |
| --- | --- | --- |
| Install debug | TODO |  |
| Premiere sync | TODO |  |
| Veille longue | TODO |  |
| Batterie degradee | TODO |  |
| Multi-montres | TODO |  |
| Reinstall | TODO |  |
| Dry-run release | TODO |  |

## Evidence minimale

- captures ecran app mobile + tile/complication
- logs WG7 phone/watch
- dossier diagnostics:

```bash
bash ./scripts/dev/collect_sync_diagnostics.sh
```

- resultat commandes:
  - `bash ./scripts/dev/verify_ci.sh`
  - `bash ./scripts/release/verify_release_artifacts.sh`
- dossier de preuves structure:

```bash
bash ./scripts/dev/prepare_e2e_evidence_pack.sh
```

- templates a remplir:
  - `build/e2e-evidence/<timestamp>/notes/scenario_results_template.md`
  - `build/e2e-evidence/<timestamp>/notes/go_no_go_template.md`

## Cloture go/no-go (pack final)

Generer un dossier de cloture consolide:

```bash
bash ./scripts/dev/finalize_e2e_closure_pack.sh
```

Sortie:
- `build/e2e-closure/<timestamp>/SUMMARY.md`
- `build/e2e-closure/<timestamp>/logs/*.log`

Mode accelere (one-shot):

```bash
bash ./scripts/dev/run_e2e_bundle.sh --skip-checks
```

Mode complet terrain (fail-fast si diagnostics indisponibles):

```bash
WIDGETG7_PHONE_SERIAL="<serial_phone>" WIDGETG7_WATCH_SERIAL="<serial_watch>" bash ./scripts/dev/run_e2e_bundle.sh --full
```
