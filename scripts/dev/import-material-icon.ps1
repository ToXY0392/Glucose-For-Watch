# Importe une icone Material Symbol officielle (Android VectorDrawable) depuis GitHub Google.
#
# Usage:
#   .\scripts\dev\import-material-icon.ps1 -IconName bluetooth -OutName ic_bluetooth_24
#   .\scripts\dev\import-material-icon.ps1 -IconName aod_watch -ListOnly
#   .\scripts\dev\import-material-icon.ps1 -IconName refresh -Target wear
#
param(
    [Parameter(Mandatory = $true)]
    [string]$IconName,

    [string]$OutName,

    [ValidateSet("outlined", "rounded", "sharp")]
    [string]$Style = "outlined",

    [ValidateSet("20", "24", "40", "48")]
    [string]$Size = "24",

    [ValidateSet("mobile", "wear")]
    [string]$Target = "mobile",

    [switch]$ListOnly,

    [switch]$Filled
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent

$styleFolder = switch ($Style) {
    "outlined" { "materialsymbolsoutlined" }
    "rounded"  { "materialsymbolsrounded" }
    "sharp"    { "materialsymbolssharp" }
}

$apiListUrl = "https://api.github.com/repos/google/material-design-icons/contents/symbols/android/$IconName/$styleFolder?ref=master"
$ghHeaders = @{ "User-Agent" = "WidgetG7-import-script" }

Write-Host "`n=== Material Symbols (official) ===" -ForegroundColor Cyan
Write-Host "Icon : $IconName | Style : $Style | Size : ${Size}px | Target : $Target`n"

if ($ListOnly) {
    try {
        $entries = Invoke-RestMethod -Uri $apiListUrl -Headers $ghHeaders
    } catch {
        Write-Error "Icon '$IconName' introuvable. Chercher le nom sur https://fonts.google.com/icons"
    }
    Write-Host "Fichiers disponibles ($Style) :" -ForegroundColor Yellow
    $entries | ForEach-Object { Write-Host "  $($_.name)" }
    exit 0
}

$suffix = if ($Filled) { "_fill1" } else { "" }
$fileName = "${IconName}${suffix}_${Size}px.xml"
$rawUrl = "https://raw.githubusercontent.com/google/material-design-icons/master/symbols/android/$IconName/$styleFolder/$fileName"

Write-Host "Download: $rawUrl" -ForegroundColor DarkGray

try {
    $response = Invoke-WebRequest -Uri $rawUrl -UseBasicParsing
} catch {
    Write-Host "Echec direct ($fileName). Variantes disponibles :" -ForegroundColor Yellow
    try {
        $entries = Invoke-RestMethod -Uri $apiListUrl -Headers $ghHeaders
        $entries | ForEach-Object { Write-Host "  $($_.name)" }
    } catch {
        Write-Host "  (API GitHub indisponible - verifier fonts.google.com/icons)" -ForegroundColor DarkGray
    }
    Write-Error "Impossible de telecharger '$fileName' pour '$IconName'."
}

$content = $response.Content
$content = $content -replace '@android:color/white', '@color/wg7_icon_tint'
$content = $content -replace '\s*android:tint="\?attr/colorControlNormal"\s*\r?\n', "`n"
if (-not $content.StartsWith("<?xml")) {
    $content = "<?xml version=`"1.0`" encoding=`"utf-8`"?>`n" + $content
}

if (-not $OutName) {
    $OutName = "ic_${IconName}_${Size}"
}
if (-not $OutName.EndsWith(".xml")) {
    $OutName = "$OutName.xml"
}

$outDir = Join-Path $Root "$Target/src/main/res/drawable"
if (-not (Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir -Force | Out-Null
}
$outPath = Join-Path $outDir $OutName
[System.IO.File]::WriteAllText($outPath, $content.TrimEnd() + "`n")

Write-Host "[OK] Ecrit : $outPath" -ForegroundColor Green
Write-Host "Doc  : docs/design/material-icons.md`n"
