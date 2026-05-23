# Compatibility

Quick reference — full details: **[docs/compatibility/environment.md](docs/compatibility/environment.md)**

## Recommended environment

| Component | Version |
|-----------|---------|
| Android Studio | Recent stable |
| Gradle wrapper | 9.4.1 |
| Android Gradle Plugin | 9.2.1 |
| Kotlin | 2.3.20 |
| JDK (Gradle) | JBR 21 |
| compileSdk | 36 |

## Quick check

```powershell
.\gradlew.bat help
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```

`BUILD SUCCESSFUL` = compatible environment.

## Windows + Android Studio

If Studio shows write permission errors, see [environment doc](docs/compatibility/environment.md) and run:

```powershell
cd scripts\windows
.\fix-windows-studio-defender-admin.ps1 -ProjectPath "C:\Dev\Widget-G7"
```

Prefer project path **without spaces** (`C:\Dev\Widget-G7`) or WSL UNC path.

## Dual IDE

Cursor (WSL) + Android Studio (Windows) on **one copy**: [dual-ide-setup.md](docs/development/dual-ide-setup.md)

## Dexcom sensors

G6 and G7 supported via Dexcom Share: [dexcom-g6-g7.md](docs/compatibility/dexcom-g6-g7.md)
