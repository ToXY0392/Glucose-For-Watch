#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

echo "[gfw] CI verification started"
python3 scripts/dev/lint_agp_colors.py
python3 scripts/dev/check_docs_links.py
# Unit tests compile main sources; Android lint fails on errors only.
./gradlew \
  :core:model:testDebugUnitTest \
  :core:datalayer-contract:testDebugUnitTest \
  :feature:dexcom-share:testDebugUnitTest \
  :feature:sync:testDebugUnitTest \
  :feature:watch-install:testDebugUnitTest \
  :mobile:testDebugUnitTest \
  :wear:testDebugUnitTest \
  :mobile:lintDebug \
  :wear:lintDebug
echo "[gfw] CI verification finished"
