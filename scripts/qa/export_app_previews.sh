#!/usr/bin/env bash
# Export mobile home preview PNGs via Robolectric (AUTO-1).
# Usage: bash scripts/qa/export_app_previews.sh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

echo "[gfw] Exporting mobile home preview PNGs"
./gradlew :mobile:testDebugUnitTest \
  --tests "com.glucoseforwatch.mobile.preview.AppPreviewExporterTest"

PREVIEW_DIR="$ROOT_DIR/mobile/build/app-previews"
if ! compgen -G "$PREVIEW_DIR/*.png" > /dev/null; then
  echo "[gfw] ERROR: no PNG files in $PREVIEW_DIR" >&2
  exit 1
fi

echo "[gfw] PNG previews:"
ls -1 "$PREVIEW_DIR"/*.png

python3 "$ROOT_DIR/scripts/qa/generate_preview_gallery.py"

GALLERY="$ROOT_DIR/mobile/build/preview-gallery/index.html"
if [[ ! -f "$GALLERY" ]]; then
  echo "[gfw] ERROR: gallery not generated at $GALLERY" >&2
  exit 1
fi

echo "[gfw] Gallery: $GALLERY"
