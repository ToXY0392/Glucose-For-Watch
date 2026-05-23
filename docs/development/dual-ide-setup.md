# Dual IDE setup (Cursor + Android Studio)

> **Last updated:** 2026-05-23

Work on **one Git copy** from both Cursor (WSL) and Android Studio (Windows).

---

## Principle

| Tool | Opens |
|------|-------|
| **Cursor** | `/home/<user>/.../Widget-G7` (WSL remote) |
| **Android Studio (Windows)** | `\\wsl.localhost\Ubuntu\home\<user>\...\Widget-G7` or `\\wsl$\Ubuntu\...` |

Do **not** maintain a second clone on `C:\Users\...\Desktop` — it causes drift and permission issues.

---

## Example paths

| OS | Path |
|----|------|
| Linux (WSL) | `/home/toxy/dossierlinux/PROJECTS/Widget-G7` |
| Windows UNC | `\\wsl.localhost\Ubuntu\home\toxy\dossierlinux\PROJECTS\Widget-G7` |

List WSL distros: `wsl -l -v`

---

## `local.properties` and SDK

One `sdk.dir` per file — choose based on who runs Gradle:

| Context | Typical `sdk.dir` |
|---------|-------------------|
| `./gradlew` in WSL terminal | Linux path, e.g. `/home/user/Android/Sdk` |
| Gradle sync from Studio on `\\wsl$\` | Windows path, e.g. `C\:\\Users\\...\\Android\\Sdk` |

You may need to switch temporarily when alternating primary build environment.

---

## Launch Studio from WSL

```bash
./scripts/dev/open-android-studio-wsl-project.sh
```

Windows script:
```powershell
.\scripts\windows\launch-android-studio-with-project.ps1 -ProjectPath "\\wsl.localhost\Ubuntu\..."
```

---

## Best practices

1. **Avoid concurrent heavy Gradle syncs** in Cursor + Studio
2. After large Gradle/Kotlin changes, sync in **one** IDE only
3. Use **LF** line endings consistently
4. `.idea` (Studio), `.cursor` (Cursor), `.vscode` can coexist
5. Deploy after mobile/wear changes: `./gradlew installWidgetG7Debug`

---

## Related

- [Android Studio guide](android-studio.md)
- [Environment compatibility](../compatibility/environment.md)
- Cursor rule: `.cursor/rules/widget-g7-dual-ide-wsl.mdc`
