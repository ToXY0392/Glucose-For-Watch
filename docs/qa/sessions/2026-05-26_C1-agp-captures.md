# C.1 AGP captures - 2026-05-26

| mg/dL | AGP band | Phone | Watch | Visual OK |
|-------|----------|-------|-------|-----------|
| 200 | high L1 amber, 181-250 mg/dL | recapture pending | - | pending |

## Operator notes

_Mark visual OK after reviewing colors on phone hero + watch tile._

First capture was AOD/lock screen only. Unlock phone, open app, re-run:

```powershell
.\scripts\qa\capture-c1-agp-session.ps1 -ValueMgDl 200 -LaunchApp
.\scripts\qa\connect-watch-adb.ps1   # if watch offline
```

