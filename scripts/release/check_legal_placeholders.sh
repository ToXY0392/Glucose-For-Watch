#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

FILES=(
  "docs/CGU.md"
  "docs/POLITIQUE_CONFIDENTIALITE.md"
  "docs/LEGAL_PUBLICATION_CHECKLIST.md"
)

echo "[gfw] Checking legal placeholders"

missing_file=0
for file in "${FILES[@]}"; do
  if [[ ! -f "$file" ]]; then
    echo "[gfw] Missing legal file: $file" >&2
    missing_file=1
  fi
done
if [[ "$missing_file" -ne 0 ]]; then
  exit 1
fi

if grep -nE "\\[À compléter\\]|\\[A completer\\]|\\[À completer\\]" "${FILES[@]}"; then
  if [[ "${ALLOW_INCOMPLETE_LEGAL:-0}" == "1" ]]; then
    echo "[gfw] Legal placeholders detected but allowed (ALLOW_INCOMPLETE_LEGAL=1)"
    exit 0
  fi
  echo "[gfw] Legal placeholders still present. Release is not publication-ready." >&2
  exit 1
fi

echo "[gfw] Legal docs have no blocking placeholders"
