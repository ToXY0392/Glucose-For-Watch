# Pair/connect Pixel Watch (or Wear OS) for adb install when only phone is on USB.
# Usage:
#   .\scripts\qa\connect-watch-adb.ps1 -WatchIp 192.168.1.42 -PairPort 12345 -PairCode 123456 -AdbPort 45678
#   .\scripts\qa\connect-watch-adb.ps1 -TryBridge   # forward via phone USB (Pixel)

param(
    [string]$WatchIp,
    [int]$PairPort = 0,
    [string]$PairCode,
    [int]$AdbPort = 0,
    [switch]$TryBridge,
    [switch]$InstallWear
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
if (-not $phone) { $phone = $env:GFW_PHONE_SERIAL }

Write-Host ""
Write-Host "=== Connect Wear OS watch to adb ===" -ForegroundColor Cyan
Write-Host "On watch: Settings > System > Developer options"
Write-Host "  1. ADB debugging ON"
Write-Host "  2. Wireless debugging ON > Pair new device (note IP, pair port, code)"
Write-Host "  3. Then note IP + adb port for connect"
Write-Host ""

if ($TryBridge -and $phone) {
    Write-Host "Trying USB bridge via phone $phone ..." -ForegroundColor Cyan
    & $adb -s $phone forward --remove tcp:4444 2>$null | Out-Null
    & $adb -s $phone forward tcp:4444 localabstract:/adb-hub
    & $adb disconnect 127.0.0.1:4444 2>$null | Out-Null
    & $adb connect 127.0.0.1:4444
    Start-Sleep -Seconds 2
    & $adb devices -l | Out-Host
}

if ($WatchIp -and $PairPort -gt 0 -and $PairCode) {
    Write-Host "Pairing $WatchIp`:$PairPort ..." -ForegroundColor Cyan
    & $adb pair "${WatchIp}:${PairPort}" $PairCode
}

if ($WatchIp -and $AdbPort -gt 0) {
    Write-Host "Connecting $WatchIp`:$AdbPort ..." -ForegroundColor Cyan
    & $adb connect "${WatchIp}:${AdbPort}"
    Start-Sleep -Seconds 2
}

& $adb devices -l | Out-Host

$watchSerial = Read-LocalProperty "gfw.adb.watch.serial"
$online = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] })
$watchOnline = $online | Where-Object { $_ -ne $phone -and $_ -notmatch "^127\.0\.0\.1" -or ($_ -match "^127\.0\.0\.1" -and $_ -notmatch "offline") }

if ($InstallWear -and ($watchOnline -or ($watchSerial -and $online -contains $watchSerial))) {
    $target = if ($online -contains $watchSerial) { $watchSerial } else { ($online | Where-Object { $_ -ne $phone } | Select-Object -First 1) }
    $apk = Join-Path $Root "wear\build\outputs\apk\debug\wear-debug.apk"
    if (-not (Test-Path $apk)) {
        & .\gradlew.bat :wear:assembleDebug
    }
    Write-Host "Installing wear APK to $target ..." -ForegroundColor Cyan
    & $adb -s $target install -t -r $apk
}

if (-not $watchOnline -and -not ($watchSerial -and $online -contains $watchSerial)) {
    Write-Host ""
    Write-Host "Watch not online in adb yet." -ForegroundColor Yellow
    Write-Host "Alternative: phone app > Montre > Assistant install (ADB Wi-Fi / Kadb)"
    Write-Host "  Or run with -WatchIp -PairPort -PairCode -AdbPort after wireless debug on watch"
    exit 2
}

Write-Host ""
Write-Host "Watch reachable. Update gfw.adb.watch.serial in local.properties if needed." -ForegroundColor Green
exit 0
