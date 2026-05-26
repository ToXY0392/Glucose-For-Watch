# Final rename: Widget G7 -> Glucose-For-Watch (after junction workaround).
# Close Cursor and Android Studio before running.
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\scripts\windows\rename-local-folder-to-glucose-for-watch.ps1

$ErrorActionPreference = "Stop"
$parent = "C:\Users\Utilisateur\Desktop\THP\Projects"
$oldName = "Widget G7"
$newName = "Glucose-For-Watch"
$oldPath = Join-Path $parent $oldName
$newPath = Join-Path $parent $newName

if ((Test-Path $oldPath) -and -not (Test-Path $newPath)) {
    Write-Host "Renaming $oldPath -> $newPath" -ForegroundColor Cyan
    Rename-Item -LiteralPath $oldPath -NewName $newName
    Write-Host "OK. Open: $newPath" -ForegroundColor Green
    exit 0
}

if (-not (Test-Path $newPath)) {
    Write-Error "Neither $oldPath nor $newPath exists."
}

# Junction workaround: Glucose-For-Watch points at Widget G7
$item = Get-Item -LiteralPath $newPath -Force
if ($item.Attributes -band [IO.FileAttributes]::ReparsePoint) {
    if (-not (Test-Path $oldPath)) {
        Write-Error "Junction at $newPath but source $oldPath missing."
    }
    Write-Host "Removing junction $newPath ..." -ForegroundColor Cyan
    cmd /c rmdir "$newPath"
    Write-Host "Renaming $oldPath -> $newName ..." -ForegroundColor Cyan
    Rename-Item -LiteralPath $oldPath -NewName $newName
    Write-Host "OK. Open: $newPath" -ForegroundColor Green
    exit 0
}

if (Test-Path $oldPath) {
    Write-Error "Both $oldPath and $newPath exist (not a junction). Resolve manually."
}

Write-Host "Already renamed: $newPath" -ForegroundColor Green
