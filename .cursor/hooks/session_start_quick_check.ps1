param()

$ErrorActionPreference = "Stop"

# Cursor hook payload can arrive on stdin; consume if present.
try {
    [void]($input | Out-String)
} catch {
}

$repoRoot = (Get-Location).Path
$stateDir = Join-Path $repoRoot ".cursor/state"
$reportsDir = Join-Path $stateDir "reports"
$stateFile = Join-Path $stateDir "run_state.json"
$quickReport = Join-Path $reportsDir "quick-check.md"

New-Item -ItemType Directory -Path $stateDir -Force | Out-Null
New-Item -ItemType Directory -Path $reportsDir -Force | Out-Null

$now = Get-Date
$cooldownHours = 6
$shouldRun = $true
$lastQuickRun = $null

if (Test-Path $stateFile) {
    try {
        $state = Get-Content -Path $stateFile -Raw | ConvertFrom-Json
        if ($state.lastQuickRunUtc) {
            $lastQuickRun = [DateTime]::Parse($state.lastQuickRunUtc).ToUniversalTime()
            if (($now.ToUniversalTime() - $lastQuickRun).TotalHours -lt $cooldownHours) {
                $shouldRun = $false
            }
        }
    } catch {
        $shouldRun = $true
    }
}

if (-not $shouldRun) {
    $content = @(
        "# Quick check"
        ""
        "- Status: skipped (cooldown active)"
        "- Last run (UTC): $($lastQuickRun.ToString('u'))"
        "- Next run allowed after: $($lastQuickRun.AddHours($cooldownHours).ToString('u'))"
        ""
        "Skills scope:"
        "- widget-g7-vendor-watch"
        "- widget-g7-security-bulletin"
    ) -join "`n"
    Set-Content -Path $quickReport -Value $content -Encoding UTF8
    exit 0
}

$trackedFiles = @(
    "README.md",
    "docs/dev.md",
    "CHANGELOG.md"
)

$staleSignals = @()
foreach ($relPath in $trackedFiles) {
    $absPath = Join-Path $repoRoot $relPath
    if (Test-Path $absPath) {
        $ageDays = ($now - (Get-Item $absPath).LastWriteTime).TotalDays
        if ($ageDays -gt 30) {
            $staleSignals += "- $relPath not updated for {0:N0} days" -f $ageDays
        }
    }
}

$summary = if ($staleSignals.Count -gt 0) { "important updates to review" } else { "no obvious drift detected" }
$signalLines = @()
if ($staleSignals.Count -gt 0) {
    $signalLines = $staleSignals
} else {
    $signalLines = @("- none")
}
$content = @(
    "# Quick check"
    ""
    "- Status: completed"
    "- Run time (UTC): $($now.ToUniversalTime().ToString('u'))"
    "- Summary: $summary"
    ""
    "Skills scope:"
    "- widget-g7-vendor-watch (delta)"
    "- widget-g7-security-bulletin (delta)"
    ""
    "Signals:"
) + $signalLines

Set-Content -Path $quickReport -Value ($content -join "`n") -Encoding UTF8

$newState = @{
    lastQuickRunUtc = $now.ToUniversalTime().ToString("o")
}
if (Test-Path $stateFile) {
    try {
        $oldState = Get-Content -Path $stateFile -Raw | ConvertFrom-Json
        if ($oldState.lastFullRunUtc) { $newState.lastFullRunUtc = $oldState.lastFullRunUtc }
    } catch {
    }
}
$newState | ConvertTo-Json | Set-Content -Path $stateFile -Encoding UTF8

exit 0
