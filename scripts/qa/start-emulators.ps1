# Demarre les AVD Glucose For Watch (phone + wear optionnel)
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

function Resolve-AvdName {
    param([string[]]$Candidates)
    $available = @(& $emu -list-avds 2>$null)
    foreach ($name in $Candidates) {
        if ($available -contains $name) { return $name }
    }
    return $null
}

function Start-Avd([string]$Name) {
    if (-not $Name) {
        Write-Error "AVD introuvable. Creez-le via scripts/qa/setup-avds.ps1 (Gfw_Phone_API36 / Gfw_Wear_API36)"
    }
    Write-Host "Demarrage $Name ..." -ForegroundColor Cyan
    Start-Process -FilePath $emu -ArgumentList @(
        "-avd", $Name,
        "-gpu", "swiftshader_indirect"
    )
}

$phoneAvd = Resolve-AvdName @("Gfw_Phone_API36", "Toxy_Phone_API36")
$wearAvd = Resolve-AvdName @("Gfw_Wear_API36", "Toxy_Wear_API36")

Start-Avd $phoneAvd
if ($Wear) {
    Start-Sleep -Seconds 5
    Start-Avd $wearAvd
}

Write-Host @"

AVD lances. Premier boot: 1-3 min.
Verifier: adb devices -l
Puis: .\gradlew.bat :mobile:installDebug

Si WHPX echoue depuis un terminal, lancez l'AVD depuis Android Studio > Device Manager (Play).

"@
