# Update existing GitHub Project board: Status columns + issue placement (no duplicate items).
param(
    [string]$Repo = "ToXY0392/Glucose-For-Watch",
    [int]$ProjectNumber = 1
)

$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..\..")

function Get-GhToken {
    if ($env:GH_TOKEN) { return $env:GH_TOKEN }
    $t = gh auth token 2>$null
    if ($t) { return $t.Trim() }
    throw "Run: gh auth login --scopes project,read:project,repo"
}

function Invoke-Gql($token, $query, $variables) {
    $body = @{ query = $query; variables = $variables } | ConvertTo-Json -Depth 12
    $r = Invoke-RestMethod -Uri "https://api.github.com/graphql" -Method Post `
        -Headers @{ Authorization = "Bearer $token" } -Body $body -ContentType "application/json"
    if ($r.errors) { throw (($r.errors | ForEach-Object { $_.message }) -join "; ") }
    return $r.data
}

$token = Get-GhToken
$owner, $repoName = $Repo -split "/"

$projQ = @"
query(`$login: String!, `$num: Int!) {
  user(login: `$login) {
    projectV2(number: `$num) {
      id
      title
      url
      field(name: "Status") {
        ... on ProjectV2SingleSelectField { id options { id name } }
      }
      items(first: 50) {
        nodes {
          id
          content { ... on Issue { number title } }
        }
      }
    }
  }
}
"@
$data = Invoke-Gql $token $projQ @{ login = $owner; num = $ProjectNumber }
$project = $data.user.projectV2
if (-not $project) { throw "Project #$ProjectNumber not found" }
Write-Host "Project: $($project.title) ($($project.url))"

$statusFieldId = $project.field.id

$desiredStatuses = @(
    @{ name = "Backlog"; color = "GRAY"; description = "Not started" },
    @{ name = "Ready"; color = "BLUE"; description = "Gate amont OK" },
    @{ name = "In Progress"; color = "YELLOW"; description = "Active dev" },
    @{ name = "In Review"; color = "ORANGE"; description = "PR open" },
    @{ name = "QA Hardware"; color = "PURPLE"; description = "adb phone+watch" },
    @{ name = "Gate Ready"; color = "GREEN"; description = "stability-gate OK" },
    @{ name = "Done"; color = "GREEN"; description = "Merged" }
)

$updateFieldQ = @"
mutation(`$input: UpdateProjectV2FieldInput!) {
  updateProjectV2Field(input: `$input) {
    projectV2Field { ... on ProjectV2SingleSelectField { options { id name } } }
  }
}
"@
Invoke-Gql $token $updateFieldQ @{
    input = @{ fieldId = $statusFieldId; singleSelectOptions = $desiredStatuses }
} | Out-Null
Write-Host "Status columns updated."

$data = Invoke-Gql $token $projQ @{ login = $owner; num = $ProjectNumber }
$statusOptions = @{}
foreach ($opt in $data.user.projectV2.field.options) { $statusOptions[$opt.name] = $opt.id }

$placement = @{
    3  = "In Progress"
    1  = "Ready"
    2  = "Ready"
    4  = "Ready"
}

$setQ = @"
mutation(`$input: UpdateProjectV2ItemFieldValueInput!) {
  updateProjectV2ItemFieldValue(input: `$input) { projectV2Item { id } }
}
"@

foreach ($node in $data.user.projectV2.items.nodes) {
    $num = $node.content.number
    if (-not $num) { continue }
    $statusName = if ($placement.ContainsKey($num)) { $placement[$num] } else { "Backlog" }
    if (-not $statusOptions.ContainsKey($statusName)) { continue }
    Invoke-Gql $token $setQ @{
        input = @{
            projectId = $data.user.projectV2.id
            itemId = $node.id
            fieldId = $statusFieldId
            value = @{ singleSelectOptionId = $statusOptions[$statusName] }
        }
    } | Out-Null
    Write-Host "#$num -> $statusName"
}

Write-Host "Done. Refresh: $($project.url)"
