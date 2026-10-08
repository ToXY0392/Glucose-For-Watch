# Direct sensor collection migration plan

## Purpose

Plan the evolution of Glucose For Watch from its current Dexcom Share phone-to-watch
flow to a **watch-first direct sensor connection**, while retaining Dexcom Share
through the phone as an explicit fallback.

This document is a plan and audit, not a protocol specification or implementation.
No direct G6/G7 support is claimed until it has been demonstrated on named sensor
and watch combinations.

## Product decisions

1. Direct Bluetooth LE collection on Wear OS is the preferred source.
2. Dexcom Share remains available as a fallback source through the phone.
3. The watch selects and displays one reading stream; the phone must not overwrite
   a newer direct reading with an older Share reading.
4. The active source and reading age must be visible. A cached value must never be
   represented as current after it has become stale.
5. The watch must continue direct collection when the phone is absent or
   unreachable, subject to verified Wear OS and hardware constraints.
6. G6 and G7 are separate compatibility targets until each has its own successful
   protocol and hardware validation.
7. Existing Share behavior remains available during development and is not
   removed as a prerequisite for enabling direct collection.
8. Do not port or copy implementation code from GPL-licensed projects into this
   repository without a deliberate license review. Studying public behavior and
   independently implementing a compatible feature are separate activities.

## Current-state audit

Audit performed against the repository source and developer documentation.

| Area | Current implementation | Migration implication |
|---|---|---|
| Phone glucose source | `PhoneGlucoseSourceFactory` creates a Dexcom Share source; `DexcomShareClient` fetches readings over HTTPS. | Keep this as the fallback source. Add no direct sensor logic to the Share HTTP client. |
| Phone sync | `PhoneGlucoseSyncEngine` runs `GlucoseSyncEngine`; the engine fetches, deduplicates and pushes readings. | Preserve this path for Share fallback and existing phone status. Avoid making it the owner of direct collection. |
| Phone-to-watch delivery | `WearSyncPublisher` writes `/glucose/latest` through Wear Data Layer. | Evolve the payload compatibly to identify Share as its source and carry reception metadata. |
| Watch receive path | `WearDataLayerListenerService` accepts `/glucose/latest`, saves `GlucoseCache`, ACKs delivery and refreshes the tile and complication. | Put both direct and fallback readings through a common watch-side validation/arbitration/store boundary. |
| Shared reading model | `GlucoseReading` and `GlucoseSnapshot` contain value, trend, delta, measurement timestamp and stale flag; neither records source or receive time. | Add source/provenance and receive-time semantics before introducing two competing sources. |
| Data Layer contract | `GlucoseDataLayerContract` defines glucose, refresh, ACK and watch-health paths; the glucose payload has no source field. | Add a versioned-compatible source envelope; continue accepting existing payloads as Share during migration. |
| Freshness | Share marks data stale after 2 minutes; the watch cache independently marks readings older than 2 minutes stale; the complication has a 15-minute presentation cutoff. | Make source freshness, overall reading freshness and display cutoffs explicit and consistent. Do not reuse one threshold as a failover threshold without evidence. |
| Polling and fallback | Phone sync normally polls every 45 seconds, or 120 seconds in degraded battery mode, with retry/backoff and WorkManager/alarm fallback. | Share fallback freshness and latency are bounded by this polling and Dexcom Share availability; they cannot be treated as an immediate sensor-level fallback. |
| Watch deployment | Wear manifest declares `com.google.android.wearable.standalone=false`, and current setup assumes a paired Android phone. Wear module has no declared BLE scan/connect permissions or sensor collector. | Validate the standalone/background model and the app metadata before changing it. Add only platform permissions actually required by the chosen implementation. |
| Watch display | The cache feeds the watch app, tile and complication; stale data is rendered differently. | Keep these surfaces on one selected watch snapshot so source changes cannot make surfaces disagree. Add visible source/connection status. |
| User setup | Phone UX configures Dexcom Share and installs/configures the watch app. The watch hints that Dexcom Share is configured on the phone. | Add a guided, separately validated direct-sensor setup path and retain Share setup for fallback. |
| Security and license | Share credentials are stored in encrypted preferences on the phone. Repository `LICENSE` reserves rights and grants limited personal sideload use; xDrip+/Juggluco are separate projects with their own licenses. | Keep Share credentials on phone, minimize sensor secrets sent/stored, prevent secret logging, and review licenses before any code reuse. |
| Existing QA evidence | Current documented hardware and stability checks validate phone-to-watch sync, including a G7 test matrix. They do not establish direct sensor-to-watch operation. | Record direct-path evidence separately; do not count existing sync gates as BLE qualification. |

