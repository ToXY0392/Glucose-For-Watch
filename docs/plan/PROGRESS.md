# Plan tracking — Glucose For Watch

> **Last updated:** 2026-05-26 · **G-M8 closed** · tag `v0.6.0` · post-v0.6 polish in progress  
> **Distribution:** PC only (`installWidgetG7Debug`) — no Play Store  
> **Plan docs:** [ACTION-PLAN.md](ACTION-PLAN.md) (operational) · [STABILITY-GATES.md](STABILITY-GATES.md) · [PR-CHECKLIST.md](PR-CHECKLIST.md)

---

## Scoreboard (weekly update)

| Gate | Status | Date | Blocks |
|------|--------|------|--------|
| **G-X** | ✅ | 2026-05-26 | X.6 + X.7 + C.7 (X.3) |
| **G-A** | ✅ | 2026-05-26 | M, B, C |
| **G-M** | ✅ | 2026-05-26 | B |
| **G-B** | ✅ | 2026-05-26 | C |
| **G-C** | ✅ | 2026-05-26 | — |
| **G-D** | ✅ | 2026-05-26 | M7 |
| **G-M7** v0.5.0 | ✅ | 2026-05-26 | — |
| **G-F3** Home Compose | ✅ | 2026-05-26 | M8 |
| **G-M8** v0.6.0 | ✅ | 2026-05-26 | — |

| KPI | Current | Target | Evidence |
|-----|---------|--------|----------|
| K1 Fatal crash | 0 | 0 | [incident closed](../qa/incidents/2026-05-25-app-crash.md) |
| K2 8 h soak | ✅ | ✅ C.7 | [soak](../qa/soak-runs/2026-05-26_C.7-soak.md) · [sign-off](../qa/2026-05-26-stability-signoff.md) |
| K3 30 min sync | ✅ | ✅ | [X.6 soak](../qa/soak-runs/2026-05-25_1458-X.6-soak.md) |
| K4 S1–S3 | ✅ post-F3 | ✅ post-F3 | [G-F3 sign-off](../qa/2026-05-26-g-f3-signoff.md) |
| K5 Unit tests | CI OK + Dexcom 9 tests | 100 % | verify_ci |
| K6 QA G7 | **7/7** | 7/7 | [bloc-c-evidence](../qa/bloc-c-evidence.md) · C.7 + waivers |
| K7 Disconnect | ✅ | ✅ | A.2 |

---

## Version goals

| Version | Goal | Gate | Target week |
|---------|------|------|-------------|
| **v0.5.0** sideload | Stable · QA 7/7 · PC install | G-M7 | S4 |
| **v0.6.0** Compose | Phone UI Compose M3 · sync OK | G-M8 | S8 |

