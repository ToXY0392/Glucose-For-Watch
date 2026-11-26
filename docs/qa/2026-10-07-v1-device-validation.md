# v1.0.0 candidate - operator device validation

## Device and build verification

On 2026-10-07, ADB verified Glucose For Watch `1.0.0` (version code `26`) installed on the operator's Pixel 8a phone and Pixel Watch 2. Both devices report Android 17 / API 37.

The installed Phone and Wear APK signing certificates match the owner's local debug keystore used to build the candidate. The candidate was installed as an update over v0.6.0; no uninstall was required.

## Operator result

After installation, the operator reported that the v1.0.0 candidate works very well. This confirms a positive functional smoke check on the operator's daily-use devices, including Android 17 / API 37.

The operator previously reported completing device tests and daily use without bugs, but ADB established those earlier daily-use tests were on v0.6.0. Do not attribute those historical results to v1.0.0.

## Scope and limits

- The candidate is installed and working on both target devices, and its signing identity is compatible with the owner's installed apps.
- Detailed scenario-by-scenario results, test duration, and dedicated soak logs were not provided.
- This report does not claim independent witnessing or formal completion of every G-V1 scenario.
- No real glucose values, account details, device serials, or credentials are included.
