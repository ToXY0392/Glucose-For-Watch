#!/usr/bin/env bash
# Claude Code PreToolUse hook (Bash | PowerShell).
# Hard constraint (AGENTS.md, C.7 soak): never install/uninstall on the test
# devices unless the user explicitly asks. This hook turns that rule into a
# confirmation prompt instead of relying on the agent remembering it.
# Pure bash + grep so it runs the same in Git Bash (Windows) and WSL.

payload="$(cat)"

if printf '%s' "$payload" | grep -Eiq 'installGlucoseForWatchDebug|adb(\.exe)?[^|;&]*[[:space:]](install|install-multiple|uninstall)([[:space:]]|"|$)'; then
  cat <<'JSON'
{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"ask","permissionDecisionReason":"Device install/uninstall detected (installGlucoseForWatchDebug / adb install / adb uninstall). Hard constraint during C.7 soak: only run on explicit user request."}}
JSON
fi
exit 0
