# One-shot GitHub bootstrap: labels, milestones, issues, project.
param(
    [string]$Repo = "ToXY0392/Glucose-For-Watch"
)

$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..\..")

if (-not $env:GH_TOKEN) {
    $cred = @"
protocol=https
host=github.com
"@ | git credential fill
    $env:GH_TOKEN = ($cred | Select-String '^password=').ToString().Substring(9)
}

function New-Label($name, $color, $desc) {
    gh label create $name --repo $Repo --color $color --description $desc 2>$null
    if ($LASTEXITCODE -ne 0) {
        gh label edit $name --repo $Repo --color $color --description $desc 2>$null
    }
}

Write-Host "=== Labels ==="
@(
    @("bloc-s", "BFD4F2", "Stabilite transverse"),
    @("bloc-x", "D73A4A", "Crash FGS gate G-X"),
    @("bloc-a", "FBCA04", "P0 fiabilite gate G-A"),
    @("bloc-m", "0E8A16", "Mock user gate G-M"),
    @("bloc-b", "1D76DB", "Sync wear gate G-B"),
    @("bloc-c", "5319E7", "QA hardware gate G-C"),
    @("bloc-d", "006B75", "Qualite tests gate G-D"),
    @("bloc-f", "E99695", "Compose v0.6 gate G-F"),
    @("gate-blocker", "B60205", "Bloque tag v0.5 ou v0.6"),
    @("incident-p0", "8B0000", "Crash fatal ouvert"),
    @("sync-critical", "D93F0B", "Touch sync S1-S3 requis"),
    @("area:mobile", "C5DEF5", "Phone app"),
    @("area:wear", "C5DEF5", "Tile complication"),
    @("area:sync", "C5DEF5", "GlucoseSyncEngine Data Layer"),
    @("area:dexcom", "C5DEF5", "Share API auth"),
    @("area:ux-kit", "C5DEF5", "toxy-ux-kit AGP colors"),
    @("area:infra", "C5DEF5", "CI scripts repo"),
    @("hardware-qa", "D4C5F9", "Session adb phone watch"),
    @("docs-only", "EDEDED", "Pas de build requis")
) | ForEach-Object { New-Label $_[0] $_[1] $_[2] }

Write-Host "=== Milestones ==="
$milestoneV05 = "v0.5.0 Stable sideload"
$milestoneV06 = "v0.6.0 Compose phone"
gh api "repos/$Repo/milestones" -f "title=$milestoneV05" -f "description=Blocs X-D gate G-M7" 2>$null
gh api "repos/$Repo/milestones" -f "title=$milestoneV06" -f "description=Blocs F0-F5 gate G-M8" 2>$null

Write-Host "=== Issues ==="
$issues = @(
    @{ title = "[bloc-x] X.3 - Repro soak FGS ou simulation quota"; labels = "bloc-x,gate-blocker,sync-critical,hardware-qa"; body = "Gate G-X. ACTION-PLAN X.3" },
    @{ title = "[bloc-x] X.7 - Test Robolectric FGS refuse fallback Worker"; labels = "bloc-x,gate-blocker,area:mobile"; body = "Gate G-X. ACTION-PLAN X.7" },
    @{ title = "[bloc-c] C.7 - Soak nuit 8h + sign-off matin"; labels = "bloc-c,gate-blocker,hardware-qa"; body = "Gate G-C. Soak actif. Sign-off: stability-gate -CheckLogcatOnly" },
    @{ title = "[incident] FGS crash 2026-05-25 - fermer apres G-X"; labels = "bug,incident-p0,gate-blocker,bloc-x"; body = "Incident K1. docs/qa/incidents/2026-05-25-app-crash.md" },
    @{ title = "[bloc-a] A.1 - Flow permission notifications"; labels = "bloc-a,area:mobile"; body = "Gate G-A. ACTION-PLAN A.1" },
    @{ title = "[bloc-a] A.3 - Snackbar feedback sync manuelle"; labels = "bloc-a,area:mobile,area:sync"; body = "Gate G-A. ACTION-PLAN A.3" },
    @{ title = "[bloc-m] M.4 - design-reference companion PNG a jour"; labels = "bloc-m,area:ux-kit"; body = "Gate G-M. ACTION-PLAN M.4" },
    @{ title = "[bloc-b] B.4 - WatchSyncVerifier via engine + test ack"; labels = "bloc-b,area:wear,sync-critical"; body = "Gate G-B. ACTION-PLAN B.4" },
    @{ title = "[bloc-c] C.2 - Complication vs tuile t/5min x 6"; labels = "bloc-c,hardware-qa,area:wear"; body = "Gate G-C. ACTION-PLAN C.2" },
    @{ title = "[bloc-c] C.3 - Offline watch 2h rattrapage"; labels = "bloc-c,hardware-qa,sync-critical"; body = "Gate G-C. ACTION-PLAN C.3" },
    @{ title = "[bloc-c] C.8 - Montre batterie 20% sync degradee"; labels = "bloc-c,hardware-qa"; body = "Gate G-C. ACTION-PLAN C.8" },
    @{ title = "[bloc-d] D.6 - capture-crash-log.ps1 one-command"; labels = "bloc-d,area:infra"; body = "Gate G-D. ACTION-PLAN D.6" }
)

$issueUrls = @()
foreach ($i in $issues) {
    $search = $i.title.Substring(0, [Math]::Min(40, $i.title.Length))
    $existing = gh issue list --repo $Repo --search "in:title $search" --json number,title --limit 5 | ConvertFrom-Json
    $match = $existing | Where-Object { $_.title -eq $i.title } | Select-Object -First 1
    if ($match) {
        $num = $match.number
        Write-Host "Issue exists: #$num"
    } else {
        $out = gh issue create --repo $Repo --title $i.title --label $i.labels --body $i.body --milestone $milestoneV05
        $num = [int]($out -replace '.*/issues/', '')
        Write-Host "Created: #$num"
    }
    $issueUrls += "https://github.com/$Repo/issues/$num"
}

Write-Host "=== Project ==="
$projectTitle = "Glucose For Watch v0.5 to v0.6"
$projectList = gh project list --owner ToXY0392 --format json | ConvertFrom-Json
$project = $projectList.projects | Where-Object { $_.title -eq $projectTitle } | Select-Object -First 1
if (-not $project) {
    $project = gh project create --owner ToXY0392 --title $projectTitle --format json | ConvertFrom-Json
    Write-Host "Project created #$($project.number)"
} else {
    Write-Host "Project exists #$($project.number)"
}

$projectNum = $project.number
foreach ($url in $issueUrls) {
    gh project item-add $projectNum --owner ToXY0392 --url $url 2>$null
}

Write-Host "=== Done ==="
Write-Host "Project: https://github.com/users/ToXY0392/projects/$projectNum"
Write-Host "Repo: https://github.com/$Repo"
