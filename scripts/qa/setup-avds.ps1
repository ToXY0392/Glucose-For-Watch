# Installe images systeme + cree AVD Glucose For Watch (phone + wear)
# Usage: .\scripts\qa\setup-avds.ps1

$ErrorActionPreference = "Stop"
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = $sdk
$env:ANDROID_HOME = $sdk
$sm = Join-Path $sdk "cmdline-tools\latest\bin\sdkmanager.bat"
$avd = Join-Path $sdk "cmdline-tools\latest\bin\avdmanager.bat"

if (-not (Test-Path $sm)) {
    Write-Error "cmdline-tools manquants. Extrayez commandlinetools-win-* dans $sdk\cmdline-tools\latest\"
}

$phoneImg = "system-images;android-36.1;google_apis_playstore;x86_64"
$wearImg = "system-images;android-36.1;android-wear-signed;x86_64"

Write-Host "Licences SDK..." -ForegroundColor Cyan
1..50 | ForEach-Object { "y" } | & $sm --sdk_root=$sdk --licenses | Out-Null

Write-Host "Installation images (plusieurs minutes)..." -ForegroundColor Cyan
& $sm --sdk_root=$sdk --install $phoneImg $wearImg "emulator" "platform-tools"

Write-Host "Creation AVD phone..." -ForegroundColor Cyan
"no" | & $avd create avd -n Gfw_Phone_API36 -k $phoneImg -d pixel_8 -f

Write-Host "Creation AVD wear..." -ForegroundColor Cyan
"no" | & $avd create avd -n Gfw_Wear_API36 -k $wearImg -d wearos_small_round -f

Write-Host "`n[OK] AVD:" -ForegroundColor Green
& (Join-Path $sdk "emulator\emulator.exe") -list-avds
