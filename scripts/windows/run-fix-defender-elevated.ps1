# Cree une fenetre UAC puis execute fix-windows-studio-defender-admin.ps1 en administrateur.
# Usage :
#   powershell.exe -File .\run-fix-defender-elevated.ps1
#   powershell.exe -File .\run-fix-defender-elevated.ps1 "C:\Dev\Glucose-For-Watch"

param(
    [Parameter(Position = 0)]
    [string] $ProjectPath = "C:\Dev\Glucose-For-Watch"
)

$ErrorActionPreference = "Stop"
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$fix = Join-Path $here "fix-windows-studio-defender-admin.ps1"

if (-not (Test-Path -LiteralPath $fix)) {
    Write-Host "Introuvable: $fix"
    exit 1
}

Write-Host "Accepte UAC (Oui) pour appliquer Defender / CFA sur : $ProjectPath"

$argList = @(
    "-NoProfile",
    "-ExecutionPolicy", "Bypass",
    "-File", $fix,
    "-ProjectPath", $ProjectPath
)

$p = Start-Process -FilePath "powershell.exe" -Verb RunAs -Wait -PassThru -ArgumentList $argList

if ($null -eq $p) {
    Write-Host "Start-Process a retourne null (UAC annule)."
    exit 2
}

Write-Host "Code sortie processe administrateur : $($p.ExitCode)"
exit [int]$p.ExitCode
