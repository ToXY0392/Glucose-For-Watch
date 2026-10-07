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

- [ ] Confirm the distribution remains owner-managed personal sideloading and complies with [LICENSE](../../LICENSE)
- [ ] Do not distribute APKs built with the debug signing key
- [ ] Validate the phone and Wear APK version name and code match
- [ ] Complete current-device sync, Wear display, reconnect, and stability validation
- [ ] Remove real glucose values, account data, and device identifiers from published evidence
- [ ] Keep the Android 17 readiness claim pending until API 37 hardware validation is signed off

## Sign-off

| Role | Name | Date |
|------|------|------|
| Developer / operator | | |
| Independent reviewer (if available) | | |
