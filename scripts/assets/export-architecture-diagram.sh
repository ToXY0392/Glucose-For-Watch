#!/usr/bin/env bash
# Regenerate docs/assets/glucose-for-watch-architecture.png from SVG (AUTO-8).
#
# Deterministic render: pinned resvg CLI + explicit DejaVu fonts, no system
# fonts. Otherwise the PNG changes whenever the machine (or the CI runner
# image) has different fonts for "Segoe UI, system-ui, sans-serif" / monospace.
#
# Fonts: Debian/Ubuntu `fonts-dejavu-core` (preinstalled on GitHub ubuntu-24.04).
# Override the directory with GFW_DIAGRAM_FONT_DIR.
set -eu

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

SVG="docs/assets/glucose-for-watch-architecture.svg"
PNG="docs/assets/glucose-for-watch-architecture.png"
RESVG_CLI="@resvg/resvg-js-cli@2.6.2-beta.1"
FONT_DIR="${GFW_DIAGRAM_FONT_DIR:-/usr/share/fonts/truetype/dejavu}"
FONTS=(DejaVuSans.ttf DejaVuSans-Bold.ttf DejaVuSansMono.ttf DejaVuSansMono-Bold.ttf)

if [[ ! -f "$SVG" ]]; then
  echo "[gfw] ERROR: missing $SVG" >&2
  exit 1
fi

font_args=()
for font in "${FONTS[@]}"; do
  if [[ ! -f "$FONT_DIR/$font" ]]; then
    echo "[gfw] ERROR: missing $FONT_DIR/$font — install fonts-dejavu-core (sudo apt install fonts-dejavu-core) or set GFW_DIAGRAM_FONT_DIR" >&2
    exit 1
  fi
  font_args+=(--font-file "$FONT_DIR/$font")
done

npx --yes "$RESVG_CLI" \
  --no-system-font "${font_args[@]}" \
  --font-default-family "DejaVu Sans" \
  --font-sans-serif-family "DejaVu Sans" \
  --font-monospace-family "DejaVu Sans Mono" \
  --fit-width 1800 "$SVG" "$PNG"
echo "[gfw] Wrote $PNG"
