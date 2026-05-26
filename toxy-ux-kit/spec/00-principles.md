# Design principles

> ToXY UX Kit · v0.1.0

## 1. Two layers, never mixed

| Layer | Token prefix | Used for |
|-------|--------------|----------|
| **Medical (AGP)** | `agp.glucose.*` | Numeric glucose, trend arrows (when fresh), complication fill |
| **Chrome (ToXY)** | `toxy.*` | Everything else |

## 2. Dark-first

Optimized for night use and OLED watches. No light theme in v1.

## 3. Clinical clarity over brand

Users must instantly recognize hypo / in-range / hyper using **standard CGM colors**, not brand mint.

## 4. Minimal watch surfaces

One primary action per tile (sync). Value + unit + trend + sync — nothing else.

## 5. Observable sync

Connection state must be visible on phone (ToXY semantic colors). Never hide watch offline silently.

## 6. Accessibility

- Minimum 48 dp touch targets on Wear
- Do not rely on color alone — show numeric value or LOW/HI text
- WCAG AA contrast for text on surfaces

## 7. Token-driven

All values live in `tokens/*.json`. Figma variables and Android XML are **exports**, not sources of truth.

## Next

- [AGP medical layer](01-agp-medical-layer.md)
- [ToXY chrome layer](02-toxy-chrome-layer.md)
