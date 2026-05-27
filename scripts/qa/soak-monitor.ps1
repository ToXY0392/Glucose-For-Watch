# Monitors logcat for FATAL crashes during a soak window (X.6 = 30 min, C.7 = 480 min).
# Usage:
#   .\scripts\qa\soak-monitor.ps1                    # X.6 default 30 min
#   .\scripts\qa\soak-monitor.ps1 -DurationMinutes 480 -Label C.7
#   .\scripts\qa\soak-monitor.ps1 -ClearLogcat:$false

param(
    [int]$DurationMinutes = 30,
    [string]$Label = "X.6",
    [string]$PackageId = "com.glucoseforwatch.mobile",
    [switch]$ClearLogcat = $true,
    [int]$PollSeconds = 60
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

function Read-LocalProperty {
    param([string]$Key)
    $localProps = Join-Path $Root "local.properties"
    if (-not (Test-Path $localProps)) { return $null }
    foreach ($line in Get-Content $localProps) {
        if ($line -match "^$([regex]::Escape($Key))=(.+)$") {
            return ($matches[1].Trim() -replace '\\(.)', '$1')
        }
    }
    return $null
}

$sdkDir = Read-LocalProperty "sdk.dir"
if (-not $sdkDir) { $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdkDir "platform-tools\adb.exe"
if (-not (Test-Path $adb)) { Write-Error "adb not found: $adb" }

$phone = Read-LocalProperty "gfw.adb.phone.serial"
if (-not $phone) {
    $phone = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] } | Select-Object -First 1)
}
if (-not $phone) { Write-Error "No phone on adb" }

$startedAt = Get-Date
$deadline = $startedAt.AddMinutes($DurationMinutes)
$reportDir = Join-Path $Root "docs\qa\soak-runs"
New-Item -ItemType Directory -Force -Path $reportDir | Out-Null
$stamp = Get-Date -Format "yyyy-MM-dd_HHmm"
$reportPath = Join-Path $reportDir "$stamp-$Label-soak.md"

Write-Host "`n=== Soak monitor $Label ($DurationMinutes min) ===" -ForegroundColor Cyan
Write-Host "Phone: $phone · Package: $PackageId"
Write-Host "Started: $($startedAt.ToString('yyyy-MM-dd HH:mm:ss')) · Ends: $($deadline.ToString('HH:mm:ss'))"
Write-Host "Report: $reportPath`n"

if ($ClearLogcat) {
    & $adb -s $phone logcat -c | Out-Null
    Write-Host "[OK] logcat cleared" -ForegroundColor Green
}

$fatalHits = @()
$poll = 0

while ((Get-Date) -lt $deadline) {
    $poll++
    $remaining = [math]::Ceiling(($deadline - (Get-Date)).TotalMinutes)
    Write-Host "[poll $poll] $remaining min left - scanning logcat..." -ForegroundColor DarkCyan

    $chunk = & $adb -s $phone logcat -d -v time 2>$null
    $fatalLogMatches = @(
        $chunk |
            Select-String -Pattern "FATAL EXCEPTION|AndroidRuntime.*FATAL" |
            Where-Object { $_.Line -match [regex]::Escape($PackageId) -or $_.Line -match "com\.gfw" }
    )
    foreach ($hit in $fatalLogMatches) {
        if ($fatalHits -notcontains $hit.Line) {
            $fatalHits += $hit.Line
            Write-Host "[FAIL] $($hit.Line)" -ForegroundColor Red
        }
    }

    if ($fatalHits.Count -gt 0) { break }
    Start-Sleep -Seconds $PollSeconds
}

$endedAt = Get-Date
$passed = $fatalHits.Count -eq 0
$status = if ($passed) { "PASS" } else { "FAIL" }

$fatalSection = if ($fatalHits.Count -eq 0) {
    "_None_"
} else {
    ($fatalHits | ForEach-Object { "- $_" }) -join [Environment]::NewLine
}

$nextSteps = if ($passed) {
@(
    "- Run capture-crash-log.ps1 if any ANR suspected"
    "- Update docs/plan/PROGRESS.md ($Label)"
    "- For C.7: fill stability-signoff-template.md"
) -join [Environment]::NewLine
} else {
@(
    "- Run capture-crash-log.ps1 immediately"
    "- File/update docs/qa/incidents/"
    "- Do not close gate until root cause fixed"
) -join [Environment]::NewLine
}

$reportLines = @(
    "# Soak run - $Label"
    ""
    "- Status: **$status**"
    "- Label: $Label"
    "- Duration requested: ${DurationMinutes} min"
    "- Started: $($startedAt.ToString('yyyy-MM-dd HH:mm:ss'))"
    "- Ended: $($endedAt.ToString('yyyy-MM-dd HH:mm:ss'))"
    "- Phone serial: $phone"
    "- FATAL count: $($fatalHits.Count)"
    ""
    "## Preconditions"
    ""
    "- Phone on charge, screen off (C.7) or active use (X.6)"
    "- Dexcom configured and sync running"
    "- Watch paired via Data Layer"
    ""
    "## FATAL lines"
    ""
    $fatalSection
    ""
    "## Next steps"
    ""
    $nextSteps
)
$reportLines | Set-Content -Path $reportPath -Encoding UTF8

Write-Host ""
if ($passed) {
    Write-Host "[PASS] $Label soak complete - 0 FATAL in window" -ForegroundColor Green
    Write-Host "Report: $reportPath"
    exit 0
}

Write-Host "[FAIL] $Label soak - $($fatalHits.Count) FATAL hit(s)" -ForegroundColor Red
Write-Host "Report: $reportPath"
exit 1
