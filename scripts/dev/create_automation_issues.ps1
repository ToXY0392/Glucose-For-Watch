# Create GitHub issues for automation / doc backlog (milestone v0.5 or v0.6).
param(
    [string]$Repo = "ToXY0392/Glucose-For-Watch",
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..\..")

$issues = @(
    @{
        title = "[bloc-s] AUTO-1 - CI export PNG previews on mobile UI PR"
        labels = "bloc-s,area:infra,area:mobile"
        milestone = "v0.5.0 Stable sideload"
        body = @"
Gate: transverse S · Effort: 4h

Add GitHub Actions job (path filter mobile/src, toxy-ux-kit):
- Run AppPreviewExporterTest / export-app-preview
- Upload PNG artifacts to PR
- Equivalent Storybook snapshot for XML/Robolectric phase

Refs: scripts/qa/export-app-preview.ps1, mobile/.../AppPreviewExporterTest.kt
"@
    },
    @{
        title = "[bloc-s] AUTO-2 - Static preview gallery HTML (design-reference)"
        labels = "bloc-s,area:ux-kit,docs-only"
        milestone = "v0.5.0 Stable sideload"
        body = @"
Gate: G-M · Effort: 6h

Generate browsable HTML gallery from:
- docs/qa/captures/ PNG exports
- toxy-ux-kit/design-reference/
- wear @Preview screenshots

Storybook-like index page committed or CI artifact. No npm Storybook (Android project).

Refs: ACTION-PLAN M.4, toxy-ux-kit/design-reference/
"@
    },
    @{
        title = "[bloc-f0] AUTO-3 - Evaluate Showkase for Compose v0.6"
        labels = "bloc-f,area:mobile,area:ux-kit"
        milestone = "v0.6.0 Compose phone"
        body = @"
Gate: G-F0 · Effort: 1d

When phone migrates to Compose (F0-F3):
- Add Showkase dependency
- Catalog @Preview Home, Dexcom, Legal screens
- Gradle task showkaseBrowserDebug for local gallery

Storybook equivalent for Compose. Defer until F0.

Refs: ACTION-PLAN F0, wear WearStatusScreenPreview.kt pattern
"@
    },
    @{
        title = "[bloc-s] AUTO-4 - CI markdown link checker for docs/"
        labels = "bloc-s,area:infra,docs-only"
        milestone = "v0.5.0 Stable sideload"
        body = @"
Effort: 2h

Workflow on PR touching docs/ or README:
- lychee or markdown-link-check
- Fail on broken internal links after repo rename Glucose-For-Watch

Refs: docs/index.md hub
"@
    },
    @{
        title = "[bloc-s] AUTO-5 - Weekly doc-drift GitHub Issue (scheduled)"
        labels = "bloc-s,area:infra,docs-only"
        milestone = "v0.5.0 Stable sideload"
        body = @"
Effort: 3h

GitHub Actions schedule (Monday):
- Open issue 'Doc drift check YYYY-MM-DD' with checklist
- Or run widget-g7-doc-drift-checker skill manually from ritual

Refs: .cursor/skills/widget-g7-doc-drift-checker
"@
    },
    @{
        title = "[bloc-s] AUTO-6 - Paparazzi screenshot tests (optional wear tile)"
        labels = "bloc-s,area:wear,area:ux-kit"
        milestone = "v0.6.0 Compose phone"
        body = @"
Effort: 2d · Priority: P2

Visual regression for wear Compose tile/complication.
Alternative/complement to manual C.1 AGP color screenshots.

Defer post v0.5 unless C.1 flaky.
"@
    },
    @{
        title = "[bloc-s] AUTO-7 - Project workflow: PR opened -> In Review"
        labels = "bloc-s,area:infra"
        milestone = "v0.5.0 Stable sideload"
        body = @"
Effort: 1h

GitHub Project built-in workflow:
- When PR linked to issue -> move card to In Review
- When merged -> Done

Configure in Project Settings > Workflows.
"@
    },
    @{
        title = "[bloc-d] AUTO-8 - Architecture diagram export on architecture.md change"
        labels = "bloc-d,area:infra,docs-only"
        milestone = "v0.5.0 Stable sideload"
        body = @"
Effort: 2h

CI or pre-commit: if docs/dev/architecture.md changes, run
scripts/assets/export-architecture-diagram.ps1 and fail if PNG/SVG drift.

Refs: docs/assets/widget-g7-architecture.png
"@
    }
)

foreach ($i in $issues) {
    $search = ($i.title -replace '\[|\]', '') -split ' ' | Select-Object -First 3 | ForEach-Object { $_ } | Where-Object { $_ }
    $searchQuery = ($search -join ' ')
    $existing = gh issue list --repo $Repo --search "in:title $searchQuery" --json number,title --limit 5 | ConvertFrom-Json
    $match = $existing | Where-Object { $_.title -eq $i.title } | Select-Object -First 1
    if ($match) {
        Write-Host "Exists #$($match.number): $($i.title)"
        continue
    }
    if ($DryRun) {
        Write-Host "Would create: $($i.title)"
        continue
    }
    $url = gh issue create --repo $Repo --title $i.title --label $i.labels --body $i.body --milestone $i.milestone
    Write-Host "Created: $url"
}

Write-Host "Done."
