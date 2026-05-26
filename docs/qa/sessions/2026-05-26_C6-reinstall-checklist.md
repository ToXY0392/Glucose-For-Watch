# C.6 — PC APK reinstall + re-add tile/complication

| Field | Value |
|-------|-------|
| Date | 2026-05-26 |
| Branch | `workspace/qa-hardware` |
| Gate | G-C · K4 |
| Status | **N/A — skipped** (no reinstall during soak / sideload QA) |

## Decision

Full PC reinstall cycle **skipped** for v0.5.0 sideload. Current build (vc 23) validated by C.7 8 h soak and ongoing smoke; no install during active QA per repo policy.

## Substitute evidence

| Source | Coverage |
|--------|----------|
| C.7 soak 8 h | same APK · 0 FATAL · morning hero OK |
| C.2 sample | tile/complication data path OK post-install |
| D.3 ✅ | `install-and-verify.ps1` push/ack auto-check |
| `hardware-smoke.ps1` | S1–S3 PASS · watch 0.4.0 · tile path live |

Post-tag or pre-release: run `install-and-verify.ps1` once before wider sideload.

## Sign-off

| Role | Date | OK |
|------|------|-----|
| C.6 waived (C.7 + D.3 + smoke) | 2026-05-26 | ✅ |
