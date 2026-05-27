# Bloc C.1 - AGP visual capture (phone hero + watch tile/complication)
#
# Manual: wait for Dexcom readings near 60 / 120 / 200 mg/dL,
# then run this script at each plateau to screencap evidence.
#
# Usage:
#   .\scripts\qa\capture-c1-agp-session.ps1
#   .\scripts\qa\capture-c1-agp-session.ps1 -ValueMgDl 120
#   .\scripts\qa\capture-c1-agp-session.ps1 -All

param(
    [ValidateSet(60, 120, 200)]
    [int]$ValueMgDl = 0,
    [switch]$All,
    [switch]$PhoneOnly,
    [switch]$NonInteractive,
    [switch]$LaunchApp
)

$PackageId = "com.glucoseforwatch.mobile"

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

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

$sdkDir = Read-LocalProperty "sdk.dir"
if (-not $sdkDir) { $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk" }
$adb = Join-Path $sdkDir "platform-tools\adb.exe"
if (-not (Test-Path $adb)) {
    Write-Error "adb not found: $adb - set sdk.dir in local.properties"
}

$phone = Read-LocalProperty "gfw.adb.phone.serial"
$watch = Read-LocalProperty "gfw.adb.watch.serial"
if (-not $phone) {
    $phone = @(& $adb devices | Select-String "\sdevice$" | ForEach-Object { ($_ -split "\s+")[0] } | Select-Object -First 1)
}
if (-not $phone) { Write-Error "No phone on adb. Connect Pixel 8a or set gfw.adb.phone.serial" }

$captureDir = Join-Path $Root "docs\qa\captures"
$sessionDir = Join-Path $Root "docs\qa\sessions"
New-Item -ItemType Directory -Force -Path $captureDir, $sessionDir | Out-Null

$stamp = Get-Date -Format "yyyy-MM-dd"
$agpExpect = @{
    60  = 'low L1 red, 54-69 mg/dL band'
    120 = 'in range green, 70-180 mg/dL'
    200 = 'high L1 amber, 181-250 mg/dL'
}

function Capture-Device {
    param(
        [string]$Serial,
        [string]$Label,
        [int]$MgDl
    )
    $out = Join-Path $captureDir "${stamp}_C1_${Label}_${MgDl}mgdl.png"
    Write-Host "  Capturing $Label ($Serial) -> $out" -ForegroundColor Cyan
    $tmp = [System.IO.Path]::GetTempFileName()
    try {
        $proc = Start-Process `
            -FilePath $adb `
            -ArgumentList @("-s", $Serial, "exec-out", "screencap", "-p") `
            -RedirectStandardOutput $tmp `
            -NoNewWindow `
            -Wait `
            -PassThru
        if ($proc.ExitCode -ne 0 -or -not (Test-Path $tmp) -or (Get-Item $tmp).Length -lt 1000) {
            Write-Warning "Capture may have failed for $Label - check device screen is on"
            return $null
        }
        Move-Item -Force $tmp $out
    } finally {
        if (Test-Path $tmp) { Remove-Item -Force $tmp -ErrorAction SilentlyContinue }
    }
    return $out
}

function Invoke-C1Capture {
    param([int]$MgDl)

    $expected = $agpExpect[$MgDl]
    Write-Host "`n=== C.1 AGP $MgDl mg/dL ($expected) ===" -ForegroundColor Cyan
    if ($LaunchApp) {
        Write-Host "Launching app on phone..."
        & $adb -s $phone shell am start -n "$PackageId/.SplashActivity" | Out-Null
        Start-Sleep -Seconds 2
    }
    Write-Host "Confirm hero + tile show reading near $MgDl before capture..."
    if (-not $NonInteractive) { Read-Host "Ready" } else { Start-Sleep -Seconds 2 }

    $files = @()
    $files += Capture-Device -Serial $phone -Label "phone" -MgDl $MgDl

    if (-not $PhoneOnly -and $watch) {
        $prevErr = $ErrorActionPreference
        $ErrorActionPreference = "SilentlyContinue"
        $watchOnline = & $adb -s $watch get-state 2>$null
        $ErrorActionPreference = $prevErr
        if ($watchOnline -eq "device") {
            $files += Capture-Device -Serial $watch -Label "watch_tile" -MgDl $MgDl
        } else {
            Write-Warning "Watch serial configured but not online: $watch"
        }
    } elseif (-not $PhoneOnly) {
        Write-Host "[INFO] No gfw.adb.watch.serial - phone capture only" -ForegroundColor Yellow
    }

    return $files
}

$targets = if ($All) { @(60, 120, 200) }
           elseif ($ValueMgDl -gt 0) { @($ValueMgDl) }
           else { @() }

if ($targets.Count -eq 0) {
    Write-Host @"

Bloc C.1 - AGP visual (STABILITY-GATES G-C)

Expected AGP bands (see toxy-ux-kit/spec/01-agp-medical-layer.md):
  60  mg/dL -> low (red)
  120 mg/dL -> in range (green)
  200 mg/dL -> high (amber)

Phone: $phone
Watch: $(if ($watch) { $watch } else { '(not configured)' })

Run with:
  .\scripts\qa\capture-c1-agp-session.ps1 -ValueMgDl 120
  .\scripts\qa\capture-c1-agp-session.ps1 -All

After captures, note PASS/FAIL in docs/qa/sessions/*_C1-agp.md

"@ -ForegroundColor DarkGray
    exit 0
}

$captured = @()
foreach ($v in $targets) {
    $captured += Invoke-C1Capture -MgDl $v
}

$reportPath = Join-Path $sessionDir "${stamp}_C1-agp-captures.md"
$lines = @(
    "# C.1 AGP captures - $stamp",
    "",
    "| mg/dL | AGP band | Phone | Watch | Visual OK |",
    "|-------|----------|-------|-------|-----------|"
)
foreach ($v in $targets) {
    $phoneFile = Join-Path $captureDir "${stamp}_C1_phone_${v}mgdl.png"
    $watchFile = Join-Path $captureDir "${stamp}_C1_watch_tile_${v}mgdl.png"
    $pRel = if (Test-Path $phoneFile) { "captures/$(Split-Path $phoneFile -Leaf)" } else { "-" }
    $wRel = if (Test-Path $watchFile) { "captures/$(Split-Path $watchFile -Leaf)" } else { "-" }
    $lines += "| $v | $($agpExpect[$v]) | $pRel | $wRel | pending |"
}
$lines += @("", "## Operator notes", "", "_Mark visual OK after reviewing colors on phone hero + watch tile._", "")
$lines | Set-Content -Path $reportPath -Encoding utf8

Write-Host "`n[OK] Session stub: $reportPath" -ForegroundColor Green
Write-Host "Review captures in docs/qa/captures/ and mark visual OK in session file." -ForegroundColor DarkGray
