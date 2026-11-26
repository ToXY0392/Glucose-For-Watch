# Regenerate docs/assets/glucose-for-watch-architecture.png from the SVG source.
# Same deterministic render as export-architecture-diagram.sh (pinned resvg CLI,
# DejaVu fonts only). Windows has no DejaVu fonts by default: prefer running the
# .sh script under WSL, or set $env:GFW_DIAGRAM_FONT_DIR to a folder holding
# DejaVuSans.ttf, DejaVuSans-Bold.ttf, DejaVuSansMono.ttf, DejaVuSansMono-Bold.ttf.

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$Svg = Join-Path $Root "docs\assets\glucose-for-watch-architecture.svg"
$Png = Join-Path $Root "docs\assets\glucose-for-watch-architecture.png"
$FontDir = $env:GFW_DIAGRAM_FONT_DIR
$Fonts = @("DejaVuSans.ttf", "DejaVuSans-Bold.ttf", "DejaVuSansMono.ttf", "DejaVuSansMono-Bold.ttf")

if (-not (Test-Path $Svg)) {
    Write-Error "Missing source: $Svg"
}
if (-not $FontDir) {
    Write-Error "Set GFW_DIAGRAM_FONT_DIR to a DejaVu font folder, or run scripts/assets/export-architecture-diagram.sh under WSL."
}

$FontArgs = @()
foreach ($Font in $Fonts) {
    $Path = Join-Path $FontDir $Font
    if (-not (Test-Path $Path)) {
        Write-Error "Missing font: $Path"
    }
    $FontArgs += @("--font-file", $Path)
}

Push-Location $Root
try {
    npx --yes "@resvg/resvg-js-cli@2.6.2-beta.1" --no-system-font @FontArgs `
        --font-default-family "DejaVu Sans" --font-sans-serif-family "DejaVu Sans" `
        --font-monospace-family "DejaVu Sans Mono" --fit-width 1800 $Svg $Png
    Write-Host "OK: $Png"
}
finally {
    Pop-Location
}
