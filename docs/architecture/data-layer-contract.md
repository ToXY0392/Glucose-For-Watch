# Data Layer contract

> **Last updated:** 2026-05-23  
> **Source:** `core/datalayer-contract/src/main/java/com/widgetg7/core/datalayer/GlucoseDataLayerContract.kt`

Canonical paths and keys for phone ↔ watch communication.

---

## Paths

| Path | Direction | Purpose |
|------|-----------|---------|
| `/glucose/latest` | Phone → Watch | Latest glucose reading payload |
| `/glucose/refresh/request` | Watch → Phone (message) | Manual refresh trigger |
| `/glucose/refresh/status` | Phone → Watch | Refresh progress / result |
| `/glucose/watch/ack` | Watch → Phone | Delivery confirmation |
| `/watch/status/request` | Phone → Watch | Request watch health |
| `/watch/status` | Watch → Phone | Battery, low-power, sync-limited flags |

All `PutDataItem` operations use `.setUrgent()` for faster delivery when connected.

---

## `/glucose/latest` payload keys

| Key | Type | Description |
|-----|------|-------------|
| `value_mg_dl` | int | Glucose value |
| `trend` | string | Normalized trend token |
| `delta` | int | Delta from previous reading |
| `timestamp_epoch_ms` | long | Reading timestamp |
| `sequence_id` | long | Monotonic push sequence |
| `stale` | boolean | Computed on watch if age > threshold |

---

## Ack payload

Watch writes to `/glucose/watch/ack`:

| Key | Type | Description |
|-----|------|-------------|
| `sequence_id` | long | Matches pushed sequence |
| `received_at_epoch_ms` | long | Watch receive time |

Phone compares `lastPushSequenceId` vs `lastAckSequenceId` in `PhoneSyncStateStore`.

---

## Refresh status values

| Status | User message (watch) |
|--------|----------------------|
| `in_progress` | Refreshing… |
| `completed` | Up to date |
| `failed` | Error message |

---

## Watch health payload

| Key | Type | Description |
|-----|------|-------------|
| `battery_level` | int | 0–100 |
| `low_power_mode` | boolean | Power save active |
| `sync_limited` | boolean | Degraded sync requested |
| `message` | string | Human-readable status |

Phone uses this for `BatteryDegradedPolicy` (120 s poll interval).

---

## Offline behavior

- Data items may be **buffered** when devices are disconnected and delivered on reconnect
- **Messages** (refresh request) require both sides awake and connected
- Do not use Data Layer as primary network store — phone always fetches from Dexcom directly

---

## Related

- [Sync pipeline](sync-pipeline.md)
- [Google Wear OS Data Layer](https://developer.android.com/training/wearables/data/data-items)
