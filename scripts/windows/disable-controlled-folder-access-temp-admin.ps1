#Requires -RunAsAdministrator
# Desactive TEMPORAIREMENT tout l accès aux dossiers controles Windows Defender (CFA).
# A utiliser uniquement pour confirmer que le blocage Studio vient de CFA.
# IMPORTANT : reactive la protection ensuite dans Sécurité Windows.
#
#   Set-MpPreference -ControlledFolderAccessEnabled $false
#
# Reactive le CFA :
#   Set-MpPreference -ControlledFolderAccessEnabled $true

$ErrorActionPreference = 'Stop'

try {
    $on = Get-MpPreference | Select-Object -ExpandProperty ControlledFolderAccessEnabled -ErrorAction SilentlyContinue
    Write-Host "ControlledFolderAccessEnabled avant : [$on]"
    Set-MpPreference -ControlledFolderAccessEnabled $false
    Write-Host "CFA DESACTIVE pour ce PC. Lance Android Studio puis teste louverture projet."
}
catch {
    Write-Host "Echec (version Windows ou autre antivirus) : $($_.Exception.Message)"
    exit 1
}
exit 0
