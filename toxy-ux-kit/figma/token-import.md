# Import design tokens into Figma

## Variable collections

Create **two** collections — never merge medical and chrome.

### Collection 1: `ToXY Chrome`

| Variable | Type | Value (from JSON path) |
|----------|------|------------------------|
| background/top | Color | `toxy.color.background.top` |
| background/bottom | Color | `toxy.color.background.bottom` |
| surface/default | Color | `toxy.color.surface.default` |
| surface/alt | Color | `toxy.color.surface.alt` |
| text/primary | Color | `toxy.color.text.primary` |
| text/secondary | Color | `toxy.color.text.secondary` |
| accent/default | Color | `toxy.color.accent.default` |
| sync/ok | Color | `toxy.color.sync.ok` |
| sync/warn | Color | `toxy.color.sync.warn` |
| sync/error | Color | `toxy.color.sync.error` |

Source file: `../tokens/toxy.color.json`

### Collection 2: `AGP Medical`

| Variable | Type | Value |
|----------|------|-------|
| glucose/veryLow | Color | `#9C0000` |
| glucose/low | Color | `#E00000` |
| glucose/inRange | Color | `#008000` |
| glucose/high | Color | `#FFCC00` |
| glucose/veryHigh | Color | `#FF9900` |
| glucose/unknown | Color | `#64748B` |

Source file: `../tokens/agp.glucose.json`

---

## Tokens Studio (optional)

1. Install [Tokens Studio for Figma](https://tokens.studio/)
2. Sync folder: point to `toxy-ux-kit/tokens/`
3. Map `$value` fields to Figma variables
4. Use **themes** to switch Chrome vs Medical if needed

---

## Material Theme Builder

Use **only** for ToXY chrome palette exploration — **not** for glucose ranges.

Export dark scheme → compare with `toxy.color.json` → adjust tokens JSON → re-import Figma.

URL: https://material-foundation.github.io/material-theme-builder/

---

## Spacing & typography in Figma

| Token file | Figma type |
|------------|------------|
| `toxy.spacing.json` | Number variables (dp → px at 1×: 1dp ≈ 1px for mockups) |
| `toxy.typography.json` | Text styles |
| `toxy.shape.json` | Corner radius variables |

---

## After token update

1. Bump `tokens/meta.json` version
2. Update `CHANGELOG.md`
3. Run `python ../tools/export-android-colors.py`
4. Notify app team for XML sync
