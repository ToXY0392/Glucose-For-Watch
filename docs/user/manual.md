# User manual

> **Last updated:** 2026-05-23

## Installation

| Device | APK |
|--------|-----|
| Phone | `mobile-debug.apk` |
| Watch | `wear-debug.apk` |

Install via ADB or the in-app wear installer (debug builds).

## First-time setup

1. Open ToXY on the phone.
2. Read and accept legal documents.
3. Enter Dexcom Share credentials:
   - Username (follower account)
   - Password
   - Server: **US** or **OUS** (outside US)
4. Confirm the phone shows a glucose reading.
5. Verify the watch is paired (Bluetooth + Wear OS companion app).
6. Tap **Sync** on the phone.
7. Add the ToXY **tile** or **complication** on the watch.

## Daily use

- Glucose updates automatically every ~45 seconds when the watch is connected.
- The phone keeps fetching from Dexcom even when the watch is off your wrist; data catches up when the watch reconnects.
- Glucose numbers use **standard AGP medical colors**:
  - **Green** — in range (70–180 mg/dL)
  - **Yellow / orange** — high
  - **Red** — low
  - **Grey trend** — data is stale (> 2 minutes old)

## Phone home screen

| Element | Meaning |
|---------|---------|
| Large glucose value | Latest Dexcom reading (AGP-colored) |
| Sync button | Force fetch + push to watch |
| Watch status | Connected / disconnected / ack pending |
| Settings | Dexcom credentials, watch setup |

## Watch tile

| Element | Meaning |
|---------|---------|
| Number | Latest synced glucose (AGP-colored) |
| Trend arrow | Direction of change |
| Sync button | Request refresh from phone |

## Watch complication

Available types: short text, long text, ranged value.  
If the complication shows an old value while the tile is correct, remove and re-add the complication on your watch face.

## Background operation

For reliable sync, allow ToXY to run in the background on the phone:

- Disable battery optimization for the app (manufacturer-dependent)
- Keep the active sync notification if shown

## Dexcom G6 vs G7

Both work through Dexcom Share with the same setup. See [Dexcom G6/G7 compatibility](../compatibility/dexcom-g6-g7.md).

## Support

- [Troubleshooting](troubleshooting.md)
- [Medical disclaimer](../legal/medical-disclaimer.md)
