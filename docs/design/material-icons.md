# Icônes officielles Android (Material)

Guide pour importer les icônes **officielles Google** dans Glucose For Watch (VectorDrawable XML, module `mobile/`).

---

## Sources officielles (par ordre recommandé)

| Source | URL | Format Android | Mise à jour |
|--------|-----|----------------|-------------|
| **Material Symbols (recommandé)** | [fonts.google.com/icons](https://fonts.google.com/icons) | Onglet **Android** → XML 24 dp | Actif (2026) |
| **Dépôt GitHub Google** | [github.com/google/material-design-icons](https://github.com/google/material-design-icons) | `symbols/android/{nom}/materialsymbols{style}/{nom}_24px.xml` | Actif |
| **Android Studio** | File → New → Vector Asset → *Material Icon* | VectorDrawable | Sync avec Google Fonts |
| Material Icons (legacy) | [fonts.google.com/icons?icon.set=Material+Icons](https://fonts.google.com/icons?icon.set=Material+Icons) | PNG / anciens XML 24×24 | Plus maintenu |

**Licence :** Apache 2.0 — usage commercial OK, attribution appréciée dans les notices open source.

**À éviter pour ce projet :**

| Option | Pourquoi |
|--------|----------|
| `androidx.compose.material:material-icons-extended` | ~18 Mo, bibliothèque Compose dépréciée ; le home mobile est en XML Views |
| Cloner tout le repo (~310 Mo ZIP) | Trop lourd — importer **icône par icône** |

---

## Chemin canonique dans le repo Google

```
symbols/android/
  └── {icon_name}/           # ex. bluetooth, battery_saver, chevron_right
        ├── materialsymbolsoutlined/   ← style par défaut (recommandé)
        ├── materialsymbolsrounded/
        └── materialsymbolssharp/
              └── {icon_name}_24px.xml
```

Exemple brut :

```
https://raw.githubusercontent.com/google/material-design-icons/master/symbols/android/bluetooth/materialsymbolsoutlined/bluetooth_24px.xml
```

Variantes : `{nom}_fill1_24px.xml` (rempli), tailles 20/40/48 px.

---

## État actuel du projet

Les drawables `mobile/src/main/res/drawable/ic_*_24.xml` reprennent déjà des tracés **Material Icons classiques** (viewport `24×24`), copiés à la main.

Les **Material Symbols** officiels (2022+) utilisent souvent viewport `960×960` — même rendu 24 dp, paths différents, rendu plus cohérent avec Android 14/15.

| Drawable projet | Nom Material Symbol | Style suggéré |
|-----------------|---------------------|---------------|
| `ic_bluetooth_24` | `bluetooth` | outlined |
| `ic_battery_24` | `battery_full` | outlined |
| `ic_battery_saver_24` | `battery_saver` | outlined |
| `ic_refresh_24` | `refresh` | outlined |
| `ic_watch_24` | `aod_watch` ou chercher sur [fonts.google.com/icons](https://fonts.google.com/icons) | outlined |
| `ic_share_24` | `share` | outlined |
| `ic_lock_24` | `lock` | outlined |
| `ic_settings_24` | `settings` | outlined |
| `ic_chevron_forward_24` | `chevron_right` | outlined |
| `ic_back_arrow` | `arrow_back` | outlined |
| `ic_more_vert_24` | `more_vert` | outlined |
| `ic_watch_install` | `install_mobile` ou `watch` | outlined |

**Ne pas remplacer :** `ic_brand_mark`, `ic_launcher_*`, `ic_sensor_glucose` (marque / métier).

---

## Import rapide (script)

Depuis la racine du repo :

```powershell
# Une icône → mobile/src/main/res/drawable/
.\scripts\dev\import-material-icon.ps1 -IconName chevron_right -OutName ic_chevron_forward_24

# Lister les styles disponibles pour une icône
.\scripts\dev\import-material-icon.ps1 -IconName watch -ListOnly
```

Le script :

1. Télécharge le XML depuis le dépôt **google/material-design-icons**
2. Remplace `@android:color/white` par `@color/wg7_icon_tint`
3. Supprime `android:tint="?attr/colorControlNormal"` (le projet teinte via `fillColor` + layouts)

---

## Import manuel (Android Studio)

1. **File → New → Vector Asset**
2. **Asset type :** Clip Art → **Material Icon**
3. Choisir l’icône, size **24 dp**, couleur noire
4. **Next** → enregistrer sous `ic_{nom}_24.xml`
5. Ouvrir le XML et mettre `android:fillColor="@color/wg7_icon_tint"`

Ou depuis Google Fonts : rechercher l’icône → onglet **Android** → télécharger le XML.

---

## Convention projet

- Préfixe : `ic_`
- Suffixe taille : `_24` pour les lignes settings / toolbar
- Couleur : `@color/wg7_icon_tint` (pas de hex dans le drawable)
- `android:contentDescription` : toujours via `@string/…` dans les layouts

---

## Wear OS

Le module `wear/` utilise Compose pour l’écran launcher ; les tuiles/complications utilisent des drawables XML. Même pipeline : importer en `wear/src/main/res/drawable/` si besoin.

---

## Références

- [Material Symbols — Google Fonts](https://fonts.google.com/icons)
- [Material Icons developer guide](https://developers.google.com/fonts/docs/material_icons)
- [Vector Asset Studio](https://developer.android.com/studio/write/vector-asset-studio)
- [Dépôt GitHub google/material-design-icons](https://github.com/google/material-design-icons)
