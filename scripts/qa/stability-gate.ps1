# Enchaîne les verifications de stabilite avant merge ou tag.
#
# Usage:
#   .\scripts\qa\stability-gate.ps1              # CI + smoke + logcat FATAL
#   .\scripts\qa\stability-gate.ps1 -SkipCi       # smoke + logcat seulement
#   .\scripts\qa\stability-gate.ps1 -CheckLogcatOnly
#
param(
    [switch]$SkipCi,
    [switch]$CheckLogcatOnly,
    [switch]$Strict
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

$fail = 0
$warn = 0

function Read-LocalProperty {
    param([string]$Key)
    $localProps = Join-Path $Root "local.properties"
    if (-not (Test-Path $localProps)) { return $null }
    foreach ($line in Get-Content $localProps) {
        if ($line -match "^$([regex]::Escape($Key))=(.+)$") {
            return ($matches[1].Trim() -replace '\\(.)', '$1')
        }
    }
    return $null
}

function Step-Ok { param([string]$Msg) Write-Host "[OK] $Msg" -ForegroundColor Green }
function Step-Warn { param([string]$Msg) Write-Host "[WARN] $Msg" -ForegroundColor Yellow; $script:warn++ }
function Step-Fail { param([string]$Msg) Write-Host "[FAIL] $Msg" -ForegroundColor Red; $script:fail++ }

Write-Host "`n=== Glucose For Watch stability gate ===" -ForegroundColor Cyan

if (-not $CheckLogcatOnly -and -not $SkipCi) {
    Write-Host "`n-- verify_ci.sh --" -ForegroundColor DarkCyan
    if (Get-Command bash -ErrorAction SilentlyContinue) {
        try {
            bash ./scripts/dev/verify_ci.sh 2>&1 | Out-Host
            Step-Ok "verify_ci.sh"
        } catch {
            Step-Fail "verify_ci.sh : $_"
        }
    } else {
        Write-Host "`n-- gradlew test (fallback, no bash) --" -ForegroundColor DarkCyan
        try {
            .\gradlew.bat :core:model:testDebugUnitTest :feature:sync:testDebugUnitTest :mobile:testDebugUnitTest :wear:testDebugUnitTest 2>&1 | Out-Host
            Step-Ok "gradlew test modules critiques"
        } catch {
            Step-Fail "gradlew test : $_"
        }
    }
}

$sdkDir = Read-LocalProperty "sdk.dir"
if (-not $sdkDir) { $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdkDir "platform-tools\adb.exe"
$phone = Read-LocalProperty "widgetg7.adb.phone.serial"

if (-not $CheckLogcatOnly) {
    if (-not (Test-Path $adb)) {
        Step-Warn "adb introuvable - hardware smoke ignore"
    } elseif (-not $phone) {
        Step-Warn "widgetg7.adb.phone.serial absent - hardware smoke ignore"
    } else {
        Write-Host "`n-- hardware-smoke.ps1 --" -ForegroundColor DarkCyan
        try {
            & "$PSScriptRoot\hardware-smoke.ps1" 2>&1 | Out-Host
            Step-Ok "hardware-smoke.ps1"
        } catch {
            if ($Strict) { Step-Fail "hardware-smoke.ps1 : $_" }
            else { Step-Warn "hardware-smoke.ps1 : $_" }
        }
    }
}

if (Test-Path $adb) {
    if (-not $phone) {
        $phone = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] } | Select-Object -First 1)
    }
    if ($phone) {
        Write-Host "`n-- logcat FATAL (com.widgetg7.mobile) --" -ForegroundColor DarkCyan
        $fatals = & $adb -s $phone logcat -d -v time 2>$null |
            Select-String -Pattern "FATAL EXCEPTION.*com\.widgetg7\.mobile|Process: com\.widgetg7\.mobile.*FATAL" |
            Select-Object -Last 5
        if ($fatals) {
            Step-Fail "FATAL logcat detecte ($($fatals.Count) recent(s))"
            $fatals | ForEach-Object { Write-Host "  $_" -ForegroundColor DarkRed }
        } else {
            Step-Ok "aucun FATAL recent com.widgetg7.mobile"
        }
    } else {
        Step-Warn "pas de phone adb pour scan logcat"
    }
} else {
    Step-Warn "adb absent pour scan logcat"
}

Write-Host "`n=== Resultat : FAIL=$fail WARN=$warn ===" -ForegroundColor $(if ($fail -gt 0) { "Red" } elseif ($warn -gt 0) { "Yellow" } else { "Green" })
Write-Host "Gates : docs/plan/STABILITY-GATES.md`n"

if ($fail -gt 0) { exit 1 }
exit 0
