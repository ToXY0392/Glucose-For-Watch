#!/usr/bin/env bash
# Deploy signed release APKs to the connected phone + Wear OS watch.
# Uses Gradle standard release outputs only (never debug).
# Usage:
#   bash ./scripts/release/deploy_release.sh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

PACKAGE_NAME="com.glucoseforwatch.mobile"

# Standard Gradle / Android Studio signed-APK destination:
#   <module>/build/outputs/apk/release/
resolve_release_apk() {
  local module="$1"
  local dir="${module}/build/outputs/apk/release"
  local preferred="${dir}/${module}-release.apk"

  if [[ ! -d "$dir" ]]; then
    echo "[gfw] Missing release output dir: $dir" >&2
    echo "[gfw] Build signed APKs in Android Studio (Build > Generate Signed Bundle / APK)," >&2
    echo "[gfw] then re-run this script. Do not use debug APKs." >&2
    exit 1
  fi

  local path=""
  if [[ -f "$preferred" ]]; then
    path="$preferred"
  else
    # Android Studio may emit a differently named *.apk under the same folder.
    path="$(find "$dir" -maxdepth 1 -type f -name '*.apk' ! -name '*unsigned*' | head -n 1 || true)"
  fi

  if [[ -z "$path" || ! -f "$path" ]]; then
    echo "[gfw] No release APK under $dir" >&2
    echo "[gfw] Expected: $preferred" >&2
    echo "[gfw] Do not use debug APKs — this script refuses them." >&2
    exit 1
  fi

  # Hard refuse any path that looks like a debug artifact.
  if [[ "$path" == *debug* ]]; then
    echo "[gfw] Refusing debug APK: $path" >&2
    exit 1
  fi

  printf '%s' "$path"
}

resolve_adb() {
  if command -v adb >/dev/null 2>&1; then
    command -v adb
    return
  fi
  if command -v adb.exe >/dev/null 2>&1; then
    command -v adb.exe
    return
  fi

  local sdk=""
  if [[ -f local.properties ]]; then
    # local.properties: sdk.dir=C\:\\Users\\...  →  C:/Users/...
    sdk="$(
      sed -n 's/^sdk\.dir=//p' local.properties | head -n 1 |
        sed 's/\\\([:\\]\)/\1/g' | tr '\\' '/'
    )"
  fi
  if [[ -z "$sdk" && -n "${ANDROID_HOME:-}" ]]; then
    sdk="$ANDROID_HOME"
  fi
  if [[ -z "$sdk" && -n "${ANDROID_SDK_ROOT:-}" ]]; then
    sdk="$ANDROID_SDK_ROOT"
  fi

  # Git Bash / MSYS: C:/Users/... → /c/Users/...
  if [[ "$sdk" =~ ^[A-Za-z]:/ ]]; then
    local drive="${sdk:0:1}"
    local rest="${sdk:2}"
    drive="$(echo "$drive" | tr '[:upper:]' '[:lower:]')"
    sdk="/${drive}${rest}"
  fi

  for candidate in \
    "${sdk}/platform-tools/adb" \
    "${sdk}/platform-tools/adb.exe"; do
    if [[ -n "$sdk" && -x "$candidate" ]]; then
      printf '%s' "$candidate"
      return
    fi
  done

  echo "[gfw] adb not found in PATH or SDK platform-tools" >&2
  exit 1
}

ADB="$(resolve_adb)"

MOBILE_APK="$(resolve_release_apk mobile)"
WEAR_APK="$(resolve_release_apk wear)"

echo "[gfw] Package: $PACKAGE_NAME"
echo "[gfw] Mobile APK: $MOBILE_APK"
echo "[gfw] Wear APK:   $WEAR_APK"
echo

# Prefer hardware serials; skip mDNS aliases that duplicate the same device.
mapfile -t SERIALS < <(
  "$ADB" devices | tr -d '\r' | awk 'NR > 1 && $2 == "device" && $1 !~ /\._adb-tls-connect\._tcp$/ { print $1 }'
)

if [[ ${#SERIALS[@]} -eq 0 ]]; then
  echo "[gfw] No online adb devices (state=device). Connect phone + watch and retry." >&2
  exit 1
fi

PHONE_SERIAL=""
WATCH_SERIAL=""

echo "[gfw] Detecting devices..."
for serial in "${SERIALS[@]}"; do
  chars="$("$ADB" -s "$serial" shell getprop ro.build.characteristics 2>/dev/null | tr -d '\r')"
  echo "  - $serial  characteristics=$chars"
  if [[ "$chars" == *watch* ]]; then
    if [[ -n "$WATCH_SERIAL" ]]; then
      echo "[gfw] Multiple watches detected ($WATCH_SERIAL and $serial). Aborting." >&2
      exit 1
    fi
    WATCH_SERIAL="$serial"
  else
    if [[ -n "$PHONE_SERIAL" ]]; then
      echo "[gfw] Multiple non-watch devices detected ($PHONE_SERIAL and $serial). Aborting." >&2
      exit 1
    fi
    PHONE_SERIAL="$serial"
  fi
done

if [[ -z "$PHONE_SERIAL" ]]; then
  echo "[gfw] Phone not detected (no non-watch device online)." >&2
  exit 1
fi
if [[ -z "$WATCH_SERIAL" ]]; then
  echo "[gfw] Watch not detected (no device with 'watch' in ro.build.characteristics)." >&2
  exit 1
fi

echo
echo "[gfw] Phone: $PHONE_SERIAL"
echo "[gfw] Watch: $WATCH_SERIAL"
echo

uninstall_pkg() {
  local serial="$1"
  local label="$2"
  echo "[gfw] Uninstall $PACKAGE_NAME on $label ($serial)..."
  # Continue if the package is not already installed.
  if "$ADB" -s "$serial" uninstall "$PACKAGE_NAME"; then
    echo "  -> removed"
  else
    echo "  -> not installed (ok)"
  fi
}

uninstall_pkg "$PHONE_SERIAL" "phone"
uninstall_pkg "$WATCH_SERIAL" "watch"

echo
echo "[gfw] Installing mobile release on phone..."
"$ADB" -s "$PHONE_SERIAL" install -r "$MOBILE_APK"

echo "[gfw] Installing wear release on watch..."
"$ADB" -s "$WATCH_SERIAL" install -r "$WEAR_APK"

echo
echo "[gfw] Release deploy complete."
echo "  phone ($PHONE_SERIAL) <- $MOBILE_APK"
echo "  watch ($WATCH_SERIAL) <- $WEAR_APK"
