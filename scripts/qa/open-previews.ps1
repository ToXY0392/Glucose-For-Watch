# Ouvre les aperçus Glucose For Watch sans émulateur
# Usage: .\scripts\qa\open-previews.ps1

$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

Write-Host "`n=== Design reference (navigateur) ===" -ForegroundColor Cyan
py -3 toxy-ux-kit/tools/export-design-reference.py
Start-Process (Join-Path $Root "toxy-ux-kit\design-reference\index.html")

Write-Host "`n=== Android Studio Compose Preview ===" -ForegroundColor Cyan
Write-Host @"

1. Gradle Sync (File > Sync Project with Gradle Files)
2. Ouvrir : wear/src/main/java/com/widgetg7/wear/ui/WearStatusScreenPreview.kt
3. View > Tool Windows > Preview  (ou Split / Design)
4. Choisir un preview : In range 120 | High 200 | Stale | No data

APK debug (si appareil/AVD connecté) :
  .\gradlew.bat installWidgetG7Debug

"@

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (Test-Path $adb) {
    $n = (& $adb devices | Select-String "device$" | Measure-Object).Count
    if ($n -eq 0) {
        Write-Host "[INFO] Aucun appareil ADB — Preview Studio suffit pour l'aperçu visuel." -ForegroundColor Yellow
        Write-Host "       Device Manager > Create Device > Phone ou Wear Round pour Run." -ForegroundColor Yellow
    } else {
        Write-Host "[OK] $n appareil(s) ADB — install possible." -ForegroundColor Green
    }
}
