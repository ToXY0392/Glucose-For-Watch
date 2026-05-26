#!/usr/bin/env bash
# Regenerate docs/assets/glucose-for-watch-architecture.png from SVG (AUTO-8).
set -eu

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

SVG="docs/assets/glucose-for-watch-architecture.svg"
PNG="docs/assets/glucose-for-watch-architecture.png"

if [[ ! -f "$SVG" ]]; then
  echo "[gfw] ERROR: missing $SVG" >&2
  exit 1
fi

npx --yes @resvg/resvg-js-cli --fit-width 1800 "$SVG" "$PNG"
echo "[gfw] Wrote $PNG"
