# Rename local folder Widget G7 -> Glucose-For-Watch (Windows).
#
# Methods (pick one):
#   Copy     Copy tree to real Glucose-For-Watch while Cursor keeps old path open (default)
#   Rename   In-place rename (requires Cursor + Studio closed)
#   Reboot   Schedule in-place rename at next Windows reboot
#   Junction Remove junction only (cleanup)
#
# Usage:
#   .\scripts\windows\rename-local-folder-to-glucose-for-watch.ps1
#   .\scripts\windows\rename-local-folder-to-glucose-for-watch.ps1 -Method Reboot

param(
    [ValidateSet("Copy", "Rename", "Reboot", "Junction")]
    [string]$Method = "Copy",
    [string]$Parent = "C:\Users\Utilisateur\Desktop\THP\Projects"
)

$ErrorActionPreference = "Stop"
$oldName = "Widget G7"
$newName = "Glucose-For-Watch"
$oldPath = Join-Path $Parent $oldName
$newPath = Join-Path $Parent $newName

function Remove-JunctionIfPresent {
    param([string]$Path)
    if (-not (Test-Path -LiteralPath $Path)) { return }
    $item = Get-Item -LiteralPath $Path -Force
    if ($item.Attributes -band [IO.FileAttributes]::ReparsePoint) {
        Write-Host "Removing junction: $Path" -ForegroundColor Cyan
        cmd /c rmdir "$Path"
    }
}

function Test-RealFolder {
    param([string]$Path)
    if (-not (Test-Path -LiteralPath $Path)) { return $false }
    $item = Get-Item -LiteralPath $Path -Force
    return -not ($item.Attributes -band [IO.FileAttributes]::ReparsePoint)
}

function Copy-ProjectTree {
    if (-not (Test-Path -LiteralPath $oldPath)) {
        Write-Error "Source not found: $oldPath"
    }
    Remove-JunctionIfPresent -Path $newPath
    if (Test-Path -LiteralPath $newPath) {
        Write-Error "Target already exists (real folder): $newPath. Move or delete it first."
    }

    Write-Host "Copying (read-only source OK while Cursor is open):" -ForegroundColor Cyan
    Write-Host "  $oldPath"
    Write-Host "  -> $newPath"
    Write-Host "  Excluding: build, .gradle, .kotlin, .android" -ForegroundColor DarkGray

    $robocopy = @(
        "robocopy", "`"$oldPath`"", "`"$newPath`"",
        "/E", "/COPY:DAT", "/DCOPY:DAT", "/XJ", "/R:2", "/W:2",
        "/XD", "build", ".gradle", ".kotlin", ".android"
    )
    $cmd = $robocopy -join " "
    cmd /c $cmd
    $rc = $LASTEXITCODE
    if ($rc -ge 8) {
        Write-Error "robocopy failed with exit code $rc"
    }

    if (-not (Test-Path -LiteralPath (Join-Path $newPath ".git"))) {
        Write-Error "Copy incomplete: .git missing in $newPath"
    }

    Write-Host "`nCopy OK (robocopy exit $rc)." -ForegroundColor Green
    Write-Host "1. Cursor: File -> Open Folder -> $newPath" -ForegroundColor Yellow
    Write-Host "2. When old window closed, delete: $oldPath" -ForegroundColor Yellow
}

function Rename-InPlace {
    Remove-JunctionIfPresent -Path $newPath
    if ((Test-Path -LiteralPath $oldPath) -and -not (Test-Path -LiteralPath $newPath)) {
        Rename-Item -LiteralPath $oldPath -NewName $newName
        Write-Host "Renamed. Open: $newPath" -ForegroundColor Green
        return
    }
    Write-Error "Rename blocked. Close Cursor/Studio or use -Method Copy."
}

function Schedule-RebootRename {
    Remove-JunctionIfPresent -Path $newPath
    if (Test-Path -LiteralPath $newPath) {
        Write-Error "Target path exists. Remove $newPath first."
    }
    if (-not (Test-Path -LiteralPath $oldPath)) {
        Write-Error "Source not found: $oldPath"
    }

    Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class MoveFileExHelper {
    [DllImport("kernel32.dll", SetLastError=true, CharSet=CharSet.Unicode)]
    public static extern bool MoveFileEx(string lpExistingFileName, string lpNewFileName, int dwFlags);
    public const int MOVEFILE_DELAY_UNTIL_REBOOT = 4;
}
"@

    $ok = [MoveFileExHelper]::MoveFileEx($oldPath, $newPath, [MoveFileExHelper]::MOVEFILE_DELAY_UNTIL_REBOOT)
    if (-not $ok) {
        throw "MoveFileEx failed. Run as Administrator or use -Method Copy."
    }
    Write-Host "Scheduled rename at next reboot:" -ForegroundColor Green
    Write-Host "  $oldPath -> $newPath"
    Write-Host "Reboot Windows to apply. Until then, use -Method Copy." -ForegroundColor Yellow
}

switch ($Method) {
    "Copy" { Copy-ProjectTree }
    "Rename" { Rename-InPlace }
    "Reboot" { Schedule-RebootRename }
    "Junction" { Remove-JunctionIfPresent -Path $newPath; Write-Host "Junction removed (if any)." -ForegroundColor Green }
}
