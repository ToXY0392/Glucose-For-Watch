#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

echo "[gfw] Phase 5 E2E dry-run started"

bash ./scripts/dev/verify_ci.sh
bash ./scripts/release/verify_release_artifacts.sh
bash ./scripts/release/check_legal_placeholders.sh

echo "[gfw] Optional diagnostics collection"
echo " - set WIDGETG7_PHONE_SERIAL and WIDGETG7_WATCH_SERIAL"
echo " - run: bash ./scripts/dev/collect_sync_diagnostics.sh"

echo "[gfw] Phase 5 E2E dry-run completed"
