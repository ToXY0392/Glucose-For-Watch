# C.3 - watch offline window + catch-up timer (phone polls + watch cache after reconnect)
# Usage:
#   .\scripts\qa\sample-c3-offline-session.ps1
#   .\scripts\qa\sample-c3-offline-session.ps1 -OfflineMinutes 120 -PhonePollMinutes 30
#   .\scripts\qa\sample-c3-offline-session.ps1 -QuickTest   # 2 min offline, 30s polls

param(
    [int]$OfflineMinutes = 120,
    [int]$PhonePollMinutes = 30,
    [int]$CatchUpMaxSeconds = 120,
    [int]$CatchUpPollSeconds = 15,
    [int]$WaitOfflineSeconds = 900,
    [int]$WaitOnlineSeconds = 900,
    [switch]$QuickTest,
    [switch]$NonInteractive,
    [switch]$ManualPrompts
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

if ($QuickTest) {
    $OfflineMinutes = 2
    $PhonePollMinutes = 1
    $CatchUpMaxSeconds = 60
    $CatchUpPollSeconds = 10
}

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

function Get-PhoneHero {
    param([string]$Adb, [string]$Serial)
    $xml = & $Adb -s $Serial shell "run-as com.glucoseforwatch.mobile cat shared_prefs/widget_g7_sync_status.xml 2>/dev/null"
    return XmlInt $xml "last_value"
}

function Get-WatchCacheValue {
    param([string]$Adb, [string]$Serial)
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    $state = & $Adb -s $Serial get-state 2>$null
    $ErrorActionPreference = $prev
    if ($state -ne "device") { return $null }
    $xml = & $Adb -s $Serial shell "run-as com.glucoseforwatch.mobile cat shared_prefs/glucose_cache.xml 2>/dev/null"
    return XmlInt $xml "valueMgDl"
}

function Get-FatalCount {
    param([string]$Adb, [string]$Serial)
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    $state = & $Adb -s $Serial get-state 2>$null
    $ErrorActionPreference = $prev
    if ($state -ne "device") { return 0 }
    $chunk = & $Adb -s $Serial logcat -d -v time 2>$null
    return @(
        $chunk |
            Select-String -Pattern "FATAL EXCEPTION|AndroidRuntime.*FATAL" |
            Where-Object { $_.Line -match "gfw" }
    ).Count
}

function Wait-ForWatchOffline {
    param([string]$Adb, [string]$Serial, [int]$MaxSeconds)
    if (-not $Serial) { return $false }
    Write-Host ">>> Enable airplane mode on watch (waiting up to ${MaxSeconds}s for adb offline) <<<" -ForegroundColor Yellow
    $deadline = (Get-Date).AddSeconds($MaxSeconds)
    while ((Get-Date) -lt $deadline) {
        if (-not (Watch-Online -Adb $Adb -Serial $Serial)) {
            Start-Sleep -Seconds 5
            if (-not (Watch-Online -Adb $Adb -Serial $Serial)) { return $true }
        }
        Start-Sleep -Seconds 5
    }
    return $false
}

function Wait-ForWatchOnline {
    param([string]$Adb, [string]$Serial, [int]$MaxSeconds)
    if (-not $Serial) { return $false }
    Write-Host ">>> Disable airplane mode (waiting up to ${MaxSeconds}s for adb online) <<<" -ForegroundColor Yellow
    $deadline = (Get-Date).AddSeconds($MaxSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Watch-Online -Adb $Adb -Serial $Serial) { return $true }
        Start-Sleep -Seconds 5
    }
    return $false
}

function Wait-Operator {
    param([string]$Message)
    if ($ManualPrompts -and -not $NonInteractive) {
        Read-Host $Message
        return
    }
    if ($NonInteractive) {
        Write-Host "[NonInteractive] $Message (skip)" -ForegroundColor DarkGray
    }
}

function Watch-Online {
    param([string]$Adb, [string]$Serial)
    if (-not $Serial) { return $false }
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    $ok = (& $Adb -s $Serial get-state 2>$null) -eq "device"
    $ErrorActionPreference = $prev
    return $ok
}

