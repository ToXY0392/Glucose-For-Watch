# C.8 - watch battery <= 20% session (smoke + logcat polls)
# Usage:
#   .\scripts\qa\sample-c8-battery-session.ps1
#   .\scripts\qa\sample-c8-battery-session.ps1 -MonitorMinutes 30 -PollMinutes 5

param(
    [int]$MonitorMinutes = 30,
    [int]$PollMinutes = 5,
    [int]$BatteryThreshold = 20,
    [switch]$ForceRun
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

function XmlInt {
    param([string]$Xml, [string]$Name)
    if ($Xml -match "<int name=`"$Name`" value=`"([^`"]*)`"") { return [int]$matches[1] }
    return $null
}

function XmlBool {
    param([string]$Xml, [string]$Name)
    if ($Xml -match "<boolean name=`"$Name`" value=`"([^`"]*)`"") { return $matches[1] -eq "true" }
    return $null
}

function Get-WatchHealth {
    param([string]$Adb, [string]$Serial)
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    if ((& $Adb -s $Serial get-state 2>$null) -ne "device") {
        $ErrorActionPreference = $prev
        return $null
    }
    $ErrorActionPreference = $prev
    $xml = & $Adb -s $Serial shell "run-as com.glucoseforwatch.mobile cat shared_prefs/glucose_cache.xml 2>/dev/null"
    if (-not $xml) { return $null }
    return [pscustomobject]@{
        Battery = XmlInt $xml "watch_battery_level"
        Charging = XmlBool $xml "watch_is_charging"
        SyncLimited = XmlBool $xml "watch_sync_limited"
        StatusMessage = if ($xml -match '<string name="watch_status_message">([^<]*)</string>') { $matches[1] } else { "" }
    }
}

function Get-FatalCount {
    param([string]$Adb, [string]$Serial)
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    if ((& $Adb -s $Serial get-state 2>$null) -ne "device") { return 0 }
    $ErrorActionPreference = $prev
    $chunk = & $Adb -s $Serial logcat -d -v time 2>$null
    return @(
        $chunk |
            Select-String -Pattern "FATAL EXCEPTION|AndroidRuntime.*FATAL" |
            Where-Object { $_.Line -match "gfw" }
    ).Count
}

$sdkDir = Read-LocalProperty "sdk.dir"
if (-not $sdkDir) { $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdkDir "platform-tools\adb.exe"
$phone = Read-LocalProperty "gfw.adb.phone.serial"
$watch = Read-LocalProperty "gfw.adb.watch.serial"
if (-not $phone) { Write-Error "No phone on adb" }

$sessionDir = Join-Path $Root "docs\qa\sessions"
New-Item -ItemType Directory -Force -Path $sessionDir | Out-Null
$stamp = Get-Date -Format "yyyy-MM-dd_HHmm"
$reportPath = Join-Path $sessionDir "$stamp-C8-battery-sample.md"

Write-Host "`n=== C.8 battery session (threshold <= ${BatteryThreshold}%) ===" -ForegroundColor Cyan

$health = Get-WatchHealth -Adb $adb -Serial $watch
if (-not $health) { Write-Error "Watch offline or no glucose_cache" }

Write-Host "Watch: battery=$($health.Battery)% charging=$($health.Charging) sync_limited=$($health.SyncLimited)" -ForegroundColor $(if ($health.Battery -le $BatteryThreshold) { 'Yellow' } else { 'DarkGray' })

if (-not $ForceRun -and $health.Battery -gt $BatteryThreshold) {
    Write-Host "Battery > ${BatteryThreshold}% — unplug watch and re-run when low, or use -ForceRun to record baseline only.`n" -ForegroundColor Yellow
    exit 2
}

& $adb -s $phone logcat -c | Out-Null
if ($watch) { & $adb -s $watch logcat -c 2>$null | Out-Null }

$rows = @()
$deadline = (Get-Date).AddMinutes($MonitorMinutes)
$poll = 0
while ((Get-Date) -lt $deadline) {
    $poll++
    $t = Get-Date -Format "HH:mm:ss"
    $h = Get-WatchHealth -Adb $adb -Serial $watch
    $phoneFatals = Get-FatalCount -Adb $adb -Serial $phone
    $watchFatals = Get-FatalCount -Adb $adb -Serial $watch
    $rows += [pscustomobject]@{
        Poll = $poll
        Time = $t
        Battery = $h.Battery
        SyncLimited = $h.SyncLimited
        PhoneFatal = $phoneFatals
        WatchFatal = $watchFatals
        Message = $h.StatusMessage
    }
    Write-Host "  #$poll $t battery=$($h.Battery)% limited=$($h.SyncLimited) FATAL p=$phoneFatals w=$watchFatals" -ForegroundColor Cyan
    if ($poll -lt [Math]::Ceiling($MonitorMinutes / $PollMinutes)) {
        Start-Sleep -Seconds ($PollMinutes * 60)
    } else {
        break
    }
}

$smokeOutput = & (Join-Path $Root "scripts\qa\hardware-smoke.ps1") 2>&1 | Out-String
$smokePass = $LASTEXITCODE -eq 0
$maxFatals = ($rows | Measure-Object -Property PhoneFatal -Maximum).Maximum
$noFatals = ($maxFatals -eq 0) -and (($rows | Measure-Object -Property WatchFatal -Maximum).Maximum -eq 0)
$lowBatterySeen = @($rows | Where-Object { $_.Battery -le $BatteryThreshold }).Count -gt 0
$overallPass = $noFatals -and $smokePass -and ($ForceRun -or $lowBatterySeen)

$lines = @(
    "# C.8 battery sample $stamp"
    ""
    "| Field | Value |"
    "|-------|-------|"
    "| Threshold | <= ${BatteryThreshold}% |"
    "| Monitor | ${MonitorMinutes}m / poll ${PollMinutes}m |"
    "| ForceRun | $ForceRun |"
    "| Overall | **$(if ($overallPass) { 'PASS' } else { 'PARTIAL' })** |"
    ""
    "## Polls"
    ""
    "| # | Time | Battery % | sync_limited | Phone FATAL | Watch FATAL | Message |"
    "|---|------|-----------|--------------|-------------|-------------|---------|"
)
foreach ($r in $rows) {
    $lines += "| $($r.Poll) | $($r.Time) | $($r.Battery) | $($r.SyncLimited) | $($r.PhoneFatal) | $($r.WatchFatal) | $($r.Message) |"
}
$lines += @(
    ""
    "## Smoke"
    ""
    "$(if ($smokePass) { 'PASS' } else { 'FAIL' })"
    ""
    '```'
    $smokeOutput.Trim()
    '```'
    ""
)
$lines | Set-Content -Path $reportPath -Encoding UTF8

Write-Host "`nReport: $reportPath" -ForegroundColor Cyan
Write-Host "Result: $(if ($overallPass) { 'PASS' } else { 'PARTIAL' })`n" -ForegroundColor $(if ($overallPass) { 'Green' } else { 'Yellow' })

if ($overallPass) { exit 0 }
if (-not $lowBatterySeen -and -not $ForceRun) { exit 2 }
exit 1
