# v1.0.0 candidate - device-version verification

## Operator report and device check

The operator reported completing device tests and wearing what they believed was the v1.0.0 candidate daily, with no bugs observed.

On 2026-10-07, ADB inspection of the operator's connected daily-use devices found Glucose For Watch `0.6.0` (version code `25`) installed on both the Pixel 8a phone and Pixel Watch 2. The installed Phone and Wear APK signing certificates both match the owner's local debug keystore used to build the v1.0.0 candidate (`1.0.0`, version code `26`).

## Conclusion

The signature match means the candidate APKs are compatible with the installed apps from a signing-certificate perspective. It does **not** establish that the v1.0.0 candidate was installed or tested on these devices. The operator's no-bug daily-use report therefore applies to the installed v0.6.0 apps unless candidate testing on other devices is separately documented.

No app was installed or uninstalled during this check. The phone and watch remain on v0.6.0. Do not claim candidate hardware validation or publish the API 37 readiness badge as validated until the v1.0.0 candidate is tested on target devices.

No real glucose values, account details, device serials, or credentials are included.
