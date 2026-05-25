# Regenerate docs/assets/widget-g7-architecture.png from the SVG source.
# Requires: npx (@resvg/resvg-js-cli)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$Svg = Join-Path $Root "docs\assets\widget-g7-architecture.svg"
$Png = Join-Path $Root "docs\assets\widget-g7-architecture.png"

if (-not (Test-Path $Svg)) {
    Write-Error "Missing source: $Svg"
}

Push-Location $Root
try {
    npx --yes @resvg/resvg-js-cli --fit-width 1800 $Svg $Png
    Write-Host "OK: $Png"
}
finally {
    Pop-Location
}
