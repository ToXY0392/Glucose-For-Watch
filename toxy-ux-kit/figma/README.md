# Figma — ToXY UX Kit

> v0.1.0 · Standalone from Android app modules

## Recommended setup

### 1. Base kits (Google official)

Download and keep as **linked libraries** (do not merge into ToXY file):

| Kit | URL |
|-----|-----|
| Wear OS — Apps | [Design for Wear OS](https://developer.android.com/design/ui/wear) |
| Wear OS — Tiles | [Tile design kit](https://developer.android.com/design/ui/wear/guides/surfaces/tiles/bestpractices) |
| Material 3 | [Material Design 3](https://m3.material.io/) |

### 2. Create ToXY Figma file

Suggested structure:

```
ToXY UX Kit.fig
├── 📁 Cover & changelog
├── 📁 Variables
│   ├── Collection: ToXY Chrome
│   └── Collection: AGP Medical      ← separate!
├── 📁 Mobile
│   ├── Home
│   ├── Dexcom settings
│   └── Watch setup
├── 📁 Wear
│   ├── Tile (round 450×450)
│   └── Complication samples
└── 📁 Icons
    ├── Sync
    ├── Trend arrows
    └── Launcher
```

### 3. Import tokens

Two options:

**A — Manual (v0.1)**  
Copy hex values from:

- `tokens/toxy.color.json` → **ToXY Chrome** variables
- `tokens/agp.glucose.json` → **AGP Medical** variables

**B — Plugin (recommended v0.2+)**  
Use [Tokens Studio for Figma](https://tokens.studio/) or Figma Variables import from JSON.

See [token-import.md](token-import.md).

### 4. Reference mocks

PNG references in `../assets/references/`:

| File | Description |
|------|-------------|
| `tile_dial_reference.png` | Full tile layout |
| `ic_tile_refresh_button_reference.png` | Sync button placement |
| `tile_reading_preview.png` | Value + trend |

---

## Naming conventions

| Prefix | Example | Layer |
|--------|---------|-------|
| `toxy/` | `toxy/color/accent` | Chrome |
| `agp/` | `agp/glucose/inRange` | Medical |

---

## Review checklist

- [ ] Two variable collections (Chrome vs Medical) — not one merged palette
- [ ] No mint `#34D399` on glucose numbers in any frame
- [ ] Tile sync button ≥ 48×48 dp
- [ ] AGP green/yellow/red/orange match `tokens/agp.glucose.json`

---

## Publish

When Figma file is ready, add link here:

```
Figma file: (pending — add URL)
```

Update `kit.manifest.json` → `documentation.figmaUrl`
