# Sync git remote after GitHub repository rename
# Run AFTER clicking Rename on GitHub (Settings → Repository name → Glucose-For-Watch)
param(
    [string]$Repo = "ToXY0392/Glucose-For-Watch"
)

$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..\..")

$url = "https://github.com/$Repo.git"
Write-Host "Setting origin to $url"
git remote set-url origin $url

Write-Host "Fetching..."
git fetch origin

Write-Host "Pushing integrate..."
git push -u origin integrate

Write-Host "Remote OK. Verify: git remote -v"
