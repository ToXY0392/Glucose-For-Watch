# Configure GitHub Project v2: columns (Status), fields, issues #1-12, placement.
# Requires GH_TOKEN or git credential with scopes: project, read:project, repo
param(
    [string]$Repo = "ToXY0392/Glucose-For-Watch",
    [string]$ProjectTitle = "Glucose For Watch v0.5 to v0.6",
    [int]$ProjectNumber = 0
)

$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..\..")

function Get-GhToken {
    if ($env:GH_TOKEN) { return $env:GH_TOKEN }
    $cred = @"
protocol=https
host=github.com
"@ | git credential fill
    return ($cred | Select-String '^password=').ToString().Substring(9)
}

function Invoke-GhGraphql($query, $variables) {
    param()
}

function Invoke-Gql($token, $query, $variables) {
    $body = @{ query = $query; variables = $variables } | ConvertTo-Json -Depth 10
    $r = Invoke-RestMethod -Uri "https://api.github.com/graphql" -Method Post `
        -Headers @{ Authorization = "Bearer $token" } `
        -Body $body -ContentType "application/json"
    if ($r.errors) {
        throw ($r.errors | ForEach-Object { $_.message } | Join-String -Separator "; ")
    }
    return $r.data
}

$token = Get-GhToken
$owner, $repoName = $Repo -split "/"

Write-Host "=== Resolve user and repo ==="
$userQ = "query { viewer { id login } repository(owner: `"$owner`", name: `"$repoName`") { id } }"
$userData = Invoke-Gql $token $userQ @{}
$userId = $userData.viewer.id
$userLogin = $userData.viewer.login
$repoId = $userData.repository.id
Write-Host "User: $userLogin"

Write-Host "=== Find or create project ==="
$listQ = @"
query(`$login: String!) {
  user(login: `$login) {
    projectsV2(first: 20) {
      nodes { id number title }
    }
  }
}
"@
$listData = Invoke-Gql $token $listQ @{ login = $userLogin }
$project = $listData.user.projectsV2.nodes | Where-Object { $_.title -eq $ProjectTitle } | Select-Object -First 1

if (-not $project) {
    Write-Host "Creating project..."
    $createQ = @"
mutation(`$input: CreateProjectV2Input!) {
  createProjectV2(input: `$input) {
    projectV2 { id number title url }
  }
}
"@
    $created = Invoke-Gql $token $createQ @{ input = @{ ownerId = $userId; title = $ProjectTitle; repositoryId = $repoId } }
    $project = $created.createProjectV2.projectV2
    Write-Host "Created: $($project.url)"
} else {
    Write-Host "Found project #$($project.number): $($project.title)"
}

$projectId = $project.id
if ($ProjectNumber -gt 0) { $projectNum = $ProjectNumber } else { $projectNum = $project.number }

Write-Host "=== Link repo to project ==="
$linkQ = @"
mutation(`$input: LinkProjectV2ToRepositoryInput!) {
  linkProjectV2ToRepository(input: `$input) {
    repository { nameWithOwner }
  }
}
"@
try {
    Invoke-Gql $token $linkQ @{ input = @{ projectId = $projectId; repositoryId = $repoId } } | Out-Null
    Write-Host "Repo linked."
} catch {
    Write-Host "Link repo: $($_.Exception.Message) (may already be linked)"
}

Write-Host "=== Configure Status field (board columns) ==="
$fieldsQ = @"
query(`$id: ID!) {
  node(id: `$id) {
    ... on ProjectV2 {
      field(name: "Status") {
        ... on ProjectV2SingleSelectField {
          id
          options { id name }
        }
      }
    }
  }
}
"@
$fieldsData = Invoke-Gql $token $fieldsQ @{ id = $projectId }
$statusFieldId = $fieldsData.node.field.id

$desiredStatuses = @(
    @{ name = "Backlog"; color = GRAY; description = "Not started" },
    @{ name = "Ready"; color = BLUE; description = "Gate amont OK" },
    @{ name = "In Progress"; color = YELLOW; description = "Active dev" },
    @{ name = "In Review"; color = ORANGE; description = "PR open" },
    @{ name = "QA Hardware"; color = PURPLE; description = "adb phone+watch" },
    @{ name = "Gate Ready"; color = GREEN; description = "stability-gate OK" },
    @{ name = "Done"; color = GREEN; description = "Merged" }
)

