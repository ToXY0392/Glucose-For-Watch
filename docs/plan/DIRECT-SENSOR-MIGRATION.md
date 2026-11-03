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
5. Compare measurement timestamps when choosing among fresh candidates. An older
   Share candidate may be selected only as fallback when the newer direct
   candidate is stale or unavailable; it cannot displace a newer fresh direct
   reading. Reject out-of-order input within each individual source.
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

**Initial feasibility baseline (2026-11-03)**

**Phase 0 status: BLOCKED for direct-collection testing; not passed.** The only
available G7 is in service, and the user cannot budget a separate, isolated
test sensor. The user intends to revisit a possible test at the next sensor
change; this is a future decision point, not authorization to connect now or
evidence that a replacement sensor is safe for experimentation. No prototype
or sensor-level test has been run. Continue only work that can be validated
without connecting to a sensor; hardware-dependent development is paused.

The first MVP feasibility target is **G7**. G6 is deferred and is not included in
the initial target; this is not a statement that G6 is unsupported. The user has
a G7 sensor in service and reports Wear OS 7 on the candidate watch.
The user-provided Dexcom app screenshot shows software number `SW12299`,
software version `2.15.0` and manufacture date `2026-09-01`. The screen does not
identify these values as the sensor/transmitter firmware. ADB confirms the
installed Dexcom G7 Android app is version `2.15.0`, so the screenshot's version
likely describes the app software; the exact G7 hardware revision and firmware
still need confirmation.

| Sensor | Candidate watch | Candidate phone | Direct collection | Phone-absent / coexistence | Status and next evidence |
|---|---|---|---|---|---|
| G7 | Pixel Watch 2; ADB: Android 17 / API 37, build ID `CP3A.260905.002.E1`, incremental `16053217`, security patch `2026-08-05`; user reports Wear OS 7 | Pixel 8a; ADB: Android 17 / API 37, build ID `CP3A.261005.005`, incremental `16271449`, security patch `2026-10-05`; Dexcom G7 app `2.15.0` installed | Unknown; no direct BLE evidence in the repository | Unknown; existing QA covers Share sync, not direct collection | Blocked: no isolated test sensor. Revisit only at the user's next sensor change and only after a safety/compatibility review; do not connect now. |
| G6 | Pixel Watch 2 candidate; Wear OS 7 (user-reported), Android/build details as above; deferred and not tested | Pixel 8a; Android 17 / API 37, build ID `CP3A.261005.005` | Unknown / not assessed | Unknown / not assessed | Explicitly deferred; no compatibility claim. |

The existing [G-F3 sign-off](../qa/2026-05-26-g-f3-signoff.md) records phone-to-watch
Share synchronization on a Pixel 8a and Pixel Watch 2. It is not evidence of
direct sensor collection and does not establish a Wear OS version for the direct
test matrix.

An ADB check on 2026-11-03 read the Pixel 8a as Android 17 / API 37, build ID
`CP3A.261005.005`, incremental `16271449`, security patch `2026-10-05`, and the
Pixel Watch 2 as Android 17 / API 37, build ID `CP3A.260905.002.E1`,
incremental `16053217`, security patch `2026-08-05`.
The installed phone package `com.dexcom.g7` reports version `2.15.0`. The
Dexcom package inventory on the watch contains `com.dexcom.g6.region2.mgdl`,
but no `com.dexcom.g7`; package presence alone does not establish a working
sensor connection or G7 compatibility. The watch also reports
`com.google.android.wearable.sysui` version
`7.2.8.14.945869767.prebuilt`; this is a system UI package version, not an
independent confirmation of the Wear OS product version. The user identifies
the watch as Wear OS 7. No G7 direct BLE feasibility test has been run.

**Official support evidence checked 2026-11-03**

