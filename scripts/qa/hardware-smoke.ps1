# Automated smoke checks when phone is on adb (watch may be Data Layer only).
# Usage: .\scripts\qa\hardware-smoke.ps1

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

Write-Host "`n=== Glucose For Watch hardware smoke ($phone) ===" -ForegroundColor Cyan

function Get-AppXml {
    param([string]$PrefsName)
    & $adb -s $phone shell "run-as com.glucoseforwatch.mobile cat shared_prefs/$PrefsName.xml 2>/dev/null"
}

function XmlLongValue {
    param([string]$Xml, [string]$Name)
    if ($Xml -match "<long name=`"$Name`" value=`"([^`"]*)`"") { return [long]$matches[1] }
    if ($Xml -match "<int name=`"$Name`" value=`"([^`"]*)`"") { return [long]$matches[1] }
    return $null
}

function XmlValue {
    param([string]$Xml, [string]$Name)
    if ($Xml -match "<int name=`"$Name`" value=`"([^`"]*)`"") { return $matches[1] }
    if ($Xml -match "<long name=`"$Name`" value=`"([^`"]*)`"") { return $matches[1] }
    if ($Xml -match "<boolean name=`"$Name`" value=`"([^`"]*)`"") { return $matches[1] }
    if ($Xml -match "<string name=`"$Name`"[^>]*>([^<]*)</string>") { return $matches[1] }
    return $null
}

function Test-WearTileProvider {
    param([string]$Serial)
    if (-not $Serial) { return $false }
    $listed = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] })
    if ($Serial -notin $listed) { return $false }
    $tile = & $adb -s $Serial shell dumpsys package com.glucoseforwatch.mobile 2>$null |
        Select-String "GlucoseTileServiceV2"
    return [bool]$tile
}

$pkg = & $adb -s $phone shell pm path com.glucoseforwatch.mobile 2>$null
if (-not $pkg) { Write-Host "[FAIL] App not installed" -ForegroundColor Red; exit 1 }
Write-Host "[OK] com.glucoseforwatch.mobile installed" -ForegroundColor Green

$sync = Get-AppXml "widget_g7_sync_status"
$state = Get-AppXml "gfw_phone_sync_state"
$health = Get-AppXml "widget_g7_watch_health"

$lastValue = XmlValue $sync "last_value"
$watchPending = XmlValue $sync "watch_push_pending"
$pushSeq = XmlValue $state "last_push_sequence_id"
$ackSeq = XmlValue $state "last_ack_sequence_id"
$pushSeqLong = XmlLongValue $state "last_push_sequence_id"
$ackSeqLong = XmlLongValue $state "last_ack_sequence_id"
$pushFails = XmlValue $state "consecutive_wear_push_failures"
$watchApp = XmlValue $health "app_installed"
$watchVer = XmlValue $health "app_version_name"
$watchModel = XmlValue $health "model"
$ackFails = XmlValue $health "ack_failure_count"

$watchSerial = Read-LocalProperty "gfw.adb.watch.serial"
$adbDevices = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] })
$watchAdbOnline = [bool]$watchSerial -and ($watchSerial -in $adbDevices)
$wearTileOk = Test-WearTileProvider $watchSerial
if ($wearTileOk) {
    Write-Host "  wear tile provider: registered" 
} elseif ($watchSerial -and -not $watchAdbOnline) {
    Write-Host "  wear tile provider: skipped (watch ADB offline — adb mdns services)" -ForegroundColor Yellow
} else {
    Write-Host "  wear tile provider: MISSING (phone APK on watch? run installGlucoseForWatchDebug)" -ForegroundColor Yellow
}

Write-Host "  Hero value: $lastValue mg/dL"
Write-Host "  watch_push_pending: $watchPending"
Write-Host "  push/ack seq: $pushSeq / $ackSeq"
Write-Host "  wear push failures: $pushFails"
Write-Host "  watch app: installed=$watchApp version=$watchVer model=$watchModel"
Write-Host "  watch ack failures: $ackFails"

$pass = 0
$fail = 0

if ($lastValue) { Write-Host "[OK] B.1.2 Dexcom hero has value" -ForegroundColor Green; $pass++ }
else { Write-Host "[FAIL] B.1.2 no glucose value" -ForegroundColor Red; $fail++ }

if ($wearTileOk) {
    if ($watchApp -eq "true" -and $watchVer -match '^0\.[456]\.') {
        Write-Host "[OK] B.1.1.2 Watch app $watchVer (tile + Data Layer)" -ForegroundColor Green; $pass++
    } else {
        Write-Host '[WARN] B.1.1.2 Watch app not confirmed (open wear app once on watch)' -ForegroundColor Yellow
    }
} elseif (-not $watchAdbOnline -and $watchApp -eq "true" -and $watchVer -match '^0\.[456]\.') {
    Write-Host "[OK] B.1.1.2 Watch app $watchVer via Data Layer (tile unchecked — ADB offline)" -ForegroundColor Green; $pass++
} elseif ($watchAdbOnline) {
    Write-Host '[FAIL] B.1.1.2 Watch APK wrong variant - reinstall with installGlucoseForWatchDebug' -ForegroundColor Red
    $fail++
} else {
    Write-Host '[WARN] B.1.1.2 Watch not confirmed (connect watch ADB or open wear app once)' -ForegroundColor Yellow
}

if ($pushSeqLong -gt 0 -and $pushSeqLong -eq $ackSeqLong) {
    Write-Host '[OK] S3 Watch ACK matches last push' -ForegroundColor Green; $pass++
} elseif ($pushSeqLong -gt 0 -and $pushSeqLong -ne $ackSeqLong) {
    Write-Host "[FAIL] S3 push/ack mismatch (push=$pushSeq ack=$ackSeq) - open wear app or tap tile sync" -ForegroundColor Red; $fail++
} else {
    Write-Host '[WARN] S3 no push yet - tap sync on watch tile' -ForegroundColor Yellow
}

if ($watchPending -eq "false" -and $pushFails -eq "0") {
    Write-Host "[OK] S1 No watch push pending / no failure streak" -ForegroundColor Green; $pass++
} else {
    Write-Host "[WARN] S1 watch push pending or failures > 0" -ForegroundColor Yellow
}

if ($ackFails -eq "0") { Write-Host "[OK] S3 ack_failure_count=0" -ForegroundColor Green; $pass++ }

Write-Host ""
Write-Host "Automated: $pass passed, $fail failed. Manual still needed: tile tap (B.1.5), AGP visual (B.1.8), offline 2h (B.1.4)." -ForegroundColor Cyan
Write-Host "Next: tap sync on watch tile, run .\scripts\qa\tail-sync-logs.ps1 in another terminal`n"

exit $fail
