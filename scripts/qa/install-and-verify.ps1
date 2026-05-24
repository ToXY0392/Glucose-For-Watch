# Glucose For Watch — install debug APKs + automated package checks + QA checklist
# Usage:
#   .\scripts\qa\install-and-verify.ps1              # install phone+watch if 2 devices
#   .\scripts\qa\uninstall-legacy-apps.ps1         # remove old package IDs first (optional)
#   .\scripts\qa\install-and-verify.ps1 -VerifyOnly  # checks only, no gradle
#   .\scripts\qa\install-and-verify.ps1 -AllowPhoneOnly

param(
    [switch]$VerifyOnly,
    [switch]$AllowPhoneOnly,
    [switch]$SkipInstall
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

$PackageId = "com.widgetg7.mobile"
$AppLabel = "Glucose For Watch"
$ExpectedVersionName = "0.4.0"

function Read-LocalProperty {
    param([string]$Key)
    $localProps = Join-Path $Root "local.properties"
    if (-not (Test-Path $localProps)) { return $null }
    foreach ($line in Get-Content $localProps) {
        if ($line -match "^$([regex]::Escape($Key))=(.+)$") {
            return $matches[1].Trim()
        }
    }
    return $null
}

function Unescape-JavaPropertyPath {
    param([string]$Value)
    if (-not $Value) { return $null }
    return $Value -replace '\\(.)', '$1'
}

function Resolve-Adb {
    $sdkDir = Unescape-JavaPropertyPath (Read-LocalProperty "sdk.dir")
    if (-not $sdkDir -or -not (Test-Path $sdkDir)) {
        $sdkDir = $env:ANDROID_SDK_ROOT
    }
    if (-not $sdkDir -or -not (Test-Path $sdkDir)) {
        $sdkDir = $env:ANDROID_HOME
    }
    if (-not $sdkDir -or -not (Test-Path $sdkDir)) {
        $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk"
    }
    $adb = Join-Path $sdkDir "platform-tools\adb.exe"
    if (-not (Test-Path $adb)) {
        Write-Error "adb not found: $adb (set sdk.dir in local.properties)"
    }
    return $adb
}

function Get-OnlineSerials {
    param([string]$Adb)
    & $adb devices -l | Out-Host
    return @(
        & $adb devices 2>$null |
            Select-String "\sdevice$" |
            ForEach-Object { ($_ -split "\s+", 2)[0].Trim() } |
            Where-Object { $_ -and $_ -notmatch "^emulator-" }
    )
}

function Resolve-TargetSerials {
    param(
        [string[]]$Online,
        [string]$PhoneProp,
        [string]$WatchProp
    )
    $phone = $null
    $watch = $null
    if ($PhoneProp -and $Online -contains $PhoneProp) { $phone = $PhoneProp }
    if ($WatchProp -and $Online -contains $WatchProp) { $watch = $WatchProp }
    if (-not $phone -and $Online.Count -ge 1) { $phone = $Online[0] }
    if (-not $watch -and $Online.Count -ge 2) {
        $watch = $Online | Where-Object { $_ -ne $phone } | Select-Object -First 1
    }
    if ($PhoneProp -and -not $phone) {
        Write-Host "[WARN] Phone serial from local.properties offline: $PhoneProp" -ForegroundColor Yellow
    }
    if ($WatchProp -and -not $watch) {
        Write-Host "[WARN] Watch serial from local.properties offline: $WatchProp" -ForegroundColor Yellow
    }
    return @{ Phone = $phone; Watch = $watch }
}

function Test-PackageInstalled {
    param(
        [string]$Adb,
        [string]$Serial,
        [string]$Role
    )
    if (-not $Serial) {
        Write-Host "  [SKIP] $Role - no serial" -ForegroundColor DarkYellow
        return $false
    }
    $installed = & $adb -s $Serial shell pm path $PackageId 2>$null
    if (-not $installed) {
        Write-Host "  [FAIL] $Role ($Serial) - $PackageId not installed" -ForegroundColor Red
        return $false
    }
    $version = & $adb -s $Serial shell dumpsys package $PackageId 2>$null |
        Select-String "versionName=" |
        Select-Object -First 1
    $versionText = if ($version) { $version.ToString().Trim() } else { "version unknown" }
    Write-Host "  [OK] $Role ($Serial) - $PackageId - $versionText" -ForegroundColor Green
    return $true
}

function Show-QaChecklist {
    Write-Host ""
    Write-Host "=== Manual QA - docs/plan/QA-MATRIX-G6-G7.md ===" -ForegroundColor Cyan
    @(
        'B.1.1.3  Tile Glycemie + bouton sync'
        'B.1.1.4  Complication SHORT_TEXT sur cadran'
        'B.1.2    Dexcom Share US ou OUS'
        'B.1.3    Sync continue 30 min'
        'B.1.4    Offline 1-2 h puis rattrapage auto'
        'B.1.5    Tap sync tile + tail-sync-logs.ps1'
        'B.1.8    Couleurs AGP - pas de mint sur chiffre'
        'S1-S4    Verifications sync P0 post-audit'
    ) | ForEach-Object { Write-Host "  [ ] $_" }
    Write-Host ""
    Write-Host "Captures: docs/qa/captures/ (voir README)" -ForegroundColor DarkGray
    Write-Host ""
}

$adb = Resolve-Adb
Write-Host "`n=== ADB devices ===" -ForegroundColor Cyan
$online = Get-OnlineSerials -Adb $adb

$phoneSerialProp = Read-LocalProperty "widgetg7.adb.phone.serial"
if (-not $phoneSerialProp) { $phoneSerialProp = $env:WIDGETG7_PHONE_SERIAL }
$watchSerialProp = Read-LocalProperty "widgetg7.adb.watch.serial"
if (-not $watchSerialProp) { $watchSerialProp = $env:WIDGETG7_WATCH_SERIAL }

$targets = Resolve-TargetSerials -Online $online -PhoneProp $phoneSerialProp -WatchProp $watchSerialProp

if ($online.Count -eq 0) {
    Write-Host "`n[WARN] No USB/Wi-Fi devices online." -ForegroundColor Yellow
    Write-Host "  Connect phone + watch, enable USB debugging, then re-run."
    Write-Host "  Or run: .\scripts\qa\export-app-preview.ps1 (PNG sans hardware)`n"
    Show-QaChecklist
    exit 2
}

if (-not $targets.Phone) {
    Write-Error "Phone serial not resolved. Set widgetg7.adb.phone.serial in local.properties"
}

$needWatch = -not $AllowPhoneOnly
if ($needWatch -and -not $targets.Watch) {
    Write-Host "`n[WARN] Watch serial not found (only phone: $($targets.Phone))." -ForegroundColor Yellow
    Write-Host "  Use -AllowPhoneOnly for mobile-only install, or set widgetg7.adb.watch.serial`n"
    if (-not $AllowPhoneOnly) { exit 1 }
}

if (-not $SkipInstall -and -not $VerifyOnly) {
    Write-Host "`n=== Build + install $AppLabel v$ExpectedVersionName ===" -ForegroundColor Cyan
    if ($targets.Phone -and $targets.Watch -and -not $AllowPhoneOnly) {
        & .\gradlew.bat installWidgetG7Debug
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    } elseif ($targets.Phone) {
        & .\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
        $mobileApk = Join-Path $Root "mobile\build\outputs\apk\debug\mobile-debug.apk"
        & $adb -s $targets.Phone install -t -r $mobileApk
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    }
}

Write-Host "`n=== Package verification ===" -ForegroundColor Cyan
$phoneOk = Test-PackageInstalled -Adb $adb -Serial $targets.Phone -Role "Phone"
$watchOk = $false
if ($targets.Watch) {
    $watchOk = Test-PackageInstalled -Adb $adb -Serial $targets.Watch -Role "Watch"
}

Write-Host "`n=== Launcher labels (manual confirm) ===" -ForegroundColor Cyan
Write-Host "  Phone + watch should show: $AppLabel"

if ($phoneOk) {
    Write-Host "`n=== Launch phone app ===" -ForegroundColor Cyan
    & $adb -s $targets.Phone shell am start -n "$PackageId/.SplashActivity" | Out-Null
}

Show-QaChecklist

if ($phoneOk -and ($watchOk -or $AllowPhoneOnly)) {
    Write-Host "Automated checks passed. Complete matrix on hardware.`n" -ForegroundColor Green
    exit 0
}
Write-Host "Some automated checks failed.`n" -ForegroundColor Yellow
exit 1
