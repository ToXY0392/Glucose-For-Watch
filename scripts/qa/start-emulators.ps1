# Demarre les AVD ToXY (phone + wear optionnel)
# Usage:
#   .\scripts\qa\start-emulators.ps1           # phone seulement
#   .\scripts\qa\start-emulators.ps1 -Wear    # phone + montre

param(
    [switch]$Wear
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$emu = Join-Path $sdk "emulator\emulator.exe"

if (-not (Test-Path $emu)) {
    Write-Error "Emulateur introuvable: $emu"
}

function Start-Avd([string]$Name) {
    $running = & $emu -list-avds 2>$null
    if ($running -notcontains $Name) {
        Write-Error "AVD '$Name' introuvable. Creez-le via Device Manager ou scripts/qa/setup-avds.ps1"
    }
    Write-Host "Demarrage $Name ..." -ForegroundColor Cyan
    Start-Process -FilePath $emu -ArgumentList @(
        "-avd", $Name,
        "-gpu", "swiftshader_indirect"
    )
}

Start-Avd "Toxy_Phone_API36"
if ($Wear) {
    Start-Sleep -Seconds 5
    Start-Avd "Toxy_Wear_API36"
}

Write-Host @"

AVD lances. Premier boot: 1-3 min.
Verifier: adb devices -l
Puis: .\gradlew.bat :mobile:installDebug

Si WHPX echoue depuis un terminal, lancez l'AVD depuis Android Studio > Device Manager (Play).

"@
