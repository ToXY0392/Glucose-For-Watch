# Operational action plan — Glucose For Watch

> **Master document** · complements [PROGRESS.md](PROGRESS.md) (tracking) and [STABILITY-GATES.md](STABILITY-GATES.md) (criteria).  
> **Last updated:** 2026-05-25 · **Distribution:** PC sideload · **Target:** v0.5.0 stable → v0.6.0 Compose

---

## Table of contents

1. [Principles](#1-principles)
2. [Critical path](#2-critical-path)
3. [8-week calendar](#3-8-week-calendar)
4. [Block matrix](#4-block-matrix)
5. [Atomic tasks](#5-atomic-tasks)
6. [Parallelization](#6-parallelization)
7. [Weekly ritual](#7-weekly-ritual)
8. [Risk register](#8-risk-register)
9. [Automation vs manual](#9-automation-vs-manual)
10. [PR checklist](#10-pr-checklist)
11. [Compose v0.6.0](#11-compose-v060)

---

## 1. Principles

| # | Principle | Application |
|---|----------|-------------|
| P1 | **Stability before features** | No tag without gate · overnight crash = P0 |
| P2 | **One PR = one measurable goal** | No mixing sync + UI + refactor |
| P3 | **Proof before merge** | `stability-gate.ps1` + block gate |
| P4 | **Soak mandatory** | C.7 8 h blocks M7 · 4 h blocks M8 |
| P5 | **Fast revert** | Sync/UI PR revertible in < 15 min |
| P6 | **Sync = sacred contract** | S1–S3 retest if touching `mobile/sync`, `wear/`, `feature/sync` |
| P7 | **Solo dev: sequence** | No big bang Compose |

---

## 2. Critical path

```
X.5 fix FGS ──► G-X ──► A P0 ──► G-A ──► B wear ──► G-B
                              └──► M mock (parallel after G-A)
                                        │
C.7 soak 8h ◄── C.0–C.6 ◄── G-B + G-M ◄┘
    │
    ▼
G-C ──► D tests ──► G-D ──► G-M7 (v0.5.0)
                              │
                              ▼
                    F0 → F1 → F2 → F3 → G-M8 (v0.6.0)
```

**Bottleneck #1:** PR #8 (FGS crash) — blocks everything.  
**Bottleneck #2:** C.7 8 h soak — blocks v0.5.0 tag.  
**Bottleneck #3:** F3 home Compose — blocks v0.6.0 tag.

---

## 3. 8-week calendar

| Week | Days | Block | Deliverable | Gate |
|------|------|-------|-------------|------|
| **S1** | Mon–Wed | **X** | PR #8 merge · test X.7 | **G-X** |
| S1 | Thu–Fri | **A** | PR #9 · notifs + disconnect | G-A |
| **S2** | Mon–Tue | **M** | PR #10 · HomeViewModel + previews | G-M |
| S2 | Wed–Fri | **B** | PR #11 · complication + tile i18n | G-B |
| **S3** | Mon | **C.0–C.1** | Crash reg + AGP visual | — |
| S3 | Tue–Wed | **C.2–C.3** | Complication 30m + offline 2h | — |
| S3 | Thu | **C.4–C.6** | LOW/HI · sync 30m · reinstall | — |
| S3 | Fri evening | **C.7 start** | Launch overnight soak | — |
| **S4** | Mon | **C.7 end + C.8** | Morning logcat · watch battery ≤20% | **G-C** |
| S4 | Tue–Thu | **D** | PR #13–14 · Dexcom tests · scripts | G-D |
| S4 | Fri | **M7** | Tag `v0.5.0` | **G-M7** |
| **S5** | | **F0** | PR #15 · Gradle Compose + theme | G-F0 |
| S5–6 | | **F1–F2** | PR #16–17 · simple screens + Dexcom | G-F1, G-F2 |
| **S7** | | **F3** | PR #18 · HomeScreen Compose | G-F3 |
| S7 | overnight | soak 4h | Post-F3 | — |
| **S8** | | **F5 + M8** | XML cleanup · icons · tag `v0.6.0` | **G-M8** |

*Buffer: +1 week if C.7 or F3 slips.*

---

## 4. Block matrix

| Block | PR | Effort | Gate | Key KPIs | Hot files |
|-------|-----|--------|------|----------|-----------|
| **S** | — | ongoing | each PR | K5 | scripts/qa/* |
| **X** | #8 | 2–3 d | G-X | K1, K2 | `ActiveGlucoseSyncService.kt`, schedulers |
| **A** | #9 | 2 d | G-A | K7 | `MainActivity`, `DexcomEntryActivity`, notifs |
| **M** | #10 | 3 d | G-M | — | `MainActivity`, preview tests |
| **B** | #11 | 4 d | G-B | K3, K4 | `ComplicationUpdateNotifier`, tile wear |
| **C** | #12 | 4 sessions | **G-C** | K1–K6 | hardware · docs/qa/ |
| **D** | #13–14 | 3 d | G-D | K5 | `DexcomShareClient`, install scripts |
| **F** | #15–18 | 3–4 weeks | G-M8 | K8 | `mobile/ui/*`, layouts |

---

## 5. Atomic tasks

### Block X — Crash (PR #8)

| ID | Task | DoD | Est. |
|----|------|-----|------|
| X.5a | `try/catch` `ForegroundServiceStartNotAllowedException` in `onCreate` | Service starts or fallback without FATAL | 2h |
| X.5b | "FGS unavailable" flag → sync via Worker/alarm only | Explicit log · no crash loop | 3h |
| X.5c | Deduplicate FGS startup (boot vs alarm vs MainActivity) | 1 documented owner | 4h |
| X.5d | `PhoneSyncStateStore` exposes degraded FGS state (optional UI) | Readable in smoke | 2h |
| X.7 | Robolectric/Espresso test: FGS throw → service alive | `:mobile:test` green | 3h |
| X.3 | Overnight repro OR quota simulation | Incident report completed | 8h |
| X.6 | 30 min repeated sync post-fix | 0 FATAL | 30m |

### Block A — P0 (PR #9)

| ID | Task | DoD | Est. |
|----|------|-----|------|
| A.1 | Notification permission flow (splash or first need) | Grant/deny without crash | 2h |
| A.2a | `DexcomEntryActivity`: stop sync + clear state | Same as settings | 1h |
| A.2b | Manual disconnect test entry → no ghost FGS | K7 OK | 15m |
| A.3 | `runManualSync()` → Snackbar by result | 3 states: OK / pending / error | 2h |
| A.4 | Externalize 15+ Kotlin strings | grep hardcoded strings = 0 targeted areas | 2h |

### Block M — Mock (PR #10)

| ID | Task | DoD | Est. |
|----|------|-----|------|
| M.1a | `HomeUiState` data class | All hero + row fields | 2h |
| M.1b | `HomeViewModel` / shared binder | MainActivity + test | 4h |
| M.2 | Documented single time rule + impl | Hero = status = tile (stale) | 4h |
| M.3 | 6 tests export PNG or `@Preview` XML | `export-app-preview -AllStates` | 3h |
| M.4 | design-reference companion | PNG up to date | 2h |

### Block B — Wear (PR #11)

| ID | Task | DoD | Est. |
|----|------|-----|------|
| B.1 | `ComplicationUpdateNotifier.notifyReadingChanged` on cache write | Tile = complication < 45s | 4h |
| B.2 | Sync error line on home OR explicit ack status | User sees Dexcom/watch error | 3h |
| B.5 | `strings.xml` wear tile sync FR | No more EN "Sync" | 30m |
| B.3 | Document single scheduler (FGS vs alarm) | dev/architecture.md | 2h |
| B.4 | `WatchSyncVerifier` via engine | Test sync waits for ack or timeout | 4h |

### Block C — QA (PR #12 doc)

| ID | Procedure | Pass | Evidence |
|----|-----------|------|----------|
| C.0 | 10× open/kill app · 10× manual sync | 0 FATAL | logcat |
| C.1 | Screenshots 60/120/200 phone+tile | AGP colors OK | docs/qa/captures/ |
| C.2 | Complication vs tile every 5min × 6 | Drift ≤ 1 sync | notes |
| C.3 | Airplane watch 2h · phone active | Catch-up < 2 min | logs |
| C.7 | Charge · screen off · 8h · Dexcom ON | Morning hero · 0 FATAL | **sign-off** |
| C.8 | Watch ≤20% · phone sync | No crash · degraded message | notes |

### Block D — Quality (PR #13–14)

| ID | Task | DoD | Est. |
|----|------|-----|------|
| D.1 | Auth tests 401, timeout, US/OUS | 5+ tests green | 6h |
| D.2 | Remove popup_home_menu + dead strings | grep unused OK | 2h |
| D.3 | install-and-verify: auto push/ack seq | FAIL on seq mismatch | 4h |
| D.6 | capture-crash-log.ps1 | 1 command post-crash | 1h |

---

## 6. Parallelization

| After gate | Can advance in parallel |
|------------|-------------------------|
| **G-A** | **M** (mock) and **B** (wear) — separate dev, sequential merge M then B |
| **G-B** | **C.0–C.6** while preparing **D.1** tests (code) |
| **G-M7** | **F0** (Gradle Compose) without touching screens |
| During **C.7** soak | **D.2** cleanup · **D.6** scripts · doc only |

**Forbidden in parallel:** X with anything · C.7 with PR touching sync · F3 with other phone UI.

---

## 7. Weekly ritual

### Start of week (15 min)

- [ ] Read [PROGRESS.md](PROGRESS.md) · update statuses
- [ ] Run `@widget-g7-doc-backlog-sync` · [DOC-BACKLOG.md](DOC-BACKLOG.md)
- [ ] Choose **1 target block** · **1 end-of-week gate**
- [ ] Verify phone+watch adb: `adb devices -l`

### Before each PR

```powershell
.\scripts\qa\stability-gate.ps1 -Strict
.\gradlew.bat installWidgetG7Debug   # if touching mobile/wear
```

- [ ] Fill in [PR-CHECKLIST.md](PR-CHECKLIST.md)
- [ ] If touching sync: note push/ack seq before/after

### End of week (30 min)

- [ ] Block gate checked or deferred (note why)
- [ ] `docs/qa/` capture if hardware session
- [ ] Open incident? → report in `docs/qa/incidents/`

### Overnight soak (C.7)

- [ ] Phone charging · Dexcom OK · watch connected
- [ ] `adb logcat -c` before bed
- [ ] Morning: `stability-gate.ps1 -CheckLogcatOnly` + hero screenshot

---

## 8. Risk register

| ID | Risk | Prob. | Impact | Mitigation | Owner |
|----|------|-------|--------|------------|-------|
| R1 | FGS quota re-crash post-fix | M | Critical | X.7 test + C.7 | X |
| R2 | Dexcom API change | L | Critical | D.1 tests · log monitoring | D |
| R3 | Complication always stale | M | Medium | B.1 + C.2 | B |
| R4 | C.7 soak flake | M | Blocks M7 | Re-run 2 nights · logcat | C |
| R5 | Compose F3 breaks sync | M | Critical | F3 gate 4h soak · revert | F |
| R6 | QA hardware unavailable | M | Delays C | strict smoke · partial repro | C |
| R7 | Double Gradle WSL+Studio | M | Corrupt build | 1 sync at a time · dev/setup.md | S |
| R8 | Watch battery <20% crashes phone | L | Medium | C.8 | C |

---

## 9. Automation vs manual

| Control | Auto | Manual | Frequency |
|---------|------|--------|-----------|
| Unit tests | `verify_ci.sh` | — | each PR |
| Compile mobile/wear | verify_ci | — | each PR |
| Push/ack seq | hardware-smoke | — | if adb |
| FATAL logcat | stability-gate | — | each PR |
| Install APK | installWidgetG7Debug | — | post-change |
| AGP colors | — | C.1 | 1× gate |
| Soak 8h | — | C.7 | 1× M7 |
| Complication 30m | — | C.2 | 1× G-B/C |
| Offline 2h | — | C.3 | 1× G-C |
| Dexcom LOW/HI | — | C.4 | if available |

**S.7 goal:** enrich `hardware-smoke.ps1` to FAIL (not WARN) on seq mismatch.

---

## 10. PR checklist

See **[PR-CHECKLIST.md](PR-CHECKLIST.md)** — copy into PR description.

Summary:

1. Scope one feature · link block ID
2. `verify_ci.sh` green
3. `stability-gate.ps1` green
4. Touch sync? → smoke + seq noted
5. Block gate ready or explicitly partial
6. No secrets · no real glucose in captures

---

## 11. Compose v0.6.0

| Phase | Screen | Sync risk | Extra gate |
|-------|--------|-----------|------------|
| F0 | none | none | compile only |
| F1 | Legal, Notice | none | navigation |
| F2 | Dexcom, WatchSetup | **medium** | retest G-A disconnect |
| F3 | **Home** | **high** | 30m sync + 4h soak |
| F4 | Installer | OCR | optional XML |
| F5 | cleanup | low | full smoke |

**Reuse:** `HomeViewModel` (Block M) · `WidgetG7Theme` toxy tokens · `@Preview` replaces Robolectric.

---

## Related documents

| Doc | Role |
|-----|------|
| [PROGRESS.md](PROGRESS.md) | Status tracking · scoreboard |
| [STABILITY-GATES.md](STABILITY-GATES.md) | Detailed gate criteria |
| [PR-CHECKLIST.md](PR-CHECKLIST.md) | Merge template |
| [../qa/stability-signoff-template.md](../qa/stability-signoff-template.md) | C.7 sign-off |
| [../qa/incidents/2026-05-25-app-crash.md](../qa/incidents/2026-05-25-app-crash.md) | P0 incident |
| [../design/material-icons.md](../design/material-icons.md) | F5 icons |

---

## Scoreboard (update weekly)

| Gate | Status | Date |
|------|--------|------|
| G-X | ☐ | |
| G-A | ☐ | |
| G-M | ☐ | |
| G-B | ☐ | |
| G-C | ☐ | |
| G-D | ☐ | |
| G-M7 | ☐ | |
| G-M8 | ☐ | |

| KPI | Current | Target |
|-----|---------|--------|
| K1 FATAL | open | 0 |
| K2 soak 8h | ☐ | ✅ |
| K3 sync 30m | partial | ✅ |
| K6 QA 7/7 | 4/7 | 7/7 |
