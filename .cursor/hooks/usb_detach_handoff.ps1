param()

$ErrorActionPreference = "Stop"

# Cursor hook payload can arrive on stdin; consume if present.
try {
    [void]($input | Out-String)
} catch {
}

function Get-AdbDeviceState {
    param(
        [string]$Serial
    )

    if ([string]::IsNullOrWhiteSpace($Serial)) {
        return "unknown"
    }

    try {
        $output = & adb devices 2>$null
    } catch {
        return "adb_unavailable"
    }

    foreach ($line in $output) {
        if ($line -match "^\Q$Serial\E\s+(\S+)$") {
            return $matches[1]
        }
    }
    return "detached"
}

function Load-JsonFile {
    param([string]$Path)
    if (-not (Test-Path $Path)) { return $null }
    try {
        return (Get-Content -Path $Path -Raw | ConvertFrom-Json)
    } catch {
        return $null
    }
}

$repoRoot = (Get-Location).Path
$stateDir = Join-Path $repoRoot ".cursor/state"
$usbStateFile = Join-Path $stateDir "usb-state.json"
$handoffPath = Join-Path $stateDir "developer-handoff.md"
$reportDir = Join-Path $stateDir "reports"
$reportPath = Join-Path $reportDir "usb-detach-check.md"

New-Item -ItemType Directory -Path $stateDir -Force | Out-Null
New-Item -ItemType Directory -Path $reportDir -Force | Out-Null

$phoneSerial = $env:WIDGETG7_PHONE_SERIAL
$watchSerial = $env:WIDGETG7_WATCH_SERIAL
$now = Get-Date
$nowUtc = $now.ToUniversalTime().ToString("o")

if ([string]::IsNullOrWhiteSpace($phoneSerial) -or [string]::IsNullOrWhiteSpace($watchSerial)) {
    Set-Content -Path $reportPath -Encoding UTF8 -Value @(
        "# USB detach check"
        ""
        "- Status: skipped (serials not configured)"
        "- Required: WIDGETG7_PHONE_SERIAL and WIDGETG7_WATCH_SERIAL"
    )
    exit 0
}

$oldState = Load-JsonFile -Path $usbStateFile
$oldPhone = if ($oldState -and $oldState.phoneState) { [string]$oldState.phoneState } else { "unknown" }
$oldWatch = if ($oldState -and $oldState.watchState) { [string]$oldState.watchState } else { "unknown" }

$newPhone = Get-AdbDeviceState -Serial $phoneSerial
$newWatch = Get-AdbDeviceState -Serial $watchSerial

$phoneDetached = ($oldPhone -eq "device" -and $newPhone -ne "device")
$watchDetached = ($oldWatch -eq "device" -and $newWatch -ne "device")

$dedupeMinutes = 30
$lastPhoneDetachUtc = if ($oldState -and $oldState.lastPhoneDetachUtc) { [DateTime]::Parse([string]$oldState.lastPhoneDetachUtc).ToUniversalTime() } else { $null }
$lastWatchDetachUtc = if ($oldState -and $oldState.lastWatchDetachUtc) { [DateTime]::Parse([string]$oldState.lastWatchDetachUtc).ToUniversalTime() } else { $null }

$createPhoneIncident = $false
$createWatchIncident = $false

if ($phoneDetached) {
    if (-not $lastPhoneDetachUtc -or (($now.ToUniversalTime() - $lastPhoneDetachUtc).TotalMinutes -ge $dedupeMinutes)) {
        $createPhoneIncident = $true
    }
}
if ($watchDetached) {
    if (-not $lastWatchDetachUtc -or (($now.ToUniversalTime() - $lastWatchDetachUtc).TotalMinutes -ge $dedupeMinutes)) {
        $createWatchIncident = $true
    }
}

$incidentsToAdd = @()
if ($createPhoneIncident) {
    $incidentsToAdd += "| $($now.ToString('yyyy-MM-dd')) | USB detach detected (phone) | Open (reconnect phone + verify sync) |"
}
if ($createWatchIncident) {
    $incidentsToAdd += "| $($now.ToString('yyyy-MM-dd')) | USB detach detected (watch) | Open (reconnect watch + verify sync) |"
}

$handoffUpdated = $false
if ((Test-Path $handoffPath) -and $incidentsToAdd.Count -gt 0) {
    $lines = Get-Content -Path $handoffPath
    $headerIndex = -1
    $separatorIndex = -1
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -eq "## Recent incidents") {
            $headerIndex = $i
        }
        if ($headerIndex -ge 0 -and $lines[$i] -eq "| --- | --- | --- |") {
            $separatorIndex = $i
            break
        }
    }

    if ($separatorIndex -ge 0) {
        $newLines = New-Object System.Collections.Generic.List[string]
        for ($i = 0; $i -le $separatorIndex; $i++) {
            [void]$newLines.Add($lines[$i])
        }
        foreach ($incident in $incidentsToAdd) {
            [void]$newLines.Add($incident)
        }
        for ($i = $separatorIndex + 1; $i -lt $lines.Count; $i++) {
            [void]$newLines.Add($lines[$i])
        }
        Set-Content -Path $handoffPath -Value $newLines -Encoding UTF8
        $handoffUpdated = $true
    }
}

$newState = @{
    lastSeenUtc = $nowUtc
    phoneSerial = $phoneSerial
    watchSerial = $watchSerial
    phoneState = $newPhone
    watchState = $newWatch
}
if ($createPhoneIncident) {
    $newState.lastPhoneDetachUtc = $nowUtc
} elseif ($lastPhoneDetachUtc) {
    $newState.lastPhoneDetachUtc = $lastPhoneDetachUtc.ToString("o")
}
if ($createWatchIncident) {
    $newState.lastWatchDetachUtc = $nowUtc
} elseif ($lastWatchDetachUtc) {
    $newState.lastWatchDetachUtc = $lastWatchDetachUtc.ToString("o")
}

$newState | ConvertTo-Json | Set-Content -Path $usbStateFile -Encoding UTF8

$reportLines = @(
    "# USB detach check"
    ""
    "- Status: completed"
    "- Phone: $phoneSerial -> $newPhone (previous: $oldPhone)"
    "- Watch: $watchSerial -> $newWatch (previous: $oldWatch)"
    "- Incidents created: $($incidentsToAdd.Count)"
    "- Handoff updated: $handoffUpdated"
)
Set-Content -Path $reportPath -Value ($reportLines -join "`n") -Encoding UTF8

exit 0
