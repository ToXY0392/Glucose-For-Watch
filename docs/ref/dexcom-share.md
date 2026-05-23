# Dexcom Share

> **Last updated:** 2026-05-23

## Project usage

- Primary glucose source on the phone module
- Data relayed to Wear OS via Data Layer (not fetched directly on watch)

## Endpoints

| Region | Host |
|--------|------|
| US | `share2.dexcom.com` |
| OUS | `shareous1.dexcom.com` |

Path prefix: `/ShareWebServices/Services/`

## Implementation

| Component | Path |
|-----------|------|
| HTTP client | `feature/dexcom-share/.../DexcomShareClient.kt` |
| Phone wiring | `mobile/.../data/PhoneGlucoseSourceFactory.kt` |
| Credentials | `mobile/.../settings/AppSettingsStore.kt` |

## Important notes

- This uses the **Share HTTP protocol**, not Dexcom OAuth v3 API
- Valid Share session required; auth errors surfaced to UI
- Never log credentials or session tokens
- G6 and G7 both work when Share is enabled — see [compatibility doc](../compatibility/dexcom-g6-g7.md)

## Official references

- [Dexcom Developer Portal](https://developer.dexcom.com/) — OAuth v3 (distinct from Share)
- [Dexcom G6/G7 compatibility](../compatibility/dexcom-g6-g7.md)
