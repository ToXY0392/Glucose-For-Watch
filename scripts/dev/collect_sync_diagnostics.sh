#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

PHONE_SERIAL="${GFW_PHONE_SERIAL:-}"
WATCH_SERIAL="${GFW_WATCH_SERIAL:-}"

if [[ -z "$PHONE_SERIAL" || -z "$WATCH_SERIAL" ]]; then
  echo "[gfw] Missing serials. Set GFW_PHONE_SERIAL and GFW_WATCH_SERIAL." >&2
  exit 1
fi

TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
OUT_DIR="build/diagnostics/$TIMESTAMP"
mkdir -p "$OUT_DIR"

echo "[gfw] Collecting diagnostics into $OUT_DIR"

adb -s "$PHONE_SERIAL" shell getprop > "$OUT_DIR/phone_getprop.txt"
adb -s "$WATCH_SERIAL" shell getprop > "$OUT_DIR/watch_getprop.txt"

adb -s "$PHONE_SERIAL" shell dumpsys activity services > "$OUT_DIR/phone_services.txt"
adb -s "$PHONE_SERIAL" shell dumpsys deviceidle > "$OUT_DIR/phone_deviceidle.txt"
adb -s "$WATCH_SERIAL" shell dumpsys battery > "$OUT_DIR/watch_battery.txt"

adb -s "$PHONE_SERIAL" logcat -d | grep "WG7" > "$OUT_DIR/phone_wg7_logcat.txt" || true
adb -s "$WATCH_SERIAL" logcat -d | grep "WG7" > "$OUT_DIR/watch_wg7_logcat.txt" || true

echo "[gfw] Diagnostics collection complete"
echo "$OUT_DIR"
