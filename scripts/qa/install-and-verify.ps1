# ToXY v0.4.0 — install debug APKs + print hardware QA checklist
# Usage: .\scripts\qa\install-and-verify.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

$localProps = Join-Path $Root "local.properties"
if (-not (Test-Path $localProps)) {
    Write-Error "local.properties missing. Copy from local.properties.example and set sdk.dir + serials."
}

$sdkDir = ($null | ForEach-Object {
    Get-Content $localProps | ForEach-Object {
        if ($_ -match '^sdk\.dir=(.+)$') { $matches[1].Replace('\\', '\') }
    }
}) | Select-Object -First 1

$adb = Join-Path $sdkDir "platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    Write-Error "adb not found: $adb"
}

Write-Host "`n=== ADB devices ===" -ForegroundColor Cyan
& $adb devices -l
$online = (& $adb devices | Select-String "device$" | Measure-Object).Count
if ($online -lt 2) {
    Write-Host "`n[WARN] Need phone + watch connected (USB or Wi-Fi adb)." -ForegroundColor Yellow
    Write-Host "  Phone serial (local.properties): widgetg7.adb.phone.serial"
    Write-Host "  Watch serial: widgetg7.adb.watch.serial"
    Write-Host "  Enable USB debugging on both devices, then re-run this script.`n"
    exit 1
}

Write-Host "`n=== Build + install ToXY v0.4.0 ===" -ForegroundColor Cyan
& .\gradlew.bat installWidgetG7Debug
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "`n=== Post-install checks (manual) ===" -ForegroundColor Cyan
@(
    "Phone launcher shows app name ToXY (v0.4.0)"
    "Watch launcher shows ToXY"
    "Tile: sync button visible, tap triggers refresh"
    "Tile + phone hero: AGP colors (not mint on glucose value)"
    "Wear status screen: AGP value + Sync button"
    "Complication matches tile value"
    "30 min continuous sync — no drift"
    "Watch offline 1-2 h -> phone badge -> reconnect -> auto catch-up"
    "G6/G7 matrix: docs/plan/QA-MATRIX-G6-G7.md"
) | ForEach-Object { Write-Host "  [ ] $_" }

Write-Host "`nDone. Fill checklists in docs/plan/PROGRESS.md and QA-MATRIX-G6-G7.md`n" -ForegroundColor Green
