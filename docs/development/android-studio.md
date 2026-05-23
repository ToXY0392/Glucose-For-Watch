# Android Studio guide

> **Last updated:** 2026-05-23  
> **Related:** [Dual IDE setup](dual-ide-setup.md) · [Environment](../compatibility/environment.md)

Guide for working on Widget G7 from **Android Studio** (Windows or WSL folder via `\\wsl$\…`).

---

## Official references

| Topic | URL |
|-------|-----|
| Android Studio | https://developer.android.com/studio |
| Build / Gradle / AGP | https://developer.android.com/build |
| Run and debug | https://developer.android.com/studio/run |
| AGP ↔ Gradle compatibility | https://developer.android.com/build/releases/gradle-plugin |

---

## Expected toolchain (this repo)

| Component | Version | Source |
|-----------|---------|--------|
| Android Gradle Plugin | **9.2.1** | Root `build.gradle.kts` |
| Gradle wrapper | **9.4.1** | `gradle/wrapper/gradle-wrapper.properties` |
| Kotlin Android plugin | **2.3.20** | Root `build.gradle.kts` |
| compileSdk / targetSdk | **36** | Module `build.gradle.kts` |

Install **SDK Platform 36** and compatible Build-Tools via SDK Manager.

---

## AGP 9 upgrade note

### Error: "You cannot add Provider instances to the Android SourceSet API"

AGP 9 disallows `Provider<Directory>` in `sourceSets`. This repo fixes embedded wear APK assets in `mobile/build.gradle.kts` using:

1. `DirectoryProperty` for `embeddedWearAssetOutputDir`
2. `prepareWearApkForDebugAssets` Copy task
3. `androidComponents { addGeneratedSourceDirectory(...) }` for debug variant

**Do not** use `android.sourceset.disallowProvider=false` — it is unsupported long-term.

After upgrade: **Sync Project with Gradle Files** → verify Upgrade Assistant shows AGP 9.2.1 up to date.

---

## Opening the project

### Option A — Windows disk (USB / ADB on Windows)

Clone to e.g. `C:\Dev\Widget-G7` (**avoid spaces** in folder name).

### Option B — Same files as WSL (Cursor on Linux)

- **Android Studio (Windows):** File → Open → `\\wsl.localhost\Ubuntu\home\<user>\...\Widget-G7`
- Or from WSL: `./scripts/dev/open-android-studio-wsl-project.sh`

**One working copy only** — do not maintain separate Desktop + WSL clones.

---

## `local.properties`

Not versioned. Must contain `sdk.dir` for the OS running Gradle:

**Windows:**
```properties
sdk.dir=C\:\\Users\\Utilisateur\\AppData\\Local\\Android\\Sdk
widgetg7.adb.phone.serial=<phone_serial>
widgetg7.adb.watch.serial=<watch_serial>
```

**Linux / WSL:**
```properties
sdk.dir=/home/user/Android/Sdk
```

When Studio on Windows syncs a `\\wsl$\` project, Gradle typically uses the **Windows SDK path**.

---

## Gradle JDK

**File → Settings → Build Tools → Gradle → Gradle JDK:** select embedded **jbr** (JBR 21).

Optional user-level `%USERPROFILE%\.gradle\gradle.properties`:
```properties
org.gradle.java.home=C:/Program Files/Android/Android Studio/jbr
```

Project uses `kotlin.compiler.execution.strategy=in-process` for daemon stability.

---

## Run configurations

| Config | Module | Type |
|--------|--------|------|
| mobile | `:mobile` | Android App |
| wear | `:wear` | Wear OS |

Both share `applicationId = com.widgetg7.mobile` — run separately or use `installWidgetG7Debug`.

---

## Install debug APKs

Gradle task: **`installWidgetG7Debug`**

```powershell
.\gradlew.bat installWidgetG7Debug
```

Requires ADB serials in `local.properties`. List devices: `adb devices -l`.

---

## Debugging

- Use **Logcat** filtered by `widgetg7` or `WearableListenerService`
- **Wear emulator** for tile preview
- Physical USB: prefer Studio on **Windows** with phone + watch connected

---

## Quick troubleshooting

| Symptom | Fix |
|---------|-----|
| Write permissions (Windows) | [Environment](../compatibility/environment.md), Defender scripts |
| Sync OK in terminal, fails in IDE | Set Gradle JDK to JBR; `./gradlew --stop` |
| Provider / SourceSet AGP 9 error | See AGP 9 section above |
| SDK not found | Fix `sdk.dir` in `local.properties` |
| Kotlin daemon error | `kotlin.compiler.execution.strategy=in-process` in `gradle.properties` |

---

## Related

- [Dual IDE setup](dual-ide-setup.md)
- [Getting started](getting-started.md)
