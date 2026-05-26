# Attend l'emulateur, installe :mobile debug, lance l'app
# Usage: .\scripts\qa\run-mobile-on-emulator.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$adb = Join-Path $sdk "platform-tools\adb.exe"

Write-Host "Attente emulateur (adb)..." -ForegroundColor Cyan
$deadline = (Get-Date).AddMinutes(10)
$serial = $null
while ((Get-Date) -lt $deadline) {
    $line = & $adb devices 2>&1 | Select-String "emulator-\d+\s+device"
    if ($line) {
        $serial = ($line -split "\s+")[0]
        $boot = (& $adb -s $serial shell getprop sys.boot_completed 2>$null) -replace "[\r\n]", ""
        if ($boot -eq "1") { break }
    }
    Start-Sleep -Seconds 5
}
if (-not $serial) {
    Write-Error "Aucun emulateur pret. Lancez: .\scripts\qa\start-emulators.ps1 ou Device Manager > Play sur Gfw_Phone_API36"
}

$env:ANDROID_SERIAL = $serial
Write-Host "Device: $serial" -ForegroundColor Green

Write-Host "Build + install :mobile..." -ForegroundColor Cyan
.\gradlew.bat :mobile:installDebug

Write-Host "Lancement SplashActivity..." -ForegroundColor Cyan
& $adb -s $serial shell am start -n com.widgetg7.mobile/.SplashActivity

Write-Host "`n[OK] Glucose For Watch lance sur $serial" -ForegroundColor Green
