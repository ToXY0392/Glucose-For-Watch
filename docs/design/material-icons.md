# Official Android Icons (Material)

Guide for importing **official Google** icons into Glucose For Watch (VectorDrawable XML, `mobile/` module).

---

## Official sources (recommended order)

| Source | URL | Android format | Updates |
|--------|-----|----------------|-------------|
| **Material Symbols (recommended)** | [fonts.google.com/icons](https://fonts.google.com/icons) | **Android** tab → XML 24 dp | Active (2026) |
| **Google GitHub repository** | [github.com/google/material-design-icons](https://github.com/google/material-design-icons) | `symbols/android/{name}/materialsymbols{style}/{name}_24px.xml` | Active |
| **Android Studio** | File → New → Vector Asset → *Material Icon* | VectorDrawable | Synced with Google Fonts |
| Material Icons (legacy) | [fonts.google.com/icons?icon.set=Material+Icons](https://fonts.google.com/icons?icon.set=Material+Icons) | PNG / legacy 24×24 XML | No longer maintained |

**License:** Apache 2.0 — commercial use OK, attribution appreciated in open source notices.

**Avoid for this project:**

| Option | Why |
|--------|----------|
| `androidx.compose.material:material-icons-extended` | ~18 MB, deprecated Compose library; mobile home is XML Views |
| Cloning the entire repo (~310 MB ZIP) | Too heavy — import **icon by icon** |

---

## Canonical path in the Google repo

```
symbols/android/
  └── {icon_name}/           # e.g. bluetooth, battery_saver, chevron_right
        ├── materialsymbolsoutlined/   ← default style (recommended)
        ├── materialsymbolsrounded/
        └── materialsymbolssharp/
              └── {icon_name}_24px.xml
```

Raw example:

```
https://raw.githubusercontent.com/google/material-design-icons/master/symbols/android/bluetooth/materialsymbolsoutlined/bluetooth_24px.xml
```

Variants: `{name}_fill1_24px.xml` (filled), sizes 20/40/48 px.

---

## Current project state

The drawables `mobile/src/main/res/drawable/ic_*_24.xml` already use **classic Material Icons** paths (viewport `24×24`), copied manually.

Official **Material Symbols** (2022+) often use viewport `960×960` — same 24 dp rendering, different paths, more consistent with Android 14/15.

| Project drawable | Material Symbol name | Suggested style |
|-----------------|---------------------|---------------|
| `ic_bluetooth_24` | `bluetooth` | outlined |
| `ic_battery_24` | `battery_full` | outlined |
| `ic_battery_saver_24` | `battery_saver` | outlined |
| `ic_refresh_24` | `refresh` | outlined |
| `ic_watch_24` | `aod_watch` or search on [fonts.google.com/icons](https://fonts.google.com/icons) | outlined |
| `ic_share_24` | `share` | outlined |
| `ic_lock_24` | `lock` | outlined |
| `ic_settings_24` | `settings` | outlined |
| `ic_chevron_forward_24` | `chevron_right` | outlined |
| `ic_back_arrow` | `arrow_back` | outlined |
| `ic_more_vert_24` | `more_vert` | outlined |
| `ic_watch_install` | `install_mobile` or `watch` | outlined |

**Do not replace:** `ic_brand_mark`, `ic_launcher_*`, `ic_sensor_glucose` (brand / domain-specific).

---

## Quick import (script)

From the repo root:

```powershell
# One icon → mobile/src/main/res/drawable/
.\scripts\dev\import-material-icon.ps1 -IconName chevron_right -OutName ic_chevron_forward_24

# List available styles for an icon
.\scripts\dev\import-material-icon.ps1 -IconName watch -ListOnly
```

The script:

1. Downloads the XML from the **google/material-design-icons** repository
2. Replaces `@android:color/white` with `@color/wg7_icon_tint`
3. Removes `android:tint="?attr/colorControlNormal"` (the project tints via `fillColor` + layouts)

---

## Manual import (Android Studio)

1. **File → New → Vector Asset**
2. **Asset type:** Clip Art → **Material Icon**
3. Choose the icon, size **24 dp**, black color
4. **Next** → save as `ic_{name}_24.xml`
5. Open the XML and set `android:fillColor="@color/wg7_icon_tint"`

Or from Google Fonts: search the icon → **Android** tab → download the XML.

---

## Project convention

- Prefix: `ic_`
- Size suffix: `_24` for settings rows / toolbar
- Color: `@color/wg7_icon_tint` (no hex in the drawable)
- `android:contentDescription`: always via `@string/…` in layouts

---

## Wear OS

The `wear/` module uses Compose for the launcher screen; tiles/complications use XML drawables. Same pipeline: import into `wear/src/main/res/drawable/` if needed.

---

## References

- [Material Symbols — Google Fonts](https://fonts.google.com/icons)
- [Material Icons developer guide](https://developers.google.com/fonts/docs/material_icons)
- [Vector Asset Studio](https://developer.android.com/studio/write/vector-asset-studio)
- [GitHub repository google/material-design-icons](https://github.com/google/material-design-icons)