$sdkDir = Read-LocalProperty "sdk.dir"
if (-not $sdkDir) { $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdkDir "platform-tools\adb.exe"
$phone = Read-LocalProperty "gfw.adb.phone.serial"
$watch = Read-LocalProperty "gfw.adb.watch.serial"
if (-not $phone) {
    $phone = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] } | Where-Object { $_ -notmatch "^adb-" } | Select-Object -First 1)
}
if (-not $phone) { Write-Error "No phone on adb" }

$sessionDir = Join-Path $Root "docs\qa\sessions"
New-Item -ItemType Directory -Force -Path $sessionDir | Out-Null
$stamp = Get-Date -Format "yyyy-MM-dd_HHmm"
$reportPath = Join-Path $sessionDir "$stamp-C3-offline-sample.md"

Write-Host "`n=== C.3 offline session (${OfflineMinutes}m offline) ===" -ForegroundColor Cyan
Write-Host "Phone: $phone"
Write-Host "Watch: $(if ($watch) { $watch } else { 'n/a - set gfw.adb.watch.serial' })`n"

& $adb -s $phone logcat -c | Out-Null
if ($watch -and (Watch-Online -Adb $adb -Serial $watch)) {
    & $adb -s $watch logcat -c 2>$null | Out-Null
}

$startHero = Get-PhoneHero -Adb $adb -Serial $phone
$startWatch = Get-WatchCacheValue -Adb $adb -Serial $watch
$startTime = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
Write-Host "Baseline: phone=$startHero watch=$startWatch @ $startTime" -ForegroundColor Green

$offlineDetected = $false
if ($ManualPrompts) {
    Wait-Operator "Enable airplane mode on watch (BT off). Press Enter when offline."
    $offlineDetected = $true
} else {
    $offlineDetected = Wait-ForWatchOffline -Adb $adb -Serial $watch -MaxSeconds $WaitOfflineSeconds
}
if (-not $offlineDetected) {
    Write-Error "Watch did not go offline within ${WaitOfflineSeconds}s. Enable airplane mode and re-run."
}

$offlineStart = Get-Date
$phoneRows = @()
$phoneRows += [pscustomobject]@{
    Phase = "offline-start"
    Time = $offlineStart.ToString("HH:mm:ss")
    Phone = $startHero
    Watch = "offline"
    Notes = "airplane ON"
}

$deadline = $offlineStart.AddMinutes($OfflineMinutes)
$pollIndex = 0
while ((Get-Date) -lt $deadline) {
    $waitSec = [Math]::Min($PhonePollMinutes * 60, ($deadline - (Get-Date)).TotalSeconds)
    if ($waitSec -gt 0) {
        Write-Host "  phone-only poll in $([int]$waitSec)s ..." -ForegroundColor DarkGray
        Start-Sleep -Seconds $waitSec
    }
    if ((Get-Date) -ge $deadline) { break }
    $pollIndex++
    $t = Get-Date -Format "HH:mm:ss"
    $hero = Get-PhoneHero -Adb $adb -Serial $phone
    $fatals = Get-FatalCount -Adb $adb -Serial $phone
    $phoneRows += [pscustomobject]@{
        Phase = "offline+$pollIndex"
        Time = $t
        Phone = $hero
        Watch = "n/a"
        Notes = "phone FATAL=$fatals"
    }
    Write-Host "  +$($pollIndex * $PhonePollMinutes)m $t phone=$hero FATAL=$fatals" -ForegroundColor Cyan
}

$offlineEnd = Get-Date
Write-Host "`nOffline window done ($OfflineMinutes min). Waiting for reconnect..." -ForegroundColor Yellow

$onlineDetected = $false
if ($ManualPrompts) {
    Wait-Operator "Disable airplane mode. Press Enter when watch is back online."
    $onlineDetected = Watch-Online -Adb $adb -Serial $watch
} else {
    $onlineDetected = Wait-ForWatchOnline -Adb $adb -Serial $watch -MaxSeconds $WaitOnlineSeconds
}
if (-not $onlineDetected) {
    Write-Error "Watch did not reconnect within ${WaitOnlineSeconds}s."
}

