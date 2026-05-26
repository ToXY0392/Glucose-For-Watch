#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

SKIP_CHECKS=0
REQUIRE_DIAGNOSTICS=0

while [[ $# -gt 0 ]]; do
  case "$1" in
    --skip-checks)
      SKIP_CHECKS=1
      shift
      ;;
    --full)
      SKIP_CHECKS=0
      REQUIRE_DIAGNOSTICS=1
      shift
      ;;
    *)
      echo "[gfw] Unknown option: $1" >&2
      echo "Usage: bash ./scripts/dev/run_e2e_bundle.sh [--skip-checks|--full]" >&2
      exit 1
      ;;
  esac
done

if [[ "$SKIP_CHECKS" -eq 1 && "$REQUIRE_DIAGNOSTICS" -eq 1 ]]; then
  echo "[gfw] --skip-checks and --full are incompatible" >&2
  exit 1
fi

echo "[gfw] Preparing evidence pack"
EVIDENCE_DIR="$(bash ./scripts/dev/prepare_e2e_evidence_pack.sh | tail -n 1)"
export WIDGETG7_E2E_EVIDENCE_DIR="$EVIDENCE_DIR"

if [[ -n "${WIDGETG7_PHONE_SERIAL:-}" && -n "${WIDGETG7_WATCH_SERIAL:-}" ]]; then
  echo "[gfw] Collecting diagnostics pack"
  DIAGNOSTICS_DIR="$(bash ./scripts/dev/collect_sync_diagnostics.sh | tail -n 1)"
  export WIDGETG7_DIAGNOSTICS_DIR="$DIAGNOSTICS_DIR"
else
  if [[ "$REQUIRE_DIAGNOSTICS" -eq 1 ]]; then
    echo "[gfw] --full requires diagnostics; set WIDGETG7_PHONE_SERIAL and WIDGETG7_WATCH_SERIAL" >&2
    exit 1
  fi
  echo "[gfw] Diagnostics skipped (set WIDGETG7_PHONE_SERIAL and WIDGETG7_WATCH_SERIAL)"
fi

echo "[gfw] Finalizing closure pack"
if [[ "$SKIP_CHECKS" -eq 1 ]]; then
  CLOSURE_DIR="$(bash ./scripts/dev/finalize_e2e_closure_pack.sh --skip-checks | tail -n 1)"
else
  CLOSURE_DIR="$(bash ./scripts/dev/finalize_e2e_closure_pack.sh | tail -n 1)"
fi

echo "[gfw] E2E bundle completed"
echo "evidence=$EVIDENCE_DIR"
echo "closure=$CLOSURE_DIR"
