# User guide — Glucose For Watch

Install Glucose For Watch and see your Dexcom glucose on Wear OS (app, tile, complication).

## Quick start

1. Install `mobile-debug.apk` on your Android phone.
2. Install `wear-debug.apk` on your Wear OS watch.
3. Open **Glucose For Watch** on the phone.
4. Accept the legal notices.
5. Connect Dexcom Share (username, password, US or OUS region).
6. Tap **Sync** on the phone home screen.
7. Add the Glucose For Watch tile or complication on the watch.

## Requirements

- Dexcom Share enabled on your account
- Follower credentials (not the patient app login unless Share is configured)
- Dexcom **G6 or G7** — see [dexcom.md](dexcom.md)
- Wear OS watch paired with an **Android** phone
- Phone and watch APKs are installed on their respective devices; both currently
  use the Android application ID `com.glucoseforwatch.mobile`.

## Daily use

- The phone checks for Dexcom updates every 45 s in normal conditions (every
  120 s in low-battery / limited-sync mode); Dexcom readings themselves usually
  arrive on Dexcom's own update cadence.
- The phone keeps fetching from Dexcom when the watch is off-wrist; data catches up on reconnect.
- Values use **AGP medical colors**: green in range, red low, and yellow/orange
  high. The Dexcom reading/Wear cache is flagged stale after 2 min; the tile
  dims old readings after 15 min, while the complication switches to a
  placeholder after 15 min.
- Network failures retry silently with backoff. The interrupted-sync
  notification is shown only after three consecutive failures; authentication
  failures prompt reconnection after two consecutive failures.

### Phone home screen

| Element | Meaning |
|---------|---------|
| Large glucose value | Latest Dexcom reading (AGP-colored) |
| Sync button | Force fetch + push to watch |
| Watch status | Connected / disconnected / ack pending |
| Settings | Dexcom credentials |

### Watch tile

| Element | Meaning |
|---------|---------|
| Number | Latest synced glucose (AGP-colored) |
| Trend arrow | Direction of change |
| Sync button | Request refresh from phone |

The tile has no logo in its scrolling preview. The Wear app launcher icon uses
the same logo as the phone app; the complication's picker icon also uses that
logo.

### Complication

If the complication shows an old value while the tile is correct, remove and re-add it on the watch face.

## Background operation

Allow Glucose For Watch to run in the background on the phone: disable battery optimization and keep the sync notification if shown.

## Troubleshooting

### Glucose frozen on watch

1. Check the value on the **phone** — if correct there, the issue is phone→watch sync.
2. Tap **Sync** on the phone, then on the watch tile.
3. Ensure Bluetooth is on and the watch is in range.
4. For complications only: remove and re-add on the watch face.
5. Last resort: reinstall both APKs.

### Watch was offline

The phone continues reading Dexcom. On reconnect, data should sync within 1–2 min if the phone app is running. Otherwise open the app and tap **Sync**.

### "Phone unavailable" on watch

Bluetooth off, phone app killed by battery optimizer, or watch out of range. Open Glucose For Watch on the phone, disable battery optimization, tap **Sync**.

### Sync button does nothing

Check Dexcom credentials, internet connectivity, and error messages. Restart the phone app.

If you uninstall and reinstall the phone app, enter your Dexcom Share settings
again; Android clears the app's encrypted local settings on uninstall.

### Dexcom authentication errors

Verify Share is enabled in the official Dexcom app, confirm US vs OUS server, update password if changed.

### Healthy sync checklist

- Phone value = watch value
- Timestamps match
- Stale indicator off when data is fresh (< 2 min)

## Support

Report issues with phone/watch model and steps to reproduce. **Do not include real glucose values or credentials.**

- [Medical disclaimer](../legal/medical-disclaimer.md)
