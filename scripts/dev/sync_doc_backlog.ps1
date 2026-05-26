# Sync DOC-BACKLOG.md - scan repo signals and write sync report.
# Usage:
#   .\scripts\dev\sync_doc_backlog.ps1           # report only
#   .\scripts\dev\sync_doc_backlog.ps1 -Apply    # bump Last updated in DOC-BACKLOG

param(
    [switch]$Apply
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$backlogPath = Join-Path $repoRoot "docs/plan/DOC-BACKLOG.md"
$reportDir = Join-Path $repoRoot ".cursor/state/reports"
$reportPath = Join-Path $reportDir "doc-backlog-sync.md"
$now = Get-Date
$dateStr = $now.ToString("yyyy-MM-dd")

New-Item -ItemType Directory -Path $reportDir -Force | Out-Null

function Test-FileContains {
    param([string]$Path, [string]$Pattern)
    if (-not (Test-Path $Path)) { return $false }
    return (Select-String -Path $Path -Pattern $Pattern -Quiet)
}

function Get-QaSessionTopics {
    $sessionsDir = Join-Path $repoRoot "docs/qa/sessions"
    if (-not (Test-Path $sessionsDir)) { return @() }
    $topics = @()
    Get-ChildItem -Path $sessionsDir -Filter "*.md" | ForEach-Object {
        $content = Get-Content $_.FullName -Raw
        if ($content -match "C\.2|complication") { $topics += "C.2" }
        if ($content -match "C\.3|offline") { $topics += "C.3" }
        if ($content -match "C\.8|battery|20%") { $topics += "C.8" }
    }
    return ($topics | Select-Object -Unique)
}

$incidentPath = Join-Path $repoRoot "docs/qa/incidents/2026-05-25-app-crash.md"
$incidentClosed = Test-FileContains $incidentPath "Status:\*\* closed"
$incidentMitigated = Test-FileContains $incidentPath "mitigated"

$signOffFiles = @(Get-ChildItem -Path (Join-Path $repoRoot "docs/qa") -Filter "stability-signoff-*.md" -ErrorAction SilentlyContinue)
$hasSignOff = $signOffFiles.Count -gt 0

$soakC7 = @(Get-ChildItem -Path (Join-Path $repoRoot "docs/qa/soak-runs") -Filter "*C.7*" -ErrorAction SilentlyContinue)
$qaTopics = Get-QaSessionTopics
$missingQa = @("C.2", "C.3", "C.8") | Where-Object { $_ -notin $qaTopics }

$contributingPath = Join-Path $repoRoot "CONTRIBUTING.md"
$auto9Contributing = (Test-FileContains $contributingPath "glucose-for-watch-pr-author") -and (Test-FileContains $contributingPath "glucose-for-watch-pr-gatekeeper")

$prTemplatePath = Join-Path $repoRoot ".github/pull_request_template.md"
$auto9PrTemplate = Test-FileContains $prTemplatePath "glucose-for-watch-pr-author|pr-author"

$captureScriptPath = Join-Path $repoRoot "scripts/qa/capture-crash-log.ps1"
$captureScriptExists = Test-Path $captureScriptPath
$setupPath = Join-Path $repoRoot "docs/dev/setup.md"
$captureDocumented = Test-FileContains $setupPath "capture-crash-log"

$designRefPath = Join-Path $repoRoot "toxy-ux-kit/design-reference/index.html"
$designRefAge = if (Test-Path $designRefPath) {
    [math]::Round(($now - (Get-Item $designRefPath).LastWriteTime).TotalDays, 0)
} else { $null }

$actionPlanPath = Join-Path $repoRoot "docs/plan/ACTION-PLAN.md"
$ritualLinked = Test-FileContains $actionPlanPath "doc-backlog-sync|DOC-BACKLOG"

$suggestions = @()

if ($hasSignOff) {
    $suggestions += "| DOC-P0-1 | open | done | Sign-off file found |"
} elseif ($soakC7.Count -gt 0) {
    $suggestions += "| DOC-P0-1 | open | in-progress | C.7 soak log exists, sign-off pending |"
}

if ($incidentClosed) {
    $suggestions += "| DOC-P0-2 | open | done | Incident marked closed |"
} elseif ($incidentMitigated) {
    $suggestions += "| DOC-P0-2 | open | in-progress | Mitigated, close after C.7 |"
}

if ($missingQa.Count -eq 0) {
    $suggestions += "| DOC-P0-4 | open | done | C.2/C.3/C.8 session evidence found |"
} else {
    $missing = $missingQa -join ", "
    $suggestions += "| DOC-P0-4 | open | in-progress | Missing sessions: $missing |"
}

if ($auto9Contributing) {
    $suggestions += "| DOC-P1-1 | open | done | CONTRIBUTING references pr-author and gatekeeper |"
}
if ($auto9PrTemplate) {
    $suggestions += "| DOC-P1-2 | open | done | PR template references pr-author |"
}
if ($captureDocumented) {
    $suggestions += "| DOC-P1-4 | open | done | capture-crash-log in dev/setup.md |"
} elseif (-not $captureScriptExists) {
    $suggestions += "| DOC-P1-4 | open | in-progress | Script missing, implement and document |"
}

if ($ritualLinked) {
    $suggestions += "| DOC-P2-2 | - | done | ACTION-PLAN links doc-backlog-sync |"
}

$p0Open = 4
if ($hasSignOff) { $p0Open-- }
if ($incidentClosed) { $p0Open-- }
if ($missingQa.Count -eq 0) { $p0Open-- }

$p1Open = 8
if ($auto9Contributing) { $p1Open-- }
if ($auto9PrTemplate) { $p1Open-- }
if ($captureDocumented) { $p1Open-- }

$nextActions = @()
if (-not $hasSignOff -and $soakC7.Count -eq 0) {
    $nextActions += "Run C.7 soak then fill stability-signoff from template"
}
if ($missingQa.Count -gt 0) {
    $nextActions += "Add QA session docs for: $($missingQa -join ', ')"
}
if (-not $auto9Contributing) {
    $nextActions += "Complete AUTO-9: CONTRIBUTING and PR template (issue #21)"
}
if ($nextActions.Count -eq 0) {
    $nextActions += "Review P1 polish items and run doc-drift-checker"
}

$report = @(
    "# Doc backlog sync - $dateStr"
    ""
    "## Summary"
    "- P0 likely open: $p0Open - P1 likely open: $p1Open"
    "- Sign-off file: $(if ($hasSignOff) { 'yes' } else { 'no' })"
    "- Incident closed: $(if ($incidentClosed) { 'yes' } else { "no (mitigated=$incidentMitigated)" })"
    "- QA sessions C.2/C.3/C.8: $(if ($missingQa.Count -eq 0) { 'complete' } else { 'missing ' + ($missingQa -join ', ') })"
    "- AUTO-9 CONTRIBUTING: $(if ($auto9Contributing) { 'done' } else { 'pending' })"
    "- design-reference age (days): $(if ($null -ne $designRefAge) { $designRefAge } else { 'n/a' })"
    "- Applied: $(if ($Apply) { 'yes (date bump only)' } else { 'no - run skill or edit DOC-BACKLOG manually' })"
    ""
    "## Suggested status changes"
    "| ID | Current | Suggested | Reason |"
    "|----|---------|-----------|--------|"
)

if ($suggestions.Count -gt 0) {
    $report += $suggestions
} else {
    $report += "| - | - | - | No automatic suggestions |"
}

$report += @(
    ""
    "## PROGRESS cross-check"
    "- K2 (8h soak): sign-off=$(if ($hasSignOff) { 'yes' } else { 'no' }) - C.7 logs=$($soakC7.Count)"
    "- K1 incident #4: closed=$(if ($incidentClosed) { 'yes' } else { 'no' })"
    "- Update PROGRESS scoreboard after P0 items complete"
    ""
    "## Next actions (max 3)"
)

$i = 1
foreach ($action in ($nextActions | Select-Object -First 3)) {
    $report += "$i. $action"
    $i++
}

Set-Content -Path $reportPath -Value ($report -join "`n") -Encoding UTF8
Write-Host "Wrote $reportPath"

if ($Apply -and (Test-Path $backlogPath)) {
    $content = Get-Content -Path $backlogPath -Raw
    $updated = $content -replace '\*\*Last updated:\*\* \d{4}-\d{2}-\d{2}', "**Last updated:** $dateStr"
    if ($updated -ne $content) {
        Set-Content -Path $backlogPath -Value $updated -NoNewline -Encoding UTF8
        Write-Host "Bumped Last updated in DOC-BACKLOG.md"
    }
}

Write-Host "Done. Invoke @glucose-for-watch-doc-backlog-sync to apply table updates."
