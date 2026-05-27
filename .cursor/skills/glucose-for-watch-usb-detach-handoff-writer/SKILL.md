---
name: glucose-for-watch-usb-detach-handoff-writer
description: Detects USB disconnections of phone or watch via ADB and automatically adds a timestamped entry in .cursor/state/developer-handoff.md to ease resumption.
disable-model-invocation: true
---

# Glucose For Watch USB Detach Handoff Writer

## Objective
Automatically trace USB disconnections of test devices in the developer handoff.

## Targets
- `.cursor/state/developer-handoff.md`
- `.cursor/state/usb-state.json`

## Preconditions
- `WIDGETG7_PHONE_SERIAL` and `WIDGETG7_WATCH_SERIAL` defined.
- `adb` available in PATH.

## Workflow
1. Read current USB state with `adb devices -l`.
2. Compare to last known state (`.cursor/state/usb-state.json` file).
3. Detect `connected -> disconnected` transitions.
4. Add an incident in `.cursor/state/developer-handoff.md` (`Recent incidents` table).
5. Apply 30-minute deduplication per device.
6. Update local state.

## Incident format
`| YYYY-MM-DD | USB detach detected (phone/watch) | Open (reconnect + verify sync) |`

## Rules
- Do not write an incident if serials are not configured.
- Do not duplicate an identical incident within the deduplication window.
- Preserve the handoff markdown structure.
