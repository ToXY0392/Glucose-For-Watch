# Désinstalle les anciennes apps / packages legacy sur phone + watch connectés.
# Usage:
#   .\scripts\qa\uninstall-legacy-apps.ps1
#   .\scripts\qa\uninstall-legacy-apps.ps1 -DryRun

param(
    [switch]$DryRun
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

$sdk = Read-LocalProperty "sdk.dir"
if (-not $sdk) { $sdk = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdk "platform-tools\adb.exe"
if (-not (Test-Path $adb)) { Write-Error "adb not found: $adb" }

# Packages historiques ou doublons possibles (canonique = com.widgetg7.mobile)
$LegacyPackages = @(
    "com.widgetg7.wear"
    "com.widgetg7"
    "com.toxy.mobile"
    "com.toxy.wear"
    "com.toxy"
    "com.widgetg7.mobile.debug"
)

$CurrentPackage = "com.widgetg7.mobile"

Write-Host "`n=== Uninstall legacy apps ===" -ForegroundColor Cyan
& $adb devices -l | Out-Host

$serials = @(
    & $adb devices 2>$null |
        Select-String "\sdevice$" |
        ForEach-Object { ($_ -split "\s+", 2)[0].Trim() } |
        Where-Object { $_ }
)

if ($serials.Count -eq 0) {
    Write-Host "[WARN] No devices online." -ForegroundColor Yellow
    exit 1
}

foreach ($serial in $serials) {
    Write-Host "`n--- Device $serial ---" -ForegroundColor DarkCyan
    foreach ($pkg in $LegacyPackages) {
        if ($pkg -eq $CurrentPackage) { continue }
        $installed = & $adb -s $serial shell pm path $pkg 2>$null
        if (-not $installed) { continue }
        if ($DryRun) {
            Write-Host "  [DRY] would uninstall $pkg" -ForegroundColor DarkYellow
        } else {
            Write-Host "  Uninstalling $pkg ..." -ForegroundColor Yellow
            & $adb -s $serial uninstall $pkg 2>&1 | Out-Host
        }
    }
    $current = & $adb -s $serial shell pm path $CurrentPackage 2>$null
    if ($current) {
        Write-Host "  [KEEP] $CurrentPackage installed" -ForegroundColor Green
    } else {
        Write-Host "  [INFO] $CurrentPackage not installed on this device" -ForegroundColor DarkGray
    }
}

Write-Host "`nDone. Reinstall current build: .\scripts\qa\install-and-verify.ps1`n" -ForegroundColor Green
