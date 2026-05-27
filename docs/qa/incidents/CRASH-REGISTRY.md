# Crash registry — Glucose For Watch (phone)

> **Purpose:** Single index of known **fatal** phone crashes, root causes, fixes, and incident detail docs.  
> **Scope:** `com.glucoseforwatch.mobile` · Pixel 8a primary QA device (`41031JEKB03416`) · Android 16 / targetSdk 36  
> **Not listed here:** user-facing errors without process death (e.g. Dexcom Share not enabled → auth failure).

---

## Summary

| ID | Date | Symptom | Exception / trigger | Status | Detail |
|----|------|---------|---------------------|--------|--------|
| **CRASH-001** | 2026-05-25 | "Glucose For Watch has stopped" · idle / charging | `ForegroundServiceStartNotAllowedException` · FGS `dataSync` quota exhausted | **Closed** · PR #8 · C.7 PASS | [2026-05-25-app-crash.md](2026-05-25-app-crash.md) |
| **CRASH-002** | 2026-05-27 | Crash right after Dexcom login / app open | `ForegroundServiceDidNotStopInTimeException` · `ActiveGlucoseSyncService` · missing `onTimeout()` | **Fixed** (local, uncommitted) | § CRASH-002 below |
| **CRASH-003** | 2026-05-27 | Instant crash on home screen | `IllegalArgumentException` · `painterResource()` on XML `<shape>` drawables | **Fixed** (local, uncommitted) | § CRASH-003 below |

---

## CRASH-001 — FGS start denied (background quota)

**Incident doc:** [2026-05-25-app-crash.md](2026-05-25-app-crash.md)

| Field | Value |
|-------|--------|
| **When** | Background · screen off · phone charging · automatic sync |
| **Logcat** | `ForegroundServiceStartNotAllowedException: Time limit already exhausted for foreground service type dataSync` at `ActiveGlucoseSyncService.onCreate` |
| **Cause** | Unhandled `startForeground()` when Android 14+ refuses a new `dataSync` FGS from background |

**Fix (merged — Block X / PR #8)**

| # | Change | File(s) |
|---|--------|---------|
| 1 | Catch `ForegroundServiceStartNotAllowedException` on FGS promote | `ActiveGlucoseSyncForegroundGate.kt` |
| 2 | Worker / alarm fallback when FGS denied | `BackgroundSyncFallback.kt` |
| 3 | Single entry point for starting sync | `ActiveGlucoseSyncController.kt` |
| 4 | Unit test for denied FGS | `ActiveGlucoseSyncForegroundGateTest` |

**Validation:** [X.6 soak](../soak-runs/2026-05-25_1458-X.6-soak.md) · [C.7 8 h soak](../soak-runs/2026-05-26_C.7-soak.md) · [stability sign-off](../2026-05-26-stability-signoff.md)

---

## CRASH-002 — FGS did not stop in time (Android 15+)

| Field | Value |
|-------|--------|
| **When** | Immediately after first successful Dexcom Share connection, or when reopening app with active sync |
| **User action** | Save Dexcom credentials → navigate to `MainActivity`, or cold start with sync already enabled |
| **Logcat** | `ForegroundServiceDidNotStopInTimeException: A foreground service of type dataSync did not stop within its timeout: ActiveGlucoseSyncService` · often preceded by `FGS (dataSync) timed out` |
| **Cause** | Android 15 (API 35+) requires `Service.onTimeout()` for long-running FGS types. Without it, the system kills the app ~10 s after timeout. A **double start** on first login (`DexcomSettingsActivity` + `MainActivity.scheduleAutoSyncIfReady`) worsened stale FGS state. |

**Fix (2026-05-27 — pending commit)**

| # | Change | File(s) |
|---|--------|---------|
| 1 | Implement `onTimeout()` → log + `stopActiveSync()` | `mobile/.../sync/ActiveGlucoseSyncService.kt` |
| 2 | On first Dexcom connection flow, skip `ActiveGlucoseSyncController.start()` in settings; let `MainActivity` start FGS once UI is foreground | `mobile/.../ui/DexcomSettingsActivity.kt` |

**Immediate workaround (no rebuild)**

```powershell
adb shell am force-stop com.glucoseforwatch.mobile
```

Then reopen the app (Dexcom config is preserved in app storage).

**Repro check**

```powershell
adb logcat -c
# launch app
adb logcat -d | Select-String "ForegroundServiceDidNotStop|fgs_timeout|FATAL EXCEPTION"
```

---

## CRASH-003 — Compose `painterResource` on shape drawables

| Field | Value |
|-------|--------|
| **When** | Every launch of `MainActivity` (Compose home) after migration from XML layout |
| **User action** | Open app from launcher → splash → home |
| **Logcat** | `IllegalArgumentException: Only VectorDrawables and rasterized asset types are supported ex. PNG, JPG, WEBP` at `MainActivity.kt` · `painterResource(R.drawable.bg_companion_canvas)` |
| **Cause** | `bg_companion_canvas.xml` and `bg_watch_face_preview.xml` are `<shape>` XML (gradient / oval + stroke). Compose `painterResource()` supports vectors and bitmaps only—not generic shape drawables. |

**Fix (2026-05-27 — pending commit)**

| # | Change | File(s) |
|---|--------|---------|
| 1 | Replace canvas background `Image` + `painterResource` with `Modifier.background(Brush.verticalGradient(...))` using `gfw_canvas_start` / `gfw_canvas_end` | `mobile/.../MainActivity.kt` |
| 2 | Replace watch preview `Image` with `CircleShape` + `background` + `border` using `wg7_watch_face_bg` / `wg7_watch_face_ring` | `mobile/.../ui/compose/HomeScreen.kt` |

**Compose rule (prevent recurrence)**

- Use `painterResource()` only for `@drawable` **vector** or **PNG/JPG/WEBP**.
- For gradients, shapes, and layered XML drawables → Compose `Brush`, `Shape`, `Surface`, or `colorResource` + `Modifier.background`.

**Repro check**

```powershell
adb logcat -c
# launch app
adb logcat -d | Select-String "IllegalArgumentException|painterResource|MainActivity"
adb shell pidof com.glucoseforwatch.mobile   # non-empty PID = no instant death
```

---

## Related (not crashes)

| Symptom | Cause | Fix / action |
|---------|--------|--------------|
| "Identifiants Dexcom invalides" | Dexcom Share disabled on account, wrong credentials, or trailing spaces in fields | Enable Share in Dexcom app · verify credentials · `trim()` on username/secret fields in `DexcomSettingsActivity` |
| App feels "stuck" after old crash | Stale FGS / process state | `adb shell am force-stop com.glucoseforwatch.mobile` |

---

## Log capture (PC)

```powershell
.\scripts\qa\capture-crash-log.ps1
.\scripts\qa\tail-sync-logs.ps1
adb -s 41031JEKB03416 logcat -d -v time AndroidRuntime:E ActivityManager:W
```

Output under `docs/qa/incidents/` — paste **redacted** `FATAL EXCEPTION` blocks into incident markdown only; never commit credentials or full unredacted logcat ([SECURITY.md](../../../SECURITY.md)).

---

## Adding a new entry

1. Create `docs/qa/incidents/YYYY-MM-DD-<slug>.md` (see [2026-05-25-app-crash.md](2026-05-25-app-crash.md) template).
2. Add a row to the **Summary** table above with the next `CRASH-00N` id.
3. Link from [docs/index.md](../../index.md) if the incident is release-blocking.
4. Update [PROGRESS.md](../../plan/PROGRESS.md) K1 row if sign-off status changes.
