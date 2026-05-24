# Troubleshooting

> **Last updated:** 2026-05-23

## Glucose value frozen on watch

1. Check the value on the **phone app** — if correct there, the issue is phone→watch sync.
2. Tap **Sync** on the phone.
3. Tap **Sync** on the watch tile.
4. Ensure the watch is connected (Bluetooth on, in range).
5. If only the **complication** is frozen: remove and re-add it on the watch face.
6. Last resort: reinstall both mobile and wear APKs.

## Watch was not worn / offline for a while

- The phone continues reading Dexcom while you are away from the watch.
- When the watch reconnects, data should sync within 1–2 minutes if the phone app is running.
- If not: open the phone app and tap **Sync**.

## "Phone unavailable" on watch

This message appears when the watch cannot find a connected phone node. Common causes:

- Bluetooth off on phone or watch
- Phone app killed by battery optimizer
- Watch out of range

**Fix:** Open Glucose For Watch on the phone, disable battery optimization, tap Sync.

## Sync button on phone does nothing

1. Check Dexcom Share credentials in Settings.
2. Verify internet connectivity on the phone.
3. Check for error messages on the home screen.
4. Restart the phone app.

## Dexcom authentication errors

- Verify Share is enabled in the official Dexcom app.
- Confirm US vs OUS server selection.
- Password recently changed → update in Glucose For Watch settings.

## Gradle / developer issues

See [Environment compatibility](../compatibility/environment.md) and [Android Studio guide](../development/android-studio.md).

## Healthy sync checklist

Compare regularly:

- Phone value vs watch value — should match
- Reading timestamp — should match
- Stale indicator — should be false when data is fresh (< 2 min)

## Still stuck?

Log an issue with:

- Phone model + Android version
- Watch model + Wear OS version
- Steps to reproduce
- Whether Dexcom G6 or G7

**Do not include real glucose values or credentials in reports.**
