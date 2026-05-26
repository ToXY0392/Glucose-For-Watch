# Rename local project folder Widget G7 -> Glucose-For-Watch (matches GitHub repo).
# Close Cursor and Android Studio before running.
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\scripts\windows\rename-local-folder-to-glucose-for-watch.ps1

$ErrorActionPreference = "Stop"
$parent = "C:\Users\Utilisateur\Desktop\THP\Projects"
$oldName = "Widget G7"
$newName = "Glucose-For-Watch"
$oldPath = Join-Path $parent $oldName
$newPath = Join-Path $parent $newName

if (Test-Path $newPath) {
    Write-Host "Already renamed: $newPath" -ForegroundColor Green
    exit 0
}

if (-not (Test-Path $oldPath)) {
    Write-Error "Source folder not found: $oldPath"
}

Write-Host "Renaming:" -ForegroundColor Cyan
Write-Host "  $oldPath"
Write-Host "  -> $newPath"

try {
    Rename-Item -LiteralPath $oldPath -NewName $newName
    Write-Host "OK. Reopen project in Cursor: $newPath" -ForegroundColor Green
    exit 0
} catch {
    Write-Host "FAILED: folder in use. Close Cursor + Android Studio, then re-run this script." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Yellow
    exit 1
}