### Audit evidence

- [Architecture](../dev/architecture.md)
- [Dexcom guide](../guide/dexcom.md)
- [Developer setup and QA commands](../dev/setup.md)
- [Stability gates and hardware evidence](STABILITY-GATES.md)
- [Phone source factory](../../mobile/src/main/java/com/glucoseforwatch/mobile/data/PhoneGlucoseSourceFactory.kt)
- [Phone sync engine](../../mobile/src/main/java/com/glucoseforwatch/mobile/sync/PhoneGlucoseSyncEngine.kt)
- [Share client](../../feature/dexcom-share/src/main/java/com/glucoseforwatch/feature/dexcomshare/DexcomShareClient.kt)
- [Wear sync publisher](../../feature/sync/src/main/java/com/glucoseforwatch/feature/sync/WearSyncPublisher.kt)
- [Watch Data Layer listener](../../wear/src/main/java/com/glucoseforwatch/wear/services/WearDataLayerListenerService.kt)
- [Watch glucose cache](../../wear/src/main/java/com/glucoseforwatch/wear/data/GlucoseCache.kt)
- [Shared Data Layer contract](../../core/datalayer-contract/src/main/java/com/glucoseforwatch/core/datalayer/GlucoseDataLayerContract.kt)
- [Repository license](../../LICENSE)

## Target architecture

```text
Dexcom sensor
    │ BLE / GATT (direct)
    ▼
Wear direct collector ──┐
                        ├─> validate + arbitrate ─> watch reading store
Phone Dexcom Share      │                            ├─> watch app
    │ HTTPS             │                            ├─> tile
    ▼                   │                            └─> complication
Phone Share service ─ Wear Data Layer ───────────────┘
```

### Component responsibilities

**Wear direct collector**

- Own scanning, connection lifecycle, protocol state, notifications, reconnects
  and source-health reporting for the selected sensor generation.
- Run independently of the phone once configured.
- Deliver normalized readings and typed connection/error state to the watch
  arbitration boundary; do not write UI caches directly.
- Keep sensor-specific protocol code isolated behind a small collector interface.
  G6 and G7 may have separate implementations and setup requirements.
- Never log authentication material, pairing data or full sensor packets.

**Phone Share collector**

- Keep the existing Dexcom Share authentication, polling, retry and notification
  behavior.
- Continue transmitting fallback readings while Share is configured and the
  phone can reach both Dexcom and the watch.
- Label readings as Share-originated; do not transmit Share credentials to Wear.

**Watch reading arbiter and store**

- Validate both inputs and select one display reading.
- Store measurement time, receive time, source, validity/freshness and a
  monotonic local revision (or equivalent ordering token).
- Ensure a delayed or duplicated Share update cannot replace a newer reading.
- Treat source health and reading freshness as separate state: a connected
  sensor can have no new reading yet, and a disconnected sensor can leave a
  temporarily fresh last measurement.
- Notify the app, tile and complication from one selected snapshot.

**Wear Data Layer contract**

- Continue the current Share path for backward-compatible phone/watch pairs.
- Version the new source metadata and define behavior for a missing source field
  (interpret legacy payloads as Share).
- Carry source, measurement timestamp, phone reception timestamp and existing
  sequence/target data as needed. Do not send sensor secrets in glucose payloads.
