# Figma handoff — ToXY UX Kit v0.1

Complete this checklist to close Phase **3.1**. Repo deliverables are ready; the `.fig` file lives in Figma (add URL when done).

---

## 1. Prerequisites (15 min)

1. Install [Tokens Studio for Figma](https://tokens.studio/)
2. Link Google libraries (do not merge):
   - [Wear OS design](https://developer.android.com/design/ui/wear)
   - [Material 3](https://m3.material.io/)
3. Generate token JSON (from repo root):

   ```bash
   python toxy-ux-kit/tools/export-figma-tokens.py
   ```

4. Tokens Studio → **Settings → Sync provider → Local** → folder:
   `toxy-ux-kit/figma/tokens-studio/`

5. Confirm **two** sets appear: `toxy-chrome` and `agp-medical`

---

## 2. Figma file structure

Create file **`ToXY UX Kit v0.1`** with pages:

| Page | Frames |
|------|--------|
| **Cover** | Title, kit version, link to repo, changelog snippet |
| **Variables** | (managed by Tokens Studio — do not duplicate manually) |
| **Wear / Tile** | 450×450 round · see [wear-tile.md](../spec/components/wear-tile.md) |
| **Wear / Status** | 450×450 round · see [wear-status-screen.md](../spec/components/wear-status-screen.md) |
| **Wear / Complication** | SHORT_TEXT, LONG_TEXT, RANGED_VALUE samples |
| **Mobile / Home** | 360×800 · hero AGP + ToXY chrome cards |
| **Mobile / Sync states** | Pills: ok, warn, error, pending |
| **AGP reference** | Swatches 60 / 120 / 200 / 300 mg/dL with labels |

Reference PNGs: [`assets/references/`](../assets/references/)

---

## 3. Variable binding rules

| Layer | Collection | Example |
|-------|------------|---------|
| Backgrounds, buttons, nav | **toxy-chrome** | `color/background/top` |
| Glucose numerals, trend color | **agp-medical** | `glucose/inRange` |
| Unit `mg/dL` | **toxy-chrome** | `color/text/secondary` |

**Never** bind glucose values to `color/accent/default` (`#34D399`).

---

## 4. Frame checklist

Copy into Figma as component variants where useful.

### Wear tile

- [ ] Default (in-range 120 mg/dL, trend ↗)
- [ ] High (200 mg/dL, yellow AGP)
- [ ] Low (60 mg/dL, red AGP)
- [ ] Stale (unknown trend, muted)
- [ ] Sync button ≥ 48×48 dp touch target

### Wear status screen

- [ ] Fresh / stale / error / no-data (see spec)
- [ ] Sync button uses chrome primary, not AGP

### Mobile home

- [ ] Hero value AGP-colored
- [ ] Dexcom + Watch cards use `surface/default`
- [ ] Watch offline badge uses `sync/warn`

### Complication

- [ ] RANGED_VALUE bar 40–400 mg/dL
- [ ] Note: text tint controlled by watch face in production

---

## 5. Review gate

Before sharing with dev:

- [ ] Two token sets — not one merged palette
- [ ] Spot-check: no `#34D399` on glucose numbers in any frame
- [ ] Tile sync + status sync buttons ≥ 48 dp
- [ ] AGP hex matches `tokens/agp.glucose.json`

---

## 6. Publish

When the file is ready:

1. Set link in [`figma/README.md`](README.md) → `Figma file: https://…`
2. Update `kit.manifest.json`:

   ```json
   "documentation": {
     "figmaUrl": "https://www.figma.com/file/…"
   }
   ```

3. Mark Phase 3.1 ✅ in [`docs/plan/PROGRESS.md`](../../docs/plan/PROGRESS.md)

---

*Android implementation reference: ToXY v0.4.0 · `ToxyWearColorScheme` · `GlucoseRangeResolver`*
