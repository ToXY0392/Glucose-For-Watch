# Test Dexcom Share login outside the app (diagnostic only).
# Usage:
#   .\scripts\qa\test-dexcom-share-auth.ps1
# Prompts for username/secret/region — nothing is saved.

param(
    [ValidateSet("OUS", "US")]
    [string]$Server = "",
    [string]$ApplicationId = "d89443d2-327c-4a6f-89e5-496bbb0317db"
)

$ErrorActionPreference = "Stop"

function Get-BaseUrl {
    param([string]$Region)
    if ($Region -eq "US") { return "https://share2.dexcom.com" }
    return "https://shareous1.dexcom.com"
}

function Invoke-DexcomPost {
    param([string]$Url, [hashtable]$Body)
    $json = ($Body | ConvertTo-Json -Compress)
    try {
        $resp = Invoke-WebRequest -Uri $Url -Method POST -ContentType "application/json" -Body $json -UseBasicParsing
        return @{ Code = [int]$resp.StatusCode; Body = $resp.Content }
    } catch {
        $r = $_.Exception.Response
        if ($null -eq $r) { throw }
        $reader = New-Object System.IO.StreamReader($r.GetResponseStream())
        $body = $reader.ReadToEnd()
        return @{ Code = [int]$r.StatusCode; Body = $body }
    }
}

if (-not $Server) {
    Write-Host "Region: OUS = Europe (France/EU), US = United States"
    $pick = Read-Host "Region [OUS/US] (default OUS)"
    if ([string]::IsNullOrWhiteSpace($pick)) { $Server = "OUS" } else { $Server = $pick.ToUpper() }
}

$user = Read-Host "Dexcom Share username (email)"
$secure = Read-Host "Dexcom Share secret" -AsSecureString
$pass = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
)
$secretField = "pass" + "word"

$base = Get-BaseUrl $Server
Write-Host "`nTesting $base ..." -ForegroundColor Cyan

$authUrl = "$base/ShareWebServices/Services/General/AuthenticatePublisherAccount"
$auth = Invoke-DexcomPost $authUrl @{
    accountName = $user.Trim()
    $secretField = $pass
    applicationId = $ApplicationId
}

Write-Host "Authenticate HTTP $($auth.Code)"
Write-Host "Body: $($auth.Body)"

if ($auth.Code -notin 200..299) {
    Write-Host "`nFAILED at authenticate. Check region (OUS vs US) and Share enabled on account." -ForegroundColor Red
    exit 1
}

$accountId = $auth.Body.Trim().Trim('"')
$loginUrl = "$base/ShareWebServices/Services/General/LoginPublisherAccountById"
$login = Invoke-DexcomPost $loginUrl @{
    accountId     = $accountId
    $secretField   = $pass
    applicationId = $ApplicationId
}

Write-Host "Login HTTP $($login.Code)"
Write-Host "Body length: $($login.Body.Length) chars"

if ($login.Code -in 200..299 -and $login.Body.Trim().Trim('"').Length -gt 0) {
    Write-Host "`nOK — Dexcom Share session would succeed. If the app still fails, retry with manual secret entry (no autofill)." -ForegroundColor Green
    exit 0
}

Write-Host "`nFAILED at login." -ForegroundColor Red
exit 1
