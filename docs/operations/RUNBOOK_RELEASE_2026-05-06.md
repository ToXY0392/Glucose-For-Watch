# Runbook Release - Widget G7

Date: 2026-05-06

## Objectif

Fournir une procedure reproductible de verification avant diffusion:
- gates build/tests critiques,
- artefacts release attendus,
- blocage juridique explicite.

## Pre-requis Linux/Unix

- Java 17 disponible (`JAVA_HOME` configure).
- Wrapper Gradle executable (`chmod +x ./gradlew` si necessaire).
- Android SDK configure (local.properties `sdk.dir` ou `ANDROID_SDK_ROOT` / `ANDROID_HOME`).

## Commandes standard

1) Gate CI locale (debug/tests):

```bash
./scripts/dev/verify_ci.sh
```

2) Verification artefacts release:

```bash
./scripts/release/verify_release_artifacts.sh
```

3) Verification juridique (bloquante si placeholders):

```bash
./scripts/release/check_legal_placeholders.sh
```

4) Dry-run complet:

```bash
./scripts/release/release_dry_run.sh
```

5) Cloture E2E (pack final de decision):

```bash
bash ./scripts/dev/finalize_e2e_closure_pack.sh
```

Smoke check rapide sans relancer Gradle:

```bash
bash ./scripts/dev/finalize_e2e_closure_pack.sh --skip-checks
```

6) Bundle one-shot (pack preuves + diagnostics si serials + cloture):

```bash
bash ./scripts/dev/run_e2e_bundle.sh --skip-checks
```

Mode complet exigeant (checks complets + diagnostics obligatoires):

```bash
WIDGETG7_PHONE_SERIAL="<serial_phone>" WIDGETG7_WATCH_SERIAL="<serial_watch>" bash ./scripts/dev/run_e2e_bundle.sh --full
```

## Artefacts attendus

- `mobile/build/outputs/apk/release/mobile-release.apk`
- `wear/build/outputs/apk/release/wear-release.apk`

## Politique juridique

La release publique reste bloquee tant que des placeholders `[À compléter]` persistent dans:
- `docs/CGU.md`
- `docs/POLITIQUE_CONFIDENTIALITE.md`
- `docs/LEGAL_PUBLICATION_CHECKLIST.md`

Pour un dry-run purement technique (sans go-public), bypass possible:

```bash
ALLOW_INCOMPLETE_LEGAL=1 ./scripts/release/check_legal_placeholders.sh
```