$updateFieldQ = @"
mutation(`$input: UpdateProjectV2FieldInput!) {
  updateProjectV2Field(input: `$input) {
    projectV2Field { ... on ProjectV2SingleSelectField { options { id name } } }
  }
}
"@
try {
    Invoke-Gql $token $updateFieldQ @{
        input = @{
            fieldId = $statusFieldId
            singleSelectOptions = $desiredStatuses
        }
    } | Out-Null
    Write-Host "Status columns updated."
} catch {
    Write-Host "Status update via API failed: $($_.Exception.Message)"
    Write-Host "Rename/add columns manually in Project Settings > Status field."
}

# Refresh status options
$fieldsData = Invoke-Gql $token $fieldsQ @{ id = $projectId }
$statusOptions = @{}
foreach ($opt in $fieldsData.node.field.options) {
    $statusOptions[$opt.name] = $opt.id
}

Write-Host "=== Add issues #1-12 ==="
$issueMap = @{}
1..12 | ForEach-Object {
    $num = $_
    $issueQ = "query { repository(owner: `"$owner`", name: `"$repoName`") { issue(number: $num) { id title } } }"
    $issueData = Invoke-Gql $token $issueQ @{}
    $issue = $issueData.repository.issue
    if (-not $issue) { Write-Warning "Issue #$num not found"; return }

    $addQ = @"
mutation(`$input: AddProjectV2ItemByIdInput!) {
  addProjectV2ItemById(input: `$input) {
    item { id }
  }
}
"@
    $added = Invoke-Gql $token $addQ @{ input = @{ projectId = $projectId; contentId = $issue.id } }
    $issueMap[$num] = @{ itemId = $added.addProjectV2ItemById.item.id; title = $issue.title }
    Write-Host "Added #$num"
}

Write-Host "=== Set Status per issue ==="
$placement = @{
    3  = "In Progress"
    1  = "Ready"
    2  = "Ready"
    4  = "Ready"
}
1..12 | ForEach-Object {
    $num = $_
    if (-not $issueMap.ContainsKey($num)) { return }
    $statusName = if ($placement.ContainsKey($num)) { $placement[$num] } else { "Backlog" }
    if (-not $statusOptions.ContainsKey($statusName)) {
        $statusName = switch -Regex ($statusName) {
            "In Progress" { "In Progress" }
            "Done" { "Done" }
            default { "Todo" }
        }
        if (-not $statusOptions.ContainsKey($statusName)) { return }
    }
    $setQ = @"
mutation(`$input: UpdateProjectV2ItemFieldValueInput!) {
  updateProjectV2ItemFieldValue(input: `$input) {
    projectV2Item { id }
  }
}
"@
    Invoke-Gql $token $setQ @{
        input = @{
            projectId = $projectId
            itemId = $issueMap[$num].itemId
            fieldId = $statusFieldId
            value = @{ singleSelectOptionId = $statusOptions[$statusName] }
        }
    } | Out-Null
    Write-Host "#$num -> $statusName"
}

Write-Host "=== Custom fields (optional) ==="
foreach ($fieldDef in @(
    @{ name = "Bloc"; options = @("S","X","A","M","B","C","D","F") },
    @{ name = "Gate"; options = @("G-X","G-A","G-M","G-B","G-C","G-D","G-M7","G-M8") }
)) {
    $createFieldQ = @"
mutation(`$input: CreateProjectV2FieldInput!) {
  createProjectV2Field(input: `$input) {
    projectV2Field { ... on ProjectV2SingleSelectField { id name } }
  }
}
"@
    try {
        $opts = $fieldDef.options | ForEach-Object { @{ name = $_; color = GRAY; description = $_ } }
        Invoke-Gql $token $createFieldQ @{
            input = @{
                projectId = $projectId
                dataType = "SINGLE_SELECT"
                name = $fieldDef.name
                singleSelectOptions = $opts
            }
        } | Out-Null
        Write-Host "Field created: $($fieldDef.name)"
    } catch {
        Write-Host "Field $($fieldDef.name): $($_.Exception.Message)"
    }
}

Write-Host ""
Write-Host "=== DONE ==="
Write-Host "Project: https://github.com/users/$userLogin/projects/$projectNum"
