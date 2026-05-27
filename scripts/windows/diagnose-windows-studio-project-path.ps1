# Diagnostic SANS elevation : pour comprendre le dialogue
# « This project folder has restricted write permissions » dans Android Studio.
#
# Usage (PowerShell normal) :
#   .\diagnose-windows-studio-project-path.ps1 -ProjectPath "C:\Users\...\Projects\Glucose-For-Watch"

param(
    [Parameter(Mandatory = $true)]
    [string] $ProjectPath
)

if (-not (Test-Path -LiteralPath $ProjectPath)) {
    Write-Host "ERREUR: chemin introuvable: $ProjectPath"
    exit 1
}
$ProjectPath = (Resolve-Path -LiteralPath $ProjectPath).Path

Write-Host "=== Diagnostics pour: $ProjectPath ===" -ForegroundColor Cyan
$who = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
Write-Host "Utilisateur courant: $who"
$isAdmin = ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator)
Write-Host "Shell admin: $isAdmin"

# OneDrive / sync
if ($ProjectPath -match 'OneDrive|SkyDrive') {
    Write-Host "ATTENTION: Le chemin semble sous OneDrive - deplacer le depot vers C:\Dev\... est souvent la seule solution stable." -ForegroundColor Yellow
}

# Espace dans le nom du dossier parent
$leaf = Split-Path $ProjectPath -Leaf
if ($leaf -match '\s') {
    Write-Host "ATTENTION: Le dossier projet contient des espaces ('$leaf'). Utiliser Glucose-For-Watch (sans espaces) elimine des bugs rares d outils." -ForegroundColor Yellow
}

# Test ecriture .NET (proche de ce que la JVM peut voir)
$probe = Join-Path $ProjectPath "_studio_probe_$([guid]::NewGuid().ToString('N')).tmp"
try {
    [System.IO.File]::WriteAllText($probe, 'ok', [System.Text.UTF8Encoding]::new($false))
    [System.IO.File]::Delete($probe)
    Write-Host "Ecriture .NET sur la racine projet: OK" -ForegroundColor Green
} catch {
    Write-Host "Ecriture .NET sur la racine projet: ECHEC -> $($_.Exception.Message)" -ForegroundColor Red
}

# Sous-dossier .idea (Studio ecrit souvent la en premier)
$idea = Join-Path $ProjectPath ".idea"
try {
    if (-not (Test-Path -LiteralPath $idea)) {
        New-Item -ItemType Directory -Path $idea -Force | Out-Null
    }
    $p2 = Join-Path $idea "_probe.tmp"
    [System.IO.File]::WriteAllText($p2, 'ok', [System.Text.UTF8Encoding]::new($false))
    [System.IO.File]::Delete($p2)
    Write-Host "Ecriture .NET dans .idea: OK" -ForegroundColor Green
} catch {
    Write-Host "Ecriture .NET dans .idea: ECHEC -> $($_.Exception.Message)" -ForegroundColor Red
}

# ACL : entrees Deny explicites pour cet utilisateur / Everyone / Users
Write-Host "`n--- ACL (entrees Deny pertinentes) ---" -ForegroundColor Cyan
$acl = Get-Acl -LiteralPath $ProjectPath
foreach ($a in $acl.Access) {
    if ($a.AccessControlType -ne [System.Security.AccessControl.AccessControlType]::Deny) { continue }
    $id = $a.IdentityReference.Value
    if ($id -match 'Everyone|Users|Authenticated' -or $id -eq $who) {
        $flags = '(' + [string]$a.InheritanceFlags + ', ' + [string]$a.PropagationFlags + ')'
        Write-Host "DENY $id -> $($a.FileSystemRights) $flags"
    }
}

# icacls resume (lignes contenant Deny)
Write-Host "`n--- icacls (lignes Deny) ---" -ForegroundColor Cyan
& icacls.exe $ProjectPath 2>$null | Select-String -Pattern 'Deny|\(DENY\)' -CaseSensitive:$false

# Defender CFA etat
Write-Host "`n--- Windows Defender (lecture seule) ---" -ForegroundColor Cyan
try {
    $mp = Get-MpPreference -ErrorAction Stop
    Write-Host "ControlledFolderAccessEnabled (si present): $($mp.ControlledFolderAccessEnabled)"
    $apps = @($mp.ControlledFolderAccessAllowedApplications | Where-Object { $_ })
    $studio = $apps | Where-Object { $_ -match 'studio64' }
    if ($studio) { Write-Host "studio64 dans CFA allow list: $studio" -ForegroundColor Green }
    else { Write-Host "studio64 PAS dans la liste CFA (ou liste vide). Executer fix-windows-studio-defender-admin.ps1 en admin." -ForegroundColor Yellow }
} catch {
    Write-Host "Get-MpPreference indisponible (autre antivirus / strategie): $($_.Exception.Message)"
}

Write-Host ""
Write-Host "=== Fin ===" -ForegroundColor Cyan
Write-Host 'Conseils : Studio en ADMIN (test), desactiver Acces dossiers controles temporairement, ouvrir depuis \\wsl$\... ou C:\Dev sans espace.'
