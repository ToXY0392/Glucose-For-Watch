# Repointe Gradle (Windows uniquement) vers le JDK embarque d'Android Studio
# comme dans l'ancien gradle.properties du depot — sans ecraser Linux/WSL.
#
# Ajoute ou met a jour %USERPROFILE%\.gradle\gradle.properties (GITIGNORE hors depot).
#
# Usage (PowerShell) :
#   powershell -ExecutionPolicy Bypass -File .\ensure-windows-studio-gradle-jvm.ps1

$gradleUser = Join-Path $env:USERPROFILE ".gradle"
$file = Join-Path $gradleUser "gradle.properties"

$jbrCandidates = @(
    "C:\Program Files\Android\Android Studio\jbr",
    (Join-Path $env:LOCALAPPDATA "Programs\Android Studio\jbr")
)

$jbr = $jbrCandidates | Where-Object { Test-Path (Join-Path $_ "bin\java.exe") } | Select-Object -First 1
if (-not $jbr) {
    Write-Host "JBR Android Studio introuvable. Installe Android Studio ou indique le chemin JBR manuellement dans $file :"
    Write-Host "  org.gradle.java.home=C:\\\\chemin\\\\vers\\\\jbr"
    exit 1
}

# Slash normaux pour java.properties (Gradle sur Windows les accepte)
$line = "org.gradle.java.home=" + ($jbr -replace '\\', '/')

if (-not (Test-Path $gradleUser)) {
    New-Item -ItemType Directory -Path $gradleUser -Force | Out-Null
}

$content = if (Test-Path $file) {
    Get-Content -Raw -LiteralPath $file
} else {
    ""
}

if ($content -match '(?m)^org\.gradle\.java\.home\s*=') {
    $content = $content -replace '(?m)^org\.gradle\.java\.home\s*=.*$', $line
} else {
    if ($content -and -not $content.EndsWith("`n")) { $content += "`n" }
    $content += "# Widget-G7 : JDK Gradle = JBR Android Studio (genere par ensure-windows-studio-gradle-jvm.ps1)`n$line`n"
}

Set-Content -LiteralPath $file -Value $content -Encoding utf8
Write-Host "OK : $line"
Write-Host "Fichier : $file"
exit 0