- Preserve ACK and queued-delivery behavior for Share without treating those ACKs
  as proof of direct sensor health.

### Source-selection policy

The policy should be implemented as a pure, unit-testable watch-side component.

1. Prefer a valid direct reading when it is fresh according to the validated
   cadence and lifecycle rules for that specific sensor.
2. Select Share when direct collection is unavailable or has failed its
   generation-specific freshness/health criteria and Share has a valid reading.
3. On direct recovery, switch back only after a valid, correctly ordered direct
   reading is received; do not switch merely because Bluetooth reports connected.
4. If neither source has a fresh reading, show the last known value only with
   stale state and age; never relabel a stale value as current.
5. Preserve timestamp order across sources. A Share reading with a newer
   measurement time may replace an older direct reading if the direct reading is
   no longer the latest; a late-arriving older value may not.
6. Record source transitions and reason codes without logging glucose values or
   secrets.

**Thresholds are intentionally not specified here.** Derive direct-data timeout,
source recovery hysteresis and displayed stale thresholds from documented sensor
cadence, hardware tests and clinical/product review. The existing 2-minute stale
flag and 15-minute complication behavior are display policies, not validated
failover parameters.

## Work plan and gates

Work is gated in order. A phase that fails its exit criteria must not be treated
as complete; retain the current Share-only production behavior until direct
collection is proven.

### Phase 0 — Feasibility and support boundary

**Work**

- Name target watch models, Wear OS versions and supported phone combinations.
- Obtain test G6 and G7 sensors/transmitters and define repeatable test procedures.
- For each sensor generation, verify what direct collection requires: setup
inputs, BLE discovery/connection, authentication, data cadence, session lifecycle,
reconnect behavior, receiver coexistence and any phone initialization dependency.
- Separate Dexcom-published support from community reverse-engineered evidence.
- Review legal, privacy, security and medical-product implications before
committing to a distribution plan.

**Exit gate G0**

- Feasibility matrix exists for G6 and G7 on named watch/OS combinations.
- Each matrix cell is marked tested, unsupported or unknown with evidence.
- Direct collection, reconnect, phone-absent collection and Share coexistence
have been demonstrated for at least one intended MVP combination per sensor
generation, or the product scope explicitly limits unsupported combinations.
- No unverified pairing field, static secret or protocol assumption is recorded
as a requirement.

### Phase 1 — Data model, provenance and compatibility design

**Work**

- Define source identity, measurement timestamp, receive timestamp, validity,
staleness and source-health models.
- Specify ordering and deduplication across direct and Share readings.
- Design a backward-compatible Data Layer payload migration, including legacy
payload interpretation and mixed app-version behavior.
- Decide persistence and retention for source metadata and health transitions.
- Define exactly what the user sees when direct, Share, both or neither are
available.

**Exit gate G1**

- Reviewed design and state-transition table cover all source combinations.
- Pure arbitration policy is specified independently of Android BLE APIs.
- Existing Share path and existing watch clients have a documented compatibility
and rollback story.

### Phase 2 — Watch direct-collector prototype

**Work**

- Implement only the collector needed for the first G0-approved sensor/watch
combination; keep it behind an internal, non-default enablement path.
- Add lifecycle, permission, foreground/background execution and power handling
appropriate to the selected Wear OS versions.
- Add typed collector states such as unconfigured, permission-required, scanning,
connecting, receiving, disconnected, unsupported and recoverable error.
- Store only the minimum sensor configuration securely on the device that needs
it; review whether phone-to-watch provisioning is necessary.
- Test with controlled disconnections, screen-off conditions, reboot and phone
absence.

**Exit gate G2**

- The watch receives timestamped readings directly without phone/network
participation after setup.
- Long-running and screen-off tests pass on named hardware with acceptable
connection reliability and battery impact.
- Secrets and raw protocol material do not appear in logs, backups or Data Layer
payloads.
- Unsupported device/sensor conditions produce explicit errors, not success-like
fallback states.

