param()

$ErrorActionPreference = "Stop"

$repoRoot = (Get-Location).Path
$stateDir = Join-Path $repoRoot ".cursor/state"
$pidFile = Join-Path $stateDir "usb-monitor.pid"
$logDir = Join-Path $stateDir "reports"
$logFile = Join-Path $logDir "usb-monitor-loop.log"
$detachScript = Join-Path $repoRoot ".cursor/hooks/usb_detach_handoff.ps1"

New-Item -ItemType Directory -Path $stateDir -Force | Out-Null
New-Item -ItemType Directory -Path $logDir -Force | Out-Null

$selfPid = $PID
Set-Content -Path $pidFile -Value $selfPid -Encoding UTF8

try {
    while ($true) {
        $timestamp = (Get-Date).ToUniversalTime().ToString("u")
        try {
            if (Test-Path $detachScript) {
                & powershell -NoProfile -ExecutionPolicy Bypass -File $detachScript | Out-Null
                Add-Content -Path $logFile -Value "[$timestamp] usb_detach_handoff.ps1 executed"
            } else {
                Add-Content -Path $logFile -Value "[$timestamp] missing usb_detach_handoff.ps1"
            }
        } catch {
            Add-Content -Path $logFile -Value "[$timestamp] monitor error: $($_.Exception.Message)"
        }

        Start-Sleep -Seconds 300
    }
} finally {
    $currentPidInFile = $null
    if (Test-Path $pidFile) {
        try {
            $currentPidInFile = [int](Get-Content -Path $pidFile -Raw)
        } catch {
            $currentPidInFile = $null
        }
    }
    if ($currentPidInFile -eq $selfPid) {
        Remove-Item -Path $pidFile -Force -ErrorAction SilentlyContinue
    }
}
