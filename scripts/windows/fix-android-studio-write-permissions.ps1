# Fix "Write Permissions Issue" when opening the project from Android Studio on Windows.
# Usage (PowerShell, from any folder):
#   powershell -ExecutionPolicy Bypass -File "\\wsl$\Ubuntu\home\YOURUSER\...\Glucose-For-Watch\scripts\windows\fix-android-studio-write-permissions.ps1" -ProjectPath "C:\Users\You\...\Glucose-For-Watch"
# Or if the repo is already on disk:
#   .\scripts\windows\fix-android-studio-write-permissions.ps1
# Without -ProjectPath: uses the repo root (two levels above this script).

param(
    [string] $ProjectPath = "",
    [switch] $ElevatedTakeOwnership = $false
)

$ErrorActionPreference = "Stop"

function Resolve-ProjectRoot {
    if ($ProjectPath -and (Test-Path -LiteralPath $ProjectPath)) {
        return (Resolve-Path -LiteralPath $ProjectPath).Path
    }
    # This script lives in scripts/windows/
    $here = Split-Path -Parent $MyInvocation.MyCommand.Path
    return (Resolve-Path (Join-Path $here "..\..")).Path
}

$root = Resolve-ProjectRoot
Write-Host "Project root: $root"

$who = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
Write-Host "Granting Full control to: $who"

# 0a. Probe write (matches what IDEs implicitly need on the folder).
$probe = Join-Path $root ".studio-write-probe-delete-me"
try {
    Remove-Item -LiteralPath $probe -Force -ErrorAction SilentlyContinue
    Set-Content -LiteralPath $probe -Value "ok" -Encoding utf8 -ErrorAction Stop
    Remove-Item -LiteralPath $probe -Force -ErrorAction Stop
    Write-Host "Write probe: OK (create+delete test file)"
} catch {
    Write-Host "Write probe FAILED: $($_.Exception.Message)"
    Write-Host "If probe fails : projet sous OneDrive, antivirus (Protection contre les ransomware), ou dossier Lecture seule. "
    Write-Host "Déplace le projet vers ex. C:\Dev\Glucose-For-Watch (hors Desktop/OneDrive) ou exécute ce script en admin avec -ElevatedTakeOwnership."
}

# 0b. Fix ACL + lecture seule sur la chaîne de dossiers du profil (Desktop / THP / … bloquent souvent Studio).
$userRoot = $env:USERPROFILE.TrimEnd('\')
$p = [string]$root
$guard = 0
while (
    ($null -ne $p) -and
    ($p.Length -ge $userRoot.Length) -and
    ($p.StartsWith($userRoot, [StringComparison]::OrdinalIgnoreCase)) -and
    ($guard++ -lt 32)
) {
    Write-Host "Parent chain attrib+icacls: $p"
    attrib.exe -R $p /D 2>$null | Out-Null
    attrib.exe -R "$p\*" /D 2>$null | Out-Null
    icacls.exe $p /grant:r "${who}:(OI)(CI)F" /C /L | Out-Null
    icacls.exe $p /inheritance:e /C /L | Out-Null
    if ($p -eq $userRoot) { break }
    $np = Split-Path -Parent $p
    if ($np -eq $p) { break }
    $p = $np
}

# Optional: take ownership (often fixes OneDrive-marked folders) — nécessite console admin.
if ($ElevatedTakeOwnership) {
    Write-Host "takeown/icacls (admin) …"
    takeown.exe /f $root /r /d y 2>$null | Out-Null
    icacls.exe $root /grant:r "${who}:(OI)(CI)F" /T /C /L
}

# 1. Clear Explorer "read-only" on repo root then per top-level folder ( Gradle caches skipped:
# deep icacls on .gradle* hits MAX_PATH/junction noise and runs for tens of minutes).
attrib.exe -R "$root\*" /D 2>$null
Get-ChildItem -LiteralPath $root -Force -ErrorAction SilentlyContinue | ForEach-Object {
    if (-not ($_.PSIsContainer)) { return }
    if ($_.Name -like '.gradle*') {
        attrib.exe -R "$($_.FullName)\*" /D 2>$null | Out-Null
        Write-Host "(shallow ACL) $($_.FullName)"
        icacls.exe $_.FullName /grant:r "${who}:(OI)(CI)F" /C /L | Out-Null
        return
    }
    Write-Host "(attrib recurse) $($_.Name)"
    attrib.exe -R "$($_.FullName)\*" /S /D 2>$null | Out-Null
    Write-Host "(icacls recurse) $($_.Name)"
    icacls.exe $_.FullName /grant:r "${who}:(OI)(CI)F" /T /C /L
}

# 2. Root files (build.gradle.kts, etc.) + root folder ACE
icacls.exe $root /grant:r "${who}:(OI)(CI)F" /C /L | Out-Null
Get-ChildItem -LiteralPath $root -Force -File -ErrorAction SilentlyContinue | ForEach-Object {
    icacls.exe $_.FullName /grant:r "${who}:F" /C /L | Out-Null
}

Write-Host ""
Write-Host "Si Studio affiche encore l'erreur :"
Write-Host "  • Windows Sécurité > Protection contre les ransomware > Accès aux dossiers contrôlés : autoriser Android Studio (studio64.exe)."
Write-Host "  • Ou déplace le dépôt hors Desktop/OneDrive (ex. C:\Dev\Glucose-For-Watch) puis rouvre depuis ce chemin ou \\wsl$\...\Glucose-For-Watch ."
Write-Host "Done. Re-open the project in Android Studio (File > Invalidate Caches rarely needed)."
exit 0
