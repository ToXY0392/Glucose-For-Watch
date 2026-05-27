# Apercu reel de l'app Glucose For Watch (capture MainActivity via Robolectric)
# Usage: .\scripts\qa\export-app-preview.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

Write-Host "`n=== Export apercu app (6 etats home) ===" -ForegroundColor Cyan
.\gradlew.bat :mobile:testDebugUnitTest --tests "com.glucoseforwatch.mobile.preview.AppPreviewExporterTest" 2>&1 | Out-Host

py -3 (Join-Path $Root "scripts\qa\generate_preview_gallery.py")
$gallery = Join-Path $Root "mobile\build\preview-gallery\index.html"
if (-not (Test-Path $gallery)) {
    Write-Error "Galerie introuvable: $gallery"
}

Write-Host "`n[OK] Galerie generee: $gallery" -ForegroundColor Green
Start-Process $gallery

Write-Host @"

Pour l'app sur emulateur/appareil (Run Android Studio) :
  1. Device Manager > Create Device (si aucun AVD)
  2. Brancher phone + montre USB, ou lancer l'emulateur
  3. .\gradlew.bat installGlucoseForWatchDebug

"@
