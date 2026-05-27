# Stability gates — Glucose For Watch

> **Role:** **blocking** criteria between each block in the [PROGRESS.md plan](PROGRESS.md).  
> **Rule:** no tag (`v0.5.0`, `v0.6.0`) without a signed **Go** on the corresponding gate.

---

## Stability KPIs (Definition of Stable)

| KPI | v0.5.0 threshold | Measurement |
|-----|------------------|-------------|
| **K1 Fatal phone crash** | 0 on mandatory scenarios | `adb logcat AndroidRuntime:E` · no "has stopped" |
| **K2 Overnight soak** | 8 h phone charging, screen off, Dexcom ON | Logcat + morning glucose hero |
| **K3 30 min sync** | push/ack seq aligned · phone/watch drift ≤ 1 reading | `hardware-smoke.ps1` + observation |
| **K4 Sync S1–S3** | ✅ after each PR touching `mobile/sync/` or `wear/` | Hardware session or enriched smoke |
| **K5 Unit tests** | 100 % green | `./gradlew test` + `verify_ci.sh` |
| **K6 QA G7 matrix** | 7/7 + session 0 crash | [Block C](PROGRESS.md#block-c--hardware-qa--pr-12--gate-g-c-) |
| **K7 Dexcom disconnect** | sync stopped · prefs consistent · no ghost FGS | Manual test post A.2 |
| **K8 Compose (v0.6)** | sync unchanged after each F phase | Smoke S1–S3 without regression |

---

## Test pyramid

```
                    ┌─────────────────────┐
                    │  Soak 8h + QA 7/7   │  ← tag v0.5.0 (M7)
                    └──────────┬──────────┘
               ┌───────────────┴───────────────┐
               │  Hardware sessions C.0–C.3    │  ← weekly / per block
               └───────────────┬───────────────┘
          ┌────────────────────┴────────────────────┐
          │  stability-gate.ps1 (smoke + logcat)      │  ← each PR
          └────────────────────┬────────────────────┘
     ┌─────────────────────────┴─────────────────────────┐
     │  verify_ci.sh (unit + compile)                     │  ← each commit
     └─────────────────────────────────────────────────────┘
```

---

## Exit gate per block

### G-X — after PR #8 (Block X)

| # | Criterion | Command / procedure |
|---|-----------|---------------------|
| 1 | Unit test: FGS denied → no crash, Worker fallback | `:mobile:test` (test to add X.7) |
| 2 | Soak repro: 8 h charge + screen off OR artificial FGS quota repro | X.3 |
| 3 | 0 `FATAL EXCEPTION` logcat post-soak | `stability-gate.ps1 -CheckLogcat` |
| 4 | Manual + auto sync OK next morning | hero + tile |

**Blocks M7 if any single ☐**

---

### G-A — after PR #9 (Block A)

| # | Criterion |
|---|-----------|
| 1 | G-X still ✅ |
| 2 | Notification permission: grant/deny handled without crash |
| 3 | Entry **and** settings disconnect: sync off, `PhoneSyncStateStore` empty |
| 4 | Manual sync: Snackbar success **or** error (never false success) |
| 5 | `verify_ci.sh` green |

---

### G-M — after PR #10 (Block M)

| # | Criterion |
|---|-----------|
| 1 | G-A ✅ |
| 2 | 6 exportable preview states |
| 3 | Hero/tile parity on 1 visual session (120 mg/dL min.) |
| 4 | No sync regression (smoke S1–S3) |

---

### G-B — after PR #11 (Block B)

| # | Criterion |
|---|-----------|
| 1 | G-M ✅ |
| 2 | Complication = tile after forced sync (30 min sample) |
| 3 | FR tile · no wear crash |
| 4 | smoke push/ack seq |

---

### G-C — after PR #12 (Block C) — **main v0.5.0 gate**

Mandatory sessions:

| ID | Scenario | Duration | KPI |
|----|----------|----------|-----|
| C.0 | Crash reg · repeated sync · app lifecycle | 30 min | K1 |
| C.1 | AGP 60/120/200 phone + tile | 2 h | visual |
| C.2 | Complication vs tile | 30 min | K3 |
| C.3 | Watch offline 2 h · phone active · reconnect | 2–3 h | catch-up |
| C.4 | LOW / HI if available | — | display |
| C.5 | Continuous sync | 30 min | K3 |
| C.6 | PC APK reinstall + re-add tile/complication | 1 h | K4 |
| **C.7** | **Overnight soak** phone charging · Dexcom · auto sync | **8 h** | **K2** |
| C.8 | Watch battery ≤ 20 % · degraded mode | 1 h | no phone crash |

Deliverable: `docs/qa/YYYY-MM-DD-stability-signoff.md` + captures + logcat excerpt

---

### G-D — after PR #13–14 (Block D)

| # | Criterion |
|---|-----------|
| 1 | G-C ✅ (including C.7 soak) |
| 2 | `DexcomShareClient` tests green |
| 3 | `install-and-verify.ps1` + auto push/ack checks |
| 4 | `stability-gate.ps1` documented in dev/setup.md |

---

### G-M7 — tag v0.5.0

**Go / No-Go** checklist:

- [x] Gates G-X → G-D all ✅
- [x] KPIs K1–K7 validated
- [x] Incident [2026-05-25-app-crash.md](../qa/incidents/2026-05-25-app-crash.md) closed (X.6 + C.7)
- [x] `./gradlew test` + `verify_ci.sh` green on tagged commit
- [x] `hardware-smoke.ps1` OK (no critical FAIL)
- [x] G7 matrix 7/7 signed

---

## Compose gates (v0.6.0)

| Gate | After | Minimal criterion |
|------|-------|---------------------|
| **G-F0** | PR #15 | Compile · tests green · **no screen migrated** · sync identical |
| **G-F1** | PR #16 | Legal/Notice Compose · navigation OK · smoke |
| **G-F2** | PR #17 | Dexcom connect/disconnect · WatchSetup · **G-A disconnect retest** |
| **G-F3** | PR #18 | Home Compose · 30 min sync · S1–S3 · **no K2 regression** — satisfied by **C.7 8 h baseline** + 0 FATAL since F3 install (dedicated 4 h post-F3 soak **optional**, archive only) |
| **G-M8** | tag v0.6.0 | G-F0→F3 ✅ · K8 · full smoke |

**Compose rule:** 1 migrated screen = 1 gate · no merging F2+F3 in one PR.

---

## Tools

| Script | Usage |
|--------|-------|
| `bash scripts/dev/verify_ci.sh` | CI gate · each PR |
| `.\scripts\qa\stability-gate.ps1` | CI + smoke + FATAL logcat |
| `.\scripts\qa\capture-crash-log.ps1` | Post-crash · feed incident report |
| `.\scripts\qa\hardware-smoke.ps1` | Push/ack, hero, watch health |
| `.\scripts\qa\install-and-verify.ps1` | Install + checklist |
| `.\scripts\qa\tail-sync-logs.ps1` | Debug sync / crash |

Operational plan: [ACTION-PLAN.md](ACTION-PLAN.md) · Merge checklist: [PR-CHECKLIST.md](PR-CHECKLIST.md)

---

## Rollback policy

| Situation | Action |
|-----------|--------|
| FATAL post-merge sync PR | Revert PR · incident doc · G-X retest |
| C.7 soak fails | No M7 tag · return to Block X/B |
| Compose F* breaks sync | Revert migrated screen · XML restored |
| Non-critical smoke WARN | Note · fix before M7 if sync touched |

---

## Sign-off

| Gate | Date | Validator | OK |
|------|------|-----------|-----|
| G-X | 2026-05-26 | operator | ✅ |
| G-A | 2026-05-26 | operator | ✅ |
| G-M | 2026-05-26 | operator | ✅ |
| G-B | 2026-05-26 | operator | ✅ |
| G-C | 2026-05-26 | operator | ✅ |
| G-D | 2026-05-26 | operator | ✅ |
| G-M7 v0.5.0 | 2026-05-26 | operator | ✅ |
| G-F3 Home Compose | 2026-05-26 | operator | ✅ |
| G-M8 v0.6.0 | 2026-05-26 | operator | ✅ |
