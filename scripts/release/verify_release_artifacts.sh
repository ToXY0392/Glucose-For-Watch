#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

echo "[widget-g7] Release verification started"
./gradlew :mobile:assembleRelease :wear:assembleRelease

find_release_apk() {
  local module="$1"
  local dir="${module}/build/outputs/apk/release"
  local apk=""

  for candidate in \
    "${dir}/${module}-release.apk" \
    "${dir}/${module}-release-unsigned.apk"; do
    if [[ -f "$candidate" ]]; then
      apk="$candidate"
      break
    fi
  done

  if [[ -z "$apk" ]]; then
    apk="$(find "$dir" -maxdepth 1 -name '*.apk' -type f 2>/dev/null | head -n 1 || true)"
  fi

  if [[ -z "$apk" || ! -f "$apk" ]]; then
    echo "[widget-g7] Missing release APK under $dir" >&2
    exit 1
  fi

  printf '%s' "$apk"
}

MOBILE_APK="$(find_release_apk mobile)"
WEAR_APK="$(find_release_apk wear)"

echo "[widget-g7] Release artifacts verified"
echo " - $MOBILE_APK"
echo " - $WEAR_APK"
