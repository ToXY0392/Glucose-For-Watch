# Manipulation interactive de l'app phone dans Cursor (Simple Browser).
# Clic / swipe sur l'ecran = commandes ADB sur le Pixel.
#
# Usage:
#   .\scripts\qa\open-phone-interactive.ps1
#   .\scripts\qa\open-phone-interactive.ps1 -Launch

param(
    [switch]$Launch,
    [int]$Port = 8766
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

$PackageId = "com.glucoseforwatch.mobile"
$ServerScript = Join-Path $PSScriptRoot "phone-interactive-server.py"

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

function Resolve-Adb {
    $sdkDir = Read-LocalProperty "sdk.dir"
    if ($sdkDir) { $sdkDir = $sdkDir -replace '\\(.)', '$1' }
    if (-not $sdkDir -or -not (Test-Path $sdkDir)) {
        $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk"
    }
    $adb = Join-Path $sdkDir "platform-tools\adb.exe"
    if (-not (Test-Path $adb)) { Write-Error "adb introuvable: $adb" }
    return $adb
}

function Resolve-PhoneSerial {
    param([string]$Adb)
    $prop = Read-LocalProperty "gfw.adb.phone.serial"
    if (-not $prop) { $prop = $env:GFW_PHONE_SERIAL }
    $online = @(
        & $Adb devices 2>$null |
            Select-String "\sdevice$" |
            ForEach-Object { ($_ -split "\s+", 2)[0].Trim() } |
            Where-Object { $_ -and $_ -notmatch "^emulator-" }
    )
    if ($prop -and $online -contains $prop) { return $prop }
    if ($online.Count -ge 1) { return $online[0] }
    return $null
}

$adb = Resolve-Adb
$serial = Resolve-PhoneSerial -Adb $adb
if (-not $serial) {
    Write-Error "Aucun telephone ADB. Branchez le Pixel et activez le debogage USB."
}

Get-Job -Name "wg7-phone-interactive*" -ErrorAction SilentlyContinue | ForEach-Object {
    Stop-Job $_ -Force | Out-Null
    Remove-Job $_ -Force | Out-Null
}

Write-Host ""
Write-Host "=== Glucose For Watch - remote interactif (Cursor) ===" -ForegroundColor Cyan
Write-Host "  Serial: $serial"
Write-Host "  Port:   $Port"

if ($Launch) {
    & $adb -s $serial shell am force-stop $PackageId | Out-Null
    Start-Sleep -Milliseconds 400
    & $adb -s $serial shell am start -n "$PackageId/.SplashActivity" | Out-Null
    Start-Sleep -Seconds 1
}

$env:GFW_ADB = $adb
$env:GFW_PHONE_SERIAL = $serial

Start-Job -Name "wg7-phone-interactive" -ScriptBlock {
    param($PyScript, $PortNum, $AdbPath, $PhoneSerial)
    & py -3 $PyScript --port $PortNum --adb $AdbPath --serial $PhoneSerial
} -ArgumentList $ServerScript, $Port, $adb, $serial | Out-Null

Start-Sleep -Seconds 2

$url = "http://127.0.0.1:$Port/"
Write-Host ""
Write-Host "  URL: $url" -ForegroundColor Green
Write-Host ""
Write-Host "  Dans Cursor:" -ForegroundColor Cyan
Write-Host "    1. Ctrl+Shift+P" -ForegroundColor White
Write-Host "    2. Simple Browser: Show" -ForegroundColor White
Write-Host "    3. Coller: $url" -ForegroundColor White
Write-Host ""
Write-Host "  Clic = tap | glisser = swipe | boutons Retour / Accueil" -ForegroundColor DarkGray
Write-Host "  Stop: Get-Job wg7-phone-interactive | Stop-Job; Remove-Job wg7-phone-interactive -Force" -ForegroundColor DarkGray
Write-Host ""

Start-Process $url
