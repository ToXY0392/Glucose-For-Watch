# Getting started

> **Last updated:** 2026-05-23

## Prerequisites

- Android Studio (recent) — [Android Studio guide](android-studio.md)
- JDK JBR 21 (bundled with Studio)
- Android SDK Platform 36
- Git
- Optional: Wear OS watch + Android phone for device testing

## Clone and configure

```bash
git clone <repository-url>
cd "Widget G7"
cp gradle.properties.example gradle.properties   # if present
```

Create `local.properties`:

```properties
sdk.dir=/path/to/Android/sdk
widgetg7.adb.phone.serial=<phone_serial>
widgetg7.adb.watch.serial=<watch_serial>
```

Dexcom Share credentials go in `gradle.properties` (never commit):

```properties
dexcomShareUsername=...
dexcomSharePassword=...
dexcomShareServer=US
dexcomShareApplicationId=d89443d2-327c-4a6f-89e5-496bbb0317db
```

## Build

```powershell
# Windows
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug

# Unix / WSL
./gradlew :mobile:assembleDebug :wear:assembleDebug
```

## Install on devices

```powershell
.\gradlew.bat installWidgetG7Debug
```

Requires ADB serials in `local.properties`. List devices:

```powershell
adb devices -l
```

## Run tests

```powershell
.\gradlew.bat test
```

## Project structure

See [Repository structure](../architecture/repository-structure.md).

## Dual IDE workflow

- **Cursor (WSL):** primary editing, agents, CLI builds
- **Android Studio (Windows):** emulators, Layout Inspector, Tile Preview, Logcat

See [Dual IDE setup](dual-ide-setup.md).

## Next steps

1. Read [Architecture overview](../architecture/overview.md)
2. Read [Sync pipeline](../architecture/sync-pipeline.md)
3. Review [Master refactor plan](../plan/MASTER-REFACTOR-PLAN.md)
