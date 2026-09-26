# Dexcom G6 and G7

Glucose For Watch reads glucose through the **Dexcom Share HTTP API**. The protocol returns values for **G6** and **G7** when Share is enabled — no sensor-type branching in app code.

## Requirements

| Requirement | Detail |
|-------------|--------|
| Dexcom Share | Enabled in official Dexcom app |
| Account | Follower / Share credentials |
| Region | Choose the account's matching region: **US** (`share2.dexcom.com`) or **OUS** (`shareous1.dexcom.com`) |
| Phone | Internet for Dexcom fetch |
| Watch | Wear OS paired with **Android** phone |

## G6 vs G7

Same Share HTTP endpoints, mg/dL integers, trend tokens 1–7, LOW/HI sentinels.

Select the same US/OUS region as the Dexcom account. The app does not infer the
region from the username. Credentials and region are entered in the phone app
and stored in encrypted preferences; they do not belong in `gradle.properties`.
The build-time `dexcomShareApplicationId` is a protocol identifier, not a
Dexcom account credential.

## Implementation

| Component | Path |
|-----------|------|
| HTTP client | `feature/dexcom-share/.../DexcomShareClient.kt` |
| Source factory | `mobile/.../data/PhoneGlucoseSourceFactory.kt` |
| Credentials | `mobile/.../settings/AppSettingsStore.kt` |

Default application ID: `d89443d2-327c-4a6f-89e5-496bbb0317db`

The Share session is cached per account and renewed once automatically when
Dexcom reports it expired. A transient network error is retried by the active
sync service with exponential backoff (15 seconds up to 5 minutes). The
interrupted-sync notification appears after three consecutive failures; the
reconnect notification appears after two consecutive authentication failures.

## Not supported

- Dexcom OAuth v3 official API
- Direct BLE to sensor/transmitter
- Insulin pump integration

Phone, tile, and complication display units can be selected as **mg/dL** or
**mmol/L**. Dexcom Share values stay mg/dL internally; AGP colors use mg/dL
thresholds.

## Medical note

Not a certified medical device. Confirm readings with official Dexcom apps and your healthcare provider. See [medical disclaimer](../legal/medical-disclaimer.md).
