param()

$ErrorActionPreference = "Stop"

# Cursor hook payload can arrive on stdin; consume if present.
try {
    [void]($input | Out-String)
} catch {
}

$repoRoot = (Get-Location).Path
$stateDir = Join-Path $repoRoot ".cursor/state"
$pidFile = Join-Path $stateDir "usb-monitor.pid"
$loopScript = Join-Path $repoRoot ".cursor/hooks/usb_monitor_loop.ps1"
$reportDir = Join-Path $stateDir "reports"
$reportPath = Join-Path $reportDir "usb-monitor-start.md"

New-Item -ItemType Directory -Path $stateDir -Force | Out-Null
New-Item -ItemType Directory -Path $reportDir -Force | Out-Null

if (-not (Test-Path $loopScript)) {
    Set-Content -Path $reportPath -Encoding UTF8 -Value @(
        "# USB monitor start"
        ""
        "- Status: failed"
        "- Reason: missing .cursor/hooks/usb_monitor_loop.ps1"
    )
    exit 0
}

$alreadyRunning = $false
if (Test-Path $pidFile) {
    try {
        $pidValue = Get-Content -Path $pidFile -Raw
        $existingPid = [int]$pidValue
        $proc = Get-Process -Id $existingPid -ErrorAction SilentlyContinue
        if ($proc) {
            $alreadyRunning = $true
        }
    } catch {
        $alreadyRunning = $false
    }
}

if ($alreadyRunning) {
    Set-Content -Path $reportPath -Encoding UTF8 -Value @(
        "# USB monitor start"
        ""
        "- Status: already-running"
        "- PID file: .cursor/state/usb-monitor.pid"
    )
    exit 0
}

$process = Start-Process -FilePath "powershell" -ArgumentList @(
    "-NoProfile",
    "-ExecutionPolicy",
    "Bypass",
    "-File",
    $loopScript
) -WindowStyle Hidden -PassThru

Set-Content -Path $pidFile -Value $process.Id -Encoding UTF8

Set-Content -Path $reportPath -Encoding UTF8 -Value @(
    "# USB monitor start"
    ""
    "- Status: started"
    "- PID: $($process.Id)"
    "- Interval: 300s"
)

exit 0
