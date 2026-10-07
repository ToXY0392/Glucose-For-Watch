# v1.0.0 release readiness

**Status: NO-GO — candidate metadata only.** The app version is set to `1.0.0` (version code `26`) for both APKs, but no v1.0.0 tag or GitHub release has been created. This checklist is the current release record; the v0.5.0/v0.6.0 gates remain historical evidence.

## Scope

- Intended distribution remains owner-managed sideloading. This project does not currently publish through Google Play.
- The repository license permits personal sideload use only. Do not redistribute, modify, or use commercially without explicit permission from the copyright holder.
- The operator confirms Android 17/API 37 validation on target hardware; see the [device validation report](../qa/2026-10-07-v1-device-validation.md).
- The `release` Gradle build type currently uses the debug signing configuration for local validation. Those APKs must not be presented or distributed as production-signed release artifacts.

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
- [ ] `bash scripts/release/verify_release_artifacts.sh` builds both release APKs.
- [ ] Inspect both APK manifests and verify they report `versionName=1.0.0`, `versionCode=26`, and the intended application IDs.
- [ ] Confirm the intended distribution signing key and update/rollback strategy outside the repository. Never use the debug key for a distributed release.

### Device validation

- [x] Operator confirms all required device tests, including sync, Wear behavior, stability, and API 37 validation, were completed on the v1.0.0 candidate; see [operator-reported validation](../qa/2026-10-07-v1-device-validation.md).
- [x] Operator reports wearing the candidate daily with no bugs observed.
- [ ] Review existing screenshots and HTML captures for real glucose values, account data, and device identifiers; remove or redact them before reusing evidence.
- [ ] Add a dated, redacted sign-off to `docs/qa/`; do not include real glucose readings, account details, or device serials.

### Legal and publication

- [ ] Complete every applicable item in [publication-checklist.md](../legal/publication-checklist.md).
- [ ] Confirm the intended distribution complies with [LICENSE](../../LICENSE); v1.0.0 does not change the license.
- [ ] Review the medical disclaimer, privacy policy, Dexcom wording, and release notes for the actual distribution.
- [ ] Obtain the required operator/reviewer sign-off for gate G-V1 in [STABILITY-GATES.md](STABILITY-GATES.md).

## Current blockers

1. The publication checklist has not been signed off.
2. The configured Gradle release signing key is the debug key; no distributable stable release-key evidence is present.
3. The license remains restricted to personal sideload use.
4. Existing QA captures require a human privacy review before reuse in release material.

CI can prove that source checks and APK assembly pass; it cannot satisfy device, legal, licensing, or release-signing approval. Do not create the `v1.0.0` tag or GitHub release until all applicable blockers are resolved and gate G-V1 is signed **Go**.

## Observed maintenance debt

The candidate build passes, but Gradle reports deprecated Android Gradle Plugin configuration flags and legacy AndroidX Security Crypto APIs (`EncryptedSharedPreferences`/`MasterKey`); Kotlin also reports deprecated Compose menu-anchor and activity-transition calls. These warnings are not build failures. Track their migration separately, and do not remove or weaken encrypted credential storage without a reviewed replacement and migration plan.