- [Dexcom FAQs](https://www.dexcom.com/en-us/faqs) direct users to Dexcom's
  compatible-device list. The page content retrieved for this review did not
  establish whether direct G7 sensor pairing with a Pixel Watch 2 is supported.
- The [Dexcom compatibility page](https://www.dexcom.com/compatibility) did not
  return a readable model list in this review. Official direct-to-Wear support
  is therefore **unverified**, not declared unsupported.
- A legal/privacy review for an independently implemented or reverse-engineered
  direct collector has not been performed. Keep distribution and reuse decisions
  out of scope until the appropriate review is identified and completed.
- Keep three paths distinct in subsequent evidence: the G7 sensor's Bluetooth
  connection to the watch, a phone-relayed reading shown on the watch, and an
  officially supported Dexcom smartwatch integration. Evidence for one is not
  evidence for the others.

**Exit gate G0**

- Feasibility matrix exists for G6 and G7 on named watch/OS combinations.
- Each matrix cell is marked tested, unsupported or unknown with evidence.
- The initial product scope is explicitly G7-first; G6 is deferred and no
  untested combination is advertised as supported.
- Official support evidence, test-hardware availability and the legal/privacy
  review path are recorded. If direct G7 feasibility remains unknown, G0 stays
  blocked for production support. Do not begin sensor-specific implementation
  without isolated test hardware; simulated tests cannot establish compatibility
  or safe coexistence.
- No unverified pairing field, static secret or protocol assumption is recorded
  as a requirement.

**G7 feasibility test procedure (not yet run)**

Do not unpair a sensor, change its Bluetooth pairing, stop an official data path
or rely on an experimental watch connection for treatment decisions.
Direct-connection testing must not interfere with the official Dexcom
monitoring path or be used for treatment decisions. A sensor change does not by
itself make the replacement sensor isolated or safe to experiment with. At the
user's next sensor change, first review whether testing can proceed without
interrupting the official receiver, whether a rollback/stop action is clear,
and whether the user still agrees to proceed. If any condition is uncertain,
do not connect and record the hardware test as blocked. Do not purchase a test
sensor as a prerequisite. Until that review and explicit go/no-go decision,
these steps remain NOT RUN:

1. Record the watch/phone OS and app versions, test-sensor generation and
   non-unique revision/firmware only. Never record serial numbers, Bluetooth
   addresses, glucose values, credentials or raw packets in the QA report.
2. With the phone available for setup, verify that the watch collector reaches
   an explicit receiving state and accepts timestamped test readings.
3. Keep the phone powered off for the agreed observation window; verify direct
   collection continues without phone or internet participation.
4. Restore the phone, interrupt and restore the watch Bluetooth connection, and
   verify reconnect without losing the official fallback setup.
5. Test Share coexistence separately; record source availability and transition
   states, not glucose values. A phone-relayed watch display does not count as
   direct collection.
6. Repeat on the same named hardware and record PASS/FAIL/NOT RUN for each
   scenario, duration, software versions and redacted technical evidence.

Do not treat BLE discovery or a connected indicator alone as proof. Direct
feasibility requires the watch collector to receive valid, timestamped readings
with the phone absent. Until that is demonstrated, compatibility remains
unknown and no direct-support claim may be made.

**Mock-only collector groundwork (2026-11-03)**

The Wear module now has an internal, transport-agnostic collector state boundary
and a unit-test-only fake transport in
`wear/src/main/java/com/glucoseforwatch/wear/direct/DirectSensorCollector.kt`
and `wear/src/test/java/com/glucoseforwatch/wear/direct/DirectSensorCollectorTest.kt`.
The tests exercise configuration/permission gates, scan/connect/receive states,
disconnect/reconnect, invalid event order, unsupported/error reporting and
shutdown, including surfaced transport-start failures, retry after a failed
start, idempotent stop and restart after stop. They use only synthetic
timestamps; no glucose values are generated.

This is simulation groundwork, not a BLE implementation: there is no Android
Bluetooth scanner/GATT code, sensor protocol, manifest permission, app wiring,
Data Layer delivery or hardware evidence. G0 remains blocked and no direct
sensor compatibility is established. Keep further work on this collector
limited to generic, hardware-independent state-machine behavior; do not add
sensor-specific BLE/protocol code while isolated validation is unavailable.

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

**Draft design based on the current implementation (2026-11-03)**

**Phase 1 status: draft; G1 not passed.** Freshness thresholds, timestamp
validation policy, glucose/trend validation, contract tests and design review
remain open. The arbitration unit tests use explicit synthetic thresholds only;
they do not set production freshness values.

Current code evidence:

- Shared `GlucoseReading` contains glucose, trend, measurement timestamp and a
  stale flag, but no source or receive time.
- Wear `GlucoseSnapshot` adds display unit. `GlucoseCache.save` records the
  watch receive time privately, but `load` does not expose it in the snapshot.
- The Data Layer payload carries timestamp, sequence, target watch and optional
  source phone node ID. It has no glucose-source field or schema version;
  `PUSH_VERSION` is a delivery sequence, not a schema version.
- The Wear listener currently writes every accepted phone payload directly to
  the single cache and ACKs it. The direct collector must not be wired to this
  path until arbitration is implemented.

Proposed source-neutral input record:

| Field | Meaning | Validation / compatibility |
|---|---|---|
| `source` | `SHARE` or `DIRECT_G7` | A missing source in a legacy phone payload means `SHARE`. G6 is not enabled by this initial design. |
| `measurementAtEpochMs` | Sensor/source measurement time | Required, positive, and used for age and ordering; never replaced by packet arrival time. |
| `receivedAtEpochMs` | Local watch receipt time | Set on the watch for both paths. Preserve the phone's receive time separately as optional diagnostic provenance if later needed; do not use it to make an older measurement appear new. |
| `valueMgDl`, `trend`, `deltaMgDl` | Normalized measurement content | Validate before arbitration; invalid records are rejected, not converted to a plausible reading. |
| `validity` | Whether the record passed input validation | Invalid records never compete for display. Specify protocol-specific bounds only after source evidence exists. |
| `sourceSequence` | Ordering/ACK token scoped to one transport | Existing Data Layer sequence and ACK semantics remain Share-only; do not compare a phone sequence to a direct-source timestamp or sequence. |

Proposed arbitration rules (pure policy; freshness limits remain undecided):

1. Keep the latest accepted valid record per source. Deduplicate by
   `(source, measurementAtEpochMs)`; identical duplicates are no-ops. Conflicting
   content for the same source and measurement timestamp is rejected and
   reported as a typed input error, not resolved by arrival order. Reject any
   record older than the latest accepted record from that same source. Do not
   reject a fresh candidate merely because its timestamp predates a candidate
   from the other source; source eligibility and measurement order determine
   selection.
2. Derive freshness from measurement time and a source-specific, reviewed
   threshold. Do not treat receive time as measurement freshness. Until
   generation-specific thresholds are approved, direct eligibility is
   **unknown/disabled**, not implicitly fresh.
3. Among valid, fresh candidates, choose the record with the latest measurement
   timestamp. If timestamps tie, prefer `DIRECT_G7`; this preserves newer
   measurements without allowing an older Share packet to overwrite them.
4. If only one source has an eligible candidate, select it. If neither source
   has one, retain the last selected record for stale presentation with its
   original source and measurement time; never relabel it as current.
5. A direct connection state is not a reading and cannot make the cached reading
   fresh. Source health (collector state) and reading freshness are stored and
   displayed separately.
6. Emit a source transition only when the selected source changes; include a
   bounded reason code such as `DIRECT_NEWER`, `SHARE_NEWER`,
   `DIRECT_UNAVAILABLE`, or `NO_FRESH_SOURCE`. Do not log glucose values or
   sensor identifiers.

Source selection table (freshness is evaluated independently per source):

| Direct candidate | Share candidate | Selected display record | Health/status |
|---|---|---|---|
| Fresh | Fresh | Later measurement timestamp; direct wins an exact tie | Show selected source; report both collectors independently |
| Fresh | Missing, invalid or stale | Direct | Share fallback unavailable/stale; direct health remains separate |
| Missing, invalid or stale | Fresh | Share | Show Share fallback; report direct collector's typed state |
| Missing, invalid or stale | Missing, invalid or stale | Last selected value marked stale, or no value if none exists | `NO_FRESH_SOURCE`; report each source state |

Additive Data Layer migration proposal:

| Contract element | Proposal |
|---|---|
| Schema version | Add a dedicated source-envelope schema version key; do not repurpose `PUSH_VERSION`. |
| Source | Add a source key. A legacy `/glucose/latest` item with both envelope fields absent means `SHARE`; a partial, unknown-version or unknown-source envelope is rejected. |
| Measurement time | Keep the existing timestamp key as the measurement timestamp for Share and direct records. |
| Receive time | Record watch receipt locally. An optional phone-received time is provenance only and does not replace measurement time. |
| Sequence / ACK | Keep sequence, queued pushes, retries and ACKs scoped to Share. Direct input is local to Wear and does not generate a phone delivery ACK. |
| Mixed versions | New watch accepts legacy Share payloads. New phone may continue sending the current payload while the watch migrates. A new source envelope must not be required by old receivers; never send direct records over the phone Data Layer in this phase. |
| Rollback | Keep the existing Share payload and cache behavior available behind the default-off migration path until compatibility tests pass. |

Persistence and presentation proposal:

- Retain one latest validated candidate per source plus the selected record and
  collector health state in watch-local storage. The existing display surfaces
  continue reading one selected snapshot; do not add an independent
  app/tile/complication arbitration path.
- Store source, measurement time, watch receive time, stale status and selected
  revision with the selected snapshot. Avoid retaining raw sensor packets,
  credentials, transmitter identifiers or unnecessary reading history.
- Add explicit source and age labels to watch status surfaces only after
  localization and small-screen review. A stale cached reading keeps its source
  label and is visibly stale; "connected" never substitutes for "fresh."
- Keep the existing two-minute stale policy unchanged for legacy Share until a
  separately reviewed freshness policy is approved. It is not a direct-source
  failover threshold.

Review items before G1 can pass: validate measurement bounds and clock-skew
handling; approve per-source freshness thresholds and stale presentation; review
the state-transition table; confirm the legacy/mixed-version behavior with
contract tests; and approve the proposed minimal retention policy. The
source-selection rows now have synthetic unit coverage for fresh, missing and
stale candidates; this verifies implementation behavior, but does not approve
the product policy or production thresholds.

**Synthetic arbitration implementation (2026-11-03)**

Added [GlucoseSourceArbiter.kt](../../core/model/src/main/java/com/glucoseforwatch/core/model/GlucoseSourceArbiter.kt)
and [GlucoseSourceArbiterTest.kt](../../core/model/src/test/java/com/glucoseforwatch/core/model/GlucoseSourceArbiterTest.kt).
The pure component exercises source ordering, exact-timestamp tie-breaking,
duplicate/conflict detection, late/out-of-order rejection, stale-cache
retention, future/invalid timestamp handling, receipt-time validation,
freshness-boundary behavior, Share fallback and direct recovery. Ordering
rejection is per source: a Share reading older than a newer but stale direct
reading may still be selected as fresh fallback, while it cannot displace a
newer fresh direct reading. A `null` G7 freshness threshold rejects direct
input, so it remains disabled unless an explicit threshold is supplied.

Focused validation:

```powershell
.\gradlew.bat :core:model:testDebugUnitTest --tests "com.glucoseforwatch.core.model.GlucoseSourceArbiterTest"
```

Result at implementation: **passed, 12 tests**. Follow-up fallback/recovery
coverage found and fixed an overly broad cross-source ordering rejection. The
source-selection table also has explicit synthetic coverage for both sources
fresh, either source missing, stale-direct Share fallback and no fresh source.
The contract, model and Wear suites now pass **75 tests total**, including the
new selection-table cases.

This arbiter is not connected to the Data Layer, cache, UI or collector. It does
not validate physiological value ranges or trend tokens, select clinical
thresholds, resolve clock synchronization, or establish that a reading is
authentic. Do not use it to display real readings or infer sensor compatibility.

**Data Layer source-envelope compatibility groundwork (2026-11-03)**

Added dedicated `readingSource` and `sourceEnvelopeVersion` keys and a typed,
pure resolver in `GlucoseDataLayerContract`. Legacy payloads with neither field
are resolved as `SHARE` and marked legacy; version 1 accepts only `SHARE` and
`DIRECT_G7`; partial, unknown-version and unknown-source envelopes return
explicit rejection reasons. Mobile and Wear key aliases expose the fields for a
later coordinated migration. The existing publisher/listener do not write or
consume them yet, so Share transport behavior is unchanged.

Focused validation:

```powershell
.\gradlew.bat :core:datalayer-contract:testDebugUnitTest :core:model:testDebugUnitTest :wear:testDebugUnitTest
```

Result: **passed, 66 tests across contract, model and Wear; 0 failures**.
`:mobile:compileDebugKotlin` and `:wear:compileDebugKotlin` also passed with the
shared contract keys.

Added a Wear-side pure payload mapper,
[GlucosePayloadMapper.kt](../../wear/src/main/java/com/glucoseforwatch/wear/direct/GlucosePayloadMapper.kt),
with tests for legacy Share mapping, versioned direct mapping, missing fields,
invalid timestamps and rejected envelopes. A synthetic legacy payload is also
passed through the source arbiter to verify the boundary end to end. The mapper
is not called by `WearDataLayerListenerService`; production reception and ACK
behavior remain unchanged.

Latest validation:

```powershell
.\gradlew.bat :core:datalayer-contract:testDebugUnitTest :core:model:testDebugUnitTest :wear:testDebugUnitTest :mobile:compileDebugKotlin :wear:compileDebugKotlin
```

Result: **passed, 71 unit tests across contract, model and Wear; 0 failures**;
both phone and watch Kotlin compile tasks passed.

**Exit gate G1**

- Reviewed design and state-transition table cover all source combinations.
- Pure arbitration policy is specified independently of Android BLE APIs.
- Existing Share path and existing watch clients have a documented compatibility
  and rollback story.
- Proposed state and contract changes are reviewed; no production payload,
  persistence or UI behavior has changed as part of this draft.

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
2. **Resolved for initial feasibility work:** assess G7 first; defer G6. Any
   advertised support remains limited to combinations that pass G0.
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

Keep G0 blocked and direct collection out of the production path. Continue only
hardware-independent work that can be meaningfully tested (contract
compatibility, synthetic arbitration/state-machine tests, and review of public
official-support information), while preserving the existing Share behavior.
At the user's next sensor change, revisit the feasibility procedure and make a
fresh go/no-go decision before any connection attempt; do not treat the sensor
change itself as proof that an experiment is safe. Do not connect now, add
sensor-specific BLE/protocol code, or make direct-support claims. If a safe,
non-interfering test cannot be established then, direct collection remains an
unvalidated, non-release goal.
