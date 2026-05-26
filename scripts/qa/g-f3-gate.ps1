# Gate G-F3 orchestrator — Home Compose validation (post PR #34).
#
# Usage:
#   .\scripts\qa\g-f3-gate.ps1 -Phase Preflight
#   .\scripts\qa\g-f3-gate.ps1 -Phase Smoke          # install + hardware-smoke + manual checklist
#   .\scripts\qa\g-f3-gate.ps1 -Phase Sync30          # 30 min soak + post smoke
#   .\scripts\qa\g-f3-gate.ps1 -Phase Soak4h         # OPTIONAL 4 h archive (not required if C.7 PASS)
#   .\scripts\qa\g-f3-gate.ps1 -Phase Signoff        # logcat + summary

param(
    [ValidateSet("Preflight", "Smoke", "Sync30", "Soak4h", "Signoff")]
    [string]$Phase = "Preflight",
    [switch]$SkipInstall
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

$Checklist = Join-Path $Root "docs\qa\G-F3-checklist.md"

Write-Host "`n=== G-F3 gate — phase: $Phase ===" -ForegroundColor Cyan
Write-Host "Checklist: $Checklist`n"

switch ($Phase) {
    "Preflight" {
        Write-Host "-- Unit tests + assemble --" -ForegroundColor DarkCyan
        .\gradlew.bat :mobile:assembleDebug :mobile:testDebugUnitTest 2>&1 | Out-Host
        .\scripts\qa\stability-gate.ps1 -SkipCi 2>&1 | Out-Host
        Write-Host "`n[OK] Preflight done. Next: .\scripts\qa\g-f3-gate.ps1 -Phase Smoke" -ForegroundColor Green
    }
    "Smoke" {
        if (-not $SkipInstall) {
            Write-Host "-- Install phone + watch debug APKs --" -ForegroundColor DarkCyan
            .\scripts\qa\install-and-verify.ps1 2>&1 | Out-Host
        } else {
            Write-Host "-- Skipping install (-SkipInstall) --" -ForegroundColor Yellow
            .\scripts\qa\install-and-verify.ps1 -VerifyOnly 2>&1 | Out-Host
        }
        Write-Host "`n-- hardware-smoke.ps1 --" -ForegroundColor DarkCyan
        .\scripts\qa\hardware-smoke.ps1 2>&1 | Out-Host
        Write-Host ""
        Write-Host "--- Manual home Compose (see G-F3-checklist.md) ---" -ForegroundColor Yellow
        Write-Host "  - Hero + sync button + status row"
        Write-Host "  - Manual sync snackbar"
        Write-Host "  - Settings rows navigation"
        Write-Host "  - Watch tile sync tap"
        Write-Host ""
        Write-Host "Next when manual OK:" -ForegroundColor Yellow
        Write-Host "  .\scripts\qa\g-f3-gate.ps1 -Phase Sync30" -ForegroundColor Yellow
    }
    "Sync30" {
        Write-Host "-- 30 min sync window (G-F3-sync) --" -ForegroundColor DarkCyan
        & "$PSScriptRoot\soak-monitor.ps1" -DurationMinutes 30 -Label "G-F3-sync"
        Write-Host "`n-- Post-sync verification --" -ForegroundColor DarkCyan
        .\scripts\qa\hardware-smoke.ps1 2>&1 | Out-Host
        .\scripts\qa\stability-gate.ps1 -CheckLogcatOnly 2>&1 | Out-Host
        Write-Host "`n[OK] Sync30 complete if reports PASS." -ForegroundColor Green
        Write-Host "  K2: C.7 8 h baseline is sufficient (see G-F3-checklist.md Phase 3)." -ForegroundColor Green
        Write-Host "  Optional archive: .\scripts\qa\g-f3-gate.ps1 -Phase Soak4h" -ForegroundColor DarkGray
        Write-Host "  Then sign-off: .\scripts\qa\g-f3-gate.ps1 -Phase Signoff" -ForegroundColor Yellow
    }
    "Soak4h" {
        Write-Host "-- OPTIONAL 4 h soak (archive only; not required when C.7 8 h PASS) --" -ForegroundColor DarkCyan
        Write-Host "See docs/qa/G-F3-checklist.md Phase 3.`n"
        Write-Host "Tip: run in a dedicated PowerShell window.`n"
        & "$PSScriptRoot\soak-monitor.ps1" -DurationMinutes 240 -Label "G-F3"
        Write-Host "`nNext morning: .\scripts\qa\g-f3-gate.ps1 -Phase Signoff" -ForegroundColor Green
    }
    "Signoff" {
        Write-Host "-- Final logcat scan --" -ForegroundColor DarkCyan
        .\scripts\qa\stability-gate.ps1 -CheckLogcatOnly 2>&1 | Out-Host
        .\scripts\qa\hardware-smoke.ps1 2>&1 | Out-Host
        $reports = Get-ChildItem (Join-Path $Root "docs\qa\soak-runs") -Filter "*G-F3*" | Sort-Object LastWriteTime -Descending
        Write-Host "`n=== G-F3 reports ===" -ForegroundColor Cyan
        foreach ($r in $reports | Select-Object -First 4) {
            Write-Host "  $($r.FullName)"
        }
        Write-Host ""
        Write-Host "Fill sign-off:" -ForegroundColor Yellow
        Write-Host "  Copy docs/qa/stability-signoff-template.md -> docs/qa/YYYY-MM-DD-g-f3-signoff.md"
        Write-Host "  Target gate: G-F3 · then update docs/plan/PROGRESS.md"
        Write-Host ""
        Write-Host "After GO: start F5 (feat/bloc-f5-xml-cleanup) then G-M8 tag." -ForegroundColor Yellow
    }
}
