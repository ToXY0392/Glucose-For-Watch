# Tail sync-related logcat (phone + watch) for QA B.1.5 / S2 / S3
# Usage:
#   .\scripts\qa\tail-sync-logs.ps1
#   .\scripts\qa\tail-sync-logs.ps1 -ClearFirst

param(
    [switch]$ClearFirst
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent

function Unescape-JavaPropertyPath {
    param([string]$Value)
    if (-not $Value) { return $null }
    return $Value -replace '\\(.)', '$1'
}

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

$sdkDir = Unescape-JavaPropertyPath (Read-LocalProperty "sdk.dir")
if (-not $sdkDir -or -not (Test-Path $sdkDir)) { $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdkDir "platform-tools\adb.exe"
if (-not (Test-Path $adb)) { Write-Error "adb not found: $adb" }

$phone = Read-LocalProperty "gfw.adb.phone.serial"
if (-not $phone) { $phone = $env:GFW_PHONE_SERIAL }
$watch = Read-LocalProperty "gfw.adb.watch.serial"
if (-not $watch) { $watch = $env:GFW_WATCH_SERIAL }

$tags = "WG7.PhoneSyncEngine|WG7.PhoneWearRefresh|WG7.WearDataLayer|WG7.Complication|GlucoseRefresh"

Write-Host "`n=== Sync log tail ($tags) ===" -ForegroundColor Cyan
Write-Host "Tap sync on watch tile now (B.1.5). Ctrl+C to stop.`n"

if ($ClearFirst) {
    if ($phone) { & $adb -s $phone logcat -c 2>$null }
    if ($watch) { & $adb -s $watch logcat -c 2>$null }
}

function Tail-Device {
    param([string]$Serial, [string]$Label)
    if (-not $Serial) { return }
    Write-Host "--- $Label ($Serial) ---" -ForegroundColor DarkCyan
    & $adb -s $Serial logcat -v time -T 1 -s $tags
}

if ($phone -and $watch) {
    Write-Host "Phone + watch configured. Tailing phone first; run a second terminal for watch if needed."
    Write-Host "  watch: adb -s $watch logcat -v time -s $tags`n"
    Tail-Device -Serial $phone -Label "Phone"
} elseif ($phone) {
    Tail-Device -Serial $phone -Label "Phone"
} elseif ($watch) {
    Tail-Device -Serial $watch -Label "Watch"
} else {
    Write-Host "[WARN] No serial in local.properties — tailing default device" -ForegroundColor Yellow
    & $adb logcat -v time -T 1 -s $tags
}