$reconnectTime = Get-Date
$phoneAtReconnect = Get-PhoneHero -Adb $adb -Serial $phone
$catchUpRows = @()
$catchUpSec = $null
$aligned = $false
$elapsed = 0
while ($elapsed -le $CatchUpMaxSeconds) {
    if ($elapsed -gt 0) { Start-Sleep -Seconds $CatchUpPollSeconds }
    $elapsed += $CatchUpPollSeconds
    $t = Get-Date -Format "HH:mm:ss"
    $hero = Get-PhoneHero -Adb $adb -Serial $phone
    $watchVal = Get-WatchCacheValue -Adb $adb -Serial $watch
    $match = ($null -ne $hero) -and ($null -ne $watchVal) -and ($hero -eq $watchVal)
    $catchUpRows += [pscustomobject]@{
        ElapsedSec = $elapsed
        Time = $t
        Phone = $hero
        Watch = $watchVal
        Match = $match
    }
    Write-Host "  catch-up +${elapsed}s phone=$hero watch=$watchVal $(if ($match) { 'ALIGNED' } else { '...' })" -ForegroundColor $(if ($match) { 'Green' } else { 'DarkGray' })
    if ($match) {
        $aligned = $true
        $catchUpSec = $elapsed
        break
    }
}

$phoneFatals = Get-FatalCount -Adb $adb -Serial $phone
$watchFatals = Get-FatalCount -Adb $adb -Serial $watch
$catchUpPass = $aligned -and ($catchUpSec -le $CatchUpMaxSeconds)
$noFatals = ($phoneFatals -eq 0) -and ($watchFatals -eq 0)
$overallPass = $catchUpPass -and $noFatals

$lines = @(
    "# C.3 offline sample $stamp"
    ""
    "| Field | Value |"
    "|-------|-------|"
    "| Offline window | ${OfflineMinutes}m |"
    "| Phone | $phone |"
    "| Watch | $(if ($watch) { $watch } else { 'n/a' }) |"
    "| Offline start | $($offlineStart.ToString('yyyy-MM-dd HH:mm:ss')) |"
    "| Reconnect | $($reconnectTime.ToString('yyyy-MM-dd HH:mm:ss')) |"
    "| Catch-up | $(if ($catchUpSec) { "${catchUpSec}s" } else { "> ${CatchUpMaxSeconds}s FAIL" }) |"
    "| Overall | **$(if ($overallPass) { 'PASS' } else { 'FAIL' })** |"
    ""
    "## Baseline"
    ""
    "- Start: phone=$startHero watch=$startWatch @ $startTime"
    "- At reconnect: phone=$phoneAtReconnect"
    ""
    "## Phone-only polls (watch offline)"
    ""
    "| Phase | Time | Phone | Watch | Notes |"
    "|-------|------|-------|-------|-------|"
)
foreach ($r in $phoneRows) {
    $lines += "| $($r.Phase) | $($r.Time) | $($r.Phone) | $($r.Watch) | $($r.Notes) |"
}
$lines += @(
    ""
    "## Catch-up (target <= ${CatchUpMaxSeconds}s)"
    ""
    "| +sec | Time | Phone | Watch cache | Match |"
    "|------|------|-------|-------------|-------|"
)
foreach ($r in $catchUpRows) {
    $m = if ($r.Match) { "PASS" } else { "pending" }
    $lines += "| $($r.ElapsedSec) | $($r.Time) | $($r.Phone) | $($r.Watch) | $m |"
}
$lines += @(
    ""
    "## Logcat"
    ""
    "- Phone FATAL gfw: $phoneFatals"
    "- Watch FATAL gfw: $watchFatals"
    ""
)
$lines | Set-Content -Path $reportPath -Encoding UTF8

Write-Host "`nReport: $reportPath" -ForegroundColor Cyan
Write-Host "Result: $(if ($overallPass) { 'PASS' } else { 'FAIL' }) catch-up=$(if ($catchUpSec) { "${catchUpSec}s" } else { 'timeout' }) FATAL phone=$phoneFatals watch=$watchFatals`n" -ForegroundColor $(if ($overallPass) { 'Green' } else { 'Red' })

if ($overallPass) { exit 0 }
exit 1
