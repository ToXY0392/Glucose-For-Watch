# Lance Android Studio en lui passant le dossier projet (evite certains soucis File > Open).
# Usage (PowerShell normal) :
#   .\launch-android-studio-with-project.ps1
#   .\launch-android-studio-with-project.ps1 -ProjectPath "C:\Dev\Widget-G7"

param(
    [string] $ProjectPath = "C:\Dev\Widget-G7"
)

$studio = @(
    "C:\Program Files\Android\Android Studio\bin\studio64.exe",
    "${env:ProgramFiles(x86)}\Android\Android Studio\bin\studio64.exe",
    (Join-Path $env:LOCALAPPDATA "Programs\Android Studio\bin\studio64.exe")
) | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1

if (-not $studio) {
    Write-Host "studio64.exe introuvable. Installe Android Studio ou corrige les chemins dans ce script."
    exit 1
}

if (-not (Test-Path -LiteralPath $ProjectPath)) {
    Write-Host "Dossier projet introuvable: $ProjectPath"
    exit 1
}

$p = (Resolve-Path -LiteralPath $ProjectPath).Path
Write-Host "Lancement: $studio -> $p"
# Lancement: chemin projet en UNIQUE argument avec guillemets si espaces internes.
Start-Process -FilePath $studio -ArgumentList "`"$p`""
exit 0
