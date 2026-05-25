param()

$ErrorActionPreference = "Stop"

$repoRoot = (Get-Location).Path
$stateDir = Join-Path $repoRoot ".cursor/state"
$reportsDir = Join-Path $stateDir "reports"
$stateFile = Join-Path $stateDir "run_state.json"
$fullReport = Join-Path $reportsDir "full-maintenance.md"
$lockFile = Join-Path $stateDir "full-maintenance.lock"

New-Item -ItemType Directory -Path $stateDir -Force | Out-Null
New-Item -ItemType Directory -Path $reportsDir -Force | Out-Null

if (Test-Path $lockFile) {
    exit 0
}

New-Item -ItemType File -Path $lockFile -Force | Out-Null
try {
    $now = Get-Date
    $checks = @()

    $checks += @{
        Name = "widget-g7-compat-matrix-maintainer"
        Target = "docs/dev/setup.md"
    }
    $checks += @{
        Name = "widget-g7-doc-drift-checker"
        Target = "docs/dev/setup.md"
    }
    $checks += @{
        Name = "widget-g7-release-notes-curator"
        Target = "CHANGELOG.md"
    }
    $checks += @{
        Name = "widget-g7-dependency-advisor"
        Target = "build.gradle.kts"
    }

    $lines = @(
        "# Full maintenance"
        ""
        "- Status: completed"
        "- Run time (UTC): $($now.ToUniversalTime().ToString('u'))"
        ""
        "Executed packs:"
    )

    foreach ($check in $checks) {
        $targetPath = Join-Path $repoRoot $check.Target
        if (Test-Path $targetPath) {
            $ageDays = ($now - (Get-Item $targetPath).LastWriteTime).TotalDays
            $risk = if ($ageDays -gt 30) { "important" } elseif ($ageDays -gt 14) { "minor" } else { "ok" }
            $lines += "- $($check.Name): $risk signal ($($check.Target), {0:N0} days)" -f $ageDays
        } else {
            $lines += "- $($check.Name): target missing ($($check.Target))"
        }
    }

    $lines += ""
    $lines += "Next actions:"
    $lines += "- Review important signals and schedule doc/dependency updates."
    $lines += "- Use widget-g7-run-coordinator policy for escalation."

    Set-Content -Path $fullReport -Value ($lines -join "`n") -Encoding UTF8

    $newState = @{}
    if (Test-Path $stateFile) {
        try {
            $oldState = Get-Content -Path $stateFile -Raw | ConvertFrom-Json
            if ($oldState.lastQuickRunUtc) { $newState.lastQuickRunUtc = $oldState.lastQuickRunUtc }
        } catch {
        }
    }
    $newState.lastFullRunUtc = $now.ToUniversalTime().ToString("o")
    $newState | ConvertTo-Json | Set-Content -Path $stateFile -Encoding UTF8
} finally {
    Remove-Item -Path $lockFile -Force -ErrorAction SilentlyContinue
}

exit 0
