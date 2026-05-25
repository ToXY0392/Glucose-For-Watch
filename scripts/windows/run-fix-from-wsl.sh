#!/usr/bin/env bash
# Lance les correctifs Windows depuis WSL (interop powershell.exe).
#
# Usage :
#   ./scripts/windows/run-fix-from-wsl.sh "C:\Users\You\Desktop\THP\Projects\Widget G7"
#   ./scripts/windows/run-fix-from-wsl.sh "/mnt/c/Users/You/Desktop/THP/Projects/Widget G7"
# Options (dans n'importe quel ordre apres le chemin) :
#   --takeown-admin      PowerShell eleve (takeown sur le projet)
#   --with-gradle-jvm    ecrit org.gradle.java.home dans %USERPROFILE%\.gradle\gradle.properties
#
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
FIX_WIN="$(wslpath -w "$ROOT/scripts/windows/fix-android-studio-write-permissions.ps1")"
JVM_WIN="$(wslpath -w "$ROOT/scripts/windows/ensure-windows-studio-gradle-jvm.ps1")"

PROJECT=""
TAKEOWN=0
WITH_JVM=0
for arg in "$@"; do
  case "$arg" in
    --takeown-admin) TAKEOWN=1 ;;
    --with-gradle-jvm) WITH_JVM=1 ;;
    -*)
      echo "Option inconnue: $arg"
      exit 1
      ;;
    *)
      if [[ -n "$PROJECT" ]]; then
        echo "Un seul chemin projet attendu."
        exit 1
      fi
      PROJECT="$arg"
      ;;
  esac
done

if [[ -z "$PROJECT" ]]; then
  echo "Usage: $0 <Windows-or-/mnt/c path to Widget-G7> [--takeown-admin] [--with-gradle-jvm]"
  exit 1
fi

if [[ "$PROJECT" == /mnt/* ]]; then
  PROJECT="$(wslpath -w "$PROJECT")"
fi

POW_EXTRA=()
if [[ "$TAKEOWN" -eq 1 ]]; then
  POW_EXTRA=(-ElevatedTakeOwnership)
fi

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$FIX_WIN" -ProjectPath "$PROJECT" "${POW_EXTRA[@]}"

if [[ "$WITH_JVM" -eq 1 ]]; then
  powershell.exe -NoProfile -ExecutionPolicy Bypass -File "$JVM_WIN"
fi

echo ""
echo "Si Android Studio affiche encore des droits d ecriture : lancer en ADMIN Windows :"
echo "  scripts/windows/fix-windows-studio-defender-admin.ps1 -ProjectPath \"<ton chemin Widget G7>\""
echo "Voir docs/dev.md section Windows write permissions."
