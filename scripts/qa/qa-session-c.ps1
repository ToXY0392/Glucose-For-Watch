# Bloc C — automated hardware checks (C.0 partial, C.5 sample) + session report
# Usage:
#   .\scripts\qa\qa-session-c.ps1
#   .\scripts\qa\qa-session-c.ps1 -SyncCycles 10 -ContinuousMinutes 5

param(
    [int]$RelaunchCycles = 3,
    [int]$SyncCycles = 10,
    [int]$ContinuousMinutes = 5,
    [switch]$SkipContinuous
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
$phone = Read-LocalProperty "gfw.adb.phone.serial"
if (-not $phone) {
    $phone = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] } | Select-Object -First 1)
}
if (-not $phone) { Write-Error "No phone on adb" }

$PackageId = "com.glucoseforwatch.mobile"
$sessionDir = Join-Path $Root "docs\qa\sessions"
New-Item -ItemType Directory -Force -Path $sessionDir | Out-Null
$stamp = Get-Date -Format "yyyy-MM-dd_HHmm"
$reportPath = Join-Path $sessionDir "$stamp-bloc-c-automated.md"

Write-Host "`n=== Bloc C automated session ($phone) ===" -ForegroundColor Cyan

function Get-FatalLines {
    $chunk = & $adb -s $phone logcat -d -v time 2>$null
    return @(
        $chunk |
            Select-String -Pattern "FATAL EXCEPTION|AndroidRuntime.*FATAL" |
            Where-Object { $_.Line -match "gfw" } |
            ForEach-Object { $_.Line }
    )
}

function Invoke-WarmStart {
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    & $adb -s $phone shell am start -n "$PackageId/.SplashActivity" 2>&1 | Out-Null
    $ErrorActionPreference = $prev
}

function Get-SyncXmlValue {
    param([string]$Name)
    $xml = & $adb -s $phone shell "run-as $PackageId cat shared_prefs/widget_g7_sync_status.xml 2>/dev/null"
    if ($xml -match ('<int name="' + [regex]::Escape($Name) + '" value="([^"]*)"')) { return $matches[1] }
    if ($xml -match ('<long name="' + [regex]::Escape($Name) + '" value="([^"]*)"')) { return $matches[1] }
    return $null
}

& $adb -s $phone logcat -c | Out-Null
$results = @()
$fatalTotal = @()

# C.0 — kill / relaunch
Write-Host "`n[C.0] Kill + relaunch x$RelaunchCycles" -ForegroundColor Cyan
for ($i = 1; $i -le $RelaunchCycles; $i++) {
    & $adb -s $phone shell am force-stop $PackageId | Out-Null
    Start-Sleep -Seconds 1
    & $adb -s $phone shell am start -n "$PackageId/.SplashActivity" | Out-Null
    Start-Sleep -Seconds 3
    $fatals = Get-FatalLines
    $fatalTotal += $fatals
    $ok = $fatals.Count -eq 0
    $results += [pscustomobject]@{ Check = "C.0 relaunch $i"; Pass = $ok; Detail = if ($ok) { "OK" } else { $fatals[0] } }
    Write-Host "  cycle $i : $(if ($ok) { 'PASS' } else { 'FAIL' })" -ForegroundColor $(if ($ok) { 'Green' } else { 'Red' })
}

# C.0 — warm start stress (proxy for repeated sync entry; tap sync button manually for full C.0)
Write-Host "`n[C.0] Warm start x$SyncCycles" -ForegroundColor Cyan
for ($i = 1; $i -le $SyncCycles; $i++) {
    Invoke-WarmStart
    Start-Sleep -Seconds 2
    $fatals = Get-FatalLines
    if ($fatals.Count -gt 0) { $fatalTotal += $fatals }
    Write-Host "  warm $i : OK" -ForegroundColor DarkGreen
}
$syncBurstOk = ($fatalTotal | Select-Object -Unique).Count -eq 0
$results += [pscustomobject]@{ Check = "C.0 warm start x$SyncCycles"; Pass = $syncBurstOk; Detail = "FATAL=$($fatalTotal.Count)" }
Write-Host "  sync burst : $(if ($syncBurstOk) { 'PASS' } else { 'FAIL' })" -ForegroundColor $(if ($syncBurstOk) { 'Green' } else { 'Red' })

