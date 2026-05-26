#!/usr/bin/env bash
# Create GitHub labels for Glucose For Watch (requires gh auth login).
# Usage: bash .github/scripts/create-labels.sh
set -euo pipefail

REPO="${GITHUB_REPO:-ToXY0392/Glucose-For-Watch}"

if ! command -v gh >/dev/null 2>&1; then
  echo "Install GitHub CLI: https://cli.github.com/"
  exit 1
fi

create() {
  gh label create "$1" --repo "$REPO" --color "$2" --description "$3" 2>/dev/null || \
    gh label edit "$1" --repo "$REPO" --color "$2" --description "$3"
}

echo "Creating labels on $REPO ..."

create "bloc-s"       "BFD4F2" "Stabilite transverse"
create "bloc-x"       "D73A4A" "Crash FGS · gate G-X"
create "bloc-a"       "FBCA04" "P0 fiabilite · gate G-A"
create "bloc-m"       "0E8A16" "Mock user · gate G-M"
create "bloc-b"       "1D76DB" "Sync/wear · gate G-B"
create "bloc-c"       "5319E7" "QA hardware · gate G-C"
create "bloc-d"       "006B75" "Qualite/tests · gate G-D"
create "bloc-f"       "E99695" "Compose v0.6 · gate G-F*"

create "gate-blocker" "B60205" "Bloque tag v0.5 ou v0.6"
create "incident-p0"  "8B0000" "Crash fatal ouvert"
create "sync-critical" "D93F0B" "Touch sync · S1-S3 requis"

create "area:mobile"  "C5DEF5" "Phone app"
create "area:wear"    "C5DEF5" "Tile · complication"
create "area:sync"    "C5DEF5" "GlucoseSyncEngine · Data Layer"
create "area:dexcom"  "C5DEF5" "Share API · auth"
create "area:ux-kit"  "C5DEF5" "toxy-ux-kit · AGP colors"
create "area:infra"   "C5DEF5" "CI · scripts · repo"

create "hardware-qa"  "D4C5F9" "Session adb phone+watch"
create "docs-only"    "EDEDED" "Pas de build requis"

echo "Done."
