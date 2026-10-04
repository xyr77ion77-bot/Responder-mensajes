@echo off
set GRADLE_VERSION=8.9
if "%GRADLE_USER_HOME%"=="" set GRADLE_USER_HOME=%USERPROFILE%\.gradle
set DIST=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-bin
set GRADLE=%DIST%\gradle-%GRADLE_VERSION%\bin\gradle.bat
if not exist "%GRADLE%" (
  mkdir "%DIST%" 2>nul
  powershell -NoProfile -Command "Invoke-WebRequest 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%DIST%\gradle.zip'; Expand-Archive -Force '%DIST%\gradle.zip' '%DIST%'; Remove-Item '%DIST%\gradle.zip'"
)
call "%GRADLE%" %*
