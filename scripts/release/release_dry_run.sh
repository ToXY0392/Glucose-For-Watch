#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

echo "[widget-g7] Release dry-run started"

bash ./scripts/dev/verify_ci.sh
bash ./scripts/release/verify_release_artifacts.sh
bash ./scripts/release/check_legal_placeholders.sh

echo "[widget-g7] Release dry-run completed"
