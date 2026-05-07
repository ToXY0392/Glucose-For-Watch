#!/usr/bin/env bash
# Ouvre ce dépôt (chemin UNC WSL) dans Android Studio sous Windows depuis WSL.
# Prérequis : Android Studio Windows installée, projet accessible via \\wsl$\
#
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
LAUNCH="$(wslpath -w "$ROOT/scripts/windows/launch-android-studio-with-project.ps1")"
UNC="$(wslpath -w "$ROOT")"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$LAUNCH" -ProjectPath "$UNC"
