# Dexcom G6 and G7 compatibility

> **Last updated:** 2026-05-23

---

## Summary

Widget G7 reads glucose through the **Dexcom Share HTTP API**. This protocol returns glucose values regardless of whether the underlying sensor is **G6** or **G7**, as long as **Dexcom Share is enabled** on the account.

There is **no sensor-type branching** in the app code today — compatibility is at the Share protocol level.

---

## Requirements

| Requirement | Detail |
|-------------|--------|
| Dexcom Share | Must be enabled in official Dexcom app |
| Account type | Follower / Share credentials (not patient app login unless Share configured) |
| Region | **US** (`share2.dexcom.com`) or **OUS** (`shareous1.dexcom.com`) |
| Network | Phone needs internet for Dexcom fetch |
| Watch | Wear OS paired with **Android** phone (Data Layer does not work with iOS) |

---

## G6 vs G7

| Aspect | G6 | G7 |
|--------|----|----|
| Share API | Same HTTP endpoints | Same HTTP endpoints |
| Value format | mg/dL integer | mg/dL integer |
| Trend tokens | Numeric 1–7 or strings | Numeric 1–7 or strings |
| LOW / HI sentinels | Supported | Supported |
| App branding | Supported (not G7-only at protocol level) | Supported |

### Validation status

| Test case | G6 | G7 |
|-----------|----|----|
| Share auth US | Planned Phase 3 | Planned Phase 3 |
| Share auth OUS | Planned Phase 3 | Planned Phase 3 |
| Tile + complication | Planned Phase 3 | Planned Phase 3 |
| Offline reconnect | Planned Phase 3 | Planned Phase 3 |

---

## Implementation

| Component | Path |
|-----------|------|
| HTTP client | `feature/dexcom-share/.../DexcomShareClient.kt` |
| Phone source factory | `mobile/.../data/PhoneGlucoseSourceFactory.kt` |
| Credentials store | `mobile/.../settings/AppSettingsStore.kt` |
| Build config app ID | `BuildConfig.DEXCOM_SHARE_APPLICATION_ID` |

Default application ID: `d89443d2-327c-4a6f-89e5-496bbb0317db`

---

## Not supported (current scope)

- Dexcom **OAuth v3 official API** (different from Share HTTP) — future evaluation
- Direct BLE to sensor/transmitter
- Insulin pump integration
- Calibrated mmol/L display (mg/dL only today)

---

## Medical note

Widget G7 is not a certified medical device. Always confirm readings and treatment decisions with official Dexcom applications and your healthcare provider.

See [Medical disclaimer](../legal/medical-disclaimer.md).

---

## Related

- [Dexcom Share reference](../ref/dexcom-share.md)
- [User manual](../user/manual.md)
