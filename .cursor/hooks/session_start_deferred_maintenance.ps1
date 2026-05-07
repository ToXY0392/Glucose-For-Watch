param()

$ErrorActionPreference = "Stop"

# Cursor hook payload can arrive on stdin; consume if present.
try {
    [void]($input | Out-String)
} catch {
}

$repoRoot = (Get-Location).Path
$stateDir = Join-Path $repoRoot ".cursor/state"
$stateFile = Join-Path $stateDir "run_state.json"
$reportsDir = Join-Path $stateDir "reports"
$deferredReport = Join-Path $reportsDir "deferred-trigger.md"

New-Item -ItemType Directory -Path $stateDir -Force | Out-Null
New-Item -ItemType Directory -Path $reportsDir -Force | Out-Null

$now = Get-Date
$cooldownHours = 24
$shouldRun = $true
$lastFullRun = $null

if (Test-Path $stateFile) {
    try {
        $state = Get-Content -Path $stateFile -Raw | ConvertFrom-Json
        if ($state.lastFullRunUtc) {
            $lastFullRun = [DateTime]::Parse($state.lastFullRunUtc).ToUniversalTime()
            if (($now.ToUniversalTime() - $lastFullRun).TotalHours -lt $cooldownHours) {
                $shouldRun = $false
            }
        }
    } catch {
        $shouldRun = $true
    }
}

if (-not $shouldRun) {
    $content = @(
        "# Deferred maintenance trigger"
        ""
        "- Status: skipped (full cooldown active)"
        "- Last full run (UTC): $($lastFullRun.ToString('u'))"
        "- Next run allowed after: $($lastFullRun.AddHours($cooldownHours).ToString('u'))"
    ) -join "`n"
    Set-Content -Path $deferredReport -Value $content -Encoding UTF8
    exit 0
}

$runnerPath = Join-Path $repoRoot ".cursor/hooks/run_full_maintenance.ps1"
if (-not (Test-Path $runnerPath)) {
    Set-Content -Path $deferredReport -Value "# Deferred maintenance trigger`n`n- Status: failed (runner script missing)" -Encoding UTF8
    exit 0
}

$command = "Start-Sleep -Seconds 180; & `"$runnerPath`""
Start-Process -FilePath "powershell" -ArgumentList @("-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $command) -WindowStyle Hidden | Out-Null

$content = @(
    "# Deferred maintenance trigger"
    ""
    "- Status: scheduled"
    "- Scheduled at (UTC): $($now.ToUniversalTime().ToString('u'))"
    "- Delay: 180 seconds"
    "- Runner: .cursor/hooks/run_full_maintenance.ps1"
) -join "`n"
Set-Content -Path $deferredReport -Value $content -Encoding UTF8

exit 0
