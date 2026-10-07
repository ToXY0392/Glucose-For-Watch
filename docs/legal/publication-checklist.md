# Publication checklist

Pre-release legal and safety review for Glucose For Watch.

## Medical

- [ ] Medical disclaimer visible in app (first launch + settings)
- [ ] Disclaimer text matches [medical-disclaimer.md](medical-disclaimer.md)
- [ ] App store description states "not a medical device"
- [ ] No claims of FDA/CE certification

## Privacy

- [ ] Privacy policy accessible in app
- [ ] Policy matches [privacy-policy.md](privacy-policy.md)
- [ ] No analytics sending glucose values without explicit consent
- [ ] Credentials stored encrypted on device

## Data safety

- [ ] No real glucose data in screenshots, docs, or sample code
- [ ] No credentials in git history (audit before public release)
- [ ] `local.properties` and `gradle.properties` in `.gitignore`

## Dexcom

- [ ] No Dexcom trademark misuse in branding
- [ ] Clear statement: unofficial follower app using Share protocol
- [ ] Link to Dexcom official apps for treatment decisions

## Store listing (if applicable)

- [ ] Age rating appropriate for health app
- [ ] Permission justifications documented
- [ ] Wear companion declared correctly

## Sideload release (v1.0.0)

- [ ] Publish only official APKs built and published by the copyright holder on this GitHub repository
- [ ] Confirm the license permission remains limited to personal, non-commercial sideloading on devices recipients own or control
- [ ] State clearly that APKs are debug-signed, not Play Store/production builds, and that future updates require the same signing certificate
- [ ] Keep the signing keystore private and backed up outside the repository; never upload it
- [ ] Validate the phone and Wear APK version name and code match
- [ ] Complete current-device sync, Wear display, reconnect, stability, and API 37 validation on the v1.0.0 candidate itself; record the installed candidate version in the QA report
- [ ] Remove real glucose values, account data, and device identifiers from published evidence
- [ ] Ensure the Android 17/API 37 claim matches the candidate device check recorded in [the QA report](../qa/2026-10-07-v1-device-validation.md)

## Sign-off

| Role | Name | Date |
|------|------|------|
| Developer / operator | | |
| Independent reviewer (if available) | | |