# C.5 — continuous sample
$continuousOk = $true
$initialValue = $null
$finalValue = $null
if (-not $SkipContinuous) {
    Write-Host "`n[C.5] Continuous sync sample ${ContinuousMinutes}m" -ForegroundColor Cyan
    $initialValue = Get-SyncXmlValue "last_value"
    $deadline = (Get-Date).AddMinutes($ContinuousMinutes)
    while ((Get-Date) -lt $deadline) {
        $fatals = Get-FatalLines
        if ($fatals.Count -gt 0) {
            $fatalTotal += $fatals
            $continuousOk = $false
            break
        }
        Start-Sleep -Seconds 30
    }
    $finalValue = Get-SyncXmlValue "last_value"
    $results += [pscustomobject]@{
        Check = "C.5 continuous ${ContinuousMinutes}m"
        Pass = $continuousOk
        Detail = "hero $initialValue -> $finalValue mg/dL"
    }
    Write-Host "  continuous : $(if ($continuousOk) { 'PASS' } else { 'FAIL' }) ($initialValue -> $finalValue)" -ForegroundColor $(if ($continuousOk) { 'Green' } else { 'Red' })
}

# hardware-smoke summary
Write-Host "`n[smoke] Running hardware-smoke.ps1" -ForegroundColor Cyan
$smokeOutput = & (Join-Path $Root "scripts\qa\hardware-smoke.ps1") 2>&1 | Out-String
$smokePass = $LASTEXITCODE -eq 0

$passCount = @($results | Where-Object { $_.Pass }).Count
$failCount = @($results | Where-Object { -not $_.Pass }).Count
$overallPass = ($failCount -eq 0) -and $smokePass -and (($fatalTotal | Select-Object -Unique).Count -eq 0)

$fatalLines = ($fatalTotal | Select-Object -Unique) -join "`n"
if (-not $fatalLines) { $fatalLines = "_None_" }

$manualItems = @(
    "C.0 tap sync button x10 on phone (manual, complements warm start)"
    "C.1 AGP couleurs 60/120/200 mg/dL (visuel phone + watch)"
    "C.2 Complication vs tuile (meme valeur, lag <= 45s)"
    "C.3 Offline montre 2h puis rattrapage"
    "C.4 LOW / HI affichage"
    "C.6 Reinstall APK + re-ajout tuile"
    "C.7 Soak nuit 8h charge ecran off"
    "C.8 Batterie montre <= 20% apres session"
)

$reportLines = @(
    "# Bloc C - session auto $stamp"
    ""
    "- Overall: **$(if ($overallPass) { 'PASS (automated partial)' } else { 'FAIL' })**"
    "- Phone: $phone"
    "- Automated checks passed: $passCount / $($results.Count)"
    "- Smoke script: $(if ($smokePass) { 'PASS' } else { 'FAIL' })"
    ""
    "## Automated results"
    ""
)
foreach ($row in $results) {
    $reportLines += "- $($row.Check): $(if ($row.Pass) { 'PASS' } else { 'FAIL' }) ($($row.Detail))"
}
$reportLines += @(
    ""
    "## FATAL logcat"
    ""
    $fatalLines
    ""
    "## Manual still required"
    ""
)
$manualItems | ForEach-Object { $reportLines += "- [ ] $_" }
$reportLines += @(
    ""
    "## Smoke output"
    ""
    '```'
    $smokeOutput.Trim()
    '```'
)
$reportLines | Set-Content -Path $reportPath -Encoding UTF8

Write-Host "`nReport: $reportPath" -ForegroundColor Cyan
Write-Host "Automated: $passCount passed, $failCount failed. Manual C.1-C.4, C.6-C.8 still required.`n" -ForegroundColor $(if ($overallPass) { 'Green' } else { 'Yellow' })

if ($overallPass) { exit 0 }
exit 1
