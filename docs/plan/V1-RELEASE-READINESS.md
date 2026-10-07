# v1.0.0 release readiness

**Status: NO-GO — candidate metadata only.** The app version is set to `1.0.0` (version code `26`) for both APKs, but no v1.0.0 tag or GitHub release has been created. This checklist is the current release record; the v0.5.0/v0.6.0 gates remain historical evidence.

## Scope

- Intended distribution remains owner-managed sideloading. This project does not currently publish through Google Play.
- The repository license permits personal sideload use only. Do not redistribute, modify, or use commercially without explicit permission from the copyright holder.
- The README Android 17/API 37 readiness badge remains pending until validated on supported hardware.
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

- [ ] Fresh phone-to-watch sync and manual refresh pass on the candidate build.
- [ ] Tile and complication parity, stale-data display, reconnect, and offline recovery pass.
- [ ] Re-run the required stability soak after the post-v0.6.0 sync and Android 17 changes.
- [ ] Validate API 37 support on target hardware; keep the README readiness badge pending until this is complete.
- [ ] Review existing screenshots and HTML captures for real glucose values, account data, and device identifiers; remove or redact them before reusing evidence.
- [ ] Add a dated, redacted sign-off to `docs/qa/`; do not include real glucose readings, account details, or device serials.

### Legal and publication

- [ ] Complete every applicable item in [publication-checklist.md](../legal/publication-checklist.md).
- [ ] Confirm the intended distribution complies with [LICENSE](../../LICENSE); v1.0.0 does not change the license.
- [ ] Review the medical disclaimer, privacy policy, Dexcom wording, and release notes for the actual distribution.
- [ ] Obtain the required operator/reviewer sign-off for gate G-V1 in [STABILITY-GATES.md](STABILITY-GATES.md).

## Current blockers

1. Existing hardware sign-offs date from the v0.6.0 baseline and do not cover the later sync, Wear, and API 37 changes.
2. Android 17/API 37 validation is explicitly pending.
3. The publication checklist has not been signed off.
4. The configured Gradle release signing key is the debug key; no distributable stable release-key evidence is present.
5. The license remains restricted to personal sideload use.
6. Existing QA captures require a human privacy review before reuse in release material.

CI can prove that source checks and APK assembly pass; it cannot satisfy device, legal, licensing, or release-signing approval. Do not create the `v1.0.0` tag or GitHub release until all applicable blockers are resolved and gate G-V1 is signed **Go**.

## Observed maintenance debt

The candidate build passes, but Gradle reports deprecated Android Gradle Plugin configuration flags and legacy AndroidX Security Crypto APIs (`EncryptedSharedPreferences`/`MasterKey`); Kotlin also reports deprecated Compose menu-anchor and activity-transition calls. These warnings are not build failures. Track their migration separately, and do not remove or weaken encrypted credential storage without a reviewed replacement and migration plan.
