# Apercu reel de l'app ToXY (capture MainActivity via Robolectric)
# Usage: .\scripts\qa\export-app-preview.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

Write-Host "`n=== Export apercu app (MainActivity) ===" -ForegroundColor Cyan
.\gradlew.bat :mobile:testDebugUnitTest --tests "com.widgetg7.mobile.preview.AppPreviewExporterTest" 2>&1 | Out-Host

$preview = Join-Path $Root "mobile\build\app-previews\mobile-home.png"
if (-not (Test-Path $preview)) {
    Write-Error "PNG introuvable: $preview"
}

Write-Host "`n[OK] Apercu genere: $preview" -ForegroundColor Green
Start-Process $preview

Write-Host @"

Pour l'app sur emulateur/appareil (Run Android Studio) :
  1. Device Manager > Create Device (si aucun AVD)
  2. Brancher phone + montre USB, ou lancer l'emulateur
  3. .\gradlew.bat installWidgetG7Debug

"@
