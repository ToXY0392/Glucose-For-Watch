# Glucose For Watch

<p align="center">
  <img alt="Version 1.0.0 candidate" src="https://img.shields.io/badge/version-1.0.0%20candidate-FBBC04?style=for-the-badge">
  <img alt="Android 17 API 37 validated" src="https://img.shields.io/badge/Android%2017-API%2037%20validated-0B3D2E?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Wear OS" src="https://img.shields.io/badge/Wear%20OS-Tile%20%2B%20Complication-4285F4?style=for-the-badge&logo=wearos&logoColor=white">
  <br>
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.3.20-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white">
</p>

Glucose For Watch is an Android phone app and Wear OS companion that retrieves glucose readings from Dexcom Share and displays them on a paired watch.

The phone app handles Dexcom Share access and synchronization. The watch companion presents the latest reading in its app, a Wear OS tile, and supported watch-face complications.

## Features

- Reads glucose data from Dexcom Share for Dexcom G6 and G7 accounts.
- Sends readings from an Android phone to a paired Wear OS watch using the Wearable Data Layer.
- Displays readings in **mg/dL** or **mmol/L** on the phone and watch.
- Provides a phone app, a watch tile, and short-text, long-text, and ranged-value complications.
- Supports automatic background updates and manual refresh from the phone or watch tile.
- Shows sync status and marks cached watch readings as stale when they are no longer recent.

Glucose For Watch uses Dexcom Share credentials; it does not connect directly to a sensor over Bluetooth. Dexcom Share must be enabled for the account.

## Requirements

### To use the apps

- An Android phone running Android 9 (API 28) or later.
- A Wear OS watch running Android API 30 or later, paired with the phone.
- A Dexcom Share account with sharing enabled and the appropriate follower credentials.
- An internet connection on the phone to retrieve readings from Dexcom Share.

### To build from source

- Android Studio with a compatible Android SDK.
- Android SDK Platform 36.
- JDK 17 or later. JBR 21 is recommended when building from Android Studio.

The phone app supports Android API 28 and later. The Wear OS app supports Android API 30 and later.

## Build and install

Clone the repository and open it in Android Studio, or build from a terminal.

Create a local `local.properties` file in the repository root and set the path to your Android SDK:

```properties
sdk.dir=/path/to/Android/sdk
```

Do not commit `local.properties`.

Build both debug APKs:

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```

```bash
./gradlew :mobile:assembleDebug :wear:assembleDebug
```

The APKs are written to:

- Phone: `mobile/build/outputs/apk/debug/mobile-debug.apk`
- Watch: `wear/build/outputs/apk/debug/wear-debug.apk`

Install each APK on its corresponding device using Android Studio or ADB. For the repository's combined phone-and-watch install task, configure the optional phone and watch serials in `local.properties`:

```properties
gfw.adb.phone.serial=<phone_adb_serial>
gfw.adb.watch.serial=<watch_adb_serial>
```

Then run:

```powershell
.\gradlew.bat installGlucoseForWatchDebug
```

```bash
./gradlew installGlucoseForWatchDebug
```

After installation, open the phone app, review the in-app legal notices, enter the Dexcom Share credentials and region, and start synchronization. Add the Glucose For Watch tile or a supported complication to the watch as needed.

The latest published release is [v0.6.0](https://github.com/ToXY0392/Glucose-For-Watch/releases/tag/v0.6.0). Version 1.0.0 is not published yet. The project is not distributed through Google Play; official APKs may be published on this repository for personal sideloading only.

Official APKs are signed with the Android debug key. Install updates only when they are signed with the same key as the installed app. Do not uninstall an existing installation before backing up anything important: uninstalling removes local app data. The debug signing key may change, and future updates are not guaranteed to remain compatible.

## Project structure

| Path | Purpose |
|------|---------|
| `mobile/` | Android phone app, Dexcom access, and sync control |
| `wear/` | Wear OS companion app, tile, and complications |
| `core/model/` | Shared glucose and sync models |
| `core/datalayer-contract/` | Phone-to-watch data contract |
| `feature/dexcom-share/` | Dexcom Share client |
| `feature/sync/` | Synchronization logic |
| `feature/watch-install/` | Debug watch installation support |
| `docs/` | User guides, technical documentation, and policies |
| `scripts/` | Development and QA tools |

For a detailed module overview and synchronization flow, see [the architecture guide](docs/dev/architecture.md).

## Privacy and security

- Dexcom Share credentials are stored encrypted on the phone.
- Glucose readings are cached locally on the phone and watch for synchronization and display.
- Never commit credentials, `local.properties`, keystores, signing secrets, or real glucose readings.
- Do not include credentials or identifiable health data in issues, logs, screenshots, or pull requests.

Read the [privacy policy](docs/legal/privacy-policy.md) and [security policy](SECURITY.md) for details.

## Medical disclaimer

Glucose For Watch is **not a certified medical device**. Its readings are for informational purposes and must not be used as the sole basis for treatment decisions. Confirm readings with an official Dexcom app and follow your healthcare provider's guidance.

Read the full [medical disclaimer](docs/legal/medical-disclaimer.md).

## Documentation

- [User guide](docs/guide/user.md)
- [Dexcom Share compatibility and setup](docs/guide/dexcom.md)
- [Developer setup and build commands](docs/dev/setup.md)
- [Architecture and synchronization flow](docs/dev/architecture.md)
- [Documentation index](docs/index.md)

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request. Development changes are reviewed through pull requests and the repository's automated checks.

## License

The [license](LICENSE) permits downloading and installing official APKs published by the copyright holder in this repository for personal, non-commercial sideloading on devices you own or control. It does not permit redistribution, modification, commercial use, or app-store publication.
