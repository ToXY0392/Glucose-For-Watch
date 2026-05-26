# Gate G-F3 — Home Compose (post PR #34)

> **Status:** ✅ closed 2026-05-26 · [sign-off](2026-05-26-g-f3-signoff.md)  
> **Target:** [STABILITY-GATES.md § Compose](../plan/STABILITY-GATES.md#compose-gates-v060)  
> **K2 policy:** C.7 8 h baseline sufficient · post-F3 4 h **optional** (archive)  
> **App:** `0.6.0` (vc 25) · tag on `integrate`

---

## KPI (this gate)

| KPI | Threshold | Evidence |
|-----|-----------|----------|
| K3 | 30 min sync, push/ack OK | `soak-monitor -Label G-F3-sync` report |
| K4 | S1–S3 no regression | `hardware-smoke.ps1` + manual tile tap |
| K2 (min) | 0 FATAL · no K2 regression | **C.7 8 h baseline** ✅ · logcat clean since F3 install · optional `G-F3` 4 h archive |
| K8 | Compose home, sync unchanged | manual home smoke below |

---

## Phase 0 — Preflight (automated)

```powershell
.\gradlew.bat :mobile:assembleDebug :mobile:testDebugUnitTest
.\scripts\qa\g-f3-gate.ps1 -Phase Preflight
```

---

## Phase 1 — Install + smoke (~15 min)

```powershell
.\scripts\qa\g-f3-gate.ps1 -Phase Smoke
```

### Manual home Compose (phone)

- [ ] Home opens after splash — hero montre, titre app, bouton sync
- [ ] Status row : Bluetooth · batterie montre (si dispo) · âge sync
- [ ] Sync manuelle : bouton grisé pendant sync · snackbar résultat
- [ ] Navigation : Dexcom · Montre · Batterie · Notice · Permissions
- [ ] Ligne install montre visible si app wear absente

### Watch tile (manual)

- [ ] Tap sync on tile → hero phone + tile update within ~2 min

---

## Phase 2 — Sync 30 min

Phone on charge, Dexcom configured, watch paired.

```powershell
.\scripts\qa\g-f3-gate.ps1 -Phase Sync30
```

At end:

```powershell
.\scripts\qa\hardware-smoke.ps1
.\scripts\qa\stability-gate.ps1 -CheckLogcatOnly
```

---

## Phase 3 — K2 long soak (waived · C.7 sufficient)

**Policy (2026-05-26):** A dedicated post-F3 **4 h** soak is **not required** when **C.7 8 h** already PASS and G-F3 sign-off records 0 FATAL since F3 install. C.7 is the stronger K2 evidence for the repo.

**Required for G-F3 GO:**

- [x] [C.7 soak](../soak-runs/2026-05-26_C.7-soak.md) PASS (8 h)
- [x] Phase 2 sync 30 min PASS · `stability-gate.ps1 -CheckLogcatOnly` clean
- [x] Sign-off [2026-05-26-g-f3-signoff.md](2026-05-26-g-f3-signoff.md) **GO**

**Optional (archive only)** — extra F3-only 4 h report, does not block M8:

```powershell
.\scripts\qa\g-f3-gate.ps1 -Phase Soak4h   # optional
.\scripts\qa\g-f3-gate.ps1 -Phase Signoff
```

---

## Rollback

If FATAL or sync regression during G-F3:

1. `capture-crash-log.ps1`
2. Revert PR #34 on `integrate`
3. Do not start F5 / G-M8 until root cause fixed

---

## After G-F3 ✅

1. **F5** — XML cleanup + Compose previews (`feat/bloc-f5-xml-cleanup`)
2. **G-M8** — full smoke + tag `v0.6.0`
