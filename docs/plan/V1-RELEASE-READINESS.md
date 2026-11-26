# v1.0.0 release readiness

**Status: NO-GO — personal-sideload release preparation.** The app version is set to `1.0.0` (version code `26`) for both APKs, but no v1.0.0 tag or GitHub release has been created. The intended release may attach official debug-signed APKs for personal sideloading only. This does not make the app a Play Store or production-signed release.

## Scope

- Intended distribution remains owner-managed sideloading. This project does not currently publish through Google Play.
- The repository license permits personal installation of official APKs published by the copyright holder on this repository only. It does not grant third-party redistribution, modification, commercial use, or app-store publication.
- The v1.0.0 candidate is installed on the operator's Pixel 8a and Pixel Watch 2, both running Android 17/API 37; the operator reports that it works very well. See the [device validation report](../qa/2026-10-07-v1-device-validation.md).
- The Gradle `debug` variant is the intended sideload artifact. It is signed with the owner's Android debug key; never describe it as production-signed or Play Store-ready.
- Release APKs must be built by the owner with the same private debug keystore used for the tested installation. Do not attach ephemeral CI-signed artifacts; an Android update must match the installed app's signing certificate.
- The updated [LICENSE](../../LICENSE) permits downloading and installing only official APKs published by the copyright holder on this repository, for personal, non-commercial use. It does not authorize third-party redistribution.

## Candidate metadata

| Item | Value |
|------|-------|
| App version name | `1.0.0` |
| App version code | `26` |
| Metadata source | `gfwVersionName` and `gfwVersionCode` in root `gradle.properties` |
| Phone and Wear | Both read the shared metadata |
| Compile / target SDK | API 37 |
| Last published release | `v0.6.0` |

## Go / No-Go checklist

Complete each item against the exact candidate commit. Keep personal health data, credentials, signing keys, and device identifiers out of the repository.

### Automated and artifacts

- [ ] `bash scripts/dev/verify_ci.sh` passes on the final candidate commit.
- [ ] Build `:mobile:assembleDebug` and `:wear:assembleDebug` on the owner's machine using the preserved debug keystore.
- [ ] Inspect both APK manifests and verify they report `versionName=1.0.0`, `versionCode=26`, and the intended application IDs.
- [ ] Verify both APK certificates match each other and the certificate of the tested installation; do not attach CI artifacts signed with an ephemeral runner key.
- [ ] Back up the debug keystore privately. Explain that updates require the same certificate and that changing keys may require uninstalling and losing local app data.

### Device validation

- [x] Install the candidate on the target phone and watch; ADB confirms version `1.0.0` (code `26`) on both devices.
- [x] Confirm both devices run Android 17/API 37 and the operator reports the candidate works very well; the README badge records this device check.
- [x] Confirm both installed apps use the same signing certificate as the owner-built candidate APKs.
- [ ] Keep a detailed scenario-by-scenario and dedicated soak record if required for formal G-V1 sign-off.
- [ ] Review existing screenshots and HTML captures for real glucose values, account data, and device identifiers; remove or redact them before reusing evidence.
- [ ] Add a dated, redacted sign-off to `docs/qa/`; do not include real glucose readings, account details, or device serials.

### Legal and publication

- [ ] Complete every applicable item in [publication-checklist.md](../legal/publication-checklist.md).
- [ ] Confirm the intended distribution complies with [LICENSE](../../LICENSE); v1.0.0 does not change the license.
- [ ] Review the medical disclaimer, privacy policy, Dexcom wording, and release notes for the actual distribution.
- [ ] Obtain the required operator/reviewer sign-off for gate G-V1 in [STABILITY-GATES.md](STABILITY-GATES.md).

## Current blockers

1. The updated personal-sideload publication checklist needs operator sign-off for this exact release.
2. Final owner-built debug APKs must be rebuilt and checked against the signing certificate of the tested installation before attachment.
3. Existing QA captures require a human privacy review before reuse in release material.

CI can prove source checks and APK assembly, but its APK signatures may not match the owner's installed build. Do not create the `v1.0.0` tag or GitHub release until the applicable publication checklist is signed, both owner-built APKs are checked against the tested signing certificate, and gate G-V1 is signed **Go**.

## Observed maintenance debt

The candidate build passes, but Gradle reports deprecated Android Gradle Plugin configuration flags and legacy AndroidX Security Crypto APIs (`EncryptedSharedPreferences`/`MasterKey`); Kotlin also reports deprecated Compose menu-anchor and activity-transition calls. These warnings are not build failures. Track their migration separately, and do not remove or weaken encrypted credential storage without a reviewed replacement and migration plan.
