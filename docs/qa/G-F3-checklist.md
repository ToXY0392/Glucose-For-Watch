# Gate G-F3 — Home Compose (post PR #34)

> **Target:** [STABILITY-GATES.md § Compose](../plan/STABILITY-GATES.md#compose-gates-v060)  
> **Blocks:** tag `v0.6.0` / G-M8  
> **App:** `0.5.0` (vc 24) · commit on `integrate` ≥ `519a458`

---

## KPI (this gate)

| KPI | Threshold | Evidence |
|-----|-----------|----------|
| K3 | 30 min sync, push/ack OK | `soak-monitor -Label G-F3-sync` report |
| K4 | S1–S3 no regression | `hardware-smoke.ps1` + manual tile tap |
| K2 (min) | 4 h soak, 0 FATAL | `soak-monitor -Label G-F3` report |
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

## Phase 3 — Soak 4 h (min)

Screen off, phone on charge, sync background OK.

```powershell
.\scripts\qa\g-f3-gate.ps1 -Phase Soak4h
```

Morning / end:

```powershell
.\scripts\qa\g-f3-gate.ps1 -Phase Signoff
```

Copy `docs/qa/stability-signoff-template.md` → `docs/qa/YYYY-MM-DD-g-f3-signoff.md` and mark **GO**.

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
