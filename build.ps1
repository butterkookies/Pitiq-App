<#
.SYNOPSIS
    One-command APK build for Pitiq.
.PARAMETER Target
    debug (default), release, install, clean
.EXAMPLE
    .\build.ps1
    .\build.ps1 release
    .\build.ps1 install
#>
param([string]$Target = "debug")

$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$gradle = ".\gradlew.bat"

switch ($Target) {
    "debug"   { & $gradle :app:assembleDebug --no-configuration-cache }
    "release" { & $gradle :app:assembleRelease --no-configuration-cache }
    "install" { & $gradle :app:installDebug --no-configuration-cache }
    "clean"   { & $gradle clean --no-configuration-cache }
    default   { Write-Error "Unknown target '$Target'. Use: debug, release, install, clean" }
}
