# Developer setup

## Prerequisites

| Component | Version |
|-----------|---------|
| Android Studio | Recent stable |
| Gradle wrapper | 9.4.1 |
| Android Gradle Plugin | 9.2.1 |
| Kotlin | 2.3.20 |
| JDK (Gradle) | JBR 21 |
| compileSdk | 36 |

Optional: Wear OS watch + Android phone for device testing.

## Clone and configure

```bash
git clone <repository-url>
cd "Glucose-For-Watch"
```

Create `local.properties` (not in git):

```properties
sdk.dir=/path/to/Android/sdk
widgetg7.adb.phone.serial=<phone_serial>
widgetg7.adb.watch.serial=<watch_serial>
```

Dexcom Share credentials in `gradle.properties` (never commit):

```properties
dexcomShareUsername=...
dexcomSharePassword=...
dexcomShareServer=US
dexcomShareApplicationId=d89443d2-327c-4a6f-89e5-496bbb0317db
```

## Build and test

```powershell
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
.\gradlew.bat test
.\gradlew.bat installWidgetG7Debug
```

APKs: `mobile/build/outputs/apk/debug/mobile-debug.apk`, `wear/build/outputs/apk/debug/wear-debug.apk`.

### Compose preview browser (Showkase · AUTO-3)

Debug builds include a searchable catalog of `@ShowkaseComposable` / `@Preview` screens:

```powershell
.\gradlew.bat :mobile:assembleDebug
```

Launch from code: `startActivity(Showkase.getBrowserIntent(context))` (generated after first debug compile). Previews live in `mobile/src/debug/` — annotate with `@ShowkaseComposable` alongside `@Preview`.

**Conventions:** one `@ShowkaseRoot` module (`WidgetG7ShowkaseRoot`) · preview composables `internal` · reuse `HomePreviewStates` / `SyncTestFixtures` for realistic Home states · KSP `2.3.9` + Showkase `1.0.5` on `:mobile` debug only.

Design tokens handoff (M.4): `py -3 toxy-ux-kit/tools/export-design-reference.py` → open `toxy-ux-kit/design-reference/index.html`.

Doc link check (CI): `py -3 scripts/dev/check_docs_links.py` (also in `verify_ci.sh`).

List devices: `adb devices -l`.

### PC sideload (distribution v0.5.0)

No Play Store — install debug builds from the dev machine only:

```powershell
# Optional: remove legacy package IDs
.\scripts\qa\uninstall-legacy-apps.ps1

# Build + install phone + watch (serials in local.properties)
.\gradlew.bat installWidgetG7Debug

# Or install + package checks + push/ack seq
.\scripts\qa\install-and-verify.ps1
.\scripts\qa\install-and-verify.ps1 -VerifyOnly   # checks only
.\scripts\qa\install-and-verify.ps1 -AllowPhoneOnly
```

Set `widgetg7.adb.phone.serial` and `widgetg7.adb.watch.serial` in `local.properties` for wireless ADB (watch) and stable targeting.

### QA and stability gates

Before merging any PR that touches sync or phone lifecycle:

```powershell
.\scripts\qa\stability-gate.ps1 -Strict
.\scripts\qa\hardware-smoke.ps1
.\scripts\qa\qa-session-c.ps1              # C.0/C.5 automated partial
.\scripts\qa\capture-c1-agp-session.ps1    # C.1 AGP 60/120/200 screencaps
.\scripts\qa\soak-monitor.ps1              # X.6 — 30 min
.\scripts\qa\soak-monitor.ps1 -DurationMinutes 480 -Label C.7
```

After a crash:

```powershell
.\scripts\qa\capture-crash-log.ps1           # phone FATAL logcat → docs/qa/incidents/
.\scripts\qa\capture-crash-log.ps1 -Watch      # watch FATAL logcat
```

Output: `docs/qa/incidents/YYYY-MM-DD-crash-phone.log` (or `-watch`). Paste relevant `FATAL EXCEPTION` blocks into the incident markdown — **never** commit credentials or full unredacted logcat. See [SECURITY.md](../../SECURITY.md).

Criteria: [STABILITY-GATES.md](../plan/STABILITY-GATES.md) · progress: [PROGRESS.md](../plan/PROGRESS.md)

### UI preview export (Bloc M)

Export six home states as PNG without hardware:

```powershell
.\scripts\qa\export-app-preview.ps1
# Linux / CI equivalent:
# bash scripts/qa/export_app_previews.sh
.\scripts\qa\open-previews.ps1
```

Output: `mobile/build/app-previews/*.png` and `mobile/build/preview-gallery/index.html`. On PRs touching mobile UI, CI uploads the gallery as artifact **mobile-home-preview-gallery** (AUTO-1/2).

## Dual IDE (Cursor + Android Studio)

Work on **one Git copy** from both environments:

| Tool | Path |
|------|------|
| Cursor (WSL) | `/home/<user>/.../Glucose-For-Watch` |
| Android Studio (Windows) | `\\wsl.localhost\Ubuntu\home\<user>\...\Glucose-For-Watch` |

Do not maintain a second clone on `C:\Users\...\Desktop`.

### `local.properties` and SDK

| Context | Typical `sdk.dir` |
|---------|-------------------|
| `./gradlew` in WSL | Linux path, e.g. `/home/user/Android/Sdk` |
| Gradle sync from Studio on `\\wsl$\` | Windows path, e.g. `C\:\\Users\\...\\Android\\Sdk` |

Launch Studio from WSL: `./scripts/dev/open-android-studio-wsl-project.sh`

### Best practices

1. Avoid concurrent heavy Gradle syncs in Cursor + Studio
2. Use **LF** line endings
3. After mobile/wear changes: `./gradlew installWidgetG7Debug`

Cursor rule: `.cursor/rules/glucose-for-watch-dual-ide-wsl.mdc`

## Android Studio

Set **Gradle JDK** to embedded JBR (File → Settings → Build Tools → Gradle).

Run configurations: `:mobile` (Android App), `:wear` (Wear OS). Both share `applicationId = com.widgetg7.mobile`.

Official refs: [Android Studio](https://developer.android.com/studio) · [AGP ↔ Gradle](https://developer.android.com/build/releases/gradle-plugin)

## Environment troubleshooting

Quick check:

```powershell
.\gradlew.bat help
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```

### Gradle IDE vs terminal

If terminal works but IDE sync fails:

```properties
kotlin.compiler.execution.strategy=in-process
```

Set Gradle JDK to JBR, then `.\gradlew.bat --stop`.

### Windows write permissions

If Studio shows **Write Permissions Issue**, run as Administrator:

```powershell
cd scripts\windows
.\fix-windows-studio-defender-admin.ps1 -ProjectPath "C:\Dev\Glucose-For-Watch"
```

Prefer `C:\Dev\Glucose-For-Watch` (no spaces) or a WSL UNC path over Desktop/OneDrive.

## Project layout

```
Glucose-For-Watch/
├── mobile/              # Phone APK
├── wear/                # Wear companion APK
├── core/                # model, datalayer-contract, testing
├── feature/             # sync, dexcom-share, watch-install
├── docs/
├── scripts/
└── toxy-ux-kit/         # Design tokens and specs
```

## Next steps

- [architecture.md](architecture.md) — sync flow and Data Layer contract
- [dexcom.md](../guide/dexcom.md) — G6/G7 compatibility
- [user.md](../guide/user.md) — end-user sideload guide
- [toxy-ux-kit/](../../toxy-ux-kit/README.md) — AGP colors and UI specs
- [CONTRIBUTING.md](../../CONTRIBUTING.md)
