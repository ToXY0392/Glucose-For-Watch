# Acces dossiers controles : autorise studio64.exe (cfa).
# Pour CFA + exclusions antivirus sur le dossier projet, utiliser fix-windows-studio-defender-admin.ps1 (ADMIN).
# DOIT etre lance en administrateur (UAC).
#
# Usage : clic droit PowerShell > Executer en tant qu'administrateur, puis :
#   powershell -ExecutionPolicy Bypass -File .\allow-studio-controlled-folders.ps1

$ErrorActionPreference = "Stop"

function Find-Studio64 {
    $candidates = @(
        "C:\Program Files\Android\Android Studio\bin\studio64.exe",
        (Join-Path $env:LOCALAPPDATA "Programs\Android Studio\bin\studio64.exe"),
        "${env:ProgramFiles(x86)}\Android\Android Studio\bin\studio64.exe"
    )
    foreach ($c in $candidates) {
        if (Test-Path -LiteralPath $c) { return $c }
    }
    return $null
}

$studio = Find-Studio64
if (-not $studio) {
    Write-Host "studio64.exe introuvable dans les chemins par defaut. Ajoute-le a la main dans :"
    Write-Host "  Windows Securite > Protection contre les ransomware > Acces aux dossiers controles."
    exit 1
}

$isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator)
if (-not $isAdmin) {
    Write-Host "Relance ce script dans une console ADMINISTRATEUR."
    exit 2
}

$pref = Get-MpPreference -ErrorAction Stop
$list = @($pref.ControlledFolderAccessAllowedApplications | Where-Object { $_ })
if ($list -contains $studio) {
    Write-Host "Deja autorise : $studio"
    exit 0
}
$list += $studio
Add-MpPreference -ControlledFolderAccessAllowedApplications $list -ErrorAction Stop
Write-Host "OK : dossiers controles autorisent maintenant"
Write-Host "  $studio"
exit 0
