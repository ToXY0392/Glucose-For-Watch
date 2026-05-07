#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

echo "[widget-g7] Release verification started"
./gradlew :mobile:assembleRelease :wear:assembleRelease

MOBILE_APK="mobile/build/outputs/apk/release/mobile-release.apk"
WEAR_APK="wear/build/outputs/apk/release/wear-release.apk"

if [[ ! -f "$MOBILE_APK" ]]; then
  echo "[widget-g7] Missing artifact: $MOBILE_APK" >&2
  exit 1
fi

if [[ ! -f "$WEAR_APK" ]]; then
  echo "[widget-g7] Missing artifact: $WEAR_APK" >&2
  exit 1
fi

echo "[widget-g7] Release artifacts verified"
echo " - $MOBILE_APK"
echo " - $WEAR_APK"
