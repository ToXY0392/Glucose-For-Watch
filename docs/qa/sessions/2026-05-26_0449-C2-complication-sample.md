# C.2 automated sample 2026-05-26_0449

| Field | Value |
|-------|-------|
| Samples | 6 every 5m |
| Phone | 41031JEKB03416 |
| Watch adb | adb-3A251RTJWWKFFD-DKGmGk._adb-tls-connect._tcp |
| Overall | **PASS** |
| Watch cache | tile + complication source (same prefs) |

## Samples

| # | Time | Phone hero | Watch cache | Match | Notes |
|---|------|------------|-------------|-------|-------|
| 1 | 04:49:56 | 207 | 207 | PASS | OK |
| 2 | 04:54:56 | 204 | 204 | PASS | OK |
| 3 | 04:59:57 | 197 | 197 | PASS | OK |
| 4 | 05:04:57 | 187 | 187 | PASS | OK |
| 5 | 05:09:58 | 171 | 171 | PASS | OK |
| 6 | 05:14:58 | 152 | 152 | PASS | OK |

## Logcat

- Phone FATAL gfw: 0
- Watch FATAL gfw: 0

## Gate note

Watch valueMgDl in glucose_cache is the shared source for tile and complication UI.
6/6 phone=watch cache with 0 FATAL satisfies C.2 drift criterion (<= 1 sync).
