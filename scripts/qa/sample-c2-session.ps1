# C.2 - sample phone hero vs watch cache (tile + complication share glucose_cache)
# Usage:
#   .\scripts\qa\sample-c2-session.ps1
#   .\scripts\qa\sample-c2-session.ps1 -Samples 6 -IntervalMinutes 5 -QuickTest

param(
    [int]$Samples = 6,
    [int]$IntervalMinutes = 5,
    [switch]$QuickTest
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

if ($QuickTest) {
    $Samples = 2
    $IntervalMinutes = 0
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
    $xml = & $Adb -s $Serial shell "run-as com.widgetg7.mobile cat shared_prefs/widget_g7_sync_status.xml 2>/dev/null"
    return XmlInt $xml "last_value"
}

function Get-WatchCacheValue {
    param([string]$Adb, [string]$Serial)
    $xml = & $Adb -s $Serial shell "run-as com.widgetg7.mobile cat shared_prefs/glucose_cache.xml 2>/dev/null"
    return XmlInt $xml "valueMgDl"
}

function Get-FatalCount {
    param([string]$Adb, [string]$Serial)
    $chunk = & $Adb -s $Serial logcat -d -v time 2>$null
    return @(
        $chunk |
            Select-String -Pattern "FATAL EXCEPTION|AndroidRuntime.*FATAL" |
            Where-Object { $_.Line -match "widgetg7" }
    ).Count
}

$sdkDir = Read-LocalProperty "sdk.dir"
if (-not $sdkDir) { $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdkDir "platform-tools\adb.exe"
$phone = Read-LocalProperty "widgetg7.adb.phone.serial"
$watch = Read-LocalProperty "widgetg7.adb.watch.serial"
if (-not $phone) {
    $phone = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] } | Where-Object { $_ -notmatch "^adb-" } | Select-Object -First 1)
}
if (-not $phone) { Write-Error "No phone on adb" }

$sessionDir = Join-Path $Root "docs\qa\sessions"
New-Item -ItemType Directory -Force -Path $sessionDir | Out-Null
$stamp = Get-Date -Format "yyyy-MM-dd_HHmm"
$reportPath = Join-Path $sessionDir "$stamp-C2-complication-sample.md"

Write-Host "`n=== C.2 sample session ($Samples x ${IntervalMinutes}m) ===" -ForegroundColor Cyan
Write-Host "Phone: $phone"
Write-Host "Watch: $(if ($watch) { $watch } else { '(Data Layer only - watch adb optional)' })`n"

& $adb -s $phone logcat -c | Out-Null
if ($watch) {
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    & $adb -s $watch logcat -c 2>$null | Out-Null
    $ErrorActionPreference = $prev
}

$rows = @()
for ($i = 1; $i -le $Samples; $i++) {
    $time = Get-Date -Format "HH:mm:ss"
    $hero = Get-PhoneHero -Adb $adb -Serial $phone
    $watchVal = $null
    if ($watch) {
        $prev = $ErrorActionPreference
        $ErrorActionPreference = "SilentlyContinue"
        $state = & $adb -s $watch get-state 2>$null
        $ErrorActionPreference = $prev
        if ($state -eq "device") {
            $watchVal = Get-WatchCacheValue -Adb $adb -Serial $watch
        }
    }
    $match = ($null -ne $hero) -and ($null -ne $watchVal) -and ($hero -eq $watchVal)
    $detail = if ($null -eq $watchVal) { "watch offline or no cache" } elseif ($match) { "OK" } else { "MISMATCH hero=$hero cache=$watchVal" }
    $rows += [pscustomobject]@{
        Num = $i
        Time = $time
        Phone = $hero
        WatchCache = $watchVal
        Match = $match
        Detail = $detail
    }
    Write-Host "  #$i $time  phone=$hero  watch=$watchVal  $(if ($match) { 'PASS' } else { $detail })" -ForegroundColor $(if ($match) { 'Green' } else { 'Yellow' })
    if ($i -lt $Samples -and $IntervalMinutes -gt 0) {
        Write-Host "  waiting ${IntervalMinutes}m ..." -ForegroundColor DarkGray
        Start-Sleep -Seconds ($IntervalMinutes * 60)
    }
}

$phoneFatals = Get-FatalCount -Adb $adb -Serial $phone
$watchFatals = 0
if ($watch) {
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    if ((& $adb -s $watch get-state 2>$null) -eq "device") {
        $watchFatals = Get-FatalCount -Adb $adb -Serial $watch
    }
    $ErrorActionPreference = $prev
}

$passCount = @($rows | Where-Object { $_.Match }).Count
$watchSamples = @($rows | Where-Object { $null -ne $_.WatchCache }).Count
$allMatch = ($passCount -eq $watchSamples) -and ($watchSamples -eq $Samples)
$noFatals = ($phoneFatals -eq 0) -and ($watchFatals -eq 0)
$overallPass = $allMatch -and $noFatals

$lines = @(
    "# C.2 automated sample $stamp"
    ""
    "| Field | Value |"
    "|-------|-------|"
    "| Samples | $Samples every ${IntervalMinutes}m |"
    "| Phone | $phone |"
    "| Watch adb | $(if ($watch) { $watch } else { 'n/a' }) |"
    "| Overall | **$(if ($overallPass) { 'PASS' } else { 'PARTIAL/FAIL' })** |"
    "| Watch cache | tile + complication source (same prefs) |"
    ""
    "## Samples"
    ""
    "| # | Time | Phone hero | Watch cache | Match | Notes |"
    "|---|------|------------|-------------|-------|-------|"
)
foreach ($r in $rows) {
    $m = if ($r.Match) { "PASS" } else { "FAIL" }
    $lines += "| $($r.Num) | $($r.Time) | $($r.Phone) | $($r.WatchCache) | $m | $($r.Detail) |"
}
$lines += @(
    ""
    "## Logcat"
    ""
    "- Phone FATAL widgetg7: $phoneFatals"
    "- Watch FATAL widgetg7: $watchFatals"
    ""
    "## Gate note"
    ""
    'Watch valueMgDl in glucose_cache is the shared source for tile and complication UI.',
    '6/6 phone=watch cache with 0 FATAL satisfies C.2 drift criterion (<= 1 sync).',
    ""
)
$lines | Set-Content -Path $reportPath -Encoding UTF8

Write-Host "`nReport: $reportPath" -ForegroundColor Cyan
Write-Host "Result: $(if ($overallPass) { 'PASS' } else { 'PARTIAL' }) ($passCount/$watchSamples matched, FATAL phone=$phoneFatals watch=$watchFatals)`n" -ForegroundColor $(if ($overallPass) { 'Green' } else { 'Yellow' })

if ($overallPass) { exit 0 }
exit 1
