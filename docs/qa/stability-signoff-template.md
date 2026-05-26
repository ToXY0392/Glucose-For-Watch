# Stability sign-off — Glucose For Watch

> Copy this file: `docs/qa/YYYY-MM-DD-stability-signoff.md`  
> Gate reference: [STABILITY-GATES.md](../plan/STABILITY-GATES.md)

---

## Context

| Field | Value |
|-------|--------|
| Date | |
| App version | |
| Commit / tag | |
| Phone | model · serial · Android |
| Watch | model · Wear OS |
| Operator | |
| Target gate | G-X / G-A / G-C / G-M7 / G-M8 |

---

## KPI

| KPI | Threshold | Result | OK |
|-----|-------|----------|-----|
| K1 Fatal crash | 0 | | ☐ |
| K2 8 h soak (C.7) | morning hero + 0 FATAL | | ☐ |
| K3 30 min sync | push/ack OK | | ☐ |
| K5 verify_ci | PASS | | ☐ |

---

## Block C scenarios

| ID | Scenario | Result | Evidence |
|----|----------|----------|----------|
| C.0 | Crash reg 30 min | | |
| C.1 | AGP colors | | |
| C.2 | Complication 30 min | | |
| C.3 | Offline 2 h | | |
| C.4 | LOW/HI | | |
| C.5 | Sync 30 min | | |
| C.6 | Reinstall + tile | | |
| C.7 | **8 h overnight soak** | | |
| C.8 | Watch battery ≤ 20 % | | |

---

## Automated

```text
stability-gate.ps1 : PASS / FAIL
hardware-smoke.ps1 : PASS / FAIL / WARN
logcat FATAL       : none / (paste excerpt)
```

---

## Open incidents

| Date | Incident | Status |
|------|----------|--------|
| | | |

---

## Decision

- [ ] **GO** — gate validated
- [ ] **NO-GO** — block tag / merge

Rationale:

Sign-off:
