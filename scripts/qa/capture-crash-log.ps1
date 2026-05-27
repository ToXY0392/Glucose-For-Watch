# Capture logcat FATAL apres crash Glucose For Watch.
#
# Usage:
#   .\scripts\qa\capture-crash-log.ps1
#   .\scripts\qa\capture-crash-log.ps1 -Watch
#
param(
    [switch]$Watch
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$outDir = Join-Path $Root "docs\qa\incidents"
if (-not (Test-Path $outDir)) { New-Item -ItemType Directory -Path $outDir -Force | Out-Null }

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

$date = Get-Date -Format "yyyy-MM-dd"
$target = if ($Watch) { "watch" } else { "phone" }
$key = if ($Watch) { "gfw.adb.watch.serial" } else { "gfw.adb.phone.serial" }
$serial = Read-LocalProperty $key
if (-not $serial) {
    $serial = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] } | Select-Object -First 1)
}
if (-not $serial) { Write-Error "No adb device for $target" }

$outFile = Join-Path $outDir "$date-crash-$target.log"
Write-Host "Capturing FATAL logcat -> $outFile" -ForegroundColor Cyan
& $adb -s $serial logcat -d -v time AndroidRuntime:E "*:S" 2>$null | Out-File -FilePath $outFile -Encoding utf8
Write-Host "[OK] Done. Review FATAL EXCEPTION blocks." -ForegroundColor Green
Write-Host "Paste into docs/qa/incidents/YYYY-MM-DD-app-crash.md" -ForegroundColor DarkGray
