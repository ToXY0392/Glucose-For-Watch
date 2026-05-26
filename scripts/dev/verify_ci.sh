#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

echo "[widget-g7] CI verification started"
python3 toxy-ux-kit/tools/tokens-validate.py
python3 toxy-ux-kit/tools/lint-agp-colors.py
python3 scripts/dev/check_docs_links.py
./gradlew \
  :core:model:testDebugUnitTest \
  :feature:dexcom-share:assembleDebug \
  :feature:dexcom-share:testDebugUnitTest \
  :feature:sync:testDebugUnitTest \
  :feature:watch-install:assembleDebug \
  :mobile:testDebugUnitTest \
  :wear:testDebugUnitTest \
  :mobile:compileDebugKotlin \
  :wear:compileDebugKotlin
echo "[widget-g7] CI verification finished"
