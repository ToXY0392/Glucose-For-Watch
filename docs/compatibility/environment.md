# Environment compatibility

> **Last updated:** 2026-05-23

## Recommended environment

| Component | Version / note |
|-----------|----------------|
| Android Studio | Recent stable |
| Gradle wrapper | **9.4.1** |
| Android Gradle Plugin | **9.2.1** |
| Kotlin | **2.3.20** |
| JDK (Gradle) | JBR 21 (Android Studio bundled) |
| compileSdk | **36** |
| Targets | Android phone + Wear OS watch |

## Quick verification

```powershell
.\gradlew.bat help
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```

`BUILD SUCCESSFUL` confirms a compatible environment.

---

## Windows: Android Studio write permissions

If Studio shows **"Write Permissions Issue"** after `attrib` / `icacls`, the usual cause is **Windows Defender** (Controlled folder access / real-time scan), not NTFS alone.

### Fix (PowerShell as Administrator)

```powershell
cd "scripts\windows"
.\fix-windows-studio-defender-admin.ps1 -ProjectPath "C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7"
```

This script:

- Allows `studio64.exe` if Controlled Folder Access is active
- Adds exclusions for the repo, `%USERPROFILE%\.gradle`, and Android Studio config folders

Then **close all Studio instances**, reopen, **File → Open** on the project folder.

**Prefer:** `C:\Dev\Widget-G7` (no spaces) or `\\wsl$\Ubuntu\...\Widget-G7` over Desktop/OneDrive paths.

### Still failing?

1. Run diagnostic (non-admin):
   ```powershell
   .\diagnose-windows-studio-project-path.ps1 -ProjectPath "C:\Dev\Widget-G7"
   ```
2. Clone to `C:\Dev\Widget-G7` without spaces
3. Test Studio as Administrator (diagnostic only)
4. Launch with project argument:
   ```powershell
   .\launch-android-studio-with-project.ps1 -ProjectPath "C:\Dev\Widget-G7"
   ```

Full Windows scripts: `scripts/windows/`

---

## Gradle IDE vs terminal

If terminal `./gradlew` succeeds but IDE sync fails:

```properties
# gradle.properties (project)
kotlin.compiler.execution.strategy=in-process
```

```powershell
.\gradlew.bat --stop
```

Set **Gradle JDK** to embedded JBR in Studio settings.

---

## Dual IDE

See [Dual IDE setup](../development/dual-ide-setup.md).

---

## Related

- [Android Studio guide](../development/android-studio.md)
- Root summary: [COMPATIBILITY.md](../../COMPATIBILITY.md)
