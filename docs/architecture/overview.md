# Architecture overview

> **Last updated:** 2026-05-23

Widget G7 is a multi-module Android project that syncs Dexcom Share glucose data from a phone to a Wear OS companion app.

## System context

```
┌─────────────┐     HTTP      ┌──────────────┐    Data Layer    ┌─────────────┐
│ Dexcom Share│──────────────►│  Mobile app  │───────────────►│  Wear app   │
│   (cloud)   │               │  (:mobile)   │◄───────────────│   (:wear)   │
└─────────────┘               └──────────────┘      ack        └─────────────┘
                                     │                              │
                                     │                              ├── Tile
                                     │                              └── Complication
                              ActiveGlucoseSyncService
                              DexcomShareClient
```

## Modules

| Gradle module | Type | Responsibility |
|---------------|------|----------------|
| `:mobile` | Application | Phone UI, Dexcom fetch, sync orchestration, watch setup |
| `:wear` | Application | Cache, tile, complication, Data Layer listener, ack |
| `:core:model` | Library | `GlucoseReading`, `SyncStatus` |
| `:core:datalayer-contract` | Library | Wear Data Layer paths and keys |
| `:core:testing` | Library | Shared test fixtures |
| `:feature:sync` | Library | `GlucoseSyncEngine`, publishers, policies, formatters |
| `:feature:dexcom-share` | Library | Dexcom Share HTTP client |
| `:feature:watch-install` | Library | Embedded wear APK install via ADB/OCR |

Both apps share `applicationId = com.widgetg7.mobile` (wear is a companion, not standalone).

## Package layout

| Package root | Module |
|--------------|--------|
| `com.widgetg7.mobile.*` | mobile |
| `com.widgetg7.wear.*` | wear |
| `com.widgetg7.core.*` | core |
| `com.widgetg7.feature.*` | feature |

## Sync design

The sync core uses **hexagonal architecture**:

- `GlucoseSyncEngine` defines ports: `GlucoseSourcePort`, `SyncStatePort`, `WearSyncPort`, `RefreshStatusPort`
- Phone adapts ports in `PhoneGlucoseSyncEngine`
- Shared logic lives in `:feature:sync` with unit tests

See [Sync pipeline](sync-pipeline.md) for the full flow.

## UI layers

| Layer | Standard | Scope |
|-------|----------|-------|
| Medical (glucose values) | AGP / TIR colors | Tile, complication, phone hero |
| Chrome (ToXY) | Material 3 dark theme | Backgrounds, buttons, navigation |

See [Glucose color standard](../design/glucose-color-standard.md) and [ToXY design system](../design/toxy-design-system.md).

## Key design decisions

1. **Dexcom Share HTTP** — not OAuth v3 API (simpler follower-account flow)
2. **Ack-based verification** — watch writes `/glucose/watch/ack`; phone tracks sequence IDs
3. **Foreground sync service** — `ActiveGlucoseSyncService` for reliable background polling
4. **Embedded wear APK** — debug mobile build packages wear APK for sideload install
5. **Protolayout tiles** — wear tile built programmatically (Material Tiles 1.5 target)

## Diagram

![Sync architecture](../assets/widget-g7-architecture.png)

## Related docs

- [Sync pipeline](sync-pipeline.md)
- [Repository structure](repository-structure.md)
- [Data Layer contract](data-layer-contract.md)
