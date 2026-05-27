# Chemin le plus court pour voir Glucose For Watch sur appareil ou apercu PNG
# Usage: .\scripts\qa\quick-run.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$adb = Join-Path $sdk "platform-tools\adb.exe"
$serials = @(& $adb devices 2>&1 | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] })

$phone = $serials | Where-Object { $_ -notmatch "^emulator-" } | Select-Object -First 1
$watch = $serials | Where-Object { $_ -ne $phone -and $_ -notmatch "^emulator-" } | Select-Object -First 1

if ($phone -and $watch) {
    Write-Host "Phone + montre detectes -> install complete" -ForegroundColor Green
    .\gradlew.bat installGlucoseForWatchDebug
    exit $LASTEXITCODE
}

if ($phone) {
    Write-Host "Phone seul -> install mobile" -ForegroundColor Green
    $env:ANDROID_SERIAL = $phone
    .\gradlew.bat :mobile:installDebug
    & $adb -s $phone shell am start -n com.glucoseforwatch.mobile/.SplashActivity
    exit $LASTEXITCODE
}

Write-Host "Aucun appareil USB -> apercu PNG (10 s, pas d'emulateur)" -ForegroundColor Yellow
& "$PSScriptRoot\export-app-preview.ps1"
