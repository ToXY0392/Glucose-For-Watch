# Capture ecran reel du telephone et ouvre dans Cursor (onglet editeur ou Simple Browser).
# Usage:
#   .\scripts\qa\open-phone-in-cursor.ps1
#   .\scripts\qa\open-phone-in-cursor.ps1 -Live
#   .\scripts\qa\open-phone-in-cursor.ps1 -Launch

param(
    [switch]$Live,
    [switch]$Launch
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
Set-Location $Root

$PackageId = "com.widgetg7.mobile"
$CapturesDir = Join-Path $Root "docs\qa\captures"
$PngPath = Join-Path $CapturesDir "live-phone.png"
$HtmlPath = Join-Path $CapturesDir "live-phone-preview.html"
$LivePort = 8765

function Read-LocalProperty {
    param([string]$Key)
    $localProps = Join-Path $Root "local.properties"
    if (-not (Test-Path $localProps)) { return $null }
    foreach ($line in Get-Content $localProps) {
        if ($line -match "^$([regex]::Escape($Key))=(.+)$") {
            return $matches[1].Trim()
        }
    }
    return $null
}

function Resolve-Adb {
    $sdkDir = Read-LocalProperty "sdk.dir"
    if ($sdkDir) { $sdkDir = $sdkDir -replace '\\(.)', '$1' }
    if (-not $sdkDir -or -not (Test-Path $sdkDir)) {
        $sdkDir = Join-Path $env:LOCALAPPDATA "Android\Sdk"
    }
    $adb = Join-Path $sdkDir "platform-tools\adb.exe"
    if (-not (Test-Path $adb)) {
        Write-Error "adb introuvable: $adb"
    }
    return $adb
}

function Resolve-PhoneSerial {
    param([string]$Adb)
    $prop = Read-LocalProperty "widgetg7.adb.phone.serial"
    if (-not $prop) { $prop = $env:WIDGETG7_PHONE_SERIAL }
    $online = @(
        & $Adb devices 2>$null |
            Select-String "\sdevice$" |
            ForEach-Object { ($_ -split "\s+", 2)[0].Trim() } |
            Where-Object { $_ -and $_ -notmatch "^emulator-" }
    )
    if ($prop -and $online -contains $prop) { return $prop }
    if ($online.Count -ge 1) { return $online[0] }
    return $null
}

function Capture-PhoneScreen {
    param(
        [string]$Adb,
        [string]$Serial,
        [string]$OutPath
    )
    New-Item -ItemType Directory -Force -Path (Split-Path $OutPath -Parent) | Out-Null
    $tmp = [System.IO.Path]::GetTempFileName()
    try {
        $proc = Start-Process `
            -FilePath $Adb `
            -ArgumentList @("-s", $Serial, "exec-out", "screencap", "-p") `
            -RedirectStandardOutput $tmp `
            -NoNewWindow `
            -Wait `
            -PassThru
        if ($proc.ExitCode -ne 0 -or -not (Test-Path $tmp) -or (Get-Item $tmp).Length -lt 1000) {
            Write-Error "Capture ADB vide ou trop petite ($Serial)"
        }
        Move-Item -Force $tmp $OutPath
    } finally {
        if (Test-Path $tmp) { Remove-Item -Force $tmp -ErrorAction SilentlyContinue }
    }
}

$adb = Resolve-Adb
$serial = Resolve-PhoneSerial -Adb $adb
if (-not $serial) {
    Write-Error "Aucun telephone ADB. Branchez le Pixel et activez le debogage USB."
}

Write-Host ""
Write-Host "=== Glucose For Watch - miroir phone -> Cursor ===" -ForegroundColor Cyan
Write-Host "  Serial: $serial"

if ($Launch) {
    & $adb -s $serial shell am force-stop $PackageId | Out-Null
    Start-Sleep -Milliseconds 400
    & $adb -s $serial shell am start -n "$PackageId/.SplashActivity" | Out-Null
    Start-Sleep -Seconds 1
}

Capture-PhoneScreen -Adb $adb -Serial $serial -OutPath $PngPath
Write-Host "  [OK] Capture: $PngPath" -ForegroundColor Green

$cursor = Get-Command cursor -ErrorAction SilentlyContinue
if ($cursor) {
    & cursor -r $PngPath
    if (-not $Live) {
        & cursor -r $HtmlPath
    }
} else {
    Write-Host "  [WARN] CLI cursor absente - ouvrez manuellement $PngPath" -ForegroundColor Yellow
}

if ($Live) {
    Get-Job -Name "wg7-live-phone*" -ErrorAction SilentlyContinue | ForEach-Object {
        Stop-Job $_ -Force | Out-Null
        Remove-Job $_ -Force | Out-Null
    }

    Start-Job -Name "wg7-live-phone-server" -ScriptBlock {
        param($Dir, $Port)
        Set-Location $Dir
        py -3 -m http.server $Port 2>$null
    } -ArgumentList $CapturesDir, $LivePort | Out-Null

    Start-Job -Name "wg7-live-phone" -ScriptBlock {
        param($AdbPath, $PhoneSerial, $Out)
        while ($true) {
            $tmp = [System.IO.Path]::GetTempFileName()
            try {
                $proc = Start-Process `
                    -FilePath $AdbPath `
                    -ArgumentList @("-s", $PhoneSerial, "exec-out", "screencap", "-p") `
                    -RedirectStandardOutput $tmp `
                    -NoNewWindow `
                    -Wait `
                    -PassThru
                if ($proc.ExitCode -eq 0 -and (Test-Path $tmp) -and (Get-Item $tmp).Length -gt 1000) {
                    Move-Item -Force $tmp $Out
                }
            } catch {
                # ignore transient adb errors
            } finally {
                if (Test-Path $tmp) { Remove-Item -Force $tmp -ErrorAction SilentlyContinue }
            }
            Start-Sleep -Seconds 2
        }
    } -ArgumentList $adb, $serial, $PngPath | Out-Null

    $url = "http://127.0.0.1:$LivePort/live-phone-preview.html"
    Write-Host ""
    Write-Host "  Live: $url" -ForegroundColor Cyan
    Write-Host "  Cursor: Ctrl+Shift+P > Simple Browser: Show > coller URL ci-dessus" -ForegroundColor Cyan
    Write-Host "  Stop: Get-Job wg7-live-phone* | Stop-Job; Get-Job wg7-live-phone* | Remove-Job -Force" -ForegroundColor DarkGray
    Write-Host ""
    Start-Process $url
} else {
    Write-Host ""
    Write-Host "  Astuce: ajoutez -Live pour miroir auto (Simple Browser)" -ForegroundColor DarkGray
    Write-Host ""
}
