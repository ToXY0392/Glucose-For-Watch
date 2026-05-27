# Removes empty legacy folder "Widget G7" after Glucose-For-Watch migration.
# Run with Cursor/Studio closed, or let the one-shot logon task handle it.
param(
    [string]$LegacyPath = "C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7"
)

$ErrorActionPreference = "SilentlyContinue"

if (-not (Test-Path -LiteralPath $LegacyPath)) {
    Write-Host "Already gone: $LegacyPath" -ForegroundColor Green
    exit 0
}

# Drop caches first if anything remains
foreach ($d in @("build", ".gradle", ".kotlin", ".android", ".g2", ".idea")) {
    $p = Join-Path $LegacyPath $d
    if (Test-Path -LiteralPath $p) { cmd /c "rmdir /s /q `"$p`"" }
}

cmd /c "rmdir /s /q `"$LegacyPath`""
if (-not (Test-Path -LiteralPath $LegacyPath)) {
    Write-Host "Deleted: $LegacyPath" -ForegroundColor Green
    Unregister-ScheduledTask -TaskName "GlucoseForWatch-DeleteWidgetG7Once" -Confirm:$false
    exit 0
}

# Schedule delete at reboot if still locked
Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class MoveFileExHelper {
    [DllImport("kernel32.dll", SetLastError=true, CharSet=CharSet.Unicode)]
    public static extern bool MoveFileEx(string lpExistingFileName, string lpNewFileName, int dwFlags);
    public const int MOVEFILE_DELAY_UNTIL_REBOOT = 4;
}
"@
$extended = "\\?\$LegacyPath"
[void][MoveFileExHelper]::MoveFileEx($extended, $null, [MoveFileExHelper]::MOVEFILE_DELAY_UNTIL_REBOOT)
Write-Host "Folder still locked. Reboot Windows to finish removal." -ForegroundColor Yellow
Write-Host "Path: $LegacyPath"
exit 1
