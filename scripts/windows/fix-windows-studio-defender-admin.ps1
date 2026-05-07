# corrige les blocages qui font encore afficher dans Android Studio / IntelliJ :
#   "Write Permissions Issue ... restricted write permissions"
# alors que attrib/icacls cote utilisateur semblent corrects (sonde OK).
#
# Fait sous Windows Defender :
#   1. Autorise studio64.exe si "Accès aux dossiers contrôlés" est activé
#   2. Ajoute aux exclusions antivirus le dossier du projet + caches usuels
#
# Usage (PowerShell EXECUTE EN ADMINISTRATEUR) :
#   powershell -ExecutionPolicy Bypass -File .\fix-windows-studio-defender-admin.ps1
# Avec ton chemin projet :
#   powershell ... -File .\fix-windows-studio-defender-admin.ps1 '-ProjectPath' 'C:\chemin\vers\Widget G7'


param(
    [Parameter(Mandatory = $false)]
    [string] $ProjectPath = ""
)

function Assert-Administrator {
    $isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator)
    if (-not $isAdmin) {
        Write-Host "REQUIS : executer ce script depuis une console PowerShell ouverte en Administrateur (ou via run-fix-defender-elevated.ps1)." -ForegroundColor Red
        exit 10
    }
}

Assert-Administrator

$ErrorActionPreference = "Continue"

function Get-MpSafe {
    param([scriptblock]$IfOk)
    try {
        $mp = Get-MpPreference -ErrorAction Stop
        & $IfOk $mp
    }
    catch {
        Write-Host "Defender MpPreference inaccessible (autre antivirus, desactive, ou stratégie groupe) : $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

function Find-JbrJava {
    $studioRoots = @(
        "C:\Program Files\Android\Android Studio",
        "${env:ProgramFiles(x86)}\Android\Android Studio",
        (Join-Path $env:LOCALAPPDATA "Programs\Android Studio")
    )
    foreach ($root in $studioRoots) {
        $jav = Join-Path $root 'jbr\bin\java.exe'
        if (Test-Path -LiteralPath $jav) { return $jav }
    }
    return $null
}

function Add-ToCfaAppList([string[]]$ExePaths, [ref]$ListRef) {
    foreach ($e in $ExePaths) {
        if (-not $e -or -not (Test-Path -LiteralPath $e)) { continue }
        if ($ListRef.Value -contains $e) { continue }
        $ListRef.Value += $e
        Write-Host "CFA liste + $e"
    }
}

function Find-Studio64 {
    foreach ($c in @(
            "C:\Program Files\Android\Android Studio\bin\studio64.exe",
            "${env:ProgramFiles(x86)}\Android\Android Studio\bin\studio64.exe",
            (Join-Path $env:LOCALAPPDATA "Programs\Android Studio\bin\studio64.exe")
        )) {
        if (Test-Path -LiteralPath $c) { return $c }
    }
    return $null
}

function Add-ExclusionPathIfMissing([string]$p) {
    if (-not (Test-Path -LiteralPath $p)) {
        Write-Host "(ignore exclusions : introuvable) $p"
        return
    }
    Get-MpSafe {
        param($mp)
        $existing = @($mp.ExclusionPath | Where-Object { $_ })
        if ($existing -contains $p) {
            Write-Host "(deja exclus) $p"
            return
        }
        try {
            Add-MpExclusion -Path $p -ErrorAction Stop | Out-Null
            Write-Host "EXCLUS antivirus : $p"
        }
        catch {
            Write-Host "Add-MpExclusion impossible (protection anti-intrusion Tamper?) : $($_.Exception.Message)" -ForegroundColor Yellow
        }
    }
}
Write-Host "--- Android Studio / Defender (admin) ---"

if (-not $ProjectPath) {
    Write-Host ""
    Write-Host "Collez le chemin ABSOLU vers le dossier Widget G7 (chaîne sous guillemets si espaces)." -ForegroundColor Yellow
    Write-Host 'Exemple : C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7' -ForegroundColor Yellow
    $ProjectPath = Read-Host "ProjectPath"
}

if (-not (Test-Path -LiteralPath $ProjectPath)) {
    Write-Host "Chemin invalide ou introuvable : $ProjectPath"
    exit 1
}
$ProjectPath = (Resolve-Path -LiteralPath $ProjectPath).Path

# 1) Accès dossiers contrôlés — studio64 ET java.exe du JBR (Gradle / daemon écrit souvent sous java.exe).
$studio = Find-Studio64
$jav = Find-JbrJava
$list = @()
try {
    $list = @((Get-MpPreference -ErrorAction Stop).ControlledFolderAccessAllowedApplications | Where-Object { $_ })
}
catch {
    Write-Host "Impossible de lire la liste CFA Defender : $($_.Exception.Message)"
}
$listRef = [ref]$list
if ($studio) { Add-ToCfaAppList @($studio) $listRef }
if ($jav) { Add-ToCfaAppList @($jav) $listRef }


if ($studio -or $jav) {
    try {
        Add-MpPreference -ControlledFolderAccessAllowedApplications $listRef.Value -ErrorAction Stop
        Write-Host "CFA : liste applications mise a jour ($($listRef.Value.Count) entrees)"
    }
    catch {
        Write-Host "CFA Add-MpPreference echouee: $($_.Exception.Message)"
    }
}

# Exclusions PROCESSUS temps reel — java.exe inclus (Gradle utilise souvent java.exe hors studio64.exe)
try {
    $procList = @((Get-MpPreference).ExclusionProcess | Where-Object { $_ })
    foreach ($proc in @('studio64.exe', 'java.exe')) {
        if ($procList -notcontains $proc) {
            $procList += $proc
            Write-Host "EXCLUS processus propose : $proc"
        }
    }
    Add-MpPreference -ExclusionProcess $procList | Out-Null
    Write-Host "EXCLUS processus mise a jour (Defender)."
}
catch {
    Write-Host "ExclusionProcess non disponible ou ignoree : $($_.Exception.Message)"
}

# 2) Exclusions analyse temps réel (JetBrains doc + caches locaux)
Add-ExclusionPathIfMissing $ProjectPath
Add-ExclusionPathIfMissing (Join-Path $env:USERPROFILE ".gradle")
Add-ExclusionPathIfMissing (Join-Path $env:USERPROFILE ".android")
if ($env:TEMP) { Add-ExclusionPathIfMissing $env:TEMP }
$gApp = Join-Path $env:APPDATA "Google"
if (Test-Path $gApp) { Add-ExclusionPathIfMissing $gApp }
$gLocal = Join-Path $env:LOCALAPPDATA "Google"
if (Test-Path $gLocal) { Add-ExclusionPathIfMissing $gLocal }

Write-Host ""
Write-Host "TERMINE : ferme puis rouvre Android Studio et re-ouvre le projet."
exit 0