### Phase 3 — Watch arbitration and single display store

**Work**

- Add a source-neutral watch input boundary and pure arbiter.
- Route direct readings and existing Share readings into the same store.
- Preserve existing app/tile/complication rendering by adapting the selected
snapshot once.
- Add source and transition state to the watch status model.
- Maintain stale-age calculations from measurement timestamps, not packet
arrival alone.

**Exit gate G3**

- Unit tests cover priority, fallback, recovery, out-of-order values, duplicates,
invalid payloads, timestamp ties and no-fresh-source behavior.
- App, tile and complication always render the same selected reading/source
state.
- Existing Share-only behavior remains unchanged when the direct collector is
disabled.

### Phase 4 — Share fallback integration

**Work**

- Extend the shared contract with source and reception metadata without breaking
legacy senders/receivers.
- Keep Share polling on the phone and mark all delivered records as Share.
- Ensure pending queues, retry, ACKs and manual refresh remain specific to Share
delivery and cannot overwrite a better direct reading.
- Expose fallback availability and its limiting conditions (phone, network,
Share authentication/service, watch link).

**Exit gate G4**

- Direct loss selects a fresh Share reading when available and indicates the
source switch on Wear.
- If Share is unavailable, direct collection continues unaffected and the UI
reports that fallback is unavailable.
- If the phone/watch link is down, direct collection continues; queued Share
data cannot displace newer direct data when connectivity returns.

### Phase 5 — Setup and operational UX

**Work**

- Add phone setup for selecting G6/G7 and provisioning only required setup data.
- Add Wear setup/status screens for permissions, sensor state, active source,
last measurement age and fallback status.
- Provide explicit recovery actions for Bluetooth disabled, permission denied,
sensor out of range, collector stopped, Share unavailable and unsupported
combinations.
- Update user guide, Dexcom guide, privacy policy, medical disclaimer and support
matrix to match verified behavior.

**Exit gate G5**

- First-run setup succeeds and fails clearly on each supported/blocked state.
- Source and data age are understandable without relying on phone connectivity.
- Accessibility, localization and watch-small-screen checks pass.

### Phase 6 — Hardware qualification and release

**Work**

- Add automated tests for models, arbitration, contract compatibility and failure
policies.
- Add hardware QA matrix for each declared sensor/watch/OS combination: pairing,
screen-off collection, phone absent, phone nearby, Share fallback, source
recovery, Bluetooth toggle, reboot, sensor end/restart, low battery and time
changes.
- Run stability/overnight soak with redacted logs and inspect battery impact.
- Verify release permissions, app metadata, security disclosures, sideload update
compatibility and rollout/rollback procedure.
- Keep direct support opt-in/internal until the hardware gate passes; then enable
only validated combinations.

**Exit gate G6**

- All advertised combinations pass the hardware matrix and stability gate.
- No unexplained missing/duplicate/out-of-order readings or source oscillation in
the agreed test window.
- Fresh/stale/unknown states and fallback behavior pass acceptance tests.
- Release documentation and support limitations match measured evidence.
- Rollback to the known-good Share path is tested.

## Acceptance criteria

- A supported watch can collect directly from its declared G6/G7 sensor without
the phone or internet after initial setup, for the verified runtime window.
- Direct collection is the preferred source; Share is used only when direct is
not currently eligible and a valid Share reading exists.
- The selected source is consistent across watch app, tile and complication.
- Every displayed reading can be attributed to its source and measurement time.
- Old, delayed, duplicate or out-of-order values cannot silently appear fresh.
- A direct BLE failure does not stop Share fallback; a Share/network failure does
not stop direct BLE collection.
- Watch restart, phone restart, phone absence, Bluetooth interruption and sensor
session end have explicit recovery behavior.
- Existing Share-only users and mixed-version phone/watch installs retain a
documented supported path during migration.
- No undocumented direct sensor compatibility is advertised.