**Critical path:** X.5 → G-X → … → **C.7 soak** → G-M7 → F3 → G-M8  
→ Detail: [ACTION-PLAN §2](ACTION-PLAN.md#2-critical-path)

---

## Current state

| Indicator | Value |
|-----------|-------|
| App | v0.6.0 (vc 25) |
| Phone | Pixel 8a · Android 14+ |
| Watch | Pixel Watch 2 (session 05-26) |
| P0 crash | FGS mitigated · C.7 8h PASS · incident closed | |
| Phone UI | **Compose M3** ✅ |
| Wear UI | Compose M3 ✅ |

---

## Plan architecture — 9 blocks · 12 gates · 2 milestones

```
S  Stability (cross-cutting, each PR)
├── v0.5.0 ─────────────────────────
│   X  Crash          PR #8   → G-X      ★ CRITICAL
│   A  P0 reliability PR #9   → G-A
│   M  Mock user      PR #10  → G-M      ║ parallel after G-A
│   B  Sync/wear      PR #11  → G-B      ║
│   C  QA + soak      PR #12  → G-C      ★ C.7 blocks M7
│   D  Quality        PR #13-14 → G-D
│   M7 tag v0.5.0
└── v0.6.0 ─────────────────────────
    F  Compose M3     PR #15-18 → G-F* → M8
```

**Strict v0.5.0 order:** S · X → A → (M ∥ prep B) → B → C → D → M7  
**Golden rule:** [PR-CHECKLIST.md](PR-CHECKLIST.md) + `stability-gate.ps1` before **every** merge

---

## Block S — Cross-cutting stability

| ID | Task | Status |
|----|------|--------|
| S.1 | STABILITY-GATES.md | 🔄 |
| S.2 | stability-gate.ps1 | 🔄 |
| S.3 | stability-signoff-template | 🔄 |
| S.4 | FGS fallback test (X.7) | ✅ |
| S.5 | Gate in dev/setup.md / CONTRIBUTING | ✅ |
| S.6 | PR-CHECKLIST.md | 🔄 |
| S.7 | hardware-smoke: FAIL if push≠ack (not WARN) | ✅ |
| S.8 | ACTION-PLAN.md (calendar + risks) | 🔄 |

```powershell
.\scripts\qa\stability-gate.ps1 -Strict    # before merge
.\scripts\qa\capture-crash-log.ps1         # after crash
```

---

## Block X — Phone crash (P0) · PR #8 · Gate G-X

> [Incident](../qa/incidents/2026-05-25-app-crash.md) · overnight · charging · `ActiveGlucoseSyncService:32`

| ID | Task | Status | Est. |
|----|------|--------|------|
| X.1 | Logcat captured | ✅ | — |
| X.2 | Overnight + charging report | ✅ | — |
| X.3 | Soak repro / FGS quota | ✅ | [C.7](../qa/soak-runs/2026-05-26_C.7-soak.md) |
| X.4 | FGS root cause | ✅ | — |
| X.5a | try/catch startForeground | ✅ | 2h |
| X.5b | Worker/alarm fallback | ✅ | 3h |
| X.5c | Deduplicate FGS schedulers | ✅ | 4h |
| X.6 | 30 min without crash | ✅ | [soak](../qa/soak-runs/2026-05-25_1458-X.6-soak.md) |
| X.7 | Unit test FGS denied | ✅ | 3h |

**G-X DoD:** X.5a–c + X.6 + X.7 + 0 FATAL · [criteria](STABILITY-GATES.md#g-x--after-pr-8-block-x)

---

## Block A — P0 reliability · PR #9 · Gate G-A

| ID | Task | Status | Est. |
|----|------|--------|------|
| A.1 | POST_NOTIFICATIONS runtime | ✅ | 2h |
| A.2 | Entry disconnect = settings | ✅ | 1h |
| A.3 | Manual sync → real result | ✅ | 2h |
| A.4 | Strings → strings.xml | ✅ | Dexcom entry/settings |

**G-A DoD:** G-X ✅ + K7 disconnect + notifs + [STABILITY-GATES § G-A](STABILITY-GATES.md#g-a--after-pr-9-block-a)

---

## Block M — User mock · PR #10 · Gate G-M

| ID | Task | Status | Est. |
|----|------|--------|------|
| M.1 | HomeUiState + HomeViewModel (F0.3 bridge) | ✅ | 6h |
| M.2 | Hero ↔ tile parity (time, stale) | ✅ | 4h |
| M.3 | 6 exportable preview states | ✅ | 3h |
| M.4 | design-reference companion | ✅ | PR chore · `export-design-reference.py` |
| M.5 | Preview doc dev/setup.md | ✅ | dev/setup.md |

**G-M DoD:** G-A ✅ + previews + smoke S1–S3 · can merge **before** B if B not ready

---

## Block B — Sync & wear · PR #11 · Gate G-B

| ID | Task | P | Status | Est. |
|----|------|---|--------|------|
| B.1 | Complication cache invalidated | P1 | ✅ | 4h |
| B.2 | UI push vs ack / visible error | P1 | ✅ | 3h |
| B.5 | Wear tile FR | P1 | ✅ | 30m |
| B.3 | Single scheduler doc | P2 | ✅ | 2h |
| B.4 | WatchSyncVerifier → engine | P2 | ✅ | PR #40 · ack wait test |

**G-B DoD:** complication ≤45s lag · FR tile · smoke seq · [STABILITY-GATES § G-B](STABILITY-GATES.md#g-b--after-pr-11-block-b)

---

## Block C — Hardware QA · PR #12 · Gate G-C ★

> **Without C.7 (8 h soak) = no v0.5.0 tag**

| ID | Scenario | Duration | KPI | Status |
|----|----------|----------|-----|--------|
| C.0 | Crash reg · kill/relaunch · sync ×10 | 30m | K1 | ✅ | [session](../qa/sessions/2026-05-25_1605-bloc-c-automated.md) · sync button ×10 manual optional |
| C.1 | AGP 60/120/200 | 2h | visual | **N/A** | [waived](../qa/sessions/2026-05-26_C1-agp-checklist.md) · unit tests + C.2 |
| C.2 | Complication vs tile | 30m | K3 | ✅ | [sample](../qa/sessions/2026-05-26_0449-C2-complication-sample.md) |
| C.3 | Watch offline 2h | 2–3h | catch-up | **N/A** | [waived](../qa/sessions/2026-05-26_C3-offline-checklist.md) |
| C.4 | LOW / HI | — | display | **N/A** | [waived](../qa/sessions/2026-05-26_C4-low-hi-checklist.md) · not observed |
| C.5 | Continuous sync | 30m | K3 | ✅ | [X.6 soak](../qa/soak-runs/2026-05-25_1458-X.6-soak.md) + smoke S3 |
| C.6 | APK reinstall + tile | 1h | K4 | **N/A** | [waived](../qa/sessions/2026-05-26_C6-reinstall-checklist.md) |
| **C.7** | **Overnight charge soak** | **8h** | **K2** | ✅ | [soak](../qa/soak-runs/2026-05-26_C.7-soak.md) |
| C.8 | Watch battery ≤20% | 1h | K1 | **N/A** | [waived](../qa/sessions/2026-05-26_C8-battery-checklist.md) · unit tests |

**C.7 procedure:** [ACTION-PLAN §7](ACTION-PLAN.md#7-weekly-ritual) · deliverable [sign-off](../qa/stability-signoff-template.md)

---

## Block D — Quality · PR #13–14 · Gate G-D → M7

| ID | Task | Status | Est. |
|----|------|--------|------|
| D.1 | DexcomShareClient tests (5+ cases) | ✅ | 6h |
| D.2 | Dead code cleanup home | ✅ | 2h |
| D.3 | install-and-verify push/ack auto | ✅ | 4h |
| D.4 | Single PC install doc | ✅ | dev/setup.md |
| D.5 | Docs index plan | 🔄 | — |
| D.6 | capture-crash-log.ps1 | ✅ | doc in [dev/setup.md](../dev/setup.md) · J1 2026-05-26 |
| D.7 | stability-gate in dev/setup.md | ✅ | dev/setup.md |

**v0.5.0 tag:** [G-M7 checklist](STABILITY-GATES.md#g-m7--tag-v050)

---

## Block F — Compose M3 · PR #15–18 · Gate G-M8

| Phase | PR | Gate | Duration |
|-------|-----|------|----------|
| F0 Foundations | #30 | G-F0 | 2–3d |
| F1 Legal/Notice | #32 | G-F1 | 2–3d |
| F2 Dexcom/Watch | #33 | G-F2 | 1 week |
| F3 **Home** | #34 | G-F3 | 1–1.5 weeks |
| F4 Installer (opt.) | — | — | opt. |
| F5 Cleanup | #36 | G-M8 | 2–3d |

Screen detail + tasks: [ACTION-PLAN §11](ACTION-PLAN.md#11-compose-v060) · [PROGRESS Block F history](ACTION-PLAN.md)

**Forbidden before G-M7:** touching MainActivity Compose

---

## Decision register

| Date | Decision | Reason |
|------|----------|--------|
| 2025-05-25 | No Play Store · PC sideload | Personal / dev use |
| 2025-05-25 | C.7 8h soak blocks M7 | Overnight crash reproduced |
| 2025-05-25 | Compose after v0.5.0 | Stability first |
| 2025-05-25 | WearInstaller stays XML (F4 opt.) | OCR = risk |
| 2026-05-26 | G-F3 K2 = C.7 baseline · post-F3 4 h optional | C.7 8 h PASS · 30 min G-F3-sync · no dedicated 4 h needed |

---

## Daily workflow

```powershell
# Dev loop
.\gradlew.bat installWidgetG7Debug

# Before PR merge
.\scripts\qa\stability-gate.ps1 -Strict

# QA session
.\scripts\qa\hardware-smoke.ps1
.\scripts\qa\qa-session-c.ps1
.\scripts\qa\tail-sync-logs.ps1

# Soak X.6 (30 min) or C.7 (8 h)
.\scripts\qa\soak-monitor.ps1                      # X.6
.\scripts\qa\soak-monitor.ps1 -DurationMinutes 480 -Label C.7
# morning:
.\scripts\qa\capture-crash-log.ps1
.\scripts\qa\stability-gate.ps1 -CheckLogcatOnly
```

---

## Backlog (post-v0.6)

Play Store · Dexcom OAuth v3 · mmol/L · watch face · G6 QA · Installer Compose
