# Dexcom G6 and G7

Glucose For Watch reads glucose through the **Dexcom Share HTTP API**. The protocol returns values for **G6** and **G7** when Share is enabled — no sensor-type branching in app code.

## Requirements

| Requirement | Detail |
|-------------|--------|
| Dexcom Share | Enabled in official Dexcom app |
| Account | Follower / Share credentials |
| Region | **US** (`share2.dexcom.com`) or **OUS** (`shareous1.dexcom.com`) |
| Phone | Internet for Dexcom fetch |
| Watch | Wear OS paired with **Android** phone |

## G6 vs G7

Same Share HTTP endpoints, mg/dL integers, trend tokens 1–7, LOW/HI sentinels.

## Implementation

| Component | Path |
|-----------|------|
| HTTP client | `feature/dexcom-share/.../DexcomShareClient.kt` |
| Source factory | `mobile/.../data/PhoneGlucoseSourceFactory.kt` |
| Credentials | `mobile/.../settings/AppSettingsStore.kt` |

Default application ID: `d89443d2-327c-4a6f-89e5-496bbb0317db`

## Not supported

- Dexcom OAuth v3 official API
- Direct BLE to sensor/transmitter
- Insulin pump integration
- mmol/L display (mg/dL only)

## Medical note

Not a certified medical device. Confirm readings with official Dexcom apps and your healthcare provider. See [medical disclaimer](legal/medical-disclaimer.md).