## Test strategy

**Automated**

- Unit tests for arbitration, freshness, ordering, deduplication and all state
transitions.
- Contract tests for current and legacy Data Layer payloads, missing metadata,
version mismatches and target-node filtering.
- Collector tests with fake BLE transport for state transitions, retries,
timeouts, malformed packets and cancellation.
- Regression tests for Share auth expiry, network failure, polling, pending
pushes, ACKs, and current UI stale/display behavior.
- Static/lint checks for manifest permissions, exported components, sensitive
logging and dependency/license inventory.

**Hardware**

| Dimension | Minimum scenarios |
|---|---|
| Sensor | G6 and G7 validated independently; lifecycle start, steady state and end |
| Watch | Each model and Wear OS version intended for support |
| Phone | Near, disconnected, powered off, rebooted |
| BLE | Screen on/off, radio toggle, transient loss, out-of-range, reconnect |
| Share | Fresh, delayed, auth expired, network unavailable, service unavailable |
| Arbitration | Direct wins; direct lost and Share takes over; direct recovery; stale from both |
| Runtime | Overnight soak, low battery, Doze/background limits, app update |
| Display | App, tile, complication parity; stale age and source transitions |

Log only technical state and reason codes. Redact credentials, transmitter
identifiers, glucose values and packet contents from diagnostics unless an
explicitly consented, controlled test requires otherwise.

## Principal risks and mitigations

| Risk | Impact | Mitigation / decision gate |
|---|---|---|
| Reverse-engineered protocol changes or incomplete knowledge | Direct path stops working or yields invalid readings | Isolate protocol adapter; validate per sensor generation; keep Share fallback; never claim untested coverage. |
| Watch BLE/background restrictions vary by model and OS | Missed readings, disconnects, excessive battery drain | Hardware matrix and long screen-off/overnight tests before support claims. |
| G6/G7 setup or authentication differ | Wrong pairing flow or inability to connect | Independent G6/G7 feasibility and state-machine validation. |
| Two sources conflict or arrive out of order | Incorrect apparent latest value/source | Central arbiter; measurement-time ordering; deterministic unit tests and transition telemetry. |
| Share is delayed or unavailable during direct failure | Fallback may be old or absent | Keep visible freshness/source; do not describe fallback as guaranteed or instantaneous. |
| Data Layer versions differ | Lost source metadata or incompatible installs | Legacy payload means Share; schema/version tests; staged rollout and rollback. |
| Secrets exposed during provisioning or logging | Sensor/account compromise | Keep Share credentials phone-only; minimize sensor data; encrypted storage where required; redacted logs. |
| GPL code copied into all-rights-reserved distribution | License conflict | Independent implementation; track references; obtain legal review before copying or linking code. |
| Medical expectations exceed validated behavior | Unsafe reliance on stale/incorrect display | Clear non-medical disclaimer, stale/source labels, no treatment recommendations, clinical/legal review. |
| Existing QA mistaken for direct-path proof | Unsupported release claims | Separate direct BLE gates and evidence from Share sync gates. |

## Open decisions required before implementation

1. Which exact watch models and Wear OS versions are the first supported targets?
2. Is support required for both G6 and G7 at first launch, or can the MVP be
   explicitly limited to the combinations that pass G0?
3. What exact setup inputs may be stored on the watch, and which component
   provisions them?
4. What sensor-specific freshness and recovery thresholds are justified by
   measured cadence and product review?
5. Should Share polling continue at the current rate while direct is healthy, or
   be reduced without compromising fallback latency? Decide only after measuring
   battery, service limits and fallback delay.
6. What minimum direct-collection runtime and battery budget are release gates?
7. Which legal/regulatory/privacy review is needed before distributing a
   reverse-engineered collector?

## Immediate next action

Run **Phase 0 only**: establish the supported hardware boundary and produce the
G6/G7 feasibility matrix. Do not change the production data path, remove Share,
or add BLE protocol code before G0 is reviewed and passed.
