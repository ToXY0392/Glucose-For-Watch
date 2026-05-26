# C.1 — AGP 60 / 120 / 200

| Field | Value |
|-------|-------|
| Date | 2026-05-26 |
| Branch | `workspace/qa-hardware` |
| Gate | G-C |
| Status | **N/A — skipped** (no manual visual) |

## Decision

Manual AGP color check (phone hero + watch tile/complication at 60 / 120 / 200 mg/dL) **skipped** for v0.5.0 sideload. Operator policy: visual validation not required when automated coverage exists.

## Substitute evidence

| Source | Coverage |
|--------|----------|
| `GlucoseRangeResolverTest` | AGP palette at 60 / 120 / 200 |
| `WearGlucoseSurfaceModelFactoryTest` | wear surface colors |
| `AgpComplicationColorRampTest` | complication color ramp |
| C.2 sample [2026-05-26_0449](2026-05-26_0449-C2-complication-sample.md) | phone hero = watch cache 207→152 mg/dL (values + sync OK) |
| `hardware-smoke.ps1` | hero + push/ack aligned |

Spec reference: [AGP medical layer](../../toxy-ux-kit/spec/01-agp-medical-layer.md)

## Plateaus (not visually confirmed — waived)

| mg/dL | AGP band | Visual | Notes |
|-------|----------|--------|-------|
| 60 | Low L1 | skipped | unit tests |
| 120 | In range | skipped | smoke 135 mg/dL in band |
| 200 | High L1 | skipped | C.2 session saw 207–215 mg/dL |

## Sign-off

| Role | Date | OK |
|------|------|-----|
| C.1 waived (automated substitute) | 2026-05-26 | ✅ |

Optional PNG script (not run): `scripts/qa/capture-c1-agp-session.ps1`
