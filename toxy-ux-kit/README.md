# ToXY UX Kit

**Standalone design system** for Widget G7 — independent from the Android app (`mobile/`, `wear/`).

| Layer | Namespace | Purpose |
|-------|-----------|---------|
| **Chrome** | `toxy.*` | Brand UI: backgrounds, buttons, navigation, sync indicators |
| **Medical** | `agp.glucose.*` | AGP / TIR glucose range colors — **never** use `toxy.accent` on glucose values |

```
toxy-ux-kit/          ← you are here (design source of truth)
├── tokens/           ← machine-readable design tokens (JSON)
├── spec/             ← component & pattern specifications
├── figma/            ← Figma setup guide
├── assets/           ← reference PNGs for designers
└── tools/            ← export scripts → Android XML

mobile/  wear/        ← app consumes tokens (generated or hand-synced)
```

---

## Why separate from the app?

1. **Designers** work in Figma / tokens without opening Gradle modules
2. **Medical colors** stay isolated from brand palette (AGP compliance)
3. **Versioning** — kit can bump `0.2.0` while app stays `0.3.1`
4. **Future reuse** — other ToXY surfaces (web, iOS) can consume the same tokens
5. **Optional extraction** — this folder can become its own git repo later

---

## Quick start

### Designers

1. Read [spec/00-principles.md](spec/00-principles.md)
2. Open [design-reference/index.html](design-reference/index.html) in a browser (or run `py -3 tools/export-design-reference.py`)
3. Optional Figma: [figma/FIGMA-HANDOFF.md](figma/FIGMA-HANDOFF.md)

### Android developers

1. Edit tokens in `tokens/` (not hard-coded in `mobile/res/values/colors.xml`)
2. Export to app:

   ```bash
   py -3 toxy-ux-kit/tools/export-figma-tokens.py   # Figma / Tokens Studio
   python toxy-ux-kit/tools/export-android-colors.py
   ```

3. Review generated files under `toxy-ux-kit/tools/export/output/`
4. Copy approved output into `mobile/src/main/res/values/` when ready

### Token files

| File | Content |
|------|---------|
| [tokens/toxy.color.json](tokens/toxy.color.json) | Brand chrome colors |
| [tokens/agp.glucose.json](tokens/agp.glucose.json) | **Medical** glucose range colors + thresholds |
| [tokens/toxy.typography.json](tokens/toxy.typography.json) | Type scale |
| [tokens/toxy.spacing.json](tokens/toxy.spacing.json) | Layout spacing |
| [tokens/toxy.shape.json](tokens/toxy.shape.json) | Corner radii |
| [tokens/toxy.motion.json](tokens/toxy.motion.json) | Durations, tile freshness |

Manifest: [kit.manifest.json](kit.manifest.json)

---

## Rules (non-negotiable)

1. Glucose **numbers** → `agp.glucose.*` only
2. Sync button, cards, nav → `toxy.color.*`
3. Stale data (`--`, grey trend) → `agp.glucose.unknown`
4. `LOW` / `HI` sentinels → `agp.glucose.low` / `agp.glucose.veryHigh`

Full spec: [spec/01-agp-medical-layer.md](spec/01-agp-medical-layer.md)

---

## Component specs

| Spec | Platform |
|------|----------|
| [mobile-home.md](spec/components/mobile-home.md) | Phone |
| [wear-tile.md](spec/components/wear-tile.md) | Wear OS tile |
| [wear-complication.md](spec/components/wear-complication.md) | Watch face |
| [sync-states.md](spec/components/sync-states.md) | Phone + watch |

---

## Versioning

See [CHANGELOG.md](CHANGELOG.md). Kit version is **independent** from app `versionName`.

| Kit version | App integration |
|-------------|-----------------|
| 0.1.0 | Spec + tokens; app still uses legacy `wg7_*` colors |

---

## Related app documentation

Mirror / summary in the main repo (points here):

- [docs/design/README.md](../docs/design/README.md)
- [docs/design/glucose-color-standard.md](../docs/design/glucose-color-standard.md)

---

## External references

- [Wear OS Design Kit — Tiles](https://developer.android.com/design/ui/wear/guides/surfaces/tiles/bestpractices)
- [Wear OS Design Kit — Apps](https://developer.android.com/design/ui/wear)
- [AGP Report](https://www.agpreport.org/)
- [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)
