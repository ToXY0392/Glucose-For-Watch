# ToXY chrome layer

> Brand UI specification · tokens: `tokens/toxy.color.json`, `toxy.typography.json`, etc.

## Scope

Everything that is **not** a glucose numeric reading or clinical trend color.

---

## Color roles

| Role | Token | Hex |
|------|-------|-----|
| Canvas top | `toxy.color.background.top` | `#0D1117` |
| Canvas bottom | `toxy.color.background.bottom` | `#0F172A` |
| Card | `toxy.color.surface.default` | `#111827` |
| Card elevated | `toxy.color.surface.alt` | `#1E293B` |
| Border | `toxy.color.outline` | `#334155` |
| Text primary | `toxy.color.text.primary` | `#F8FAFC` |
| Text secondary | `toxy.color.text.secondary` | `#94A3B8` |
| Brand accent | `toxy.color.accent.default` | `#34D399` |
| Sync OK | `toxy.color.sync.ok` | `#4ADE80` |
| Sync OK pill | `toxy.color.sync.okSoft` / `okStroke` | `#14532D` / `#334ADE80` |
| Sync warn | `toxy.color.sync.warn` | `#FB923C` |
| Sync warn pill | `toxy.color.sync.warnSoft` | `#422006` |
| Sync error | `toxy.color.sync.error` | `#F87171` |
| Sync error pill | `toxy.color.sync.errorSoft` | `#3F1818` |

---

## Typography (chrome only)

| Style | Size | Weight | Color |
|-------|------|--------|-------|
| Section title | 20 sp | Medium | text.primary |
| Body | 16 sp | Regular | text.secondary |
| Caption | 14 sp | Regular | text.tertiary |
| Unit `mg/dL` | 18 sp | Bold | text.secondary |

Glucose value typography sizes are in `toxy.typography.json` but **colors come from AGP layer**.

---

## Components using chrome only

- Primary / secondary buttons
- Navigation header
- Dexcom settings form fields
- Watch setup wizard cards
- Sync status pills (semantic: ok/warn/error)
- Tile background and sync edge button icon tint

---

## Glow & aurora (mobile hero)

Decorative only — never on glucose numbers:

- `toxy.color.overlay.glowStart/Center/End`
- Aurora gradient on home hero card

---

## Figma

Collection name: **「ToXY Chrome v0.1」**  
Base: Material 3 Dark + Wear OS Design Kit

See [figma/README.md](../figma/README.md)
