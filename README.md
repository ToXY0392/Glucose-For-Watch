# Glucose For Watch

Glucose For Watch syncs Dexcom glucose data to Wear OS for fast at-a-glance display on your watch (app, tile, and complication). The app uses the **ToXY UX kit** design tokens with **AGP-standard medical colors** for all glucose values.

> Repository folder: **`Glucose-For-Watch`** · GitHub: [`Glucose-For-Watch`](https://github.com/ToXY0392/Glucose-For-Watch) · install: `installGlucoseForWatchDebug`

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-Mobile-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Wear OS" src="https://img.shields.io/badge/Wear%20OS-Watch-4285F4?style=for-the-badge&logo=wearos&logoColor=white">
  <img alt="Gradle" src="https://img.shields.io/badge/Gradle-9.4.1-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.3.20-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
</p>

<p align="center">
  <img alt="Glucose For Watch sync flow: Dexcom Share → Mobile → Wear OS" src="docs/assets/glucose-for-watch-architecture.png" width="780">
</p>

## Overview

Phone fetches **Dexcom Share**, pushes to the watch via the **Wear Data Layer**, and updates the tile and complication with ack-based sync health.

**Supported sensors:** Dexcom **G6** and **G7** — see [guide/dexcom.md](docs/guide/dexcom.md).

## Highlights

- Modular sync engine (`GlucoseSyncEngine`) with ack-based delivery
- AGP / Time-in-Range colors on glucose values
- Separate mobile and wear APKs (embedded wear install in debug)

**Architecture:** modules, sync flow, Data Layer contract — [dev/architecture.md](docs/dev/architecture.md).

## Prerequisites

Android Studio, JDK **JBR 21**, Gradle **9.4.1**, `compileSdk 36`, phone + Wear OS watch for testing. Details: [dev/setup.md](docs/dev/setup.md).

## Quick start

1. Install debug APKs on phone and watch (`installGlucoseForWatchDebug`).
2. Open the app, accept legal screens, connect Dexcom Share, tap **Sync**.
3. Add the tile or complication on the watch.

Full steps: [guide/user.md](docs/guide/user.md).

## Build

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat installGlucoseForWatchDebug
```

APK paths, QA scripts, and troubleshooting: [dev/setup.md](docs/dev/setup.md).

## Troubleshooting

| Symptom | Action |
|---------|--------|
| Gradle IDE sync fails but terminal works | [dev/setup.md](docs/dev/setup.md) — Environment troubleshooting |
| Watch value frozen, sync issues | [guide/user.md](docs/guide/user.md) — Troubleshooting |

## Documentation

**[docs/index.md](docs/index.md)** · Design: [toxy-ux-kit/](toxy-ux-kit/README.md)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) and [AGENTS.md](AGENTS.md). Sync-related PRs: use [plan/PR-CHECKLIST.md](docs/plan/PR-CHECKLIST.md).

## Security

- Never commit Dexcom credentials or real glucose data
- Never commit `local.properties`, keystores, or secrets
- Use `gradle.properties.example` as template only

## Medical disclaimer

Glucose For Watch is **not** a certified medical device. See [medical disclaimer](docs/legal/medical-disclaimer.md).

## License

See repository license file (if applicable).

---

Developed to keep glucose visible on Wear OS with a simple, traceable, and robust sync flow.
